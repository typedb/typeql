/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

// #[test]
// fn test_aggregate_group_count_query() {
//     let query = r#"match
// ($x, $y) isa friendship;
// get $x, $y;
// group $x; count;"#;
//     let parsed = parse_query(query).unwrap().into_get_group_aggregate();
//     let expected =
//         typeql_match!(rel("x").links("y").isa("friendship")).get_fixed([var("x"), cvar("y")]).group(cvar("x")).count();
//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_single_line_group_aggregate_max_query() {
//     let query = r#"match
// $x has age $a;
// group $x; max $a;"#;
//     let parsed = parse_query(query).unwrap().into_get_group_aggregate();
//     let expected = typeql_match!(var("x").has(("age", cvar("a")))).group(cvar("x")).max(cvar("a"));

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_multi_line_group_aggregate_max_query() {
//     let query = r#"match
// ($x, $y) isa friendship;
// $y has age $z;
// group $x; max $z;"#;

//     let parsed = parse_query(query).unwrap().into_get_group_aggregate();
//     let expected = typeql_match!(rel("x").links("y").isa("friendship"), var("y").has(("age", cvar("z"))))
//
//         .group(var("x"))
//         .max(var("z"));

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_multi_line_filtered_group_aggregate_max_query() {
//     let query = r#"match
// ($x, $y) isa friendship;
// $y has age $z;
// get $x, $y, $z;
// group $x; max $z;"#;

//     let parsed = parse_query(query).unwrap().into_get_group_aggregate();
//     let expected = typeql_match!(rel("x").links("y").isa("friendship"), var("y").has(("age", cvar("z"))))
//         .get_fixed([var("x"), cvar("y"), cvar("z")])
//         .group(var("x"))
//         .max(var("z"));

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_filtered_group_aggregates_on_value_variable() {
//     let query = r#"match
// $i ($x, $s) isa income-source;
// $i has value $v,
//     has tax-rate $r;
// $t = $r * $v;
// get $x, $t;
// group $x; sum $t;"#;

//     let parsed = parse_query(query).unwrap().into_get_group_aggregate();
//     let select: [Variable; 2] = [var("x").into(), vvar("t").into()];
//     let expected = typeql_match!(
//         var("i").links("x").links("s").isa("income-source"),
//         var("i").has(("value", cvar("v"))).has(("tax-rate", cvar("r"))),
//         var("t").assign(cvar("r").multiply(cvar("v"))),
//     )
//     .get_fixed(select)
//     .group(var("x"))
//     .sum(var("t"));

//     assert_valid_eq_repr!(expected, parsed, query);
// }
