package com.github.vmssilva.calculator.engine.std.value;

import com.github.vmssilva.calculator.engine.std.type.ValueType;

public record BooleanValue(Boolean bool) implements Value<Boolean> {

  @Override
  public ValueType type() {
    return ValueType.BOOLEAN;
  }

  @Override
  public Boolean unwrap() {
    return bool;
  }

  @Override
  public final String toString() {
    return String.valueOf(bool);
  }
}
