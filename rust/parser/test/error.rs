/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::assert_valid_eq_repr;
use crate::parse_query;

#[test]
fn test_parsing_empty_string() {
    assert!(parse_query("").is_err());
}
#[test]
fn when_parsing_query_with_syntax_error_report_error() {
    let query_text = "define\nperson sub entity has name;"; // note no comma
    let parsed = parse_query(query_text);
    assert!(parsed.is_err());
    assert!(parsed.unwrap_err().to_string().contains("person sub entity has name;"));
}

#[test]
fn test_missing_colon() {
    assert!(parse_query("match\n(actor $x, $y) isa has-cast;").is_err());
}

#[test]
fn test_missing_comma() {
    assert!(parse_query("match\n($x $y) isa has-cast;").is_err());
}

#[test]
fn test_limit_mistake() {
    let parsed = parse_query("match\n($x, $y); limit1;");
    assert!(parsed.is_err());
    let report = parsed.unwrap_err().to_string();
    assert!(report.contains("limit1"));
}

// #[test]
// fn when_parsing_aggregate_with_wrong_variable_argument_number_throw() {
//     assert!(parse_query("match\n$x isa name; group;").is_err());
// }

// #[test]
// fn when_parsing_aggregate_with_wrong_name_throw() {
//     assert!(parse_query("match\n$x isa name; hello $x;").is_err());
// }

// #[test]
// fn when_value_equality_to_string_create_valid_query_string() {
//     let expected = typeql_match!(var("x").eq(cvar("y")));
//     let parsed = parse_query(&expected.to_string()).unwrap();
//     assert_eq!(expected, parsed);
// }

#[test]
fn test_iid_constraint() {
    let iid = "0x0123456789abcdefdeadbeef";
    let query = format!(
        r#"match
$x iid {iid};"#
    );
    let parsed = parse_query(&query).unwrap();
    // let expected = match_!(var("x").iid(iid));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_invalid_iid_throw() {
    let query = r#"match
$x iid invalid;"#;
    let parsed = parse_query(query);
    assert!(parsed.is_err());
}

#[test]
fn test_parsing_key() {
    let query = r#"match
$x owns name @key;"#;
    let parsed = parse_query(query).unwrap();
    // let expected = typeql_match!(var("x").owns(("name", Key))).get_fixed([cvar("x")]);
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_syntax_error_pointer() {
    let parsed = parse_query("match\n$x of");
    assert!(parsed.is_err());
    let report = parsed.unwrap_err().to_string();
    assert!(report.contains("at 2:4"), "{report}");
    assert!(report.contains("$x of"), "{report}");
}

#[test]
fn when_parsing_incorrect_syntax_trailing_query_whitespace_is_ignored() {
    let parsed = parse_query("match\n$x isa \n");
    assert!(parsed.is_err());
    let report = parsed.unwrap_err().to_string();
    assert!(report.contains("at 2:7"), "{report}");
    assert!(report.contains("$x isa"), "{report}");
}

#[test]
fn when_parsing_incorrect_syntax_error_message_should_retain_whitespace() {
    let parsed = parse_query("match\n$x isa ");
    assert!(parsed.is_err());
    let report = parsed.unwrap_err().to_string();
    assert!(!report.contains("match$xisa"), "{report}");
}

#[test]
fn when_parsing_incorrect_syntax_throw_typeql_syntax_exception_with_helpful_error() {
    let parsed = parse_query("match\n$x isa");
    assert!(parsed.is_err());
    let report = parsed.unwrap_err().to_string();
    assert!(report.contains("at 2:7"), "{report}");
    assert!(report.contains("$x isa"), "{report}");
}
