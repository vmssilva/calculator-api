package com.github.vmssilva.calculator.engine.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public final class Validators {

  private Validators() {
  }

  public static boolean isAssignable(FunctionValue fn, Value[] args) {

    // var types = fn.parameters();

    // if (types.length != args.length) {
    // return false;
    // }

    // for (int i = 0; i < types.length; i++) {

    // Class<?> expected = types[i];

    // if (!expected.isInstance(args[i])) {
    // return false;
    // }
    // }

    return true;
  }

  public static void validate(FunctionValue fn, Value[] args) {

    if (args == null) {
      throw new ExecutionErrorException("Arguments cannot be null");
    }

    Class<?>[] params = new Class[] {};// fn.parameters();

    boolean isVarArgs = params.length > 0 &&
        params[params.length - 1].isArray();

    int fixedParams = isVarArgs
        ? params.length - 1
        : params.length;

    // aridade fixa
    if (!isVarArgs && args.length != params.length) {

      throw new ExecutionErrorException(
          "expects: "
              + signature(fn.name(), params)
              + ", got: "
              + actualSignature(fn.name(), args));
    }

    // mínimo de argumentos
    if (isVarArgs && args.length < fixedParams) {

      throw new ExecutionErrorException(
          "expects at least: "
              + fixedParams
              + " args, got: "
              + args.length);
    }

    // parâmetros fixos
    for (int i = 0; i < fixedParams; i++) {

      Class<?> expected = params[i];
      Value received = args[i];

      if (!expected.isInstance(received)) {

        throw new ExecutionErrorException(
            "expects: "
                + signature(fn.name(), params)
                + ", got: "
                + actualSignature(fn.name(), args));
      }
    }

    // varargs
    if (isVarArgs) {

      Class<?> varType = params[params.length - 1]
          .componentType();

      for (int i = fixedParams; i < args.length; i++) {

        Value received = args[i];

        if (!varType.isInstance(received)) {

          throw new ExecutionErrorException(
              "expects: "
                  + signature(fn.name(), params)
                  + ", got: "
                  + actualSignature(fn.name(), args));
        }
      }
    }
  }

  private static String signature(
      String name,
      Class<?>[] params) {

    return name + "(" +
        Arrays.stream(params)
            .map(Validators::displayType)
            .collect(Collectors.joining(", "))
        + ")";
  }

  private static String actualSignature(
      String name,
      Value[] args) {

    return name + "(" +
        Arrays.stream(args)
            .map(v -> v.getClass().getSimpleName())
            .collect(Collectors.joining(", "))
        + ")";
  }

  private static String displayType(Class<?> type) {

    if (type.isArray()) {
      return type.componentType().getSimpleName() + "...";
    }

    return type.getSimpleName();
  }

  // public static void validate(FunctionValue fn, Value[] args) {

  // if (args == null) {
  // throw new ExecutionErrorException("Arguments cannot be null");
  // }

  // Class<?>[] params = fn.parameters();

  // boolean isVarArgs = params.length > 0 &&
  // params[params.length - 1].isArray();

  // int fixedParams = isVarArgs ? params.length - 1 : params.length;

  // if (!isVarArgs && args.length != params.length) {
  // throw new ExecutionErrorException(
  // fn.name() + " expects " + params.length
  // + " args, got " + args.length);
  // }

  // if (isVarArgs && args.length < fixedParams) {
  // throw new ExecutionErrorException(
  // fn.name() + " expects at least "
  // + fixedParams + " args, got " + args.length);
  // }

  // // valida parâmetros fixos
  // for (int i = 0; i < fixedParams; i++) {

  // Class<?> expected = params[i];

  // if (!expected.isInstance(args[i])) {
  // throw new ExecutionErrorException(
  // fn.name()
  // + " expects "
  // + expected.getSimpleName()
  // + " at position "
  // + i);
  // }
  // }

  // // valida varargs (se houver)
  // if (isVarArgs) {

  // Class<?> varType = params[params.length - 1].componentType();

  // for (int i = fixedParams; i < args.length; i++) {

  // if (!varType.isInstance(args[i])) {
  // throw new ExecutionErrorException(
  // fn.name()
  // + " invalid vararg at position "
  // + i);
  // }
  // }
  // }
  // }

}
