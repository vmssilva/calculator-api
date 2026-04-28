package com.github.vmssilva.calculator.engine.value;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.std.ValueType;
import com.github.vmssilva.calculator.engine.utils.Validators;

public class BuiltinFunctionValue implements FunctionValue {

  private final FunctionValue delegate;
  private ApplicationContext context;

  public BuiltinFunctionValue(FunctionValue delegate, ApplicationContext context) {
    this.delegate = delegate;
    this.context = context;
  }

  @Override
  public Value apply(ApplicationContext ctx, List<Value> args) {
    Validators.validate(delegate, args);

    return delegate.apply(context, args);
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
  public ValueType[] parameters() {
    return delegate.parameters();
  }

  @Override
  public String name() {
    return delegate.name();
  }

}
