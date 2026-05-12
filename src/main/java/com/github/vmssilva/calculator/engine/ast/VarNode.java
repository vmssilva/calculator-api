package com.github.vmssilva.calculator.engine.ast;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.std.value.Value;

public record VarNode(String name, Node node) implements Node {
  @Override
  public Value interpret(ApplicationContext context) {
    var value = node.interpret(context);

    context.define(name, value);

    return value;
  }

  @Override
  public final String toString() {
    return name;
  }

}
