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
    let expected = (); // typeql_match!(cvar("x").isa("movie")).get();
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_named_type_variable() {
    let query = r#"match
$a type attribute_label;"#;

    let parsed = parse_query(query).unwrap();
    let expected = (); // typeql_match!(cvar("a").type_("attribute_label")).get_fixed([cvar("a")]);
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_parsing_string_with_slash() {
    let query = r#"match
$x isa person,
    has name "alice/bob";"#;

    let parsed = parse_query(query).unwrap();
    let expected = (); //typeql_match!(cvar("x").isa("person").has(("name", "alice/bob"))).get();

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_relation_query() {
    let query = r#"match
$brando "Marl B" isa name;
(actor: $brando, $char, production-with-cast: $prod);
filter $char, $prod;"#;

    let parsed = parse_query(query).unwrap();
    let expected = (); /* typeql_match!(
                           cvar("brando").eq("Marl B").isa("name"),
                           rel(("actor", "brando")).links("char").links(("production-with-cast", "prod")),
                       )
                       .get_fixed([cvar("char"), cvar("prod")]); */

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_role_type_scoped_globally() {
    let query = r#"match
$m relates spouse;"#;

    let parsed = parse_query(query).unwrap();
    let expected = (); // typeql_match!(cvar("m").relates("spouse")).get();
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_role_type_not_scoped() {
    let query = r#"match
marriage relates $s;"#;

    let parsed = parse_query(query).unwrap();
    let expected = (); // typeql_match!(type_("marriage").relates(cvar("s"))).get();
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
    let expected = ();
    // typeql_match!(
    // cvar("x").isa("movie").has(("title", cvar("t"))),
    // or!(
    // cvar("t").eq("Apocalypse Now"),
    // and!(cvar("t").lt("Juno"), cvar("t").gt("Godfather")),
    // cvar("t").eq("Spy"),
    // ),
    // cvar("t").neq("Apocalypse Now"),
    // )
    // .get();

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
    let expected = (); /* typeql_match!(
                           cvar("x").isa("movie").has(("title", cvar("t"))),
                           or!(
                               and!(cvar("t").lte("Juno"), cvar("t").gte("Godfather"), cvar("t").neq("Heat")),
                               cvar("t").eq("The Muppets"),
                           ),
                       )
                       .get(); */
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
    let expected = (); /* typeql_match!(
                           rel("x").links("y"),
                           cvar("y").isa("person").has(("name", cvar("n"))),
                           or!(cvar("n").contains("ar"), cvar("n").like("^M.*$")),
                       )
                       .get(); */

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_predicate_query_4() {
    let query = r#"match
$x has age $y;
$y >= $z;
$z 18 isa age;"#;

    let parsed = parse_query(query).unwrap();
    let expected = ();
    // typeql_match!(cvar("x").has(("age", cvar("y"))), cvar("y").gte(cvar("z")), cvar("z").eq(18).isa("age"),).get();

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
    let expected = (); /* typeql_match!(
                           cvar("x").sub(cvar("z")),
                           cvar("y").sub(cvar("z")),
                           cvar("a").isa(cvar("x")),
                           cvar("b").isa(cvar("y")),
                           not(cvar("x").is(cvar("y"))),
                           not(cvar("a").is(cvar("b"))),
                       )
                       .get(); */

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_value_equals_variable_query() {
    let query = r#"match
$s1 == $s2;"#;

    let parsed = parse_query(query).unwrap();
    let expected = (); // typeql_match!(cvar("s1").eq(cvar("s2"))).get();
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
//     let expected = typeql_match!(cvar("s1").eq(cvar("s2"))).get();
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
    let expected = (); /* typeql_match!(
                           cvar("x").has(("release-date", gte(cvar("r")))),
                           cvar(()).has(("title", "Spy")).has(("release-date", cvar("r"))),
                           )
                           .get();
                       */
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_predicate() {
    let query = r#"match
$x has release-date < 1986-03-03T00:00,
    has tmdb-vote-count 100,
    has tmdb-vote-average <= 9.0;"#;

    let parsed = parse_query(query).unwrap();
    let expected = (); /* typeql_match!(cvar("x")
                           .has((
                               "release-date",
                               lt(NaiveDateTime::new(
                                   NaiveDate::from_ymd_opt(1986, 3, 3).unwrap(),
                                   NaiveTime::from_hms_opt(0, 0, 0).unwrap()
                               ))
                           ))
                           .has(("tmdb-vote-count", 100))
                           .has(("tmdb-vote-average", lte(9.0))))
                       .get(); */

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_time() {
    let query = r#"match
$x has release-date 1000-11-12T13:14:15;"#;

    let parsed = parse_query(query).unwrap();
    let expected = (); /* typeql_match!(cvar("x").has((
                           "release-date",
                           NaiveDateTime::new(
                               NaiveDate::from_ymd_opt(1000, 11, 12).unwrap(),
                               NaiveTime::from_hms_opt(13, 14, 15).unwrap()
                           ),
                       )))
                       .get();*/

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_big_years() {
    let query = r#"match
$x has release-date +12345-12-25T00:00;"#;

    let parsed = parse_query(query).unwrap();
    let expected = (); /* typeql_match!(cvar("x").has((
                           "release-date",
                           NaiveDateTime::new(NaiveDate::from_ymd_opt(12345, 12, 25).unwrap(), NaiveTime::from_hms_opt(0, 0, 0).unwrap()),
                       )))
                       .get(); */

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_small_years() {
    let query = r#"match
$x has release-date 0867-01-01T00:00;"#;

    let parsed = parse_query(query).unwrap();
    let expected = (); /* typeql_match!(cvar("x").has((
                           "release-date",
                           NaiveDateTime::new(NaiveDate::from_ymd_opt(867, 1, 1).unwrap(), NaiveTime::from_hms_opt(0, 0, 0).unwrap()),
                       )))
                       .get(); */

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_negative_years() {
    let query = r#"match
$x has release-date -3200-01-01T00:00;"#;

    let parsed = parse_query(query).unwrap();
    let expected = (); /* typeql_match!(cvar("x").has((
                           "release-date",
                           NaiveDateTime::new(NaiveDate::from_ymd_opt(-3200, 1, 1).unwrap(), NaiveTime::from_hms_opt(0, 0, 0).unwrap()),
                       )))
                       .get(); */

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_millis() {
    let query = r#"match
$x has release-date 1000-11-12T13:14:15.123;"#;

    let parsed = parse_query(query).unwrap();
    let expected = (); /* typeql_match!(cvar("x").has((
                           "release-date",
                           NaiveDateTime::new(
                               NaiveDate::from_ymd_opt(1000, 11, 12).unwrap(),
                               NaiveTime::from_hms_milli_opt(13, 14, 15, 123).unwrap(),
                           ),
                       )))
                       .get(); */

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn when_parsing_date_handle_millis_shorthand() {
    let query = r#"match
$x has release-date 1000-11-12T13:14:15.1;"#;

    let parsed = parse_query(query).unwrap();
    let expected = (); /* typeql_match!(cvar("x").has((
                           "release-date",
                           NaiveDateTime::new(
                               NaiveDate::from_ymd_opt(1000, 11, 12).unwrap(),
                               NaiveTime::from_hms_milli_opt(13, 14, 15, 100).unwrap(),
                           ),
                       )))
                       .get(); */

    let parsed_query = r#"match
$x has release-date 1000-11-12T13:14:15.100;"#;
    assert_valid_eq_repr!(expected, parsed, parsed_query);
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
    let expected = (); // typeql_match!(cvar("x").isa("movie").has(("tmdb-vote-count", lte(400)))).get();

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_parsing_attribute_query_by_value_variable() {
    let query = r#"match
$x = 5;
$a == $x isa age;"#;

    let parsed = parse_query(query).unwrap();
    let expected = (); // typeql_match!(vvar("x").assign(5), cvar("a").eq(vvar("x")).isa("age"),).get();

    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_parsing_precedence_operators() {
    let query = r#"match
$res = $a / $b * $c + $d ^ $e ^ $f / $g;"#;

    let parsed = parse_query(query).unwrap();
    let expected = (); /* typeql_match!(vvar("res").assign(
                           cvar("a")
                               .divide(cvar("b"))
                               .multiply(cvar("c"))
                               .add(cvar("d").power(cvar("e").power(cvar("f"))).divide(cvar("g")))
                       ))
                       .get(); */
    assert_valid_eq_repr!(expected, parsed, query);
}

#[test]
fn test_parsing_precedence_function_and_parentheses() {
    let query = r#"match
$res = $a + (round($b + $c) + $d) * $e;"#;

    let parsed = parse_query(query).unwrap();
    let expected = (); /* typeql_match!(
                           vvar("res").assign(cvar("a").add(round(cvar("b").add(vvar("c"))).add(cvar("d")).multiply(vvar("e"))))
                       )
                       .get(); */

    assert_valid_eq_repr!(expected, parsed, query);
}
