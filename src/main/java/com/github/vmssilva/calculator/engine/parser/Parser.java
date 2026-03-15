package com.github.vmssilva.calculator.engine.parser;

import com.github.vmssilva.calculator.engine.ast.Expression;

public interface Parser {
  Expression parse(String expression);
}
