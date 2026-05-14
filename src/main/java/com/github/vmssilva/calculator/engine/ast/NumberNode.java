package com.github.vmssilva.calculator.engine.ast;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.std.value.NumberValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public record NumberNode(NumberValue value) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {
    return value;
  }

  @Override
  public final String toString() {
    return value.toString();
  }
}
