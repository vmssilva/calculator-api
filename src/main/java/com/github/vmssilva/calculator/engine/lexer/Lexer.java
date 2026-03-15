package com.github.vmssilva.calculator.engine.lexer;

import java.util.List;

import com.github.vmssilva.calculator.engine.token.Token;

public interface Lexer {
  List<Token> tokenize(String source);
}
