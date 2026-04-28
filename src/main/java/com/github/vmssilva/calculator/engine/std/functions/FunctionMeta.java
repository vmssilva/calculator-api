package com.github.vmssilva.calculator.engine.std.functions;

import java.util.List;

import com.github.vmssilva.calculator.engine.std.ValueType;

public record FunctionMeta(
    String name,
    List<Parameter> params,
    String description,
    boolean safe) {

  public static FunctionMeta ofBinary(String name, String description) {
    return ofBinary(name, description, false);
  }

  public static FunctionMeta ofUnary(String name, String description) {
    return ofUnary(name, description, false);
  }

  public static FunctionMeta ofBinary(String name, String description, boolean safe) {
    return new FunctionMeta(
        name,
        List.of(
            new Parameter("x", ValueType.NUMBER),
            new Parameter("y", ValueType.NUMBER)),
        description,
        safe);
  }

  public static FunctionMeta ofUnary(String name, String description, boolean safe) {
    return new FunctionMeta(
        name,
        List.of(
            new Parameter("x", ValueType.NUMBER)),
        description,
        safe);
  }

}
