package com.github.vmssilva.calculator.api.lexer;

import java.util.ArrayList;
import java.util.List;

import com.github.vmssilva.calculator.api.token.Token;
import com.github.vmssilva.calculator.api.token.TokenType;

public final class SimpleLexer implements Lexer {

  private final List<Token> tokens;
  private String expression;
  private int current = 0;

  public SimpleLexer() {
    this.tokens = new ArrayList<>();
  }

  @Override
  public List<Token> tokenize(String source) {
    this.expression = source.replaceAll("\s+", "");
    this.current = 0;

    while (current < expression.length()) {
      char c = peek();
      scan(c);
    }

    return tokens;
  }

  private void scan(char c) {
    if (c == '(') {
      addToken(TokenType.LPAREN, String.valueOf(c), c);
      advance();
      return;
    }

    if (c == ')') {
      addToken(TokenType.RPAREN, String.valueOf(c), c);
      advance();
      return;
    }

    if (c == '+') {
      addToken(TokenType.PLUS, String.valueOf(c), c);
      advance();
      return;
    }

    if (c == '-') {
      addToken(TokenType.MINUS, String.valueOf(c), c);
      advance();
      return;
    }

    if (c == '*') {
      addToken(TokenType.STAR, String.valueOf(c), c);
      advance();
      return;
    }

    if (c == '/') {
      addToken(TokenType.SLASH, String.valueOf(c), c);
      advance();
      return;
    }

    if (c == '%') {
      addToken(TokenType.PERCENT, String.valueOf(c), c);
      advance();
      return;
    }

    if (isDigit(c)) {
      handleDigit();
      return;
    }

    throw new NumberFormatException("Invalid character '" + c + "' at index: " + current);

  }

  private void handleDigit() {
    var value = new StringBuilder();
    var isDouble = false;

    while (isDigit(peek())) {
      value.append(peek());
      advance();
    }

    if (peek() == '.') {

      if (!isDigit(peekNext()))
        throw new NumberFormatException("Invalid number format");

      isDouble = true;
      value.append(peek());
      advance();

      while (isDigit(peek())) {
        value.append(peek());
        advance();
      }

      if (isAtEnd() && !isDigit(peek()))
        throw new NumberFormatException("Invalid number format");

    }

    if (isDouble) {
      addToken(TokenType.NUMBER, value.toString(), Double.valueOf(value.toString()));
    } else {
      addToken(TokenType.NUMBER, value.toString(), Integer.valueOf(value.toString()));
    }
  }

  private void addToken(TokenType type, String value, Object literal) {
    tokens.add(new Token(type, value, literal));
  }

  private char peek() {
    return (!isAtEnd()) ? expression.charAt(current) : '\0';
  }

  private char peekNext() {
    return (current + 1 < expression.length()) ? expression.charAt(current + 1) : '\0';
  }

  private char advance() {
    return (!isAtEnd()) ? expression.charAt(current++) : '\0';
  }

  private boolean isDigit(char c) {
    return Character.isDigit(c);
  }

  private boolean isAtEnd() {
    return current >= expression.length();
  }
}
