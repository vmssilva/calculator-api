package com.github.vmssilva.calculator.engine.ast.expressions;

import java.math.BigDecimal;

import com.github.vmssilva.calculator.engine.ast.Node;
import com.github.vmssilva.calculator.engine.context.ApplicationContext;

public record UnaryExpression(String operator, Node right) implements Node {

  @Override
  public Object interpret(ApplicationContext context) {
    return switch (operator) {
      case "+" -> right.interpret(context);
      case "-" -> ((BigDecimal) right.interpret(context)).negate();
      default -> throw new UnsupportedOperationException("Invalid unary operator: "
          + operator);
    };
  }

}
