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

use chrono::{NaiveDate, NaiveDateTime, NaiveTime};

use crate::{
    and,
    common::{
        token::{
            Order::{Asc, Desc},
            ValueType,
        },
        validatable::Validatable,
    },
    gte, lt, lte, not, or, parse_definables, parse_label, parse_pattern, parse_patterns, parse_queries, parse_query,
    parse_variable,
    pattern::{
        Annotation::Key, ConceptVariableBuilder, Conjunction, Disjunction, Label, RelationVariableBuilder,
        ThingVariableBuilder, TypeVariableBuilder, Variable,
    },
    query::{AggregateQueryBuilder, TypeQLDefine, TypeQLInsert, TypeQLMatch, TypeQLUndefine},
    rel, rule, type_, typeql_insert, typeql_match, var, Query,
};

macro_rules! assert_valid_eq_repr {
    ($expected:ident, $parsed:ident, $query:ident) => {
        assert!($parsed.is_valid());
        assert!($expected.is_valid());
        assert_eq!($expected, $parsed);
        assert_eq!($expected.to_string(), $query);
        assert_eq!($parsed.to_string(), $query);
    };
}

#[test]
fn test_simple_query() {
    let query = r#"match
$x isa movie;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").isa("movie"));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_named_type_variable() {
    let query = r#"match
$a type attribute_label;
get $a;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("a").type_("attribute_label")).get(["a"]);
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_parsing_string_with_slash() {
    let query = r#"match
$x isa person,
    has name "alice/bob";"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").isa("person").has(("name", "alice/bob")));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_relation_query() {
    let query = r#"match
$brando "Marl B" isa name;
(actor: $brando, $char, production-with-cast: $prod);
get $char, $prod;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(
        var("brando").eq("Marl B").isa("name"),
        rel(("actor", "brando")).rel("char").rel(("production-with-cast", "prod")),
    )
    .get(["char", "prod"]);

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_role_type_scoped_globally() {
    let query = r#"match
$m relates spouse;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("m").relates("spouse"));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_role_type_not_scoped() {
    let query = r#"match
marriage relates $s;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(type_("marriage").relates(var("s")));
    assert_valid_eq_repr!(expected, parsed, query);
}

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

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(
        var("x").isa("movie").has(("title", var("t"))),
        or!(var("t").eq("Apocalypse Now"), and!(var("t").lt("Juno"), var("t").gt("Godfather")), var("t").eq("Spy"),),
        var("t").neq("Apocalypse Now"),
    );

    assert_valid_eq_repr!(expected, parsed, query);
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

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(
        var("x").isa("movie").has(("title", var("t"))),
        or!(and!(var("t").lte("Juno"), var("t").gte("Godfather"), var("t").neq("Heat")), var("t").eq("The Muppets"),),
    );
    assert_valid_eq_repr!(expected, parsed, query);
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

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(
        rel("x").rel("y"),
        var("y").isa("person").has(("name", var("n"))),
        or!(var("n").contains("ar"), var("n").like("^M.*$")),
    );

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_predicate_query_4() {
    let query = r#"match
$x has age $y;
$y >= $z;
$z 18 isa age;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").has(("age", var("y"))), var("y").gte(var("z")), var("z").eq(18).isa("age"),);

    assert_valid_eq_repr!(expected, parsed, query);
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

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(
        var("x").sub(var("z")),
        var("y").sub(var("z")),
        var("a").isa(var("x")),
        var("b").isa(var("y")),
        not(var("x").is(var("y"))),
        not(var("a").is(var("b"))),
    );

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_value_equals_variable_query() {
    let query = r#"match
$s1 = $s2;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("s1").eq(var("s2")));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_movies_released_after_or_at_the_same_time_as_spy() {
    let query = r#"match
$x has release-date >= $r;
$_ has title "Spy",
    has release-date $r;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(
        var("x").has(("release-date", gte(var("r")))),
        var(()).has(("title", "Spy")).has(("release-date", var("r"))),
    );
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_predicate() {
    let query = r#"match
$x has release-date < 1986-03-03T00:00,
    has tmdb-vote-count 100,
    has tmdb-vote-average <= 9.0;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x")
        .has((
            "release-date",
            lt(NaiveDateTime::new(
                NaiveDate::from_ymd_opt(1986, 3, 3).unwrap(),
                NaiveTime::from_hms_opt(0, 0, 0).unwrap()
            ))
        ))
        .has(("tmdb-vote-count", 100))
        .has(("tmdb-vote-average", lte(9.0))));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_time() {
    let query = r#"match
$x has release-date 1000-11-12T13:14:15;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").has((
        "release-date",
        NaiveDateTime::new(
            NaiveDate::from_ymd_opt(1000, 11, 12).unwrap(),
            NaiveTime::from_hms_opt(13, 14, 15).unwrap()
        ),
    )));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_big_years() {
    let query = r#"match
$x has release-date +12345-12-25T00:00;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").has((
        "release-date",
        NaiveDateTime::new(NaiveDate::from_ymd_opt(12345, 12, 25).unwrap(), NaiveTime::from_hms_opt(0, 0, 0).unwrap()),
    )));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_small_years() {
    let query = r#"match
$x has release-date 0867-01-01T00:00;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").has((
        "release-date",
        NaiveDateTime::new(NaiveDate::from_ymd_opt(867, 1, 1).unwrap(), NaiveTime::from_hms_opt(0, 0, 0).unwrap()),
    )));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_negative_years() {
    let query = r#"match
$x has release-date -3200-01-01T00:00;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").has((
        "release-date",
        NaiveDateTime::new(NaiveDate::from_ymd_opt(-3200, 1, 1).unwrap(), NaiveTime::from_hms_opt(0, 0, 0).unwrap()),
    )));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_millis() {
    let query = r#"match
$x has release-date 1000-11-12T13:14:15.123;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").has((
        "release-date",
        NaiveDateTime::new(
            NaiveDate::from_ymd_opt(1000, 11, 12).unwrap(),
            NaiveTime::from_hms_milli_opt(13, 14, 15, 123).unwrap(),
        ),
    )));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_millis_shorthand() {
    let query = r#"match
$x has release-date 1000-11-12T13:14:15.1;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").has((
        "release-date",
        NaiveDateTime::new(
            NaiveDate::from_ymd_opt(1000, 11, 12).unwrap(),
            NaiveTime::from_hms_milli_opt(13, 14, 15, 100).unwrap(),
        ),
    )));

    let parsed_query = r#"match
$x has release-date 1000-11-12T13:14:15.100;"#;
    assert_valid_eq_repr!(expected, parsed, parsed_query);
}

#[test]
fn when_parsing_date_error_when_parsing_overly_precise_decimal_seconds() {
    let query = r#"match
$x has release-date 1000-11-12T13:14:15.000123456;"#;

    let parsed = parse_query(query);
    assert!(parsed.is_err());
    assert!(parsed.unwrap_err().to_string().contains("expected"));
}

#[test]
fn when_parsing_date_error_when_handling_overly_precise_nanos() {
    let validated = typeql_match!(var("x").has((
        "release-date",
        NaiveDateTime::new(
            NaiveDate::from_ymd_opt(1000, 11, 12).unwrap(),
            NaiveTime::from_hms_nano_opt(13, 14, 15, 123450000).unwrap(),
        ),
    )))
    .validated();
    assert!(validated.is_err());
    assert!(validated.unwrap_err().to_string().contains("more precise than 1 millisecond"));
}

#[test]
fn test_long_predicate_query() {
    let query = r#"match
$x isa movie,
    has tmdb-vote-count <= 400;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").isa("movie").has(("tmdb-vote-count", lte(400))));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_schema_query() {
    let query = r#"match
$x plays starring:actor;
sort $x asc;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").plays(("starring", "actor"))).sort([("x", Asc)]);

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_get_sort() {
    let query = r#"match
$x isa movie,
    has rating $r;
sort $r desc;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").isa("movie").has(("rating", var("r")))).sort([("r", Desc)]);

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_get_sort_limit() {
    let query = r#"match
$x isa movie,
    has rating $r;
sort $r; limit 10;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").isa("movie").has(("rating", var("r")))).sort("r").limit(10);

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_get_sort_offset_limit() {
    let query = r#"match
$x isa movie,
    has rating $r;
sort $r desc; offset 10; limit 10;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected =
        typeql_match!(var("x").isa("movie").has(("rating", var("r")))).sort([("r", Desc)]).offset(10).limit(10);

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_get_offset_limit() {
    let query = r#"match
$y isa movie,
    has title $n;
offset 2; limit 4;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("y").isa("movie").has(("title", var("n")))).offset(2).limit(4);

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_variables_everywhere_query() {
    let query = r#"match
($p: $x, $y);
$x isa $z;
$y "crime";
$z sub production;
has-genre relates $p;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(
        rel((var("p"), var("x"))).rel("y"),
        var("x").isa(var("z")),
        var("y").eq("crime"),
        var("z").sub("production"),
        type_("has-genre").relates(var("p")),
    );

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_parsing_relates_type_variable() {
    let query = r#"match
$x isa $type;
$type relates someRole;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").isa(var("type")), var("type").relates("someRole"));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_or_query() {
    let query = r#"match
$x isa movie;
{
    $y "drama" isa genre;
    ($x, $y);
} or {
    $x "The Muppets";
};"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(
        var("x").isa("movie"),
        or!(and!(var("y").eq("drama").isa("genre"), rel("x").rel("y")), var("x").eq("The Muppets"))
    );

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_disjunction_not_in_conjunction() {
    let query = r#"match
{
    $x isa person;
} or {
    $x isa company;
};"#;

    assert!(parse_query(query).is_err())
}

#[test]
fn test_nested_conjunction_and_disjunction() {
    let query = r#"match
$y isa $p;
{
    ($y, $q);
} or {
    $x isa $p;
    {
        $x has first-name $y;
    } or {
        $x has last-name $z;
    };
};"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(
        var("y").isa(var("p")),
        or!(
            rel("y").rel("q"),
            and!(
                var("x").isa(var("p")),
                or!(var("x").has(("first-name", var("y"))), var("x").has(("last-name", var("z"))))
            )
        )
    );

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_disjunction_not_binding_conjunction() {
    let query = r#"match
$y isa $p;
{ ($y, $q); } or { $x isa $p; { $x has first-name $y; } or { $q has last-name $z; }; };"#;
    let parsed = parse_query(query);
    assert!(parsed.is_err());
}

#[test]
fn test_aggregate_count_query() {
    let query = r#"match
($x, $y) isa friendship;
get $x, $y;
count;"#;

    let parsed = parse_query(query).unwrap().into_aggregate();
    let expected = typeql_match!(rel("x").rel("y").isa("friendship")).get(["x", "y"]).count();

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_aggregate_group_count_query() {
    let query = r#"match
($x, $y) isa friendship;
get $x, $y;
group $x; count;"#;

    let parsed = parse_query(query).unwrap().into_group_aggregate();
    let expected = typeql_match!(rel("x").rel("y").isa("friendship")).get(["x", "y"]).group("x").count();

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_single_line_group_aggregate_max_query() {
    let query = r#"match
$x has age $a;
group $x; max $a;"#;

    let parsed = parse_query(query).unwrap().into_group_aggregate();
    let expected = typeql_match!(var("x").has(("age", var("a")))).group("x").max("a");

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_multi_line_group_aggregate_max_query() {
    let query = r#"match
($x, $y) isa friendship;
$y has age $z;
group $x; max $z;"#;

    let parsed = parse_query(query).unwrap().into_group_aggregate();
    let expected =
        typeql_match!(rel("x").rel("y").isa("friendship"), var("y").has(("age", var("z")))).group("x").max("z");

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_multi_line_filtered_group_aggregate_max_query() {
    let query = r#"match
($x, $y) isa friendship;
$y has age $z;
get $x, $y, $z;
group $x; max $z;"#;

    let parsed = parse_query(query).unwrap().into_group_aggregate();
    let expected = typeql_match!(rel("x").rel("y").isa("friendship"), var("y").has(("age", var("z"))))
        .get(["x", "y", "z"])
        .group("x")
        .max("z");

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_comparing_count_query_using_typeql_and_rust_typeql_they_are_equivalent() {
    let query = r#"match
$x isa movie,
    has title "Godfather";
count;"#;

    let parsed = parse_query(query).unwrap().into_aggregate();
    let expected = typeql_match!(var("x").isa("movie").has(("title", "Godfather"))).count();

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_insert_query() {
    let query = r#"insert
$_ isa movie,
    has title "The Title";"#;

    let parsed = parse_query(query).unwrap().into_insert();
    let expected = typeql_insert!(var(()).isa("movie").has(("title", "The Title")));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_delete_query_result_is_same_as_java_typeql() {
    let query = r#"match
$x isa movie,
    has title "The Title";
$y isa movie;
delete
$x isa movie;
$y isa movie;"#;

    let parsed = parse_query(query).unwrap().into_delete();
    let expected = typeql_match!(var("x").isa("movie").has(("title", "The Title")), var("y").isa("movie"))
        .delete([var("x").isa("movie"), var("y").isa("movie")]);

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_insert_query_result_is_same_as_java_typeql() {
    let query = r#"insert
$x isa pokemon,
    has name "Pichu";
$y isa pokemon,
    has name "Pikachu";
$z isa pokemon,
    has name "Raichu";
(evolves-from: $x, evolves-to: $y) isa evolution;
(evolves-from: $y, evolves-to: $z) isa evolution;"#;

    let parsed = parse_query(query).unwrap().into_insert();
    let expected = typeql_insert!(
        var("x").isa("pokemon").has(("name", "Pichu")),
        var("y").isa("pokemon").has(("name", "Pikachu")),
        var("z").isa("pokemon").has(("name", "Raichu")),
        rel(("evolves-from", "x")).rel(("evolves-to", "y")).isa("evolution"),
        rel(("evolves-from", "y")).rel(("evolves-to", "z")).isa("evolution")
    );

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_update_query_result_is_same_as_java_typeql() {
    let query = r#"match
$x isa person,
    has name "alice",
    has age $a;
delete
$x has $a;
insert
$x has age 25;"#;

    let parsed = parse_query(query).unwrap().into_update();
    let expected = typeql_match!(var("x").isa("person").has(("name", "alice")).has(("age", var("a"))))
        .delete(var("x").has(var("a")))
        .insert(var("x").has(("age", 25)));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_as_in_define_result_is_same_as_sub() {
    let query = r#"define
parent sub role;
child sub role;
parenthood sub relation,
    relates parent,
    relates child;
fatherhood sub parenthood,
    relates father as parent,
    relates son as child;"#;

    let parsed = parse_query(query).unwrap().into_define();
    let expected = typeql_define!(
        type_("parent").sub("role"),
        type_("child").sub("role"),
        type_("parenthood").sub("relation").relates("parent").relates("child"),
        type_("fatherhood").sub("parenthood").relates(("father", "parent")).relates(("son", "child"))
    );

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_as_in_match_result_is_same_as_sub() {
    let query = r#"match
$f sub parenthood,
    relates father as parent,
    relates son as child;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("f").sub("parenthood").relates(("father", "parent")).relates(("son", "child")));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_define_query_with_owns_overrides_result_is_same_as_java_typeql() {
    let query = r#"define
triangle sub entity;
triangle owns side-length;
triangle-right-angled sub triangle;
triangle-right-angled owns hypotenuse-length as side-length;"#;

    let parsed = parse_query(query).unwrap().into_define();
    let expected = typeql_define!(
        type_("triangle").sub("entity"),
        type_("triangle").owns("side-length"),
        type_("triangle-right-angled").sub("triangle"),
        type_("triangle-right-angled").owns(("hypotenuse-length", "side-length"))
    );

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_define_query_with_relates_overrides_result_is_same_as_java_typeql() {
    let query = r#"define
pokemon sub entity;
evolves sub relation;
evolves relates from,
    relates to;
evolves-final sub evolves;
evolves-final relates from-final as from;"#;

    let parsed = parse_query(query).unwrap().into_define();
    let expected = typeql_define!(
        type_("pokemon").sub("entity"),
        type_("evolves").sub("relation"),
        type_("evolves").relates("from").relates("to"),
        type_("evolves-final").sub("evolves"),
        type_("evolves-final").relates(("from-final", "from"))
    );

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_define_query_with_plays_overrides_result_is_same_as_java_typeql() {
    let query = r#"define
pokemon sub entity;
evolves sub relation;
evolves relates from,
    relates to;
evolves-final sub evolves;
evolves-final relates from-final as from;
pokemon plays evolves-final:from-final as from;"#;

    let parsed = parse_query(query).unwrap().into_define();
    let expected = typeql_define!(
        type_("pokemon").sub("entity"),
        type_("evolves").sub("relation"),
        type_("evolves").relates("from").relates("to"),
        type_("evolves-final").sub("evolves"),
        type_("evolves-final").relates(("from-final", "from")),
        type_("pokemon").plays(("evolves-final", "from-final", "from"))
    );

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_define_query_result_is_same_as_java_typeql() {
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

    let parsed = parse_query(query).unwrap().into_define();
    let expected = typeql_define!(
        type_("pokemon").sub("entity"),
        type_("evolution").sub("relation"),
        type_("evolves-from").sub("role"),
        type_("evolves-to").sub("role"),
        type_("evolves").relates("from").relates("to"),
        type_("pokemon").plays(("evolves", "from")).plays(("evolves", "to")).owns("name")
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

    let parsed = parse_query(query).unwrap().into_undefine();
    let expected = typeql_undefine!(
        type_("pokemon").sub("entity"),
        type_("evolution").sub("relation"),
        type_("evolves-from").sub("role"),
        type_("evolves-to").sub("role"),
        type_("evolves").relates("from").relates("to"),
        type_("pokemon").plays(("evolves", "from")).plays(("evolves", "to")).owns("name")
    );

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_undefine_rule() {
    let query = r#"undefine
rule r;"#;

    let parsed = parse_query(query).unwrap().into_undefine();
    let expected = typeql_undefine!(rule("r"));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_match_insert_query() {
    let query = r#"match
$x isa language;
insert
$x has name "HELLO";"#;

    let parsed = parse_query(query).unwrap().into_insert();
    let expected = typeql_match!(var("x").isa("language")).insert(var("x").has(("name", "HELLO")));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_define_abstract_entity_query() {
    let query = r#"define
concrete-type sub entity;
abstract-type sub entity,
    abstract;"#;

    let parsed = parse_query(query).unwrap().into_define();
    let expected =
        typeql_define!(type_("concrete-type").sub("entity"), type_("abstract-type").sub("entity").abstract_());

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_match_value_type_query() {
    let query = r#"match
$x value double;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").value(ValueType::Double));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_parsing_without_var() {
    let query = r#"match
$_ isa person;"#;

    let parsed = parse_query(query);
    assert!(parsed.is_err());
    let built = typeql_match!(var(()).isa("person")).validated();
    assert!(built.is_err());
}

#[test]
fn when_parsing_date_keyword_parse_as_the_correct_value_type() {
    let query = r#"match
$x value datetime;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").value(ValueType::DateTime));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_define_value_type_query() {
    let query = r#"define
my-type sub attribute,
    value long;"#;

    let parsed = parse_query(query).unwrap().into_define();
    let expected = typeql_define!(type_("my-type").sub("attribute").value(ValueType::Long));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_escape_string() {
    let input = r#"This has \"double quotes\" and a single-quoted backslash: '\\'"#;

    let query = format!(
        r#"insert
$_ isa movie,
    has title "{input}";"#
    );

    let parsed = parse_query(&query).unwrap().into_insert();
    let expected = typeql_insert!(var(()).isa("movie").has(("title", input)));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_query_with_comments_they_are_ignored() {
    let query = "match\n\n# there's a comment here\n$x isa###WOW HERES ANOTHER###\r\nmovie; count;";
    let uncommented = r#"match
$x isa movie;
count;"#;

    let parsed = parse_query(query).unwrap().into_aggregate();
    let expected = typeql_match!(var("x").isa("movie")).count();

    assert_valid_eq_repr!(expected, parsed, uncommented);
}

#[test]
fn test_parsing_pattern() {
    let pattern = r#"{
    (wife: $a, husband: $b) isa marriage;
    $a has gender "male";
    $b has gender "female";
}"#;

    let parsed = parse_pattern(pattern).unwrap().into_conjunction();
    let expected = and!(
        rel(("wife", "a")).rel(("husband", "b")).isa("marriage"),
        var("a").has(("gender", "male")),
        var("b").has(("gender", "female"))
    );

    assert_valid_eq_repr!(expected, parsed, pattern);
}

#[test]
fn test_parsing_patterns() {
    let patterns = r#"(wife: $a, husband: $b) isa marriage;
    $a has gender "male";
    $b has gender "female";
"#;

    let parsed = parse_patterns(patterns).unwrap().into_iter().map(|p| p.into_variable()).collect::<Vec<_>>();
    let expected: Vec<Variable> = vec![
        Variable::Thing(rel(("wife", "a")).rel(("husband", "b")).isa("marriage")),
        Variable::Thing(var("a").has(("gender", "male"))),
        Variable::Thing(var("b").has(("gender", "female"))),
    ];

    assert_eq!(expected, parsed);
}

#[test]
fn test_define_rules() {
    let query = r#"define
rule a-rule: when {
    $x isa person;
    not {
        $x has name "Alice";
        $x has name "Bob";
    };
    {
        ($x) isa friendship;
    } or {
        ($x) isa employment;
    };
} then {
    $x has is_interesting true;
};"#;

    let parsed = parse_query(query).unwrap().into_define();
    let expected = typeql_define!(rule("a-rule")
        .when(and!(
            var("x").isa("person"),
            not(and!(var("x").has(("name", "Alice")), var("x").has(("name", "Bob")))),
            or!(rel(var("x")).isa("friendship"), rel(var("x")).isa("employment"))
        ))
        .then(var("x").has(("is_interesting", true))));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_parsing_definables() {
    let query = r#"athlete sub person;
      runner sub athlete;
      sprinter sub runner;"#;

    let parsed = parse_definables(query).unwrap().into_iter().map(|p| p.into_type_variable()).collect::<Vec<_>>();
    let expected =
        vec![type_("athlete").sub("person"), type_("runner").sub("athlete"), type_("sprinter").sub("runner")];

    assert_eq!(expected, parsed);
}

#[test]
fn test_parsing_variable_rel() {
    let variable = "(wife: $a, husband: $b) isa marriage";

    let parsed = parse_variable(variable).unwrap();
    if let Variable::Thing(parsed_var) = parsed {
        let expected = rel(("wife", "a")).rel(("husband", "b")).isa("marriage");
        assert_valid_eq_repr!(expected, parsed_var, variable);
    } else {
        panic!("Expected ThingVariable, found {variable:?}.");
    }
}

#[test]
fn test_parsing_variable_has() {
    let variable = "$x has is_interesting true";

    let parsed = parse_variable(variable).unwrap();
    if let Variable::Thing(parsed_var) = parsed {
        let expected = var("x").has(("is_interesting", true));
        assert_valid_eq_repr!(expected, parsed_var, variable);
    } else {
        panic!("Expected ThingVariable, found {variable:?}.");
    }
}

#[test]
fn test_parsing_label() {
    let label = "label_with-symbols";

    let parsed = parse_label(label).unwrap();
    let expected = Label { scope: None, name: String::from(label) };
    assert_eq!(expected, parsed);
}

#[test]
fn test_parsing_boolean() {
    let query = r#"insert
$_ has flag true;"#;

    let parsed = parse_query(query).unwrap().into_insert();
    let expected = typeql_insert!(var(()).has(("flag", true)));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_parsing_aggregate_group() {
    let query = r#"match
$x isa movie;
group $x;"#;

    let parsed = parse_query(query).unwrap().into_group();
    let expected = typeql_match!(var("x").isa("movie")).group("x");

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_parsing_aggregate_group_count() {
    let query = r#"match
$x isa movie;
group $x; count;"#;

    let parsed = parse_query(query).unwrap().into_group_aggregate();
    let expected = typeql_match!(var("x").isa("movie")).group("x").count();

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_parsing_aggregate_std() {
    let query = r#"match
$x isa movie;
std $x;"#;

    let parsed = parse_query(query).unwrap().into_aggregate();
    let expected = typeql_match!(var("x").isa("movie")).std("x");

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_parsing_aggregate_to_string() {
    let query = r#"match
$x isa movie;
get $x;
group $x; count;"#;

    assert_eq!(query, parse_query(query).unwrap().to_string());
}

#[test]
fn when_parsing_incorrect_syntax_throw_typeql_syntax_exception_with_helpful_error() {
    let parsed = parse_query("match\n$x isa");
    assert!(parsed.is_err());
    let report = parsed.unwrap_err().to_string();
    assert!(report.contains("syntax error"));
    assert!(report.contains("line 2"));
    assert!(report.contains("\n$x isa"));
    assert!(report.contains("\n      ^"));
}

#[test]
fn when_parsing_incorrect_syntax_trailing_query_whitespace_is_ignored() {
    let parsed = parse_query("match\n$x isa \n");
    assert!(parsed.is_err());
    let report = parsed.unwrap_err().to_string();
    assert!(report.contains("syntax error"));
    assert!(report.contains("line 2"));
    assert!(report.contains("\n$x isa"));
    assert!(report.contains("\n      ^"));
}

#[test]
fn when_parsing_incorrect_syntax_error_message_should_retain_whitespace() {
    let parsed = parse_query("match\n$x isa ");
    assert!(parsed.is_err());
    let report = parsed.unwrap_err().to_string();
    assert!(!report.contains("match$xisa"));
}

#[test]
fn test_syntax_error_pointer() {
    let parsed = parse_query("match\n$x of");
    assert!(parsed.is_err());
    let report = parsed.unwrap_err().to_string();
    assert!(report.contains("\n$x of"));
    assert!(report.contains("\n   ^"));
}

#[test]
fn test_has_variable() {
    let query = r#"match
$_ has title "Godfather",
    has tmdb-vote-count $x;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var(()).has(("title", "Godfather")).has(("tmdb-vote-count", var("x"))));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_regex_attribute_type() {
    let query = r#"match
$x regex "(fe)male";"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").regex("(fe)male"));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_typeql_parsing_query() {
    assert!(matches!(parse_query("match\n$x isa movie;"), Ok(Query::Match(_))));
}

#[test]
fn test_parsing_key() {
    let query = r#"match
$x owns name @key;
get $x;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").owns(("name", Key))).get(["x"]);
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_parsing_empty_string() {
    assert!(parse_query("").is_err());
}

#[test]
fn test_parsing_list_one_match() {
    let queries = "match $y isa movie;";
    let parsed = parse_queries(queries).unwrap().map(|q| q.unwrap().into_match()).collect::<Vec<_>>();
    let expected = vec![typeql_match!(var("y").isa("movie"))];
    assert_eq!(parsed, expected);
}

#[test]
fn test_parsing_list_one_insert() {
    let queries = "insert $x isa movie;";
    let parsed = parse_queries(queries).unwrap().map(|q| q.unwrap().into_insert()).collect::<Vec<_>>();
    let expected = vec![typeql_insert!(var("x").isa("movie"))];
    assert_eq!(parsed, expected);
}

#[test]
fn test_parsing_list_one_insert_with_whitespace_prefix() {
    let queries = " insert $x isa movie;";
    let parsed = parse_queries(queries).unwrap().map(|q| q.unwrap().into_insert()).collect::<Vec<_>>();
    let expected = vec![typeql_insert!(var("x").isa("movie"))];
    assert_eq!(parsed, expected);
}

#[test]
fn test_parsing_list_one_insert_with_prefix_comment() {
    let queries = r#"#hola
insert $x isa movie;"#;
    let parsed = parse_queries(queries).unwrap().map(|q| q.unwrap().into_insert()).collect::<Vec<_>>();
    let expected = vec![typeql_insert!(var("x").isa("movie"))];
    assert_eq!(parsed, expected);
}

#[test]
fn test_parsing_list() {
    let queries = "insert $x isa movie; match $y isa movie;";
    let parsed = parse_queries(queries).unwrap().collect::<Result<Vec<_>, _>>().unwrap();
    let expected = vec![typeql_insert!(var("x").isa("movie")).into(), typeql_match!(var("y").isa("movie")).into()];
    assert_eq!(parsed, expected);
}

#[test]
fn test_parsing_many_match_insert_without_stack_overflow() {
    let num_queries = 10_000;
    let query = "match\n$x isa person; insert $x has name 'bob';\n";
    let queries = query.repeat(num_queries);

    let mut parsed = Vec::with_capacity(num_queries);
    parsed.extend(parse_queries(&queries).unwrap().map(|q| q.unwrap().into_insert()));

    let expected = typeql_match!(var("x").isa("person")).insert(var("x").has(("name", "bob")));

    assert_eq!(vec![expected; num_queries], parsed);
}

#[test]
fn when_parsing_list_of_queries_with_syntax_error_report_error() {
    let query_text = "define\nperson sub entity has name;"; // note no comma
    let parsed = parse_query(query_text);
    assert!(parsed.is_err());
    assert!(parsed.unwrap_err().to_string().contains("\nperson sub entity has name;"));
}

#[test]
fn when_parsing_multiple_queries_like_one_throw() {
    assert!(parse_query("insert\n$x isa movie; insert $y isa movie").is_err());
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

#[test]
fn when_parsing_aggregate_with_wrong_variable_argument_number_throw() {
    assert!(parse_query("match\n$x isa name; group;").is_err());
}

#[test]
fn when_parsing_aggregate_with_wrong_name_throw() {
    assert!(parse_query("match\n$x isa name; hello $x;").is_err());
}

#[test]
fn define_attribute_type_regex() {
    let query = r#"define
digit sub attribute,
    regex "\d";"#;

    let parsed = parse_query(query).unwrap().into_define();
    let expected = typeql_define!(type_("digit").sub("attribute").regex(r#"\d"#));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn undefine_attribute_type_regex() {
    let query = r#"undefine
digit regex "\d";"#;

    let parsed = parse_query(query).unwrap().into_undefine();
    let expected = typeql_undefine!(type_("digit").regex(r#"\d"#));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn regex_predicate_parses_character_classes_correctly() {
    let query = r#"match
$x like "\d";"#;
    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").like("\\d"));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn regex_predicate_parses_quotes_correctly() {
    let query = r#"match
$x like "\"";"#;
    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").like("\\\""));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn regex_predicate_parses_backslashes_correctly() {
    let query = r#"match
$x like "\\";"#;
    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").like("\\\\"));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn regex_predicate_parses_newline_correctly() {
    let query = r#"match
$x like "\n";"#;
    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").like("\\n"));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn regex_predicate_parses_forward_slashes_correctly() {
    let query = r#"match
$x like "\/";"#;
    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").like("/"));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_value_equality_to_string_create_valid_query_string() {
    let expected = typeql_match!(var("x").eq(var("y")));
    let parsed = parse_query(&expected.to_string()).unwrap().into_match();

    assert_eq!(expected, parsed);
}

#[test]
fn test_iid_constraint() {
    let iid = "0x0123456789abcdefdeadbeef";
    let query = format!(
        r#"match
$x iid {iid};"#
    );

    let parsed = parse_query(&query).unwrap().into_match();
    let expected = typeql_match!(var("x").iid(iid));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_invalid_iid_throw() {
    let iid = "invalid";
    let query = format!(
        r#"match
$x iid {iid};"#
    );

    let parsed = parse_query(&query);
    assert!(parsed.is_err());
}

#[test]
fn when_building_invalid_iid_throw() {
    let iid = "invalid";
    let expected = typeql_match!(var("x").iid(iid)).validated();
    assert!(expected.is_err());
}
