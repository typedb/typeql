/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 */

use super::assert_valid_eq_repr;
use crate::{parse_query, typeql_define, typeql_undefine};

#[test]
fn test_define_query_with_owns_overrides() {
    let query = r#"define
triangle sub entity;
triangle owns side-length;
triangle-right-angled sub triangle;
triangle-right-angled owns hypotenuse-length as side-length;"#;
    let parsed = parse_query(query).unwrap().into_schema().into_define();
    let expected = typeql_define!(
    //         type_("triangle").sub("entity"),
    //         type_("triangle").owns("side-length"),
    //         type_("triangle-right-angled").sub("triangle"),
    //         type_("triangle-right-angled").owns(("hypotenuse-length", "side-length"))
    );
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_define_query_with_relates_overrides() {
    let query = r#"define
pokemon sub entity;
evolves sub relation;
evolves relates from,
    relates to;
evolves-final sub evolves;
evolves-final relates from-final as from;"#;

    let parsed = parse_query(query).unwrap().into_schema().into_define();
    let expected = typeql_define!(
    //         type_("pokemon").sub("entity"),
    //         type_("evolves").sub("relation"),
    //         type_("evolves").relates("from").relates("to"),
    //         type_("evolves-final").sub("evolves"),
    //         type_("evolves-final").relates(("from-final", "from"))
        );

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_define_query_with_plays_overrides() {
    let query = r#"define
pokemon sub entity;
evolves sub relation;
evolves relates from,
    relates to;
evolves-final sub evolves;
evolves-final relates from-final as from;
pokemon plays evolves-final:from-final as from;"#;

    let parsed = parse_query(query).unwrap().into_schema().into_define();
    let expected = typeql_define!(
        // type_("pokemon").sub("entity"),
        // type_("evolves").sub("relation"),
        // type_("evolves").relates("from").relates("to"),
        // type_("evolves-final").sub("evolves"),
        // type_("evolves-final").relates(("from-final", "from")),
        // type_("pokemon").plays(("evolves-final", "from-final", "from"))
    );

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_define_query() {
    let query = r#"define
pokemon sub entity;
evolution sub relation;
evolves-from sub role;
evolves-to sub role;
evolves relates from,
    relates to;
pokemon plays evolves:from,
    plays evolves:to,
    owns name;"#;

    let parsed = parse_query(query).unwrap().into_schema().into_define();
    let expected = typeql_define!(
        // type_("pokemon").sub("entity"),
        // type_("evolution").sub("relation"),
        // type_("evolves-from").sub("role"),
        // type_("evolves-to").sub("role"),
        // type_("evolves").relates("from").relates("to"),
        // type_("pokemon").plays(("evolves", "from")).plays(("evolves", "to")).owns("name")
    );

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_undefine_query_result_is_same_as_java_typeql() {
    let query = r#"undefine
pokemon sub entity;
evolution sub relation;
evolves-from sub role;
evolves-to sub role;
evolves relates from,
    relates to;
pokemon plays evolves:from,
    plays evolves:to,
    owns name;"#;

    let parsed = parse_query(query).unwrap().into_schema().into_undefine();
    let expected = typeql_undefine!(
        // type_("pokemon").sub("entity"),
        // type_("evolution").sub("relation"),
        // type_("evolves-from").sub("role"),
        // type_("evolves-to").sub("role"),
        // type_("evolves").relates("from").relates("to"),
        // type_("pokemon").plays(("evolves", "from")).plays(("evolves", "to")).owns("name")
    );

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_define_abstract_entity_query() {
    let query = r#"define
concrete-type sub entity;
abstract-type sub entity @abstract;"#;
    let parsed = parse_query(query).unwrap().into_schema().into_define();
    let expected =
        typeql_define!(/*type_("concrete-type").sub("entity"), type_("abstract-type").sub("entity").abstract_()*/);
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_define_value_type_query() {
    let query = r#"define
my-type sub attribute,
    value long;"#;
    let parsed = parse_query(query).unwrap().into_schema().into_define();
    let expected = typeql_define!(/*type_("my-type").sub("attribute").value(ValueType::Long)*/);
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn define_attribute_type_regex() {
    let query = r#"define
digit sub attribute,
    value string @regex("\d");"#;
    let parsed = parse_query(query).unwrap().into_schema().into_define();
    let expected = typeql_define!(/*type_("digit").sub("attribute").regex(r"\d")*/);
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn undefine_attribute_type_regex() {
    let query = r#"undefine
digit value string @regex("\d");"#;
    let parsed = parse_query(query).unwrap().into_schema().into_undefine();
    let expected = typeql_undefine!(/*type_("digit").regex(r"\d")*/);
    assert_valid_eq_repr!(expected, parsed, query);
}
