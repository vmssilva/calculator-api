package com.github.vmssilva.calculator.engine.ast;

import java.util.List;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.value.BaseFunctionValue;
import com.github.vmssilva.calculator.engine.value.Value;

public record LambdaNode(List<String> params, Node body) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {

    return new BaseFunctionValue() {

      @Override
      public Value apply(List<Value> args) {
        context.pushScope();

        try {
          for (int i = 0; i < params.size(); i++) {
            context.set(params.get(i), args.get(i));
          }

          return body.interpret(context);
        } finally {
          context.popScope();
        }
      }

      @Override
      public String toString() {
        return "(" + String.join(", ", params) + ") -> " + body;
      }
    };
  }
}
