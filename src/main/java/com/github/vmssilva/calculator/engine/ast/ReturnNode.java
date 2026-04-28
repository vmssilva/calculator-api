package com.github.vmssilva.calculator.engine.ast;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.exception.ReturnException;
import com.github.vmssilva.calculator.engine.value.Value;

public record ReturnNode(Node expression) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {
    Value value = expression.interpret(context);
    throw new ReturnException(value);
  }
}
