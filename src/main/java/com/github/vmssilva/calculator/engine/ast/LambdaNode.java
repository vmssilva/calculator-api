package com.github.vmssilva.calculator.engine.ast;

import java.util.List;

import com.github.vmssilva.calculator.engine.ast.expressions.NumberExpression;
import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.context.Scope;
import com.github.vmssilva.calculator.engine.utils.Validators;
import com.github.vmssilva.calculator.engine.std.type.ValueType;
import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public record LambdaNode(List<String> params, Node body) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {

    Scope closureScope = context.currentScope();

    return new FunctionValue() {

      private final Scope closure = closureScope;

      @Override
      public Value apply(ApplicationContext ctx, List<Value> args) {

        List<String> paramNames = params();

        Validators.validate(this, args);

        // Full Application
        ApplicationContext local = new ApplicationContext();
        local.currentScope().setParent(closure);
        local.pushScope();

        try {
          for (int i = 0; i < args.size(); i++) {
            local.set(paramNames.get(i), args.get(i));
          }

          return body.interpret(local);

        } finally {
          local.popScope();
        }
        // }

        // Partial Application
        // if (args.size() < paramNames.size()) {

        // List<String> remaining = paramNames.subList(args.size(), paramNames.size());

        // Scope newClosure = new Scope(closure);

        // for (int i = 0; i < args.size(); i++) {
        // newClosure.set(paramNames.get(i), args.get(i));
        // }

        // return new BaseFunctionValue(
        // remaining.stream().map(p -> ValueType.ANY).toArray(ValueType[]::new),
        // true) {

        // private final Scope closureScope = newClosure;

        // @Override
        // public Value apply(ApplicationContext ctx, List<Value> nextArgs) {

        // ApplicationContext local = new ApplicationContext();
        // local.currentScope().setParent(closureScope);
        // local.pushScope();

        // try {
        // for (int i = 0; i < remaining.size(); i++) {
        // local.set(remaining.get(i), nextArgs.get(i));
        // }

        // return body.interpret(local); // ✔ Value direto

        // } finally {
        // local.popScope();
        // }
        // }
        // };
        // }

        // throw new RuntimeException("Too many arguments");
      }

      @Override
      public String toString() {
        return "(" + String.join(", ", params) + ") -> " + formatBody(body);
      }

      private String formatBody(Node node) {
        if (node instanceof IdentifierNode id) {

          if (context.has(id.name())) {
            return context.get(id.name()).toString();
          }
          return id.name();
        }
        if (node instanceof LambdaNode lambda) {

          String params = lambda.params().stream()
              .map(p -> p)
              .collect(java.util.stream.Collectors.joining(", "));

          return "(" + params + ") -> " + formatBody(lambda.body());
        }

        return formatNode(node);
      }

      private String formatNode(Node node) {

        if (node instanceof IdentifierNode id) {
          return id.name();
        }

        if (node instanceof NumberExpression num) {
          return num.value().toString();
        }

        return node.toString();
      }

      @Override
      public ValueType[] parameters() {
        return buildParamTypes(params);
      }

    };

  }

  private static ValueType[] buildParamTypes(List<String> params) {
    ValueType[] types = new ValueType[params.size()];

    for (int i = 0; i < params.size(); i++) {
      types[i] = ValueType.ANY;
    }

    return types;
  }

}
