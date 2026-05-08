package com.github.vmssilva.calculator.engine.std.value;

import com.github.vmssilva.calculator.engine.std.type.ValueType;

public interface Value<T> {
  ValueType type();

  T unwrap();
}
