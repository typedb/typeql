
## TypeQL Grammar and Language Library distributions for Rust

Available through https://crates.io/crates/typeql.
```
cargo add typeql@3.10.0
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

- **Bazel 8 upgrade**
  
  Update Bazel version from 6.2 to 8.5.1.
  
  The upgrade is done in a backwards-compatible way, such that "upstream" repositories that are yet to be upgraded may depend on this repository. This is done by preserving WORKSPACE and the deps.bzl loader files alongside the new Bazel 8 ones. Once every repository has been upgraded to Bazel 8, these files will be removed.
