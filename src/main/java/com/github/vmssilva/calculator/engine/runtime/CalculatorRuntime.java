package com.github.vmssilva.calculator.engine.runtime;

import java.math.RoundingMode;
import java.util.Objects;

import com.github.vmssilva.calculator.engine.ast.Node;
import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.exception.CalculatorLexerException;
import com.github.vmssilva.calculator.engine.exception.CalculatorParserException;
import com.github.vmssilva.calculator.engine.exception.ExecutionErrorException;
import com.github.vmssilva.calculator.engine.exception.ValueErrorException;
import com.github.vmssilva.calculator.engine.parser.RecursiveAstParser;
import com.github.vmssilva.calculator.engine.std.value.DecimalValue;
import com.github.vmssilva.calculator.engine.std.value.NumberValue;
import com.github.vmssilva.calculator.engine.std.value.Value;
import com.github.vmssilva.calculator.engine.std.value.Values;

public class CalculatorRuntime {

  private final ApplicationContext context;

  public CalculatorRuntime(ApplicationContext context) {
    this.context = Objects.requireNonNull(context);
  }

  public CalculatorRuntime() {
    this(new ApplicationContext());
  }

  public void run(Node ast) {
    var node = ast.interpret(context);
    System.out.println(Values.asDecimal(node));
  }

  public Value evaluate(String expression, ApplicationContext context) {
    try {
      var result = new RecursiveAstParser()
          .parse(expression)
          .interpret(context);

      if (result instanceof NumberValue && context.hasVariable("scale")) {
        int scale = Values.asDecimal(context.resolve("scale")).intValue();
        result = new DecimalValue(Values.asDecimal(result).setScale(scale, RoundingMode.HALF_UP));
      }

      return result;

    } catch (ValueErrorException | ExecutionErrorException | CalculatorParserException | CalculatorLexerException e) {
      throw new ExecutionErrorException(e.getMessage());
    } catch (ArrayIndexOutOfBoundsException ex) {
      throw new ExecutionErrorException(ex.getMessage());
    } catch (RuntimeException rt) {
      rt.printStackTrace();
      throw new ExecutionErrorException(rt.getMessage());
    }
  }

  public Value evaluate(String expression) {
    return evaluate(expression, this.context);
  }

}
