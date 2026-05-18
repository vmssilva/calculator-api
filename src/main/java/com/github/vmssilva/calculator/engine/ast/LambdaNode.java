package com.github.vmssilva.calculator.engine.ast;

import java.util.Arrays;
import java.util.List;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.context.Scope;
import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.exception.ReturnValueException;
import com.github.vmssilva.calculator.engine.utils.AstPrinter;
import com.github.vmssilva.calculator.engine.std.type.ValueType;
import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public record LambdaNode(List<String> params, Node body) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {

    Scope closureScope = context.snapshot();

    return new FunctionValue() {

      private final Scope closure = closureScope;

      @Override
      public Value call(ApplicationContext ctx, Value... args) {

        List<String> paramNames = params();

        if (args.length != paramNames.size()) {
          throw new ExecutionErrorException(
              "lambda expects " + paramNames.size()
                  + " args, got " + args.length);
        }

        ApplicationContext local = new ApplicationContext();
        local.snapshot().setParent(closure);
        local.pushScope();

        try {

          for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof FunctionValue fn) {
              local.defineFunction(paramNames.get(i), fn);
            } else {
              local.defineVariable(paramNames.get(i), args[i]);
            }
          }

          try {
            return body.interpret(local);
          } catch (ReturnValueException e) {
            return e.getValue();
          } catch (RuntimeException r) {
            throw r;
          }

        } finally {
          local.popScope();
        }
      }

      @Override
      public String toString() {
        return "(" + String.join(", ", params) + ") -> "
            + AstPrinter.print(body);
      }

      @Override
      public ValueType[] parameters() {
        ValueType[] types = new ValueType[params.size()];
        Arrays.fill(types, ValueType.ANY);

        return types;
      }

    };
  }

}
