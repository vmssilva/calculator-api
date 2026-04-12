package com.github.vmssilva.calculator.engine.ast.expressions;

import java.math.BigDecimal;

import com.github.vmssilva.calculator.engine.ast.Node;
import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.value.NumberValue;
import com.github.vmssilva.calculator.engine.value.Value;

public record NumberExpression(BigDecimal value) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {
    return new NumberValue(value);
  }
}
