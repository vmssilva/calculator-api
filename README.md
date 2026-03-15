# CalculatorApp – Recursive Parser Study Project

A simple arithmetic calculator built in **Java**, designed as a **study project** to explore **lexers, parsers, and abstract syntax trees (AST)**.

This project supports:

* Basic arithmetic: `+ - * / %`
* Parentheses: `()`
* Signed numbers (unary operators): `+5`, `-3`, `-(2+3)`
* Implicit multiplication: `5(2+3)` → interpreted as `5 * (2+3)`

> ⚠️ This project is intended for learning purposes and experimenting with parsing techniques, not for production use.

---

## Architecture

![Architecture](docs/images/architecture.svg)

```

Input String
│
▼
Lexer (tokenizes input) → List<Token>
│
▼
Parser (RecursiveAstParser) → AST (Expression)
│
▼
Expression.interpret() → Double

```

### Key Components:

* **Lexer (`SimpleLexer`)**
  Converts the input string into tokens (`NUMBER`, `PLUS`, `MINUS`, etc.).

* **Parser (`RecursiveAstParser`)**
  Builds an **AST** respecting operator precedence, parentheses, unary operators, and implicit multiplication.

* **AST (`Expression`)**

  * `NumberExpression` → represents a literal number
  * `BinaryExpression` → represents binary operations (`+ - * / %`)
  * `UnaryExpression` → represents unary operations (`+ -`)

---

## AST Structure

The parser generates an Abstract Syntax Tree composed of different expression types.

![AST Class Diagram](docs/images/ast-class-diagram.svg)

---
## Expression Evaluation Pipeline

![Expression Pipeline](docs/images/expression-pipeline.svg)

The evaluation process follows these stages:

1. Raw input string
2. Tokenization
3. Recursive parsing
4. AST construction
5. AST interpretation

---

## Tokenization Example

For the expression:

```

2 + 3 * 4

```

The lexer produces a token stream similar to:

![Token Stream](docs/images/token-stream.svg)

---

## AST Example

Expression:

```

5(2+3)

```

Implicit multiplication converts it internally to:

```

5 * (2 + 3)

```

AST representation:

![AST Tree](docs/images/ast-tree.svg)

Evaluation order:

```

5 + 5 = 10
5 * 10 = 50

```

---

## Recursive Parsing Flow

The recursive descent parser processes expressions using this hierarchy:

![Parsing Flow](docs/images/parsing-flow.svg)

Grammar approximation:

```

expression -> term ((+|-) term)*
term       -> factor ((*|/|%) factor)*
factor     -> NUMBER | '(' expression ')'

```

---

## Installation

This project is simple to run locally using **Java 17+**. You can compile and execute it from the command line, or import it into an IDE like **IntelliJ IDEA** or **Eclipse**.

### Clone the repository

```bash
git clone https://github.com/vmssilva/calculator-engine.git
cd calculator-engine
```

### Compile

Using **javac**:

```bash
            javac -d out src/com/github/vmssilva/calculator/engine/**/*.javaApp
```

This will compile all Java files into the `out` directory.

### Run

```bash
java -cp out com.github.vmssilva.calculator.engine.CalculatorApp
```

You should see the result of the expression(s) in the console.

### Run Tests

The project uses **JUnit 5** for testing. If you have Maven or Gradle configured:

**Using Maven:**

```bash
mvn test
```

---

💡 **Tip:** You can also run it directly from your IDE by running the `CalculatorApp` class.

---


## Usage Example

```java
import com.github.vmssilva.calculator.engine.parser.RecursiveAstParser;
import com.github.vmssilva.calculator.engine.ast.Expression;

public class CalculatorApp {
    public static void main(String[] args) {

        RecursiveAstParser parser = new RecursiveAstParser();

        Expression expr1 = parser.parse("1 + 2 * 3");
        System.out.println(expr1.interpret()); // 7.0

        Expression expr2 = parser.parse("-(2 + 3) * 4");
        System.out.println(expr2.interpret()); // -20.0

        Expression expr3 = parser.parse("5(2+3)");
        System.out.println(expr3.interpret()); // 25.0

        Expression expr4 = parser.parse("-5(2+3)");
        System.out.println(expr4.interpret()); // -25.0
    }
}
```

---

## Features

* Operator precedence correctly handled:

  * `* / %` > `+ -`
* Unary operators:

  * `+5`, `-3`, `-(1+2)`
* Implicit multiplication:

  * `5(2+3)` → automatically treated as `5 * (2+3)`
* Nested parentheses supported:

  * `((1+2)*(3+4))`
* Exception handling for malformed expressions:

  * `UnsupportedOperationException` is thrown for invalid syntax

---

## Testing

The project includes a comprehensive JUnit 5 test suite, covering:

* Addition, subtraction, multiplication, division, modulus
* Unary numbers (`+`, `-`)
* Implicit multiplication
* Nested parentheses
* Edge cases for malformed expressions

Example:

```java
assertEquals(25.0, parser.parse("5(2+3)").interpret());
assertEquals(-20.0, parser.parse("-(2+3)*4").interpret());
assertThrows(UnsupportedOperationException.class, () -> parser.parse("2++3"));
```

---

## Notes

* This project is **for study and experimentation**.
* Future improvements could include:

  * Additional operators (`^`, `sqrt`)
  * Functions (`sin`, `cos`, etc.)
  * More robust error messages with position tracking
  * Support for scientific notation (`1e3`)

---

## Requirements

* Java 17+
* JUnit 5 for running tests

---

## Summary

This project is a **learning exercise** to practice:

* Lexical analysis (tokenization)
* Recursive descent parsing
* Building and interpreting an AST
* Handling operator precedence, unary operators, and implicit multiplication

Perfect for anyone learning **compiler fundamentals** or **expression evaluation** in Java.

---

## License

This project is licensed under the **MIT License** – see the [LICENSE](LICENSE) file for details.
