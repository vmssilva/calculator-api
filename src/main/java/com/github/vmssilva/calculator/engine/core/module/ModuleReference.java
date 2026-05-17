package com.github.vmssilva.calculator.engine.core.module;

import com.github.vmssilva.calculator.engine.std.type.ValueType;
import com.github.vmssilva.calculator.engine.std.value.Value;

public record ModuleReference(
    String name,
    Class<?> clazz)
    implements Value {

  @Override
  public ValueType type() {
    return ValueType.ANY;
  }

  @Override
  public Object unwrap() {
    throw new UnsupportedOperationException("Unimplemented method 'unwrap'");
  }

}
