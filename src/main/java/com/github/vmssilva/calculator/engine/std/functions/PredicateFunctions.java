package com.github.vmssilva.calculator.engine.std.functions;

import java.math.BigDecimal;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.std.value.NumberValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public final class PredicateFunctions {

  private PredicateFunctions() {
  }

  @Builtin(name = "isPositive", description = "Check if number is positive")
  public static Value isPositive(ApplicationContext context, NumberValue n) {

    return new NumberValue(
        n.unwrap().compareTo(BigDecimal.ZERO) > 0
            ? BigDecimal.ONE
            : BigDecimal.ZERO);
  }

  @Builtin(name = "isNegative", description = "Check if number is negative")
  public static Value isNegative(ApplicationContext context, NumberValue n) {

    return new NumberValue(
        n.unwrap().compareTo(BigDecimal.ZERO) < 0
            ? BigDecimal.ONE
            : BigDecimal.ZERO);
  }

  @Builtin(name = "isZero", description = "Check if number is zero")
  public static Value isZero(ApplicationContext context, NumberValue n) {

    return new NumberValue(
        n.unwrap().compareTo(BigDecimal.ZERO) == 0
            ? BigDecimal.ONE
            : BigDecimal.ZERO);
  }
}
