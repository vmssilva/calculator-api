package com.github.vmssilva.calculator.engine.std.functions;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.std.value.StringValue;
import com.github.vmssilva.calculator.engine.std.value.Value;
import com.github.vmssilva.calculator.engine.std.value.Values;

public final class StringFunctions {

  private StringFunctions() {
  }

  @Builtin(name = "len")
  public static final Value len(ApplicationContext context, StringValue value) {
    return Values.of(value.unwrap().length());
  }

}
