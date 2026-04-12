package com.github.vmssilva.calculator.engine;

import com.github.vmssilva.calculator.engine.ast.Node;
import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.parser.Parser;
import com.github.vmssilva.calculator.engine.parser.RecursiveAstParser;
import com.github.vmssilva.calculator.engine.runtime.CalculatorRuntime;

public class CalculatorApp {
  public static void main(String[] args) {

    var expression = "1000*(1+0.20)";

    if (args.length > 0) {
      expression = args[0];
    }

    Parser parser = new RecursiveAstParser();
    Node ast = parser.parse(expression);
    System.out.println(ast);
    var ctx = new ApplicationContext();
    var runtime = new CalculatorRuntime(ctx);

    var result = runtime.evaluate(expression, ctx);

    System.out.println(result);
  }
}
