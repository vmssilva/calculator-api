package com.github.vmssilva.calculator.api;

import com.github.vmssilva.calculator.api.ast.Expression;
import com.github.vmssilva.calculator.api.lexer.Lexer;
import com.github.vmssilva.calculator.api.lexer.SimpleLexer;
import com.github.vmssilva.calculator.api.parser.Parser;
import com.github.vmssilva.calculator.api.parser.RecursiveParser;

public class CalculatorApp {
  public static void main(String[] args) {

    Lexer lexer = new SimpleLexer();
    Parser parser = new RecursiveParser(lexer);
    Expression ast = parser.parse("1000*(1+0.20)");
    Double result = ast.interpret();

    System.out.println(result);

  }
}
