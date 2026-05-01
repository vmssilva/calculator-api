package com.github.vmssilva.calculator.engine.context;

import com.github.vmssilva.calculator.engine.exception.CalculatorRuntimeException;
import com.github.vmssilva.calculator.engine.std.constants.Constants;
import com.github.vmssilva.calculator.engine.std.functions.BuiltinFunction;
import com.github.vmssilva.calculator.engine.value.FunctionValue;
import com.github.vmssilva.calculator.engine.value.Value;

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

  // -------------------------
  // Scope control (call frames)
  // -------------------------
  public void pushScope() {
    current = new Scope(current);
  }

  public void popScope() {
    if (current.getParent() != null) {
      current = current.getParent();
    }
  }

  // -------------------------
  // Variable operations
  // -------------------------
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

  // -------------------------
  // Builtins access
  // -------------------------
  public Scope getBuiltins() {
    return builtins;
  }

  // -------------------------
  // Functions helper
  // -------------------------
  public void addFunction(String name, FunctionValue fn) {
    current.set(name, fn);
  }

  // -------------------------
  // Current scope access (important for closures)
  // -------------------------
  public Scope currentScope() {
    return current;
  }

  // -------------------------
  // Builtins loader
  // -------------------------
  private void loadBuiltins(Scope builtins) {

    for (Constants constant : Constants.values()) {
      builtins.set(constant.key(), constant.value());
    }

    for (BuiltinFunction fn : BuiltinFunction.values()) {
      builtins.set(fn.key(), fn.create(this));
    }
  }
}

// package com.github.vmssilva.calculator.engine.context;
//
// import java.util.ArrayDeque;
// import java.util.Deque;
// import java.util.LinkedHashMap;
// import java.util.List;
// import java.util.Map;
//
// import com.github.vmssilva.calculator.engine.std.functions.BuiltinFunction;
// import com.github.vmssilva.calculator.engine.std.constants.Constants;
// import com.github.vmssilva.calculator.engine.value.FunctionValue;
// import com.github.vmssilva.calculator.engine.value.Value;
//
// public class ApplicationContext {
//
// private Deque<Scope> scopes = new ArrayDeque<>();
// private final Scope builtins;
//
// public ApplicationContext() {
// this.builtins = new Scope(null);
// loadBuiltin(builtins);
// var global = new Scope(builtins);
// this.scopes.push(new Scope(global));
// }
//
// public Scope getBuiltins() {
// return builtins;
// }
//
// public boolean remove(String name) {
// return peek().remove(name);
// }
//
// public void pushScope() {
// scopes.push(new Scope(scopes.peek()));
// }
//
// public void popScope() {
// if (scopes.size() > 1)
// this.scopes.pop();
// }
//
// public void set(String name, Value value) {
// scopes.peek().set(name, value);
// }
//
// public Value get(String name) {
// return scopes.peek().get(name);
// }
//
// public void addFunction(String name, FunctionValue fn) {
// peek().set(name, fn);
// }
//
// public Scope peek() {
// return scopes.peek();
// }
//
// public boolean has(String name) {
// if (peek().has(name))
// return true;
//
// if (peek().getParent() != null)
// return peek().getParent().has(name);
//
// return false;
// }
//
// public Map<String, Value> flatten() {
// Map<String, Value> result = new LinkedHashMap<>();
//
// Scope current = scopes.peek();
//
// while (current != null) {
// for (var entry : current.entries().entrySet()) {
// result.putIfAbsent(entry.getKey(), entry.getValue());
// }
// current = current.getParent();
// }
//
// return result;
// }
//
// private void loadBuiltin(Scope builtins) {
//
// for (Constants constant : Constants.values()) {
// builtins.set(constant.key(), constant.value());
// }
//
// for (BuiltinFunction fn : BuiltinFunction.values()) {
// builtins.set(fn.key(), fn.create(this));
// }
// }
//
// public ApplicationContext createChild() {
// var child = new ApplicationContext();
// Deque<Scope> copy = new ArrayDeque<>(List.copyOf(scopes));
// child.scopes = copy;
// return child;
// }
// }
