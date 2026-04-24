package com.github.vmssilva.calculator.engine.ast;

import java.util.List;

import com.github.vmssilva.calculator.engine.value.Value;

public interface Function<K, R> {
  Value apply(List<Value> args);
}
