package com.github.vmssilva.calculator.engine.std.functions;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.std.type.ValueType;
import com.github.vmssilva.calculator.engine.std.value.Value;

public record JavaFunction(Method method) implements Value {

  @Override
  public ValueType type() {
    return ValueType.FUNCTION;
  }

  @Override
  public Object unwrap() {
    return method;
  }

  public Value call(Value... args) {
    try {
      Object[] raw = Arrays.stream(args)
          .map(Value::unwrap)
          .toArray();

      Object result = method.invoke(null, raw);

      return (Value) result;

    } catch (Exception e) {
      throw new ExecutionErrorException(e.getMessage());
    }
  }
}
