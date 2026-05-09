package com.github.vmssilva.calculator.engine.std.value;

import java.util.List;

import com.github.vmssilva.calculator.engine.std.type.ValueType;

public record ListValue(List<Value> values) implements Value<List<Value>> {

  @Override
  public ValueType type() {
    return ValueType.LIST;
  }

  @Override
  public List<Value> unwrap() {
    return values;
  }

  @Override
  public final String toString() {
    return values.toString();
  }

}
