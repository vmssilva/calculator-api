package com.github.vmssilva.calculator.engine.std.value;

import java.math.BigDecimal;

import com.github.vmssilva.calculator.engine.std.type.ValueType;

public record LongValue(Long value) implements NumberValue<Long> {

  @Override
  public final String toString() {
    return value.toString();
  }

  @Override
  public ValueType type() {
    return ValueType.LONG;
  }

  @Override
  public Long unwrap() {
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
