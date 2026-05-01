package com.github.vmssilva.calculator.engine.std.functions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import com.github.vmssilva.calculator.engine.exception.ValueErrorException;
import com.github.vmssilva.calculator.engine.value.FunctionValue;
import com.github.vmssilva.calculator.engine.value.NumberValue;
import com.github.vmssilva.calculator.engine.value.Value;
import com.github.vmssilva.calculator.engine.value.Values;
import com.github.vmssilva.calculator.engine.std.ValueType;

public final class MathFunctions {

  private MathFunctions() {
  }

  private static ValueType[] binaryNumericExpression() {
    return new ValueType[] { ValueType.NUMBER, ValueType.NUMBER };
  }

  private static ValueType[] unaryNumericExpression() {
    return new ValueType[] { ValueType.NUMBER };
  }

  // =========================
  // BASIC OPERATIONS
  // =========================
  public static FunctionValue add() {
    String name = "add";

    return FunctionFactory.of(name, (context, args) -> {
      var l = Values.asNumber(args.get(0));
      var r = Values.asNumber(args.get(1));

      return new NumberValue(l.add(r));
    }, binaryNumericExpression(), true);
  }

  public static FunctionValue subtract() {
    String name = "subtract";

    return FunctionFactory.of(name, (context, args) -> {
      var l = Values.asNumber(args.get(0));
      var r = Values.asNumber(args.get(1));
      return new NumberValue(l.subtract(r));
    }, binaryNumericExpression());
  }

  public static FunctionValue multiply() {
    String name = "multiply";

    return FunctionFactory.of(name, (context, args) -> {
      var l = Values.asNumber(args.get(0));
      var r = Values.asNumber(args.get(1));
      return new NumberValue(l.multiply(r));
    }, binaryNumericExpression());
  }

  public static FunctionValue divide() {
    String name = "divide";

    return FunctionFactory.of(name, (context, args) -> {
      var l = Values.asNumber(args.get(0));
      var r = Values.asNumber(args.get(1));

      if (r.compareTo(BigDecimal.ZERO) == 0) {
        throw new ValueErrorException("division by zero");
      }

      return new NumberValue(
          l.divide(r, 10, RoundingMode.HALF_UP));
    }, binaryNumericExpression());
  }

  public static FunctionValue percentage() {
    String name = "percentage";

    return FunctionFactory.of(name, (context, args) -> {
      var left = Values.asNumber(args.get(0)).divide(new BigDecimal(100));
      var right = Values.asNumber(args.get(1));

      return new NumberValue(left.multiply(right));
    }, binaryNumericExpression());
  }

  public static FunctionValue remainder() {
    String name = "remainder";

    return FunctionFactory.of(name, (context, args) -> {
      var x = Values.asNumber(args.get(0));
      var y = Values.asNumber(args.get(1));

      return new NumberValue(x.remainder(y));
    }, binaryNumericExpression());
  }

  public static FunctionValue min() {
    String name = "min";

    return FunctionFactory.of(name, (context, args) -> {
      BigDecimal min = Values.asNumber(args.get(0));

      for (int i = 1; i < args.size(); i++) {
        BigDecimal v = Values.asNumber(args.get(i));
        if (v.compareTo(min) < 0) {
          min = v;
        }
      }

      return new NumberValue(min);
    }, new ValueType[] { ValueType.LIST });
  }

  public static FunctionValue max() {
    String name = "max";

    return FunctionFactory.of(name, (context, args) -> {
      BigDecimal max = Values.asNumber(args.get(0));

      for (int i = 1; i < args.size(); i++) {
        BigDecimal v = Values.asNumber(args.get(i));
        if (v.compareTo(max) > 0) {
          max = v;
        }
      }

      return new NumberValue(max);
    }, new ValueType[] { ValueType.LIST });
  }

  public static FunctionValue sum() {
    String name = "sum";

    return FunctionFactory.of(name, (context, args) -> {
      BigDecimal acc = BigDecimal.ZERO;
      for (Value v : args) {
        acc = acc.add(Values.asNumber(v));
      }
      return new NumberValue(acc);
    }, new ValueType[] { ValueType.LIST });
  }

  public static FunctionValue negate() {
    String name = "negate";

    return FunctionFactory.of(name, (context, args) -> {
      var x = Values.asNumber(args.get(0));
      return new NumberValue(x.negate());
    }, unaryNumericExpression());
  }

  // =========================
  // MATH OPERATIONS
  // =========================
  // Square root
  public static FunctionValue sqrt() {
    String name = "sqrt";

    return FunctionFactory.of(name, (context, args) -> {
      var v = Values.asNumber(args.get(0));

      if (v.compareTo(BigDecimal.ZERO) < 0) {
        throw new ValueErrorException("sqrt of negative number");
      }

      return new NumberValue(
          new BigDecimal(Math.sqrt(v.doubleValue())));
    }, unaryNumericExpression());
  }

  // Absolute value
  public static FunctionValue abs() {
    String name = "abs";

    return FunctionFactory.of(name, (context, args) -> {
      var x = Values.asNumber(args.get(0));
      return new NumberValue(x.abs());
    }, unaryNumericExpression());
  }

  public static FunctionValue sign() {
    String name = "sign";

    return FunctionFactory.of(name, (context, args) -> {
      var x = Values.asNumber(args.get(0));

      int sign = x.compareTo(BigDecimal.ZERO);
      return new NumberValue(BigDecimal.valueOf(sign));
    }, new ValueType[] { ValueType.NUMBER });
  }

  public static FunctionValue pow() {
    String name = "pow";

    return FunctionFactory.of(name, (context, args) -> {
      var base = Values.asNumber(args.get(0));
      var exp = Values.asNumber(args.get(1));

      return new NumberValue(
          BigDecimal.valueOf(Math.pow(
              base.doubleValue(),
              exp.doubleValue())));
    }, binaryNumericExpression());
  }

  public static FunctionValue exp() {
    String name = "exp";

    return FunctionFactory.of(name, (context, args) -> {
      var x = Values.asNumber(args.get(0));
      return new NumberValue(
          BigDecimal.valueOf(Math.exp(x.doubleValue())));
    }, unaryNumericExpression());
  }

  // Logarithm
  public static FunctionValue log() {
    String name = "log";

    return FunctionFactory.of(name, (context, args) -> {
      if (args.size() != 2) {
        throw new ValueErrorException("log(x, base) expects exactly 2 arguments");
      }

      var x = Values.asNumber(args.get(0));
      var base = Values.asNumber(args.get(1));

      if (x.compareTo(BigDecimal.ZERO) <= 0) {
        throw new ValueErrorException("log(x, base): x must be > 0");
      }

      if (base.compareTo(BigDecimal.ZERO) <= 0 || base.compareTo(BigDecimal.ONE) == 0) {
        throw new ValueErrorException("log(x, base): base must be > 0 and != 1");
      }

      double result = Math.log(x.doubleValue()) / Math.log(base.doubleValue());

      return new NumberValue(BigDecimal.valueOf(result));
    }, new ValueType[] { ValueType.LIST }); // varargs controlado manualmente
  }

  public static FunctionValue ln() {
    String name = "ln";

    return FunctionFactory.of(name, (context, args) -> {
      var x = Values.asNumber(args.get(0));

      if (x.compareTo(BigDecimal.ZERO) <= 0) {
        throw new ValueErrorException("ln(x): x must be > 0");
      }

      return new NumberValue(
          BigDecimal.valueOf(Math.log(x.doubleValue())));
    }, unaryNumericExpression());
  }

  public static FunctionValue log10() {
    String name = "log10";

    return FunctionFactory.of(name, (context, args) -> {
      var x = Values.asNumber(args.get(0));

      if (x.compareTo(BigDecimal.ZERO) <= 0) {
        throw new ValueErrorException("log10(x): x must be > 0");
      }

      return new NumberValue(
          BigDecimal.valueOf(Math.log10(x.doubleValue())));
    }, unaryNumericExpression());

  }

  // =========================
  // GEOMETRY
  // =========================

  public static FunctionValue hypot() {
    String name = "hypot";

    return FunctionFactory.of(name, (context, args) -> {
      var x = Values.asNumber(args.get(0));
      var y = Values.asNumber(args.get(1));

      return new NumberValue(
          BigDecimal.valueOf(Math.hypot(
              x.doubleValue(),
              y.doubleValue())));
    }, binaryNumericExpression());
  }

  public static FunctionValue clamp() {
    String name = "clamp";

    return FunctionFactory.of(name, (context, args) -> {
      var value = Values.asNumber(args.get(0));
      var min = Values.asNumber(args.get(1));
      var max = Values.asNumber(args.get(2));

      if (value.compareTo(min) < 0)
        return new NumberValue(min);
      if (value.compareTo(max) > 0)
        return new NumberValue(max);
      return new NumberValue(value);
    }, new ValueType[] { ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER });
  }

  // =========================
  // TRIG
  // =========================

  public static FunctionValue sin() {
    String name = "sin";

    return FunctionFactory.of(name, (context, args) -> new NumberValue(BigDecimal.valueOf(
        Math.sin(Values.asNumber(args.get(0)).doubleValue()))), unaryNumericExpression());
  }

  public static FunctionValue cos() {
    String name = "cos";

    return FunctionFactory.of(name, (context, args) -> new NumberValue(BigDecimal.valueOf(
        Math.cos(Values.asNumber(args.get(0)).doubleValue()))), unaryNumericExpression());
  }

  public static FunctionValue tan() {
    String name = "tan";

    return FunctionFactory.of(name, (context, args) -> new NumberValue(BigDecimal.valueOf(
        Math.tan(Values.asNumber(args.get(0)).doubleValue()))), unaryNumericExpression());
  }

  public static FunctionValue asin() {
    String name = "asin";

    return FunctionFactory.of(name, (context, args) -> new NumberValue(BigDecimal.valueOf(
        Math.asin(Values.asNumber(args.get(0)).doubleValue()))), unaryNumericExpression());
  }

  public static FunctionValue acos() {
    String name = "acos";

    return FunctionFactory.of(name, (context, args) -> new NumberValue(BigDecimal.valueOf(
        Math.acos(Values.asNumber(args.get(0)).doubleValue()))), unaryNumericExpression());
  }

  public static FunctionValue atan() {
    String name = "atan";

    return FunctionFactory.of(name, (context, args) -> new NumberValue(BigDecimal.valueOf(
        Math.atan(Values.asNumber(args.get(0)).doubleValue()))), unaryNumericExpression());
  }

  // =========================
  // ANGLES
  // =========================

  public static FunctionValue deg() {
    String name = "deg";

    return FunctionFactory.of(name, (context, args) -> {
      var x = Values.asNumber(args.get(0));
      return new NumberValue(
          BigDecimal.valueOf(Math.toDegrees(x.doubleValue())));
    }, unaryNumericExpression());
  }

  public static FunctionValue rad() {
    String name = "rad";

    return FunctionFactory.of(name, (context, args) -> {
      var x = Values.asNumber(args.get(0));
      return new NumberValue(
          BigDecimal.valueOf(Math.toRadians(x.doubleValue())));
    }, unaryNumericExpression());
  }

  // =========================
  // ROUNDING
  // =========================

  public static FunctionValue round() {
    String name = "round";

    return FunctionFactory.of(name, (context, args) -> {
      var x = Values.asNumber(args.get(0));
      return new NumberValue(new BigDecimal(Math.round(x.doubleValue())));
    }, unaryNumericExpression());
  }

  public static FunctionValue floor() {
    String name = "floor";

    return FunctionFactory.of(name, (context, args) -> {
      var x = Values.asNumber(args.get(0));
      return new NumberValue(
          BigDecimal.valueOf(Math.floor(x.doubleValue())));
    }, unaryNumericExpression());
  }

  public static FunctionValue ceil() {
    String name = "ceil";

    return FunctionFactory.of(name, (context, args) -> {
      var x = Values.asNumber(args.get(0));
      return new NumberValue(
          BigDecimal.valueOf(Math.ceil(x.doubleValue())));
    }, unaryNumericExpression());
  }

  // =========================
  // UTILITIES
  // =========================
  public static FunctionValue truncate() {
    String name = "truncate";

    return FunctionFactory.of(name, (context, args) -> {
      var x = Values.asNumber(args.get(0));
      return new NumberValue(x.setScale(0, RoundingMode.DOWN));
    }, unaryNumericExpression());
  }

  public static FunctionValue factorial() {
    String name = "factorial";

    return FunctionFactory.of(name, (context, args) -> {
      var value = Values.asNumber(args.get(0));

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
    }, unaryNumericExpression());
  }

}
