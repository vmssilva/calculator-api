package com.github.vmssilva.calculator.engine.utils;

import com.github.vmssilva.calculator.engine.ast.IdentifierNode;
import com.github.vmssilva.calculator.engine.ast.LambdaNode;
import com.github.vmssilva.calculator.engine.ast.Node;
import com.github.vmssilva.calculator.engine.ast.NumberNode;

public final class AstPrinter {

  public static String print(Node node) {
    if (node instanceof IdentifierNode id) {
      return id.name();
    }

    if (node instanceof NumberNode num) {
      return num.value().toString();
    }

    if (node instanceof LambdaNode lambda) {
      String params = String.join(", ", lambda.params());
      return "(" + params + ") -> " + print(lambda.body());
    }

    return node.toString();
  }
}
