package com.github.vmssilva.calculator.engine.context;

import com.github.vmssilva.calculator.engine.std.constants.Constants;
import com.github.vmssilva.calculator.engine.std.functions.Functions;
import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public class ApplicationContext {

  private final Scope builtins;
  private Scope current;

  public ApplicationContext() {
    this.builtins = new Scope(null);
    loadBuiltins(builtins);

    // global scope aponta para builtins
    Scope global = new Scope(builtins);

    // current começa no global
    this.current = global;
  }

  // Scope control (call frames)
  public void pushScope() {
    current = new Scope(current);
  }

  public void popScope() {
    if (current.getParent() != null) {
      current = current.getParent();
    }
  }

  // Variable operations
  public void set(String name, Value value) {
    current.set(name, value);
  }

  public Value get(String name) {
    return current.get(name);
  }

  public boolean has(String name) {
    return current.has(name);
  }

  public boolean remove(String name) {
    return current.remove(name);
  }

  // Builtins access
  public Scope getBuiltins() {
    return builtins;
  }

  // Functions helper
  public void addFunction(String name, FunctionValue fn) {
    current.set(name, fn);
  }

  // Current scope access (important for closures)
  public Scope currentScope() {
    return current;
  }

  // Builtins loader
  private void loadBuiltins(Scope builtins) {

    for (Constants constant : Constants.values()) {
      builtins.set(constant.key(), constant.value());
    }

    for (Functions fn : Functions.values()) {
      builtins.set(fn.key(), fn.create(this));
    }
  }
}
