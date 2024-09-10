/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::assert_valid_eq_repr;
use crate::parse_query;

#[test]
fn test_delete_query() {
    let query = r#"match
$x isa movie,
    has title "The Title";
$y isa movie,
    has title $t;
delete
$x;
has $t of $y;"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(var("x").isa("movie").has(("title", "The Title")), cvar("y").isa("movie"))
    //         .delete([var("x").isa("movie"), cvar("y").isa("movie")]);
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_insert_query() {
    let query = r#"insert
$x isa pokemon,
    has name "Pichu";
$y isa pokemon,
    has name "Pikachu";
$z isa pokemon,
    has name "Raichu";
(evolves-from: $x, evolves-to: $y) isa evolution;
(evolves-from: $y, evolves-to: $z) isa evolution;"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_insert!(
    //         var("x").isa("pokemon").has(("name", "Pichu")),
    //         var("y").isa("pokemon").has(("name", "Pikachu")),
    //         var("z").isa("pokemon").has(("name", "Raichu")),
    //         rel(("evolves-from", "x")).links(("evolves-to", "y")).isa("evolution"),
    //         rel(("evolves-from", "y")).links(("evolves-to", "z")).isa("evolution")
    //     );
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_update_query() {
    let query = r#"match
$x isa person,
    has name "alice",
    has age $a;
update
$x has age 25;"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(var("x").isa("person").has(("name", "alice")).has(("age", cvar("a"))))
    //         .delete(var("x").has(cvar("a")))
    //         .insert(var("x").has(("age", 25)));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_match_insert_query() {
    let query = r#"match
$x isa language;
insert
$x has name "HELLO";"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(var("x").isa("language")).insert(cvar("x").has(("name", "HELLO")));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_if_call_limit_hit() {
    let query = "insert $p isa person, has age 10;";
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(var("x").isa("language")).insert(cvar("x").has(("name", "HELLO")));
    assert_valid_eq_repr!(expected, parsed, query);

}