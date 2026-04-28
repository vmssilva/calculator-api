package com.github.vmssilva.calculator.engine.ast;

import java.util.List;
import java.util.stream.Collectors;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.std.functions.FunctionMeta;
import com.github.vmssilva.calculator.engine.std.functions.Parameter;
import com.github.vmssilva.calculator.engine.std.ValueType;
import com.github.vmssilva.calculator.engine.utils.Validators;
import com.github.vmssilva.calculator.engine.value.FunctionValue;
import com.github.vmssilva.calculator.engine.value.Value;

public record LambdaNode(List<String> params, Node body) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {

    FunctionMeta meta = new FunctionMeta(
        "<lambda>",
        params.stream()
            .map(p -> new Parameter(p, ValueType.ANY))
            .toList(),
        "user lambda",
        false);

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

      @Override
      public String toString() {
        return "("
            + meta.params().stream().map(m -> m.name() + ": " + m.type().value()).collect(Collectors.joining(", "))
            + ") -> " + body;
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
        return "lambda";
      }
    };
  }
}
