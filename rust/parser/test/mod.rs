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
    parse_query, rel, type_, typeql_match, var, RelationVariableBuilder, ThingVariableBuilder,
    TypeVariableBuilder,
};
use chrono::{NaiveDate, NaiveDateTime, NaiveTime};

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

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match(var("x").isa("movie"));
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_named_type_variable() {
    let query = r#"match
$a type attribute_label;
get $a;"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match(var("a").type_("attribute_label")).get(["a"]);
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_parse_string_with_slash() {
    let query = r#"match
$x isa person,
    has name "alice/bob";"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match(var("x").isa("person").has("name", "alice/bob"));
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_relation_query() {
    let query = r#"match
$brando "Marl B" isa name;
(actor: $brando, $char, production-with-cast: $prod);
get $char, $prod;"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match([
        var("brando").eq("Marl B").isa("name"),
        rel(("actor", "brando"))
            .rel("char")
            .rel(("production-with-cast", "prod")),
    ])
    .get(["char", "prod"]);

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_role_type_scoped_globally() {
    let query = r#"match
$m relates spouse;"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match(var("m").relates("spouse"));
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_role_type_not_scoped() {
    let query = r#"match
marriage relates $s;"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match(type_("marriage").relates(var("s")));
    assert_query_eq!(expected, parsed, query);
}

/*
#[test]
fn test_predicate_query_1() {
    let query = r#"match
$x isa movie,
    has title $t;
{
    $t "Apocalypse Now";
} or {
    $t < "Juno";
    $t > "Godfather";
} or {
    $t "Spy";
};
$t != "Apocalypse Now";"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match([
        var("x").isa("movie").has("title", var("t")),
        or(
            var("t").eq("Apocalypse Now"),
            and(var("t").lt("Juno"), var("t").gt("Godfather")),
            var("t").eq("Spy"),
        ),
        var("t").neq("Apocalypse Now"),
    ]);
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_predicate_query_2() {
    let query = r#"match
$x isa movie,
    has title $t;
{
    $t <= "Juno";
    $t >= "Godfather";
    $t != "Heat";
} or {
    $t "The Muppets";
};"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match([
        var("x").isa("movie").has("title", var("t")),
        or(
            and(
                var("t").lte("Juno"),
                var("t").gte("Godfather"),
                var("t").neq("Heat"),
            ),
            var("t").eq("The Muppets"),
        ),
    ]);
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_predicate_query_3() {
    let query = r#"match
($x, $y);
$y isa person,
    has name $n;
{
    $n contains "ar";
} or {
    $n like "^M.*$";
};"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match([
        rel("x").rel("y"),
        var("y").isa("person").has("name", var("n")),
        or(var("n").contains("ar"), var("n").like("^M.*$")),
    ]);
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_predicate_query_4() {
    let query = r#"match
$x has age $y;
$y >= $z;
$z 18 isa age;"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match([
        var("x").has("age", var("y")),
        var("y").gte(var("z")),
        var("z").eq(18).isa("age"),
    ]);
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_concept_variable() {
    let query = r#"match
$x sub $z;
$y sub $z;
$a isa $x;
$b isa $y;
not { $x is $y; };
not { $a is $b; };"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match([
        var("x").sub(var("z")),
        var("y").sub(var("z")),
        var("a").isa(var("x")),
        var("b").isa(var("y")),
        not(var("x").is("y")),
        not(var("a").is("b")),
    ]);
    assert_query_eq!(expected, parsed, query);
}
*/

#[test]
fn test_value_equals_variable_query() {
    let query = r#"match
$s1 = $s2;"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match(var("s1").eq(var("s2")));
    assert_query_eq!(expected, parsed, query);
}

/*
#[test]
fn test_movies_released_after_or_at_the_same_time_as_spy() {
    let query = r#"match
$x has release-date >= $r;
$_ has title "Spy",
    has release-date $r;"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match([
        var("x").has("release-date", gte(var("r"))),
        var(()).has("title", "Spy").has("release-date", var("r")),
    ]);
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_predicate() {
    let query = r#"
$x has release-date < 1986-03-03T00:00,
    has tmdb-vote-count 100,
    has tmdb-vote-average <= 9.0;"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match([var("x")
        .has("release-date", lt(LocalDate.of(1986, 3, 3).atStartOfDay()))
        .has("tmdb-vote-count", 100)
        .has("tmdb-vote-average", lte(9.0))]);

    assert_query_eq!(expected, parsed, query);
}
*/

#[test]
fn when_parsing_date_handle_time() {
    let query = r#"match
$x has release-date 1000-11-12T13:14:15;"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match(var("x").has(
        "release-date",
        NaiveDateTime::new(
            NaiveDate::from_ymd(1000, 11, 12),
            NaiveTime::from_hms(13, 14, 15),
        ),
    ));

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_big_years() {
    let query = r#"match
$x has release-date +12345-12-25T00:00;"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match(var("x").has(
        "release-date",
        NaiveDateTime::new(
            NaiveDate::from_ymd(12345, 12, 25),
            NaiveTime::from_hms(0, 0, 0),
        ),
    ));

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_small_years() {
    let query = r#"match
$x has release-date 0867-01-01T00:00;"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match(var("x").has(
        "release-date",
        NaiveDateTime::new(NaiveDate::from_ymd(867, 1, 1), NaiveTime::from_hms(0, 0, 0)),
    ));

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_negative_years() {
    let query = r#"match
$x has release-date -3200-01-01T00:00;"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match(var("x").has(
        "release-date",
        NaiveDateTime::new(
            NaiveDate::from_ymd(-3200, 1, 1),
            NaiveTime::from_hms(0, 0, 0),
        ),
    ));

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_millis() {
    let query = r#"match
$x has release-date 1000-11-12T13:14:15.123;"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match(var("x").has(
        "release-date",
        NaiveDateTime::new(
            NaiveDate::from_ymd(1000, 11, 12),
            NaiveTime::from_hms_milli(13, 14, 15, 123),
        ),
    ));

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_millis_shorthand() {
    let input_query = r#"match
$x has release-date 1000-11-12T13:14:15.1;"#;

    let parsed = parse_query(input_query).unwrap();
    let expected = typeql_match(var("x").has(
        "release-date",
        NaiveDateTime::new(
            NaiveDate::from_ymd(1000, 11, 12),
            NaiveTime::from_hms_milli(13, 14, 15, 100),
        ),
    ));

    let parsed_query = r#"match
$x has release-date 1000-11-12T13:14:15.100;"#;
    assert_query_eq!(expected, parsed, parsed_query);
}

#[test]
fn when_parsing_date_error_when_parsing_overly_precise_decimal_seconds() {
    let query = r#"match
$x has release-date 1000-11-12T13:14:15.000123456;"#;

    let parsed = parse_query(query);
    match parsed {
        Err(err) => assert!(err.contains("no viable alternative")),
        Ok(_) => assert!(false),
    }
}

/*
#[test]
fn when_parsing_date_error_when_handling_overly_precise_nanos() {
    let expected = typeql_match(var("x").has(
        "release-date",
        NaiveDateTime::new(
            NaiveDate::from_ymd(1000, 11, 12),
            NaiveTime::from_hms_milli(13, 14, 15, 123450000),
        ),
    ));
    match expected {
        Err(err) => assert!(err.contains("more precise than 1 millisecond")),
        Ok(_) => assert!(false),
    }
}
*/

#[test]
fn test_schema_query() {
    let query = r#"match
$x plays starring:actor;
sort $x asc;"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match(var("x").plays(("starring", "actor"))).sort((["x"], "asc"));

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_get_sort() {
    let query = r#"match
$x isa movie,
    has rating $r;
sort $r desc;"#;

    let parsed = parse_query(query).unwrap();
    let expected =
        typeql_match(var("x").isa("movie").has("rating", var("r"))).sort((["r"], "desc"));

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_get_sort_limit() {
    let query = r#"match
$x isa movie,
    has rating $r;
sort $r; limit 10;"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match(var("x").isa("movie").has("rating", var("r")))
        .sort("r")
        .limit(10);

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_get_sort_offset_limit() {
    let query = r#"match
$x isa movie,
    has rating $r;
sort $r desc; offset 10; limit 10;"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match(var("x").isa("movie").has("rating", var("r")))
        .sort((["r"], "desc"))
        .offset(10)
        .limit(10);

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_get_offset_limit() {
    let query = r#"match
$y isa movie,
    has title $n;
offset 2; limit 4;"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match(var("y").isa("movie").has("title", var("n")))
        .offset(2)
        .limit(4);

    assert_query_eq!(expected, parsed, query);
}

/*
#[test]
fn test_variables_everywhere_query() {  // SubConstraint
    let query = r#"match
($p: $x, $y);
$x isa $z;
$y "crime";
$z sub production;
has-genre relates $p;"#;

    let parsed = parse_query(query).unwrap();
    let expected = typeql_match([
        rel((var("p"), var("x"))).rel("y"),
        var("x").isa(var("z")),
        var("y").eq("crime"),
        var("z").sub("production"),
        type_("has-genre").relates(var("p")),
    ]);

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_parse_relates_type_variable() {
    let query = r#"match
$x isa $type;
$type relates someRole;"#;
    let parsed = parse_query(query).unwrap();
    let expected = typeql_match([var("x").isa(var("type")), var("type").relates("someRole")]);
    // [ThingVariable, TypeVariable] no bueno

    assert_query_eq!(expected, parsed, query);
}
 */
