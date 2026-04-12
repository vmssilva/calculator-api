package com.github.vmssilva.calculator.engine.context;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.github.vmssilva.calculator.engine.value.FunctionValue;
import com.github.vmssilva.calculator.engine.value.NumberValue;
import com.github.vmssilva.calculator.engine.value.Value;
import com.github.vmssilva.calculator.engine.value.Values;

public class ApplicationContext {

  private Stack<Scope> scopes = new Stack<>();
  private Map<String, String> alias = new HashMap<>();

  public ApplicationContext() {
    var builtins = new Scope(null);
    loadBuiltin(builtins);
    var global = new Scope(builtins);
    this.scopes.push(new Scope(global));
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
    if (alias.containsKey(name))
      return scopes.peek().get(alias.get(name));

    return scopes.peek().get(name);
  }

  public Scope scope() {
    return peek();
  }

  public boolean has(String name) {
    if (scope().has(name))
      return true;

    if (peek().parent != null)
      return scope().parent.has(name);

    return false;
  }

  private Scope peek() {
    return scopes.peek();
  }

  private class Scope {
    private Scope parent;
    private Map<String, Value> map;

    private Scope(Scope parent) {
      this.parent = parent;
      this.map = new HashMap<>();
    }

    public Value get(String name) {
      if (has(name))
        return map.get(name);

      if (parent != null)
        return parent.get(name);

      throw new RuntimeException(name + " is not defined");
    }

    public void set(String name, Value value) {
      map.put(name, value);
    }

    public boolean has(String name) {
      if (map.containsKey(name))
        return true;

      return false;
    }
  }

  private void assertUnaryFunction(List<Value> args) {
    asserNumArgs(args, 1);
  }

  private void assertBinaryFunction(List<Value> args) {
    asserNumArgs(args, 2);
  }

  private void asserNumArgs(List<Value> args, int count) {
    if (args.isEmpty())
      throw new IllegalArgumentException("Missing operands");

    if (args.size() < count)
      throw new IllegalArgumentException("Missing right operand");

    if (args.size() > count)
      throw new IllegalArgumentException("Too much arguments");

  }

  private void loadBuiltin(Scope builtins) {
    builtins.set("PI", new NumberValue(new BigDecimal(Math.PI)));
    builtins.set("E", new NumberValue(new BigDecimal(Math.E)));

    builtins.set("add", (FunctionValue) args -> {
      assertBinaryFunction(args);
      var left = Values.asNumber(args.get(0));
      var right = Values.asNumber(args.get(1));
      return new NumberValue(left.add(right));
    });

    builtins.set("subtract", (FunctionValue) args -> {
      assertBinaryFunction(args);
      var left = Values.asNumber(args.get(0));
      var right = Values.asNumber(args.get(1));
      return new NumberValue(left.subtract(right));
    });

    builtins.set("multiply", (FunctionValue) args -> {
      assertBinaryFunction(args);
      var left = Values.asNumber(args.get(0));
      var right = Values.asNumber(args.get(1));
      return new NumberValue(left.multiply(right));
    });

    builtins.set("divide", (FunctionValue) args -> {
      assertBinaryFunction(args);
      var left = Values.asNumber(args.get(0));
      var right = Values.asNumber(args.get(1));
      return new NumberValue(left.divide(right, 10, RoundingMode.HALF_UP));
    });

    builtins.set("remainder", (FunctionValue) args -> {
      assertBinaryFunction(args);
      var left = Values.asNumber(args.get(0));
      var right = Values.asNumber(args.get(1));
      return new NumberValue(left.remainder(right));
    });

    builtins.set("abs", (FunctionValue) args -> {
      assertUnaryFunction(args);
      var value = Values.asNumber(args.get(0));
      return new NumberValue(value.abs());
    });

    builtins.set("negate", (FunctionValue) args -> {
      assertUnaryFunction(args);
      var value = Values.asNumber(args.get(0));
      return new NumberValue(value.negate());
    });

    builtins.set("pow", (FunctionValue) args -> {
      assertBinaryFunction(args);
      var left = Values.asNumber(args.get(0));
      var right = Values.asNumber(args.get(1));
      return new NumberValue(left.pow(right.intValue()));
    });

    builtins.set("sqrt", (FunctionValue) args -> {
      assertUnaryFunction(args);
      var value = Values.asNumber(args.get(0));
      return new NumberValue(new BigDecimal(Math.sqrt(value.doubleValue())));
    });

    builtins.set("sin", (FunctionValue) args -> {
      assertUnaryFunction(args);
      var value = Values.asNumber(args.get(0));
      return new NumberValue(new BigDecimal(Math.sin(value.doubleValue())));
    });

    builtins.set("tan", (FunctionValue) args -> {
      assertUnaryFunction(args);
      var value = Values.asNumber(args.get(0));
      return new NumberValue(new BigDecimal(Math.tan(value.doubleValue())));
    });

    builtins.set("cos", (FunctionValue) args -> {
      assertUnaryFunction(args);
      var value = Values.asNumber(args.get(0));
      return new NumberValue(new BigDecimal(Math.cos(value.doubleValue())));
    });

    builtins.set("log", (FunctionValue) args -> {
      assertUnaryFunction(args);
      var value = Values.asNumber(args.get(0));
      return new NumberValue(new BigDecimal(Math.log(value.doubleValue())));
    });

    builtins.set("floor", (FunctionValue) args -> {
      assertUnaryFunction(args);
      var value = Values.asNumber(args.get(0));
      return new NumberValue(new BigDecimal(Math.floor(value.doubleValue())));
    });

    builtins.set("ceil", (FunctionValue) args -> {
      assertUnaryFunction(args);
      var value = Values.asNumber(args.get(0));
      return new NumberValue(new BigDecimal(Math.ceil(value.doubleValue())));
    });

    alias.put("mod", "remainder");
    alias.put("div", "divide");
    alias.put("sub", "subtract");
  }

}
