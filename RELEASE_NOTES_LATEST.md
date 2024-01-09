
## TypeQL Grammar and Language Library distributions for Rust

Available through https://crates.io/crates/typeql.
```
cargo add typeql@2.25.8
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
        <version>2.25.8</version>
    </dependency>
    <dependency>
        <groupId>com.vaticle.typeql</groupId>
        <artifactId>typeql-lang</artifactId>
        <version>2.25.8</version>
    </dependency>
</dependencies>
```

## TypeQL Grammar distribution for Python

Available through https://pypi.org

```
pip install typeql-grammar==2.25.8
```


## New Features


## Bugs Fixed


## Code Refactors
- **Technical debt: improve error_messages, cleanup**
  
  `error_messages!` now accepts struct enum variants, rather than tuple variants. This forces the user to name the fields and to refer to the fields by name in the format strings, reducing user error.
  
  

## Other Improvements
- **Update README.md**

- **Update readme: fix the forum badge**
  
  Update the readme file to fix the forum badge.
  
  
- **Fixed badges in README.md to refer to TypeQL**


    

