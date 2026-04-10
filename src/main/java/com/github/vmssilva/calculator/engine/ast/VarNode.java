package com.github.vmssilva.calculator.engine.ast;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;

public record VarNode(String name, Node node) implements Node {
  @Override
  public Object interpret(ApplicationContext context) {
    var value = node.interpret(context);
    context.set(name, value);
    return null;
  }

}
