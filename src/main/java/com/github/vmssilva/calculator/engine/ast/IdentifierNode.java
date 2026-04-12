package com.github.vmssilva.calculator.engine.ast;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.value.Value;

public record IdentifierNode(String name) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {
    return context.get(name);
  }
}
