package com.github.vmssilva.calculator.engine.std.value;

import java.util.List;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.std.type.ValueType;

public interface FunctionValue extends Value<FunctionValue> {
  Value apply(ApplicationContext context, List<Value> args);

  ValueType[] parameters();

  default String name() {
    return "<function>";
  }

  @Override
  default ValueType type() {
    return ValueType.FUNCTION;
  }

  @Override
  default FunctionValue unwrap() {
    return this;
  }

}
