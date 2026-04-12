package com.github.vmssilva.calculator.engine.runtime;

import java.math.BigDecimal;
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
    return Values.asNumber(new RecursiveAstParser().parse(expression).interpret(context));
  }

  public BigDecimal evaluate(String expression) {
    return evaluate(expression, this.context);
  }

}
