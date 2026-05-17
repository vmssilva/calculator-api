package com.github.vmssilva.calculator.engine.exception;

public class ValueErrorException extends RuntimeException {
  public ValueErrorException(String message) {
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
