package com.github.vmssilva.calculator.engine.std.type;

public enum ValueType {
  NUMBER("Number"),
  FUNCTION("Function"),
  ANY("Any"),
  STRING("String"),
  LIST("List"),
  UNIT("Unit");

  private String value;

  ValueType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }
}
