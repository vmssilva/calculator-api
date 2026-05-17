package com.github.vmssilva.calculator.engine.exception;

public class CalculatorLexerException extends RuntimeException {

  private int line;
  private int column;

  public CalculatorLexerException(String message) {
    super(message);
  }

  public CalculatorLexerException(String message, int line, int column) {
    this(message);

    this.line = line;
    this.column = column;
  }

  @Override
  public String getMessage() {
    return super.getMessage();
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }

  @Override
  public String toString() {
    return getMessage();
  }
}
