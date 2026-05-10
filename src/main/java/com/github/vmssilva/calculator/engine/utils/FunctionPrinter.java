package com.github.vmssilva.calculator.engine.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public final class FunctionPrinter {

  private FunctionPrinter() {
  }

  public static String format(FunctionValue fn) {

    return "";
    // return fn.name() + "(" +
    // Arrays.stream(fn.parameters())
    // .map(p -> p.getSimpleName()) // fallback seguro
    // .collect(Collectors.joining(", "))
    // + ")";
  }

  public static String format(FunctionValue fn, Value[] args) {

    return fn.name() + "(" +
        Arrays.stream(args)
            .map(v -> v.type().value())
            .collect(Collectors.joining(", "))
        + ")";
  }

  public static String formatWithPrefix(String name, FunctionValue fn) {

    return "fn " + name + " -> " + format(fn);
  }
}
