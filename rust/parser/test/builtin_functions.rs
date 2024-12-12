/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::assert_valid_eq_repr;
use crate::parse_query;

#[test]
fn test_function_min() {
    let query = r#"match
$x isa commodity,
    has price $p;
$oder isa order (commodity: $x, qty: $q);
let $net = $p * $q;
let $gross = min($net * 1.21, $net + 100.0);"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(
    //         var("x").isa("commodity").has(("price", cvar("p"))),
    //         rel(("commodity", "x")).links(("qty", "q")).isa("order"),
    //         var("net").assign(cvar("p").multiply(cvar("q"))),
    //         var("gross").assign(min!(vvar("net").multiply(1.21), vvar("net").add(100.0)))
    //     );
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_function_max() {
    let query = r#"match
$x isa commodity,
    has price $p;
$order isa order (commodity: $x, qty: $q);
let $net = $p * $q;
let $gross = max($net * 1.21, $net + 100.0);"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(
    //         var("x").isa("commodity").has(("price", cvar("p"))),
    //         rel(("commodity", "x")).links(("qty", "q")).isa("order"),
    //         var("net").assign(cvar("p").multiply(cvar("q"))),
    //         var("gross").assign(max!(vvar("net").multiply(1.21), vvar("net").add(100.0)))
    //     );
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_function_abs() {
    let query = r#"match
$x isa commodity,
    has price $p;
$order isa order (commodity: $x, qty: $q);
let $net = $p * $q;
let $value = abs($net * 1.21);"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(
    //         var("x").isa("commodity").has(("price", cvar("p"))),
    //         rel(("commodity", "x")).links(("qty", "q")).isa("order"),
    //         var("net").assign(cvar("p").multiply(cvar("q"))),
    //         var("value").assign(abs(vvar("net").multiply(1.21)))
    //     );
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_function_ceil() {
    let query = r#"match
$x isa commodity,
    has price $p;
(commodity: $x, qty: $q) isa order;
let $net = $p * $q;
let $value = ceil($net * 1.21);"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(
    //         var("x").isa("commodity").has(("price", cvar("p"))),
    //         rel(("commodity", "x")).links(("qty", "q")).isa("order"),
    //         var("net").assign(cvar("p").multiply(cvar("q"))),
    //         var("value").assign(ceil(vvar("net").multiply(1.21)))
    //     );
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_function_floor() {
    let query = r#"match
$x isa commodity,
    has price $p;
$order isa order (commodity: $x, qty: $q);
let $net = $p * $q;
let $value = floor($net * 1.21);"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(
    //         var("x").isa("commodity").has(("price", cvar("p"))),
    //         rel(("commodity", "x")).links(("qty", "q")).isa("order"),
    //         var("net").assign(cvar("p").multiply(cvar("q"))),
    //         var("value").assign(floor(vvar("net").multiply(1.21)))
    //     );
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_function_round() {
    let query = r#"match
$x isa commodity,
    has price $p;
$order isa order (commodity: $x, qty: $q);
let $net = $p * $q;
let $value = round($net * 1.21);"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(
    //         var("x").isa("commodity").has(("price", cvar("p"))),
    //         rel(("commodity", "x")).links(("qty", "q")).isa("order"),
    //         var("net").assign(cvar("p").multiply(cvar("q"))),
    //         var("value").assign(round(vvar("net").multiply(1.21)))
    //     );
    assert_valid_eq_repr!(expected, parsed, query);
}
