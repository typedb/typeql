/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::assert_valid_eq_repr;
use crate::parse_query;

#[test]
fn test_define_query_with_relates_specialises() {
    let query = r#"define
entity pokemon;
relation evolves;
evolves relates pre,
    relates post;
evolves-final sub evolves;
evolves-final relates pre-final as pre;"#;

    let parsed = parse_query(query).unwrap();
    // let expected = define!(
    // type_("pokemon").sub("entity"),
    // type_("evolves").sub("relation"),
    // type_("evolves").relates("from").relates("to"),
    // type_("evolves-final").sub("evolves"),
    // type_("evolves-final").relates("from-final".as_("from"))
    // );

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_define_query_with_owns_specialises_is_not_allowed() {
    let query = r#"define
entity triangle;
triangle owns side-length;
triangle-right-angled sub triangle;
triangle-right-angled owns hypotenuse-length as side-length;"#;
    assert!(parse_query(query).is_err());
}

#[test]
fn test_define_query_with_plays_specialises_is_not_allowed() {
    let query = r#"define
entity pokemon;
relation evolves;
relation evolves relates from,
    relates to;
relation evolves-final sub evolves;
evolves-final relates from-final as from;
pokemon plays evolves-final:from-final as from;"#;

    assert!(parse_query(query).is_err());
}

#[test]
fn test_define_query() {
    let query = r#"define
entity pokemon;
relation evolution;
evolves relates pre,
    relates post;
pokemon plays evolves:pre,
    plays evolves:post,
    owns name;"#;

    let parsed = parse_query(query).unwrap();
    // let expected = define!(
    // type_("pokemon").sub("entity"),
    // type_("evolution").sub("relation"),
    // type_("evolves-from").sub("role"),
    // type_("evolves-to").sub("role"),
    // type_("evolves").relates("from").relates("to"),
    // type_("pokemon").plays(("evolves", "from")).plays(("evolves", "to")).owns("name")
    // );

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_undefine_query_result_is_same_as_java_typeql() {
    let query = r#"undefine
pokemon;
evolution;
evolves-pre;
evolves-post;
relates pre from evolves;
relates post from evolves;
plays evolves:pre from pokemon;
plays evolves:post from pokemon;
owns name from pokemon;"#;

    let parsed = parse_query(query).unwrap();
    // let expected = undefine!(
    // type_("pokemon").sub("entity"),
    // type_("evolution").sub("relation"),
    // type_("evolves-from").sub("role"),
    // type_("evolves-to").sub("role"),
    // type_("evolves").relates("from").relates("to"),
    // type_("pokemon").plays(("evolves", "from")).plays(("evolves", "to")).owns("name")
    // );

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_define_abstract_entity_query() {
    let query = r#"define
entity concrete-type;
entity abstract-type @abstract;"#;
    let parsed = parse_query(query).unwrap();
    // let expected = define!(type_("concrete-type").sub("entity"), type_("abstract-type").sub("entity"));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_define_value_type_query() {
    let query = r#"define
attribute my-type value integer;"#;
    let parsed = parse_query(query).unwrap();
    // let expected = define!(type_("my-type").sub("attribute").value(ValueType::Integer));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn define_attribute_type_regex() {
    let query = r#"define
attribute digit value string @regex("\d");"#;
    let parsed = parse_query(query).unwrap();
    // let expected = define!(type_("digit").sub("attribute").regex(r"\d"));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_as_in_define_result_is_same_as_sub() {
    let query = r#"define
relation parenthood relates parent,
    relates child;
fatherhood sub parenthood,
    relates father as parent,
    relates son as child;"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_define!(
    //         type_("parent").sub("role"),
    //         type_("child").sub("role"),
    //         type_("parenthood").sub("relation").relates("parent").relates("child"),
    //         type_("fatherhood").sub("parenthood").relates(("father", "parent")).relates(("son", "child"))
    //     );
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn undefine_attribute_type_regex() {
    let query = r#"undefine
@regex from digit value string;"#;
    let parsed = parse_query(query).unwrap();
    // let expected = undefine!(type_("digit").regex(r"\d"));
    assert_valid_eq_repr!(expected, parsed, query);
}
