package com.github.vmssilva.calculator.engine.context;

import java.util.List;
import java.util.Map;

import com.github.vmssilva.calculator.engine.std.constants.Constants;
import com.github.vmssilva.calculator.engine.std.functions.Builtin;
import com.github.vmssilva.calculator.engine.std.functions.FunctionFactory;
import com.github.vmssilva.calculator.engine.std.functions.MathFunctions;
import com.github.vmssilva.calculator.engine.std.functions.PredicateFunctions;
import com.github.vmssilva.calculator.engine.std.functions.UtilFunctions;
import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
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
  public void set(String name, Value value) {
    current.set(name, value);
  }

  @Override
  public Value get(String name) {
    return current.get(name);
  }

  @Override
  public boolean has(String name) {
    return current.has(name);
  }

  @Override
  public boolean remove(String name) {
    return current.remove(name);
  }

  /**
   * Snapshot imutável da stack de escopos.
   * Evita vazamento de estrutura interna.
   */
  @Override
  public Scope snapshot() {
    return current;
  }

  public void addFunction(String name, FunctionValue fn) {
    current.set(name, fn);
  }

  // =========================
  // SAFE INTROSPECTION (IMPORTANT CHANGE)
  // =========================

  /**
   * Acesso somente leitura ao escopo de builtins.
   * NÃO expõe o scope completo da runtime.
   */
  public Scope builtins() {
    return builtins;
  }

  // =========================
  // INTERNAL STATE (NOT EXPOSED)
  // =========================

  public Scope currentScopeInternal() {
    return current;
  }

  public List<String> listVariables() {
    return current.entries().entrySet().stream()
        .filter(e -> !(e.getValue() instanceof FunctionValue))
        .map(Map.Entry::getKey)
        .toList();
  }

  public List<String> listFunctions() {
    return current.entries().entrySet().stream()
        .filter(e -> e.getValue() instanceof FunctionValue)
        .map(Map.Entry::getKey)
        .toList();
  }

  // Builtins loader
  private void loadBuiltins(Scope builtins) {

    for (Constants constant : Constants.values()) {
      builtins.set(constant.key(), constant.value());
    }

    scanBuiltins(builtins, MathFunctions.class);
    scanBuiltins(builtins, PredicateFunctions.class);
    scanBuiltins(builtins, UtilFunctions.class);

  }

  private void scanBuiltins(Scope builtins, Class<?> clazz) {

    for (var method : clazz.getDeclaredMethods()) {

      var builtin = method.getAnnotation(Builtin.class);

      if (builtin == null) {
        continue;
      }

      FunctionValue function = FunctionFactory.of(method);

      builtins.set(builtin.name(), function);
    }
  }

}
