package com.github.vmssilva.calculator.engine.std.value;

import com.github.vmssilva.calculator.engine.core.annotations.Expose;
import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.std.type.ValueType;

import java.util.ArrayList;
import java.util.List;

public record StringValue(String value) implements Value<String> {

  @Override
  public ValueType type() {
    return ValueType.STRING;
  }

  @Override
  public String unwrap() {
    return value;
  }

  @Override
  public final String toString() {
    return value;
  }

  // =====================================
  // INFO
  // =====================================

  @Expose
  public IntValue length() {
    return new IntValue(value.length());
  }

  @Expose
  public BooleanValue isEmpty() {
    return new BooleanValue(value.isEmpty());
  }

  @Expose
  public BooleanValue contains(StringValue other) {
    return new BooleanValue(value.contains(other.value));
  }

  @Expose
  public BooleanValue startsWith(StringValue other) {
    return new BooleanValue(value.startsWith(other.value));
  }

  @Expose
  public BooleanValue endsWith(StringValue other) {
    return new BooleanValue(value.endsWith(other.value));
  }

  // =====================================
  // TRANSFORM
  // =====================================

  @Expose
  public StringValue upper() {
    return new StringValue(value.toUpperCase());
  }

  @Expose
  public StringValue lower() {
    return new StringValue(value.toLowerCase());
  }

  @Expose
  public StringValue trim() {
    return new StringValue(value.trim());
  }

  @Expose
  public StringValue repeat(IntValue count) {
    return new StringValue(value.repeat(count.unwrap()));
  }

  @Expose
  public StringValue replace(
      StringValue target,
      StringValue replacement) {

    return new StringValue(
        value.replace(
            target.value,
            replacement.value));
  }

  @Expose
  public ListValue chars() {

    List<Value> characters = new ArrayList<>();

    for (int i = 0; i < value.length(); i++) {
      characters.add(new StringValue(String.valueOf(value.charAt(i))));
    }

    return new ListValue(characters);
  }
  // =====================================
  // ACCESS
  // =====================================

  @Expose
  public StringValue charAt(IntValue index) {

    int i = index.unwrap();

    if (i < 0 || i >= value.length()) {
      throw new ExecutionErrorException(
          "Index out of bounds: " + i);
    }

    return new StringValue(
        String.valueOf(value.charAt(i)));
  }

  @Expose
  public StringValue substring(IntValue begin) {
    return new StringValue(
        value.substring(begin.unwrap()));
  }

  @Expose
  public StringValue substring(
      IntValue begin,
      IntValue end) {

    return new StringValue(
        value.substring(
            begin.unwrap(),
            end.unwrap()));
  }

  // =====================================
  // CONCAT
  // =====================================

  @Expose
  public StringValue concat(Value<?> other) {
    return new StringValue(
        value + other.unwrap());
  }

  @Expose
  public StringValue plus(Value<?> other) {
    return concat(other);
  }

  // =====================================
  // COMPARE
  // =====================================

  @Expose
  public BooleanValue equals(Value<?> other) {
    return equalsTo(other);
  }

  @Expose
  public BooleanValue equalsTo(Value<?> other) {

    if (!(other instanceof StringValue str)) {
      return new BooleanValue(false);
    }

    return new BooleanValue(
        value.equals(str.value));
  }

  @Expose
  public IntValue compareTo(StringValue other) {
    return new IntValue(
        value.compareTo(other.value));
  }

}
