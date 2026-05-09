package com.github.vmssilva.calculator.engine.ast;

import java.util.List;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public record FunctionCallNode(Node target, List<Node> args) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {

    Value value = target.interpret(context);

    if (!(value instanceof FunctionValue fn)) {
      throw new ExecutionErrorException("Target is not a function");
    }

    var evaluated = args.stream()
        .map(arg -> arg.interpret(context))
        .toList();

    return fn.call(context, evaluated);
  }

  @Override
  public final String toString() {
    StringBuilder repr = new StringBuilder();

    repr.append(target).append("(");
    for (int i = 0; i < args.size(); i++) {
      if (args.isEmpty())
        break;

      repr.append(args.get(i));

      if (i < args.size() - 1)
        repr.append(", ");
    }

    repr.append(")");

    return repr.toString();
  }
}
