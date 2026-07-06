
## TypeQL Grammar and Language Library distributions for Rust

Available through https://crates.io/crates/typeql.
```
cargo add typeql@3.12.0
```


## New Features
- **@doc and @meta annotations**
  
  We implement `@doc("docstring")` and `@meta("key", "value")` schema annotations. These annotations can be attached to types and capabilities i.e. `owns`, `plays`, `relates`, and `sub`:
  
  ```php
  define
  entity person @doc("this represents an individual client") @meta("icon", ":silhouette.png"),
    owns name @doc("full name including title");
  ```
  ```php
  match
    $x isa client;
    let $x_icon = get_meta("icon", $t); # get each client with a UI display icon hint
  ```
  
  These annotations can also be attached to function definitions. They go at the end of the signature, before the colon introducing the body of the function:
  ```php
  define
  fun get_random_number() -> integer
      @doc("chosen by a fair dice roll"):
  match
    let $rand = 4;
  return first $rand;
  ```
  
  
- **Add given stage**
  Adds the 'given' stage to the language.
  
  
  

## Bugs Fixed


## Code Refactors


## Other Improvements

    

