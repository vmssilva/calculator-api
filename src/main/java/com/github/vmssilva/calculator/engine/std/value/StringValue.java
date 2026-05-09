package com.github.vmssilva.calculator.engine.std.value;

import com.github.vmssilva.calculator.engine.std.type.ValueType;

public record StringValue(String value) implements Value<String> {

  @Override
  public ValueType type() {
    return ValueType.STRING;
  }

  @Override
  public String unwrap() {
    return value;
  }

  @Override
  public final String toString() {
    return value;
  }

}
