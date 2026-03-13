package com.github.vmssilva.calculator.api.ast;

public record NumberExpression(Double value) implements Expression {

  @Override
  public Double interpret() {
    return value;
  }
}
