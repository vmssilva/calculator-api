package com.github.vmssilva.calculator.engine.runtime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.github.vmssilva.calculator.engine.ast.Node;
import com.github.vmssilva.calculator.engine.context.ApplicationContext;

@SuppressWarnings("unchecked")
public class CalculatorRuntime {

  private ApplicationContext context;

  public CalculatorRuntime(ApplicationContext context) {
    this.context = context;
  }

  public void run(Node calc) {

    var node = calc.interpret(context);

    if (node instanceof List) {
      ((List<Object>) node).forEach(this::print);
    } else {
      print(node);
    }
  }

  private void print(Object o) {

    if (o == null)
      return;

    if (o instanceof List) {
      ((List<Object>) o).forEach((e) -> {
        print(e);
      });
    }

    var result = (BigDecimal) o;
    var scale = (context.has("scale")) ? ((BigDecimal) context.get("scale")).intValue() : result.scale();
    System.out.println(result.setScale(scale, RoundingMode.HALF_UP));

  }
}
