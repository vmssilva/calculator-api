package com.github.vmssilva.calculator.engine.std.value;

import java.math.BigDecimal;

import com.github.vmssilva.calculator.engine.core.annotations.Expose;
import com.github.vmssilva.calculator.engine.std.type.ValueType;

@Expose
public record IntValue(Integer value) implements NumberValue<Integer> {

  @Override
  public final String toString() {
    return value.toString();
  }

  @Override
  public ValueType type() {
    return ValueType.INT;
  }

  @Override
  public Integer unwrap() {
    return value;
  }

  @Override
  @Expose
  public int asInt() {
    return value;
  }

  @Override
  @Expose
  public long asLong() {
    return value.longValue();
  }

  @Override
  @Expose
  public double asDouble() {
    return value.doubleValue();
  }

  @Override
  @Expose
  public BigDecimal asDecimal() {
    return new BigDecimal(value);
  }

}
