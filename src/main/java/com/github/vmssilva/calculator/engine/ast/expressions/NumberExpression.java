package com.github.vmssilva.calculator.engine.ast.expressions;

import java.math.BigDecimal;

import com.github.vmssilva.calculator.engine.ast.Node;
import com.github.vmssilva.calculator.engine.context.ApplicationContext;

public record NumberExpression(BigDecimal value) implements Node {

  @Override
  public Object interpret(ApplicationContext context) {
    return value;
  }

}
