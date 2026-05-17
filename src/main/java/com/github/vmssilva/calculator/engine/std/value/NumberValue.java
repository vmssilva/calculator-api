package com.github.vmssilva.calculator.engine.std.value;

import java.math.BigDecimal;

import com.github.vmssilva.calculator.engine.core.lang.math.MathOperations;
import com.github.vmssilva.calculator.engine.core.lang.math.NumericOperations;

public interface NumberValue<T extends Number> extends Value<T> {

  // =========================
  // Conversions
  // =========================

  int asInt();

  long asLong();

  double asDouble();

  BigDecimal asDecimal();

  default IntValue toInt() {
    return new IntValue(asInt());
  }

  default LongValue toLong() {
    return new LongValue(asLong());
  }

  default DoubleValue toDouble() {
    return new DoubleValue(asDouble());
  }

  default DecimalValue toDecimal() {
    return new DecimalValue(asDecimal());
  }

  // =========================
  // Core operations
  // (implementadas pelas classes concretas)
  // =========================
  default NumberValue<?> add(NumberValue<?> other) {
    return NumericOperations.add(this, other);
  }

  default NumberValue<?> sub(NumberValue<? extends Number> other) {
    return NumericOperations.subtract(this, other);
  }

  default NumberValue<?> multiply(NumberValue<? extends Number> other) {
    return NumericOperations.multiply(this, other);
  }

  default NumberValue<?> div(NumberValue<? extends Number> other) {
    return NumericOperations.divide(this, other);
  }

  default NumberValue<?> mod(NumberValue<? extends Number> other) {
    return NumericOperations.mod(this, other);
  }

  default NumberValue<?> negate() {
    return NumericOperations.negate(this);
  }

  default NumberValue<?> pow(NumberValue<?> exp) {
    return MathOperations.pow(this, exp);
  }

  // =========================
  // DEFAULTS (DERIVED OPS)
  // =========================

  default NumberValue<?> abs() {
    return MathOperations.abs(this);
  }

  default boolean isNegative() {
    return MathOperations.isNegative(this);
  }

  // =========================
  // Helpers
  // =========================

  default NumberValue<?> one() {
    return switch (type()) {
      case DECIMAL -> new DecimalValue(BigDecimal.ONE);
      case DOUBLE -> new DoubleValue(1D);
      case LONG -> new LongValue(1L);
      default -> new IntValue(1);
    };
  }

  default NumberValue<?> zero() {
    return switch (type()) {
      case DECIMAL -> new DecimalValue(BigDecimal.ZERO);
      case DOUBLE -> new DoubleValue(0D);
      case LONG -> new LongValue(0L);
      default -> new IntValue(0);
    };
  }

  default NumberValue<?> increment() {
    return add(one());
  }

  default NumberValue<?> decrement() {
    return sub(one());
  }

}
