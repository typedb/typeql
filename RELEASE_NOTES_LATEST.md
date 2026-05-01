
## TypeQL Grammar and Language Library distributions for Rust

Available through https://crates.io/crates/typeql.
```
cargo add typeql@3.10.4
```


## New Features


## Bugs Fixed


## Code Refactors


## Other Improvements
- **Allow leading comma in redefine constraints**
  
  Mirrors the leading-comma sugar already accepted by `define`: `define person, owns name;` parses, but `redefine person, owns name @card(0..10);` did not. Make `redefinable_type` accept an optional COMMA between the label and the constraint so concat-based query generation has the same shape for define and redefine.
  
  
- **Update Rust dependencies**
  
  
  
    

