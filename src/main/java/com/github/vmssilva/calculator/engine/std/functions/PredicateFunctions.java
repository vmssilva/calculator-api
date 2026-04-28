package com.github.vmssilva.calculator.engine.std.functions;

import java.math.BigDecimal;

import com.github.vmssilva.calculator.engine.std.ValueType;
import com.github.vmssilva.calculator.engine.value.FunctionValue;
import com.github.vmssilva.calculator.engine.value.NumberValue;
import com.github.vmssilva.calculator.engine.value.Values;

public final class PredicateFunctions {

  private PredicateFunctions() {
  }

  public static FunctionValue isPositive() {
    return FunctionFactory.of("isPositive", (context, args) -> {
      var v = Values.asNumber(args.get(0));

      return new NumberValue(
          v.compareTo(BigDecimal.ZERO) > 0
              ? BigDecimal.ONE
              : BigDecimal.ZERO);
    }, new ValueType[] { ValueType.NUMBER });
  }

  public static FunctionValue isNegative() {
    return FunctionFactory.of("isNegative", (context, args) -> {
      var v = Values.asNumber(args.get(0));

      return new NumberValue(
          v.compareTo(BigDecimal.ZERO) < 0
              ? BigDecimal.ONE
              : BigDecimal.ZERO);
    }, new ValueType[] { ValueType.NUMBER });
  }

  public static FunctionValue isZero() {
    return FunctionFactory.of("isZero", (context, args) -> {
      var x = Values.asNumber(args.get(0));
      return new NumberValue(
          x.compareTo(BigDecimal.ZERO) == 0
              ? BigDecimal.ONE
              : BigDecimal.ZERO);
    }, new ValueType[] { ValueType.NUMBER });
  }
}
