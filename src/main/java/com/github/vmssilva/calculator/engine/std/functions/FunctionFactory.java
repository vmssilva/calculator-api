package com.github.vmssilva.calculator.engine.std.functions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.std.type.ValueType;
import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public final class FunctionFactory {

  private FunctionFactory() {
  }

  public static FunctionValue of(String name, Callable impl, ValueType[] parameters) {
    return new FunctionValue() {

      @Override
      public Value call(ApplicationContext context, List<Value> args) {
        return impl.call(context, args);
      }

      @Override
      public String name() {
        return name;
      }

      @Override
      public String toString() {
        return name() + "(" +
            Arrays.stream(parameters())
                .map(p -> p.value())
                .collect(Collectors.joining(", "))
            + ")";
      }

      @Override
      public ValueType[] parameters() {
        return parameters;
      }

    };
  }
}
