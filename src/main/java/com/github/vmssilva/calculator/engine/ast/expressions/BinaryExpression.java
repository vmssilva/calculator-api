package com.github.vmssilva.calculator.engine.ast.expressions;

import java.util.List;

import com.github.vmssilva.calculator.engine.ast.Node;
import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.value.FunctionValue;
import com.github.vmssilva.calculator.engine.value.Value;

public record BinaryExpression(Node left, Node right, String operator) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {
    return switch (operator) {
      case "+" ->
        ((FunctionValue) context.get("add")).apply(context, List.of(left.interpret(context), right.interpret(context)));
      case "-" ->
        ((FunctionValue) context.get("subtract"))
            .apply(context, List.of(left.interpret(context), right.interpret(context)));
      case "*" ->
        ((FunctionValue) context.get("multiply"))
            .apply(context, List.of(left.interpret(context), right.interpret(context)));
      case "/" ->
        ((FunctionValue) context.get("divide"))
            .apply(context, List.of(left.interpret(context), right.interpret(context)));
      case "%" ->
        ((FunctionValue) context.get("percentage"))
            .apply(context, List.of(left.interpret(context), right.interpret(context)));
      case "^" ->
        ((FunctionValue) context.get("pow")).apply(context, List.of(left.interpret(context), right.interpret(context)));
      default -> throw new ExecutionErrorException("Invalid operation: " + operator);
    };
  }

  @Override
  public final String toString() {
    return left.toString() + " " + operator + " " + right.toString();

  }
}
