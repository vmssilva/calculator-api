package com.github.vmssilva.calculator.engine.std.value;

import com.github.vmssilva.calculator.engine.std.type.ValueType;

public record IntValue(Integer value) implements Value<Integer> {

  @Override
  public final String toString() {
    return value.toString();
  }

  @Override
  public ValueType type() {
    return ValueType.INT;
  }

  @Override
  public Integer unwrap() {
    return value;
  }

}
