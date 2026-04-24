package com.github.vmssilva.calculator.engine.value;

import java.math.BigDecimal;

public record NumberValue(BigDecimal value) implements Value {

  @Override
  public final String toString() {
    return Values.formatNumber(value);
  }
}
