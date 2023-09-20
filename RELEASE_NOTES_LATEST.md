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
- **Introduce expressions and computed value variables in typeql-rust**
  
  We introduce in `typeql-rust` the ability to perform arithmetic computation and store the results in a "value variable" - denoted by a preceding `?`, the same as introduced in `typeql-java` by https://github.com/vaticle/typeql/pull/260.
  
  All redundant parenthesis from original query will not persist in the string representation of the parsed expression anymore. If we get this query: 
  ```
  match
    $p isa person, has salary $s;
    ?net = (($s - 12500) * 0.8 + 12500);
  ```
  it will be transformed into
  ```
  match
    $p isa person, has salary $s;
    ?net = ($s - 12500) * 0.8 + 12500;
  ```
  
  
- **Implement formatted code accessor for the TypeQL/common error macro**
  
  We implement the `format_code()` accessor for the generated error types, as well as expose the `PREFIX` string.
  
- **Debug formatting Errors**
  We improve the debug formatting for macro-generated errors, by including more detail about the error. This solves issue https://github.com/vaticle/typedb-client-rust/issues/44
  
  

## Bugs Fixed
- **Sanitise rust macros to avoid forcing user's environment**
  
  Problem is described in #298: we have macros that need access to other items in our crate. Paths to these items were not absolute, and users had to import that particular items and were able to use their own objects with the same names and use it in the macros. Now all paths are absolute, based on the `$crate` metavariable.
  
  
- **Relation variable name in rule 'then' must not be present.**
  
  Check that relation variables used to infer new relations in then clause should be anonymous
  
  
- **Implement common error traits for Error**
  
  We implement cloning, equality comparison, and the standard error trait for the main `Error` type.
  
  
  
- **Fix Rust parser for definables and variables**
  
  While parsing `Variable`, `visit_pattern_variable()` panicked because of incorrect argument format. We fixed it and added unit tests. Similar errors ware while parsing `definables` and `patterns`.
  
  

## Code Refactors


## Other Improvements
- **Enable Rust crate deployment**
  
  We enable the TypeQL Rust crate deployment jobs in CI.
  
- **Remove header from template**

    

