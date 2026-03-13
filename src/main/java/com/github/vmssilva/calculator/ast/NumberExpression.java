package com.github.vmssilva.calculator.ast;

public record NumberExpression(Double value) implements Expression {

  @Override
  public Double interpret() {
    return value;
  }
}
