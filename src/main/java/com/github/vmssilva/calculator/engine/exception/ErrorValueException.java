package com.github.vmssilva.calculator.engine.exception;

public class ErrorValueException extends RuntimeException {
  public ErrorValueException(String message) {
    super("ErrorValue: " + message);
  }

  @Override
  public String toString() {
    return getMessage();
  }
}
