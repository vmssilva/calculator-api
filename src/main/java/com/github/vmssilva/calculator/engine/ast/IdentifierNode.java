package com.github.vmssilva.calculator.engine.ast;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.exception.CalculatorRuntimeException;
import com.github.vmssilva.calculator.engine.value.NumberValue;
import com.github.vmssilva.calculator.engine.value.Value;

public record IdentifierNode(String name) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {
    if (context.get(name) instanceof NumberValue v)
      return v;

    throw new CalculatorRuntimeException(name + " is not defined");
  }
}
