package com.github.vmssilva.calculator.engine.value;

public record ErrorValue(String message) implements Value {
  @Override
  public String toString() {
    return "Error: " + message;
  }
}
