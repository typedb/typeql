/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

// #[test]
// fn test_parsing_list_one_match() {
// let queries = "match $y isa movie;";
// let parsed = parse_queries(queries).unwrap().collect_vec();
// let expected = vec![typeql_match!(var("y").isa("movie"))];
// assert_eq!(parsed, expected);
// }

// #[test]
// fn test_parsing_list_one_insert() {
// let queries = "insert $x isa movie;";
// let parsed = parse_queries(queries).unwrap().collect_vec();
// let expected = vec![typeql_insert!(var("x").isa("movie"))];
// assert_eq!(parsed, expected);
// }

// #[test]
// fn test_parsing_list_one_insert_with_whitespace_prefix() {
// let queries = " insert $x isa movie;";
// let parsed = parse_queries(queries).unwrap().collect_vec();
// let expected = vec![typeql_insert!(var("x").isa("movie"))];
// assert_eq!(parsed, expected);
// }

// #[test]
// fn test_parsing_list_one_insert_with_prefix_comment() {
// let queries = r#"#hola
// insert $x isa movie;"#;
// let parsed = parse_queries(queries).unwrap().collect_vec();
// let expected = vec![typeql_insert!(var("x").isa("movie"))];
// assert_eq!(parsed, expected);
// }

// #[test]
// fn test_parsing_list() {
// let queries = "insert $x isa movie; match $y isa movie;";
// let parsed = parse_queries(queries).unwrap().collect_vec();
// let expected = vec![typeql_insert!(var("x").isa("movie")).into(), typeql_match!(cvar("y").isa("movie")).into()];
// assert_eq!(parsed, expected);
// }

// #[test]
// fn test_parsing_many_match_insert_without_stack_overflow() {
//     let num_queries = 10_000;
//     let query = "match $x isa person; insert $x has name 'bob';";
//     let queries = query.repeat(num_queries);
//     let mut parsed = Vec::with_capacity(num_queries);
//     parsed.extend(parse_queries(&queries).unwrap());
//     let expected = typeql_match!(var("x").isa("person")).insert(cvar("x").has(("name", "bob")));
//     assert_eq!(vec![expected; num_queries], parsed);
// }
