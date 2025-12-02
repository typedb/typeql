
## TypeQL Grammar and Language Library distributions for Rust

Available through https://crates.io/crates/typeql.
```
cargo add typeql@3.7.0
```


## New Features
- **Try blocks in write stages**
  
  We allow `try {}` blocks in all write stages, viz. `insert`, `delete`, `put`, and `update`.
  
  

## Bugs Fixed


## Code Refactors


## Other Improvements 
   
- **Change CODEOWNERS**

- **Fix illegal grammar error formatting**
  
  We fix the (internal) illegal grammar error formatting to show the part of the query that caused the error rather than the corresponding parsed subtree.
  
