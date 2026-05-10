package com.github.vmssilva.calculator.engine.context;

import com.github.vmssilva.calculator.engine.std.value.Value;

public interface ContextCapabilities {

  // leitura
  Value get(String name);

  boolean has(String name);

  // mutação controlada
  void set(String name, Value value);

  boolean remove(String name);

  // escopo
  void pushScope();

  void popScope();

  // introspecção segura
  Scope snapshot();
}
