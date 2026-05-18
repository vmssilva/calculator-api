package com.github.vmssilva.calculator.engine.ast;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.context.Dispatcher;
import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.std.value.DecimalValue;
import com.github.vmssilva.calculator.engine.std.value.DoubleValue;
import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.IntValue;
import com.github.vmssilva.calculator.engine.std.value.NumberValue;
import com.github.vmssilva.calculator.engine.std.value.StringValue;
import com.github.vmssilva.calculator.engine.std.value.Value;
import com.github.vmssilva.calculator.engine.std.value.Values;

public record BinaryNode(Node _left, Node _right, String operator) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {

    Value left = _left.interpret(context);
    Value right = _right.interpret(context);

    return switch (operator) {
      case "+" -> add(left, right);
      case "-" -> call(context, "subtract", left, right);
      case "*" -> call(context, "multiply", left, right);
      case "/" -> call(context, "divide", left, right);
      case "%" -> call(context, "percentage", left, right);
      case "^" -> call(context, "pow", left, right);
      default -> throw new ExecutionErrorException("Invalid operation: " + operator);
    };
  }

  private Value call(ApplicationContext context, String name, Value a, Value b) {

    Dispatcher dispatcher = new Dispatcher();
    return dispatcher.dispatch(context, name, new Value[] { a, b });
    // return context.resolve(name, new Value[] { a, b });
  }

  private Value add(Value left, Value right) {

    boolean leftIsString = left instanceof StringValue;
    boolean rightIsString = right instanceof StringValue;

    if (leftIsString || rightIsString) {

      if (left instanceof FunctionValue || right instanceof FunctionValue) {
        throw new ExecutionErrorException(
            "Cannot concatenate FUNCTION with STRING");
      }

      return new StringValue(
          left.unwrap().toString() + right.unwrap().toString());
    }

    if (NumberValue.class.isAssignableFrom(left.getClass()) &&
        NumberValue.class.isAssignableFrom(right.getClass())) {

      if (left instanceof DecimalValue l && right instanceof DecimalValue r) {
        return Values.of(l.unwrap().add(r.unwrap()));
      }

      if (left instanceof DoubleValue a && right instanceof DoubleValue b) {
        return Values.of(a.unwrap() + b.unwrap());
      }

      if (left instanceof IntValue a && right instanceof IntValue b) {
        return Values.of(a.unwrap() + b.unwrap());
      }
    }

    throw new ExecutionErrorException(
        "Invalid '+' operation: " + left + " + " + right);
  }

  @Override
  public final String toString() {
    return _left.toString() + " " + operator + " " + _right.toString();

  }

}
