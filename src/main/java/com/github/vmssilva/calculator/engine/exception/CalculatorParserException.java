package com.github.vmssilva.calculator.engine.exception;

public class CalculatorParserException extends CalculatorException {

  public CalculatorParserException(String message, int line, int column) {
    super(message, line, column);
  }

  public CalculatorParserException(String message) {
    super(message);
  }

}
