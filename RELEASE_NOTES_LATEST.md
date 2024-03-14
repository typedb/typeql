
## TypeQL Grammar and Language Library distributions for Rust

Available through https://crates.io/crates/typeql.
```
cargo add typeql@2.27.0-rc0
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
        <version>2.27.0-rc0</version>
    </dependency>
    <dependency>
        <groupId>com.vaticle.typeql</groupId>
        <artifactId>typeql-lang</artifactId>
        <version>2.27.0-rc0</version>
    </dependency>
</dependencies>
```

## TypeQL Grammar distribution for Python

Available through https://pypi.org

```
pip install typeql-grammar==2.27.0-rc0
```


## New Features


## Bugs Fixed


## Code Refactors
- **Refactor TypeQL Java projection builder**
  
  We note a previous change in [2eef07d388391e073cc1631f5af2bbf15e844cc4](https://github.com/vaticle/typeql/commit/2eef07d388391e073cc1631f5af2bbf15e844cc4) and extend it here to refactor the TypeQL Fetch projection query builder:
  
  Usage rename, before:
  ```
  cVar("x").map("name")
  label("subquery").map(TypeQL.match(...).fetch(...))
  ```
  
  Usage now: 
  ```
  cVar("x").fetch("name")
  label("subquery").fetch(TypeQL.match(...).fetch(...))
  ```
  
  
  Fetching multiple attributes without relabeling, before:
  ```
  cVar("x").fetch(list(pair("name", null), pair("age", null), pair("dob", null)))
  ```
  Usage now:
  ```
  cVar("x").fetch("name", "age", "dob")
  ```

## Other Improvements
- **Add helper method to create Sorting modifier with just one argument**

- **ProjectionBuilder for fetch queries**

- **Renamed projection builder 'map()' to 'fetch()' and dissolved Stream overload**

