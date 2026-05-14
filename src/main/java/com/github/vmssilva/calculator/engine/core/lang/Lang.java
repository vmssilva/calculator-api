package com.github.vmssilva.calculator.engine.core.lang;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.std.value.*;

public final class Lang {

  private Lang() {
  }

  // =========================
  // INTROSPECTION
  // =========================
  public static Value vars(ApplicationContext context) {

    List<Value> result = new ArrayList<>();

    // context.snapshot().entries().forEach((name, value) -> {

    // if (!(value instanceof FunctionValue)) {
    // result.add(new BindingValue(name, value));
    // }

    // });

    return new ListValue(result);
  }

  public static Value fns(ApplicationContext context) {
    List<Value> result = new ArrayList<>();

    // for (Map.Entry<String, Value> e : context.snapshot().entries().entrySet()) {
    // if (e.getValue() instanceof FunctionValue fn) {
    // result.add(Values.of(fn.name()));
    // }
    // }

    return new ListValue(result);
  }

  // =========================
  // FUNCTION COMPOSITION
  // =========================

  public static Value identity(Value x) {
    return x;
  }

  public static Value apply(ApplicationContext context, FunctionValue fn, Value x) {
    return fn.call(context, x);
  }

  public static Value filter(ApplicationContext context, ListValue vars, FunctionValue fn) {

    List<Value> out = new ArrayList<>();

    for (Value v : vars.values()) {

      Value result = fn.call(context, v);

      if (Values.asDecimal(result).compareTo(BigDecimal.ZERO) != 0) {
        out.add(v);
      }
    }

    return new ListValue(out);
  }

  public static Value map(ApplicationContext context, ListValue vars, FunctionValue fn) {

    List<Value> out = new ArrayList<>();

    for (Value v : vars.values()) {
      out.add(fn.call(context, v));
    }

    return new ListValue(out);
  }

}
