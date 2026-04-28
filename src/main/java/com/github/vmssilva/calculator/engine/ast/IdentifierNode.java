package com.github.vmssilva.calculator.engine.ast;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.exception.CalculatorRuntimeException;
import com.github.vmssilva.calculator.engine.value.Value;

public record IdentifierNode(String name) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {
    Value value = context.get(name);

    if (value == null)
      throw new CalculatorRuntimeException(name + " is not defined");

    return value;
  }

  @Override
  public final String toString() {
    return name;
  }
}
