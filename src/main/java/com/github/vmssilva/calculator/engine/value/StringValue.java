package com.github.vmssilva.calculator.engine.value;

import com.github.vmssilva.calculator.engine.std.ValueType;

public record StringValue(String value) implements Value {

  @Override
  public ValueType type() {
    return ValueType.STRING;
  }
}
