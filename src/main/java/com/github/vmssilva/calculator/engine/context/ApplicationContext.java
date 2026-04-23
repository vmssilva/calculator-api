package com.github.vmssilva.calculator.engine.context;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.vmssilva.calculator.engine.exception.CalculatorRuntimeException;
import com.github.vmssilva.calculator.engine.value.FunctionValue;
import com.github.vmssilva.calculator.engine.value.NumberValue;
import com.github.vmssilva.calculator.engine.value.Value;
import com.github.vmssilva.calculator.engine.value.Values;

public class ApplicationContext {

  private Deque<Scope> scopes = new ArrayDeque<>();
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
    scopes.peek().setVar(name, value);
  }

  public Value get(String name) {
    if (alias.containsKey(name))
      return scopes.peek().getVar(alias.get(name));

    return scopes.peek().getVar(name);
  }

  public Value getFunction(String name) {
    if (alias.containsKey(name)) {
      if (hasFunction(alias.get(name)))
        return peek().getFunction(alias.get(name));
    }

    return peek().getFunction(name);
  }

  public void addFunction(String name, FunctionValue fn) {
    peek().setFunction(name, fn);
  }

  private Scope peek() {
    return scopes.peek();
  }

  public boolean hasFunction(String name) {
    if (peek().hasFunction(name))
      return true;

    if (peek().parent != null)
      return peek().parent.hasFunction(name);

    return false;
  }

  public boolean has(String name) {
    if (peek().hasVar(name) || peek().hasFunction(name))
      return true;

    if (peek().parent != null)
      return peek().parent.hasVar(name) || peek().parent.hasFunction(name);

    return false;
  }

  private class Scope {
    private Scope parent;
    private Map<String, Value> vars;
    private Map<String, Value> functions;

    private Scope(Scope parent) {
      this.parent = parent;
      this.vars = new HashMap<>();
      this.functions = new HashMap<>();
    }

    public void setVar(String name, Value value) {
      vars.put(name, value);
    }

    public Value getVar(String name) {
      if (has(name, vars))
        return vars.get(name);

      if (parent != null)
        return parent.getVar(name);

      throw new CalculatorRuntimeException(name + " is not defined");
    }

    public void setFunction(String name, Value value) {
      functions.put(name, value);
    }

    public Value getFunction(String name) {
      if (has(name, functions))
        return functions.get(name);

      if (parent != null)
        return parent.getFunction(name);

      throw new CalculatorRuntimeException("Function " + name + " is not defined");
    }

    public boolean hasVar(String name) {
      return has(name, vars);
    }

    public boolean hasFunction(String name) {
      return has(name, functions);
    }

    public boolean has(String name, Map<String, Value> map) {
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
      throw new CalculatorRuntimeException("Missing operands");

    if (args.size() < count)
      throw new CalculatorRuntimeException("Missing right operand");

    if (args.size() > count)
      throw new CalculatorRuntimeException("Too much arguments");

  }

  private void loadBuiltin(Scope builtins) {
    builtins.setVar("PI", new NumberValue(new BigDecimal(Math.PI)));
    builtins.setVar("E", new NumberValue(new BigDecimal(Math.E)));

    builtins.setFunction("add", (FunctionValue) args -> {
      assertBinaryFunction(args);
      var left = Values.asNumber(args.get(0));
      var right = Values.asNumber(args.get(1));
      return new NumberValue(left.add(right));
    });

    builtins.setFunction("subtract", (FunctionValue) args -> {
      assertBinaryFunction(args);
      var left = Values.asNumber(args.get(0));
      var right = Values.asNumber(args.get(1));
      return new NumberValue(left.subtract(right));
    });

    builtins.setFunction("multiply", (FunctionValue) args -> {
      assertBinaryFunction(args);
      var left = Values.asNumber(args.get(0));
      var right = Values.asNumber(args.get(1));
      return new NumberValue(left.multiply(right));
    });

    builtins.setFunction("divide", (FunctionValue) args -> {
      assertBinaryFunction(args);
      var left = Values.asNumber(args.get(0));
      var right = Values.asNumber(args.get(1));
      return new NumberValue(left.divide(right, 10, RoundingMode.HALF_UP));
    });

    builtins.setFunction("remainder", (FunctionValue) args -> {
      assertBinaryFunction(args);
      var left = Values.asNumber(args.get(0));
      var right = Values.asNumber(args.get(1));
      return new NumberValue(left.remainder(right));
    });

    builtins.setFunction("abs", (FunctionValue) args -> {
      assertUnaryFunction(args);
      var value = Values.asNumber(args.get(0));
      return new NumberValue(value.abs());
    });

    builtins.setFunction("negate", (FunctionValue) args -> {
      assertUnaryFunction(args);
      var value = Values.asNumber(args.get(0));
      return new NumberValue(value.negate());
    });

    builtins.setFunction("pow", (FunctionValue) args -> {
      assertBinaryFunction(args);
      var left = Values.asNumber(args.get(0));
      var right = Values.asNumber(args.get(1));
      return new NumberValue(left.pow(right.intValue(), MathContext.DECIMAL64));
    });

    builtins.setFunction("sqrt", (FunctionValue) args -> {
      assertUnaryFunction(args);

      var value = Values.asNumber(args.get(0));
      return new NumberValue(new BigDecimal(Math.sqrt(value.doubleValue())));
    });

    builtins.setFunction("sin", (FunctionValue) args -> {
      assertUnaryFunction(args);
      var value = Values.asNumber(args.get(0));
      return new NumberValue(new BigDecimal(Math.sin(value.doubleValue())));
    });

    builtins.setFunction("tan", (FunctionValue) args -> {
      assertUnaryFunction(args);
      var value = Values.asNumber(args.get(0));
      return new NumberValue(new BigDecimal(Math.tan(value.doubleValue())));
    });

    builtins.setFunction("cos", (FunctionValue) args -> {
      assertUnaryFunction(args);
      var value = Values.asNumber(args.get(0));
      return new NumberValue(new BigDecimal(Math.cos(value.doubleValue())));
    });

    builtins.setFunction("log", (FunctionValue) args -> {
      assertUnaryFunction(args);
      var value = Values.asNumber(args.get(0));
      return new NumberValue(new BigDecimal(Math.log(value.doubleValue())));
    });

    builtins.setFunction("floor", (FunctionValue) args -> {
      assertUnaryFunction(args);
      var value = Values.asNumber(args.get(0));
      return new NumberValue(new BigDecimal(Math.floor(value.doubleValue())));
    });

    builtins.setFunction("ceil", (FunctionValue) args -> {
      assertUnaryFunction(args);
      var value = Values.asNumber(args.get(0));
      return new NumberValue(new BigDecimal(Math.ceil(value.doubleValue())));
    });

    builtins.setFunction("truncate", (FunctionValue) args -> {
      assertUnaryFunction(args);
      return new NumberValue(new BigDecimal(Values.asNumber(args.get(0)).intValue()));
    });

  }
}
