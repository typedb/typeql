/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::assert_valid_eq_repr;
use crate::parse_query;

#[test]
fn test_or_query() {
    let query = r#"match
$x isa movie;
{
    $y isa genre == "drama";
    ($x, $y);
} or {
    $x == "The Muppets";
};"#;
    let parsed = parse_query(query).unwrap();
    // let expected = typeql_match!(
    //     var("x").isa("movie"),
    //     or!(and!(var("y").eq("drama").isa("genre"), rel("x").links("y")), cvar("x").eq("The Muppets"))
    // );
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_disjunction_not_in_conjunction() {
    let query = r#"match
{
    $x isa person;
} or {
    $x isa company;
};"#;
    parse_query(query).unwrap();
}

#[test]
fn test_nested_conjunction_and_disjunction() {
    let query = r#"match
$y isa $p;
{
    ($y, $q);
} or {
    $x isa $p;
    {
        $x has first-name $y;
    } or {
        $x has last-name $z;
    };
};"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(
    //         var("y").isa(cvar("p")),
    //         or!(
    //             rel("y").links("q"),
    //             and!(
    //                 var("x").isa(cvar("p")),
    //                 or!(var("x").has(("first-name", cvar("y"))), cvar("x").has(("last-name", cvar("z"))))
    //             )
    //         )
    //     );
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_disjunction_not_binding_conjunction() {
    let query = r#"match
$y isa $p;
{
    ($y, $q);
} or {
    $x isa $p;
    {
        $x has first-name $y;
    } or {
        $q has last-name $z;
    };
};"#;
    let parsed = parse_query(query).unwrap();
    assert_valid_eq_repr!(expected, parsed, query);
}
