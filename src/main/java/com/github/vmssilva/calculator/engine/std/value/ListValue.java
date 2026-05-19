package com.github.vmssilva.calculator.engine.std.value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.github.vmssilva.calculator.engine.core.annotations.Expose;
import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.std.type.ValueType;

public record ListValue(List<Value> values) implements Value<List<Value>> {

  @Override
  public ValueType type() {
    return ValueType.LIST;
  }

  @Override
  public List<Value> unwrap() {
    return values;
  }

  @Override
  public final String toString() {
    return values.toString();
  }

  // =====================================
  // INFO
  // =====================================

  @Expose
  public IntValue size() {
    return new IntValue(values.size());
  }

  @Expose
  public BooleanValue isEmpty() {
    return new BooleanValue(values.isEmpty());
  }

  @Expose
  public BooleanValue contains(Value value) {
    return new BooleanValue(values.contains(value));
  }

  // =====================================
  // ACCESS
  // =====================================

  @Expose
  public Value get(IntValue index) {

    int i = index.unwrap();

    if (i < 0 || i >= values.size()) {
      throw new ExecutionErrorException(
          "Index out of bounds: " + i);
    }

    return values.get(i);
  }

  @Expose
  public ListValue add(Value value) {

    List<Value> copy = new ArrayList<>(values);
    copy.add(value);

    return new ListValue(copy);
  }

  @Expose
  public ListValue addAll(ListValue other) {

    List<Value> copy = new ArrayList<>(values);
    copy.addAll(other.values);

    return new ListValue(copy);
  }

  @Expose
  public ListValue remove(IntValue index) {

    int i = index.unwrap();

    if (i < 0 || i >= values.size()) {
      throw new ExecutionErrorException(
          "Index out of bounds: " + i);
    }

    List<Value> copy = new ArrayList<>(values);
    copy.remove(i);

    return new ListValue(copy);
  }

  // =====================================
  // HIGH ORDER
  // =====================================

  @Expose
  public final ListValue map(FunctionValue fn) {

    List<Value> l = new ArrayList<>();

    for (Value value : values) {
      Value call = fn.call(null, value);
      l.add(call);
    }

    return new ListValue(l);
  }

  @Expose
  public ListValue filter(FunctionValue fn) {

    List<Value> l = new ArrayList<>();

    for (Value value : values) {

      Value result = fn.call(null, value);

      if (!(result instanceof BooleanValue bool)) {
        throw new ExecutionErrorException(
            "filter function must return Bool");
      }

      if (bool.unwrap()) {
        l.add(value);
      }
    }

    return new ListValue(l);
  }

  @Expose
  public Value reduce(FunctionValue fn, Value initial) {

    Value acc = initial;

    for (Value value : values) {
      acc = fn.call(null, acc, value);
    }

    return acc;
  }

  @Expose
  public ListValue each(FunctionValue fn) {

    for (Value value : values) {
      fn.call(null, value);
    }

    return this;
  }

  // =====================================
  // TRANSFORM
  // =====================================

  @Expose
  public ListValue reverse() {

    List<Value> copy = new ArrayList<>(values);
    Collections.reverse(copy);

    return new ListValue(copy);
  }

  @Expose
  public ListValue sort(FunctionValue comparator) {

    List<Value> copy = new ArrayList<>(values);

    copy.sort((a, b) -> {

      Value result = comparator.call(null, a, b);

      if (!(result instanceof IntValue n)) {
        throw new ExecutionErrorException(
            "sort comparator must return Int");
      }

      return n.unwrap();
    });

    return new ListValue(copy);
  }

  @Expose
  public ListValue concat(ListValue other) {

    List<Value> copy = new ArrayList<>(values);
    copy.addAll(other.values);

    return new ListValue(copy);
  }

  // =====================================
  // STRING
  // =====================================

  @Expose
  public StringValue join(StringValue separator) {

    String sep = separator.unwrap();

    String result = values.stream()
        .map(String::valueOf)
        .collect(Collectors.joining(sep));

    return new StringValue(result);
  }
}
