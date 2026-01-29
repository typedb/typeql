
## TypeQL Grammar and Language Library distributions for Rust

Available through https://crates.io/crates/typeql.
```
cargo add typeql@3.8.0-rc0
```


## New Features
- **Add built-in functions iid() and label()**
  
  We add `iid()` and `label()` to the built-in functions in the TypeQL grammar.
  
  

## Bugs Fixed
- **Use XID_START and XID_CONTINUE character classes**
  
  We use `XID_START` and `XID_CONTINUE` character classes provided by pest instead of manually listing unicode codepoint ranges for our identifiers. We also change the definition of the word boundary (`WB`) to be "any character that is not an identifier continuation character" that resolves parsing bugs such as `let $var= 4;` failing to parse: `=` is not `PUNCTUATION`, so not valid word boundary.
  
  

## Code Refactors


## Other Improvements
- **Expose value parser**
  
  We expose value parsing as a top-level function, and update the value literal definitions. This lets us re-use the value parser for test frameworks in other repositories.
  
  
- **Update TypeQL banner image link in README**

- **Add contributing guidelines to CONTRIBUTING.md**

- **Allow trailing commas (#421)**
  
  To simplify writing and generating queries, we allow trailing commas anywhere we use a comma separated list of variables, statements, reductions, etc.
  
  
- **Remove discussion and Stack Overflow links**

- **Allow trailing commas**
  
  To simplify writing and generating queries, we allow trailing commas anywhere we use a comma separated list of variables, statements, reductions, etc.
  
    

