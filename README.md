# Calculator Engine – Recursive Parser & Runtime Study Project

A Java-based expression engine built as a **study project for parsing, AST interpretation, and runtime execution**, evolving into a lightweight **expression language runtime**.

This project explores:

* Lexical analysis (lexer)
* Recursive descent parser
* AST construction
* Expression evaluation
* Function system (builtins + lambdas)
* Closures and lexical scoping
* Runtime context (environment)

> ⚠️ This project is intended for learning purposes and experimenting with parsing techniques, not for production use.

---

## Features

### Arithmetic Engine

* `+ - * / % ^`
* Operator precedence
* Unary operators (`+`, `-`)
* Parentheses grouping

---

### Built-in Functions

#### Predicate Functions

```text
isPositive(x)
isNegative(x)
isZero(x)
```

#### Math Operations

```text
add(x, y)
subtract(x, y)
multiply(x, y)
divide(x, y)
percentage(x, y)
```

#### Aggregations

```text
sum(x1, x2, ...)
min(...)
max(...)
```

#### Advanced Math

```text
abs(x)
sqrt(x)
pow(x, y)
log(x)
log10(x)
ln(x)
exp(x)
factorial(x)
```

#### Rounding

```text
round(x)
floor(x)
ceil(x)
truncate(x)
```

#### Utilities

```text
env()     // prints runtime environment
unset(x: String)  // removes variable from current scope
```

---

### Lambda Functions

The engine supports first-class lambda expressions:

```text
(a) -> a + 10
(a, b) -> a + b
```

#### Assignment

```text
sum = (a, b) -> a + b
sum(10, 20) → 30
```

---

### Closures

Lambdas capture lexical scope:

```text
fn = (a) -> (b) -> a + b
f = fn(10)
f(5) → 15
```

`a` is preserved via closure.

---

### Higher-order functions

Functions can accept other functions:

```text
apply = (fn, x) -> fn(x)
apply((a) -> a * 2, 10) → 20
```

---

### Runtime Environment

The engine maintains a scoped runtime:

* Global scope
* Nested scopes
* Builtins scope
* Closure scopes

You can inspect it:

```text
env()
```

Example output:

```text
fn add -> (a, b) -> a + b
fn multiply -> (a, b) -> a * b
var x = 10
var fn = (a) -> a + 10
```

---

## Architecture

```
Input String
   │
   ▼
Lexer → Token Stream
   │
   ▼
Parser (RecursiveAstParser)
   │
   ▼
AST (Nodes: Binary, Unary, Lambda, Call, etc.)
   │
   ▼
Interpreter (interpret(context))
   │
   ▼
Value (Number / Function / Closure)
```

---

## Core Components

### Lexer

Tokenizes input into:

* Numbers
* Identifiers
* Operators
* Symbols

---

### Parser

Recursive descent parser that builds AST:

Supports:

* precedence rules
* function calls
* lambda expressions
* assignments

---

### AST

Main nodes:

* `NumberExpression`
* `BinaryExpression`
* `UnaryExpression`
* `IdentifierNode`
* `FunctionCallNode`
* `LambdaNode`
* `VarNode`

---

### Runtime

Execution is handled by:

```java
CalculatorRuntime
```

Features:

* scoped execution
* global + nested scopes
* function evaluation
* lambda closure support
* optional numeric scaling

---

## ⚙ Execution Pipeline

```
String Expression
   ↓
Tokenization
   ↓
AST Generation
   ↓
interpret(context)
   ↓
Value Result
```

---

## Usage Example

```java
CalculatorRuntime runtime = new CalculatorRuntime();

System.out.println(runtime.evaluate("1 + 2 * 3")); 
// 7

System.out.println(runtime.evaluate("5(2+3)"));
// 25

System.out.println(runtime.evaluate("(a) -> a + 10"));
// function

System.out.println(runtime.evaluate("((a) -> (b) -> a + b)(10)(5)"));
// 15
```

---

## Lambda Examples

### Basic

```text
(a) -> a * 2
```

---

### Assignment

```text
double = (x) -> x * 2
double(10) → 20
```

---

### Currying

```text
add = (a) -> (b) -> a + b
add(10)(5) → 15
```

---

### Higher-order

```text
apply = (fn, x) -> fn(x)
apply((a) -> a + 1, 9) → 10
```

---

## Closures

```text
fn = (a) -> (b) -> a + b
f = fn(10)
f(5)
```

`a = 10` is preserved in closure scope.

---

## Testing

JUnit 5 test coverage includes:

* arithmetic operations
* precedence rules
* lambda parsing
* function calls
* closures
* nested expressions


---

## Notes

This project evolved from a simple calculator into a **mini expression language runtime**.

It is still intended for:

* studying interpreters
* learning parsing techniques
* experimenting with language design

---

## Future Improvements

* string support
* boolean logic
* conditionals (`if`)
* loops
* typed functions
* pattern matching
* better error diagnostics (line/column)
* bytecode compilation layer

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
* Handling operator precedence

Perfect for anyone learning **compiler fundamentals** or **expression evaluation** in Java.

---

## License

This project is licensed under the **MIT License** – see the [LICENSE](LICENSE) file for details.
