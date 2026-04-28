package com.github.vmssilva.calculator.engine.std;

public enum ValueType {
  NUMBER("Number"),
  FUNCTION("Function"),
  ANY("Any"),
  STRING("String"),
  LIST("List");

  private String value;

  ValueType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }
}
