package com.github.vmssilva.calculator.engine.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.vmssilva.calculator.engine.ast.Node;
import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.exception.CalculatorParserException;
import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.lexer.Lexer;
import com.github.vmssilva.calculator.engine.lexer.SimpleLexer;
import com.github.vmssilva.calculator.engine.std.value.Values;

class RecursiveAstParserTest {

  private Parser parser;
  private ApplicationContext ctx;

  @BeforeEach
  void setup() {
    Lexer lexer = new SimpleLexer();
    parser = new RecursiveAstParser(lexer);
    ctx = new ApplicationContext();
  }

  private BigDecimal eval(String expr) {
    Node ast = parser.parse(expr);
    var value = Values.asNumber(ast.interpret(ctx));
    return value.setScale(2, RoundingMode.HALF_UP);
  }

  private BigDecimal expect(Double value) {
    return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
  }

  @Test
  void shouldSupportSimpleVariableAssignment() {
    BigDecimal result = eval("x = 10; x");

    assertEquals(expect(10.0), result);
  }

  @Test
  void shouldSupportVariableWithExpression() {
    BigDecimal result = eval("x = 10 + 5; x");

    assertEquals(expect(15.0), result);
  }

  @Test
  void shouldSupportChainedVariables() {
    BigDecimal result = eval("x = 10; y = x * 2; y");

    assertEquals(expect(20.0), result);
  }

  @Test
  void shouldReuseVariableInExpression() {
    BigDecimal result = eval("x = 10; x + 5");

    assertEquals(expect(15.0), result);
  }

  @Test
  void shouldUseFunctionWithVariable() {
    BigDecimal result = eval("a = 10; multiply(a, 2)");

    assertEquals(expect(20.0), result);
  }

  @Test
  void shouldComposeFunctions() {
    BigDecimal result = eval("a = 10; add(1, multiply(a, 10))");

    assertEquals(expect(101.0), result);
  }

  @Test
  void shouldSupportNestedFunctions() {
    BigDecimal result = eval("multiply(add(2, 3), 4)");

    assertEquals(expect(20.0), result);
  }

  @Test
  void shouldSupportComplexFunctionChains() {
    BigDecimal result = eval("a = 2; b = 3; add(multiply(a, b), multiply(b, a))");

    assertEquals(expect(12.0), result);
  }

  @Test
  void shouldEvaluateMultipleExpressions() {
    BigDecimal result = eval("x = 10; y = 20; x + y");

    assertEquals(expect(30.0), result);
  }

  @Test
  void shouldReturnLastExpressionValue() {
    BigDecimal result = eval("x = 10; y = 20; add(x, y)");

    assertEquals(expect(30.0), result);
  }

  @Test
  void shouldPassFunctionResultAsArgument() {
    BigDecimal result = eval("a = 10; add(multiply(a, 2), multiply(a, 3))");

    assertEquals(expect(50.0), result);
  }

  @Test
  void shouldHandleDeepComposition() {
    BigDecimal result = eval("a = 2; b = 3; c = 4; add(multiply(a, b), multiply(b, c))");

    assertEquals(expect(18.0), result);
  }

  @Test
  void shouldOverrideVariable() {
    BigDecimal result = eval("x = 10; x = 20; x");

    assertEquals(expect(20.0), result);
  }

  @Test
  void shouldPersistVariableAcrossExpressions() {
    eval("x = 10");
    BigDecimal result = eval("x + 5");

    assertEquals(expect(15.0), result);
  }

  @Test
  void shouldRunMiniScript() {
    BigDecimal result = eval(
        "a = 5;" +
            "b = multiply(a, 2);" +
            "c = add(b, 10);" +
            "c");

    assertEquals(expect(20.0), result);
  }

  @Test
  void shouldStillWorkWithoutVariables() {
    BigDecimal result = eval("2 + 3 * 4");

    assertEquals(expect(14.0), result);
  }

  @Test
  @DisplayName("Should add simple numbers")
  void testAddition() {
    assertEquals(expect(6.0), eval("1+2+3"));
  }

  @Test
  @DisplayName("Should subtract numbers")
  void testSubtraction() {
    assertEquals(expect(5.0), eval("10-3-2"));
  }

  @Test
  @DisplayName("Should multiply numbers")
  void testMultiplication() {
    assertEquals(expect(24.0), eval("4*3*2"));
  }

  @Test
  @DisplayName("Should divide numbers")
  void testDivision() {
    assertEquals(expect(5.0), eval("20/4"));
  }

  @Test
  @DisplayName("Should calculate modulo")
  void testModulo() {
    assertEquals(expect(1.0), eval("remainder(10,3)"));
  }

  @Test
  @DisplayName("Should calculate percentage")
  void testPercentage() {
    assertEquals(expect(1560.0), eval("1200 * (1 + 0.30)"));
    assertEquals(expect(1560.0), eval("1200 + (30 % 1200)"));
  }

  @Test
  @DisplayName("Should respect operator precedence")
  void testOperatorPrecedence() {
    assertEquals(expect(7.0), eval("1+2*3"));
  }

  @Test
  @DisplayName("Parentheses should change precedence")
  void testParentheses() {
    assertEquals(expect(9.0), eval("(1+2)*3"));
  }

  @Test
  @DisplayName("Should interpret negative numbers")
  void testSignedNegativeNumber() {
    assertEquals(expect(-2.0), eval("-5+3"));
  }

  @Test
  @DisplayName("Should interpret positive signed numbers")
  void testSignedPositiveNumber() {
    assertEquals(expect(8.0), eval("+5+3"));
  }

  @Test
  @DisplayName("Node with multiple operators")
  void testMultipleOperators() {
    assertEquals(expect(11.0), eval("3+4*2"));
  }

  @Test
  @DisplayName("Node with nested parentheses")
  void testNestedParentheses() {
    assertEquals(expect(21.0), eval("(1+2)*(3+4)"));
  }

  @Test
  @DisplayName("Multiplication by simple parentheses with single number")
  void testImplicitMultiplicationSingleNumber() {
    assertEquals(expect(20.0), eval("5*(4)"));
    assertEquals(expect(20.0), eval("8*(2.5)"));
  }

  @Test
  @DisplayName("Multiplication with inner Node")
  void testImplicitMultiplicationComplex() {
    assertEquals(expect(30.0), eval("5*(2+4)"));
  }

  @Test
  @DisplayName("Multiplication after another operator")
  void testImplicitMultiplicationAfterOperator() {
    assertEquals(expect(17.0), eval("2+3*(5)"));
  }

  @Test
  @DisplayName("Multiplication with nested parentheses")
  void testImplicitMultiplicationNestedParentheses() {
    assertEquals(expect(50.0), eval("5*(2*(3+2))"));
  }

  @Test
  @DisplayName("Multiplication with negative number")
  void testImplicitMultiplicationWithNegative() {
    assertEquals(expect(-10.0), eval("5*(-2)"));
  }

  @Test
  @DisplayName("Multiplication with decimal number")
  void testImplicitMultiplicationDecimal() {
    assertEquals(expect(12.5), eval("5*(2.5)"));
  }

  @Test
  @DisplayName("Single number")
  void testSingleNumber() {
    assertEquals(expect(3.0), eval("3"));
  }

  @Test
  @DisplayName("Single decimal-point number")
  void testSingleDecimalPointNumber() {
    assertEquals(expect(0.5), eval("0.5"));
    assertEquals(expect(3.1), eval("3.1"));
    assertEquals(expect(3.50), eval("3.50"));
  }

  @Test
  @DisplayName("Simple parentheses around a number")
  void testSingleNumberParentheses() {
    assertEquals(expect(3.0), eval("(3)"));
    assertEquals(expect(35.0), eval("(35)"));
  }

  @Test
  @DisplayName("Simple parentheses around a decimal-point number")
  void testSingleDecimalPointNumberParentheses() {
    assertEquals(expect(3.5), eval("(3.5)"));
    assertEquals(expect(0.2), eval("(0.2)"));
    assertEquals(expect(0.25), eval("(0.25)"));
  }

  @Test
  @DisplayName("Parentheses around an Node")
  void testNodeParentheses() {
    assertEquals(expect(5.0), eval("(2+3)"));
    assertEquals(expect(5.0), eval("(10-5)"));
  }

  @Test
  @DisplayName("Parentheses around a multiplication")
  void testMultiplicationInsideParentheses() {
    assertEquals(expect(6.0), eval("(2*3)"));
  }

  @Test
  @DisplayName("Nested parentheses with a single number")
  void testNesteParenthesesSingleNumber() {
    assertEquals(expect(3.0), eval("((3))"));
  }

  @Test
  @DisplayName("Nested parentheses with Node")
  void testNestedNodeParentheses() {
    assertEquals(expect(9.0), eval("((1+2)*3)"));
  }

  @Test
  @DisplayName("Parentheses with negative number")
  void testNegativeInsideParentheses() {
    assertEquals(expect(-5.0), eval("(-5)"));
  }

  @Test
  @DisplayName("Parentheses with decimal number")
  void testDecimalInsideParentheses() {
    assertEquals(expect(2.5), eval("(2.5)"));
  }

  @Test
  @DisplayName("Operator at the end of the Node")
  void testTrailingOperator() {
    assertThrows(CalculatorParserException.class, () -> eval("1+"));
  }

  @Test
  @DisplayName("Invalid operator at the beginning")
  void testLeadingOperator() {
    assertThrows(CalculatorParserException.class, () -> eval("*2+3"));
  }

  @Test
  @DisplayName("Unclosed parenthesis")
  void testUnclosedParenthesis() {
    assertThrows(CalculatorParserException.class, () -> eval("(1+2"));
  }

  @Test
  @DisplayName("Unexpected closing parenthesis")
  void testUnexpectedClosingParenthesis() {
    assertThrows(CalculatorParserException.class, () -> eval("1+2)"));
  }

  @Test
  @DisplayName("Empty parentheses")
  void testEmptyParentheses() {
    assertThrows(CalculatorParserException.class, () -> eval("()"));
  }

  @Test
  @DisplayName("Two consecutive operators")
  void testDoubleOperator() {
    assertThrows(CalculatorParserException.class, () -> eval("2++3"));
  }

  @Test
  @DisplayName("Empty Node")
  void testEmptyNode() {
    assertThrows(CalculatorParserException.class, () -> eval(""));
  }

  @Test
  @DisplayName("Invalid operator sequence")
  void testInvalidOperatorSequence() {
    assertThrows(CalculatorParserException.class, () -> eval("5*/2"));
  }

  @Test
  @DisplayName("Incomplete parentheses Node")
  void testIncompleteParenthesesNode() {
    assertThrows(CalculatorParserException.class, () -> eval("(2+)"));
  }

  @Test
  @DisplayName("Should evaluate predicate functions")
  void testPredicates() {
    assertEquals(expect(1.0), eval("isPositive(10)"));
    assertEquals(expect(0.0), eval("isPositive(-1)"));

    assertEquals(expect(1.0), eval("isNegative(-5)"));
    assertEquals(expect(0.0), eval("isNegative(5)"));

    assertEquals(expect(1.0), eval("isZero(0)"));
    assertEquals(expect(0.0), eval("isZero(10)"));
  }

  @Test
  @DisplayName("Should evaluate basic operations")
  void testBasicOperations() {
    assertEquals(expect(5.0), eval("add(2, 3)"));
    assertEquals(expect(1.0), eval("subtract(3, 2)"));
    assertEquals(expect(6.0), eval("multiply(2, 3)"));
    assertEquals(expect(2.0), eval("divide(6, 3)"));

    assertEquals(expect(300.0), eval("percentage(30, 1000)"));
  }

  @Test
  @DisplayName("Should evaluate aggregation functions")
  void testAggregations() {
    assertEquals(expect(6.0), eval("sum(1, 2, 3)"));
    assertEquals(expect(1.0), eval("min(1, 2, 3)"));
    assertEquals(expect(3.0), eval("max(1, 2, 3)"));
  }

  @Test
  @DisplayName("Should evaluate unary math functions")
  void testUnaryMath() {
    assertEquals(expect(5.0), eval("abs(-5)"));
    assertEquals(expect(5.0), eval("truncate(5.9)"));
    assertEquals(expect(-5.0), eval("negate(5)"));
  }

  @Test
  @DisplayName("Should evaluate factorial and remainder")
  void testFactorialAndRemainder() {
    assertEquals(expect(120.0), eval("factorial(5)"));
    assertEquals(expect(1.0), eval("remainder(10, 3)"));
  }

  @Test
  @DisplayName("Should evaluate rounding functions")
  void testRounding() {
    assertEquals(expect(5.0), eval("round(4.6)"));
    assertEquals(expect(4.0), eval("floor(4.9)"));
    assertEquals(expect(5.0), eval("ceil(4.1)"));
  }

  @Test
  @DisplayName("Should evaluate advanced math functions")
  void testAdvancedMath() {
    assertEquals(expect(4.0), eval("sqrt(16)"));
    assertEquals(expect(2.0), eval("log10(100)"));
    assertEquals(expect(3.0), eval("log(8, 2)")); // dependendo da implementação

    assertEquals(expect(8.0), eval("pow(2, 3)"));
    assertEquals(expect(Math.exp(1)), eval("exp(1)"));
    assertEquals(expect(Math.log(2)), eval("ln(2)"));

    assertEquals(expect(5.0), eval("hypot(3, 4)"));
  }

  @Test
  @DisplayName("Should clamp values")
  void testClamp() {
    assertEquals(expect(5.0), eval("clamp(5, 0, 10)"));
    assertEquals(expect(0.0), eval("clamp(-1, 0, 10)"));
    assertEquals(expect(10.0), eval("clamp(20, 0, 10)"));
  }

  @Test
  @DisplayName("Should evaluate trigonometric functions")
  void testTrig() {
    assertEquals(expect(0.0), eval("sin(0)"));
    assertEquals(expect(1.0), eval("cos(0)"));
    assertEquals(expect(0.0), eval("tan(0)"));
    // assertEquals(expect(1.0), eval("cos(0)"), 1e-9);
  }

  @Test
  @DisplayName("Should evaluate inverse trigonometric functions")
  void testInverseTrig() {
    assertEquals(expect(Math.PI / 2), eval("asin(1)"));
    assertEquals(expect(0.0), eval("acos(1)"));
    assertEquals(expect(0.0), eval("atan(0)"));
  }

  @Test
  @DisplayName("Should convert degrees and radians")
  void testDegRad() {
    assertEquals(expect(180.0), eval("deg(PI)"));
    assertEquals(expect(Math.PI), eval("rad(180)"));
  }

  @Test
  @DisplayName("Should evaluate sign function")
  void testSign() {
    assertEquals(expect(1.0), eval("sign(10)"));
    assertEquals(expect(-1.0), eval("sign(-5)"));
    assertEquals(expect(0.0), eval("sign(0)"));
  }

  @Test
  @DisplayName("Should evaluate simple lambda assignment and call")
  void testSimpleLambda() {
    assertEquals(expect(10.0), eval("f = (x) -> x; f(10)"));
  }

  @Test
  @DisplayName("Should evaluate lambda with expression body")
  void testLambdaExpressionBody() {
    assertEquals(expect(20.0), eval("f = (x) -> x * 2; f(10)"));
  }

  @Test
  @DisplayName("Should evaluate lambda with multiple arguments")
  void testLambdaMultipleArgs() {
    assertEquals(expect(30.0), eval("f = (a, b) -> a + b; f(10, 20)"));
  }

  @Test
  @DisplayName("Should pass function as argument to lambda")
  void testLambdaAsCallback() {
    assertEquals(
        expect(30.0),
        eval("fn = (cb, a) -> cb(a, 10); fn(add, 20)"));
  }

  @Test
  @DisplayName("Should call builtin function inside lambda")
  void testLambdaWithBuiltin() {
    assertEquals(
        expect(8.0),
        eval("f = (x) -> multiply(x, 2); f(4)"));
  }

  @Test
  @DisplayName("Should evaluate immediate lambda execution")
  void testImmediateLambda() {
    assertEquals(
        expect(10.0),
        eval("((x) -> x + 5)(5)"));
  }

  @Test
  @DisplayName("Should evaluate immediate lambda with expression body")
  void testImmediateLambdaExpression() {
    assertEquals(
        expect(14.0),
        eval("((a, b) -> a * b + 4)(2, 5)"));
  }

  @Test
  @DisplayName("Should evaluate lambda inside expression")
  void testLambdaInsideExpression() {
    assertEquals(
        expect(11.0),
        eval("2 + ((x) -> x * 3)(3)"));
  }

  @Test
  @DisplayName("Should fail invalid lambda syntax")
  void testInvalidLambdaSyntax() {
    assertThrows(CalculatorParserException.class, () -> eval("f = (x -> x + 1"));
  }

  @Test
  @DisplayName("Should fail invalid lambda call")
  void testInvalidLambdaCall() {
    assertThrows(ExecutionErrorException.class, () -> eval("f = (x) -> x; f()"));
  }

}
