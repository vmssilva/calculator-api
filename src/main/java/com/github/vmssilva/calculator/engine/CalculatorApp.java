package com.github.vmssilva.calculator.engine;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.exception.CalculatorException;
import com.github.vmssilva.calculator.engine.runtime.CalculatorRuntime;

public class CalculatorApp {
  public static void main(String[] args) {

    var expression = "";

    if (args.length > 0) {
      expression = args[0];
    }

    var ctx = new ApplicationContext();
    var runtime = new CalculatorRuntime();

    try {
      var result = runtime.evaluate(expression, ctx);
      System.out.println(result);
    } catch (CalculatorException e) {
      System.out.println(e.getMessage());
    }
  }
}
