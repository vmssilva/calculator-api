package com.github.vmssilva.calculator.engine.core.lang.number;

import com.github.vmssilva.calculator.engine.core.annotations.Expose;
import com.github.vmssilva.calculator.engine.core.annotations.Module;
import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.std.value.IntValue;
import com.github.vmssilva.calculator.engine.std.value.NumberValue;
import com.github.vmssilva.calculator.engine.std.value.StringValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

@Module(name = "Int")
public class Int {
  private Int() {
  }

  @Expose(name = "parse")
  public static IntValue parse(Value value) {

    if (value instanceof StringValue s && s.unwrap().length() == 1)
      return new IntValue((int) s.unwrap().charAt(0));

    if (NumberValue.class.isAssignableFrom(value.getClass()))
      return ((NumberValue) value).intValue();

    throw new ExecutionErrorException("Argument mismatch '" + value.unwrap() + "'");
  }
}
