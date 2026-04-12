package com.github.vmssilva.calculator.engine.value;

import java.util.List;

public interface FunctionValue extends Value {
  Value apply(List<Value> args);
}
