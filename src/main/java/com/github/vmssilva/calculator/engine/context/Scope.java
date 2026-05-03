package com.github.vmssilva.calculator.engine.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.value.Value;

public class Scope {

  private Scope parent;
  private final Map<String, Value> values;

  public Scope(Scope parent) {
    this.parent = parent;
    this.values = new HashMap<>();
  }

  // Assignment (local scope)
  public void set(String name, Value value) {
    values.put(name, value);
  }

  // Lookup (lexical scope)
  public Value get(String name) {
    if (values.containsKey(name)) {
      return values.get(name);
    }

    if (parent != null) {
      return parent.get(name);
    }

    throw new ExecutionErrorException("Invalid indentifier: '" + name + "' is not defined");
  }

  // Existence check (lexical)
  public boolean has(String name) {
    if (values.containsKey(name)) {
      return true;
    }

    return parent != null && parent.has(name);
  }

  // Remove (lexical unset)
  public boolean remove(String name) {
    if (values.containsKey(name)) {
      values.remove(name);
      return true;
    }

    if (parent != null) {
      return parent.remove(name);
    }

    return false;
  }

  // Introspection (debug / flatten)
  public Map<String, Value> entries() {
    return Collections.unmodifiableMap(values);
  }

  // Parent access (closures)
  public Scope getParent() {
    return parent;
  }

  public void setParent(Scope parent) {
    this.parent = parent;
  }
}
