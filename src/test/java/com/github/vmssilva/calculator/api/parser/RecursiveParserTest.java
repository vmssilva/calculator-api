package com.github.vmssilva.calculator.api.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.vmssilva.calculator.api.ast.Expression;
import com.github.vmssilva.calculator.api.lexer.Lexer;
import com.github.vmssilva.calculator.api.lexer.SimpleLexer;

class RecursiveParserTest {

  private Parser parser;

  @BeforeEach
  void setup() {
    Lexer lexer = new SimpleLexer();
    parser = new RecursiveParser(lexer);
  }

  private Double eval(String expr) {
    Expression ast = parser.parse(expr);
    return ast.interpret();
  }

  @Test
  @DisplayName("Should add simple numbers")
  void testAddition() {
    assertEquals(6.0, eval("1+2+3"));
  }

  @Test
  @DisplayName("Should subtract numbers")
  void testSubtraction() {
    assertEquals(5.0, eval("10-3-2"));
  }

  @Test
  @DisplayName("Should multiply numbers")
  void testMultiplication() {
    assertEquals(24.0, eval("4*3*2"));
  }

  @Test
  @DisplayName("Should divide numbers")
  void testDivision() {
    assertEquals(5.0, eval("20/4"));
  }

  @Test
  @DisplayName("Should calculate modulo")
  void testModulo() {
    assertEquals(1.0, eval("10%3"));
  }

  @Test
  @DisplayName("Should respect operator precedence")
  void testOperatorPrecedence() {
    assertEquals(7.0, eval("1+2*3"));
  }

  @Test
  @DisplayName("Parentheses should change precedence")
  void testParentheses() {
    assertEquals(9.0, eval("(1+2)*3"));
  }

  @Test
  @DisplayName("Should interpret negative numbers")
  void testSignedNegativeNumber() {
    assertEquals(-2.0, eval("-5+3"));
  }

  @Test
  @DisplayName("Should interpret positive signed numbers")
  void testSignedPositiveNumber() {
    assertEquals(8.0, eval("+5+3"));
  }

  @Test
  @DisplayName("Complex expression")
  void testComplexExpression() {
    assertEquals(14.0, eval("2*(3+4)"));
  }

  @Test
  @DisplayName("Expression with multiple operators")
  void testMultipleOperators() {
    assertEquals(11.0, eval("3+4*2"));
  }

  @Test
  @DisplayName("Expression with nested parentheses")
  void testNestedParentheses() {
    assertEquals(21.0, eval("(1+2)*(3+4)"));
  }

  @Test
  @DisplayName("Number followed by parentheses should imply multiplication")
  void testImplicitMultiplicationSimple() {
    assertEquals(25.0, eval("5(2+3)"));
  }

  @Test
  @DisplayName("Number followed by simple parentheses")
  void testImplicitMultiplicationSingleNumber() {
    assertEquals(20.0, eval("5(4)"));
  }

  @Test
  @DisplayName("Expression before parentheses")
  void testImplicitMultiplicationExpression() {
    assertEquals(21.0, eval("3(2+5)"));
  }

  @Test
  @DisplayName("Implicit multiplication with inner expression")
  void testImplicitMultiplicationComplex() {
    assertEquals(30.0, eval("5(2+4)"));
  }

  @Test
  @DisplayName("Implicit multiplication after another operator")
  void testImplicitMultiplicationAfterOperator() {
    assertEquals(17.0, eval("2+3(5)"));
  }

  @Test
  @DisplayName("Implicit multiplication with nested parentheses")
  void testImplicitMultiplicationNestedParentheses() {
    assertEquals(50.0, eval("5(2*(3+2))"));
  }

  @Test
  @DisplayName("Implicit multiplication with negative number")
  void testImplicitMultiplicationWithNegative() {
    assertEquals(-10.0, eval("5(-2)"));
  }

  @Test
  @DisplayName("Implicit multiplication with decimal number")
  void testImplicitMultiplicationDecimal() {
    assertEquals(12.5, eval("5(2.5)"));
  }

  @Test
  @DisplayName("Simple parentheses around a number")
  void testSingleNumberParentheses() {
    assertEquals(3.0, eval("(3)"));
  }

  @Test
  @DisplayName("Parentheses around an expression")
  void testExpressionParentheses() {
    assertEquals(5.0, eval("(2+3)"));
  }

  @Test
  @DisplayName("Parentheses around a multiplication")
  void testMultiplicationInsideParentheses() {
    assertEquals(6.0, eval("(2*3)"));
  }

  @Test
  @DisplayName("Nested parentheses with a single number")
  void testNesteParenthesesSingleNumber() {
    assertEquals(3.0, eval("((3))"));
  }

  @Test
  @DisplayName("Nested parentheses with expression")
  void testNestedExpressionParentheses() {
    assertEquals(9.0, eval("((1+2)*3)"));
  }

  @Test
  @DisplayName("Parentheses with negative number")
  void testNegativeInsideParentheses() {
    assertEquals(-5.0, eval("(-5)"));
  }

  @Test
  @DisplayName("Parentheses with decimal number")
  void testDecimalInsideParentheses() {
    assertEquals(2.5, eval("(2.5)"));
  }

  @Test
  @DisplayName("Operator at the end of the expression")
  void testTrailingOperator() {
    assertThrows(UnsupportedOperationException.class, () -> eval("1+"));
  }

  @Test
  @DisplayName("Invalid operator at the beginning")
  void testLeadingOperator() {
    assertThrows(UnsupportedOperationException.class, () -> eval("*2+3"));
  }

  @Test
  @DisplayName("Unclosed parenthesis")
  void testUnclosedParenthesis() {
    assertThrows(UnsupportedOperationException.class, () -> eval("(1+2"));
  }

  @Test
  @DisplayName("Unexpected closing parenthesis")
  void testUnexpectedClosingParenthesis() {
    assertThrows(UnsupportedOperationException.class, () -> eval("1+2)"));
  }

  @Test
  @DisplayName("Empty parentheses")
  void testEmptyParentheses() {
    assertThrows(UnsupportedOperationException.class, () -> eval("()"));
  }

  @Test
  @DisplayName("Two consecutive operators")
  void testDoubleOperator() {
    assertThrows(UnsupportedOperationException.class, () -> eval("2++3"));
  }

  @Test
  @DisplayName("Empty expression")
  void testEmptyExpression() {
    assertThrows(UnsupportedOperationException.class, () -> eval(""));
  }

  @Test
  @DisplayName("Invalid operator sequence")
  void testInvalidOperatorSequence() {
    assertThrows(UnsupportedOperationException.class, () -> eval("5*/2"));
  }

  @Test
  @DisplayName("Incomplete parentheses expression")
  void testIncompleteParenthesesExpression() {
    assertThrows(UnsupportedOperationException.class, () -> eval("(2+)"));
  }

}
