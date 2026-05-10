package com.github.vmssilva.calculator.engine.std.functions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public final class FunctionFactory {

  private FunctionFactory() {
  }

  public static FunctionValue of(Method method) {

    var builtin = method.getAnnotation(Builtin.class);

    if (builtin == null) {
      throw new IllegalArgumentException("Missing @Builtin");
    }

    Class<?>[] paramTypes = method.getParameterTypes();
    boolean isVarArgs = method.isVarArgs();

    int totalParams = paramTypes.length - 1; // remove ApplicationContext
    int fixedParams = isVarArgs ? totalParams - 1 : totalParams;

    return new FunctionValue() {

      @Override
      public Value call(ApplicationContext context, Value... args) {

        if (args == null) {
          throw new ExecutionErrorException("Arguments cannot be null");
        }

        // =========================
        // 1. ARITY VALIDATION
        // =========================

        if (!isVarArgs) {

          if (args.length != totalParams) {
            throw new ExecutionErrorException(
                builtin.name() + " expects " + totalParams +
                    " args, got " + args.length);
          }

        } else {

          if (args.length < fixedParams) {
            throw new ExecutionErrorException(
                builtin.name() + " expects at least " + fixedParams +
                    " args, got " + args.length);
          }
        }

        try {

          Object[] invokeArgs = new Object[paramTypes.length];

          // context sempre primeiro
          invokeArgs[0] = context;

          // =========================
          // 2. FIXED ARGS
          // =========================

          for (int i = 0; i < fixedParams; i++) {
            invokeArgs[i + 1] = cast(args[i], paramTypes[i + 1]);
          }

          // =========================
          // 3. VARARGS (se existir)
          // =========================

          if (isVarArgs) {

            int varCount = args.length - fixedParams;

            Class<?> varArrayType = paramTypes[paramTypes.length - 1];
            Class<?> componentType = varArrayType.componentType();

            Object varArray = java.lang.reflect.Array.newInstance(componentType, varCount);

            for (int i = 0; i < varCount; i++) {
              java.lang.reflect.Array.set(varArray, i, args[fixedParams + i]);
            }

            invokeArgs[paramTypes.length - 1] = varArray;
          }

          // =========================
          // 4. INVOKE
          // =========================

          return (Value) method.invoke(null, invokeArgs);

        } catch (InvocationTargetException e) {

          Throwable cause = e.getCause();
          throw unwrap(cause);

        } catch (ExecutionErrorException e) {
          throw e;

        } catch (RuntimeException e) {
          throw e;

        } catch (Throwable t) {
          throw new RuntimeException(t);
        }
      }

      private RuntimeException unwrap(Throwable t) {

        if (t instanceof ExecutionErrorException ex) {
          return ex;
        }

        if (t instanceof RuntimeException re) {
          return re;
        }

        return new RuntimeException(t);
      }

      @Override
      public String name() {
        return builtin.name();
      }

      @Override
      public FunctionValue unwrap() {
        return this;
      }
    };
  }

  private static Object cast(Value v, Class<?> expected) {

    if (expected.isInstance(v)) {
      return v;
    }

    throw new ExecutionErrorException(
        "Expected " + expected.getSimpleName()
            + " but got " + v.getClass().getSimpleName());
  }

}
