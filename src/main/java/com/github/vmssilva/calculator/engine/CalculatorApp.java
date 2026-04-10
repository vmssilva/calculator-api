package com.github.vmssilva.calculator.engine;

import com.github.vmssilva.calculator.engine.ast.Node;
import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.lexer.Lexer;
import com.github.vmssilva.calculator.engine.lexer.SimpleLexer;
import com.github.vmssilva.calculator.engine.parser.Parser;
import com.github.vmssilva.calculator.engine.parser.RecursiveAstParser;
import com.github.vmssilva.calculator.engine.runtime.CalculatorRuntime;

public class CalculatorApp {
  public static void main(String[] args) {

    var expression = "1000*(1+0.20)";

    if (args.length > 0) {
      expression = args[0];
    }

    Lexer lexer = new SimpleLexer();
    Parser parser = new RecursiveAstParser(lexer);
    Node ast = parser.parse(expression);
    var ctx = new ApplicationContext();
    var runtime = new CalculatorRuntime(ctx);
    runtime.run(ast);

  }
}
