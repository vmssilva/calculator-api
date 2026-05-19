package com.github.vmssilva.calculator.engine.std.functions;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.std.value.NumberValue;
import com.github.vmssilva.calculator.engine.std.value.StringValue;
import com.github.vmssilva.calculator.engine.std.value.Value;
import com.github.vmssilva.calculator.engine.std.value.Values;

public class TypeBuiltins {

  private TypeBuiltins() {
  }

  @Builtin(name = "int")
  public static Value intValue(ApplicationContext context, NumberValue<?> v) {
    return v.intValue();
  }

  @Builtin(name = "double")
  public static Value doubleValue(ApplicationContext context, NumberValue<?> v) {
    return v.doubleValue();
  }

  @Builtin(name = "decimal")
  public static Value decimalValue(ApplicationContext context, NumberValue<?> v) {
    return v.decimalValue();
  }

  @Builtin(name = "str")
  public static Value stringValue(ApplicationContext context, Value v) {
    return Values.of(v.unwrap().toString());
  }

  @Builtin(name = "type")
  public static Value typeValue(ApplicationContext context, Value v) {
    return new StringValue(v.type().friendly());
  }

}
