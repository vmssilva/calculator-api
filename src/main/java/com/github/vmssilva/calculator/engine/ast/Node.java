package com.github.vmssilva.calculator.engine.ast;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;

public interface Node {
  Object interpret(ApplicationContext context);
}
