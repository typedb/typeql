
## TypeQL Grammar and Language Library distributions for Rust

Available through https://crates.io/crates/typeql.

## New Features


- **TypeQL 3.0**

  User-defined functions and structs:
  ```typeql
  fun mean_salary($c: company) -> double? :
      match 
          (company: $c, employee: $_) isa employment, has salary $s;
      return mean($s); 
  ```
  ```typeql
  struct dated_coordinate:
      longitude value double,
      latitude value double,
      date value datetime;
  ```

  Query pipelines:
  ```
  with fun costliest_printer($employee: employee) -> printer? :
    match 
      ($printer, $employee) isa print_permission;
      $printer has cost_per_page $cost;
    sort $cost desc;
    return first($printer);
  match
    $printer isa printer, has office_number $n, has newly_installed true;
    $employee isa employee, has office_number $n;
  put ($employee, $printer) isa print_permission;
  match 
    $high_cost_printer = costliest_printer($employee), has printer_name $name;
    not { $printer is $high_cost_printer; };
    $employee has contact $address;
  insert 
    $notice isa queued_email, has recipient $address, 
    has content "Do you still need the printer " + $name + "?";
  ```

  New undefine syntax allows user to be more precise as to what is being undefined:
  ```typeql
  undefine
  owns age from person;
  @regex from first-name;
  as name from person owns first-name;
  ```
  New, more concise delete syntax:
  ```typeql
  match $p isa person, has name $n;
  delete $n of $p;
  ```

  Implement JSON-like string unescaping (closes #106).

  See [The TypeDB 3.0 Roadmap](<https://typedb.com/blog/typedb-3-roadmap>) for more details!


- **[3.0] Change plays override from label to named_type to allow both scoped and not scoped labels**

  Previously, we could only use not scoped labels (names without scopes) in `as` of `plays`. However, it can cause troubles while reading the schema by a human eye:
  ```
  define
  relation family relates father;
  relation fathership relates father;
  
  entity person plays family:father, plays fathership:father;
  
  # It is fine: we can't have another "relates father" in family or fathership
  relation subfamily sub family, relates subfather as father;
  relation subfathership sub fathership, relates subfather as father;
  
  # It creates more questions as subperson can play multiple `father`s
  entity subperson sub person, plays subfamily:subfather as father, plays subfathership:subfather as father;
  ```

  This PR allows us to use both
  `entity subperson sub person, plays subfamily:subfather as family:father, plays subfathership:subfather as fathership:father;`
  and
  `
  entity subperson sub person, plays subfamily:subfather as father, plays subfathership:subfather as father;`
  based on users' preferences.


- **TypeQL 3 grammar enhancements**

  1. New rich `fetch` syntax (see below).
  2. Standardise the vocabulary of `pipeline`, `stage`, `clause`, `operator`. A _pipeline_ consists of _stages_. Each _stage_ may be an _operator_, which modifies the data stream without accessing the database (e.g. `count`, `mean($x)`), or a _clause,_ which may fetch data from the database to modify the stream (e.g. `match`, `fetch`).
  3. `list()` stream reduce operator.
  4. `$x in [$a, $b, $c]` and other list expressions now allowed in `in`-statements (previously stream-only)

  ### New `fetch` syntax sample

  ```php
  ... # incoming pipeline
  fetch {
  # Printing values directly from pipeline
    "key_1": $x, # var $x (from input stream) holds a value or list
    "key_2": <EXPR>, # <EXPR> is an expression like $x + $y
  
  # Inline attribute retrieval variations
    "key_3": $y.attr, # var $y holds an object with singleton attribute 'attr'
    "key_4": [ $y.attr ], # object var $y has multiple attributes 'attr'
    "key_5": $y.attr[], # object var $y has a list attribute 'attr'
  
  # Function call variations
    "key_6": my_fun1($x,$y), # function my_fun1 has single-return
    "key_7": [ my_fun2($x,$y) ], # function my_fun2 has stream-return
  
  # Match-fetch subqueries
    "key_8": [
      match ...;
      fetch {
        "sub_key": $z, 
        ...
      };
    ]
  
  # Match-reduce-value subqueries
    "key_9": 
      match ...;
      reduce agg($z); # agg could be, e.g., 'count', 'sum', or 'list'
  
  # Nested keys: Nothing stops you from nesting the above!
    "super_key": {
      "sub_key_1": $x,
      "sub_key_2": $y.attr,
      "sub_key_3": [
        ... # some subquery
      ]
    }
  };
  
  
  ```
  
- **Update syntax for reduce stages in pipelines**
  Update syntax for reduce stages in pipelines. Example: `reduce $max = max($of1), $sum = sum($of2) within ($group, $variables)`


- **Implement full fetch specification**

  We refactor Fetch and Function behaviour, to allow any of the following Fetching patterns:
  ```
  match
  ...
  fetch {
      // fetch a matched attribute, value, or type. Represented as a attribute/value/type or null (if the variable is optional).
      "single variable": $a,   
  
      // attribute 'age' of $x. Must be `@card(0..1)` or `@card(1..1)`. Represented as an attribute or null.
      "single-card attributes": $x.age,
  
      // all attributes 'name' of $x. Can be any cardinality.
      "list higher-card attributes": [ $x.name ], 
  
      // inline-computed expression value. Represented as a value.
      "single value expression": $a + 1,  
  
      // an inline query with a 'return' block to select a *single* answer. Represented identically to a single variable or null.
      "single answer block": (  
          match
          $x has name $name;
          return first $name;
      ),
  
      // an inline query with a 'return' block to reduce to a *single* answer. Represented as a value or null
      "reduce answer block": ( 
          match
          $x has name $name;
          return count($name);
      ),
  
      // an inline query that returns a stream of lists/tuples. Represented as list of lists.
      "list positional return block": [  
          match
          $x has name $n,
              has age $a;
          return { $n, $a };
      ],
  
      // an inline query that returns stream of sub-documents. Represented as a list of objects.
      "list pipeline": [ 
          match
          $x has name $n,
              has age $a;
          fetch {
              "name": $n
          };
      ],
  
      // special syntax to fetch all attributes of a concept. Represented as an object, where keys are attribute type names and values are either lists (for >card(1)) or nullable values (for card(0..1) or card(1..1))
      "all attributes": { $x.* }
  }
  ```


- **Implement require operator**

  Implement the 'require' clause:
  ```
  match
  ...
  require $x, $y, $z;
  ```
  Will filter the match output stream to ensure that the variable `$x, $y, $z` are all non-empty variables (if they are optional).


- **Introduce Reduce keyword for stream reduction operations**

  We introduce the missing `reduce` keyword for operations like `min/max/count` aggregations, as well as `first()` and `check`. However, we do not require the `reduce` keyword for function return statements:

  ```
  match
    ...
  reduce count($x);
  ```
  in a function would be;
  ```
  define 
  fun test(...) -> long:
    match ...
    return count($x);
  ```

  We also allow trailing commas throughout the grammar, though they are ignored, to allow the user to generate queries more simply:
  ```
  match
    $x, isa person, has name $n, ... ; #equivalent to the user-friendly syntax: $x isa person, has name, ...;
  ```


- **Fill in some Display traits & fetch refactor for fetch ***

  We fill in missing Display and Pretty printing traits -- note Struct destructuring and Functions are still not implemented.

  We also add a new special syntax to fetch all attributes, since `attribute` is no longer a supertype of all attributes:
  ```
  fetch {
    "attrs": { $x.* }
  }
  ```

- **Implement duration literal parsing**



## Code Refactors
- **TypeQL syntax updates**
  Adds `let` keyword before assignments & moves the constraint on an `isa` to the end. E.g. `$x isa marriage ($a, $b)`


- **Rename override to specialise. Remove specialisations for owns and plays**
  Rename override to specialise. Remove specialisations for owns and plays respecting the server's changes: https://github.com/typedb/typedb/pull/7157

- **Low hanging optimisations for TypeQL**
  Low hanging optimisations for TypeQL
  
  
- **Make Is statement fields public**
  
  We make the `lhs` and `rhs` fields of the `Is` statement struct public for use in typedb core.
  
- **Expose some extra fields in statements**
  Expose some more required fields.
  
  
- **Update fields used by function builders in core to be public**
  Updates fields used by function builders in core to be public.
  
  

## Other Improvements
- **Make iid field public**

- **Update maven snapshot**
  
  We update the maven artifacts snapshot for build dependency test.

  
- **Reorder fetch variants to prioritize the fetch stream for functions over the list of expressions**
  We reorder fetch variants to prioritize the fetch stream for functions over the list of expressions. The old ordering led to incorrect parsing of fetch statements with listed function calls.
  
  
- **Commit generated Cargo.toml**
  
  We commit the generated cargo manifests so that `typeql` can be used as a cargo git dependency.

- **Add unescape version for regex annotations**
  We add a separate `unescape` method for strings inside `regex` annotations to preserve regex escaped characters and unescape `"` quotes.
   
- **autogenerated grammar visitor tests**