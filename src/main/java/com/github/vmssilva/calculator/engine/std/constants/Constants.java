package com.github.vmssilva.calculator.engine.std.constants;

import com.github.vmssilva.calculator.engine.value.Value;
import com.github.vmssilva.calculator.engine.value.Values;

public enum Constants {

  PI("PI", Values.of(Math.PI)),
  E("E", Values.of(Math.E));

  private String key;
  private Value value;

  Constants(String key, Value value) {
    this.key = key;
    this.value = value;
  }

  public String key() {
    return this.key;
  }

  public Value value() {
    return this.value;
  }

}
