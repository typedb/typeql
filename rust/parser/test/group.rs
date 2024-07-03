/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

// #[test]
// fn test_parsing_aggregate_group() {
//     let query = r#"match
// $x isa movie;
// group $x;"#;
//     let parsed = parse_query(query).unwrap().into_get_group();
//     let expected = typeql_match!(var("x").isa("movie")).group(cvar("x"));
//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_parsing_aggregate_group_count() {
//     let query = r#"match
// $x isa movie;
// group $x; count;"#;
//     let parsed = parse_query(query).unwrap().into_get_group_aggregate();
//     let expected = typeql_match!(var("x").isa("movie")).group(cvar("x")).count();
//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_parsing_aggregate_to_string() {
//     let query = r#"match
// $x isa movie;
// get $x;
// group $x; count;"#;
//     assert_eq!(query, parse_query(query).unwrap().to_string());
// }
