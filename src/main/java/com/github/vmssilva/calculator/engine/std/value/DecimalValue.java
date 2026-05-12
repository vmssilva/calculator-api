package com.github.vmssilva.calculator.engine.std.value;

import java.math.BigDecimal;

import com.github.vmssilva.calculator.engine.std.type.ValueType;

public record DecimalValue(BigDecimal value) implements Value<BigDecimal> {

  @Override
  public final String toString() {
    return Values.formatNumber(value);
  }

  @Override
  public ValueType type() {
    return ValueType.NUMBER;
  }

  @Override
  public BigDecimal unwrap() {
    return value;
  }

}
