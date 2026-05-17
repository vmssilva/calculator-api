package com.github.vmssilva.calculator.engine.exception;

public class ExecutionErrorException extends RuntimeException {
  public ExecutionErrorException(String message) {
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
