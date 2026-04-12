package com.github.vmssilva.calculator.engine.runtime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import com.github.vmssilva.calculator.engine.ast.Node;
import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.parser.RecursiveAstParser;
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

  public BigDecimal evaluate(String expression, ApplicationContext context) {
    BigDecimal result = Values.asNumber(new RecursiveAstParser().parse(expression).interpret(context));
    int scale = result.scale();

    if (context.has("scale"))
      scale = Values.asNumber(context.get("scale")).intValue();

    return result.setScale(scale, RoundingMode.HALF_UP);
  }

  public BigDecimal evaluate(String expression) {
    return evaluate(expression, this.context);
  }

}
