package com.github.vmssilva.calculator.engine.std.functions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.exception.ValueErrorException;
import com.github.vmssilva.calculator.engine.std.value.NumberValue;
import com.github.vmssilva.calculator.engine.std.value.Value;
import com.github.vmssilva.calculator.engine.std.value.Values;

public final class MathFunctions {

  private MathFunctions() {
  }

  // =========================
  // BASIC OPERATIONS
  // =========================
  @Builtin(name = "add", description = "Sum two numbers")
  public static Value add(ApplicationContext context, NumberValue x, NumberValue y) {
    return new NumberValue(x.unwrap().add(y.unwrap()));
  }

  @Builtin(name = "subtract", description = "Subtract two numbers")
  public static Value subtract(ApplicationContext context, NumberValue x, NumberValue y) {
    return new NumberValue(x.unwrap().subtract(y.unwrap()));
  }

  @Builtin(name = "multiply", description = "Multiply two numbers")
  public static Value multiply(ApplicationContext context, NumberValue x, NumberValue y) {
    return new NumberValue(x.unwrap().multiply(y.unwrap()));
  }

  @Builtin(name = "divide", description = "Divide two numbers")
  public static Value divide(ApplicationContext context, NumberValue x, NumberValue y) {

    if (x.unwrap().compareTo(BigDecimal.ZERO) == 0) {
      throw new ValueErrorException("division by zero");
    }

    return new NumberValue(x.unwrap().divide(y.unwrap(), 10, RoundingMode.HALF_UP));
  }

  @Builtin(name = "percentage", description = "Calculate percentage")
  public static Value percentage(ApplicationContext context, NumberValue x, NumberValue y) {

    var left = x.unwrap().divide(new BigDecimal(100));
    var right = y.unwrap();

    return new NumberValue(left.multiply(right));
  }

  @Builtin(name = "remainder", description = "Calculate remainder")
  public static Value remainder(ApplicationContext context, NumberValue x, NumberValue y) {
    return new NumberValue(x.unwrap().remainder(y.unwrap()));
  }

  @Builtin(name = "min", description = "Return the smallest number")
  public static Value min(ApplicationContext context, NumberValue... args) {

    if (args.length == 0)
      return Values.of(0);

    BigDecimal min = args[0].unwrap();

    for (int i = 1; i < args.length; i++) {

      BigDecimal v = args[i].unwrap();

      if (v.compareTo(min) < 0) {
        min = v;
      }
    }

    return new NumberValue(min);
  }

  @Builtin(name = "max", description = "Return the largest number")
  public static Value max(ApplicationContext context, NumberValue... args) {

    if (args.length == 0)
      return Values.of(0);

    BigDecimal max = args[0].unwrap();

    for (int i = 1; i < args.length; i++) {

      BigDecimal v = args[i].unwrap();

      if (v.compareTo(max) > 0) {
        max = v;
      }
    }

    return new NumberValue(max);
  }

  @Builtin(name = "sum", description = "Sum all numbers")
  public static Value sum(ApplicationContext context, NumberValue... args) {

    BigDecimal acc = BigDecimal.ZERO;

    for (NumberValue v : args) {
      acc = acc.add(v.unwrap());
    }

    return new NumberValue(acc);
  }

  @Builtin(name = "negate", description = "Negate a number")
  public static Value negate(ApplicationContext context, NumberValue n) {
    return new NumberValue(n.unwrap().negate());
  }

  // =========================
  // MATH OPERATIONS
  // =========================
  // Square root

  @Builtin(name = "sqrt", description = "Square root")
  public static Value sqrt(ApplicationContext context, NumberValue n) {

    var v = n.unwrap();

    if (v.compareTo(BigDecimal.ZERO) < 0) {
      throw new ValueErrorException(
          "sqrt of negative number");
    }

    return new NumberValue(
        new BigDecimal(Math.sqrt(v.doubleValue())));
  }

  @Builtin(name = "abs", description = "Absolute value")
  public static Value abs(ApplicationContext context, NumberValue n) {
    return new NumberValue(n.unwrap().abs());
  }

  @Builtin(name = "sign", description = "Sign of a number")
  public static Value sign(ApplicationContext context, NumberValue n) {

    int sign = n.unwrap().compareTo(BigDecimal.ZERO);

    return new NumberValue(
        BigDecimal.valueOf(sign));
  }

  @Builtin(name = "pow", description = "Power function")
  public static Value pow(ApplicationContext context, NumberValue base, NumberValue exp) {

    var left = base.unwrap();
    var right = exp.unwrap();

    return new NumberValue(BigDecimal.valueOf(Math.pow(left.doubleValue(), right.doubleValue())));
  }

  @Builtin(name = "exp", description = "Exponential function")
  public static Value exp(ApplicationContext context, NumberValue n) {
    return new NumberValue(BigDecimal.valueOf(Math.exp(n.unwrap().doubleValue())));
  }

  @Builtin(name = "log", description = "Logarithm with base")
  public static Value log(ApplicationContext context, NumberValue x, NumberValue base) {

    var _x = x.unwrap();
    var _base = base.unwrap();

    if (_x.compareTo(BigDecimal.ZERO) <= 0) {
      throw new ValueErrorException(
          "log(x, base): x must be > 0");
    }

    if (_base.compareTo(BigDecimal.ZERO) <= 0
        || _base.compareTo(BigDecimal.ONE) == 0) {
      throw new ValueErrorException(
          "log(x, base): base must be > 0 and != 1");
    }

    double result = Math.log(_x.doubleValue())
        / Math.log(_base.doubleValue());

    return new NumberValue(
        BigDecimal.valueOf(result));
  }

  @Builtin(name = "ln", description = "Natural logarithm")
  public static Value ln(ApplicationContext context, NumberValue n) {

    var x = n.unwrap();

    if (x.compareTo(BigDecimal.ZERO) <= 0) {
      throw new ValueErrorException(
          "ln(x): x must be > 0");
    }

    return new NumberValue(BigDecimal.valueOf(Math.log(x.doubleValue())));
  }

  @Builtin(name = "log10", description = "Base-10 logarithm")
  public static Value log10(ApplicationContext context, NumberValue n) {

    var x = n.unwrap();

    if (x.compareTo(BigDecimal.ZERO) <= 0) {
      throw new ValueErrorException(
          "log10(x): x must be > 0");
    }

    return new NumberValue(BigDecimal.valueOf(Math.log10(x.doubleValue())));
  }

  // =========================
  // GEOMETRY
  // =========================
  @Builtin(name = "hypot", description = "Hypotenuse of two numbers")
  public static Value hypot(ApplicationContext context, NumberValue x, NumberValue y) {

    var l = x.unwrap();
    var r = y.unwrap();

    return new NumberValue(BigDecimal.valueOf(Math.hypot(l.doubleValue(), r.doubleValue())));
  }

  @Builtin(name = "clamp", description = "Clamp value between min and max")
  public static Value clamp(ApplicationContext context, NumberValue value, NumberValue min, NumberValue max) {

    var _value = value.unwrap();
    var _min = min.unwrap();
    var _max = max.unwrap();

    if (_value.compareTo(_min) < 0) {
      return new NumberValue(_min);
    }

    if (_value.compareTo(_max) > 0) {
      return new NumberValue(_max);
    }

    return new NumberValue(_value);
  }

  // TRIG
  @Builtin(name = "sin", description = "Sine function")
  public static Value sin(ApplicationContext context, NumberValue n) {
    return new NumberValue(BigDecimal.valueOf(Math.sin(n.unwrap().doubleValue())));
  }

  @Builtin(name = "cos", description = "Cosine function")
  public static Value cos(ApplicationContext context, NumberValue n) {
    return new NumberValue(BigDecimal.valueOf(Math.cos(n.unwrap().doubleValue())));
  }

  @Builtin(name = "tan", description = "Tangent function")
  public static Value tan(ApplicationContext context, NumberValue n) {
    return new NumberValue(BigDecimal.valueOf(Math.tan(n.unwrap().doubleValue())));
  }

  @Builtin(name = "asin", description = "Arc sine function")
  public static Value asin(ApplicationContext context, NumberValue n) {
    return new NumberValue(BigDecimal.valueOf(Math.asin(n.unwrap().doubleValue())));
  }

  @Builtin(name = "acos", description = "Arc cosine function")
  public static Value acos(ApplicationContext context, NumberValue n) {
    return new NumberValue(BigDecimal.valueOf(Math.acos(n.unwrap().doubleValue())));
  }

  @Builtin(name = "atan", description = "Arc tangent function")
  public static Value atan(ApplicationContext context, NumberValue n) {
    return new NumberValue(BigDecimal.valueOf(Math.atan(n.unwrap().doubleValue())));
  }

  // =========================
  // ANGLES
  // =========================
  @Builtin(name = "deg", description = "Convert radians to degrees")
  public static Value deg(ApplicationContext context, NumberValue n) {
    return new NumberValue(BigDecimal.valueOf(Math.toDegrees(n.unwrap().doubleValue())));
  }

  @Builtin(name = "rad", description = "Convert degrees to radians")
  public static Value rad(ApplicationContext context, NumberValue n) {
    return new NumberValue(BigDecimal.valueOf(Math.toRadians(n.unwrap().doubleValue())));
  }

  // Rounding
  @Builtin(name = "round", description = "Round number")
  public static Value round(ApplicationContext context, NumberValue n) {
    return new NumberValue(BigDecimal.valueOf(Math.round(n.unwrap().doubleValue())));
  }

  @Builtin(name = "floor", description = "Floor number")
  public static Value floor(ApplicationContext context, NumberValue n) {
    return new NumberValue(BigDecimal.valueOf(Math.floor(n.unwrap().doubleValue())));
  }

  @Builtin(name = "ceil", description = "Ceil number")
  public static Value ceil(ApplicationContext context, NumberValue n) {
    return new NumberValue(BigDecimal.valueOf(Math.ceil(n.unwrap().doubleValue())));
  }

  // Utilities
  @Builtin(name = "truncate", description = "Truncate decimal part")
  public static Value truncate(ApplicationContext context, NumberValue n) {
    return new NumberValue(n.unwrap().setScale(0, RoundingMode.DOWN));
  }

  @Builtin(name = "factorial", description = "Factorial of a non-negative integer")
  public static Value factorial(ApplicationContext context, NumberValue x) {

    var value = x.unwrap();

    if (value.compareTo(BigDecimal.ZERO) < 0) {
      throw new ValueErrorException("factorial of negative number");
    }

    if (value.stripTrailingZeros().scale() > 0) {
      throw new ValueErrorException("factorial only defined for integers");
    }

    int n;
    try {
      n = value.intValueExact();
    } catch (ArithmeticException e) {
      throw new ValueErrorException("number too large");
    }

    int max = 1000;
    if (n > max) {
      throw new ValueErrorException("factorial too large");
    }

    BigInteger result = BigInteger.ONE;

    for (int i = 2; i <= n; i++) {
      result = result.multiply(BigInteger.valueOf(i));
    }

    return new NumberValue(new BigDecimal(result));
  }
}
