package com.github.vmssilva.calculator.engine.ast.expressions;

import java.math.BigDecimal;

import com.github.vmssilva.calculator.engine.ast.Node;
import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.value.NumberValue;
import com.github.vmssilva.calculator.engine.value.Value;
import com.github.vmssilva.calculator.engine.value.Values;

public record UnaryExpression(String operator, Node right) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {
    return switch (operator) {
      case "+" -> right.interpret(context);
      case "-" -> new NumberValue(Values.asNumber(right.interpret(context)).negate());
      default -> throw new UnsupportedOperationException("Invalid unary operator: "
          + operator);
    };
  }

}
