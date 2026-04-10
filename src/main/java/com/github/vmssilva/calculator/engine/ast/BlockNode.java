package com.github.vmssilva.calculator.engine.ast;

import java.util.List;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;

public record BlockNode(List<Node> nodes) implements Node {
  @Override
  public Object interpret(ApplicationContext context) {
    context.pushScope();
    var result = nodes.stream().map(node -> node.interpret(context)).toList();
    context.popScope();

    return result;
  }
}
