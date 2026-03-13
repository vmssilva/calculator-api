package com.github.vmssilva.calculator.parser;

import com.github.vmssilva.calculator.ast.Expression;

public interface Parser {
  Expression parse(String expression);
}
