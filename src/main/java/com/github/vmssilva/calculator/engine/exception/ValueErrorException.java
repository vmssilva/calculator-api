package com.github.vmssilva.calculator.engine.exception;

public class ValueErrorException extends RuntimeException {
  public ValueErrorException(String message) {
    super("ValueError: " + message);
  }

  @Override
  public String toString() {
    return getMessage();
  }
}
