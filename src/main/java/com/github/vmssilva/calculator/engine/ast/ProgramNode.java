package com.github.vmssilva.calculator.engine.ast;

import java.util.List;
import java.util.Objects;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;

public record ProgramNode(List<Node> nodes) implements Node {
  @Override
  public Object interpret(ApplicationContext context) {
    var result = nodes.stream().filter(Objects::nonNull)
        .map(node -> node.interpret(context)).toList();

    return result;
  }
}
