package com.github.vmssilva.calculator.engine.runtime;

import java.math.RoundingMode;
import java.util.Objects;

import com.github.vmssilva.calculator.engine.ast.Node;
import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.exception.CalculatorException;
import com.github.vmssilva.calculator.engine.parser.RecursiveAstParser;
import com.github.vmssilva.calculator.engine.value.NumberValue;
import com.github.vmssilva.calculator.engine.value.Value;
import com.github.vmssilva.calculator.engine.value.Values;

public class CalculatorRuntime {

  private final ApplicationContext context;

  public CalculatorRuntime(ApplicationContext context) {
    this.context = Objects.requireNonNull(context);
  }

  public CalculatorRuntime() {
    this(new ApplicationContext());
  }

  public void run(Node ast) {
    var node = ast.interpret(context);
    System.out.println(Values.asNumber(node));
  }

  public Value evaluate(String expression, ApplicationContext context) {
    try {
      var result = new RecursiveAstParser()
          .parse(expression)
          .interpret(context);

      if (result instanceof NumberValue && context.has("scale")) {
        int scale = Values.asNumber(context.get("scale")).intValue();
        result = new NumberValue(Values.asNumber(result).setScale(scale, RoundingMode.HALF_UP));
      }

      return result;

    } catch (CalculatorException e) {
      throw e;
    }
  }

  public Value evaluate(String expression) {
    return evaluate(expression, this.context);
  }

}
