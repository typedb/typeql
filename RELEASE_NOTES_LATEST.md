
## TypeQL Grammar and Language Library distributions for Rust

Available through https://crates.io/crates/typeql.
```
cargo add typeql@3.1.0-rc0
```


## New Features
- **3.0 distinct**

  Added the `distinct` query pipeline stage.

## Bugs Fixed
- **Allow duration literals without date component**
  
  We modify the grammar to accept a `duration_literal` without a date component (e.g. `PT1S`) as per the standard. 
  
  

## Code Refactors
- **Restrict by grammar refactoring type_ref and named_type**
  Restrict by grammar refactoring type_ref and named_type
  
  

## Other Improvements
   
- **Rustfmt**

- **Simplify formatting**

- **remove py env**

- **spanned for distinct**

- **Grammar fix**
    

