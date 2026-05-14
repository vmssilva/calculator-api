package com.github.vmssilva.calculator.engine.ast;

import java.util.List;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.ListValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public record FunctionCallNode(Node target, List<Node> args) implements Node {
  @Override
  public Value interpret(ApplicationContext context) {

    Value[] evaluated = new Value[args.size()];

    for (int i = 0; i < args.size(); i++) {
      evaluated[i] = args.get(i).interpret(context);
    }

    if (target instanceof IdentifierNode id) {
      return context.resolve(id.name(), evaluated);
    }

    // fallback: expressão qualquer
    Value value = target.interpret(context);

    if (value instanceof FunctionValue fn) {
      return fn.call(context, evaluated);
    }

    // overload set
    if (value instanceof ListValue list) {

      List<FunctionValue> overloads = list.values().stream()
          .filter(FunctionValue.class::isInstance)
          .map(FunctionValue.class::cast)
          .toList();

      if (!overloads.isEmpty()) {
        return context.resolve(overloads, evaluated);
      }
    }

    throw new ExecutionErrorException(
        "Target is not callable");

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
