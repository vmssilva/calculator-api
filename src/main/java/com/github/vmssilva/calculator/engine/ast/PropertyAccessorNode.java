package com.github.vmssilva.calculator.engine.ast;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.std.value.Value;

public record PropertyAccessorNode(Node target, Node property) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {
    if (target instanceof IdentifierNode mod &&
        property instanceof IdentifierNode prop)

      return context.resolve(mod.name(), prop.name());

    throw new ExecutionErrorException("Module not found ");
  }
}
