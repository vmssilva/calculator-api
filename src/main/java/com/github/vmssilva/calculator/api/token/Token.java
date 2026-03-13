package com.github.vmssilva.calculator.api.token;

public record Token(TokenType type, String lexeme, Object literal) {

  public static Token empty() {
    return new Token(null, null, null);
  }

}
