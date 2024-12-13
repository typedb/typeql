/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{parse_query, parser::test::assert_valid_eq_repr};

#[test]
fn match_anonymous_relation() {
    let query = r#"match
marriage (husband: $a, wife: $b);"#;
    let parsed = parse_query(query).unwrap();
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn match_anonymous_untyped_relation() {
    let query = r#"match
(husband: $a, wife: $b);"#;
    let parsed = parse_query(query).unwrap();
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn match_anonymous_untyped_relation_no_roles() {
    let query = r#"match
($a, $b);"#;
    let parsed = parse_query(query).unwrap();
    assert_valid_eq_repr!(expected, parsed, query);
}
//
// #[test]
// fn match_anonymous_attribute() {
//     let query = r#"
//     match name "Alice";
//     "#;
//     let parsed = parse_query(query).unwrap();
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// // Insert
// #[test]
// fn insert_anonymous_entity() {
//     let query = r#"insert
// person;"#;
//     let parsed = parse_query(query).unwrap();
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn insert_anonymous_attribute() {
//     let query = r#"insert
// name "Alice";"#;
//     let parsed = parse_query(query).unwrap();
//     assert_valid_eq_repr!(expected, parsed, query);
// }

#[test]
fn insert_anonymous_relation() {
    let query = r#"insert
marriage ($a, $b);"#;
    let parsed = parse_query(query).unwrap();
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn insert_anonymous_untyped_relation() {
    let query = r#"insert
(role1: $a, role2: $b);"#;
    let parsed = parse_query(query).unwrap();
    assert_valid_eq_repr!(expected, parsed, query);
}
