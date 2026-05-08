package com.github.vmssilva.calculator.engine.std.value;

import com.github.vmssilva.calculator.engine.std.type.ValueType;

public final class UnitValue implements Value {

  public static final UnitValue INSTANCE = new UnitValue();

  private UnitValue() {
  }

  @Override
  public String toString() {
    return "";
  }

  @Override
  public ValueType type() {
    return ValueType.UNIT;
  }

}
