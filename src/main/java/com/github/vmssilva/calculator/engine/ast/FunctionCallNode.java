package com.github.vmssilva.calculator.engine.ast;

import java.util.List;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.value.FunctionValue;
import com.github.vmssilva.calculator.engine.value.NumberValue;
import com.github.vmssilva.calculator.engine.value.Value;
import com.github.vmssilva.calculator.engine.value.Values;

public record FunctionCallNode(String name, List<Node> args) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {
    var fn = (FunctionValue) context.get(name);
    var evaluated = args.stream().map((e) -> e.interpret(context)).toList();
    return new NumberValue(Values.asNumber(fn.apply(evaluated)));
  }
}
