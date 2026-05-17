package com.github.vmssilva.calculator.engine.std.functions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.std.type.ValueType;
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

    return new BuiltinFunctionValue(method, builtin.name());
  }

  private static final class BuiltinFunctionValue implements FunctionValue {

    private final Method method;
    private final String name;

    private BuiltinFunctionValue(Method method, String name) {
      this.method = method;
      this.name = name;
    }

    @Override
    public Value call(ApplicationContext context, Value... args) {
      try {

        Object[] invokeArgs = buildInvokeArgs(context, args);

        return (Value) method.invoke(null, invokeArgs);

      } catch (ArrayIndexOutOfBoundsException ex) {

        throw new ExecutionErrorException(buildVarArgsError(args));

      } catch (InvocationTargetException ex) {

        throw unwrap(ex.getCause());

      } catch (ExecutionErrorException ex) {

        throw ex;

      } catch (Throwable t) {

        throw new ExecutionErrorException(
            buildInternalError(t));
      }
    }

    @Override
    public ValueType[] parameters() {

      return Arrays.stream(method.getParameterTypes())
          .skip(1) // remove ApplicationContext
          .map(ValueType::fromJava)
          .toArray(ValueType[]::new);
    }

    @Override
    public String name() {
      return name;
    }

    @Override
    public boolean isVarArgs() {
      return method.isVarArgs();
    }

    private String buildInternalError(Throwable t) {

      return """
          Internal error executing builtin function '%s'.

          Message:
          %s
          """
          .formatted(name, t.getMessage());
    }

    // ======================================================
    // INVOCATION
    // ======================================================
    private String buildVarArgsError(Value[] args) {

      return """
          Error calling builtin function '%s'.

          Reason:
          Varargs invocation failed during argument packing.

          Received:
            %s

          Likely cause:
          - wrong number of arguments for varargs function
          - mismatch between fixed parameters and varargs tail

          Hint:
          Check the function signature (fixed args + ...).
          """
          .formatted(
              name,
              Arrays.stream(args)
                  .map(v -> v.type().friendly())
                  .collect(Collectors.joining(", ")));
    }

    private Object[] buildInvokeArgs(ApplicationContext context, Value[] args) {

      Class<?>[] types = method.getParameterTypes();

      if (types.length == 0 ||
          !ApplicationContext.class.isAssignableFrom(types[0])) {

        throw new IllegalArgumentException(
            buildContextError(method));
      }

      Object[] invokeArgs = new Object[types.length];

      // context sempre primeiro
      invokeArgs[0] = context;

      if (!method.isVarArgs()) {

        for (int i = 1; i < types.length; i++) {
          invokeArgs[i] = args[i - 1];
        }

        return invokeArgs;
      }

      int fixed = types.length - 2;
      int varCount = args.length - fixed;

      // fixed args
      for (int i = 0; i < fixed; i++) {
        invokeArgs[i + 1] = args[i];
      }

      // varargs
      Class<?> component = types[types.length - 1].getComponentType();

      Object array = java.lang.reflect.Array.newInstance(component, varCount);

      for (int i = 0; i < varCount; i++) {
        java.lang.reflect.Array.set(array, i, args[fixed + i]);
      }

      invokeArgs[types.length - 1] = array;

      return invokeArgs;
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
    public String toString() {
      return name + "(" +
          Arrays.stream(parameters())
              .map(ValueType::friendly)
              .collect(Collectors.joining(", "))
          +
          ")";
    }
  }

  private static String buildContextError(Method method) {

    return """
        Invalid @Builtin function declaration:

        Method: %s.%s

        Reason:
        The first parameter MUST be ApplicationContext.

        Example:
          public static Value %s(ApplicationContext context, ...)

        Fix the method signature and try again.
        """
        .formatted(
            method.getDeclaringClass().getSimpleName(),
            method.getName(),
            method.getName());
  }

}
