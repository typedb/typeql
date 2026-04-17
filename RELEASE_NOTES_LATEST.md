
## TypeQL Grammar and Language Library distributions for Rust

Available through https://crates.io/crates/typeql.
```
cargo add typeql@3.8.4-rc0
```


## New Features
- **Implement unicode unescaping**
  Escaped unicode characters can be used in TypeQL string literals. These must be of the form `\uXXXX` (exactly 4 hex digits), or `\u{XX...X}` (1 to 6 hex digits).
  
  

## Bugs Fixed


## Code Refactors


## Other Improvements
- **Fix parser for inserting with anonymous relation syntax**
  Fixes a bug in parsing for inserting with anonymous relation syntax
  
  
- **Minor grammar refactor for parser performance improvements**
  Reordering the choices to boost performance by failing faster and having more frequent alternatives earlier. Also removes the `!reserved` check for identifiers and expects this to be handled in an application post-check.
  
    

