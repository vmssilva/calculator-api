package com.github.vmssilva.calculator.engine.ast;

import java.math.BigDecimal;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.std.value.NumberValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public record NumberNode(BigDecimal value) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {
    return new NumberValue(value);
  }

  @Override
  public final String toString() {
    return value.toString();
  }
}
