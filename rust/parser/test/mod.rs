/*
 * Copyright (C) 2022 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

use crate::{
    parse_query, rel, typeql_match, var, RelationVariableBuilder, ThingVariableBuilder,
    TypeVariableBuilder,
};

macro_rules! assert_query_eq {
    ($expected:ident, $parsed:ident, $query:ident) => {
        assert_eq!($expected, $parsed);
        assert_eq!($expected.to_string(), $query);
        assert_eq!($parsed.to_string(), $query);
    };
}

#[test]
fn test_simple_query() {
    let query = r#"match
$x isa movie;"#;

    let parsed = parse_query(query);
    let expected = typeql_match(var("x").isa("movie"));
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_named_type_variable() {
    let query = r#"match
$a type attribute_label;
get $a;"#;

    let parsed = parse_query(query);
    let expected = typeql_match(var("a").type_("attribute_label")).get(["a"]);
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_parse_string_with_slash() {
    let query = r#"match
$x isa person,
    has name "alice/bob";"#;

    let parsed = parse_query(query);
    let expected = typeql_match(var("x").isa("person").has("name", "alice/bob"));
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_relation_query() {
    let query = r#"match
$brando "Marl B" isa name;
(actor: $brando, $char, production-with-cast: $prod);
get $char, $prod;"#;

    let parsed = parse_query(query);
    let expected = typeql_match([
        var("brando").eq("Marl B").isa("name"),
        rel(("actor", "brando"))
            .rel("char")
            .rel(("production-with-cast", "prod")),
    ])
    .get(["char", "prod"]);

    println!("{:?}", query);
    println!("{:?}", parsed.to_string());
    println!("{:?}", expected.to_string());
    assert_query_eq!(expected, parsed, query);
}
