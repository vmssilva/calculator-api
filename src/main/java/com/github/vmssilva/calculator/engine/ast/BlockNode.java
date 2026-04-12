package com.github.vmssilva.calculator.engine.ast;

import java.util.List;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.value.Value;

public record BlockNode(List<Node> nodes) implements Node {
  @Override
  public Value interpret(ApplicationContext context) {
    context.pushScope();
    var result = nodes.stream().map(node -> node.interpret(context)).reduce((first, second) -> second);
    context.popScope();

    return result.orElse(null);
  }
}
