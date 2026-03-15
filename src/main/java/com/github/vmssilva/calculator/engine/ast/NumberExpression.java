package com.github.vmssilva.calculator.engine.ast;

public record NumberExpression(Double value) implements Expression {

  @Override
  public Double interpret() {
    return value;
  }
}
