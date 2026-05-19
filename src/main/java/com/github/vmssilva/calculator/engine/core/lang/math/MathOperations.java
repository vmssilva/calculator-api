package com.github.vmssilva.calculator.engine.core.lang.math;

import java.math.BigDecimal;

import com.github.vmssilva.calculator.engine.core.annotations.Expose;
import com.github.vmssilva.calculator.engine.core.annotations.Module;
import com.github.vmssilva.calculator.engine.exception.ValueErrorException;
import com.github.vmssilva.calculator.engine.std.type.ValueType;
import com.github.vmssilva.calculator.engine.std.value.DecimalValue;
import com.github.vmssilva.calculator.engine.std.value.DoubleValue;
import com.github.vmssilva.calculator.engine.std.value.IntValue;
import com.github.vmssilva.calculator.engine.std.value.LongValue;
import com.github.vmssilva.calculator.engine.std.value.NumberValue;

@Module(name = "Math")
public class MathOperations {

  private MathOperations() {
  }

  @Expose(name = "pow")
  public static NumberValue<?> pow(NumberValue<?> x, NumberValue<?> y) {
    ValueType type = ValueType.promote(x, y);

    return switch (type) {
      case DECIMAL -> new DecimalValue(BigDecimal.valueOf(Math.pow(x.asDouble(), y.asDouble())));
      default -> new DoubleValue(Math.pow(x.asDouble(), y.asDouble()));
    };
  }

  @Expose(name = "abs")
  public static NumberValue<?> abs(NumberValue<?> n) {
    return n.isNegative() ? n.negate() : n;
  }

  @Expose(name = "isNegative")
  public static boolean isNegative(NumberValue<?> n) {
    return n.asDecimal().compareTo(BigDecimal.ZERO) < 0;
  }

  @Expose(name = "sqrt")
  public static NumberValue<?> sqrt(NumberValue<?> n) {

    if (n.asDouble() < 0)
      throw new ValueErrorException("sqrt of negative number");

    return new DoubleValue(Math.sqrt(n.asDouble()));
  }

  @Expose(name = "sin")
  public static NumberValue<?> sin(NumberValue<?> n) {
    return new DoubleValue(Math.sin(n.asDouble()));
  }

  @Expose(name = "cos")
  public static NumberValue<?> cos(NumberValue<?> n) {
    return new DoubleValue(Math.cos(n.asDouble()));
  }

  @Expose(name = "tan")
  public static NumberValue<?> tan(NumberValue<?> n) {
    return new DoubleValue(Math.tan(n.asDouble()));
  }

  @Expose(name = "log")
  public static NumberValue<?> log(NumberValue<?> n) {

    if (n.asDecimal().compareTo(BigDecimal.ZERO) <= 0) {
      throw new ValueErrorException(
          "log(x): x must be > 0");
    }

    return new DoubleValue(Math.log(n.asDouble()));
  }

  @Expose(name = "log")
  public static NumberValue<?> log(NumberValue<?> n, NumberValue<?> base) {
    var value = n.asDecimal();
    var b = base.asDecimal();

    if (value.compareTo(BigDecimal.ZERO) <= 0) {
      throw new ValueErrorException(
          "log(x, base): x must be > 0");
    }

    if (b.compareTo(BigDecimal.ZERO) <= 0
        || b.compareTo(BigDecimal.ONE) == 0) {
      throw new ValueErrorException(
          "log(x, base): base must be > 0 and != 1");
    }

    if (b.doubleValue() <= 0 || b.doubleValue() == 1) {
      throw new ValueErrorException("Invalid log base");
    }

    return new DoubleValue(
        Math.log(value.doubleValue()) / Math.log(b.doubleValue()));
  }

  @Expose(name = "ln")
  public static NumberValue<?> ln(NumberValue<?> n) {

    if (n.asDecimal().compareTo(BigDecimal.ZERO) <= 0) {
      throw new ValueErrorException(
          "ln(x): x must be > 0");
    }

    return new DoubleValue(Math.log(n.asDouble()));
  }

  @Expose(name = "log10")
  public static NumberValue<?> log10(NumberValue<?> n) {

    if (n.asDecimal().compareTo(BigDecimal.ZERO) <= 0) {
      throw new ValueErrorException(
          "log10(x): x must be > 0");
    }
    return new DoubleValue(Math.log10(n.asDouble()));
  }

  @Expose(name = "exp")
  public static NumberValue<?> exp(NumberValue<?> n) {
    return new DoubleValue(Math.exp(n.asDouble()));
  }

  @Expose(name = "asin")
  public static NumberValue<?> asin(NumberValue<?> n) {
    return new DoubleValue(Math.asin(n.asDouble()));
  }

  @Expose(name = "acos")
  public static NumberValue<?> acos(NumberValue<?> n) {
    return new DoubleValue(Math.acos(n.asDouble()));
  }

  @Expose(name = "atan")
  public static NumberValue<?> atan(NumberValue<?> n) {
    return new DoubleValue(Math.atan(n.asDouble()));
  }

  @Expose(name = "hypot")
  public static NumberValue<?> hypot(NumberValue<?> x, NumberValue<?> y) {
    ValueType type = ValueType.promote(x, y);

    return switch (type) {
      case DECIMAL -> new DecimalValue(new BigDecimal(Math.hypot(x.asDouble(), y.asDouble())));
      default -> new DoubleValue(Math.hypot(x.asDouble(), y.asDouble()));
    };
  }

  @Expose(name = "clamp")
  public static NumberValue clamp(NumberValue<?> value, NumberValue<?> min, NumberValue<?> max) {

    var v = value.asDouble();
    var minimum = min.asDouble();
    var maximum = max.asDouble();

    if (v < minimum) {
      return new DoubleValue(minimum);
    }

    if (v > maximum) {
      return new DoubleValue(maximum);
    }

    return new DoubleValue(v);
  }

  @Expose(name = "sign")
  public static NumberValue<?> sign(NumberValue<?> n) {
    int sign = n.asDecimal().compareTo(BigDecimal.ZERO);
    return new IntValue(sign);
  }

  @Expose(name = "deg")
  public static NumberValue deg(NumberValue<?> n) {
    return new DoubleValue(Math.toDegrees(n.asDouble()));
  }

  @Expose(name = "rad")
  public static NumberValue rad(NumberValue<?> n) {
    return new DoubleValue(Math.toRadians(n.asDouble()));
  }

  // Rounding
  @Expose(name = "round")
  public static NumberValue round(NumberValue<?> n) {
    return new LongValue(Math.round(n.asDouble()));
  }

  @Expose(name = "floor")
  public static NumberValue floor(NumberValue<?> n) {
    return new DoubleValue(Math.floor(n.asDouble()));
  }

  @Expose(name = "ceil")
  public static NumberValue ceil(NumberValue<?> n) {
    return new DoubleValue(Math.ceil(n.asDouble()));
  }

}
