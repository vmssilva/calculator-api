package com.github.vmssilva.calculator.engine.std.value;

import java.math.BigDecimal;

import com.github.vmssilva.calculator.engine.std.type.ValueType;

public record DecimalValue(BigDecimal value) implements NumberValue<BigDecimal> {

  @Override
  public final String toString() {
    return Values.formatNumber(value);
  }

  @Override
  public ValueType type() {
    return ValueType.DECIMAL;
  }

  @Override
  public BigDecimal unwrap() {
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
    return value;
  }

}
