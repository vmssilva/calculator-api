package com.github.vmssilva.calculator.engine.std.value;

import java.util.Map;

import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.std.type.ValueType;

public final class ModuleValue implements Value<Map<String, Value>> {

  private final Map<String, Value> members;

  public ModuleValue(Map<String, Value> members) {
    this.members = members;
  }

  public Value get(String name) {
    Value v = members.get(name);

    if (v == null) {
      throw new ExecutionErrorException("No member: " + name);
    }

    return v;
  }

  @Override
  public Map<String, Value> unwrap() {
    return members;
  }

  @Override
  public ValueType type() {
    return ValueType.MODULE;
  }
}
