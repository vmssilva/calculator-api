package com.github.vmssilva.calculator.engine;

import com.github.vmssilva.calculator.engine.ast.Expression;
import com.github.vmssilva.calculator.engine.lexer.Lexer;
import com.github.vmssilva.calculator.engine.lexer.SimpleLexer;
import com.github.vmssilva.calculator.engine.parser.Parser;
import com.github.vmssilva.calculator.engine.parser.RecursiveAstParser;

public class CalculatorApp {
  public static void main(String[] args) {

    Lexer lexer = new SimpleLexer();
    Parser parser = new RecursiveAstParser(lexer);
    Expression ast = parser.parse("1000*(1+0.20)");
    Double result = ast.interpret();
    System.out.println(result);

  }
}
