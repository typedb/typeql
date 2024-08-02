
## TypeQL Grammar and Language Library distributions for Rust

Available through https://crates.io/crates/typeql.
```
cargo add typeql@2.28.5
```

## TypeQL Grammar and Language Library distributions for Java

```xml
<repositories>
    <repository>
        <id>repo.typedb.com</id>
        <url>https://repo.typedb.com/public/public-release/maven/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.vaticle.typeql</groupId>
        <artifactId>typeql-grammar</artifactId>
        <version>2.28.5</version>
    </dependency>
    <dependency>
        <groupId>com.vaticle.typeql</groupId>
        <artifactId>typeql-lang</artifactId>
        <version>2.28.5</version>
    </dependency>
</dependencies>
```

## TypeQL Grammar distribution for Python

Available through https://pypi.org

```
pip install typeql-grammar==2.28.5
```


## New Features


## Bugs Fixed


## Code Refactors


## Other Improvements
- **Make the author of the Python grammar and TypeQL Rust "TypeDB Community"**
  
  The `author` field of our Python grammar and Rust library is now **TypeDB Community** with the email being **community@typedb.com**.
  
  
- **Update error messages to match Rust and Java implementations**
  We fixed various logical and grammatical issues in the Java and Rust error messages, aiming to have similar errors from both implementations.
  
  
- **Java and Rust error messages: grammatical fixes**
  
  We fixed various grammatical issues in the Java and Rust error messages.
  
  
- **Update readme**
  
    

