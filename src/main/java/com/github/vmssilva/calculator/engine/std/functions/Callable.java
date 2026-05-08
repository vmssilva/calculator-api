package com.github.vmssilva.calculator.engine.std.functions;

import java.util.List;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.std.value.Value;

public interface Callable {
  Value apply(ApplicationContext contex, List<Value> args);
}
