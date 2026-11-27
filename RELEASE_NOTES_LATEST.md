
## TypeQL Grammar and Language Library distributions for Rust

Available through https://crates.io/crates/typeql.
```
cargo add typeql@3.16.0-rc0
```


## New Features
- **Support vector value type**
  
  Adds support for declaring `vector`. This lets users work with embeddings, and later express vector-similarity queries.
  
  1. Declaring a vector embedding attribute 
  ```typeql
  define
  attribute embedding,
      value vector(64, "float32");
  ```
  
  2. Inserting a vector embedding attribute: using declarative syntax
  ```typeql
  insert
  $x isa document,
      has embedding vector([1.0, 2.0, 3.0], "float32");
  ```
  
  3. Inserting a vector embedding attribute: using `given` syntax
  ```typeql
  given $e: vector(64, "float32");
  insert
  $x isa document,
      has embedding $e;
  ```
  
  
## Bugs Fixed

## Code Refactors
- **Simplify workspace referencing**
  Update workspace reference setup in `MODULE.bazel` to support the new simplified approach that avoids keeping a separate dictionary of dependencies.
  
  
## Other Improvements
- **Update dependency refs**
  
  
  
    

