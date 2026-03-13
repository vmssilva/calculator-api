package com.github.vmssilva.calculator;

import com.github.vmssilva.calculator.ast.Expression;
import com.github.vmssilva.calculator.lexer.Lexer;
import com.github.vmssilva.calculator.lexer.SimpleLexer;
import com.github.vmssilva.calculator.parser.Parser;
import com.github.vmssilva.calculator.parser.RecursiveParser;

public class CalculatorApp {
  public static void main(String[] args) {

    Lexer lexer = new SimpleLexer();
    Parser parser = new RecursiveParser(lexer);
    Expression ast = parser.parse("1000*(1+0.20)");
    Double result = ast.interpret();

    System.out.println(result);

  }
}
