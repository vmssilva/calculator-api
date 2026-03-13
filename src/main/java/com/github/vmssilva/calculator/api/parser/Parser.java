package com.github.vmssilva.calculator.api.parser;

import com.github.vmssilva.calculator.api.ast.Expression;

public interface Parser {
  Expression parse(String expression);
}
