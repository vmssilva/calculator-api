package com.github.vmssilva.calculator.engine.std.value;

import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.std.type.ValueType;

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

  public IntValue length() {
    return new IntValue(value.length());
  }

  public BooleanValue isEmpty() {
    return new BooleanValue(value.isEmpty());
  }

  public BooleanValue contains(StringValue other) {
    return new BooleanValue(value.contains(other.value));
  }

  public BooleanValue startsWith(StringValue other) {
    return new BooleanValue(value.startsWith(other.value));
  }

  public BooleanValue endsWith(StringValue other) {
    return new BooleanValue(value.endsWith(other.value));
  }

  // =====================================
  // TRANSFORM
  // =====================================

  public StringValue upper() {
    return new StringValue(value.toUpperCase());
  }

  public StringValue lower() {
    return new StringValue(value.toLowerCase());
  }

  public StringValue trim() {
    return new StringValue(value.trim());
  }

  public StringValue repeat(IntValue count) {
    return new StringValue(value.repeat(count.unwrap()));
  }

  public StringValue replace(
      StringValue target,
      StringValue replacement) {

    return new StringValue(
        value.replace(
            target.value,
            replacement.value));
  }

  // =====================================
  // ACCESS
  // =====================================

  public StringValue charAt(IntValue index) {

    int i = index.unwrap();

    if (i < 0 || i >= value.length()) {
      throw new ExecutionErrorException(
          "Index out of bounds: " + i);
    }

    return new StringValue(
        String.valueOf(value.charAt(i)));
  }

  public StringValue substring(IntValue begin) {
    return new StringValue(
        value.substring(begin.unwrap()));
  }

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

  public StringValue concat(Value<?> other) {
    return new StringValue(
        value + other.unwrap());
  }

  public StringValue plus(Value<?> other) {
    return concat(other);
  }

  // =====================================
  // COMPARE
  // =====================================

  public BooleanValue equalsTo(Value<?> other) {

    if (!(other instanceof StringValue str)) {
      return new BooleanValue(false);
    }

    return new BooleanValue(
        value.equals(str.value));
  }

  public IntValue compareTo(StringValue other) {
    return new IntValue(
        value.compareTo(other.value));
  }

}
