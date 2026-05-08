package com.github.vmssilva.calculator.engine.std.functions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.std.type.ValueType;
import com.github.vmssilva.calculator.engine.utils.Validators;
import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public class BuiltinFunction implements FunctionValue, Callable {

  private ApplicationContext context;
  private FunctionValue delegate;

  public BuiltinFunction(FunctionValue delegate, ApplicationContext context) {
    this.delegate = delegate;
    this.context = context;
  }

  @Override
  public Value apply(ApplicationContext ctx, List<Value> args) {
    Validators.validate(delegate, args);
    return delegate.apply(context, args);
  }

  @Override
  public ValueType[] parameters() {
    return delegate.parameters();
  }

  @Override
  public String toString() {
    return name() + "(" +
        Arrays.asList(parameters()).stream()
            .map(p -> p.value())
            .collect(Collectors.joining(", "))
        + ")";
  }

  @Override
  public String name() {
    return delegate.name();
  }

  @Override
  public FunctionValue unwrap() {
    return delegate;
  }

}
