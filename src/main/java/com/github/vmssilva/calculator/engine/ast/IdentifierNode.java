package com.github.vmssilva.calculator.engine.ast;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;

public record IdentifierNode(String name) implements Node {

  @Override
  public Object interpret(ApplicationContext context) {
    return context.get(name);
  }
}
