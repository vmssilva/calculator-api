package com.github.vmssilva.calculator.engine.context;

import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public interface ContextCapabilities {

  void defineVariable(String name, Value value);

  void defineFunction(String name, FunctionValue function);

  FunctionValue getFunction(String name, FunctionValue fn);

  Value resolveVariable(String name);

  Value resolve(String name);

  Value resolve(String name, int arity);

  Value resolve(String name, Value... args);

  Value resolveFunction(String name, Value... args);

  void pushScope();

  void popScope();

  Scope snapshot();
}
