package com.github.vmssilva.calculator.engine.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.github.vmssilva.calculator.engine.exception.ValueErrorException;
import com.github.vmssilva.calculator.engine.std.type.ValueType;
import com.github.vmssilva.calculator.engine.std.value.DecimalValue;
import com.github.vmssilva.calculator.engine.std.value.DoubleValue;
import com.github.vmssilva.calculator.engine.std.value.IntValue;
import com.github.vmssilva.calculator.engine.std.value.LongValue;
import com.github.vmssilva.calculator.engine.std.value.NumberValue;

public class NumericOperations {

  private NumericOperations() {
  }

  public static NumberValue add(NumberValue<?> x, NumberValue<?> y) {
    ValueType type = ValueType.promote(x, y);

    return switch (type) {
      case DECIMAL -> new DecimalValue(x.asDecimal().add(y.asDecimal()));
      case DOUBLE -> new DoubleValue(x.asDouble() + y.asDouble());
      case LONG -> new LongValue(x.asLong() + y.asLong());
      default -> new IntValue(x.asInt() + y.asInt());
    };
  }

  public static NumberValue subtract(NumberValue<?> x, NumberValue<?> y) {
    ValueType type = ValueType.promote(x, y);

    return switch (type) {
      case DECIMAL -> new DecimalValue(x.asDecimal().subtract(y.asDecimal()));
      case DOUBLE -> new DoubleValue(x.asDouble() - y.asDouble());
      case LONG -> new LongValue(x.asLong() - y.asLong());
      default -> new IntValue(x.asInt() - y.asInt());
    };
  }

  public static NumberValue multiply(NumberValue<?> x, NumberValue<?> y) {
    ValueType type = ValueType.promote(x, y);

    return switch (type) {
      case DECIMAL -> new DecimalValue(x.asDecimal().multiply(y.asDecimal()));
      case DOUBLE -> new DoubleValue(x.asDouble() * y.asDouble());
      case LONG -> new LongValue(x.asLong() * y.asLong());
      default -> new IntValue(x.asInt() * y.asInt());
    };
  }

  public static NumberValue divide(NumberValue<?> x, NumberValue<?> y) {
    if (x.asDouble() == 0)
      throw new ValueErrorException("division by zero");

    ValueType type = ValueType.promote(x, y);

    return switch (type) {
      case DECIMAL -> new DecimalValue(x.asDecimal().divide(y.asDecimal(), 10, RoundingMode.HALF_UP));
      case DOUBLE -> new DoubleValue(x.asDouble() + y.asDouble());
      case LONG -> new LongValue(x.asLong() + y.asLong());
      default -> new IntValue(x.asInt() + y.asInt());
    };
  }

  public static NumberValue<?> mod(NumberValue<?> x, NumberValue<?> y) {
    ValueType type = ValueType.promote(x, y);

    return switch (type) {
      case DECIMAL -> new DecimalValue(x.asDecimal().remainder(y.asDecimal()));
      case DOUBLE -> new DoubleValue(x.asDouble() % y.asDouble());
      case LONG -> new LongValue(x.asLong() % y.asLong());
      default -> new IntValue(x.asInt() % y.asInt());
    };
  }

  public static NumberValue<?> negate(NumberValue<?> n) {
    ValueType type = n.type();

    return switch (type) {
      case DECIMAL -> new DecimalValue(n.asDecimal().negate());
      case DOUBLE -> new DoubleValue(-n.asDouble());
      case LONG -> new LongValue(-n.asLong());
      default -> new IntValue(-n.asInt());
    };
  }

  public static NumberValue<?> min(NumberValue<?>... args) {

    if (args.length == 0)
      return new IntValue(0);

    var min = args[0];

    for (int i = 1; i < args.length; i++) {

      BigDecimal v = args[i].asDecimal();

      if (v.compareTo(min.asDecimal()) < 0) {
        min = args[i];
      }
    }

    return min;
  }

  public static NumberValue<?> max(NumberValue<?>... args) {

    if (args.length == 0)
      return new IntValue(0);

    var max = args[0];

    for (int i = 1; i < args.length; i++) {

      BigDecimal v = args[i].asDecimal();

      if (v.compareTo(max.asDecimal()) > 0) {
        max = args[i];
      }
    }

    return max;
  }

}
