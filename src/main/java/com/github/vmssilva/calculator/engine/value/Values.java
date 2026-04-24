package com.github.vmssilva.calculator.engine.value;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class Values {

  public static BigDecimal asNumber(Value v) {
    if (v instanceof NumberValue n)
      return new BigDecimal(formatNumber(n.value()));
    throw new RuntimeException("Expected number but got " + v.getClass());
  }

  public static String formatNumber(BigDecimal value) {
    if (value == null)
      return "null";

    int digits = value.precision();

    // limite de segurança visual
    int MAX_DIGITS = 30;

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
