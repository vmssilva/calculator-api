package com.github.vmssilva.calculator.engine.std.functions;

import java.util.List;

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
      public Value apply(ApplicationContext context, List<Value> args) {
        return impl.apply(context, args);
      }

      @Override
      public String name() {
        return name;
      }

      @Override
      public ValueType[] parameters() {
        return parameters;
      }
    };
  }
}
