package com.github.vmssilva.calculator.engine.context;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.vmssilva.calculator.engine.exception.CalculatorRuntimeException;
import com.github.vmssilva.calculator.engine.std.functions.BuiltinFunction;
import com.github.vmssilva.calculator.engine.std.constants.Constants;
import com.github.vmssilva.calculator.engine.value.FunctionValue;
import com.github.vmssilva.calculator.engine.value.Value;

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

  public boolean remove(String name) {
    return peek().remove(name);
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

  private void loadBuiltin(Scope builtins) {

    for (Constants constant : Constants.values()) {
      builtins.set(constant.key(), constant.value());
    }

    for (BuiltinFunction fn : BuiltinFunction.values()) {
      builtins.set(fn.key(), fn.create(this));
    }

    // builtins.set("functions", fn(args -> {

    // if (!args.isEmpty()) {
    // throw new ErrorValueException("functions() takes no arguments");
    // }

    // System.out.println("Built-in functions:");

    // for (var entry : getBuiltins().entries().entrySet()) {
    // var name = entry.getKey();
    // var value = entry.getValue();

    // if (value instanceof BaseFunctionValue fn) {
    // System.out.println(name + " => " + fn);
    // }
    // }

    // return new NumberValue(BigDecimal.ZERO);

    // }, "functions()", false));
  }
}
