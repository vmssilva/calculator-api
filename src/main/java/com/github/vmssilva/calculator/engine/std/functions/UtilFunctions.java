package com.github.vmssilva.calculator.engine.std.functions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.context.Scope;
import com.github.vmssilva.calculator.engine.exception.ValueErrorException;
import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.ListValue;
import com.github.vmssilva.calculator.engine.std.value.StringValue;
import com.github.vmssilva.calculator.engine.std.value.UnitValue;
import com.github.vmssilva.calculator.engine.std.value.Value;
import com.github.vmssilva.calculator.engine.std.value.Values;
import com.github.vmssilva.calculator.engine.utils.FunctionPrinter;

public final class UtilFunctions {

  private UtilFunctions() {
  }

  @Builtin(name = "unset", description = "Remove variable from current scope")
  public static Value unset(ApplicationContext context, StringValue s) {

    String name = s.unwrap();
    boolean removed = context.remove(name);

    if (!removed) {
      throw new ValueErrorException(
          "variable '" + name + "' not found in current scope");
    }

    return UnitValue.INSTANCE;
  }

  @Builtin(name = "env", description = "List current environment variables")
  public static Value env(ApplicationContext context) {

    List<Value> lines = new ArrayList<>();

    Scope scope = context.snapshot();

    Map<String, Value> seen = new LinkedHashMap<>();

    while (scope != null) {

      for (var entry : scope.entries().entrySet()) {
        seen.putIfAbsent(entry.getKey(), entry.getValue());
      }

      scope = scope.getParent();
    }

    seen.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByKey())
        .forEach(entry -> {

          var name = entry.getKey();
          var value = entry.getValue();

          if (value instanceof FunctionValue fn) {
            lines.add(
                Values.of(FunctionPrinter.formatWithPrefix(name, fn)));
          } else {
            lines.add(
                Values.of("var " + name + " = " + value.type().value()));
          }
        });

    return new ListValue(lines);
  }
}
