package com.github.vmssilva.calculator.engine.ast;

import java.util.List;
import java.util.Objects;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.value.Value;

public record ProgramNode(List<Node> nodes) implements Node {
  @Override
  public Value interpret(ApplicationContext context) {

    return nodes.stream().filter(n -> Objects.nonNull(n)).map(node -> node.interpret(context))
        .reduce((first, second) -> second).orElse(null);
  }
}
