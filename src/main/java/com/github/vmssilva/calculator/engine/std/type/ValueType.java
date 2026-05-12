package com.github.vmssilva.calculator.engine.std.type;

import com.github.vmssilva.calculator.engine.std.value.DecimalValue;
import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.DoubleValue;
import com.github.vmssilva.calculator.engine.std.value.IntValue;
import com.github.vmssilva.calculator.engine.std.value.ListValue;
import com.github.vmssilva.calculator.engine.std.value.NumberValue;
import com.github.vmssilva.calculator.engine.std.value.StringValue;
import com.github.vmssilva.calculator.engine.std.value.UnitValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public enum ValueType {
  NUMBER(NumberValue.class, "Number"),
  DECIMAL(DecimalValue.class, "Decimal"),
  INT(IntValue.class, "Int"),
  DOUBLE(DoubleValue.class, "Double"),
  // FLOAT("Float"),
  // LONG("Long"),
  FUNCTION(FunctionValue.class, "Function"),
  ANY(Value.class, "Any"),
  STRING(StringValue.class, "String"),
  LIST(ListValue.class, "List"),
  UNIT(UnitValue.class, "Unit");

  private String value;
  private final Class<?> javaType;

  ValueType(Class<?> javaType, String value) {
    this.javaType = javaType;
    this.value = value;
  }

  public Class<?> type() {
    return javaType;
  }

  public String friendly() {
    return this.value;
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
