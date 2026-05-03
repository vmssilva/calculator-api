package com.github.vmssilva.calculator.engine.exception;

public class ExecutionErrorException extends RuntimeException {
  public ExecutionErrorException(String message) {
    super("ExecutionError: " + message);
  }

  @Override
  public String toString() {
    return getMessage();
  }
}
