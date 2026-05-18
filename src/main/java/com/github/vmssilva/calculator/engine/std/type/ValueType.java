package com.github.vmssilva.calculator.engine.std.type;

import com.github.vmssilva.calculator.engine.std.value.DecimalValue;
import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.DoubleValue;
import com.github.vmssilva.calculator.engine.std.value.IntValue;
import com.github.vmssilva.calculator.engine.std.value.ListValue;
import com.github.vmssilva.calculator.engine.std.value.LongValue;
import com.github.vmssilva.calculator.engine.std.value.ModuleValue;
import com.github.vmssilva.calculator.engine.std.value.NumberValue;
import com.github.vmssilva.calculator.engine.std.value.StringValue;
import com.github.vmssilva.calculator.engine.std.value.UnitValue;
import com.github.vmssilva.calculator.engine.std.value.Value;
import com.sun.jdi.BooleanValue;

public enum ValueType {
  FUNCTION(FunctionValue.class, "Function", 0),
  ANY(Value.class, "Any", 0),
  STRING(StringValue.class, "String", 0),
  LIST(ListValue.class, "List", 0),
  UNIT(UnitValue.class, "Unit", 0),
  // Numbers
  NUMBER(NumberValue.class, "Number", 0),
  INT(IntValue.class, "Int", 1),
  LONG(LongValue.class, "Long", 2),
  DOUBLE(DoubleValue.class, "Double", 3),
  DECIMAL(DecimalValue.class, "Decimal", 4),
  MODULE(ModuleValue.class, "Module", 0),
  BOOLEAN(BooleanValue.class, "Boolean", 0);

  private String value;
  private final Class<?> javaType;
  private final int rank;

  ValueType(Class<?> javaType, String value, int rank) {
    this.javaType = javaType;
    this.value = value;
    this.rank = rank;
  }

  public static ValueType promote(
      NumberValue<? extends Number> a,
      NumberValue<? extends Number> b) {

    return a.type().rank() > b.type().rank()
        ? a.type()
        : b.type();
  }

  public Class<?> type() {
    return javaType;
  }

  public String friendly() {
    return this.value;
  }

  public int rank() {
    return this.rank;
  }

  public boolean accepts(Value<?> value) {

    if (this == ANY) {
      return true;
    }

    return javaType.isAssignableFrom(value.getClass());
  }

  public static ValueType fromJava(Class<?> type) {

    if (type.isArray()) {
      type = type.getComponentType();
    }

    for (ValueType value : values()) {
      if (value.javaType.equals(type)) {
        return value;
      }
    }

    throw new IllegalArgumentException(
        "Unsupported parameter type: " + type.getSimpleName());
  }
}
