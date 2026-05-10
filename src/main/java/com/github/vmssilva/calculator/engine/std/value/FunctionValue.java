package com.github.vmssilva.calculator.engine.std.value;

import com.github.vmssilva.calculator.engine.std.functions.Callable;
import com.github.vmssilva.calculator.engine.std.type.ValueType;

public interface FunctionValue extends Value<FunctionValue>, Callable {

  // Class<?>[] parameters();

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
