package com.github.vmssilva.calculator.engine.ast;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.std.value.DecimalValue;
import com.github.vmssilva.calculator.engine.std.value.Value;
import com.github.vmssilva.calculator.engine.std.value.Values;

public record UnaryNode(String operator, Node right) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {
    return switch (operator) {
      case "+" -> right.interpret(context);
      case "-" -> new DecimalValue(Values.asDecimal(right.interpret(context)).negate());
      default -> throw new ExecutionErrorException("Invalid unary operator: "
          + operator);
    };
  }

  @Override
  public final String toString() {
    return operator + right.toString();
  }

}
