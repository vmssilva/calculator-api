package com.github.vmssilva.calculator.engine.exception;

public class CalculatorLexerException extends CalculatorException {

  public CalculatorLexerException(String message) {
    super(message);
  }

  public CalculatorLexerException(String message, int line, int column) {
    super(message, line, column);
  }

}
