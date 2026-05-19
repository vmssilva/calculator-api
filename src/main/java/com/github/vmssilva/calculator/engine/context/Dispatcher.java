package com.github.vmssilva.calculator.engine.context;

import java.util.List;

import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public class Dispatcher {

  private final Resolver resolver;

  public Dispatcher() {
    this.resolver = new Resolver();
  }

  public Value dispatch(ApplicationContext ctx, Value receiver, String method, Value... args) {

    List<FunctionValue> overloads = ctx.lookup(receiver, method);

    FunctionValue fn = resolver.resolve(overloads, args);

    return fn.call(ctx, args);
  }

  public Value dispatch(ApplicationContext context, String name, Value[] values) {
    List<FunctionValue> overloads = context.lookup(name, values);
    FunctionValue fn = resolver.resolve(overloads, values);
    return fn.call(context, values);
  }

  public Value dispatch(
      ApplicationContext ctx,
      FunctionValue fn,
      Value... args) {

    FunctionValue resolved = resolver.resolve(
        List.of(fn),
        args);

    return resolved.call(ctx, args);
  }
}
