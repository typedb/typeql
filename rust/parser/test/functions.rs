/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::assert_valid_eq_repr;
use crate::parse_query;

#[test]
fn test_function_stream() {
    let query = r#"define
    fun my_function() -> { return-type }:
        match
        $v isa var-type;
        return { $v };"#;
    let parsed = parse_query(query).unwrap();
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_function_single() {
    let query = r#"define
    fun my_function() -> return-type:
        match
        $v isa var-type;
        return first $v;"#;
    let parsed = parse_query(query).unwrap();
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_function_reduce() {
    let query = r#"define
    fun my_function() -> return-type:
        match
        $v isa var-type;
        return count($v), sum($v);"#;
    let parsed = parse_query(query).unwrap();
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_function_args() {
    let query = r#"define
    fun my_function($arg1: type1, $arg2: type2) -> { return-type }:
        match
        $v isa var-type;
        return { $v };"#;
    let parsed = parse_query(query).unwrap();
    assert_valid_eq_repr!(expected, parsed, query);
}
