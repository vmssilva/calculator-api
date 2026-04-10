package com.github.vmssilva.calculator.engine.ast;

import java.util.List;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;

public record FunctionCallNode(String name, List<Node> args) implements Node {

  @Override
  public Object interpret(ApplicationContext context) {
    var fn = (Function) context.get(name);
    var evaluated = args.stream().map((e) -> e.interpret(context)).toList();
    return fn.apply(evaluated);
  }
}
