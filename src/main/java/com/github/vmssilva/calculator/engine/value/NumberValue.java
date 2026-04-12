package com.github.vmssilva.calculator.engine.value;

import java.math.BigDecimal;

public record NumberValue(BigDecimal value) implements Value {
}

// public class NumberValue implements Value {
//
// private BigDecimal value;
//
// public NumberValue(BigDecimal value) {
// this.value = value;
// }
//
// public BigDecimal get() {
// return value;
// }
//
// }
