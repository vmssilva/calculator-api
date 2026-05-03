package com.github.vmssilva.calculator.engine.exception;

public class CalculatorParserException extends RuntimeException {

  private int line;
  private int column;

  public CalculatorParserException(String message, int line, int column) {
    this(message);
    this.line = line;
    this.column = column;
  }

  public CalculatorParserException(String message) {
    super("ParserError: " + message);
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
