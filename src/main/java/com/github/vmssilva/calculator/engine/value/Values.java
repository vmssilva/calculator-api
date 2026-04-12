package com.github.vmssilva.calculator.engine.value;

import java.math.BigDecimal;
import java.util.function.Function;

public final class Values {

  public static BigDecimal asNumber(Value v) {
    if (v instanceof NumberValue n)
      return n.value();
    throw new RuntimeException("Expected number but got " + v.getClass());
  }

}
