package com.github.vmssilva.calculator.engine.ast;

import java.util.List;

public interface Function {
  Object apply(List<Object> args);
}
