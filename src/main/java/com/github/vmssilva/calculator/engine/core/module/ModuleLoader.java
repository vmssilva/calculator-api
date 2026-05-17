package com.github.vmssilva.calculator.engine.core.module;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.github.vmssilva.calculator.engine.std.functions.JavaFunction;
import com.github.vmssilva.calculator.engine.std.value.ModuleValue;

public final class ModuleLoader {

  public static ModuleValue fromClass(Class<?> clazz) {

    ModuleBuilder builder = new ModuleBuilder();

    for (Method m : clazz.getDeclaredMethods()) {

      if (!Modifier.isStatic(m.getModifiers())) {
        continue;
      }

      builder.add(
          m.getName(),
          new JavaFunction(m));
    }

    return builder.build();
  }
}
