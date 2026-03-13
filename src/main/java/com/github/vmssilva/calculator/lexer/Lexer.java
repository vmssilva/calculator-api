package com.github.vmssilva.calculator.lexer;

import java.util.List;

import com.github.vmssilva.calculator.token.Token;

public interface Lexer {
  List<Token> tokenize(String source);
}
