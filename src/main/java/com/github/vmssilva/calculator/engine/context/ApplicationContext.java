package com.github.vmssilva.calculator.engine.context;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.github.vmssilva.calculator.engine.ast.Function;

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

  public void set(String name, Object value) {
    scopes.peek().set(name, value);
  }

  public Object get(String name) {
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
    private Map<String, Object> map;

    private Scope(Scope parent) {
      this.parent = parent;
      this.map = new HashMap<>();
    }

    public Object get(String name) {
      if (has(name))
        return map.get(name);

      if (parent != null)
        return parent.get(name);

      throw new RuntimeException(name + " is not defined");
    }

    public void set(String name, Object value) {
      map.put(name, value);
    }

    public boolean has(String name) {
      if (map.containsKey(name))
        return true;

      return false;
    }

  }

  private void assertUnaryFunction(List<Object> args) {
    asserNumArgs(args, 1);
  }

  private void assertBinaryFunction(List<Object> args) {
    asserNumArgs(args, 2);
  }

  private void asserNumArgs(List<Object> args, int count) {
    if (args.isEmpty())
      throw new IllegalArgumentException("Missing operands");

    if (args.size() < count)
      throw new IllegalArgumentException("Missing right operand");

    if (args.size() > count)
      throw new IllegalArgumentException("Too much arguments");

  }

  private void loadBuiltin(Scope builtins) {
    builtins.set("PI", new BigDecimal(Math.PI));
    builtins.set("E", new BigDecimal(Math.E));

    builtins.set("add", (Function) args -> {
      assertBinaryFunction(args);
      return ((BigDecimal) args.get(0)).add((BigDecimal) args.get(1));
    });

    builtins.set("subtract", (Function) args -> {
      assertBinaryFunction(args);
      return ((BigDecimal) args.get(0)).subtract((BigDecimal) args.get(1));
    });

    builtins.set("multiply", (Function) args -> {
      assertBinaryFunction(args);
      return ((BigDecimal) args.get(0)).multiply((BigDecimal) args.get(1));
    });

    builtins.set("divide", (Function) args -> {
      assertBinaryFunction(args);
      return ((BigDecimal) args.get(0)).divide((BigDecimal) args.get(1), 10, RoundingMode.HALF_UP);
    });

    builtins.set("remainder", (Function) args -> {
      assertBinaryFunction(args);
      return ((BigDecimal) args.get(0)).remainder((BigDecimal) args.get(1));
    });

    builtins.set("abs", (Function) args -> {
      assertUnaryFunction(args);
      return ((BigDecimal) args.get(0)).abs();
    });

    builtins.set("negate", (Function) args -> {
      assertUnaryFunction(args);
      return ((BigDecimal) args.get(0)).negate();
    });

    builtins.set("pow", (Function) args -> {
      assertBinaryFunction(args);
      return ((BigDecimal) args.get(0)).pow(((BigDecimal) args.get(1)).intValue());
    });

    builtins.set("sqrt", (Function) args -> {
      assertUnaryFunction(args);
      return new BigDecimal(Math.sqrt(((BigDecimal) args.get(0)).doubleValue()));
    });

    builtins.set("sin", (Function) args -> {
      assertUnaryFunction(args);
      return new BigDecimal(Math.sin(((BigDecimal) args.get(0)).doubleValue()));
    });

    builtins.set("tan", (Function) args -> {
      assertUnaryFunction(args);
      return new BigDecimal(Math.tan(((BigDecimal) args.get(0)).doubleValue()));
    });

    builtins.set("cos", (Function) args -> {
      assertUnaryFunction(args);
      return new BigDecimal(Math.cos(((BigDecimal) args.get(0)).doubleValue()));
    });

    builtins.set("log", (Function) args -> {
      assertUnaryFunction(args);
      return new BigDecimal(Math.log(((BigDecimal) args.get(0)).doubleValue()));
    });

    builtins.set("floor", (Function) args -> {
      assertUnaryFunction(args);
      return new BigDecimal(Math.floor(((BigDecimal) args.get(0)).doubleValue()));
    });

    builtins.set("ceil", (Function) args -> {
      assertUnaryFunction(args);
      return new BigDecimal(Math.ceil(((BigDecimal) args.get(0)).doubleValue()));
    });

    alias.put("mod", "remainder");

  }

}
