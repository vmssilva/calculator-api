package com.github.vmssilva.calculator.engine.ast;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.std.value.StringValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public record StringNode(String value) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {
    return new StringValue(value);
  }

}
