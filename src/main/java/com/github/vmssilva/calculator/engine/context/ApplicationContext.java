package com.github.vmssilva.calculator.engine.context;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.github.vmssilva.calculator.engine.exception.CalculatorRuntimeException;
import com.github.vmssilva.calculator.engine.value.BaseFunctionValue;
import com.github.vmssilva.calculator.engine.value.ErrorValue;
import com.github.vmssilva.calculator.engine.value.FunctionValue;
import com.github.vmssilva.calculator.engine.value.NumberValue;
import com.github.vmssilva.calculator.engine.value.Value;
import com.github.vmssilva.calculator.engine.value.Values;

public class ApplicationContext {

  private Deque<Scope> scopes = new ArrayDeque<>();
  private final Scope builtins;

  public ApplicationContext() {
    this.builtins = new Scope(null);
    loadBuiltin(builtins);
    var global = new Scope(builtins);
    this.scopes.push(new Scope(global));
  }

  public Scope getBuiltins() {
    return builtins;
  }

  public void pushScope() {
    scopes.push(new Scope(scopes.peek()));
  }

  public void popScope() {
    if (scopes.size() > 1)
      this.scopes.pop();
  }

  public void set(String name, Value value) {
    scopes.peek().set(name, value);
  }

  public Value get(String name) {
    return scopes.peek().get(name);
  }

  public void addFunction(String name, FunctionValue fn) {
    peek().set(name, fn);
  }

  private Scope peek() {
    return scopes.peek();
  }

  public boolean has(String name) {
    if (peek().has(name))
      return true;

    if (peek().parent != null)
      return peek().parent.has(name);

    return false;
  }

  private class Scope {
    private Scope parent;
    private Map<String, Value> values;

    private Scope(Scope parent) {
      this.parent = parent;
      this.values = new HashMap<>();
    }

    public void set(String name, Value value) {
      values.put(name, value);
    }

    public Value get(String name) {
      if (has(name))
        return values.get(name);

      if (parent != null)
        return parent.get(name);

      throw new CalculatorRuntimeException("Execution error: '" + name + "' is not defined");
    }

    public boolean has(String name) {
      if (values.containsKey(name))
        return true;

      return false;
    }

    public Map<String, Value> entries() {
      return Collections.unmodifiableMap(values);
    }

    public Scope parent() {
      return parent;
    }

    public boolean remove(String name) {
      if (parent == null) { // root/builtins layer protection
        return false;
      }

      return values.remove(name) != null;
    }
  }

  public Map<String, Value> flatten() {
    Map<String, Value> result = new LinkedHashMap<>();

    Scope current = scopes.peek();

    while (current != null) {
      for (var entry : current.entries().entrySet()) {
        result.putIfAbsent(entry.getKey(), entry.getValue());
      }
      current = current.parent();
    }

    return result;
  }

  private void assertUnaryFunction(List<Value> args) {
    asserNumArgs(args, 1);
  }

  private void assertBinaryFunction(List<Value> args) {
    asserNumArgs(args, 2);
  }

  private void asserNumArgs(List<Value> args, int count) {
    if (args.isEmpty())
      throw new CalculatorRuntimeException("Missing operands");

    if (args.size() < count)
      throw new CalculatorRuntimeException("Missing right operand");

    if (args.size() > count)
      throw new CalculatorRuntimeException("Too much arguments");

  }

  public static FunctionValue fn(Function<List<Value>, Value> impl, String repr) {
    return fn(impl, repr, false);
  }

  public static FunctionValue fn(Function<List<Value>, Value> impl, String repr, boolean safeMode) {
    return new BaseFunctionValue() {

      @Override
      public Value apply(List<Value> args) {

        if (!safeMode) {
          return impl.apply(args);
        }

        try {
          return impl.apply(args);
        } catch (CalculatorRuntimeException e) {
          return new ErrorValue(e.getMessage());
        } catch (ArithmeticException e) {
          return new ErrorValue("Math error: " + e.getMessage());
        } catch (Exception e) {
          return new ErrorValue("Internal error: " + e.getMessage());
        }
      }

      @Override
      public String toString() {
        return repr;
      }
    };
  }

  private void loadBuiltin(Scope builtins) {

    builtins.set("PI", new NumberValue(new BigDecimal(Math.PI)));
    builtins.set("E", new NumberValue(new BigDecimal(Math.E)));

    builtins.set("unset", fn(args -> {

      if (args.size() != 1) {
        return new ErrorValue("unset(name) expects 1 argument");
      }

      String name = args.get(0).toString();

      boolean removed = peek().remove(name);

      if (!removed) {
        return new ErrorValue("variable '" + name + "' not found in current scope");
      }

      return new NumberValue(BigDecimal.ZERO);

    }, "unset(name)", false));

    builtins.set("env", fn(args -> {

      if (!args.isEmpty()) {
        return new ErrorValue("env() takes no arguments");
      }

      System.out.println("Environment:");

      flatten().entrySet().stream()
          .sorted(Map.Entry.comparingByKey())
          .forEach(entry -> {

            var name = entry.getKey();
            var value = entry.getValue();

            if (value instanceof BaseFunctionValue fn) {
              System.out.println("fn  " + name + " -> " + fn);
            } else {
              System.out.println("var " + name + " = " + value);
            }
          });

      return new NumberValue(BigDecimal.ZERO);

    }, "env()", false));

    builtins.set("functions", fn(args -> {

      if (!args.isEmpty()) {
        return new ErrorValue("functions() takes no arguments");
      }

      System.out.println("Built-in functions:");

      for (var entry : getBuiltins().entries().entrySet()) {
        var name = entry.getKey();
        var value = entry.getValue();

        if (value instanceof BaseFunctionValue fn) {
          System.out.println(name + " => " + fn);
        }
      }

      return new NumberValue(BigDecimal.ZERO);

    }, "functions()", false));

    builtins.set("round", fn(args -> {
      if (args.size() == 1) {
        var v = Values.asNumber(args.get(0));
        return new NumberValue(v.setScale(0, RoundingMode.HALF_UP));
      }

      if (args.size() == 2) {
        var v = Values.asNumber(args.get(0));
        var scale = Values.asNumber(args.get(1)).intValue();
        return new NumberValue(v.setScale(scale, RoundingMode.HALF_UP));
      }

      return new ErrorValue("round expects 1 or 2 arguments");
    }, "round(x [, n])", true));

    builtins.set("min", fn(args -> {

      if (args.isEmpty()) {
        return new ErrorValue("min expects at least 1 argument");
      }

      BigDecimal result = Values.asNumber(args.get(0));

      for (int i = 1; i < args.size(); i++) {
        var value = Values.asNumber(args.get(i));
        result = result.min(value);
      }

      return new NumberValue(result);

    }, "min(x1, x2, ...)", false));

    builtins.set("max", fn(args -> {

      if (args.isEmpty()) {
        return new ErrorValue("max expects at least 1 argument");
      }

      BigDecimal result = Values.asNumber(args.get(0));

      for (int i = 1; i < args.size(); i++) {
        var value = Values.asNumber(args.get(i));
        result = result.max(value);
      }

      return new NumberValue(result);

    }, "max(x1, x2, ...)", false));

    builtins.set("exp", fn(args -> {
      assertUnaryFunction(args);

      var v = Values.asNumber(args.get(0));
      return new NumberValue(new BigDecimal(Math.exp(v.doubleValue())));
    }, "exp(x)", true));

    builtins.set("ln", fn(args -> {
      assertUnaryFunction(args);

      var v = Values.asNumber(args.get(0));

      if (v.compareTo(BigDecimal.ZERO) <= 0) {
        return new ErrorValue("ln of non-positive number");
      }

      return new NumberValue(new BigDecimal(Math.log(v.doubleValue())));
    }, "ln(x)", true));

    builtins.set("log10", fn(args -> {
      assertUnaryFunction(args);

      var v = Values.asNumber(args.get(0));

      if (v.compareTo(BigDecimal.ZERO) <= 0) {
        return new ErrorValue("log10 of non-positive number");
      }

      return new NumberValue(new BigDecimal(Math.log10(v.doubleValue())));
    }, "log10(x)", true));

    builtins.set("hypot", fn(args -> {
      assertBinaryFunction(args);

      var x = Values.asNumber(args.get(0));
      var y = Values.asNumber(args.get(1));

      double result = Math.hypot(x.doubleValue(), y.doubleValue());
      return new NumberValue(new BigDecimal(result));
    }, "hypot(x, y)", true));

    builtins.set("clamp", fn(args -> {
      if (args.size() != 3) {
        return new ErrorValue("clamp expects 3 arguments");
      }

      var x = Values.asNumber(args.get(0));
      var min = Values.asNumber(args.get(1));
      var max = Values.asNumber(args.get(2));

      if (min.compareTo(max) > 0) {
        return new ErrorValue("min cannot be greater than max");
      }

      if (x.compareTo(min) < 0)
        return new NumberValue(min);
      if (x.compareTo(max) > 0)
        return new NumberValue(max);

      return new NumberValue(x);
    }, "clamp(x, min, max)", false));

    builtins.set("asin", fn(args -> {
      assertUnaryFunction(args);

      var v = Values.asNumber(args.get(0)).doubleValue();

      if (v < -1 || v > 1) {
        return new ErrorValue("asin domain is [-1,1]");
      }

      return new NumberValue(new BigDecimal(Math.asin(v)));
    }, "asin(x)", true));

    builtins.set("acos", fn(args -> {
      assertUnaryFunction(args);

      var v = Values.asNumber(args.get(0)).doubleValue();

      if (v < -1 || v > 1) {
        return new ErrorValue("acos domain is [-1,1]");
      }

      return new NumberValue(new BigDecimal(Math.acos(v)));
    }, "acos(x)", true));

    builtins.set("atan", fn(args -> {
      assertUnaryFunction(args);

      var v = Values.asNumber(args.get(0)).doubleValue();
      return new NumberValue(new BigDecimal(Math.atan(v)));
    }, "atan(x)", true));

    builtins.set("deg", fn(args -> {
      assertUnaryFunction(args);

      var v = Values.asNumber(args.get(0)).doubleValue();
      return new NumberValue(new BigDecimal(Math.toDegrees(v)));
    }, "deg(x)", false));

    builtins.set("rad", fn(args -> {
      assertUnaryFunction(args);

      var v = Values.asNumber(args.get(0)).doubleValue();
      return new NumberValue(new BigDecimal(Math.toRadians(v)));
    }, "rad(x)", false));

    builtins.set("sign", fn(args -> {
      assertUnaryFunction(args);

      var v = Values.asNumber(args.get(0));

      return new NumberValue(
          BigDecimal.valueOf(v.compareTo(BigDecimal.ZERO)));
    }, "sign(x)", false));

    builtins.set("isZero", fn(args -> {
      assertUnaryFunction(args);

      var v = Values.asNumber(args.get(0));

      return new NumberValue(
          v.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ONE : BigDecimal.ZERO);
    }, "isZero(x)", false));

    builtins.set("isPositive", fn(args -> {
      assertUnaryFunction(args);

      var v = Values.asNumber(args.get(0));

      return new NumberValue(
          v.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.ONE : BigDecimal.ZERO);
    }, "isPositive(x)", false));

    builtins.set("isNegative", fn(args -> {
      assertUnaryFunction(args);

      var v = Values.asNumber(args.get(0));

      return new NumberValue(
          v.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ONE : BigDecimal.ZERO);
    }, "isNegative(x)", false));

    builtins.set("factorial", fn(args -> {
      assertUnaryFunction(args);

      var value = Values.asNumber(args.get(0));
      int max = 1000;

      // não permite negativos
      if (value.compareTo(BigDecimal.ZERO) < 0) {
        return new ErrorValue("factorial of negative number");
      }

      // precisa ser inteiro
      if (value.stripTrailingZeros().scale() > 0) {
        return new ErrorValue("factorial only defined for integers");
      }

      int n;
      try {
        n = value.intValueExact();
      } catch (ArithmeticException e) {
        return new ErrorValue("number too large");
      }

      // limite de segurança (opcional, mas recomendado)
      if (n > max) {
        return new ErrorValue("factorial too large");
      }

      BigInteger result = BigInteger.ONE;

      for (int i = 2; i <= n; i++) {
        result = result.multiply(BigInteger.valueOf(i));
      }

      return new NumberValue(new BigDecimal(result));

    }, "factorial(x)", true));

    builtins.set("add", fn(args -> {
      assertBinaryFunction(args);

      var left = Values.asNumber(args.get(0));
      var right = Values.asNumber(args.get(1));

      return new NumberValue(left.add(right));
    }, "add(x, y)"));

    builtins.set("subtract", fn(args -> {
      assertBinaryFunction(args);

      var left = Values.asNumber(args.get(0));
      var right = Values.asNumber(args.get(1));

      return new NumberValue(left.subtract(right));
    }, "subtract(x, y)"));

    builtins.set("multiply", fn(args -> {
      assertBinaryFunction(args);

      var left = Values.asNumber(args.get(0));
      var right = Values.asNumber(args.get(1));

      return new NumberValue(left.multiply(right));
    }, "multiply(x, y)"));

    builtins.set("divide", fn(args -> {
      assertBinaryFunction(args);

      var left = Values.asNumber(args.get(0));
      var right = Values.asNumber(args.get(1));

      if (right.compareTo(BigDecimal.ZERO) == 0) {
        return new ErrorValue("division by zero");
      }

      return new NumberValue(left.divide(right, 10, RoundingMode.HALF_UP));
    }, "divide(x, y)", true));

    builtins.set("sqrt", fn(args -> {
      assertUnaryFunction(args);

      var value = Values.asNumber(args.get(0));

      if (value.compareTo(BigDecimal.ZERO) < 0) {
        return new ErrorValue("sqrt of negative number");
      }

      return new NumberValue(new BigDecimal(Math.sqrt(value.doubleValue())));
    }, "sqrt(x)", true));

    builtins.set("log", fn(args -> {
      assertUnaryFunction(args);

      var value = Values.asNumber(args.get(0));

      if (value.compareTo(BigDecimal.ZERO) <= 0) {
        return new ErrorValue("log of non-positive number");
      }

      return new NumberValue(new BigDecimal(Math.log(value.doubleValue())));
    }, "log(x)", true));

    builtins.set("remainder", fn(args -> {
      assertBinaryFunction(args);

      var left = Values.asNumber(args.get(0));
      var right = Values.asNumber(args.get(1));

      if (right.compareTo(BigDecimal.ZERO) == 0) {
        return new ErrorValue("remainder by zero");
      }

      return new NumberValue(left.remainder(right));
    }, "remainder(x, y)"));

    builtins.set("pow", fn(args -> {
      assertBinaryFunction(args);

      var left = Values.asNumber(args.get(0));
      var right = Values.asNumber(args.get(1));

      try {
        return new NumberValue(
            left.pow(right.intValue(), MathContext.DECIMAL64));
      } catch (ArithmeticException e) {
        return new ErrorValue("invalid exponentiation");
      }

    }, "pow(x, y)", true));

    builtins.set("abs", fn(args -> {
      assertUnaryFunction(args);

      var value = Values.asNumber(args.get(0));
      return new NumberValue(value.abs());
    }, "abs(x)"));

    builtins.set("negate", fn(args -> {
      assertUnaryFunction(args);

      var value = Values.asNumber(args.get(0));
      return new NumberValue(value.negate());
    }, "negate(x)"));

    builtins.set("sin", fn(args -> {
      assertUnaryFunction(args);

      var value = Values.asNumber(args.get(0));
      return new NumberValue(new BigDecimal(Math.sin(value.doubleValue())));
    }, "sin(x)", true));

    builtins.set("cos", fn(args -> {
      assertUnaryFunction(args);

      var value = Values.asNumber(args.get(0));
      return new NumberValue(new BigDecimal(Math.cos(value.doubleValue())));
    }, "cos(x)", true));

    builtins.set("tan", fn(args -> {
      assertUnaryFunction(args);

      var value = Values.asNumber(args.get(0));
      return new NumberValue(new BigDecimal(Math.tan(value.doubleValue())));
    }, "tan(x)", true));

    builtins.set("floor", fn(args -> {
      assertUnaryFunction(args);

      var value = Values.asNumber(args.get(0));
      return new NumberValue(new BigDecimal(Math.floor(value.doubleValue())));
    }, "floor(x)"));

    builtins.set("ceil", fn(args -> {
      assertUnaryFunction(args);

      var value = Values.asNumber(args.get(0));
      return new NumberValue(new BigDecimal(Math.ceil(value.doubleValue())));
    }, "ceil(x)"));

    builtins.set("truncate", fn(args -> {
      assertUnaryFunction(args);

      return new NumberValue(
          new BigDecimal(Values.asNumber(args.get(0)).intValue()));
    }, "truncate(x)"));
  }
}
