package com.github.vmssilva.calculator.engine.context;

import com.github.vmssilva.calculator.engine.std.type.ValueType;
import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public final class FunctionMatcher {

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
