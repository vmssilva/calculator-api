package com.github.vmssilva.calculator.engine.context;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.vmssilva.calculator.engine.core.lang.math.MathOperations;
import com.github.vmssilva.calculator.engine.core.module.ModuleReference;
import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.std.constants.Constants;
import com.github.vmssilva.calculator.engine.std.functions.Builtin;
import com.github.vmssilva.calculator.engine.std.functions.FunctionFactory;
import com.github.vmssilva.calculator.engine.std.functions.MathBuiltins;
import com.github.vmssilva.calculator.engine.std.functions.PredicateBuiltins;
import com.github.vmssilva.calculator.engine.std.functions.StringBuiltins;
import com.github.vmssilva.calculator.engine.std.functions.TypeBuiltins;
import com.github.vmssilva.calculator.engine.std.functions.UtilBuiltins;
import com.github.vmssilva.calculator.engine.std.type.ValueType;
import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.ListValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public class ApplicationContext implements ContextCapabilities {

  private final Scope builtins;
  private Scope current;
  private Map<String, Class<?>> modules;

  public ApplicationContext() {
    this.builtins = new Scope(null);
    loadBuiltins(builtins);

    Scope global = new Scope(builtins);
    this.current = global;

    this.modules = new HashMap<>();
    addModule("math", MathOperations.class);
  }

  private void addModule(String name, Class<?> clazz) {
    modules.put(name, clazz);
  }

  // =========================
  // STACK CONTROL
  // =========================
  @Override
  public void pushScope() {
    current = new Scope(current);
  }

  @Override
  public void popScope() {
    if (current.getParent() != null) {
      current = current.getParent();
    }
  }

  public Class<?> getModule(String name) {
    return modules.get(name);
  }

  public Scope currentScope() {
    return current;
  }

  public List<FunctionValue> lookup(Value receiver, String method) {

    if (receiver instanceof ModuleReference module) {
      return lookupStatic(module.name(), module.clazz(), method);
    }

    List<FunctionValue> overloads = new ArrayList<>();

    // =====================================
    // INSTANCE METHODS
    // =====================================
    for (Method m : receiver.getClass().getMethods()) {

      if (!m.getName().equals(method)) {
        continue;
      }

      overloads.add(
          delegate(receiver, m));
    }

    // =====================================
    // no method
    // =====================================
    if (overloads.isEmpty()) {

      throw new ExecutionErrorException(
          "Method '" + method +
              "' is not defined for type '" +
              receiver.type().friendly() + "'");
    }

    return overloads;
  }

  public List<FunctionValue> lookup(String name, Value... args) {
    var funcs = current.getFunctions(name).stream()
        .filter(fn -> fn.parameters().length == args.length).toList();

    if (funcs == null || funcs.isEmpty())
      throw new ExecutionErrorException("Function " + name + " is not defined");

    return funcs;
  }

  public void define(String name, Value value) {
    if (value instanceof FunctionValue fn) {
      defineFunction(name, fn);
      return;
    }
    defineVariable(name, value);
  }

  @Override
  public void defineFunction(String name, FunctionValue fn) {
    current.defineFunction(name, fn);
  }

  @Override
  public void defineVariable(String name, Value value) {
    current.defineVariable(name, value);
  }

  @Override
  public Scope snapshot() {
    return current;
  }

  public Scope builtins() {
    return builtins;
  }

  public boolean hasVariable(String name) {
    return current.hasVariable(name);
  }

  public boolean hasFunction(String name) {
    return current.hasFunction(name);
  }

  public boolean removeVariable(String name) {
    return current.removeVariable(name);
  }

  public boolean removeFunction(String name, FunctionValue fn) {
    return current.removeFunction(name, fn);
  }

  public Value resolve(String module, String property) {

    Class<?> type = modules.get(module);

    if (type == null) {
      throw new ExecutionErrorException(
          "Module '" + module + "' is not defined");
    }

    List<FunctionValue> overloads = new ArrayList<>();

    for (Method m : type.getMethods()) {

      // apenas static
      if (!Modifier.isStatic(m.getModifiers())) {
        continue;
      }

      if (!m.getName().equals(property)) {
        continue;
      }

      overloads.add(delegate(null, m));
    }

    // =====================================
    // encontrou métodos
    // =====================================
    if (!overloads.isEmpty()) {

      if (overloads.size() == 1) {
        return overloads.get(0);
      }

      return new ListValue(
          new ArrayList<>(overloads));
    }

    // =====================================
    // tenta campo/property
    // =====================================
    try {

      Field field = type.getField(property);

      if (!Modifier.isStatic(
          field.getModifiers())) {

        throw new ExecutionErrorException(
            "'" + property +
                "' is not static");
      }

      return (Value) field.get(null);

    } catch (
        NoSuchFieldException | IllegalAccessException e) {

      throw new ExecutionErrorException(
          "Property '" + property +
              "' is not defined in module '" +
              module + "'");
    }
  }

  public Value resolve(String name) {

    // =========================
    // variável
    // =========================
    var variable = current.getVariable(name);

    if (variable != null) {
      return variable.get(1);
    }

    // =========================
    // função global
    // =========================
    var funcs = current.getFunctions(name);

    if (funcs != null && !funcs.isEmpty()) {

      if (funcs.size() == 1) {
        return funcs.get(0);
      }

      return new ListValue(
          new ArrayList<>(funcs));
    }

    // =========================
    // módulo
    // =========================
    var module = modules.get(name);

    if (module != null) {
      return new ModuleReference(name, module);
    }

    throw new ExecutionErrorException(
        "'" + name + "' is not defined");
  }

  // Helpers
  // Builtins loader
  private void loadBuiltins(Scope builtins) {

    for (Constants constant : Constants.values()) {
      builtins.defineVariable(constant.key(), constant.value());
    }

    scanBuiltins(builtins, MathBuiltins.class);
    scanBuiltins(builtins, PredicateBuiltins.class);
    scanBuiltins(builtins, UtilBuiltins.class);
    scanBuiltins(builtins, StringBuiltins.class);
    scanBuiltins(builtins, TypeBuiltins.class);

  }

  private void scanBuiltins(Scope builtins, Class<?> clazz) {

    for (var method : clazz.getDeclaredMethods()) {

      var builtin = method.getAnnotation(Builtin.class);

      if (builtin == null) {
        continue;
      }

      FunctionValue function = FunctionFactory.of(method);

      builtins.defineFunction(builtin.name(), function);
    }
  }

  private FunctionValue delegate(Value target, Method method) {

    return new FunctionValue() {

      @Override
      public Value call(
          ApplicationContext context,
          Value... invokeArgs) {

        try {

          return (Value) method.invoke(
              target,
              buildInvokeArgs(
                  context,
                  method,
                  invokeArgs));

        } catch (InvocationTargetException ex) {

          Throwable cause = ex.getCause();

          if (cause instanceof RuntimeException rt) {
            throw rt;
          }

          throw new ExecutionErrorException(
              cause.getMessage());

        } catch (Exception e) {

          throw new ExecutionErrorException(
              e.getMessage());
        }
      }

      @Override
      public ValueType[] parameters() {

        return Arrays.stream(
            method.getParameterTypes())

            // ignora ApplicationContext
            .filter(type -> !ApplicationContext.class
                .isAssignableFrom(type))

            .map(ValueType::fromJava)

            .toArray(ValueType[]::new);
      }

      @Override
      public boolean isVarArgs() {
        return method.isVarArgs();
      }

      @Override
      public String name() {
        return method.getName();
      }

      @Override
      public String toString() {

        String params = Arrays.stream(parameters())
            .map(ValueType::friendly)
            .collect(Collectors.joining(", "));

        return name() + "(" + params + ")";
      }
    };
  }

  private List<FunctionValue> lookupStatic(String name, Class<?> type, String method,
      Value... args) {

    List<FunctionValue> overloads = new ArrayList<>();

    for (Method m : type.getMethods()) {

      if (!Modifier.isStatic(m.getModifiers())) {
        continue;
      }

      if (!m.getName().equals(method)) {
        continue;
      }

      overloads.add(delegate(null, m));
    }

    if (overloads.isEmpty()) {

      throw new ExecutionErrorException(
          "Method '" + method +
              "' is not defined in module '" +
              name + "'");
    }

    return overloads; // resolve(overloads, args);
  }

  private Object[] buildInvokeArgs(ApplicationContext context, Method method, Value[] args) {
    Class<?>[] types = method.getParameterTypes();

    // =====================================
    // sem context
    // =====================================
    if (types.length == args.length) {
      return args;
    }

    // =====================================
    // context primeiro parâmetro
    // =====================================
    if (types.length > 0 &&
        ApplicationContext.class.isAssignableFrom(types[0])) {

      Object[] invokeArgs = new Object[args.length + 1];

      invokeArgs[0] = context;

      System.arraycopy(
          args,
          0,
          invokeArgs,
          1,
          args.length);

      return invokeArgs;
    }

    return args;
  }

  // public Value resolve(String name, Value... args) {
  // var funcs = current.getFunctions(name);

  // if (funcs == null || funcs.isEmpty())
  // throw new ExecutionErrorException("Function " + name + " is not defined");

  // return resolve(funcs, args);
  // }
  // public Value resolve(List<FunctionValue> overloads, Value... args) {

  // FunctionValue bestMatch = null;
  // String name = "";

  // if (overloads == null || overloads.isEmpty())
  // throw new ExecutionErrorException("Target is not a function");

  // name = overloads.get(0).name();

  // for (FunctionValue fn : overloads) {

  // if (!FunctionMatcher.matches(
  // fn.parameters(),
  // fn.isVarArgs(),
  // args)) {
  // continue;
  // }

  // if (bestMatch == null ||
  // FunctionMatcher.isBetter(fn, bestMatch)) {
  // bestMatch = fn;
  // }
  // }

  // if (bestMatch != null) {
  // return bestMatch.call(this, args);
  // }

  // throw buildError(name, overloads, args);

  // }

  // public Value resolve(Value value, String method, Value... args) {

  // // =====================================
  // // MODULE STATIC CALL
  // // =====================================
  // if (value instanceof ModuleReference module) {

  // return resolveStatic(
  // module.name(),
  // module.clazz(),
  // method,
  // args);
  // }

  // List<FunctionValue> overloads = new ArrayList<>();

  // // =====================================
  // // INSTANCE METHODS
  // // =====================================
  // for (Method m : value.getClass().getMethods()) {

  // if (!m.getName().equals(method)) {
  // continue;
  // }

  // overloads.add(
  // delegate(value, m));
  // }

  // // =====================================
  // // no method
  // // =====================================
  // if (overloads.isEmpty()) {

  // throw new ExecutionErrorException(
  // "Method '" + method +
  // "' is not defined for type '" +
  // value.type().friendly() + "'");
  // }

  // return resolve(overloads, args);
  // }

  // private Value resolveStatic(String name, Class<?> type, String method,
  // Value... args) {

  // List<FunctionValue> overloads = new ArrayList<>();

  // for (Method m : type.getMethods()) {

  // if (!Modifier.isStatic(m.getModifiers())) {
  // continue;
  // }

  // if (!m.getName().equals(method)) {
  // continue;
  // }

  // overloads.add(delegate(null, m));
  // }

  // if (overloads.isEmpty()) {

  // throw new ExecutionErrorException(
  // "Method '" + method +
  // "' is not defined in module '" +
  // name + "'");
  // }

  // return resolve(overloads, args);
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

  // private ExecutionErrorException buildError(String name, List<FunctionValue>
  // overloads, Value... args) {

  // String received = Arrays.stream(args)
  // .map(v -> v.type().friendly())
  // .collect(Collectors.joining(", "));

  // String available = overloads.stream()
  // .map(fn -> formatSignature(fn.name(), fn))
  // .collect(Collectors.joining("\n - ", "\n - ", ""));

  // String message = """
  // No matching overload for function '%s(%s)'.

  // Available overloads:%s
  // """
  // .formatted(name, received, available);

  // return new ExecutionErrorException(message);
  // }

}
