package com.github.vmssilva.calculator.engine.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.std.type.ValueType;
import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.StringValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public final class Scope {

  private Scope parent;

  private final Map<String, Value> variables;
  private final Map<String, List<FunctionValue>> functions;

  public Scope(Scope parent) {
    this.parent = parent;
    this.variables = new HashMap<>();
    this.functions = new HashMap<>();
  }

  public void setParent(Scope parent) {
    this.parent = parent;
  }

  // Parent access (closures)
  public Scope getParent() {
    return parent;
  }

  public void defineVariable(String name, Value value) {
    variables.put(name, value);
  }

  public void defineFunction(String name, FunctionValue function) {
    functions
        .computeIfAbsent(name, _i -> new ArrayList<>())
        .add(function);
  }

  public boolean hasVariable(String name) {

    if (variables.containsKey(name)) {
      return true;
    }

    return parent != null && parent.hasVariable(name);
  }

  public boolean hasFunction(String name) {

    if (functions.containsKey(name)) {
      return true;
    }

    return parent != null && parent.hasFunction(name);
  }

  public boolean removeVariable(String name) {

    if (variables.containsKey(name)) {
      variables.remove(name);
      return true;
    }

    if (parent != null) {
      return parent.removeVariable(name);
    }

    return false;
  }

  public boolean removeFunction(String name, FunctionValue function) {

    List<FunctionValue> overloads = functions.get(name);

    if (overloads != null) {

      boolean removed = overloads.remove(function);

      if (overloads.isEmpty()) {
        functions.remove(name);
      }

      return removed;
    }

    return parent != null &&
        parent.removeFunction(name, function);
  }

  public FunctionValue getFunction(String name, FunctionValue fn) {

    List<FunctionValue> overloads = functions.get(name);

    if (overloads == null || overloads.isEmpty()) {
      throw new ExecutionErrorException(
          "Undefined function '%s'".formatted(name));
    }

    for (FunctionValue existing : overloads) {
      if (FunctionMatcher.sameSignature(existing, fn)) {
        return existing;
      }
    }

    throw new ExecutionErrorException(
        FunctionDiagnostics.buildFunctionNotFoundError(name, fn, overloads));
  }

  public List<Value> getVariable(String name) {

    Value value = variables.get(name);

    if (value != null) {
      return List.of(new StringValue(name), value);
    }

    if (parent != null) {
      return parent.getVariable(name);
    }

    return null;

  }

  public List<FunctionValue> getFunctions(String name) {

    List<FunctionValue> overloads = functions.get(name);

    if (overloads != null && !overloads.isEmpty()) {
      return overloads;
    }

    if (parent != null) {
      return parent.getFunctions(name);
    }

    return List.of();
  }

  public FunctionValue getFunction(String name, int arity) {

    List<FunctionValue> overloads = getFunctions(name);

    for (FunctionValue fn : overloads) {
      if (fn.parameters().length == arity) {
        return fn;
      }
    }

    return null;
  }

  public FunctionValue getFunction(String name, Value... args) {

    List<FunctionValue> overloads = functions.get(name);

    if ((overloads == null || overloads.isEmpty()) && parent != null) {
      return parent.getFunction(name, args);
    }

    if (overloads == null || overloads.isEmpty()) {
      return null;
    }

    FunctionValue bestMatch = null;

    for (FunctionValue fn : overloads) {

      ValueType[] params = fn.parameters();

      boolean varArgs = fn.isVarArgs(); // instanceof BuiltinFunctionValue b
      // && b.getMethod().isVarArgs(); // OU equivalente interno

      if (!FunctionMatcher.matches(params, varArgs, args)) {
        continue;
      }

      if (bestMatch == null || FunctionMatcher.isBetter(fn, bestMatch)) {
        bestMatch = fn;
      }
    }

    if (bestMatch != null) {
      return bestMatch;
    }

    return null;
  }

  public FunctionValue getFunction(
      List<FunctionValue> overloads,
      Value... args) {

    FunctionValue bestMatch = null;

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

    return null;
  }

  // HELPER CLASSES
  private static final class FunctionMatcher {

    static boolean matches(ValueType[] params, boolean varArgs, Value[] args) {

      int fixed = varArgs ? params.length - 1 : params.length;

      if (args.length < fixed) {
        return false;
      }

      if (!varArgs) {

        if (args.length != params.length) {
          return false;
        }

        for (int i = 0; i < params.length; i++) {
          if (!params[i].accepts(args[i])) {
            return false;
          }
        }

        return true;
      }

      for (int i = 0; i < fixed; i++) {

        if (!params[i].accepts(args[i])) {
          return false;
        }
      }

      ValueType varType = params[params.length - 1];

      for (int i = fixed; i < args.length; i++) {

        if (!varType.accepts(args[i])) {
          return false;
        }
      }

      return true;
    }

    static boolean isBetter(FunctionValue a, FunctionValue b) {

      boolean aVar = a.isVarArgs();
      boolean bVar = b.isVarArgs();

      if (!aVar && bVar)
        return true;
      if (aVar && !bVar)
        return false;

      return a.parameters().length < b.parameters().length;
    }

    static boolean sameSignature(FunctionValue a, FunctionValue b) {

      ValueType[] ap = a.parameters();
      ValueType[] bp = b.parameters();

      if (a.isVarArgs() != b.isVarArgs()) {
        return false;
      }

      if (ap.length != bp.length) {
        return false;
      }

      for (int i = 0; i < ap.length; i++) {
        if (ap[i] != bp[i]) {
          return false;
        }
      }

      return true;
    }
  }

  private static final class FunctionDiagnostics {

    static String buildFunctionNotFoundError(
        String name,
        FunctionValue fn,
        List<FunctionValue> overloads) {

      String targetSignature = format(fn);

      String available = overloads.stream()
          .map(FunctionDiagnostics::format)
          .collect(Collectors.joining("\n  - ", "\n  - ", ""));

      return """
          Function '%s' with signature %s not found.

          Available overloads:%s
          """
          .formatted(name, targetSignature, available);
    }

    static String format(FunctionValue fn) {

      String params = Arrays.stream(fn.parameters())
          .map(ValueType::friendly)
          .collect(Collectors.joining(", "));

      return fn.isVarArgs()
          ? "%s(%s...)".formatted(fn.name(), params)
          : "%s(%s)".formatted(fn.name(), params);
    }
    // static String buildError(
    // String name,
    // List<FunctionValue> overloads,
    // Value[] args) {

    // String received = Arrays.stream(args)
    // .map(v -> v.type().friendly())
    // .collect(Collectors.joining(", "));

    // String available = overloads.stream()
    // .map(fn -> formatSignature(name, fn))
    // .collect(Collectors.joining("\n - ", "\n - ", ""));

    // return """
    // No matching overload for function '%s(%s)'.

    // Available overloads:%s
    // """
    // .formatted(name, received, available);
    // }

    // static String formatSignature(String name, FunctionValue fn) {

    // ValueType[] params = fn.parameters();

    // List<String> parts = new ArrayList<>();

    // for (int i = 0; i < params.length; i++) {

    // String type = params[i].friendly();

    // if (fn.isVarArgs() && i == params.length - 1) {
    // type += "...";
    // }

    // parts.add(type);
    // }

    // return "%s(%s)"
    // .formatted(name, String.join(", ", parts));
    // }
  }

}
