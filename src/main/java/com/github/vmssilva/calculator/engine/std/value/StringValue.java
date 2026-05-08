package com.github.vmssilva.calculator.engine.std.value;

import com.github.vmssilva.calculator.engine.std.type.ValueType;

public record StringValue(String value) implements Value {

  @Override
  public ValueType type() {
    return ValueType.STRING;
  }
}
