package com.github.vmssilva.calculator.engine.std.functions;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.vmssilva.calculator.engine.context.Scope;
import com.github.vmssilva.calculator.engine.exception.ValueErrorException;
import com.github.vmssilva.calculator.engine.std.ValueType;
import com.github.vmssilva.calculator.engine.value.FunctionValue;
import com.github.vmssilva.calculator.engine.value.NumberValue;
import com.github.vmssilva.calculator.engine.value.Value;
import com.github.vmssilva.calculator.engine.value.Values;

public class UtilFunctions {

  public static FunctionValue unset() {
    return FunctionFactory.of("unset", (context, args) -> {

      if (args.get(0).type() != ValueType.STRING) {
        throw new ValueErrorException("unset expect String argument");
      }

      var name = Values.asString(args.get(0));

      boolean removed = context.remove(name);

      if (!removed) {
        throw new ValueErrorException("variable '" + name + "' not found in current scope");
      }

      return new NumberValue(BigDecimal.ZERO);

    }, new ValueType[] { ValueType.STRING });

  }

  public static FunctionValue env() {
    return FunctionFactory.of("env", (context, args) -> {

      if (!args.isEmpty()) {
        throw new ValueErrorException("env() takes no arguments");
      }

      System.out.println("Environment:");

      Scope scope = context.currentScope();
      Map<String, Value> seen = new LinkedHashMap<>();

      while (scope != null) {

        for (var entry : scope.entries().entrySet()) {
          seen.putIfAbsent(entry.getKey(), entry.getValue());
        }

        scope = scope.getParent();
      }

      seen.entrySet().stream()
          .sorted(Map.Entry.comparingByKey())
          .forEach(entry -> {

            var name = entry.getKey();
            var value = entry.getValue();

            if (value instanceof FunctionValue fn) {
              System.out.println("fn " + name + " -> " + fn);
            } else {
              System.out.println("var " + name + " = " + value);
            }
          });

      return new NumberValue(BigDecimal.ZERO);

    }, new ValueType[] {});
  }

}
