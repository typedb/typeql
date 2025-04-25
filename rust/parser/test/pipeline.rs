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
