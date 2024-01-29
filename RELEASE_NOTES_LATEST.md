
## TypeQL Grammar and Language Library distributions for Rust

Available through https://crates.io/crates/typeql.
```
cargo add typeql@2.26.6-rc0
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
        <version>2.26.6-rc0</version>
    </dependency>
    <dependency>
        <groupId>com.vaticle.typeql</groupId>
        <artifactId>typeql-lang</artifactId>
        <version>2.26.6-rc0</version>
    </dependency>
</dependencies>
```

## TypeQL Grammar distribution for Python

Available through https://pypi.org

```
pip install typeql-grammar==2.26.6-rc0
```


## New Features
- **Implement non-ascii variables in Java and Rust**
  We update to TypeQL with Unicode support in both value and concept variables. This makes the following valid TypeQL:
  ```
  match $人 isa person, has name "Liu"; get  $人;
  ```
  ```
  match $אדם isa person, has name "Solomon"; get $אדם;
  ```
  
  We now require all Labels and Variables are valid unicode identifiers not starting with `_`.
  
  This change is fully backwards compatible. We also validate that Type Labels and Variables created using the TypeQL language builders in both Rust and Java are conforming to our Unicode specification.
  
  

## Bugs Fixed
- **Fix snapshot version in test-deployment-maven**
  
  We update the generated snapshot version in test-deployment-maven CI job to correspond to the updated snapshot version format.
  

## Code Refactors

- **Merge typedb-common repository into typeql**

  As part of the effort to reduce the number of vaticle organization repositories, we merge typedb-common into the typeql repo as a subpackage.


## Other Improvements
- **Sync dependencies in CI**
  
  We add a sync-dependencies job to be run in CI after successful snapshot and release deployments. The job sends a request to vaticle-bot to update all downstream dependencies.
  
  Note: this PR does _not_ update the `dependencies` repo dependency. It will be updated automatically by the bot during its first pass.
  
- **Set up CI filters for master-development workflow**

- **Migrate artifact hosting to cloudsmith**
  Updates artifact deployment & consumption rules to use cloudsmith instead of the self-hosted sonatype repository.
  
  
  
  
    

