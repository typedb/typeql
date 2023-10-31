
## TypeQL Grammar and Language Library distributions for Rust

Available through https://crates.io/crates/typeql.
```
cargo add typeql@2.25.0
```

## TypeQL Grammar and Language Library distributions for Java

```xml
<repositories>
    <repository>
        <id>repo.vaticle.com</id>
        <url>https://repo.vaticle.com/repository/maven/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.vaticle.typeql</groupId>
        <artifactId>typeql-grammar</artifactId>
        <version>2.25.0</version>
    </dependency>
    <dependency>
        <groupId>com.vaticle.typeql</groupId>
        <artifactId>typeql-lang</artifactId>
        <version>2.25.0</version>
    </dependency>
</dependencies>
```

## TypeQL Grammar distribution for Python

Available through https://pypi.org

```
pip install typeql-grammar==2.25.0
```


## New Features
- **Implement TypeQL Fetch query**
  
  We implement a new type of query: the `Fetch` query. This type of query does three things:
  1. Projects the concepts selected in the 'match' clause into 'data' objects that can be consumed as a simple JSON structure
  2. Customising the desired JSON structure to be returned
  3. Fetches extra data beyond that described by the 'match' clause in the form of attribute retrieval or full subqueries.
  
  The terminology we use to decribe 'fetch' clauses is that each entry in the 'fetch' is a _projection_.
  
  
  _Examples_
  1. Projecting concepts selected from the 'match' clause directory into data objects. We are allowed to project attributes, types, and value concepts without transformation in the 'fetch' clause:
  ```
  match
  $movie-type sub movie;  # movie or its subtypes
  $x isa! $movie-type,        # an entity instance of the type
      has title "Godfather",
      has release-date $date,
      has duration-minutes $mins;
  ?duration-hours = $mins / 60.0;
  fetch
  $movie-type;
  $date;
  ?duration-hours;
  ```
  
  
  2. Customising the desired JSON structure to be returned
  ```
  match
  $movie-type sub movie;
  $x isa! $movie-type,       
      has title "Godfather",
      has release-date $date,
      has duration-minutes $mins;
  ?duration-hours = $mins / 60.0;
  fetch
  $movie-type as "movie category";   # set the key to return $movie-types as to "movie category"
  $date as "release date";                   # ...
  ?duration-hours as "length";            # ...
  ```
  
  3a. Fetching extra data in the form of attributes. We use this to project an entity or relation into 'data' objects such as attributes, values, and types.
  ```
  match
  $x isa movie,
      has title "Godfather",
      has release-date $date;
  fetch
  $x: title, duration-minutes as "length";
  $date as "release-date";
  ```
  
  3b. Fetching extra data in the form of subqueries:
  ```
  match
  $x isa movie,
      has title "Godfather",
      has release-date $date;
  fetch
  $x: title, duration-minutes as "length";
  $date as "release-date";
  director-details: {              # for each movie found, we will also get all the directors and fetch them as name and age
      match
      ($x, $director) isa directorship;
      fetch
      $director: name, age;
  };
  director-count: {              # for each movie found, we will retrieve the count of all directors for the movie
      match
      ($x, $director) isa directorship;
      get $director;
      count;
  };
  ```
  
  __TypeQL Fetch Query Builders__
  
  We also implement programmatic TypeQL builders for both Java and Rust. Without too much detail, here is how one would programmatically generate the query from 3b in Java and Rust builders:
  
  Java
  ```Java
  TypeQLFetch expected = match(
      cVar("x").isa("movie").has("title", "Godfather").has("release-date", cVar("date"))
  ).fetch(
      cVar("date").asLabel("release date"),    // $date as "release date"
      cVar("x").map("title").map("duration-minutes", "length"),    // $x: title, duration-minutes as "length"
      label("director-details").map(    // subquery 'director-details'
          match(
              rel(cVar("x")).rel(cVar("director")).isa("directorship")
          ).fetch(
              cVar("director").map("name").map("age")
          )   
      ),  
      label("directors-count").map(     // subquery 'director-count'
          match(
              rel(cVar("x")).rel(cVar("director")).isa("directorship")
          ).get(cVar("director")).count()
      ) 
  );
  ```
  
  Rust
  ```Rust
  let projections: Vec<Projection> = vec![
      cvar("date").label("release date").into(),  // $date as "release date"
      cvar("x").map_attributes(vec![    // $x: title, duration-minutes as "length"
          "title".into(),
          ("duration-minutes", "length").into(),
      ]),
      label("director-details").map_subquery_fetch(     // subquery 'director-details'
          typeql_match!(
              rel(cvar("x")).rel(cvar("director")).isa("directorship")
          ).fetch(vec![
               cvar("director").map_attribute(vec!["name".into(), "age".into()])
          ])
      ),
      label("director-count").map_subquery_get_aggregate(    // subquery 'director-count'
          typeql_match!(
              rel(cvar("x")).rel(cvar("director")).isa("directorship")
          ).get_fixed([cvar("director")]).count()
      )
  ];
  let typeql_fetch = typeql_match!(
      cvar("x").isa("movie").has(("title", "Godfather")).has(("release-date", cvar("date")))
  ).fetch(projections);
  ```
  
  **Important TypeQL Changes**
  
  To help enforce when a 'Get' and 'Fetch' query is being issued, we now require that the 'get' clause is mandatory in `Get` queries, and rename what used to be considered a "Match" query to be a "Get" query. 
  
  The mental model we encourage is that the 'match' clause of a query is the definition/constraint space to search, and the following clause is the operation over that space - for example get (without transformation), fetch (transformation), insert, delete, update, etc.
  
  
  

## Bugs Fixed


## Code Refactors


## Other Improvements
- **Update README file**

  Update the README file.

- **Update root checkstyle to exclude banner**
