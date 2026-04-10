package com.github.vmssilva.calculator.engine.token;

public record Token(TokenType type, String value) {

  public static Token empty() {
    return new Token(null, null);
  }

}
