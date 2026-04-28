package com.github.vmssilva.calculator.engine.parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.vmssilva.calculator.engine.ast.FunctionCallNode;
import com.github.vmssilva.calculator.engine.ast.IdentifierNode;
import com.github.vmssilva.calculator.engine.ast.LambdaNode;
import com.github.vmssilva.calculator.engine.ast.Node;
import com.github.vmssilva.calculator.engine.ast.ProgramNode;
import com.github.vmssilva.calculator.engine.ast.VarNode;
import com.github.vmssilva.calculator.engine.ast.expressions.BinaryExpression;
import com.github.vmssilva.calculator.engine.ast.expressions.NumberExpression;
import com.github.vmssilva.calculator.engine.ast.expressions.UnaryExpression;
import com.github.vmssilva.calculator.engine.exception.CalculatorParserException;
import com.github.vmssilva.calculator.engine.lexer.Lexer;
import com.github.vmssilva.calculator.engine.lexer.SimpleLexer;
import com.github.vmssilva.calculator.engine.token.Token;
import com.github.vmssilva.calculator.engine.token.TokenType;

public final class RecursiveAstParser implements Parser {

  private final Lexer lexer;
  private List<Token> tokens;
  private int line = 0;
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
      syntaxError(0, pos);

    while (!isAstEnd()) {
      nodes.add(statement());
    }

    return new ProgramNode(nodes);
  }

  private Node statement() {
    Node node = parseAssignment();

    if (!isAstEnd()) {
      expect(TokenType.SEMICOLON);
      advance();
    }

    return node;
  }

  private Node expression() {

    Node expr = term();

    while (match(TokenType.PLUS, TokenType.MINUS)) {

      String operator = advance().value();

      if (isOperator())
        syntaxError(0, pos);

      Node right = term();
      expr = new BinaryExpression(expr, right, operator);

    }

    return expr;
  }

  private Node term() {

    Node expr = power();

    while (match(TokenType.STAR, TokenType.SLASH, TokenType.PERCENT, TokenType.CARET)) {
      var operator = advance().value();

      if (isOperator())
        syntaxError(0, pos);

      Node right = factor();

      expr = new BinaryExpression(expr, right, operator);
    }

    return expr;
  }

  private Node power() {

    Node left = factor();

    while (match(TokenType.CARET)) {
      var operator = advance();

      if (isOperator())
        syntaxError(0, pos);

      Node right = power();
      return new BinaryExpression(left, right, operator.value());
    }

    return left;
  }

  private Node factor() {

    // 1. unary
    if (match(TokenType.PLUS, TokenType.MINUS)) {
      String op = advance().value();
      return new UnaryExpression(op, factor());
    }

    // 2. grouping OR lambda
    if (match(TokenType.LPAREN)) {
      advance();

      List<Node> parts = new ArrayList<>();

      if (!match(TokenType.RPAREN)) {
        parts.add(expression());

        while (match(TokenType.COMMA)) {
          advance();
          parts.add(expression());
        }
      }

      expect(TokenType.RPAREN);
      advance();

      // lambda
      if (match(TokenType.ARROW)) {
        advance();

        List<String> params = parts.stream()
            .map(n -> {
              if (!(n instanceof IdentifierNode id)) {
                throw new CalculatorParserException("Invalid lambda parameter", line, pos);
              }
              return id.name();
            })
            .toList();

        return new LambdaNode(params, expression());
      }

      // grouping
      if (parts.size() == 1) {
        return parts.get(0);
      }

      throw new CalculatorParserException("Invalid grouped expression", line, pos);
    }

    // 3. function call
    if (match(TokenType.IDENTIFIER) && peekNext().type() == TokenType.LPAREN) {
      String name = advance().value();

      advance(); // (

      List<Node> args = new ArrayList<>();

      if (!match(TokenType.RPAREN)) {
        args.add(expression());

        while (match(TokenType.COMMA)) {
          advance();
          args.add(expression());
        }
      }

      expect(TokenType.RPAREN);
      advance();

      return new FunctionCallNode(new IdentifierNode(name), args);
    }

    // 4. identifier
    if (match(TokenType.IDENTIFIER)) {
      return new IdentifierNode(advance().value());
    }

    // 5. number
    if (match(TokenType.NUMBER)) {
      return new NumberExpression(new BigDecimal(advance().value()));
    }

    throw new CalculatorParserException("Unexpected token in factor", line, pos);
  }

  private Node parseAssignment() {
    Node left = expression();

    if (match(TokenType.EQUAL)) {
      advance(); // consume '='

      Node right = parseAssignment(); // right-associative

      // caso 1: variável simples
      if (left instanceof IdentifierNode id) {
        return new VarNode(id.name(), right);
      }

      // caso 2: definição de função (syntactic sugar)
      if (left instanceof FunctionCallNode call &&
          call.target() instanceof IdentifierNode fn &&
          call.args().stream().allMatch(arg -> arg instanceof IdentifierNode)) {

        List<String> params = call.args().stream()
            .map(arg -> ((IdentifierNode) arg).name())
            .toList();

        return new VarNode(
            fn.name(),
            new LambdaNode(params, right));
      }

      throw new CalculatorParserException("Invalid assignment target", line, pos);
    }

    return left;
  }

  private boolean isOperator() {
    return match(operators());
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

  private void error(String message, int line, int col) {
    throw new CalculatorParserException(message, line, col);
  }

  private void syntaxError(int line, int col) {

    StringBuilder message = new StringBuilder("Syntax error: invalid syntax");

    if (!tokens.isEmpty()) {
      StringBuilder sb = new StringBuilder(
          tokens.stream().map(token -> token.value())
              .collect(Collectors.joining("")));

      sb.insert(pos, "^");
      message.append(" near '").append(sb).append("'");
      message.append(" at ").append("index [").append(line).append(", ").append(col).append("]");

    } else {
      message.append(" expression can't be empty");
    }

    error(message.toString(), line, col);

  }

  private void expect(TokenType... types) throws CalculatorParserException {
    if (!match(types)) {
      syntaxError(0, pos);
    }
  }

}
