package com.github.vmssilva.calculator.engine.value;

import com.github.vmssilva.calculator.engine.std.ValueType;

public abstract class BaseFunctionValue implements FunctionValue {

  private ValueType[] parameters;
  private boolean curried;

  public BaseFunctionValue(ValueType[] parameters, boolean curried) {
    this.parameters = parameters;
    this.curried = curried;
  }

  public BaseFunctionValue(ValueType[] parameters) {
    this(parameters, false);
  }

  @Override
  public ValueType[] parameters() {
    return parameters;
  }

  public boolean isCurried() {
    return curried;
  }

}
