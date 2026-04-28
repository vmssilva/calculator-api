package com.github.vmssilva.calculator.engine.ast;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.value.Value;
import com.github.vmssilva.calculator.engine.value.Values;

public record VarNode(String name, Node node) implements Node {
  @Override
  public Value interpret(ApplicationContext context) {
    var value = node.interpret(context);
    context.set(name, value);
    return Values.of(0);
  }

  @Override
  public final String toString() {
    return name;
  }

}
