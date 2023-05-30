# TypeQL

This release includes exciting changes such as expression-based computation (for an initially limited set of functions) and new annotation for attribute ownership: `@unique`.

## TypeQL Grammar and Language Library distributions for Java

```xml
<repositories>
    <repository>
        <id>repo.vaticle.com</id>
        <url>https://repo.vaticle.com/repository/maven/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.vaticle.typeql</groupId>
        <artifactId>typeql-grammar</artifactId>
        <version>{version}</version>
    </dependency>
    <dependency>
        <groupId>com.vaticle.typeql</groupId>
        <artifactId>typeql-lang</artifactId>
        <version>{version}</version>
    </dependency>
</dependencies>
```

## TypeQL Grammar distribution for Python

Available through https://pypi.org

```
pip install typeql-grammar=={version}
```


## New Features
- **Introduce expressions and computed value variables**
  
  We introduce the ability to perform arithmetic computation and store the results in a 'value variable' - denoted by a preceding `?`. For example:
  ```typeql
  match
    $x isa triangle, has base $b, has height $h;
    ?area = 0.5 * $b * $h;
  ```
  
  Or through the java api:
  ```java
  TypeQLMatch query = match(
          cVar("x").isa("triangle").has("base", cVar("b")).has("height", cVar("h")),
          vVar("area").assign(Expression.constant(0.5).mul(cVar("b").mul(cVar("h"))))))
  );
  ```
  
  Rules now also support value-variable conclusions:
  ```
  define
  rule computation-cost-dollars-to-pounds: when {
    $c isa computation, has cost-dollars $c;
    $_ isa exchange-rate, has name "dollar-pound", has rate $rate;
    ?pounds = $rate * $c;
  } then {
    $c has cost-pounds ?pounds;
  };
  ```
  
  The expression on the right hand side of an assignment can be functions, operations, variables or constants:
  * This PR implements infix operators: `+`, `-`, `*`, `/`, `^`, `%`, 
  * This PR implements prefix functions: `min`, `max`, `floor`, `ceil`, `round`, `abs`
  * This PR implements parantheses: `(...)` and instantiation of constants eg. `12`, `0.25`, `false`, `"abc"`, etc.
  
  These constructs are currently defined on double and long valued Attribute instances or Value instances.
  
  The language implements order-of-operations for infix operators in this order: `()`, `^`, `*`, `|`, `%`, `+`, `-`.
  
  **Deprecation warnings**
  * Using `=` can should no longer be used to denote value-equality. `=` now represents value-assignment, with `==` representing value equality. For the time being, concept variables `$x` will still support the old syntax, `=` and the new `==`, however expect `$x = ` to be removed from the language in future releases.
  
  **Breaking changes**
  * `var` is no longer part of the TypeQL builder API, being replaced by `cVar` to create concept variables (`$x`) and `vVar` to create a value variable (`?y`).
  
  
- **Implement unique annotation in TypeQL Rust**
  
  We generalise the annotation syntax and parsing to be able to handle a new type of annotation: the `@unique` annotation, which is available only on the owns constraint: `define person sub entity, owns email @unique;`
  
  The `@unique` annotation has been introduced first in TypeQL Java in https://github.com/vaticle/typeql/pull/273.
  
  
- **Introduce unique annotation in TypeQL Java**
  
  We generalise the annotation syntax and parsing to be able to handle a new type of annotation: the `@unique` annotation, which is available only on the `owns` constraint:
  
  ```
  define
  person sub entity, owns email @unique;
  email sub attribute, value string;
  ```
  
  This annotation indicates that any `email`s a person owns must be unique to that person. It does not place any restrictions on the number of emails any given person may own.
  
  The language builder API has also been updated to use a generalised form of any number of annotations, rather than having a particular boolean per annotation type (now, pass annotation `UNIQUE` or `KEY` instead of booleans).
  
  

## Bugs Fixed
- **Fix rule validation**
  
  While parsing a rule with `has` and without relation in `then` part `expect_valid_inference()` function panicked. Now it returns an `Error`.
  
  
- **Fix TypeQL Python build**
  
  We fix the issue with grammar-python producing empty pip packages.
  
  

## Code Refactors
- **Remove unnecessary parentheses**
  
  As per the Rust compiler:
  ```
  warning: unnecessary parentheses around type
  
      |
  306 | impl<const N: usize> From<([(&str, token::Order); N])> for Sorting {
      |                           ^                         ^
      |
      = note: `#[warn(unused_parens)]` on by default
  help: remove these parentheses
      |
  306 - impl<const N: usize> From<([(&str, token::Order); N])> for Sorting {
  306 + impl<const N: usize> From<[(&str, token::Order); N]> for Sorting {
      |
  ```
  
  

## Other Improvements

- **Update release notes workflow**
  
  We integrate the new release notes tooling. The release notes are now to be written by a person and committed to the repo.
  
  
- **Don't use bazel-cache for building all targets, which includes Python targets**

- **Set up remote bazel cache**

    

