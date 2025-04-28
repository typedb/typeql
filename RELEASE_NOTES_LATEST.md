
## TypeQL Grammar and Language Library distributions for Rust

Available through https://crates.io/crates/typeql.
```
cargo add typeql@3.2.0-rc2
```


## New Features
- **Add query end marker**
  
  TypeQL query pipelines cannot be distinguished since query stages can be arbitrarily chained and concatenated. To resolve this, we introduce the `end;` marker. For example:
  ```
  match ...
  insert ...
  end;
  match ...
  insert...
  ```
  
  Is now correctly interpretable as two separate match-insert queries! Compared to before:
  ```
  match ...
  insert ...
  match ...
  insert ...
  ```
  
  Where it was not clear if this is one match-insert-match-insert pipeline, two match-insert pipelines, or 4 separate queries, or anything in between!
  
  For simplicity, any query can be terminated with an 'end;` marker, though it is redundant in for schema queries and 'fetch' queries:
  ```
  define ...;
  end; # redundant!
  
  match ...
  insert ...
  end;  # not redundant!
  
  match ...
  insert ...
  fetch { ... };
  end;  # redundant!
  ```
  
  
- **Add prefix query parser**
  
  We create a "query prefix parsing" API, which attempts to parse the maximal query prefix from the input. 
  
  This is designed to help applications like Console consume a single complete query at a time from a set of concatenated queries.


- **Allow relates overrides to have lists**

  We fix one syntactic inconsistency, grammatically allowing list overrides for relation's roles:
  ```
  define
    relation sub-rel, relates sub-role[] as super-role[];
  ```

## Bugs Fixed


## Code Refactors
- **Refactor grammar: partial rules, cleanup**
  
  We rename rules used for partial parsing to have suffix `_partial` rather than `_no_test` (randomized tests only use complete queries).
  
  

## Other Improvements
- **Fix unit test checks**

- **Improve TypeQL syntax errors**

- **Update README.md**

    

