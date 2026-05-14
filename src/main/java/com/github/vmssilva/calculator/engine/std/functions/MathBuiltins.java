package com.github.vmssilva.calculator.engine.std.functions;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.std.value.IntValue;
import com.github.vmssilva.calculator.engine.std.value.NumberValue;
import com.github.vmssilva.calculator.engine.std.value.Value;
import com.github.vmssilva.calculator.engine.utils.MathFunctions;
import com.github.vmssilva.calculator.engine.utils.NumericOperations;

public final class MathBuiltins {

  private MathBuiltins() {
  }

  @Builtin(name = "add", description = "Sum two numbers")
  public static Value add(ApplicationContext context, NumberValue<?> x, NumberValue<?> y) {
    return x.add(y);
  }

  @Builtin(name = "subtract", description = "Subtract two numbers")
  public static NumberValue subtract(ApplicationContext context, NumberValue<?> x, NumberValue<?> y) {
    return x.sub(y);
  }

  @Builtin(name = "multiply", description = "Multiply two numbers")
  public static NumberValue multiply(ApplicationContext context, NumberValue<?> x, NumberValue<?> y) {
    return x.multiply(y);
  }

  @Builtin(name = "divide", description = "Divide two numbers")
  public static NumberValue divide(ApplicationContext context, NumberValue<?> x, NumberValue<?> y) {
    return x.div(y);
  }

  @Builtin(name = "percentage", description = "Calculate percentage")
  public static NumberValue percentage(ApplicationContext context, NumberValue<?> x, NumberValue<?> y) {
    return x.div(new IntValue(100)).multiply(y);
  }

  @Builtin(name = "remainder", description = "Calculate remainder")
  public static NumberValue remainder(ApplicationContext context, NumberValue<?> x, NumberValue<?> y) {
    return x.mod(y);
  }

  @Builtin(name = "min", description = "Return the smallest number")
  public static Value min(ApplicationContext context, NumberValue... args) {
    return NumericOperations.min(args);
  }

  @Builtin(name = "max", description = "Return the largest number")
  public static Value max(ApplicationContext context, NumberValue<?>... args) {
    return NumericOperations.max(args);
  }

  @Builtin(name = "sum", description = "Sum all numbers")
  public static Value sum(ApplicationContext context, NumberValue<?>... args) {

    if (args.length == 0)
      return new IntValue(0);

    NumberValue<?> value = args[0];

    for (int i = 1; i < args.length; i++) {
      value = value.add(args[i]);
    }

    return value;
  }

  @Builtin(name = "negate", description = "Negate a number")
  public static Value negate(ApplicationContext context, NumberValue<?> n) {
    return NumericOperations.negate(n);
  }

  // =========================
  // MATH OPERATIONS
  // =========================
  // Square root
  @Builtin(name = "sqrt", description = "Square root")
  public static NumberValue sqrt(ApplicationContext context, NumberValue<?> n) {
    return MathFunctions.sqrt(n);
  }

  @Builtin(name = "abs", description = "Absolute value")
  public static NumberValue abs(ApplicationContext context, NumberValue<?> n) {
    return MathFunctions.abs(n);
  }

  @Builtin(name = "sign", description = "Sign of a number")
  public static Value sign(ApplicationContext context, NumberValue<?> n) {
    return MathFunctions.sign(n);
  }

  @Builtin(name = "pow", description = "Power function")
  public static NumberValue pow(ApplicationContext context, NumberValue<?> base, NumberValue<?> exp) {
    return MathFunctions.pow(base, exp);
  }

  @Builtin(name = "exp", description = "Exponential function")
  public static NumberValue exp(ApplicationContext context, NumberValue<?> n) {
    return MathFunctions.exp(n);
  }

  @Builtin(name = "log", description = "Logarithm with base")
  public static NumberValue log(ApplicationContext context, NumberValue<?> x, NumberValue<?> base) {
    return MathFunctions.log(x, base);
  }

  @Builtin(name = "ln", description = "Natural logarithm")
  public static NumberValue ln(ApplicationContext context, NumberValue<?> n) {
    return MathFunctions.ln(n);
  }

  @Builtin(name = "log10", description = "Base-10 logarithm")
  public static NumberValue log10(ApplicationContext context, NumberValue<?> n) {
    return MathFunctions.log10(n);
  }

  // =========================
  // GEOMETRY
  // =========================
  @Builtin(name = "hypot", description = "Hypotenuse of two numbers")
  public static NumberValue hypot(ApplicationContext context, NumberValue<?> x, NumberValue<?> y) {
    return MathFunctions.hypot(x, y);
  }

  @Builtin(name = "clamp", description = "Clamp value between min and max")
  public static NumberValue clamp(ApplicationContext context, NumberValue<?> value, NumberValue<?> min,
      NumberValue<?> max) {
    return MathFunctions.clamp(value, min, max);
  }

  // TRIG
  @Builtin(name = "sin", description = "Sine function")
  public static NumberValue sin(ApplicationContext context, NumberValue<?> n) {
    return MathFunctions.sin(n);
  }

  @Builtin(name = "cos", description = "Cosine function")
  public static NumberValue cos(ApplicationContext context, NumberValue<?> n) {
    return MathFunctions.cos(n);
  }

  @Builtin(name = "tan", description = "Tangent function")
  public static NumberValue tan(ApplicationContext context, NumberValue<?> n) {
    return MathFunctions.tan(n);
  }

  @Builtin(name = "asin", description = "Arc sine function")
  public static NumberValue asin(ApplicationContext context, NumberValue<?> n) {
    return MathFunctions.asin(n);
  }

  @Builtin(name = "acos", description = "Arc cosine function")
  public static NumberValue acos(ApplicationContext context, NumberValue<?> n) {
    return MathFunctions.acos(n);
  }

  @Builtin(name = "atan", description = "Arc tangent function")
  public static NumberValue atan(ApplicationContext context, NumberValue<?> n) {
    return MathFunctions.atan(n);
  }

  // =========================
  // ANGLES
  // =========================
  @Builtin(name = "deg", description = "Convert radians to degrees")
  public static NumberValue deg(ApplicationContext context, NumberValue<?> n) {
    return MathFunctions.deg(n);
  }

  @Builtin(name = "rad", description = "Convert degrees to radians")
  public static NumberValue rad(ApplicationContext context, NumberValue<?> n) {
    return MathFunctions.rad(n);
  }

  // Rounding
  @Builtin(name = "round", description = "Round number")
  public static NumberValue round(ApplicationContext context, NumberValue<?> n) {
    return MathFunctions.round(n);
  }

  @Builtin(name = "floor", description = "Floor number")
  public static NumberValue floor(ApplicationContext context, NumberValue<?> n) {
    return MathFunctions.floor(n);
  }

  @Builtin(name = "ceil", description = "Ceil number")
  public static NumberValue ceil(ApplicationContext context, NumberValue<?> n) {
    return MathFunctions.ceil(n);
  }

  // Utilities
  @Builtin(name = "truncate", description = "Truncate decimal part")
  public static NumberValue truncate(ApplicationContext context, NumberValue<?> n) {
    var type = n.type();

    return switch (type) {
      case DECIMAL -> new IntValue(n.asDecimal().intValue());
      default -> new IntValue(n.asInt());
    };
  }

  //
  // @Builtin(name = "factorial", description = "Factorial of a non-negative
  // integer")
  // public static NumberValue factorial(ApplicationContext context,
  // NumberValue<?> x) {

  // var value = x.unwrap();

  // if (value.compareTo(BigDecimal.ZERO) < 0) {
  // throw new ValueErrorException("factorial of negative number");
  // }

  // if (value.stripTrailingZeros().scale() > 0) {
  // throw new ValueErrorException("factorial only defined for integers");
  // }

  // int n;
  // try {
  // n = value.intValueExact();
  // } catch (ArithmeticException e) {
  // throw new ValueErrorException("number too large");
  // }

  // int max = 1000;
  // if (n > max) {
  // throw new ValueErrorException("factorial too large");
  // }

  // BigInteger result = BigInteger.ONE;

  // for (int i = 2; i <= n; i++) {
  // result = result.multiply(BigInteger.valueOf(i));
  // }

  // return new DecimalValue(new BigDecimal(result));
  // }
}
