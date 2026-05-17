package com.github.vmssilva.calculator.engine.lexer;

import java.util.ArrayList;
import java.util.List;

import com.github.vmssilva.calculator.engine.exception.CalculatorLexerException;
import com.github.vmssilva.calculator.engine.token.Token;
import com.github.vmssilva.calculator.engine.token.TokenType;

public final class SimpleLexer implements Lexer {

  private List<Token> tokens;
  private String expression;
  private int current = 0;
  private int line;

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

    if (c == '\n') {
      line++;
      advance();
    }

    if (Character.isWhitespace(c)) {
      advance();
      return;
    }

    if (String.valueOf(c).isBlank()) {
      advance();
      return;
    }

    if (c == '.') {
      addToken(TokenType.DOT, String.valueOf(c));
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
      if (!isAtEnd() && peekNext() == '>') {
        advance();
        advance();
        addToken(TokenType.ARROW, "->");
        return;
      }

      addToken(TokenType.MINUS, "-");
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

    if (c == '"') {
      handleString();
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

    invalidCharacterFound(c, line, current);

  }

  private void handleString() {
    advance();

    StringBuilder value = new StringBuilder();

    while (!isAtEnd() && peek() != '"') {

      if (peek() == '\\') {
        value.append(readEscape());
      } else {
        value.append(advance());
      }

    }

    if (isAtEnd())
      throw new CalculatorLexerException("Unbalanced string", line, current);

    advance();

    addToken(TokenType.STRING, value.toString());
  }

  private String readEscape() {

    // We are called right after detecting '\'
    advance(); // consume '\'

    if (isAtEnd()) {
      throw new CalculatorLexerException(
          "Unexpected EOF after escape character",
          line,
          current);
    }

    char escapedChar = advance(); // consume escaped character

    return switch (escapedChar) {

      case '\\' -> "\\";
      case '"' -> "\"";
      case 't' -> "\t";
      case 'r' -> "\r";
      case 'f' -> "\f";
      case 'b' -> "\b";
      case 'n' -> "\n";
      case '0' -> "\0";

      case 'u' -> {

        // At this point we are positioned at the first hex digit
        int hexStart = current;
        int hexEnd = current + 4;

        if (hexEnd > expression.length()) {
          throw new CalculatorLexerException(
              "Invalid unicode escape: unexpected EOF",
              line,
              current);
        }

        String hex = expression.substring(hexStart, hexEnd);

        int codePoint;
        try {
          codePoint = Integer.parseInt(hex, 16);
        } catch (NumberFormatException e) {
          throw new CalculatorLexerException(
              "Invalid unicode escape: \\u" + hex,
              line,
              current);
        }

        // Consume the 4 hex digits explicitly
        current += 4;

        yield String.valueOf((char) codePoint);
      }

      default -> throw new CalculatorLexerException(
          "Invalid escape sequence: \\" + escapedChar,
          line,
          current);
    };
  }

  private void handleDigit() {
    var value = new StringBuilder();

    while (isDigit(peek())) {
      value.append(peek());
      advance();
    }

    if (peek() == '.') {

      if (isAtEnd())
        invalidCharacterFound(peek(), line, current);

      if (!isDigit(peekNext()))
        invalidCharacterFound(peek(), line, current);

      value.append(peek());
      advance();

      while (isDigit(peek())) {
        value.append(peek());
        advance();
      }

      if (!isAtEnd() && peek() == '.')
        invalidCharacterFound(peek(), line, current);

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

  private void invalidCharacterFound(char c, int line, int col) throws CalculatorLexerException {
    throw new CalculatorLexerException(
        String.format(
            "Invalid character found '%s' at index [%s, %s]",
            c, line, col),
        line, col);
  }

}
