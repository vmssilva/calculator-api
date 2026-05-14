package com.github.vmssilva.calculator.engine.std.value;

import java.math.BigDecimal;

import com.github.vmssilva.calculator.engine.std.type.ValueType;

public record DoubleValue(Double value) implements NumberValue<Double> {

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

  @Override
  public int asInt() {
    return value.intValue();
  }

  @Override
  public long asLong() {
    return value.longValue();
  }

  @Override
  public double asDouble() {
    return value.doubleValue();
  }

  @Override
  public BigDecimal asDecimal() {
    return new BigDecimal(value);
  }

}
