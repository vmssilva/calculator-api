package com.github.vmssilva.calculator.engine.core.module;

import java.util.HashMap;
import java.util.Map;

import com.github.vmssilva.calculator.engine.std.value.ModuleValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public final class ModuleBuilder {

  private final Map<String, Value> members = new HashMap<>();

  public ModuleBuilder add(String name, Value value) {
    members.put(name, value);
    return this;
  }

  public ModuleValue build() {
    return new ModuleValue(members);
  }
}
