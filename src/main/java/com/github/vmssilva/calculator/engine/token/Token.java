package com.github.vmssilva.calculator.engine.token;

public record Token(TokenType type, String lexeme, Object literal) {

  public static Token empty() {
    return new Token(null, null, null);
  }

}
