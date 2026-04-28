package com.github.vmssilva.calculator.engine.ast;

import java.util.List;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.value.Value;

public record BlockNode(List<Node> nodes, boolean scoped) implements Node {
  @Override
  public Value interpret(ApplicationContext context) {

    if (scoped)
      context.pushScope();

    try {
      Value result = null;

      for (Node node : nodes) {
        result = node.interpret(context);
      }

      return result;
    } finally {
      if (scoped)
        context.popScope();
    }
  }
}
