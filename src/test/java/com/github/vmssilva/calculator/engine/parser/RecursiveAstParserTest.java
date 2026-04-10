package com.github.vmssilva.calculator.engine.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.vmssilva.calculator.engine.ast.Node;
import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.lexer.Lexer;
import com.github.vmssilva.calculator.engine.lexer.SimpleLexer;

class RecursiveAstParserTest {

  private Parser parser;
  private ApplicationContext ctx;

  @BeforeEach
  void setup() {
    Lexer lexer = new SimpleLexer();
    parser = new RecursiveAstParser(lexer);
    ctx = new ApplicationContext();
  }

  @SuppressWarnings("unchecked")
  private BigDecimal eval(String expr) {
    Node ast = parser.parse(expr);
    var value = (BigDecimal) ((List<Object>) ast.interpret(ctx)).get(0);
    return value.setScale(2, RoundingMode.HALF_UP);
  }

  private BigDecimal expected(Double value) {
    return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
  }

  @Test
  @DisplayName("Should add simple numbers")
  void testAddition() {
    assertEquals(expected(6.0), eval("1+2+3"));
  }

  @Test
  @DisplayName("Should subtract numbers")
  void testSubtraction() {
    assertEquals(expected(5.0), eval("10-3-2"));
  }

  @Test
  @DisplayName("Should multiply numbers")
  void testMultiplication() {
    assertEquals(expected(24.0), eval("4*3*2"));
  }

  @Test
  @DisplayName("Should divide numbers")
  void testDivision() {
    assertEquals(expected(5.0), eval("20/4"));
  }

  @Test
  @DisplayName("Should calculate modulo")
  void testModulo() {
    assertEquals(expected(1.0), eval("10%3"));
  }

  @Test
  @DisplayName("Should respect operator precedence")
  void testOperatorPrecedence() {
    assertEquals(expected(7.0), eval("1+2*3"));
  }

  @Test
  @DisplayName("Parentheses should change precedence")
  void testParentheses() {
    assertEquals(expected(9.0), eval("(1+2)*3"));
  }

  @Test
  @DisplayName("Should interpret negative numbers")
  void testSignedNegativeNumber() {
    assertEquals(expected(-2.0), eval("-5+3"));
  }

  @Test
  @DisplayName("Should interpret positive signed numbers")
  void testSignedPositiveNumber() {
    assertEquals(expected(8.0), eval("+5+3"));
  }

  @Test
  @DisplayName("Node with multiple operators")
  void testMultipleOperators() {
    assertEquals(expected(11.0), eval("3+4*2"));
  }

  @Test
  @DisplayName("Node with nested parentheses")
  void testNestedParentheses() {
    assertEquals(expected(21.0), eval("(1+2)*(3+4)"));
  }

  @Test
  @DisplayName("Multiplication by simple parentheses with single number")
  void testImplicitMultiplicationSingleNumber() {
    assertEquals(expected(20.0), eval("5*(4)"));
    assertEquals(expected(20.0), eval("8*(2.5)"));
  }

  @Test
  @DisplayName("Multiplication with inner Node")
  void testImplicitMultiplicationComplex() {
    assertEquals(expected(30.0), eval("5*(2+4)"));
  }

  @Test
  @DisplayName("Multiplication after another operator")
  void testImplicitMultiplicationAfterOperator() {
    assertEquals(expected(17.0), eval("2+3*(5)"));
  }

  @Test
  @DisplayName("Multiplication with nested parentheses")
  void testImplicitMultiplicationNestedParentheses() {
    assertEquals(expected(50.0), eval("5*(2*(3+2))"));
  }

  @Test
  @DisplayName("Multiplication with negative number")
  void testImplicitMultiplicationWithNegative() {
    assertEquals(expected(-10.0), eval("5*(-2)"));
  }

  @Test
  @DisplayName("Multiplication with decimal number")
  void testImplicitMultiplicationDecimal() {
    assertEquals(expected(12.5), eval("5*(2.5)"));
  }

  @Test
  @DisplayName("Single number")
  void testSingleNumber() {
    assertEquals(expected(3.0), eval("3"));
  }

  @Test
  @DisplayName("Single decimal-point number")
  void testSingleDecimalPointNumber() {
    assertEquals(expected(0.5), eval("0.5"));
    assertEquals(expected(3.1), eval("3.1"));
    assertEquals(expected(3.50), eval("3.50"));
  }

  @Test
  @DisplayName("Simple parentheses around a number")
  void testSingleNumberParentheses() {
    assertEquals(expected(3.0), eval("(3)"));
    assertEquals(expected(35.0), eval("(35)"));
  }

  @Test
  @DisplayName("Simple parentheses around a decimal-point number")
  void testSingleDecimalPointNumberParentheses() {
    assertEquals(expected(3.5), eval("(3.5)"));
    assertEquals(expected(0.2), eval("(0.2)"));
    assertEquals(expected(0.25), eval("(0.25)"));
  }

  @Test
  @DisplayName("Parentheses around an Node")
  void testNodeParentheses() {
    assertEquals(expected(5.0), eval("(2+3)"));
    assertEquals(expected(5.0), eval("(10-5)"));
  }

  @Test
  @DisplayName("Parentheses around a multiplication")
  void testMultiplicationInsideParentheses() {
    assertEquals(expected(6.0), eval("(2*3)"));
  }

  @Test
  @DisplayName("Nested parentheses with a single number")
  void testNesteParenthesesSingleNumber() {
    assertEquals(expected(3.0), eval("((3))"));
  }

  @Test
  @DisplayName("Nested parentheses with Node")
  void testNestedNodeParentheses() {
    assertEquals(expected(9.0), eval("((1+2)*3)"));
  }

  @Test
  @DisplayName("Parentheses with negative number")
  void testNegativeInsideParentheses() {
    assertEquals(expected(-5.0), eval("(-5)"));
  }

  @Test
  @DisplayName("Parentheses with decimal number")
  void testDecimalInsideParentheses() {
    assertEquals(expected(2.5), eval("(2.5)"));
  }

  @Test
  @DisplayName("Operator at the end of the Node")
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
  @DisplayName("Empty Node")
  void testEmptyNode() {
    assertThrows(UnsupportedOperationException.class, () -> eval(""));
  }

  @Test
  @DisplayName("Invalid operator sequence")
  void testInvalidOperatorSequence() {
    assertThrows(UnsupportedOperationException.class, () -> eval("5*/2"));
  }

  @Test
  @DisplayName("Incomplete parentheses Node")
  void testIncompleteParenthesesNode() {
    assertThrows(UnsupportedOperationException.class, () -> eval("(2+)"));
  }

}
