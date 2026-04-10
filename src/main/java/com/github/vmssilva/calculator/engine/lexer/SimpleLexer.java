package com.github.vmssilva.calculator.engine.lexer;

import java.util.ArrayList;
import java.util.List;

import com.github.vmssilva.calculator.engine.token.Token;
import com.github.vmssilva.calculator.engine.token.TokenType;

public final class SimpleLexer implements Lexer {

  private List<Token> tokens;
  private String expression;
  private int current = 0;

  @Override
  public List<Token> tokenize(String source) {
    this.tokens = new ArrayList<>();
    this.expression = source;
    this.current = 0;

    while (current < expression.length()) {
      char c = peek();
      scan(c);
    }

    return tokens;
  }

  private void scan(char c) {

    if (String.valueOf(c).isBlank()) {
      advance();
      return;
    }

    if (c == '(') {
      addToken(TokenType.LPAREN, String.valueOf(c));
      advance();
      return;
    }

    if (c == ')') {
      addToken(TokenType.RPAREN, String.valueOf(c));
      advance();
      return;
    }

    if (c == '+') {
      addToken(TokenType.PLUS, String.valueOf(c));
      advance();
      return;
    }

    if (c == '-') {
      addToken(TokenType.MINUS, String.valueOf(c));
      advance();
      return;
    }

    if (c == '*') {
      addToken(TokenType.STAR, String.valueOf(c));
      advance();
      return;
    }

    if (c == '/') {
      addToken(TokenType.SLASH, String.valueOf(c));
      advance();
      return;
    }

    if (c == '%') {
      addToken(TokenType.PERCENT, String.valueOf(c));
      advance();
      return;
    }

    if (c == '=') {
      addToken(TokenType.EQUAL, String.valueOf(c));
      advance();
      return;
    }

    if (c == '^') {
      addToken(TokenType.CARET, String.valueOf(c));
      advance();
      return;
    }

    if (c == ',') {
      addToken(TokenType.COMMA, String.valueOf(c));
      advance();
      return;
    }

    if (c == ';') {
      addToken(TokenType.SEMICOLON, String.valueOf(c));
      advance();
      return;
    }

    if (isDigit(c)) {
      handleDigit();
      return;
    }

    if (isAlpha(c)) {
      handleAlpha();
      return;
    }

    throw new NumberFormatException("Invalid character '" + c + "' at index: " + current);

  }

  private void handleDigit() {
    var value = new StringBuilder();

    while (isDigit(peek())) {
      value.append(peek());
      advance();
    }

    if (peek() == '.') {

      if (!isDigit(peekNext()))
        throw new NumberFormatException("Invalid number format");

      value.append(peek());
      advance();

      while (isDigit(peek())) {
        value.append(peek());
        advance();
      }

      if (!isAtEnd() && peek() == '.')
        throw new NumberFormatException("Invalid number format");

    }

    addToken(TokenType.NUMBER, value.toString());
  }

  private void handleAlpha() {
    StringBuilder value = new StringBuilder();

    while (isAlpha(peek()) || isDigit(peek()) || peek() == '_') {
      value.append(advance());
    }

    addToken(TokenType.IDENTIFIER, value.toString());
  }

  private void addToken(TokenType type, String value) {
    tokens.add(new Token(type, value));
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

  private boolean isAlpha(char c) {
    return Character.isLetter(c);
  }

  private boolean isAtEnd() {
    return current >= expression.length();
  }
}
