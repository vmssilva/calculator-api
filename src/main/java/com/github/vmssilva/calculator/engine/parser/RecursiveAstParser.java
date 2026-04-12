package com.github.vmssilva.calculator.engine.parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.github.vmssilva.calculator.engine.ast.FunctionCallNode;
import com.github.vmssilva.calculator.engine.ast.IdentifierNode;
import com.github.vmssilva.calculator.engine.ast.Node;
import com.github.vmssilva.calculator.engine.ast.ProgramNode;
import com.github.vmssilva.calculator.engine.ast.VarNode;
import com.github.vmssilva.calculator.engine.ast.expressions.BinaryExpression;
import com.github.vmssilva.calculator.engine.ast.expressions.NumberExpression;
import com.github.vmssilva.calculator.engine.ast.expressions.UnaryExpression;
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

  public Node parse(String expression) {
    this.tokens = lexer.tokenize(expression);
    this.pos = 0;

    return parseProgram();
  }

  private Node parseProgram() {
    List<Node> nodes = new ArrayList<>();

    if (tokens.isEmpty())
      error("Malformed expression", pos);

    while (!isAstEnd()) {
      nodes.add(statement());
    }

    return new ProgramNode(nodes);
  }

  private Node statement() {
    if (isAssignment()) {
      var declaration = parseDeclaration();

      if (!isAstEnd()) {
        expect(TokenType.SEMICOLON);
        advance();
      }
      return declaration;
    }

    return parseExpression();
  }

  private Node parseExpression() {
    var expression = expression();

    if (!isAstEnd()) {
      expect(TokenType.SEMICOLON);
      advance();
    }

    return expression;
  }

  private Node expression() {

    Node expr = term();

    while (match(TokenType.PLUS, TokenType.MINUS)) {

      String operator = advance().value();

      if (match(operators()))
        error("Malformed expression", pos);

      Node right = term();
      expr = new BinaryExpression(expr, right, operator);

    }

    return expr;
  }

  private Node term() {

    Node expr = power();

    while (match(TokenType.STAR, TokenType.SLASH, TokenType.PERCENT, TokenType.CARET)) {
      var operator = advance().value();

      if (match(operators()))
        error("Malformed expression", pos);

      Node right = factor();

      expr = new BinaryExpression(expr, right, operator);
    }

    return expr;
  }

  private Node power() {

    Node left = factor();

    while (match(TokenType.CARET)) {
      var operator = advance();
      Node right = power();
      return new BinaryExpression(left, right, operator.value());
    }

    return left;
  }

  private Node factor() {
    Node expr = null;

    // Signed expressions
    if (match(TokenType.PLUS, TokenType.MINUS)) {
      String operator = advance().value();
      Node right = factor();
      expr = new UnaryExpression(operator, right);

      return expr;
    }

    if (match(TokenType.IDENTIFIER)) {

      if (isFunction())
        return parseCallFunction();

      if (isAssignment())
        return parseDeclaration();

      return parseIdentifier();
    }

    if (match(TokenType.NUMBER)) {
      Token token = advance();
      var value = new BigDecimal(token.value());
      expr = new NumberExpression(value);

      // if (match(TokenType.LPAREN)) {
      // // advance LPAREN
      // advance();

      // Node right = expression();
      // String operator = "*";

      // expr = new BinaryExpression(expr, right, operator);

      // if (!match(TokenType.RPAREN))
      // error("Malformad expression", pos);
      // // advance RPAREN
      // advance();
      // }

      // return expr;
    }

    if (match(TokenType.LPAREN)) {
      advance();

      if (isAstEnd())
        error("Malformad expression", pos);

      expr = expression();

      if (!(match(TokenType.RPAREN)))
        error("Malformad expression", pos);

      // Skipping token LPAREN
      advance();

    }

    if (expr == null)
      error("Malformad expression", pos);

    return expr;
  }

  // Helpers
  private Node parseIdentifier() {
    return new IdentifierNode(advance().value());
  }

  private Node parseDeclaration() {
    var identifier = advance();
    advance();
    var expression = expression();

    return new VarNode(identifier.value(), expression);
  }

  private Node parseCallFunction() {
    List<Node> args = new ArrayList<>();

    var identifier = advance();

    expect(TokenType.LPAREN);
    advance(); // LPAREN

    if (match(TokenType.RPAREN)) {
      advance();
      return new FunctionCallNode(identifier.value(), List.of());
    }

    args.add(expression());

    while (match(TokenType.COMMA)) {
      advance();
      args.add(expression());
    }

    expect(TokenType.RPAREN);
    advance();

    return new FunctionCallNode(identifier.value(), args);
  }

  private boolean isAssignment() {
    return peek().type() == TokenType.IDENTIFIER && peekNext().type() == TokenType.EQUAL;
  }

  private boolean isFunction() {
    return peek().type() == TokenType.IDENTIFIER && peekNext().type() == TokenType.LPAREN;
  }

  private TokenType[] operators() {
    return new TokenType[] { TokenType.PLUS, TokenType.MINUS, TokenType.STAR, TokenType.SLASH, TokenType.PERCENT,
        TokenType.CARET };
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

  private void error(String message, int pos) {
    throw new UnsupportedOperationException(String.format("%s: at index %s", message, pos));
  }

  private Token advance() {
    return (isAstEnd()) ? Token.empty() : tokens.get(pos++);
  }

  private Token peek(int offset) {
    if (pos + offset >= tokens.size())
      return Token.empty();

    return tokens.get(pos + offset);
  }

  private Token peek() {
    return peek(0);
  }

  private Token peekNext() {
    return peek(1);
  }

  private boolean isAstEnd() {
    return pos >= tokens.size();
  }

  private void expect(TokenType... types) throws UnsupportedOperationException {
    if (!match(types)) {
      error("Malformad expression", pos);
    }
  }

}
