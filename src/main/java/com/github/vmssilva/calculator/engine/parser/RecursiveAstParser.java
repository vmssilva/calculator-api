package com.github.vmssilva.calculator.engine.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.vmssilva.calculator.engine.ast.FunctionCallNode;
import com.github.vmssilva.calculator.engine.ast.IdentifierNode;
import com.github.vmssilva.calculator.engine.ast.LambdaNode;
import com.github.vmssilva.calculator.engine.ast.Node;
import com.github.vmssilva.calculator.engine.ast.ProgramNode;
import com.github.vmssilva.calculator.engine.ast.PropertyAccessorNode;
import com.github.vmssilva.calculator.engine.ast.StringNode;
import com.github.vmssilva.calculator.engine.ast.VarNode;
import com.github.vmssilva.calculator.engine.ast.BinaryNode;
import com.github.vmssilva.calculator.engine.ast.NumberNode;
import com.github.vmssilva.calculator.engine.ast.UnaryNode;
import com.github.vmssilva.calculator.engine.exception.CalculatorParserException;
import com.github.vmssilva.calculator.engine.lexer.Lexer;
import com.github.vmssilva.calculator.engine.lexer.SimpleLexer;
import com.github.vmssilva.calculator.engine.std.value.DoubleValue;
import com.github.vmssilva.calculator.engine.std.value.IntValue;
import com.github.vmssilva.calculator.engine.std.value.LongValue;
import com.github.vmssilva.calculator.engine.std.value.NumberValue;
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
    ArrayList<Node> nodes = new ArrayList<>();

    if (tokens.isEmpty())
      syntaxError(0, pos);

    while (!isAstEnd()) {
      var stmt = statement();
      nodes.add(stmt);
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

      if (isOperator() && peek().type() != TokenType.MINUS) {
        syntaxError(0, pos);
      }

      Node right = term();
      expr = new BinaryNode(expr, right, operator);

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

      expr = new BinaryNode(expr, right, operator);
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
      return new BinaryNode(left, right, operator.value());
    }

    return left;
  }

  private Node unary() {

    if (match(TokenType.PLUS, TokenType.MINUS)) {
      String op = advance().value();
      return new UnaryNode(op, unary());
    }

    return primary();
  }

  private Node primary() {

    // grouping OR lambda start
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
                throw new CalculatorParserException(
                    "Invalid lambda parameter",
                    line,
                    pos);
              }
              return id.name();
            })
            .toList();

        Node body = expression();

        return new LambdaNode(params, body);
      }

      // grouping
      if (parts.size() == 1) {
        return parts.get(0);
      }

      throw new CalculatorParserException(
          "Invalid grouped expression",
          line,
          pos);
    }

    // number
    if (match(TokenType.NUMBER)) {
      return new NumberNode(parseNumber(advance().value()));
    }

    // string
    if (match(TokenType.STRING)) {
      return new StringNode(advance().value());
    }

    // identifier
    if (match(TokenType.IDENTIFIER)) {
      return new IdentifierNode(advance().value());
    }

    throw new CalculatorParserException(
        "Unexpected token in primary",
        line,
        pos);
  }

  private Node factor() {

    Node node = unary();

    while (true) {

      // function call
      if (match(TokenType.LPAREN)) {
        node = parseCall(node);
        continue;
      }

      // property access
      if (match(TokenType.DOT)) {
        advance();

        if (!match(TokenType.IDENTIFIER)) {
          throw new CalculatorParserException("Expected identifier", line, pos);
        }

        String name = advance().value();
        node = new PropertyAccessorNode(node, new IdentifierNode(name));
        continue;
      }

      break;
    }

    return node;
  }

  private NumberValue parseNumber(String raw) {

    try {

      // decimal/scientific
      if (raw.contains(".") ||
          raw.contains("e") ||
          raw.contains("E")) {

        return new DoubleValue(
            Double.parseDouble(raw));
      }

      try {
        return new IntValue(
            Integer.parseInt(raw));
      } catch (NumberFormatException ignored) {
      }

      try {
        return new LongValue(
            Long.parseLong(raw));
      } catch (NumberFormatException ignored) {
      }

      // largest int
      return new DoubleValue(
          Double.parseDouble(raw));

    } catch (NumberFormatException ex) {

      throw new CalculatorParserException(
          "Invalid numeric literal: '" + raw + "'");
    }
  }

  private Node parseAssignment() {
    Node left = expression();

    if (match(TokenType.EQUAL)) {
      advance(); // consume '='

      Node right = parseAssignment(); // right-associative

      // case 1: Simple definition
      if (left instanceof IdentifierNode id) {
        return new VarNode(id.name(), right);
      }

      // case 2: function definition (syntactic sugar)
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

  private Node parseCall(Node callee) {

    while (true) {

      // =========================
      // MEMBER ACCESS
      // foo.bar
      // =========================
      if (match(TokenType.DOT)) {

        advance();

        expect(TokenType.IDENTIFIER);

        Node prop = new IdentifierNode(
            advance().value());

        // callee = new FunctionCallNode(
        // callee,
        // prop,
        // List.of());
        //
        callee = new PropertyAccessorNode(callee, prop);

        continue;
      }

      // =========================
      // FUNCTION CALL
      // foo(...)
      // foo.bar(...)
      // =========================
      if (match(TokenType.LPAREN)) {

        advance();

        List<Node> args = parseArguments();

        expect(TokenType.RPAREN);

        advance();

        if (callee instanceof PropertyAccessorNode accessor) {
          callee = new FunctionCallNode(accessor, args);
        } else {
          callee = new FunctionCallNode(callee, args);
        }

        continue;
      }

      break;
    }

    return callee;
  }

  private List<Node> parseArguments() {

    List<Node> args = new ArrayList<>();

    if (!match(TokenType.RPAREN)) {

      args.add(expression());

      while (match(TokenType.COMMA)) {
        advance();
        args.add(expression());
      }
    }

    return args;
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

  private boolean isAstEnd() {
    return pos >= tokens.size();
  }

  private void error(String message, int line, int col) {
    throw new CalculatorParserException(message, line, col);
  }

  private void syntaxError(int line, int col) {

    StringBuilder message = new StringBuilder("Invalid syntax");

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
