package com.github.vmssilva.calculator.engine.core.lang;

import com.github.vmssilva.calculator.engine.std.type.ValueType;
import com.github.vmssilva.calculator.engine.std.value.Value;

public record BindingValue(String name, Value value) implements Value {

  @Override
  public ValueType type() {
    return value.type();
  }

  @Override
  public Object unwrap() {
    return value.unwrap();
  }
}
