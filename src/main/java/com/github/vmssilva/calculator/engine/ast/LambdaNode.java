package com.github.vmssilva.calculator.engine.ast;

import java.util.List;

import com.github.vmssilva.calculator.engine.ast.expressions.NumberExpression;
import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.context.Scope;
import com.github.vmssilva.calculator.engine.std.ValueType;
import com.github.vmssilva.calculator.engine.value.BaseFunctionValue;
import com.github.vmssilva.calculator.engine.value.Value;

public record LambdaNode(List<String> params, Node body) implements Node {

  @Override
  public Value interpret(ApplicationContext context) {

    Scope closureScope = context.currentScope();

    return new BaseFunctionValue(buildParamTypes(params), false) {

      private final Scope closure = closureScope;

      @Override
      public Value apply(ApplicationContext ctx, List<Value> args) {

        List<String> paramNames = params();

        // -------------------------
        // FULL APPLICATION
        // -------------------------
        if (args.size() == paramNames.size()) {

          ApplicationContext local = new ApplicationContext();
          local.currentScope().setParent(closure);
          local.pushScope();

          try {
            for (int i = 0; i < args.size(); i++) {
              local.set(paramNames.get(i), args.get(i));
            }

            return body.interpret(local); // ✔ retorna Value

          } finally {
            local.popScope();
          }
        }

        // -------------------------
        // PARTIAL APPLICATION
        // -------------------------
        if (args.size() < paramNames.size()) {

          List<String> remaining = paramNames.subList(args.size(), paramNames.size());

          Scope newClosure = new Scope(closure);

          for (int i = 0; i < args.size(); i++) {
            newClosure.set(paramNames.get(i), args.get(i));
          }

          return new BaseFunctionValue(
              remaining.stream().map(p -> ValueType.ANY).toArray(ValueType[]::new),
              true) {

            private final Scope closureScope = newClosure;

            @Override
            public Value apply(ApplicationContext ctx, List<Value> nextArgs) {

              ApplicationContext local = new ApplicationContext();
              local.currentScope().setParent(closureScope);
              local.pushScope();

              try {
                for (int i = 0; i < remaining.size(); i++) {
                  local.set(remaining.get(i), nextArgs.get(i));
                }

                return body.interpret(local); // ✔ Value direto

              } finally {
                local.popScope();
              }
            }
          };
        }

        throw new RuntimeException("Too many arguments");
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

        // qualquer outro Node (Binary, Call, etc.)
        return node.toString();
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
  // @Override
  // public Value interpret(ApplicationContext context) {

  // // 🔥 CAPTURA LEXICAL SCOPE (closure)
  // final Scope closureScope = context.currentScope();

  // return new FunctionValue() {

  // @Override
  // public Value apply(ApplicationContext ignored, List<Value> args) {

  // Validators.validate(this, args);

  // // 🔥 novo contexto de execução
  // ApplicationContext local = new ApplicationContext();

  // // 🔥 conecta closure (lexical environment)
  // local.currentScope().setParent(closureScope);

  // // 🔥 cria frame de execução
  // local.pushScope();

  // try {
  // // bind de parâmetros
  // for (int i = 0; i < params.size(); i++) {
  // local.set(params.get(i), args.get(i));
  // }

  // return body.interpret(local);

  // } finally {
  // local.popScope();
  // }
  // }

  // // -------------------------
  // // metadata / debug
  // // -------------------------
  // @Override
  // public ValueType type() {
  // return ValueType.FUNCTION;
  // }

  // @Override
  // public ValueType[] parameters() {
  // ValueType[] types = new ValueType[params.size()];
  // for (int i = 0; i < types.length; i++) {
  // types[i] = ValueType.ANY;
  // }
  // return types;
  // }

  // @Override
  // public String name() {
  // return "<lambda>";
  // }

  // @Override
  // public String toString() {
  // return format();
  // }

  // private String format() {
  // return "(" + String.join(", ", params) + ") -> " + formatBody(body);
  // }

  // private String formatBody(Node node) {

  // if (node instanceof LambdaNode lambda) {

  // String params = lambda.params().stream()
  // .map(p -> p + ": Any")
  // .collect(Collectors.joining(", "));

  // return "(" + params + ") -> " + formatBody(lambda.body());
  // }

  // // fallback seguro para qualquer outro tipo de AST
  // return node.toString();
  // }

  // };
  // }
}
