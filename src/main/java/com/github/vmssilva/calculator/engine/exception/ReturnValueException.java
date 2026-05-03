package com.github.vmssilva.calculator.engine.exception;

import com.github.vmssilva.calculator.engine.value.Value;

public class ReturnValueException extends RuntimeException {
  private final Value value;

  public ReturnValueException(Value value) {
    this.value = value;
  }

  public Value getValue() {
    return value;
  }
}
