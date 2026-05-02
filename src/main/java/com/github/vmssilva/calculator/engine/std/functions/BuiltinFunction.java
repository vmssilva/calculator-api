package com.github.vmssilva.calculator.engine.std.functions;

import java.util.function.Supplier;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.value.BuiltinFunctionValue;
import com.github.vmssilva.calculator.engine.value.FunctionValue;

public enum BuiltinFunction {

  // Predicate functions
  IS_POSITIVE("isPositive", "Returns 1 if is positive, 0 otherwise", PredicateFunctions::isPositive),
  IS_NEGATIVE("isNegative", "Return 1 if is negative, 0 otherwise", PredicateFunctions::isNegative),
  IS_ZERO("isZero", "Returns 1 if is ZERO, 0 otherwise", PredicateFunctions::isZero),

  // Basic operations
  ADD("add", "Sum two numbers", MathFunctions::add),
  DIVIDE("divide", "Divide two numbers", MathFunctions::divide),
  SUBTRACT("subtract", "Subtract two numbers", MathFunctions::subtract),
  MULTIPLY("multiply", "Multiply two numbers", MathFunctions::multiply),
  PERCENTAGE("percentage", "Calculate percentage of a number", MathFunctions::percentage),

  // Extras
  ENV("env", "Displays enrironment", UtilFunctions::env),
  UNSET("unset", "Remove identifier of current scope", UtilFunctions::unset),
  SUM("sum", "Sum all numbers", MathFunctions::sum),
  MIN("min", "Minimum value", MathFunctions::min),
  MAX("max", "Maximum value", MathFunctions::max),
  ABS("abs", "Absolute value", MathFunctions::abs),
  TRUNCATE("truncate", "Truncate decimal part", MathFunctions::truncate),
  NEGATE("negate", "Negate number", MathFunctions::negate),
  FACTORIAL("factorial", "Factorial of a non-negative integer", MathFunctions::factorial),
  REMAINDER("remainder", "Remainder of division", MathFunctions::remainder),

  // Rounding
  ROUND("round", "Round to nearest integer", MathFunctions::round),
  FLOOR("floor", "Floor", MathFunctions::floor),
  CEIL("ceil", "Ceiling", MathFunctions::ceil),

  // Math
  SQRT("sqrt", "Square root", MathFunctions::sqrt),
  LOG("log", "Logarithm", MathFunctions::log),
  LOG10("log10", "Base-10 logarithm", MathFunctions::log10),
  POW("pow", "Power function", MathFunctions::pow),
  EXP("exp", "e^x", MathFunctions::exp),
  LN("ln", "Natural logarithm", MathFunctions::ln),
  HYPOT("hypot", "srqt(x^2 + y^2)", MathFunctions::hypot),
  CLAMP("clamp", "Clamp value between min and max", MathFunctions::clamp),
  ASIN("asin", "Arc sine", MathFunctions::asin),
  ACOS("acos", "Arc cosine", MathFunctions::acos),
  ATAN("atan", "Arc tangent", MathFunctions::atan),
  SIN("sin", "Sine", MathFunctions::sin),
  COS("cos", "Cosine", MathFunctions::cos),
  TAN("tan", "Tangent", MathFunctions::tan),
  DEG("deg", "Radians to degrees", MathFunctions::deg),
  RAD("rad", "Degrees to radians", MathFunctions::rad),
  SIGN("sign", "Sign of number (-1, 0, 1)", MathFunctions::sign);

  private final String name;
  private final String description;

  private final Supplier<FunctionValue> supplier;

  BuiltinFunction(String name, String description, Supplier<FunctionValue> supplier) {
    this.name = name;
    this.description = description;
    this.supplier = supplier;
  }

  public String key() {
    return name;
  }

  public String description() {
    return description;
  }

  public FunctionValue create(ApplicationContext context) {
    return new BuiltinFunctionValue(supplier.get(), context);
  }
}
