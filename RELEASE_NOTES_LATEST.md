
## TypeQL Grammar and Language Library distributions for Rust

Available through https://crates.io/crates/typeql.
```
cargo add typeql@3.0.5
```

## New Features


## Bugs Fixed


## Code Refactors
- **Enhance error printing and query source span availability**
  
  We improve the error messages to show a `^` column indicator along with `-->` line indicator:
  ```
          define
          attribute name value string;
  -->     entity person owns name @range(0..10);
                                  ^
  ```
  
  We also expose more information about where in the original query spans which sourced various internal data structures.
  
  
  

## Other Improvements

    

