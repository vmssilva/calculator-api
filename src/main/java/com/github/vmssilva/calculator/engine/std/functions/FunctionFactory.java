package com.github.vmssilva.calculator.engine.std.functions;

import java.util.List;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.std.ValueType;
import com.github.vmssilva.calculator.engine.value.BaseFunctionValue;
import com.github.vmssilva.calculator.engine.value.FunctionValue;
import com.github.vmssilva.calculator.engine.value.Value;

public final class FunctionFactory {

  private FunctionFactory() {
  }

  public static FunctionValue of(String name, Function<List<Value>, Value> impl, ValueType[] parameters) {
    return of(name, impl, parameters, false);
  }

  public static FunctionValue of(String name, Function<List<Value>, Value> impl, ValueType[] parameters,
      boolean curried) {
    return new BaseFunctionValue(parameters, curried) {

      @Override
      public Value apply(ApplicationContext context, List<Value> args) {
        return impl.apply(context, args);
      }

      @Override
      public String name() {
        return name;
      }
    };
  }
}
