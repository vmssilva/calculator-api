package com.github.vmssilva.calculator.engine.value;

import java.math.BigDecimal;

import com.github.vmssilva.calculator.engine.exception.CalculatorRuntimeException;

public final class Values {

  public static <T> NumberValue of(T num) {

    if (num instanceof BigDecimal n)
      return new NumberValue(n);

    if (num instanceof Short n)
      return new NumberValue(new BigDecimal(n));

    if (num instanceof Integer n)
      return new NumberValue(new BigDecimal(n));

    if (num instanceof Float n)
      return new NumberValue(new BigDecimal(n));

    if (num instanceof Double n)
      return new NumberValue(new BigDecimal(n));

    if (num instanceof Long n)
      return new NumberValue(new BigDecimal(n));

    throw new CalculatorRuntimeException("Invalid number format");
  }

  public static BigDecimal asNumber(Value v) {
    if (v instanceof NumberValue n)
      return new BigDecimal(formatNumber(n.value()));
    throw new RuntimeException("Expected number but got " + v.getClass());
  }

  public static String asString(Value v) {
    if (v instanceof StringValue s)
      return s.value();
    throw new RuntimeException("Expected String but got " + v.getClass());
  }

  public static String formatNumber(BigDecimal value) {
    if (value == null)
      return "null";

    int digits = value.precision();

    // limite de segurança visual
    int MAX_DIGITS = 100;

    if (digits > MAX_DIGITS) {
      return formatScientificTruncated(value);
    }

    return value.stripTrailingZeros().toPlainString();
  }

  private static String formatScientificTruncated(BigDecimal value) {
    int digits = value.precision();
    int exponent = digits - value.scale() - 1;

    BigDecimal mantissa = value
        .movePointLeft(exponent)
        .stripTrailingZeros();

    String m = mantissa.toPlainString();

    // limita mantissa também
    if (m.length() > 8) {
      m = m.substring(0, 8);
    }

    return m + "e+" + exponent;
  }

}
