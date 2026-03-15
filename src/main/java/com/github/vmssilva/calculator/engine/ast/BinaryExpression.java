package com.github.vmssilva.calculator.engine.ast;

public record BinaryExpression(Expression left, Expression right, String operator) implements Expression {

  @Override
  public Double interpret() {

    return switch (operator) {
      case "+" -> left.interpret() + right.interpret();
      case "-" -> left.interpret() - right.interpret();
      case "*" -> left.interpret() * right.interpret();
      case "/" -> left.interpret() / right.interpret();
      case "%" -> left.interpret() % right.interpret();
      default -> throw new UnsupportedOperationException("Invalid operation: " + operator);
    };
  }
}
