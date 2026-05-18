package com.github.vmssilva.calculator.engine.std.value;

import com.github.vmssilva.calculator.engine.std.type.ValueType;

public record BooleanValue(Boolean bool) implements Value<Integer> {

  @Override
  public ValueType type() {
    return ValueType.BOOLEAN;
  }

  @Override
  public Integer unwrap() {
    return bool ? 1 : 0;
  }
}
