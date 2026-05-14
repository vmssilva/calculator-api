package com.github.vmssilva.calculator.engine.std.functions;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.std.value.Value;

public interface Callable {
  Value call(ApplicationContext contex, Value... args);
}
