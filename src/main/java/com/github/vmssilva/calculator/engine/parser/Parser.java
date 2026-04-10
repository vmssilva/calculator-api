package com.github.vmssilva.calculator.engine.parser;

import com.github.vmssilva.calculator.engine.ast.Node;

public interface Parser {
  Node parse(String expression);
}
