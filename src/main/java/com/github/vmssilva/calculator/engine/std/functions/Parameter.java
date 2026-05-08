package com.github.vmssilva.calculator.engine.std.functions;

import com.github.vmssilva.calculator.engine.std.type.ValueType;

public record Parameter(
    String name,
    ValueType type,
    boolean varargs) {

  public Parameter(String name, ValueType type) {
    this(name, type, false);
  }
}
