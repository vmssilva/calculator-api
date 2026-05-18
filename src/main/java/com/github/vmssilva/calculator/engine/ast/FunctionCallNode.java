package com.github.vmssilva.calculator.engine.ast;

import java.util.List;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.context.Dispatcher;
import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public record FunctionCallNode(Node target, List<Node> args) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {

    Dispatcher dispatcher = new Dispatcher();
    Value[] evaluated = new Value[args.size()];

    for (int i = 0; i < args.size(); i++) {
      evaluated[i] = args.get(i).interpret(context);
    }

    if (target instanceof PropertyAccessorNode accessor &&
        accessor.property() instanceof IdentifierNode property) {

      Value value = accessor.target().interpret(context);
      return dispatcher.dispatch(context, value, property.name(), evaluated);
    }

    // Call
    Value value = target.interpret(context);

    if (value instanceof FunctionValue fn) {
      return fn.call(context, evaluated);
    }

    throw new ExecutionErrorException(
        "Value is not callable");
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
