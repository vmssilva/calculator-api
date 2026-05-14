package com.github.vmssilva.calculator.engine.std.value;

import java.math.BigDecimal;

import com.github.vmssilva.calculator.engine.exception.CalculatorRuntimeException;
import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;

public final class Values {

  public static <T> Value of(T type) {

    if (type instanceof BigDecimal value)
      return new DecimalValue(value);

    if (type instanceof Integer value)
      return new IntValue(value);

    if (type instanceof Double value)
      return new DoubleValue(value);

    if (type instanceof String value)
      return new StringValue(value);

    throw new CalculatorRuntimeException("Invalid type + '" + type.getClass().getSimpleName() + "'");
  }

  public static <R extends Number> R asNumber(Value v, Class<R> target) {

    if (Number.class.isAssignableFrom(v.unwrap().getClass())) {

      if (v instanceof DecimalValue d) {

        if (target == Integer.class)
          return target.cast(d.unwrap().intValue());

        if (target == Double.class)
          return target.cast(d.unwrap().doubleValue());

        if (target == Long.class)
          return target.cast(d.unwrap().longValue());

        if (target == BigDecimal.class)
          return target.cast(d.unwrap());
      }

      if (v instanceof IntValue i) {
        return asNumber(Values.of(new BigDecimal(i.unwrap())), target);
      }

      if (v instanceof DoubleValue d) {
        return asNumber(Values.of(new BigDecimal(d.unwrap())), target);
      }
    }

    if (v instanceof StringValue s) {
      try {
        BigDecimal d = new BigDecimal(s.unwrap());
        return asNumber(Values.of(d), target);
      } catch (ClassCastException c) {
        throw new ExecutionErrorException("error when trying to cast " + s.unwrap().toString() + " to Number");
      } catch (NumberFormatException n) {
        throw new ExecutionErrorException(
            "cannot cast " + s.type().friendly() + " to Number");
      } catch (RuntimeException r) {
        Throwable cause = r.getCause();

        if (cause instanceof RuntimeException rt)
          throw rt;

        throw new RuntimeException(cause.getCause());
      }
    }

    throw new ExecutionErrorException(
        "cannot cast " + v.type().friendly() + " to Number");

  }

  public static Integer asInt(Value v) {
    return asNumber(v, Integer.class);
  }

  public static Double asDouble(Value v) {
    return asNumber(v, Double.class);
  }

  public static BigDecimal asDecimal(Value v) {
    return asNumber(v, BigDecimal.class);
  }

  public static String asString(Value v) {
    if (v instanceof StringValue s)
      return s.unwrap();
    throw new ExecutionErrorException("Expected String but got " + v.getClass().getSimpleName());
  }

  public static String formatNumber(BigDecimal value) {
    if (value == null)
      return "null";

    int digits = value.precision();

    // limite de seguranca visual
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
