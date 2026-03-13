package com.github.vmssilva.calculator.api.ast;

public record UnaryExpression(String operator, Expression right) implements Expression {

  @Override
  public Double interpret() {
    return switch (operator) {
      case "+" -> right.interpret();
      case "-" -> -right.interpret();
      default -> throw new UnsupportedOperationException("Invalid unary operator: " + operator);
    };
  }
}
