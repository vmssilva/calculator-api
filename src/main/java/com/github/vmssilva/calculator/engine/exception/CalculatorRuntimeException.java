package com.github.vmssilva.calculator.engine.exception;

public class CalculatorRuntimeException extends RuntimeException {

  public CalculatorRuntimeException(String message) {
    super(message);
  }

  @Override
  public String getMessage() {
    return super.getMessage();
  }

  @Override
  public String toString() {
    return getMessage();
  }

}
