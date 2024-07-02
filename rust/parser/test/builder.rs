/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

// #[test]
// fn when_building_invalid_iid_throw() {
//     let iid = "invalid";
//     let expected = typeql_match!(var("x").iid(iid));
//     assert!(expected.is_err());
// }

// #[test]
// fn test_utf8_variable() {
//     let var = "人";
//     let expected = typeql_match!(var(var).isa("person"));
//     assert!(expected.is_ok());
// }

// #[test]
// fn when_using_invalid_variable_throw() {
//     let var = "_人";
//     let expected = typeql_match!(var(var).isa("person"));
//     assert!(expected.is_err());
// }

// #[test]
// fn test_utf8_label() {
//     let label = "人";
//     let expected = typeql_match!(var("x").isa(label));
//     assert!(expected.is_ok());
// }

// #[test]
// fn test_utf8_value() {
//     let value = "人";
//     let expected = typeql_match!(var("x").isa("person").has(("name", value)));
//     assert!(expected.is_ok());
// }
