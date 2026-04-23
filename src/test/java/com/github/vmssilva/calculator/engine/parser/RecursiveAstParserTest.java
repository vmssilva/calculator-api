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
import com.github.vmssilva.calculator.engine.lexer.Lexer;
import com.github.vmssilva.calculator.engine.lexer.SimpleLexer;
import com.github.vmssilva.calculator.engine.value.Values;

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
    assertEquals(expect(1.0), eval("10%3"));
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

}
