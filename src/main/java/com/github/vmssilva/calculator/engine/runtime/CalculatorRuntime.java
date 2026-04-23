package com.github.vmssilva.calculator.engine.runtime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import com.github.vmssilva.calculator.engine.ast.Node;
import com.github.vmssilva.calculator.engine.context.ApplicationContext;
import com.github.vmssilva.calculator.engine.exception.CalculatorException;
import com.github.vmssilva.calculator.engine.exception.CalculatorLexerException;
import com.github.vmssilva.calculator.engine.exception.CalculatorParserException;
import com.github.vmssilva.calculator.engine.exception.CalculatorRuntimeException;
import com.github.vmssilva.calculator.engine.parser.RecursiveAstParser;
import com.github.vmssilva.calculator.engine.value.Values;

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
    System.out.println(Values.asNumber(node));
  }

  public BigDecimal evaluate(String expression, ApplicationContext context) throws CalculatorException {

    try {
      BigDecimal result = Values.asNumber(new RecursiveAstParser().parse(expression).interpret(context));
      int scale = result.scale();

      if (context.has("scale"))
        scale = Values.asNumber(context.get("scale")).intValue();

      return result.setScale(scale, RoundingMode.HALF_UP);

    } catch (CalculatorRuntimeException re) {
      throw new CalculatorRuntimeException(re.getMessage());
    } catch (CalculatorParserException pe) {
      throw new CalculatorParserException(pe.getMessage());
    } catch (CalculatorLexerException le) {
      throw new CalculatorLexerException(le.getMessage());
    } catch (RuntimeException e) {
      throw new CalculatorException("Unknon error");
    }

  }

  public BigDecimal evaluate(String expression) {
    return evaluate(expression, this.context);
  }

}
