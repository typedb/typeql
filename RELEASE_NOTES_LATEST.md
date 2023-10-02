
## TypeQL Grammar and Language Library distributions for Rust

Available through https://crates.io/crates/typeql.
```
cargo add typeql@2.24.8
```

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
        <version>2.24.8</version>
    </dependency>
    <dependency>
        <groupId>com.vaticle.typeql</groupId>
        <artifactId>typeql-lang</artifactId>
        <version>2.24.8</version>
    </dependency>
</dependencies>
```

## TypeQL Grammar distribution for Python

Available through https://pypi.org

```
pip install typeql-grammar==2.24.8
```


## New Features


## Bugs Fixed


## Code Refactors


## Other Improvements
- **Update pest 2.4.0 => 2.7.4**
  
  We update to pest and pest-derive v2.7.4, which among other things purports to fix the error where [deriving Parser fails on "undeclared crate or module `alloc`"](https://github.com/pest-parser/pest/issues/899) (https://github.com/pest-parser/pest/pull/900).
  
  
- **Replace vaticle.com with typedb.com**

- **Update release notes**

    

