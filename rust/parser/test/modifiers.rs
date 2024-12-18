/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::assert_valid_eq_repr;
use crate::parse_query;

#[test]
fn test_get_sort_on_concept_variable() {
    let query = r#"match
$x isa movie,
    has rating $r;
sort $r desc;"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(var("x").isa("movie").has(("rating", cvar("r")))).sort([(cvar("r"), Desc)]);
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_get_sort_on_value_variable() {
    let query = r#"match
$x isa movie,
    has rating $r;
let $l = 100 - $r;
sort $l desc;"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(
    //         var("x").isa("movie").has(("rating", cvar("r"))),
    //         var("l").assign(constant(100).subtract(cvar("r")))
    //     )
    //
    //     .sort([(var("l"), Desc)]);
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_get_sort_multiple() {
    let query = r#"match
$x isa movie,
    has title $t,
    has rating $r;
let $rate = $r * 100;
sort $rate desc, $t;"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(
    //         var("x").isa("movie").has(("title", cvar("t"))).has(("rating", cvar("r"))),
    //         var("rate").assign(cvar("r").multiply(100)),
    //     )
    //
    //     .sort(sort_vars!((var("rate"), Desc), cvar("t")));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_get_sort_limit() {
    let query = r#"match
$x isa movie,
    has rating $r;
sort $r;
limit 10;"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(var("x").isa("movie").has(("rating", cvar("r")))).sort([cvar("r")]).limit(10);
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_get_sort_offset_limit() {
    let query = r#"match
$x isa movie,
    has rating $r;
sort $r desc;
offset 10;
limit 10;"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(var("x").isa("movie").has(("rating", cvar("r"))))
    //         .sort([(var("r"), Desc)])
    //         .offset(10)
    //         .limit(10);
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_get_offset_limit() {
    let query = r#"match
$y isa movie,
    has title $n;
offset 2;
limit 4;"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(var("y").isa("movie").has(("title", cvar("n")))).offset(2).limit(4);
    assert_valid_eq_repr!(expected, parsed, query);
}
