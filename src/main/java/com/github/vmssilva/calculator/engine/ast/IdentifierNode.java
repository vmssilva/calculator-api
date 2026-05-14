package com.github.vmssilva.calculator.engine.ast;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.std.value.Value;

public record IdentifierNode(String name) implements ReferenceNode {

  @Override
  public Value interpret(ApplicationContext context) {

    Value value = context.resolve(name);

    if (value == null)
      throw new ExecutionErrorException(name + " is not defined");

    return value;
  }

  @Override
  public final String toString() {
    return name;
  }
}
