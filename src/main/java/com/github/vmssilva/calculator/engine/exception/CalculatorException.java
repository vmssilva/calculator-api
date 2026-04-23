package com.github.vmssilva.calculator.engine.exception;

public class CalculatorException extends RuntimeException {

  private int line;
  private int column;

  public CalculatorException(String message) {
    super(message);
  }

  public CalculatorException(String message, int line, int column) {
    super(message);
    this.line = line;
    this.column = column;
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }
}
