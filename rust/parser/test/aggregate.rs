/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::assert_valid_eq_repr;
use crate::parse_query;

#[test]
fn test_parsing_aggregate_std() {
    let query = r#"match
$x isa movie;
reduce std($x);"#;
    let parsed = parse_query(query).unwrap();
    // let expected = typeql_match!(var("x").isa("movie")).std(cvar("x"));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_has_variable() {
    let query = r#"match
$_ has title "Godfather",
    has tmdb-vote-count $x;"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(var(()).has(("title", "Godfather")).has(("tmdb-vote-count", cvar("x"))));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_regex_attribute_type() {
    let query = r#"match
$x value string @regex("(fe)male");"#;
    let parsed = parse_query(query).unwrap();
    // let expected = typeql_match!(var("x").regex("(fe)male"));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_aggregate_count_query() {
    let query = r#"match
($x, $y) isa friendship;
select $x, $y;
reduce count($x);"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(rel("x").links("y").isa("friendship")).get_fixed([var("x"), cvar("y")]).count();
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_comparing_count_query_using_typeql_and_rust_typeql_they_are_equivalent() {
    let query = r#"match
$x isa movie,
    has title "Godfather";
reduce count($x);"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(var("x").isa("movie").has(("title", "Godfather"))).count();
    assert_valid_eq_repr!(expected, parsed, query);
}
