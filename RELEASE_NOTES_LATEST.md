
## TypeQL Grammar and Language Library distributions for Rust

Available through https://crates.io/crates/typeql.
```
cargo add typeql@3.8.2
```


## New Features
- **Allow leading underscore in type labels**
  
  We relax our identifier rules, allowing `_` as a leading underscore in type labels. This does NOT extend to variables, since we have some complexity around anonymous variables for the time being we don't want to modify.
  
  

## Bugs Fixed


## Code Refactors
- **Introduce Collector variant of Reducer to accomodate the unimplemented list reducer**
  It was placed under Stat, which it is not and caused confusion & crashes in core.
  
  

## Other Improvements
- **Add tests for end; clause in query pipelines**
  
  Add tests for `end;` statements
  
- **Allow decimal syntax without a period**
  
  To give both humans and LLMs more flexibility when writing `decimal` value types, we no longer requiring the `.0` in `xxx.0dec`. This is unambiguous since the `dec` suffix already syntactically differentiates when a value is a decimal.
  
  
    

