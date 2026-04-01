/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{parse_queries, parse_query, parser::test::assert_valid_eq_repr};

#[test]
fn test_data_pipeline() {
    let query = r#"match
$x isa movie;
reduce $std = std($x);
match
$y isa person,
    has name "Rocky";
insert
watches (watched: $x, watcher: $y);"#;
    let parsed = parse_query(query).unwrap();
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_parsing_two_pipelines() {
    let queries = r#"match
$x isa movie;
reduce $std = std($x);
end;
# this is a comment

# this is another commnet
match
# this is another commnet
$y isa person,
    has name "Rocky";
# this is another commnet
insert
watches (watched: $x, watcher: $y);
# this is another commnet
"#;
    let parsed = parse_queries(queries).unwrap();
    assert_eq!(parsed.len(), 2);
}

#[test]
fn test_parsing_schema_and_data_queries() {
    let queries = r#"define
entity movie;
entity person, owns name;
attribute name, value string;

match
$x isa movie;
reduce $std = std($x);
end;

match
$y isa person,
    has name "Rocky";
# this is another commnet
insert
watches (watched: $x, watcher: $y);
# this is another commnet
"#;
    let parsed = match parse_queries(queries) {
        Ok(parsed) => parsed,
        Err(err) => {
            println!("{}", err);
            panic!("")
        }
    };
    assert_eq!(parsed.len(), 3);
}

#[test]
fn test_parsing_mixing_query_types() {
    let queries = r#"define
entity movie;
entity person, owns name;
attribute name, value string;

match
$x isa movie;
reduce $std = std($x);
end;

match
$y isa person;
fetch {
  "all": { $y.* },
};
end;

match
$y isa person,
    has name "Rocky";
insert
watches (watched: $x, watcher: $y);
fetch {
  "bla": { $y.* },
};
"#;
    let parsed = parse_queries(queries).unwrap();
    assert_eq!(parsed.len(), 4);
}

#[test]
fn test_single_pipeline_with_optional_end() {
    // end; is optional for a single pipeline
    let query_without_end = r#"match
$x isa movie;
insert
$x has rating 5;"#;
    let parsed_without = parse_query(query_without_end).unwrap();
    assert!(!parsed_without.has_explicit_end());

    let query_with_end = r#"match
$x isa movie;
insert
$x has rating 5;
end;"#;
    let parsed_with = parse_query(query_with_end).unwrap();
    assert!(parsed_with.has_explicit_end());

    // parse_queries with a single query should return exactly one query
    // and it should be equivalent to parse_query
    let parsed_queries = parse_queries(query_with_end).unwrap();
    assert_eq!(parsed_queries.len(), 1);
    assert!(parsed_queries[0].has_explicit_end());
    assert_eq!(parsed_with.to_string(), parsed_queries[0].to_string());
}

#[test]
fn test_schema_query_with_optional_end() {
    // end; is optional for schema queries
    let query_without_end = r#"define
entity movie;"#;
    let parsed_without = parse_query(query_without_end).unwrap();
    assert!(!parsed_without.has_explicit_end());

    let query_with_end = r#"define
entity movie;
end;"#;
    let parsed_with = parse_query(query_with_end).unwrap();
    assert!(parsed_with.has_explicit_end());
}

#[test]
fn test_all_pipeline_stages_with_end() {
    // Various pipeline stage combinations with end;
    let queries = r#"match
$x isa person;
end;

match
$x isa person;
select $x;
end;

match
$x isa person;
delete $x;
end;

match
$x isa person, has name $n;
reduce $count = count($n);
end;

insert
$x isa person, has name "Alice";
end;"#;
    let parsed = parse_queries(queries).unwrap();
    assert_eq!(parsed.len(), 5);
    for i in 0..5 {
        assert!(parsed[i].has_explicit_end(), "Query {} should have explicit end", i);
    }
}
