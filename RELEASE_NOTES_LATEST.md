
## TypeQL Grammar and Language Library distributions for Rust

Available through https://crates.io/crates/typeql.
```
cargo add typeql@3.5.0
```


## New Features


## Bugs Fixed


## Code Refactors
- **Remove Java and update README**
  Remove deprecated Java code and grammar and update READMEs to align with the recent changes in TypeDB 3.x.
  
  

## Other Improvements

- **Simplify type statements, allow empty define queries, bring back BDD**
  
  Composite changes:
  1) We clean up the grammar for Type statements in both Patterns and Definables, which now allow commas after a `<kind> <variable>` query: 
  ```
  match entity $x, sub $y;
  ```
  is now legal with the comma. This is a more regular language that is easier to generate.
  
  2) we allow empty define/undefine/redefine queries:
  ```
  define
  ```
  
  This addresses: https://github.com/typedb/typedb/issues/7531
  
  3) We also re-enable half of our 2.x BDD suite, which parses every query in the behaviour repository (though we leave out the other 2.x half, which converts the parsed results back into strings, reparses, and validates the cycle is equivalent), ensuring we don't get parsing errors when we don't expect them and we do when they are expected.
  
  
- **Update README**

- **Update factory/automation.yml**
  Fix build. Remove excessive branches
  
  
- **Update README.md**
  Update contributors
  
  
    

