package com.github.vmssilva.calculator.engine.std.functions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.vmssilva.calculator.engine.std.type.ValueType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Builtin {

  String name();

  ValueType[] parameters() default {};

  String description() default "";
}
