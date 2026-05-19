package com.github.vmssilva.calculator.engine.std.functions;

import java.util.ArrayList;
import java.util.List;

import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.std.value.BooleanValue;
import com.github.vmssilva.calculator.engine.std.value.FunctionValue;
import com.github.vmssilva.calculator.engine.std.value.IntValue;
import com.github.vmssilva.calculator.engine.std.value.ListValue;
import com.github.vmssilva.calculator.engine.std.value.NumberValue;
import com.github.vmssilva.calculator.engine.std.value.StringValue;
import com.github.vmssilva.calculator.engine.std.value.Value;

public final class QueryBuiltins {

  private QueryBuiltins() {
  }

  // =====================================
  // SOURCE
  // =====================================

  @Builtin(name = "from", description = "Create query source")
  public static Value from(
      ApplicationContext context,
      ListValue list) {

    return list;
  }

  // =====================================
  // FILTER
  // =====================================

  @Builtin(name = "where", description = "Filter values")
  public static Value where(
      ApplicationContext context,
      ListValue list,
      FunctionValue predicate) {

    List<Value> result = new ArrayList<>();

    for (Value value : list.values()) {

      Value test = predicate.call(context, value);

      if (!(test instanceof BooleanValue bool)) {
        throw new RuntimeException(
            "where predicate must return Bool");
      }

      if (bool.unwrap()) {
        result.add(value);
      }
    }

    return new ListValue(result);
  }

  // =====================================
  // MAP
  // =====================================

  @Builtin(name = "select", description = "Map values")
  public static Value select(
      ApplicationContext context,
      ListValue list,
      FunctionValue mapper) {

    List<Value> result = new ArrayList<>();

    for (Value value : list.values()) {

      Value mapped = mapper.call(context, value);

      result.add(mapped);
    }

    return new ListValue(result);
  }

  // =====================================
  // TAKE
  // =====================================

  @Builtin(name = "take", description = "Take first N values")
  public static Value take(
      ApplicationContext context,
      ListValue list,
      IntValue count) {

    List<Value> result = new ArrayList<>();

    int max = Math.min(count.unwrap(), list.values().size());

    for (int i = 0; i < max; i++) {
      result.add(list.values().get(i));
    }

    return new ListValue(result);
  }

  // =====================================
  // COUNT
  // =====================================

  @Builtin(name = "count", description = "Count values")
  public static Value count(
      ApplicationContext context,
      ListValue list) {

    return new IntValue(list.values().size());
  }

  // =====================================
  // SUM
  // =====================================

  @Builtin(name = "sum", description = "Sum numbers")
  public static Value sum(
      ApplicationContext context,
      ListValue list) {

    NumberValue<?> total = new IntValue(0);

    for (Value value : list.values()) {

      if (!(value instanceof NumberValue<?> n)) {
        throw new RuntimeException(
            "sum only accepts numbers");
      }

      total = total.add(n);
    }

    return total;
  }

  // =====================================
  // PRINT
  // =====================================

  @Builtin(name = "print", description = "Print value")
  public static Value print(
      ApplicationContext context,
      Value value) {

    System.out.println(value);

    return value;
  }

  // =====================================
  // TO STRING
  // =====================================

  @Builtin(name = "str", description = "Convert value to string")
  public static Value str(
      ApplicationContext context,
      Value value) {

    return new StringValue(
        String.valueOf(value));
  }
}
