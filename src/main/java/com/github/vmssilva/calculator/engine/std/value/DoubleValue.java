package com.github.vmssilva.calculator.engine.std.value;

import com.github.vmssilva.calculator.engine.std.type.ValueType;

public record DoubleValue(Double value) implements Value<Double> {

  @Override
  public final String toString() {
    return value.toString();
  }

  @Override
  public ValueType type() {
    return ValueType.DOUBLE;
  }

  @Override
  public Double unwrap() {
    return value;
  }

}
