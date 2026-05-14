package com.github.vmssilva.calculator.engine.std.functions;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.std.value.IntValue;
import com.github.vmssilva.calculator.engine.std.value.StringValue;
import com.github.vmssilva.calculator.engine.std.value.Value;
import com.github.vmssilva.calculator.engine.std.value.Values;

public final class StringBuiltins {

  private StringBuiltins() {
  }

  @Builtin(name = "len")
  public static final Value len(ApplicationContext context, StringValue value) {
    return Values.of(value.unwrap().length());
  }

  @Builtin(name = "substring")
  public static final Value subString(ApplicationContext context, StringValue value, IntValue begin, IntValue end) {
    return Values.of(value.unwrap().substring(begin.unwrap(), end.unwrap()));
  }

  @Builtin(name = "substring")
  public static final Value subString(ApplicationContext context, StringValue value, IntValue begin) {
    return Values.of(value.unwrap().substring(begin.unwrap(), value.unwrap().length()));
  }
}
