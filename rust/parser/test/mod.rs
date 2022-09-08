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

#[test]
fn test_variables_everywhere_query() {
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
    let parsed = parse_query(query).unwrap();
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
    assertThrows(() -> parseQuery(query).unwrap());
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
    let parsed = parse_query(query).unwrap();
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
    assertThrows(() -> parseQuery(query).unwrap());
}

#[test]
fn test_aggregate_count_query() {
    let query = "match\n" +
            "($x, $y) isa friendship;\n" +
            "get $x, $y;\n" +
            "count;";
    let parsed = parseQuery(query).unwrapAggregate();
    let expected = typeql_match(rel("x").rel("y").isa("friendship")).get("x", "y").count();

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_aggregate_group_count_query() {
    let query = "match\n" +
            "($x, $y) isa friendship;\n" +
            "get $x, $y;\n" +
            "group $x; count;";
    let parsed = parseQuery(query).unwrapGroupAggregate();
    let expected = typeql_match(rel("x").rel("y").isa("friendship")).get("x", "y").group("x").count();

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_single_line_group_aggregate_max_query() {
    let query = "match\n" +
            "$x has age $a;\n" +
            "group $x; max $a;";
    let parsed = parseQuery(query).unwrapGroupAggregate();
    let expected = typeql_match(var("x").has("age", var("a"))).group("x").max("a");

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_multi_line_group_aggregate_max_query() {
    let query = "match\n" +
            "($x, $y) isa friendship;\n" +
            "$y has age $z;\n" +
            "group $x; max $z;";
    let parsed = parseQuery(query).unwrapGroupAggregate();
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
    let parsed = parseQuery(query).unwrapGroupAggregate();
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
    let parsed = parseQuery(query).unwrapAggregate();
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
    let parsed = parse_query(query).unwrap();
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
    let parsed = parse_query(query).unwrap();
    let expected = typeql_match(var("x").value(TypeQLArg.ValueType.DOUBLE));

    assert_query_eq!(expected, parsed, query);
}
 */

#[test]
fn test_parse_without_var() {
    let query = r#"match
$_ isa person;"#;

    let parsed = parse_query(query);  // todo error
    assert!(parsed.is_err());
    // let built = typeql_match(var(()).isa("person"));  // todo typeql_match -> Result
    // assert!(built.is_err());
}

/*
#[test]
fn when_parsing_date_keyword_parse_as_the_correct_value_type() {
    let query = "typeql_match\n$x value datetime;";
    let parsed = TypeQL.parseQuery(query).asMatch();
    let expected = typeql_match(var("x").value(TypeQLArg.ValueType.DATETIME));

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_define_value_type_query() {
    let query = "define\n" +
            "my-type sub attribute,\n" +
            "    value long;";
    let parsed = TypeQL.parseQuery(query).asDefine();
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
    let parsed = parse_query(query).unwrap();
    let expected = insert(var(()).isa("movie").has("title", input));

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_query_with_comments_they_are_ignored() {
    let query = "match\n" +
            "\n# there's a comment here\n$x isa###WOW HERES ANOTHER###\r\nmovie; count;";
    let uncommented = "match\n$x isa movie;\ncount;";

    let parsed = parseQuery(query).asMatchAggregate();
    let expected = typeql_match(var("x").isa("movie")).count();

    assert_query_eq!(expected, parsed, uncommented);
}
*/
