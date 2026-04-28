package com.github.vmssilva.calculator.engine.value;

import java.math.BigDecimal;

import com.github.vmssilva.calculator.engine.std.ValueType;

public record NumberValue(BigDecimal value) implements Value {

  @Override
  public final String toString() {
    return Values.formatNumber(value);
  }

  @Override
  public ValueType type() {
    return ValueType.NUMBER;
  }

}
