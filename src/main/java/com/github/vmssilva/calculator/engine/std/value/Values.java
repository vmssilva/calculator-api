package com.github.vmssilva.calculator.engine.std.value;

import java.math.BigDecimal;

import com.github.vmssilva.calculator.engine.exception.CalculatorRuntimeException;

public final class Values {

  public static <T> Value of(T type) {

    if (type instanceof BigDecimal value)
      return new NumberValue(value);

    if (type instanceof Short value)
      return new NumberValue(new BigDecimal(value));

    if (type instanceof Integer value)
      return new NumberValue(new BigDecimal(value));

    if (type instanceof Float value)
      return new NumberValue(new BigDecimal(value));

    if (type instanceof Double value)
      return new NumberValue(new BigDecimal(value));

    if (type instanceof Long value)
      return new NumberValue(new BigDecimal(value));

    if (type instanceof String value)
      return new StringValue(value);

    throw new CalculatorRuntimeException("Invalid format");
  }

  public static BigDecimal asNumber(Value v) {
    if (v instanceof NumberValue n)
      return new BigDecimal(formatNumber(n.unwrap()));
    throw new RuntimeException("Expected number but got " + v.getClass());
  }

  public static String asString(Value v) {
    if (v instanceof StringValue s)
      return s.unwrap();
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
