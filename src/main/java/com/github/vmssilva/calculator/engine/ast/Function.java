package com.github.vmssilva.calculator.engine.ast;

import java.math.BigDecimal;
import java.util.List;

import com.github.vmssilva.calculator.engine.value.Value;

public interface Function {
  BigDecimal apply(List<Value> args);
}
