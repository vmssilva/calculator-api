package com.github.vmssilva.calculator.engine.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.std.type.ValueType;
import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public class Resolver {

  public FunctionValue resolve(List<FunctionValue> overloads, Value... args) {

    FunctionValue bestMatch = null;
    String name = "";

    if (overloads == null || overloads.isEmpty())
      throw new ExecutionErrorException("Target is not a function");

    name = overloads.get(0).name();

    for (FunctionValue fn : overloads) {

      if (!FunctionMatcher.matches(
          fn.parameters(),
          fn.isVarArgs(),
          args)) {
        continue;
      }

      if (bestMatch == null ||
          FunctionMatcher.isBetter(fn, bestMatch)) {
        bestMatch = fn;
      }
    }

    if (bestMatch != null) {
      return bestMatch;
    }

    throw buildError(name, overloads, args);

  }

  private ExecutionErrorException buildError(String name, List<FunctionValue> overloads, Value... args) {

    String received = Arrays.stream(args)
        .map(v -> v.type().friendly())
        .collect(Collectors.joining(", "));

    String available = overloads.stream()
        .map(fn -> formatSignature(fn.name(), fn))
        .collect(Collectors.joining("\n - ", "\n - ", ""));

    String message = """
        No matching overload for function '%s(%s)'.

        Available overloads:%s
        """
        .formatted(name, received, available);

    return new ExecutionErrorException(message);
  }

  static String formatSignature(String name, FunctionValue fn) {

    ValueType[] params = fn.parameters();

    List<String> parts = new ArrayList<>();

    for (int i = 0; i < params.length; i++) {

      String type = params[i].friendly();

      if (fn.isVarArgs() && i == params.length - 1) {
        type += "...";
      }

      parts.add(type);
    }

    return "%s(%s)"
        .formatted(name, String.join(", ", parts));
  }

}
