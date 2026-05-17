package com.github.vmssilva.calculator.engine.std.functions;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.core.lang.math.MathOperations;
import com.github.vmssilva.calculator.engine.std.value.DecimalValue;
import com.github.vmssilva.calculator.engine.std.value.NumberValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public final class PredicateBuiltins {

  private PredicateBuiltins() {
  }

  @Builtin(name = "isPositive", description = "Check if number is positive")
  public static Value isPositive(ApplicationContext context, NumberValue<?> n) {
    return (MathOperations.isNegative(n)) ? n.zero() : n.one();
  }

  @Builtin(name = "isNegative", description = "Check if number is negative")
  public static Value isNegative(ApplicationContext context, DecimalValue n) {
    return (MathOperations.isNegative(n)) ? n.one() : n.zero();
  }

  @Builtin(name = "isZero", description = "Check if number is zero")
  public static Value isZero(ApplicationContext context, DecimalValue n) {
    return (n.asDouble() == 0D) ? n.one() : n.zero();
  }
}
