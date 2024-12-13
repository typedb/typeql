/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::assert_valid_eq_repr;
use crate::parse_query;

#[test]
fn test_simple_query() {
    let query = r#"match
$x isa movie;"#;

    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("x").isa("movie"));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_named_type_variable() {
    let query = r#"match
$a label attribute_label;"#;

    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("a").type_("attribute_label")).get_fixed([var("a")]);
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_parsing_string_with_slash() {
    let query = r#"match
$x isa person,
    has name "alice/bob";"#;

    let parsed = parse_query(query).unwrap();
    // let expected = (); //typeql_match!(var("x").isa("person").has(("name", "alice/bob")));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_relation_query() {
    let query = r#"match
$brando isa name "Marl B";
(actor: $brando, $char, production-with-cast: $prod);
select $char, $prod;"#;

    let parsed = parse_query(query).unwrap();
    // let expected = match_!(
    // var("brando").equals("Marl B").isa("name"),
    // rel(("actor", "brando")).links("char").links(("production-with-cast", "prod")),
    // )
    // .get_fixed([var("char"), var("prod")]);

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_labelled_relation() {
    let query = r#"match
$brando isa name "Marl B";
casting (actor: $brando, $char, production-with-cast: $prod);
select $char, $prod;"#;
    let parsed = parse_query(query).unwrap();
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_role_type_scoped_globally() {
    let query = r#"match
$m relates spouse;"#;

    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("m").relates("spouse"));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_role_type_not_scoped() {
    let query = r#"match
marriage relates $s;"#;

    let parsed = parse_query(query).unwrap();
    // let expected = match_!(type_("marriage").relates(var("s")));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_predicate_query_1() {
    let query = r#"match
$x isa movie,
    has title $t;
{
    $t == "Apocalypse Now";
} or {
    $t < "Juno";
    $t > "Godfather";
} or {
    $t == "Spy";
};
$t != "Apocalypse Now";"#;

    let parsed = parse_query(query).unwrap();
    // let expected = ();
    // typeql_match!(
    // var("x").isa("movie").has(("title", var("t"))),
    // or!(
    // var("t").equals("Apocalypse Now"),
    // and!(var("t").lt("Juno"), var("t").gt("Godfather")),
    // var("t").equals("Spy"),
    // ),
    // var("t").neq("Apocalypse Now"),
    // )
    // ;

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
    $t == "The Muppets";
};"#;

    let parsed = parse_query(query).unwrap();
    // let expected = match_!(
    // var("x").isa("movie").has(("title", var("t"))),
    // or!(and!(var("t").lte("Juno"), var("t").gte("Godfather"), var("t").neq("Heat")), var("t").equals("The Muppets"),),
    // );
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

    let parsed = parse_query(query).unwrap();
    // let expected = match_!(
    // rel("x").links("y"),
    // var("y").isa("person").has(("name", var("n"))),
    // or!(var("n").contains("ar"), var("n").like("^M.*$")),
    // );

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_predicate_query_4() {
    let query = r#"match
$x has age $y;
$y >= $z;
$z isa age 18;"#;

    let parsed = parse_query(query).unwrap();
    // let expected = ();
    // typeql_match!(var("x").has(("age", var("y"))), var("y").gte(var("z")), var("z").equals(18).isa("age"),);

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

    let parsed = parse_query(query).unwrap();
    // let expected = match_!(
    // var("x").sub(var("z")),
    // var("y").sub(var("z")),
    // var("a").isa(var("x")),
    // var("b").isa(var("y")),
    // not(var("x").is(var("y"))),
    // not(var("a").is(var("b"))),
    // );

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_value_equals_variable_query() {
    let query = r#"match
$s1 == $s2;"#;

    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("s1").equals(var("s2")));
    assert_valid_eq_repr!(expected, parsed, query);
}

// // TODO: Remove when we fully deprecate '=' for ThingVariable equality.
// #[test]
// fn test_value_equals_variable_query_backwards_compatibility() {
//     let query = r#"match
// $s1 = $s2;
// get;"#;
//
//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(var("s1").equals(var("s2")));
//
//     assert_eq!(expected, parsed);
//     assert_eq!(expected, parse_query(parsed.to_string().as_str()).unwrap());
// }

#[test]
fn test_movies_released_after_or_at_the_same_time_as_spy() {
    let query = r#"match
$x has release-date >= $r;
$_ has title "Spy",
    has release-date $r;"#;

    let parsed = parse_query(query).unwrap();
    // let expected = match_!(
    // var("x").has(("release-date", gte(var("r")))),
    // var(()).has(("title", "Spy")).has(("release-date", var("r"))),
    // );

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_predicate() {
    let query = r#"match
$x has release-date < 1986-03-03T00:00,
    has tmdb-vote-count 100,
    has tmdb-vote-average <= 9.0;"#;

    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("x")
    // .has((
    // "release-date",
    // lt(NaiveDateTime::new(
    // NaiveDate::from_ymd_opt(1986, 3, 3).unwrap(),
    // NaiveTime::from_hms_opt(0, 0, 0).unwrap(),
    // )),
    // ))
    // .has(("tmdb-vote-count", 100))
    // .has(("tmdb-vote-average", lte(9.0))),);

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_time() {
    let query = r#"match
$x has release-date 1000-11-12T13:14:15;"#;

    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("x").has((
    // "release-date",
    // NaiveDateTime::new(
    // NaiveDate::from_ymd_opt(1000, 11, 12).unwrap(),
    // NaiveTime::from_hms_opt(13, 14, 15).unwrap(),
    // ),
    // )));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_big_years() {
    let query = r#"match
$x has release-date +12345-12-25T00:00;"#;

    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("x").has((
    // "release-date",
    // NaiveDateTime::new(NaiveDate::from_ymd_opt(12345, 12, 25).unwrap(), NaiveTime::from_hms_opt(0, 0, 0).unwrap()),
    // )));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_small_years() {
    let query = r#"match
$x has release-date 0867-01-01T00:00;"#;

    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("x").has((
    // "release-date",
    // NaiveDateTime::new(NaiveDate::from_ymd_opt(867, 1, 1).unwrap(), NaiveTime::from_hms_opt(0, 0, 0).unwrap()),
    // )));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_negative_years() {
    let query = r#"match
$x has release-date -3200-01-01T00:00;"#;

    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("x").has((
    // "release-date",
    // NaiveDateTime::new(NaiveDate::from_ymd_opt(-3200, 1, 1).unwrap(), NaiveTime::from_hms_opt(0, 0, 0).unwrap()),
    // )));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_millis() {
    let query = r#"match
$x has release-date 1000-11-12T13:14:15.123;"#;

    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("x").has((
    // "release-date",
    // NaiveDateTime::new(
    // NaiveDate::from_ymd_opt(1000, 11, 12).unwrap(),
    // NaiveTime::from_hms_milli_opt(13, 14, 15, 123).unwrap(),
    // ),
    // )));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_millis_shorthand() {
    let query = r#"match
$x has release-date 1000-11-12T13:14:15.1;"#;

    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("x").has((
    // "release-date",
    // NaiveDateTime::new(
    // NaiveDate::from_ymd_opt(1000, 11, 12).unwrap(),
    // NaiveTime::from_hms_milli_opt(13, 14, 15, 100).unwrap(),
    // ),
    // )));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_date_error_when_parsing_overly_precise_decimal_seconds() {
    let query = r#"match
$x has release-date 1000-11-12T13:14:15.0001234567;"#;

    let parsed = parse_query(query);
    assert!(parsed.is_err());
    assert!(parsed.unwrap_err().to_string().contains(" at 2:50"));
}

#[test]
fn test_parsing_long_predicate_query() {
    let query = r#"match
$x isa movie,
    has tmdb-vote-count <= 400;"#;

    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("x").isa("movie").has(("tmdb-vote-count", lte(400))));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_parsing_attribute_query_by_value_variable() {
    let query = r#"match
let $x = 5;
$a isa age == $x;"#;

    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("x").assign(5), var("a").equals(var("x")).isa("age"));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_parsing_precedence_operators() {
    let query = r#"match
let $res = $a / $b * $c + $d ^ $e ^ $f / $g;"#;

    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("res").assign(
    // var("a").divide(var("b")).multiply(var("c")).add(var("d").power(var("e").power(var("f"))).divide(var("g"))),
    // ));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_parsing_precedence_function_and_parentheses() {
    let query = r#"match
let $res = $a + (round($b + $c) + $d) * $e;"#;

    let parsed = parse_query(query).unwrap();
    // let expected =
    // match_!(var("res").assign(var("a").add(round(var("b").add(var("c"))).add(var("d")).multiply(var("e")))));

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_schema_query() {
    let query = r#"match
$x plays starring:actor;
sort $x asc;"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(var("x").plays(("starring", "actor"))).sort([(cvar("x"), Asc)]);
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_as_in_match_result_is_same_as_sub() {
    let query = r#"match
$f sub parenthood,
    relates father as parent,
    relates son as child;"#;
    let parsed = parse_query(query).unwrap();
    //     let expected =
    //         typeql_match!(var("f").sub("parenthood").relates(("father", "parent")).relates(("son", "child")));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_match_value_type_query() {
    let query = r#"match
$x value double;"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(var("x").value(ValueType::Double));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_date_keyword_parse_as_the_correct_value_type() {
    let query = r#"match
$x value datetime;"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_match!(var("x").value(ValueType::DateTime));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_parsing_boolean() {
    let query = r#"insert
$_ has flag true;"#;
    let parsed = parse_query(query).unwrap();
    //     let expected = typeql_insert!(var(()).has(("flag", true)));
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_variables_everywhere_query() {
    let query = r#"match
($p: $x, $y);
$x isa $z;
$y == "crime";
$z sub production;
has-genre relates $p;"#;
    let parsed = parse_query(query).unwrap();
    // let expected = typeql_match!(
    // rel((var("p"), cvar("x"))).links("y"),
    // var("x").isa(cvar("z")),
    // var("y").eq("crime"),
    // var("z").sub("production"),
    // type_("has-genre").relates(var("p")),
    // );
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_parsing_relates_type_variable() {
    let query = r#"match
$x isa $type;
$type relates someRole;"#;
    let parsed = parse_query(query).unwrap();
    // let expected = typeql_match!(var("x").isa(cvar("type")), cvar("type").relates("someRole"));
    assert_valid_eq_repr!(expected, parsed, query);
}
