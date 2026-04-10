package com.github.vmssilva.calculator.engine.ast.expressions;

import java.util.List;

import com.github.vmssilva.calculator.engine.ast.Function;
import com.github.vmssilva.calculator.engine.ast.Node;
import com.github.vmssilva.calculator.engine.context.ApplicationContext;

public record BinaryExpression(Node left, Node right, String operator) implements Node {

  @Override
  public Object interpret(ApplicationContext context) {
    return switch (operator) {
      case "+" -> ((Function) context.get("add")).apply(List.of(left.interpret(context), right.interpret(context)));
      case "-" ->
        ((Function) context.get("subtract")).apply(List.of(left.interpret(context), right.interpret(context)));
      case "*" ->
        ((Function) context.get("multiply")).apply(List.of(left.interpret(context), right.interpret(context)));
      case "/" -> ((Function) context.get("divide")).apply(List.of(left.interpret(context), right.interpret(context)));
      case "%" ->
        ((Function) context.get("remainder")).apply(List.of(left.interpret(context), right.interpret(context)));
      case "^" -> ((Function) context.get("pow")).apply(List.of(left.interpret(context), right.interpret(context)));
      default -> throw new UnsupportedOperationException("Invalid operation: " + operator);
    };
  }
}
