package com.github.vmssilva.calculator.api.lexer;

import java.util.List;

import com.github.vmssilva.calculator.api.token.Token;

public interface Lexer {
  List<Token> tokenize(String source);
}
