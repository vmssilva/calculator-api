package com.github.vmssilva.calculator.engine.ast;

import java.util.List;
import java.util.stream.Collectors;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.exception.ValueErrorException;
import com.github.vmssilva.calculator.engine.std.ValueType;
import com.github.vmssilva.calculator.engine.utils.Validators;
import com.github.vmssilva.calculator.engine.value.FunctionValue;
import com.github.vmssilva.calculator.engine.value.Value;

public record LambdaNode(List<String> params, Node body) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {

    if (body instanceof LambdaNode) {
      throw new ValueErrorException("Invalid syntax: lambda is not a valid body expression");
    }

    return new FunctionValue() {

      @Override
      public Value apply(ApplicationContext context, List<Value> args) {

        Validators.validate(this, args);

        context.pushScope();

        try {
          // bind params
          for (int i = 0; i < params.size(); i++) {
            context.set(params.get(i), args.get(i));
          }

          return body.interpret(context);

        } finally {
          context.popScope();
        }
      }

      public String format(Node node) {
        String type = "Any";

        if (node instanceof LambdaNode lambda) {
          String params = lambda.params().stream()
              .map(p -> p + ": " + type)
              .collect(Collectors.joining(", "));

          return "(" + params + ") -> " + format(lambda.body());
        }

        return node.toString();
      }

      @Override
      public String toString() {

        return "("
            + params.stream().map(m -> m + ": Any").collect(Collectors.joining(", "))
            + ") -> "
            + format(body);
      }

      @Override
      public ValueType type() {
        return ValueType.FUNCTION;
      }

      @Override
      public ValueType[] parameters() {

        ValueType[] types = new ValueType[params.size()];

        for (int i = 0; i < params.size(); i++) {
          types[i] = ValueType.ANY;
        }

        return types;
      }

      @Override
      public String name() {
        return "<lambda>";
      }
    };
  }
}
