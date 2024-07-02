/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::assert_valid_eq_repr;
use crate::parse_query;

#[test]
fn regex_predicate_parses_character_classes_correctly() {
    let query = r#"match
$x like "\d";"#;
    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("x").like(r"\d"));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn regex_predicate_parses_quotes_correctly() {
    let query = r#"match
$x like "\"";"#;
    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("x").like(r#"\""#));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn regex_predicate_parses_backslashes_correctly() {
    let query = r#"match
$x like "\\";"#;
    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("x").like(r"\\"));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn regex_predicate_parses_newline_correctly() {
    let query = r#"match
$x like "\n";"#;
    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("x").like(r"\n"));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn regex_predicate_parses_forward_slashes_correctly() {
    let query = r#"match
$x like "\/";"#;
    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("x").like("/"));
    assert_valid_eq_repr!(expected, parsed, query);
}
