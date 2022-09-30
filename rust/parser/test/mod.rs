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
    parse_query, rel, type_, typeql_match, var, MatchQueryBuilder, Query, RelationVariableBuilder,
    ThingVariableBuilder, TypeVariableBuilder, KEY,
};
use chrono::{NaiveDate, NaiveDateTime, NaiveTime};

macro_rules! assert_query_eq {
    ($expected:ident, $parsed:ident, $query:ident) => {
        assert_eq!($expected.as_ref().unwrap(), $parsed.as_ref().unwrap()); // TODO remove unwraps when `typeql_match` returns ErrorMessage
        assert_eq!($expected.as_ref().unwrap().to_string(), $query);
        assert_eq!($parsed.as_ref().unwrap().to_string(), $query);
    };
}

#[test]
fn test_simple_query() {
    let query = r#"match
$x isa movie;"#;

    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(var("x").isa("movie"));
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_named_type_variable() {
    let query = r#"match
$a type attribute_label;
get $a;"#;

    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(var("a").type_("attribute_label")).get(["a"]);
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_parse_string_with_slash() {
    let query = r#"match
$x isa person,
    has name "alice/bob";"#;

    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(var("x").isa("person").has("name", "alice/bob"));
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_relation_query() {
    let query = r#"match
$brando "Marl B" isa name;
(actor: $brando, $char, production-with-cast: $prod);
get $char, $prod;"#;

    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match([
        var("brando").eq("Marl B").isa("name"),
        rel(("actor", "brando")).rel("char").rel(("production-with-cast", "prod")),
    ])
    .get(["char", "prod"]);

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_role_type_scoped_globally() {
    let query = r#"match
$m relates spouse;"#;

    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(var("m").relates("spouse"));
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_role_type_not_scoped() {
    let query = r#"match
marriage relates $s;"#;

    let parsed = parse_query(query).map(Query::into_match);
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

    let parsed = parse_query(query).map(Query::into_match);
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

    let parsed = parse_query(query).map(Query::into_match);
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

    let parsed = parse_query(query).map(Query::into_match);
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

    let parsed = parse_query(query).map(Query::into_match);
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

    let parsed = parse_query(query).map(Query::into_match);
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

    let parsed = parse_query(query).map(Query::into_match);
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

    let parsed = parse_query(query).map(Query::into_match);
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

    let parsed = parse_query(query).map(Query::into_match);
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

    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(var("x").has(
        "release-date",
        NaiveDateTime::new(NaiveDate::from_ymd(1000, 11, 12), NaiveTime::from_hms(13, 14, 15)),
    ));

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_big_years() {
    let query = r#"match
$x has release-date +12345-12-25T00:00;"#;

    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(var("x").has(
        "release-date",
        NaiveDateTime::new(NaiveDate::from_ymd(12345, 12, 25), NaiveTime::from_hms(0, 0, 0)),
    ));

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_small_years() {
    let query = r#"match
$x has release-date 0867-01-01T00:00;"#;

    let parsed = parse_query(query).map(Query::into_match);
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

    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(var("x").has(
        "release-date",
        NaiveDateTime::new(NaiveDate::from_ymd(-3200, 1, 1), NaiveTime::from_hms(0, 0, 0)),
    ));

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_millis() {
    let query = r#"match
$x has release-date 1000-11-12T13:14:15.123;"#;

    let parsed = parse_query(query).map(Query::into_match);
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
    let query = r#"match
$x has release-date 1000-11-12T13:14:15.1;"#;

    let parsed = parse_query(query).map(Query::into_match);
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

    let parsed = parse_query(query).map(Query::into_match);
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
            NaiveTime::from_hms_nano(13, 14, 15, 123450000),
        ),
    ));
    match expected {
        Err(err) => assert!(err.contains("more precise than 1 millisecond")),
        Ok(_) => assert!(false),
    }
}

#[test]
fn test_long_predicate_query() {
    let query = r#"match
$x isa movie,
    has tmdb-vote-count <= 400;"#;

    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(var("x").isa("movie").has("tmdb-vote-count", lte(400)));

    assert_query_eq!(expected, parsed, query);
}
*/

#[test]
fn test_schema_query() {
    let query = r#"match
$x plays starring:actor;
sort $x asc;"#;

    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(var("x").plays(("starring", "actor"))).sort([("x", "asc")]);

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_get_sort() {
    let query = r#"match
$x isa movie,
    has rating $r;
sort $r desc;"#;

    let parsed = parse_query(query).map(Query::into_match);
    let expected =
        typeql_match(var("x").isa("movie").has("rating", var("r"))).sort([("r", "desc")]);

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_get_sort_limit() {
    let query = r#"match
$x isa movie,
    has rating $r;
sort $r; limit 10;"#;

    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(var("x").isa("movie").has("rating", var("r"))).sort("r").limit(10);

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_get_sort_offset_limit() {
    let query = r#"match
$x isa movie,
    has rating $r;
sort $r desc; offset 10; limit 10;"#;

    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(var("x").isa("movie").has("rating", var("r")))
        .sort([("r", "desc")])
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

    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(var("y").isa("movie").has("title", var("n"))).offset(2).limit(4);

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_variables_everywhere_query() {
    let query = r#"match
($p: $x, $y);
$x isa $z;
$y "crime";
$z sub production;
has-genre relates $p;"#;

    let parsed = parse_query(query).map(Query::into_match);
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
    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match([var("x").isa(var("type")), var("type").relates("someRole")]);

    assert_query_eq!(expected, parsed, query);
}

/*
#[test]
fn test_or_query() {
    let query = "match\n" +
            "$x isa movie;\n" +
            "{\n" +
            "    $y 'drama' isa genre;\n" +
            "    ($x, $y);\n" +
            "} or {\n" +
            "    $x 'The Muppets';\n" +
            "};";
    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(
            var("x").isa("movie"),
            or(
                    and(
                            var("y").eq("drama").isa("genre"),
                            rel("x").rel("y")
                    ),
                    var("x").eq("The Muppets")
            )
    );

    assert_query_eq!(expected, parsed, query.replace("'", "\""));
}

#[test]
fn test_disjunction_not_in_conjunction() {
    String query = "match\n" +
            "{\n" +
            "    $x isa person;\n" +
            "} or {\n" +
            "    $x isa company;\n" +
            "};";
    assertThrows(() -> parse_query(query));
}

#[test]
fn test_nested_conjunction_and_disjunction() {
    let query = "match\n" +
            "$y isa $p;\n" +
            "{\n" +
            "    ($y, $q);\n" +
            "} or {\n" +
            "    $x isa $p;\n" +
            "    {\n" +
            "        $x has first-name $y;\n" +
            "    } or {\n" +
            "        $x has last-name $z;\n" +
            "    };\n" +
            "};";
    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(
            var("y").isa(var("p")),
            or(rel("y").rel("q"),
               and(var("x").isa(var("p")),
                   or(var("x").has("first-name", var("y")),
                      var("x").has("last-name", var("z"))))));
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_disjunction_not_binding_conjunction() {
    let query = "match\n" +
            "$y isa $p;\n" +
            "{ ($y, $q); } or { $x isa $p; { $x has first-name $y; } or { $q has last-name $z; }; };";
    assertThrows(() -> parse_query(query));
}

#[test]
fn test_aggregate_count_query() {
    let query = "match\n" +
            "($x, $y) isa friendship;\n" +
            "get $x, $y;\n" +
            "count;";
    let parsed = parse_query(query).unwrapAggregate();
    let expected = typeql_match(rel("x").rel("y").isa("friendship")).get("x", "y").count();

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_aggregate_group_count_query() {
    let query = "match\n" +
            "($x, $y) isa friendship;\n" +
            "get $x, $y;\n" +
            "group $x; count;";
    let parsed = parse_query(query).unwrapGroupAggregate();
    let expected = typeql_match(rel("x").rel("y").isa("friendship")).get("x", "y").group("x").count();

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_single_line_group_aggregate_max_query() {
    let query = "match\n" +
            "$x has age $a;\n" +
            "group $x; max $a;";
    let parsed = parse_query(query).unwrapGroupAggregate();
    let expected = typeql_match(var("x").has("age", var("a"))).group("x").max("a");

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_multi_line_group_aggregate_max_query() {
    let query = "match\n" +
            "($x, $y) isa friendship;\n" +
            "$y has age $z;\n" +
            "group $x; max $z;";
    let parsed = parse_query(query).unwrapGroupAggregate();
    let expected = typeql_match(
            rel("x").rel("y").isa("friendship"),
            var("y").has("age", var("z"))
    ).group("x").max("z");

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_multi_line_filtered_group_aggregate_max_query() {
    let query = "match\n" +
            "($x, $y) isa friendship;\n" +
            "$y has age $z;\n" +
            "get $x, $y, $z;\n" +
            "group $x; max $z;";
    let parsed = parse_query(query).unwrapGroupAggregate();
    let expected = typeql_match(
            rel("x").rel("y").isa("friendship"),
            var("y").has("age", var("z"))
    ).get("x", "y", "z").group("x").max("z");

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_comparing_count_query_using_typeql_and_java_typeql_they_are_equivalent() {
    let query = "match\n" +
            "$x isa movie,\n" +
            "    has title \"Godfather\";\n" +
            "count;";
    let parsed = parse_query(query).unwrapAggregate();
    let expected = typeql_match(var("x").isa("movie").has("title", "Godfather")).count();

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_insert_query() {
    let query = "insert\n$_ isa movie,\n" +
            "    has title \"The Title\";";
    let parsed = parse_query(query).asInsert();
    let expected = insert(var().isa("movie").has("title", "The Title"));

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_delete_query_result_is_same_as_java_type_ql() {
    let query = "match\n" +
            "$x isa movie,\n" +
            "    has title 'The Title';\n" +
            "$y isa movie;\n" +
            "delete\n" +
            "$x isa movie;\n" +
            "$y isa movie;";
    let parsed = parse_query(query).asDelete();
    let expected = typeql_match(
            var("x").isa("movie").has("title", "The Title"),
            var("y").isa("movie")
    ).delete(var("x").isa("movie"), var("y").isa("movie"));

    assert_query_eq!(expected, parsed, query.replace("'", "\""));
}

#[test]
fn when_parsing_insert_query_result_is_same_as_java_type_ql() {
    let query = "insert\n" +
            "$x isa pokemon,\n" +
            "    has name 'Pichu';\n" +
            "$y isa pokemon,\n" +
            "    has name 'Pikachu';\n" +
            "$z isa pokemon,\n" +
            "    has name 'Raichu';\n" +
            "(evolves-from: $x, evolves-to: $y) isa evolution;\n" +
            "(evolves-from: $y, evolves-to: $z) isa evolution;";
    let parsed = parse_query(query).asInsert();
    let expected = insert(
            var("x").isa("pokemon").has("name", "Pichu"),
            var("y").isa("pokemon").has("name", "Pikachu"),
            var("z").isa("pokemon").has("name", "Raichu"),
            rel("evolves-from", "x").rel("evolves-to", "y").isa("evolution"),
            rel("evolves-from", "y").rel("evolves-to", "z").isa("evolution")
    );

    assert_query_eq!(expected, parsed, query.replace("'", "\""));
}

#[test]
fn when_parsing_update_query_result_issame_as_java_type_ql() {
    String query = "match\n" +
            "$x isa person,\n" +
            "    has name 'alice',\n" +
            "    has age $a;\n" +
            "delete\n" +
            "$x has $a;\n" +
            "insert\n" +
            "$x has age 25;";
    let parsed = parse_query(query).asUpdate();
    let expected = typeql_match(var("x").isa("person").has("name", "alice").has("age", var("a")))
            .delete(var("x").has(var("a")))
            .insert(var("x").has("age", 25));
    assert_query_eq!(expected, parsed, query.replace("'", "\""));
}

#[test]
fn when_parsing_as_in_define_result_is_same_as_sub() {
    let query = "define\n" +
            "parent sub role;\n" +
            "child sub role;\n" +
            "parenthood sub relation,\n" +
            "    relates parent,\n" +
            "    relates child;\n" +
            "fatherhood sub parenthood,\n" +
            "    relates father as parent,\n" +
            "    relates son as child;";
    let parsed = parse_query(query).asDefine();

    let expected = define(
            type_("parent").sub("role"),
            type_("child").sub("role"),
            type_("parenthood").sub("relation")
                    .relates("parent")
                    .relates("child"),
            type_("fatherhood").sub("parenthood")
                    .relates("father", "parent")
                    .relates("son", "child")
    );

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_as_in_match_result_is_same_as_sub() {
    let query = "match\n" +
            "$f sub parenthood,\n" +
            "    relates father as parent,\n" +
            "    relates son as child;";
    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(
            var("f").sub("parenthood")
                    .relates("father", "parent")
                    .relates("son", "child")
    );

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_define_query_with_owns_overrides_result_is_same_as_java_type_ql() {
    let query = "define\n" +
            "triangle sub entity;\n" +
            "triangle owns side-length;\n" +
            "triangle-right-angled sub triangle;\n" +
            "triangle-right-angled owns hypotenuse-length as side-length;";
    let parsed = parse_query(query).asDefine();

    let expected = define(
            type_("triangle").sub("entity"),
            type_("triangle").owns("side-length"),
            type_("triangle-right-angled").sub("triangle"),
            type_("triangle-right-angled").owns("hypotenuse-length", "side-length")
    );
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_define_query_with_relates_overrides_result_is_same_as_java_type_ql() {
    let query = "define\n" +
            "pokemon sub entity;\n" +
            "evolves sub relation;\n" +
            "evolves relates from,\n" +
            "    relates to;\n" +
            "evolves-final sub evolves;\n" +
            "evolves-final relates from-final as from;";
    let parsed = parse_query(query).asDefine();

    let expected = define(
            type_("pokemon").sub("entity"),
            type_("evolves").sub("relation"),
            type_("evolves").relates("from").relates("to"),
            type_("evolves-final").sub("evolves"),
            type_("evolves-final").relates("from-final", "from")
    );
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_define_query_with_plays_overrides_result_is_same_as_java_type_ql() {
    let query = "define\n" +
            "pokemon sub entity;\n" +
            "evolves sub relation;\n" +
            "evolves relates from,\n" +
            "    relates to;\n" +
            "evolves-final sub evolves;\n" +
            "evolves-final relates from-final as from;\n" +
            "pokemon plays evolves-final:from-final as from;";
    let parsed = parse_query(query).asDefine();

    let expected = define(
            type_("pokemon").sub("entity"),
            type_("evolves").sub("relation"),
            type_("evolves").relates("from").relates("to"),
            type_("evolves-final").sub("evolves"),
            type_("evolves-final").relates("from-final", "from"),
            type_("pokemon").plays("evolves-final", "from-final", "from")
    );
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_define_query_result_is_same_as_java_type_ql() {
    let query = "define\n" +
            "pokemon sub entity;\n" +
            "evolution sub relation;\n" +
            "evolves-from sub role;\n" +
            "evolves-to sub role;\n" +
            "evolves relates from,\n" +
            "    relates to;\n" +
            "pokemon plays evolves:from,\n" +
            "    plays evolves:to,\n" +
            "    owns name;";
    let parsed = parse_query(query).asDefine();

    let expected = define(
            type_("pokemon").sub("entity"),
            type_("evolution").sub("relation"),
            type_("evolves-from").sub("role"),
            type_("evolves-to").sub("role"),
            type_("evolves").relates("from").relates("to"),
            type_("pokemon").plays("evolves", "from").plays("evolves", "to").owns("name")
    );

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_undefine_query_result_is_same_as_java_type_ql() {
    let query = "undefine\n" +
            "pokemon sub entity;\n" +
            "evolution sub relation;\n" +
            "evolves-from sub role;\n" +
            "evolves-to sub role;\n" +
            "evolves relates from,\n" +
            "    relates to;\n" +
            "pokemon plays evolves:from,\n" +
            "    plays evolves:to,\n" +
            "    owns name;";
    let parsed = parse_query(query).asUndefine();

    let expected = undefine(
            type_("pokemon").sub("entity"),
            type_("evolution").sub("relation"),
            type_("evolves-from").sub("role"),
            type_("evolves-to").sub("role"),
            type_("evolves").relates("from").relates("to"),
            type_("pokemon").plays("evolves", "from").plays("evolves", "to").owns("name")
    );

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_match_insert_query() {
    let query = "match\n" +
            "$x isa language;\n" +
            "insert\n$x has name \"HELLO\";";
    let parsed = parse_query(query).asInsert();
    let expected = typeql_match(var("x").isa("language"))
            .insert(var("x").has("name", "HELLO"));

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_define_abstract_entity_query() {
    let query = "define\n" +
            "concrete-type_ sub entity;\n" +
            "abstract-type_ sub entity,\n" +
            "    abstract;";
    let parsed = parse_query(query).asDefine();
    let expected = define(
            type_("concrete-type").sub("entity"),
            type_("abstract-type").sub("entity").isAbstract()
    );

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_match_value_type_query() {
    let query = "match\n$x value double;";
    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(var("x").value(TypeQLArg.ValueType.DOUBLE));

    assert_query_eq!(expected, parsed, query);
}
 */

// #[test]
fn test_parse_without_var() {
    let query = r#"match
$_ isa person;"#;

    let parsed = parse_query(query); // todo error
    assert!(parsed.is_err());
    let built = typeql_match(var(()).isa("person")); // todo error
    assert!(built.is_err());
}

/*
#[test]
fn when_parsing_date_keyword_parse_as_the_correct_value_type() {
    let query = "typeql_match\n$x value datetime;";
    let parsed = parse_query(query).asMatch();
    let expected = typeql_match(var("x").value(TypeQLArg.ValueType.DATETIME));

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_define_value_type_query() {
    let query = "define\n" +
            "my-type sub attribute,\n" +
            "    value long;";
    let parsed = parse_query(query).asDefine();
    let expected = define(type_("my-type").sub("attribute").value(TypeQLArg.ValueType.LONG));

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_escape_string() {
    // ANTLR will see this as a string that looks like:
    // "This has \"double quotes\" and a single-quoted backslash: '\\'"
    let input = r#"This has \"double quotes\" and a single-quoted backslash: '\\'"#;

    let query = "insert\n" +
            "$_ isa movie,\n" +
            "    has title \"" + input + "\";";
    let parsed = parse_query(query);
    let expected = insert(var(()).isa("movie").has("title", input));

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_query_with_comments_they_are_ignored() {
    let query = "match\n" +
            "\n# there's a comment here\n$x isa###WOW HERES ANOTHER###\r\nmovie; count;";
    let uncommented = "match\n$x isa movie;\ncount;";

    let parsed = parse_query(query).asMatchAggregate();
    let expected = typeql_match(var("x").isa("movie")).count();

    assert_query_eq!(expected, parsed, uncommented);
}

#[test]
fn test_parsing_pattern() {
    let pattern = "{\n" +
            "    (wife: $a, husband: $b) isa marriage;\n" +
            "    $a has gender 'male';\n" +
            "    $b has gender 'female';\n" +
            "}";
    let parsed = parse_pattern(pattern);
    let expected = and(
            rel("wife", "a").rel("husband", "b").isa("marriage"),
            var("a").has("gender", "male"),
            var("b").has("gender", "female")
    );

    assert_query_eq!(expected, parsed, pattern.replace("'", "\""));
}

#[test]
fn test_define_rules() {
    let when = "$x isa movie;";
    let then = "$x has genre 'drama';";
    let when_pattern = and((var("x").isa("movie")));
    let then_pattern = var("x").has("genre", "drama");

    let expected = define(rule("all-movies-are-drama").when(when_pattern).then(then_pattern));
    let query = "define\n" +
            "rule all-movies-are-drama:\n" +
            "    when {\n" +
            "        " + when + "\n" +
            "    }\n" +
            "    then {\n" +
            "        " + then + "\n" +
            "    };";
    let parsed = parse_query(query).asDefine();

    assert_query_eq!(expected, parsed, query.replace("'", "\""));
}

#[test]
fn test_parse_boolean() {
    let query = "insert\n$_ has flag true;";
    let parsed = parse_query(query).asInsert();
    let expected = insert(var(()).has("flag", true));

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_parse_aggregate_group() {
    let query = "match\n" +
            "$x isa movie;\n" +
            "group $x;";
    let parsed = parse_query(query).asMatchGroup();
    let expected = typeql_match(var("x").isa("movie")).group("x");

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_parse_aggregate_group_count() {
    let query = "match\n" +
            "$x isa movie;\n" +
            "group $x; count;";
    let parsed = parse_query(query).asMatchGroupAggregate();
    let expected = typeql_match(var("x").isa("movie")).group("x").count();

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_parse_aggregate_std() {
    let query = "match\n" +
            "$x isa movie;\n" +
            "std $x;";
    let parsed = parse_query(query).asMatchAggregate();
    let expected = typeql_match(var("x").isa("movie")).std("x");

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_parse_aggregate_to_string() {
    let query = "match\n" +
            "$x isa movie;\n" +
            "get $x;\n" +
            "group $x; count;";
    assertEquals(query, parse_query(query).toString());
}
*/

#[test]
fn when_parse_incorrect_syntax_throw_typeql_syntax_exception_with_helpful_error() {
    let parsed = parse_query("match\n$x isa").map(Query::into_match);
    assert!(parsed.is_err());
    let message = parsed.unwrap_err();
    assert!(message.contains("syntax error"));
    assert!(message.contains("line 2"));
    assert!(message.contains("\n$x isa"));
    assert!(message.contains("\n      ^"));
}

#[test]
fn when_parse_incorrect_syntax_trailing_query_whitespace_is_ignored() {
    let parsed = parse_query("match\n$x isa \n").map(Query::into_match);
    assert!(parsed.is_err());
    let message = parsed.unwrap_err();
    assert!(message.contains("syntax error"));
    assert!(message.contains("line 2"));
    assert!(message.contains("\n$x isa"));
    assert!(message.contains("\n      ^"));
}

#[test]
fn when_parse_incorrect_syntax_error_message_should_retain_whitespace() {
    let parsed = parse_query("match\n$x isa ").map(Query::into_match);
    assert!(parsed.is_err());
    let message = parsed.unwrap_err();
    assert!(!message.contains("match$xisa")); // FIXME bizarre thing to test?
}

#[test]
fn test_syntax_error_pointer() {
    let parsed = parse_query("match\n$x of").map(Query::into_match);
    assert!(parsed.is_err());
    let message = parsed.unwrap_err();
    assert!(message.contains("\n$x of"));
    assert!(message.contains("\n   ^"));
}

#[test]
fn test_has_variable() {
    let query = r#"match
$_ has title "Godfather",
    has tmdb-vote-count $x;"#;

    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(var(()).has("title", "Godfather").has("tmdb-vote-count", var("x")));

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_regex_attribute_type() {
    let query = r#"match
$x regex "(fe)?male";"#;

    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(var("x").regex("(fe)?male"));

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_typeql_parse_query() {
    assert!(matches!(parse_query("match\n$x isa movie;"), Ok(Query::Match(_))));
}

#[test]
fn test_parse_key() {
    let query = r#"match
$x owns name @key;
get $x;"#;

    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(var("x").owns(("name", KEY))).get(["x"]);

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_parse_empty_string() {
    assert!(parse_query("").is_err());
}

/*
#[test]
fn test_parse_list_one_match() {
    let queries = r#"match
$y isa movie;"#;

    let parsed = parse_queries(queries).collect();  // TODO parse_queries -> Iter
    let expected = vec![typeql_match(var("y").isa("movie"))];

    assert_eq!(parsed, expected);
}

#[test]
fn test_parse_list_one_insert() {
    let insertString = "insert\n$x isa movie;";
    let queries = parse_queries(insertString).collect(toList());

    assertEquals(list(insert(var("x").isa("movie"))), queries);
}

#[test]
fn test_parse_list_one_insert_with_whitespace_prefix() {
    let queries = " insert $x isa movie;";

    let queries = parse_queries(queries).collect();

    assertEquals(list(insert(var("x").isa("movie"))), queries);
}

#[test]
fn test_parse_list_one_insert_with_prefix_comment() {
    let insertString = "#hola\ninsert $x isa movie;";
    let queries = parse_queries(insertString).collect(toList());

    assertEquals(list(insert(var("x").isa("movie"))), queries);
}

#[test]
fn test_parse_list() {
    let insert_string = "insert\n$x isa movie;";
    let match_string = "match\n$y isa movie;";
    let queries = parse_queries(insert_string + match_string).collect();

    assertEquals(list(insert(var("x").isa("movie")), typeql_match(var("y").isa("movie"))), queries);
}

#[test]
fn test_parse_many_match_insert_without_stack_overflow() {
    let num_queries = 10_000;
    let match_insert_string = "match\n$x isa person; insert $x has name 'bob';\n";
    let mut long_query = String::new();
    for _ in 0..num_queries {
        long_query += match_insert_string;
    }

    let parsed = parse_queries(long_query).collect();
    let expected = typeql_match(var("x").isa("person")).insert(var("x").has("name", "bob"));

    assert_eq!(vec![expected; num_queries], parsed);
}

#[test]
fn when_parsing_list_of_queries_with_syntax_error_report_error() {
    let query_text = "define\nperson sub entity has name;"; // note no semicolon

    exception.expect(TypeQLException.class);
    exception.expectMessage("\nperson sub entity has name;"); // Message should refer to line

    //noinspection ResultOfMethodCallIgnored
    parse_query(query_text);
}
 */

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
    let message = parsed.unwrap_err();
    assert!(message.contains("limit1"));
}

#[test]
fn when_parsing_aggregate_with_wrong_variable_argument_number_throw() {
    assert!(parse_query("match\n$x isa name; group;").is_err());
}

#[test]
fn when_parsing_aggregate_with_wrong_name_throw() {
    assert!(parse_query("match\n$x isa name; hello $x;").is_err());
}

/*
#[test]
fn define_attribute_type_regex() {
    let query = "define\n" +
            "digit sub attribute,\n" +
            "    regex '\\d';";
    let = parse_query(query);
    let = define(type_("digit").sub("attribute").regex("\\d"));
    assert_query_eq!(expected, parsed, query.replace("'", "\""));
}

#[test]
fn undefine_attribute_type_regex() {
    let query = "undefine\ndigit regex '\\d';";
    let parsed = parse_query(query).map(Query::into_match);
    let expected = undefine(type_("digit").regex("\\d"));
    assert_query_eq!(expected, parsed, query.replace("'", "\""));
}

#[test]
fn regex_predicate_parses_character_classes_correctly() {
    let query = "match\n$x like '\\d';";
    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(var("x").like("\\d"));
    assert_query_eq!(expected, parsed, query.replace("'", "\""));
}

#[test]
fn regex_predicate_parses_quotes_correctly() {
    let query = "match\n$x like '\\\"';";
    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(var("x").like("\\\""));
    assert_query_eq!(expected, parsed, query.replace("'", "\""));
}

#[test]
fn regex_predicate_parses_backslashes_correctly() {
    let query = "match\n$x like '\\\\';";
    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(var("x").like("\\\\"));
    assert_query_eq!(expected, parsed, query.replace("'", "\""));
}

#[test]
fn regex_predicate_parses_newline_correctly() {
    let query = "match\n$x like '\\n';";
    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(var("x").like("\\n"));
    assert_query_eq!(expected, parsed, query.replace("'", "\""));
}

#[test]
fn regex_predicate_parses_forward_slashes_correctly() {
    let query = "match\n$x like '\\/';";
    let parsed = parse_query(query).map(Query::into_match);
    let expected = typeql_match(var("x").like("/"));
    assert_query_eq!(expected, parsed, query.replace("'", "\""));
}
 */

#[test]
fn when_value_equality_to_string_create_valid_query_string() {
    // TODO no unwraps
    let expected = typeql_match(var("x").eq(var("y"))).unwrap();
    let parsed = parse_query(&expected.to_string()).map(Query::into_match).unwrap();

    assert_eq!(expected, parsed);
}

#[test]
fn test_iid_constraint() {
    let iid = "0x0123456789abcdefdeadbeef";
    let query = format!(
        r#"match
$x iid {};"#,
        iid
    );

    let parsed = parse_query(&query).map(Query::into_match);
    let expected = typeql_match(var("x").iid(iid));
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_invalid_iid_throw() {
    let iid = "invalid";
    let query = format!(
        r#"match
$x iid {};"#,
        iid
    );

    let parsed = parse_query(&query).map(Query::into_match);
    assert!(parsed.is_err());
}

#[test]
fn when_building_invalid_iid_throw() {
    let iid = "invalid";
    let expected = typeql_match(var("x").iid(iid));
    assert!(expected.is_err());
}
