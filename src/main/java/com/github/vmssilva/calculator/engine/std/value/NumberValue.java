package com.github.vmssilva.calculator.engine.std.value;

import java.math.BigDecimal;

import com.github.vmssilva.calculator.engine.core.annotations.Expose;
import com.github.vmssilva.calculator.engine.core.lang.math.MathOperations;
import com.github.vmssilva.calculator.engine.core.lang.math.NumericOperations;

public interface NumberValue<T extends Number> extends Value<T> {

  // =========================
  // Conversions
  // =========================

  @Expose
  int asInt();

  @Expose
  long asLong();

  @Expose
  double asDouble();

  @Expose
  BigDecimal asDecimal();

  @Expose
  default IntValue intValue() {
    return new IntValue(asInt());
  }

  @Expose
  default LongValue longValue() {
    return new LongValue(asLong());
  }

  @Expose
  default DoubleValue doubleValue() {
    return new DoubleValue(asDouble());
  }

  @Expose
  default DecimalValue decimalValue() {
    return new DecimalValue(asDecimal());
  }

  // =========================
  // Core operations
  // (implementadas pelas classes concretas)
  // =========================
  @Expose
  default NumberValue<?> add(NumberValue<?> other) {
    return NumericOperations.add(this, other);
  }

  @Expose
  default NumberValue<?> sub(NumberValue<? extends Number> other) {
    return NumericOperations.subtract(this, other);
  }

  default NumberValue<?> multiply(NumberValue<? extends Number> other) {
    return NumericOperations.multiply(this, other);
  }

  @Expose
  default NumberValue<?> div(NumberValue<? extends Number> other) {
    return NumericOperations.divide(this, other);
  }

  @Expose
  default NumberValue<?> mod(NumberValue<? extends Number> other) {
    return NumericOperations.mod(this, other);
  }

  @Expose
  default NumberValue<?> negate() {
    return NumericOperations.negate(this);
  }

  @Expose
  default NumberValue<?> pow(NumberValue<?> exp) {
    return MathOperations.pow(this, exp);
  }

  // =========================
  // DEFAULTS (DERIVED OPS)
  // =========================

  @Expose
  default NumberValue<?> abs() {
    return MathOperations.abs(this);
  }

  @Expose
  default boolean isNegative() {
    return MathOperations.isNegative(this);
  }

  // =========================
  // Helpers
  // =========================

  @Expose
  default NumberValue<?> one() {
    return switch (type()) {
      case DECIMAL -> new DecimalValue(BigDecimal.ONE);
      case DOUBLE -> new DoubleValue(1D);
      case LONG -> new LongValue(1L);
      default -> new IntValue(1);
    };
  }

  @Expose
  default NumberValue<?> zero() {
    return switch (type()) {
      case DECIMAL -> new DecimalValue(BigDecimal.ZERO);
      case DOUBLE -> new DoubleValue(0D);
      case LONG -> new LongValue(0L);
      default -> new IntValue(0);
    };
  }

  @Expose
  default NumberValue<?> increment() {
    return add(one());
  }

  @Expose
  default NumberValue<?> decrement() {
    return sub(one());
  }

}
