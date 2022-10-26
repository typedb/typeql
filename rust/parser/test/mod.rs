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
    and, gte, lt, lte, not, or, parse_pattern, parse_query, rel, rule, try_, type_, typeql_insert,
    typeql_match, var, ConceptVariableBuilder, Conjunction, Disjunction, ErrorMessage, Query,
    RelationVariableBuilder, ThingVariableBuilder, TypeQLDefine, TypeQLInsert, TypeQLMatch,
    TypeQLUndefine, TypeVariableBuilder, KEY,
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

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").isa("movie"));
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_named_type_variable() {
    let query = r#"match
$a type attribute_label;
get $a;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("a").type_("attribute_label")).get(["a"]);
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_parse_string_with_slash() {
    let query = r#"match
$x isa person,
    has name "alice/bob";"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! {
        typeql_match!(var("x").isa("person").has(("name", "alice/bob"))?)
    }
    .unwrap();

    assert_query_eq!(expected, parsed, query);
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

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_role_type_scoped_globally() {
    let query = r#"match
$m relates spouse;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("m").relates("spouse"));
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_role_type_not_scoped() {
    let query = r#"match
marriage relates $s;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(type_("marriage").relates(var("s")));
    assert_query_eq!(expected, parsed, query);
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
    let expected = try_! {
        typeql_match!(
            var("x").isa("movie").has(("title", var("t")))?,
            or!(
                var("t").eq("Apocalypse Now"),
                and!(var("t").lt("Juno"), var("t").gt("Godfather")),
                var("t").eq("Spy"),
            ),
            var("t").neq("Apocalypse Now"),
        )
    }
    .unwrap();

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

    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! {
        typeql_match!(
            var("x").isa("movie").has(("title", var("t")))?,
            or!(
                and!(var("t").lte("Juno"), var("t").gte("Godfather"), var("t").neq("Heat"),),
                var("t").eq("The Muppets"),
            ),
        )
    }
    .unwrap();

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

    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! {
        typeql_match!(
            rel("x").rel("y"),
            var("y").isa("person").has(("name", var("n")))?,
            or!(var("n").contains("ar")?, var("n").like("^M.*$")?),
        )
    }
    .unwrap();

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_predicate_query_4() {
    let query = r#"match
$x has age $y;
$y >= $z;
$z 18 isa age;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! {
        typeql_match!(
            var("x").has(("age", var("y")))?,
            var("y").gte(var("z")),
            var("z").eq(18).isa("age"),
        )
    }
    .unwrap();

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

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(
        var("x").sub(var("z")),
        var("y").sub(var("z")),
        var("a").isa(var("x")),
        var("b").isa(var("y")),
        not(var("x").is(var("y"))),
        not(var("a").is(var("b"))),
    );

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_value_equals_variable_query() {
    let query = r#"match
$s1 = $s2;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("s1").eq(var("s2")));
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_movies_released_after_or_at_the_same_time_as_spy() {
    let query = r#"match
$x has release-date >= $r;
$_ has title "Spy",
    has release-date $r;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! {
        typeql_match!(
            var("x").has(("release-date", gte(var("r"))))?,
            var(()).has(("title", "Spy"))?.has(("release-date", var("r")))?,
        )
    }
    .unwrap();
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_predicate() {
    let query = r#"match
$x has release-date < 1986-03-03T00:00,
    has tmdb-vote-count 100,
    has tmdb-vote-average <= 9.0;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! {
        typeql_match!(var("x")
        .has((
            "release-date",
            lt(NaiveDateTime::new(NaiveDate::from_ymd(1986, 3, 3), NaiveTime::from_hms(0, 0, 0)))?
        ))?
        .has(("tmdb-vote-count", 100))?
        .has(("tmdb-vote-average", lte(9.0)))?)
    }
    .unwrap();

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_time() {
    let query = r#"match
$x has release-date 1000-11-12T13:14:15;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! {
        typeql_match!(var("x").has((
            "release-date",
            NaiveDateTime::new(NaiveDate::from_ymd(1000, 11, 12), NaiveTime::from_hms(13, 14, 15)),
        ))?)
    }
    .unwrap();

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_big_years() {
    let query = r#"match
$x has release-date +12345-12-25T00:00;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! {
        typeql_match!(var("x").has((
            "release-date",
            NaiveDateTime::new(NaiveDate::from_ymd(12345, 12, 25), NaiveTime::from_hms(0, 0, 0)),
        ))?)
    }
    .unwrap();

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_small_years() {
    let query = r#"match
$x has release-date 0867-01-01T00:00;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! {
        typeql_match!(var("x").has((
            "release-date",
            NaiveDateTime::new(NaiveDate::from_ymd(867, 1, 1), NaiveTime::from_hms(0, 0, 0)),
        ))?)
    }
    .unwrap();

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_negative_years() {
    let query = r#"match
$x has release-date -3200-01-01T00:00;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! {
        typeql_match!(var("x").has((
            "release-date",
            NaiveDateTime::new(NaiveDate::from_ymd(-3200, 1, 1), NaiveTime::from_hms(0, 0, 0)),
        ))?)
    }
    .unwrap();

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_millis() {
    let query = r#"match
$x has release-date 1000-11-12T13:14:15.123;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! {
        typeql_match!(var("x").has((
            "release-date",
            NaiveDateTime::new(
                NaiveDate::from_ymd(1000, 11, 12),
                NaiveTime::from_hms_milli(13, 14, 15, 123),
            ),
        ))?)
    }
    .unwrap();

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_millis_shorthand() {
    let query = r#"match
$x has release-date 1000-11-12T13:14:15.1;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! {
        typeql_match!(var("x").has((
            "release-date",
            NaiveDateTime::new(
                NaiveDate::from_ymd(1000, 11, 12),
                NaiveTime::from_hms_milli(13, 14, 15, 100),
            ),
        ))?)
    }
    .unwrap();

    let parsed_query = r#"match
$x has release-date 1000-11-12T13:14:15.100;"#;
    assert_query_eq!(expected, parsed, parsed_query);
}

#[test]
fn when_parsing_date_error_when_parsing_overly_precise_decimal_seconds() {
    let query = r#"match
$x has release-date 1000-11-12T13:14:15.000123456;"#;

    let parsed = parse_query(query);
    assert!(parsed.is_err());
    assert!(parsed.err().unwrap().contains("no viable alternative"));
}

#[test]
fn when_parsing_date_error_when_handling_overly_precise_nanos() {
    let expected = try_! {
        typeql_match!(var("x").has((
            "release-date",
            NaiveDateTime::new(
                NaiveDate::from_ymd(1000, 11, 12),
                NaiveTime::from_hms_nano(13, 14, 15, 123450000),
            ),
        ))?)
    };
    assert!(expected.is_err());
    assert!(expected.err().unwrap().message.contains("more precise than 1 millisecond"));
}

#[test]
fn test_long_predicate_query() {
    let query = r#"match
$x isa movie,
    has tmdb-vote-count <= 400;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! {
        typeql_match!(var("x").isa("movie").has(("tmdb-vote-count", lte(400)))?)
    }
    .unwrap();

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_schema_query() {
    let query = r#"match
$x plays starring:actor;
sort $x asc;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").plays(("starring", "actor"))).sort([("x", "asc")]);

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_get_sort() {
    let query = r#"match
$x isa movie,
    has rating $r;
sort $r desc;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! {
        typeql_match!(var("x").isa("movie").has(("rating", var("r")))?).sort([("r", "desc")])
    }
    .unwrap();

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_get_sort_limit() {
    let query = r#"match
$x isa movie,
    has rating $r;
sort $r; limit 10;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! {
        typeql_match!(var("x").isa("movie").has(("rating", var("r")))?).sort("r").limit(10)
    }
    .unwrap();

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_get_sort_offset_limit() {
    let query = r#"match
$x isa movie,
    has rating $r;
sort $r desc; offset 10; limit 10;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! {
        typeql_match!(var("x").isa("movie").has(("rating", var("r")))?)
        .sort([("r", "desc")])
        .offset(10)
        .limit(10)
    }
    .unwrap();

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_get_offset_limit() {
    let query = r#"match
$y isa movie,
    has title $n;
offset 2; limit 4;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! {
        typeql_match!(var("y").isa("movie").has(("title", var("n")))?).offset(2).limit(4)
    }
    .unwrap();

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

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(
        rel((var("p"), var("x"))).rel("y"),
        var("x").isa(var("z")),
        var("y").eq("crime"),
        var("z").sub("production"),
        type_("has-genre").relates(var("p")),
    );

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_parse_relates_type_variable() {
    let query = r#"match
$x isa $type;
$type relates someRole;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").isa(var("type")), var("type").relates("someRole"));

    assert_query_eq!(expected, parsed, query);
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

    assert_query_eq!(expected, parsed, query);
}

// #[test]
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
    let expected = try_! {
        typeql_match!(
            var("y").isa(var("p")),
            or!(
                rel("y").rel("q"),
                and!(
                    var("x").isa(var("p")),
                    or!(var("x").has(("first-name", var("y")))?, var("x").has(("last-name", var("z")))?)
                )
            )
        )
    }.unwrap();

    assert_query_eq!(expected, parsed, query);
}

/*
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
    let expected = try_! {
    typeql_match!(rel("x").rel("y").isa("friendship")).get("x", "y").count()
};

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_aggregate_group_count_query() {
    let query = "match\n" +
            "($x, $y) isa friendship;\n" +
            "get $x, $y;\n" +
            "group $x; count;";
    let parsed = parse_query(query).unwrapGroupAggregate();
    let expected = try_! {
    typeql_match!(rel("x").rel("y").isa("friendship")).get("x", "y").group("x").count()
};

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_single_line_group_aggregate_max_query() {
    let query = "match\n" +
            "$x has age $a;\n" +
            "group $x; max $a;";
    let parsed = parse_query(query).unwrapGroupAggregate();
    let expected = try_! {
    typeql_match!(var("x").has(("age", var("a")))).group("x").max("a")
};

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_multi_line_group_aggregate_max_query() {
    let query = "match\n" +
            "($x, $y) isa friendship;\n" +
            "$y has age $z;\n" +
            "group $x; max $z;";
    let parsed = parse_query(query).unwrapGroupAggregate();
    let expected = try_! {
    typeql_match!(
            rel("x").rel("y").isa("friendship"),
            var("y").has(("age", var("z")))
    ).group("x").max("z")
};

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
    let expected = try_! {
    typeql_match!(
            rel("x").rel("y").isa("friendship"),
            var("y").has(("age", var("z")))
    ).get("x", "y", "z").group("x").max("z")
};

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_comparing_count_query_using_typeql_and_java_typeql_they_are_equivalent() {
    let query = "match\n" +
            "$x isa movie,\n" +
            "    has title \"Godfather\";\n" +
            "count;";
    let parsed = parse_query(query).unwrapAggregate();
    let expected = try_! {
    typeql_match!(var("x").isa("movie").has(("title", "Godfather"))).count()
};

    assert_query_eq!(expected, parsed, query);
}
*/
#[test]
fn test_insert_query() {
    let query = r#"insert
$_ isa movie,
    has title "The Title";"#;

    let parsed = parse_query(query).unwrap().into_insert();
    let expected = try_! {
        typeql_insert!(var(()).isa("movie").has(("title", "The Title"))?)
    }
    .unwrap();

    assert_query_eq!(expected, parsed, query);
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
    let expected = try_! {
        typeql_match!(var("x").isa("movie").has(("title", "The Title"))?, var("y").isa("movie"))
        .delete([var("x").isa("movie"), var("y").isa("movie")])
    }
    .unwrap();

    assert_query_eq!(expected, parsed, query);
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
    let expected = try_! {
        typeql_insert!(
            var("x").isa("pokemon").has(("name", "Pichu"))?,
            var("y").isa("pokemon").has(("name", "Pikachu"))?,
            var("z").isa("pokemon").has(("name", "Raichu"))?,
            rel(("evolves-from", "x")).rel(("evolves-to", "y")).isa("evolution"),
            rel(("evolves-from", "y")).rel(("evolves-to", "z")).isa("evolution")
        )
    }
    .unwrap();

    assert_query_eq!(expected, parsed, query);
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
    let expected = try_! {
        typeql_match!(var("x").isa("person").has(("name", "alice"))?.has(("age", var("a")))?)
            .delete(var("x").has(var("a"))?)
            .insert(var("x").has(("age", 25))?)
    }
    .unwrap();

    assert_query_eq!(expected, parsed, query);
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
        type_("fatherhood")
            .sub("parenthood")
            .relates(("father", "parent"))
            .relates(("son", "child"))
    );

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_parsing_as_in_match_result_is_same_as_sub() {
    let query = r#"match
$f sub parenthood,
    relates father as parent,
    relates son as child;"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("f")
        .sub("parenthood")
        .relates(("father", "parent"))
        .relates(("son", "child")));

    assert_query_eq!(expected, parsed, query);
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

    assert_query_eq!(expected, parsed, query);
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

    assert_query_eq!(expected, parsed, query);
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

    assert_query_eq!(expected, parsed, query);
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

    assert_query_eq!(expected, parsed, query);
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

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_undefine_rule() {
    let query = r#"undefine
rule r;"#;

    let parsed = parse_query(query).unwrap().into_undefine();
    let expected = typeql_undefine!(rule("r"));
    assert_query_eq!(expected, parsed, query);
}

// #[test]
fn when_parse_undefine_rule_with_body_throw() {
    let query = r#"undefine rule r: when { $x isa thing; } then { $x has name "a"; };"#;
    let parsed = parse_query(query);
    assert!(parsed.is_err());
}

#[test]
fn test_match_insert_query() {
    let query = r#"match
$x isa language;
insert
$x has name "HELLO";"#;

    let parsed = parse_query(query).unwrap().into_insert();
    let expected = try_! {
        typeql_match!(var("x").isa("language")).insert(var("x").has(("name", "HELLO"))?)
    }
    .unwrap();

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_define_abstract_entity_query() {
    let query = r#"define
concrete-type sub entity;
abstract-type sub entity,
    abstract;"#;

    let parsed = parse_query(query).unwrap().into_define();
    let expected = typeql_define!(
        type_("concrete-type").sub("entity"),
        type_("abstract-type").sub("entity").abstract_()
    );

    assert_query_eq!(expected, parsed, query);
}

/*
#[test]
fn test_match_value_type_query() {
    let query = "match\n$x value double;";
    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! {
    typeql_match!(var("x").value(TypeQLArg.ValueType.DOUBLE))
};

    assert_query_eq!(expected, parsed, query);
}
 */
// #[test]
fn test_parse_without_var() {
    let query = r#"match
$_ isa person;"#;

    let parsed = parse_query(query); // todo error
    assert!(parsed.is_err());
    let built = try_! { typeql_match!(var(()).isa("person")) }; // todo error
    assert!(built.is_err());
}

/*
#[test]
fn when_parsing_date_keyword_parse_as_the_correct_value_type() {
    let query = "match\n$x value datetime;";
    let parsed = parse_query(query).asMatch();
    let expected = try_! {
    typeql_match!(var("x").value(TypeQLArg.ValueType.DATETIME))
};

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
*/
#[test]
fn test_escape_string() {
    let input = r#"This has \"double quotes\" and a single-quoted backslash: '\\'"#;

    let query = format!(
        r#"insert
$_ isa movie,
    has title "{}";"#,
        input
    );

    let parsed = parse_query(&query).unwrap().into_insert();
    let expected = try_! { typeql_insert!(var(()).isa("movie").has(("title", input))?) }.unwrap();
    assert_query_eq!(expected, parsed, query);
}

/*
#[test]
fn when_parsing_query_with_comments_they_are_ignored() {
    let query = "match\n" +
            "\n# there's a comment here\n$x isa###WOW HERES ANOTHER###\r\nmovie; count;";
    let uncommented = "match\n$x isa movie;\ncount;";

    let parsed = parse_query(query).asMatchAggregate();
    let expected = try_! {
    typeql_match!(var("x").isa("movie")).count()
};

    assert_query_eq!(expected, parsed, uncommented);
}
*/

#[test]
fn test_parsing_pattern() {
    let pattern = r#"{
    (wife: $a, husband: $b) isa marriage;
    $a has gender "male";
    $b has gender "female";
}"#;

    let parsed = parse_pattern(pattern).unwrap().into_conjunction();
    let expected = try_! {
        and!(
            rel(("wife", "a")).rel(("husband", "b")).isa("marriage"),
            var("a").has(("gender", "male"))?,
            var("b").has(("gender", "female"))?
        )
    }
    .unwrap();

    // TODO rename the assert macro
    assert_query_eq!(expected, parsed, pattern);
}

#[test]
fn test_define_rules() {
    let query = r#"define
rule all-movies-are-drama:
    when {
        $x isa movie;
    }
    then {
        $x has genre "drama";
    };"#;

    let parsed = parse_query(&query).unwrap().into_define();
    let expected = typeql_define!(rule("all-movies-are-drama")
        .when(and!(var("x").isa("movie")))
        .then(var("x").has(("genre", "drama")).unwrap()));

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_parse_boolean() {
    let query = r#"insert
$_ has flag true;"#;

    let parsed = parse_query(query).unwrap().into_insert();
    let expected = try_! { typeql_insert!(var(()).has(("flag", true))?) }.unwrap();
    assert_query_eq!(expected, parsed, query);
}

/*
#[test]
fn test_parse_aggregate_group() {
    let query = "match\n" +
            "$x isa movie;\n" +
            "group $x;";
    let parsed = parse_query(query).asMatchGroup();
    let expected = try_! {
    typeql_match!(var("x").isa("movie")).group("x")
};

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_parse_aggregate_group_count() {
    let query = "match\n" +
            "$x isa movie;\n" +
            "group $x; count;";
    let parsed = parse_query(query).asMatchGroupAggregate();
    let expected = try_! {
    typeql_match!(var("x").isa("movie")).group("x").count()
};

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_parse_aggregate_std() {
    let query = "match\n" +
            "$x isa movie;\n" +
            "std $x;";
    let parsed = parse_query(query).asMatchAggregate();
    let expected = try_! {
    typeql_match!(var("x").isa("movie")).std("x")
};

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
    let parsed = parse_query("match\n$x isa");
    assert!(parsed.is_err());
    let message = parsed.unwrap_err();
    assert!(message.contains("syntax error"));
    assert!(message.contains("line 2"));
    assert!(message.contains("\n$x isa"));
    assert!(message.contains("\n      ^"));
}

#[test]
fn when_parse_incorrect_syntax_trailing_query_whitespace_is_ignored() {
    let parsed = parse_query("match\n$x isa \n");
    assert!(parsed.is_err());
    let message = parsed.unwrap_err();
    assert!(message.contains("syntax error"));
    assert!(message.contains("line 2"));
    assert!(message.contains("\n$x isa"));
    assert!(message.contains("\n      ^"));
}

#[test]
fn when_parse_incorrect_syntax_error_message_should_retain_whitespace() {
    let parsed = parse_query("match\n$x isa ");
    assert!(parsed.is_err());
    let message = parsed.unwrap_err();
    assert!(!message.contains("match$xisa")); // FIXME bizarre thing to test?
}

#[test]
fn test_syntax_error_pointer() {
    let parsed = parse_query("match\n$x of");
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

    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! {
        typeql_match!(var(()).has(("title", "Godfather"))?.has(("tmdb-vote-count", var("x")))?)
    }
    .unwrap();
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn test_regex_attribute_type() {
    let query = r#"match
$x regex "(fe)?male";"#;

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").regex("(fe)?male"));
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

    let parsed = parse_query(query).unwrap().into_match();
    let expected = typeql_match!(var("x").owns(("name", KEY))).get(["x"]);
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
    let expected = vec![try_! {
    typeql_match!(var("y").isa("movie"))]
};

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

    assertEquals(list(insert(var("x").isa("movie")), try_! {
    typeql_match!(var("y").isa("movie"))), queries)
};
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
    let expected = try_! {
    typeql_match!(var("x").isa("person")).insert(var("x").has(("name", "bob")))
};

    assert_eq!(vec![expected; num_queries], parsed);
}
 */

#[test]
fn when_parsing_list_of_queries_with_syntax_error_report_error() {
    let query_text = "define\nperson sub entity has name;"; // note no comma
    let parsed = parse_query(query_text);
    assert!(parsed.is_err());
    assert!(parsed.err().unwrap().contains("\nperson sub entity has name;"));
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

#[test]
fn define_attribute_type_regex() {
    let query = r#"define
digit sub attribute,
    regex "\d";"#;

    let parsed = parse_query(query).unwrap().into_define();
    let expected = typeql_define!(type_("digit").sub("attribute").regex(r#"\d"#));

    assert_query_eq!(expected, parsed, query);
}

#[test]
fn undefine_attribute_type_regex() {
    let query = r#"undefine
digit regex "\d";"#;

    let parsed = parse_query(query).unwrap().into_undefine();
    let expected = typeql_undefine!(type_("digit").regex(r#"\d"#));
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn regex_predicate_parses_character_classes_correctly() {
    let query = r#"match
$x like "\d";"#;
    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! { typeql_match!(var("x").like("\\d")?) }.unwrap();
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn regex_predicate_parses_quotes_correctly() {
    let query = r#"match
$x like "\"";"#;
    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! { typeql_match!(var("x").like("\\\"")?) }.unwrap();
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn regex_predicate_parses_backslashes_correctly() {
    let query = r#"match
$x like "\\";"#;
    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! { typeql_match!(var("x").like("\\\\")?) }.unwrap();
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn regex_predicate_parses_newline_correctly() {
    let query = r#"match
$x like "\n";"#;
    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! { typeql_match!(var("x").like("\\n")?) }.unwrap();
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn regex_predicate_parses_forward_slashes_correctly() {
    let query = r#"match
$x like "\/";"#;
    let parsed = parse_query(query).unwrap().into_match();
    let expected = try_! { typeql_match!(var("x").like("/")?) }.unwrap();
    assert_query_eq!(expected, parsed, query);
}

#[test]
fn when_value_equality_to_string_create_valid_query_string() {
    let expected = try_! {
        typeql_match!(var("x").eq(var("y")))
    }
    .unwrap();
    let parsed = parse_query(&expected.to_string()).unwrap().into_match();

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

    let parsed = parse_query(&query).unwrap().into_match();
    let expected = try_! { typeql_match!(var("x").iid(iid)?) }.unwrap();
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

    let parsed = parse_query(&query);
    assert!(parsed.is_err());
}

#[test]
fn when_building_invalid_iid_throw() {
    let iid = "invalid";
    let expected = try_! { typeql_match!(var("x").iid(iid)?) };
    assert!(expected.is_err());
}
