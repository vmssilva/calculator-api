package com.github.vmssilva.calculator.engine.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.vmssilva.calculator.engine.exception.ErrorValueException;
import com.github.vmssilva.calculator.engine.std.ValueType;
import com.github.vmssilva.calculator.engine.value.FunctionValue;
import com.github.vmssilva.calculator.engine.value.Value;

public final class Validators {

  private Validators() {
  }

  public static boolean isAssignable(FunctionValue fn, List<Value> args) {

    var parameters = Arrays.asList(fn.parameters());

    if (parameters.size() == 1 && parameters.get(0) == ValueType.LIST && !args.isEmpty()) {
      return true;
    }

    if (parameters.size() != args.size())
      return false;

    for (int i = 0; i < parameters.size(); i++) {
      var type = parameters.get(i);

      if (type == ValueType.ANY)
        continue;

      if (type != args.get(i).type())
        return false;
    }

    return true;
  }

  public static void validate(FunctionValue fn, List<Value> args) {

    if (args == null) {
      throw new ErrorValueException("Arguments cannot be null");
    }

    var isAssignable = isAssignable(fn, args);

    if (!isAssignable) {

      var parameters = Arrays.asList(fn.parameters());

      var expect = fn.name() + "(" +
          parameters.stream()
              .map(p -> p.value())
              .collect(Collectors.joining(", "))
          + ")";

      var actual = fn.name() + "(" +
          args.stream()
              .map(p -> p.type().value())
              .collect(Collectors.joining(", "))
          + ")";

      if (!parameters.isEmpty() && parameters.get(0) == ValueType.LIST) {
        expect = fn.name() + "(...Any)";
      }

      throw new ErrorValueException("expects: " + expect + ", got: " + actual);
    }
  }

}
