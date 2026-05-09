package com.github.vmssilva.calculator.engine.std.functions;

import java.util.List;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.std.type.ValueType;
import com.github.vmssilva.calculator.engine.utils.Validators;
import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public class BuiltinFunction implements FunctionValue {

  private ApplicationContext context;
  private FunctionValue delegate;

  public BuiltinFunction(FunctionValue delegate, ApplicationContext context) {
    this.delegate = delegate;
    this.context = context;
  }

  @Override
  public Value call(ApplicationContext ctx, List<Value> args) {
    Validators.validate(delegate, args);
    return delegate.call(context, args);
  }

  @Override
  public ValueType[] parameters() {
    return delegate.parameters();
  }

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public String name() {
    return delegate.name();
  }

  @Override
  public FunctionValue unwrap() {
    return delegate.unwrap();
  }

}
