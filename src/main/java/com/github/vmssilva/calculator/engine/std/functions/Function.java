package com.github.vmssilva.calculator.engine.std.functions;

import java.util.List;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.value.Value;

public interface Function<K, R> {
  Value apply(ApplicationContext context, List<Value> args);
}
