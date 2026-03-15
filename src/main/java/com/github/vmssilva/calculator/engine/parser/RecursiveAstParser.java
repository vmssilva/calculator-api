package com.github.vmssilva.calculator.engine.parser;

import java.util.List;

import com.github.vmssilva.calculator.engine.ast.BinaryExpression;
import com.github.vmssilva.calculator.engine.ast.Expression;
import com.github.vmssilva.calculator.engine.ast.NumberExpression;
import com.github.vmssilva.calculator.engine.ast.UnaryExpression;
import com.github.vmssilva.calculator.engine.lexer.Lexer;
import com.github.vmssilva.calculator.engine.lexer.SimpleLexer;
import com.github.vmssilva.calculator.engine.token.Token;
import com.github.vmssilva.calculator.engine.token.TokenType;

public final class RecursiveAstParser implements Parser {

  private final Lexer lexer;
  private List<Token> tokens;
  private int pos = 0;

  public RecursiveAstParser() {
    this.lexer = new SimpleLexer();
  }

  public RecursiveAstParser(Lexer lexer) {
    this.lexer = lexer;
  }

  public Expression parse(String expression) {
    this.tokens = lexer.tokenize(expression);
    return expression();
  }

  private Expression expression() {
    return expression(primary());
  }

  private Expression expression(Expression left) {

    Expression expr = left;

    while (match(TokenType.PLUS, TokenType.MINUS)) {

      String operator = advance().lexeme();

      if (!match(TokenType.NUMBER, TokenType.LPAREN)) {
        throw new UnsupportedOperationException("Malformed expression: '" + peek() + "' at index: " + pos);
      }

      Expression right = primary();

      expr = new BinaryExpression(expr, right, operator);

    }

    validateExpression();

    return expr;
  }

  private Expression primary() {
    Expression expr = factor();

    while (match(TokenType.STAR, TokenType.SLASH, TokenType.PERCENT)) {
      String operator = advance().lexeme();
      Expression right = factor();
      expr = new BinaryExpression(expr, right, operator);
    }

    return expr;
  }

  private Expression factor() {
    Expression expr = null;

    // Signed expressions
    if (match(TokenType.PLUS, TokenType.MINUS)) {
      String operator = advance().lexeme();
      Expression right = factor();
      expr = new UnaryExpression(operator, right);
    }

    if (match(TokenType.NUMBER)) {
      Token token = advance();
      Double value = Double.valueOf(token.lexeme());

      expr = new NumberExpression(value);

      if (match(TokenType.LPAREN)) {
        // advance LPAREN
        advance();

        Expression right = expression();
        String operator = "*";

        expr = new BinaryExpression(expr, right, operator);

        if (!match(TokenType.RPAREN))
          throw new UnsupportedOperationException("Malformed expression: '" + peek() + "' at index: " + pos);
        // advance RPAREN
        advance();

      }
    }

    if (match(TokenType.LPAREN)) {
      advance();

      if (isAstEnd())
        throw new UnsupportedOperationException("Malformed expression: '" + peek().lexeme() + "' at index: " + pos);

      expr = expression();

      if (!(match(TokenType.RPAREN)))
        throw new UnsupportedOperationException("Malformed expression: '" + peek().lexeme() + "' at index: " + pos);

      // Skipping token LPAREN
      advance();

    }

    if (expr == null)
      throw new UnsupportedOperationException("Malformed expression: '" + peek().lexeme() + "' at index: " + pos);

    return expr;
  }

  private boolean match(TokenType... types) {
    boolean found = false;

    for (TokenType type : types) {
      if (peek().type() == type) {
        found = true;
        break;
      }
    }

    return found;
  }

  private Token advance() {
    return (isAstEnd()) ? Token.empty() : tokens.get(pos++);
  }

  private Token peek() {
    return (isAstEnd()) ? Token.empty() : tokens.get(pos);
  }

  private boolean isAstEnd() {
    return pos >= tokens.size();
  }

  private void validateExpression() throws UnsupportedOperationException {
    var lparens = tokens.stream().filter((t) -> t.type() == TokenType.LPAREN).count();
    var rparens = tokens.stream().filter((t) -> t.type() == TokenType.RPAREN).count();

    if (lparens != rparens)
      throw new UnsupportedOperationException("Malformed expression Unbalanced paraentheses");
  }
}
