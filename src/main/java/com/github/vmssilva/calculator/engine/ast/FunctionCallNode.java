package com.github.vmssilva.calculator.engine.ast;

import java.math.BigDecimal;
import java.util.List;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.exception.CalculatorRuntimeException;
import com.github.vmssilva.calculator.engine.value.FunctionValue;
import com.github.vmssilva.calculator.engine.value.NumberValue;
import com.github.vmssilva.calculator.engine.value.Value;
import com.github.vmssilva.calculator.engine.value.Values;

public record FunctionCallNode(String name, List<Node> args) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {
    var fn = (FunctionValue) context.getFunction(name);
    var evaluated = args.stream().map((e) -> e.interpret(context)).toList();

    try {
      return new NumberValue(Values.asNumber(fn.apply(evaluated)));
    } catch (CalculatorRuntimeException e) {

      List<BigDecimal> arguments = evaluated.stream().map(Values::asNumber).toList();

      throw new CalculatorRuntimeException(
          "Execution error: Failed to evaluate function '" + name + "' with args " + arguments + "' Reason: "
              + e.getMessage().toLowerCase());

      // throw new CalculatorRuntimeException(
      // "Invalid call to function '" + name + "' with args " + arguments + "'" +
      // e.getMessage().toLowerCase());
    }
  }
}
