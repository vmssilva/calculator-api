package com.github.vmssilva.calculator.engine.exception;

public class CalculatorRuntimeException extends RuntimeException {

  public CalculatorRuntimeException(String message) {
    super("RuntimeError: " + message);
  }

  @Override
  public String toString() {
    return getMessage();
  }

}
