package com.github.vmssilva.calculator.engine.context;

import java.util.ArrayList;
import java.util.List;

import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.std.constants.Constants;
import com.github.vmssilva.calculator.engine.std.functions.Builtin;
import com.github.vmssilva.calculator.engine.std.functions.FunctionFactory;
import com.github.vmssilva.calculator.engine.std.functions.MathBuiltins;
import com.github.vmssilva.calculator.engine.std.functions.PredicateBuiltins;
import com.github.vmssilva.calculator.engine.std.functions.StringBuiltins;
import com.github.vmssilva.calculator.engine.std.functions.TypeBuiltins;
import com.github.vmssilva.calculator.engine.std.functions.UtilBuiltins;
import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.ListValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public class ApplicationContext implements ContextCapabilities {

  private final Scope builtins;
  private Scope current;

  public ApplicationContext() {
    this.builtins = new Scope(null);
    loadBuiltins(builtins);

    Scope global = new Scope(builtins);
    this.current = global;
  }

  // =========================
  // STACK CONTROL
  // =========================

  @Override
  public void pushScope() {
    current = new Scope(current);
  }

  @Override
  public void popScope() {
    if (current.getParent() != null) {
      current = current.getParent();
    }
  }

  // =========================
  // VARIABLES API (PUBLIC SAFE)
  // =========================
  @Override
  public Value resolve(String name) {
    // variável tem prioridade
    // Value variable = resolveVariable(name);

    if (current.hasVariable(name))
      return resolveVariable(name);

    List<FunctionValue> overloads = current.resolveFunctions(name);

    if (overloads == null || overloads.isEmpty()) {
      throw new ExecutionErrorException(
          "'" + name + "' is not defined");
    }

    // apenas uma função
    if (overloads.size() == 1) {
      return overloads.get(0);
    }

    // múltiplos overloads
    return new ListValue(new ArrayList<>(overloads));

  }

  public Value resolve(List<FunctionValue> overloads, Value... args) {
    return current.resolveFunction(overloads, args).call(this, args);
  }

  @Override
  public FunctionValue resolve(String name, int arity) {
    return current.resolveFunction(name, arity);
  }

  @Override
  public Value resolve(String name, Value... args) {
    return current.resolveFunction(name, args).call(this, args);
  }

  @Override
  public FunctionValue getFunction(String name, FunctionValue fn) {
    return current.getFunction(name, fn);
  }

  public void define(String name, Value value) {
    if (value instanceof FunctionValue fn) {
      defineFunction(name, fn);
      return;
    }
    defineVariable(name, value);
  }

  @Override
  public void defineFunction(String name, FunctionValue fn) {
    current.defineFunction(name, fn);
  }

  @Override
  public FunctionValue resolveFunction(String name, Value... args) {
    return current.resolveFunction(name, args);
  }

  @Override
  public void defineVariable(String name, Value value) {
    current.defineVariable(name, value);
  }

  @Override
  public Value resolveVariable(String name) {
    return current.resolveVariable(name);
  }

  public boolean hasVariable(String name) {
    return current.hasVariable(name);
  }

  public boolean hasFunction(String name) {
    return current.hasFunction(name);
  }

  public boolean removeVariable(String name) {
    return current.removeVariable(name);
  }

  public boolean removeFunction(String name, FunctionValue fn) {
    return current.removeFunction(name, fn);
  }

  @Override
  public Scope snapshot() {
    return current;
  }

  public Scope builtins() {
    return builtins;
  }

  // Builtins loader
  private void loadBuiltins(Scope builtins) {

    for (Constants constant : Constants.values()) {
      builtins.defineVariable(constant.key(), constant.value());
    }

    scanBuiltins(builtins, MathBuiltins.class);
    scanBuiltins(builtins, PredicateBuiltins.class);
    scanBuiltins(builtins, UtilBuiltins.class);
    scanBuiltins(builtins, StringBuiltins.class);
    scanBuiltins(builtins, TypeBuiltins.class);

  }

  private void scanBuiltins(Scope builtins, Class<?> clazz) {

    for (var method : clazz.getDeclaredMethods()) {

      var builtin = method.getAnnotation(Builtin.class);

      if (builtin == null) {
        continue;
      }

      FunctionValue function = FunctionFactory.of(method);

      builtins.defineFunction(builtin.name(), function);
    }
  }

}
