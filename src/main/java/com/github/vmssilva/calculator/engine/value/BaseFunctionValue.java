package com.github.vmssilva.calculator.engine.value;

import com.github.vmssilva.calculator.engine.std.ValueType;

public abstract class BaseFunctionValue implements FunctionValue {

  private ValueType[] parameters;

  public BaseFunctionValue(ValueType[] parameters) {
    this.parameters = parameters;
  }

  @Override
  public ValueType[] parameters() {
    return parameters;
  }

}
