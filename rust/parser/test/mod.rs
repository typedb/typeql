/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
use pest::Parser;

use crate::{
    and,
    builder::{abs, ceil, constant, cvar, eq, floor, label, round},
    common::{
        token::{
            self,
            Order::{Asc, Desc},
            ValueType,
        },
        validatable::Validatable,
    },
    pattern::{
        ThingStatementBuilder,
        TypeStatementBuilder,
    },
    rel, type_,
    typeql_match,
};
use crate::parser::{Rule, TypeQLParser};

macro_rules! assert_valid_eq_repr {
    ($expected:ident, $parsed:ident, $query:ident) => {
        assert!($parsed.validate().is_ok());
        assert!($expected.validate().is_ok());
        assert_eq!($expected, $parsed);
        assert_eq!($expected.to_string(), $query);
        assert_eq!($parsed.to_string(), $query);
    };
}

#[test]
fn tmp() {
    let query_struct = r#"
    define
    struct coordinate:
      x value double,
      y value doube,
      time value datetime;

    struct nested-struct:
      nested value coordinate,
      comment value string;

    insert
    $x {

    "#;
    let result = TypeQLParser::parse(Rule::eof_query, query_struct);
    dbg!("{}", &result);

    // let query_fn = r#"
    // define
    // fun test_stream_1($x: person) -> {name}:
    //   match
    //    $x isa person, has name $name;
    //   return {$x};
    //
    // fun test_stream_many($x: person) -> {name, age, dob}:
    //   match
    //     $x isa person, has name $name, has age $age, has dob $dob;
    //   filter $name, $age, $dob;
    //   sort $name;
    //   offset 10;
    //   limit 10;
    //   return { $name, $age, $dob };
    //
    // fun test_single_1($x: person) -> long:
    //   match
    //     $x isa person;
    //   return count($x);
    //
    // fun test_single_many($x: person) -> long, long:
    //   match
    //     $x isa person, has age $a;
    //   return count($x), sum($a);
    //
    // fun test_stream_optional($x: person) -> { name, age?, dob }:
    //   match
    //     $x isa person, has name $name;
    //     try { $x has age $age; };
    //     $y in get_all_dob($x);
    //   return { $x, $age, $y };
    //
    // fun test_single_optional($x: person) -> name?, long, double?:
    //   match
    //     $x isa person, has age $age;
    //     try { $one_name = get_a_name($x); };
    //   return $one_name, count($x), std($age);
    //  "#;
    // let result = TypeQLParser::parse(Rule::eof_query, query_fn);
    // dbg!("{}", &result);
    //
    // let query_list_card = r#"
    // match
    // $x is $y;
    //
    // $x > $y;
    // $x like "abc";
    // $x like $y;
    // $x == $y;
    //
    // $x = $y;
    // $x = 10 + 11;
    //
    // person sub attribute @abstract,
    //   value long @values(1,2,3);
    //
    // $person sub attribute @abstract;
    // $person sub $parent, value string @regex("abc");
    //
    // $person type person;
    //
    // $x sub entity,
    //     owns age as abstract_age @card(0,*) @key @unique,
    //     owns name[] @card(0, *) @distinct,
    //     owns $attr[];
    //
    // $x sub relation,
    //     relates friend @card(10, 100) @cascade,
    //     relates best-friend[] @distinct,
    //     relates $role[];
    // get;
    // "#;
    // let result = TypeQLParser::parse(Rule::eof_query, query_list_card);
    // dbg!("{}", &result);
}
//
// #[test]
// fn test_simple_query() {
//     let query = r#"match
// $x isa movie;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").isa("movie")).get();
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_named_type_variable() {
//     let query = r#"match
// $a type attribute_label;
// get $a;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("a").type_("attribute_label")).get_fixed([cvar("a")]);
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_parsing_string_with_slash() {
//     let query = r#"match
// $x isa person,
//     has name "alice/bob";
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").isa("person").has(("name", "alice/bob"))).get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_relation_query() {
//     let query = r#"match
// $brando "Marl B" isa name;
// (actor: $brando, $char, production-with-cast: $prod);
// get $char, $prod;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(
//         cvar("brando").eq("Marl B").isa("name"),
//         rel(("actor", "brando")).links("char").links(("production-with-cast", "prod")),
//     )
//         .get_fixed([cvar("char"), cvar("prod")]);
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_role_type_scoped_globally() {
//     let query = r#"match
// $m relates spouse;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("m").relates("spouse")).get();
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_role_type_not_scoped() {
//     let query = r#"match
// marriage relates $s;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(type_("marriage").relates(cvar("s"))).get();
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_predicate_query_1() {
//     let query = r#"match
// $x isa movie,
//     has title $t;
// {
//     $t "Apocalypse Now";
// } or {
//     $t < "Juno";
//     $t > "Godfather";
// } or {
//     $t "Spy";
// };
// $t != "Apocalypse Now";
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected =
//         typeql_match!(
//             cvar("x").isa("movie").has(("title", cvar("t"))),
//             or!(
//                 cvar("t").eq("Apocalypse Now"),
//                 and!(cvar("t").lt("Juno"), cvar("t").gt("Godfather")),
//                 cvar("t").eq("Spy"),
//             ),
//             cvar("t").neq("Apocalypse Now"),
//         )
//             .get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_predicate_query_2() {
//     let query = r#"match
// $x isa movie,
//     has title $t;
// {
//     $t <= "Juno";
//     $t >= "Godfather";
//     $t != "Heat";
// } or {
//     $t "The Muppets";
// };
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(
//         cvar("x").isa("movie").has(("title", cvar("t"))),
//         or!(
//             and!(cvar("t").lte("Juno"), cvar("t").gte("Godfather"), cvar("t").neq("Heat")),
//             cvar("t").eq("The Muppets"),
//         ),
//     )
//         .get();
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_predicate_query_3() {
//     let query = r#"match
// ($x, $y);
// $y isa person,
//     has name $n;
// {
//     $n contains "ar";
// } or {
//     $n like "^M.*$";
// };
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(
//         rel("x").links("y"),
//         cvar("y").isa("person").has(("name", cvar("n"))),
//         or!(cvar("n").contains("ar"), cvar("n").like("^M.*$")),
//     )
//         .get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_predicate_query_4() {
//     let query = r#"match
// $x has age $y;
// $y >= $z;
// $z 18 isa age;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected =
//         typeql_match!(cvar("x").has(("age", cvar("y"))), cvar("y").gte(cvar("z")), cvar("z").eq(18).isa("age"),).get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_concept_variable() {
//     let query = r#"match
// $x sub $z;
// $y sub $z;
// $a isa $x;
// $b isa $y;
// not { $x is $y; };
// not { $a is $b; };
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(
//         cvar("x").sub(cvar("z")),
//         cvar("y").sub(cvar("z")),
//         cvar("a").isa(cvar("x")),
//         cvar("b").isa(cvar("y")),
//         not(cvar("x").is(cvar("y"))),
//         not(cvar("a").is(cvar("b"))),
//     )
//     .get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_value_equals_variable_query() {
//     let query = r#"match
// $s1 == $s2;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("s1").eq(cvar("s2"))).get();
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// // TODO: Remove when we fully deprecate '=' for ThingVariable equality.
// #[test]
// fn test_value_equals_variable_query_backwards_compatibility() {
//     let query = r#"match
// $s1 = $s2;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("s1").eq(cvar("s2"))).get();
//
//     assert_eq!(expected, parsed);
//     assert_eq!(expected, parse_query(parsed.to_string().as_str()).unwrap().into_get());
// }
//
// #[test]
// fn test_movies_released_after_or_at_the_same_time_as_spy() {
//     let query = r#"match
// $x has release-date >= $r;
// $_ has title "Spy",
//     has release-date $r;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(
//         cvar("x").has(("release-date", gte(cvar("r")))),
//         cvar(()).has(("title", "Spy")).has(("release-date", cvar("r"))),
//     )
//     .get();
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_predicate() {
//     let query = r#"match
// $x has release-date < 1986-03-03T00:00,
//     has tmdb-vote-count 100,
//     has tmdb-vote-average <= 9.0;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x")
//         .has((
//             "release-date",
//             lt(NaiveDateTime::new(
//                 NaiveDate::from_ymd_opt(1986, 3, 3).unwrap(),
//                 NaiveTime::from_hms_opt(0, 0, 0).unwrap()
//             ))
//         ))
//         .has(("tmdb-vote-count", 100))
//         .has(("tmdb-vote-average", lte(9.0))))
//     .get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn when_parsing_date_handle_time() {
//     let query = r#"match
// $x has release-date 1000-11-12T13:14:15;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").has((
//         "release-date",
//         NaiveDateTime::new(
//             NaiveDate::from_ymd_opt(1000, 11, 12).unwrap(),
//             NaiveTime::from_hms_opt(13, 14, 15).unwrap()
//         ),
//     )))
//     .get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn when_parsing_date_handle_big_years() {
//     let query = r#"match
// $x has release-date +12345-12-25T00:00;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").has((
//         "release-date",
//         NaiveDateTime::new(NaiveDate::from_ymd_opt(12345, 12, 25).unwrap(), NaiveTime::from_hms_opt(0, 0, 0).unwrap()),
//     )))
//     .get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn when_parsing_date_handle_small_years() {
//     let query = r#"match
// $x has release-date 0867-01-01T00:00;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").has((
//         "release-date",
//         NaiveDateTime::new(NaiveDate::from_ymd_opt(867, 1, 1).unwrap(), NaiveTime::from_hms_opt(0, 0, 0).unwrap()),
//     )))
//     .get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn when_parsing_date_handle_negative_years() {
//     let query = r#"match
// $x has release-date -3200-01-01T00:00;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").has((
//         "release-date",
//         NaiveDateTime::new(NaiveDate::from_ymd_opt(-3200, 1, 1).unwrap(), NaiveTime::from_hms_opt(0, 0, 0).unwrap()),
//     )))
//     .get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn when_parsing_date_handle_millis() {
//     let query = r#"match
// $x has release-date 1000-11-12T13:14:15.123;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").has((
//         "release-date",
//         NaiveDateTime::new(
//             NaiveDate::from_ymd_opt(1000, 11, 12).unwrap(),
//             NaiveTime::from_hms_milli_opt(13, 14, 15, 123).unwrap(),
//         ),
//     )))
//     .get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn when_parsing_date_handle_millis_shorthand() {
//     let query = r#"match
// $x has release-date 1000-11-12T13:14:15.1;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").has((
//         "release-date",
//         NaiveDateTime::new(
//             NaiveDate::from_ymd_opt(1000, 11, 12).unwrap(),
//             NaiveTime::from_hms_milli_opt(13, 14, 15, 100).unwrap(),
//         ),
//     )))
//     .get();
//
//     let parsed_query = r#"match
// $x has release-date 1000-11-12T13:14:15.100;
// get;"#;
//     assert_valid_eq_repr!(expected, parsed, parsed_query);
// }
//
// #[test]
// fn when_parsing_date_error_when_parsing_overly_precise_decimal_seconds() {
//     let query = r#"match
// $x has release-date 1000-11-12T13:14:15.000123456;
// get;"#;
//
//     let parsed = parse_query(query);
//     assert!(parsed.is_err());
//     assert!(parsed.unwrap_err().to_string().contains("line 2"));
// }
//
// #[test]
// fn when_parsing_date_error_when_handling_overly_precise_nanos() {
//     let validated = typeql_match!(cvar("x").has((
//         "release-date",
//         NaiveDateTime::new(
//             NaiveDate::from_ymd_opt(1000, 11, 12).unwrap(),
//             NaiveTime::from_hms_nano_opt(13, 14, 15, 123450000).unwrap(),
//         ),
//     )))
//     .validated();
//     assert!(validated.is_err());
//     assert!(validated.unwrap_err().to_string().contains("more precise than 1 millisecond"));
// }
//
// #[test]
// fn test_parsing_long_predicate_query() {
//     let query = r#"match
// $x isa movie,
//     has tmdb-vote-count <= 400;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").isa("movie").has(("tmdb-vote-count", lte(400)))).get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_parsing_attribute_query_by_value_variable() {
//     let query = r#"match
// ?x = 5;
// $a == ?x isa age;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(vvar("x").assign(5), cvar("a").eq(vvar("x")).isa("age"),).get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_parsing_variable_name_clash_throws() {
//     let query = r"match\n
// $z isa person, has age $y;
// ?y = $y;
// get;";
//
//     let parsed = parse_query(query);
//     assert!(parsed.is_err());
// }
//
// #[test]
// fn test_parsing_precedence_operators() {
//     let query = r#"match
// ?res = $a / $b * $c + $d ^ $e ^ $f / $g;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(vvar("res").assign(
//         cvar("a")
//             .divide(cvar("b"))
//             .multiply(cvar("c"))
//             .add(cvar("d").power(cvar("e").power(cvar("f"))).divide(cvar("g")))
//     ))
//     .get();
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_parsing_precedence_function_and_parenthesis() {
//     let query = r#"match
// ?res = $a + (round($b + ?c) + $d) * ?e;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(
//         vvar("res").assign(cvar("a").add(round(cvar("b").add(vvar("c"))).add(cvar("d")).multiply(vvar("e"))))
//     )
//     .get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_builder_precedence_operators() {
//     let query = r#"match
// ?a = ($b + $c) * $d;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(vvar("a").assign(cvar("b").add(cvar("c")).multiply(cvar("d")))).get();
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_builder_associativity_left() {
//     let query = r#"match
// ?a = $b - ($c - $d);
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(vvar("a").assign(cvar("b").subtract(cvar("c").subtract(cvar("d"))))).get();
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_builder_associativity_right() {
//     let query = r#"match
// ?a = ($b ^ $c) ^ $d;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(vvar("a").assign(cvar("b").power(cvar("c")).power(cvar("d")))).get();
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_parenthesis_preserving() {
//     let query = r#"match
// ?a = $b + ($c + $d) + $e * ($f * $g);
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(vvar("a")
//         .assign(cvar("b").add(cvar("c").add(cvar("d"))).add(cvar("e").multiply(cvar("f").multiply(cvar("g"))))))
//     .get();
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_parenthesis_not_adding_unnecessary() {
//     let query = r#"match
// ?a = $b + $c + $d + $e * $f * $g;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(vvar("a")
//         .assign(cvar("b").add(cvar("c")).add(cvar("d")).add(cvar("e").multiply(cvar("f")).multiply(cvar("g")))))
//     .get();
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_function_min() {
//     let query = r#"match
// $x isa commodity,
//     has price $p;
// (commodity: $x, qty: $q) isa order;
// ?net = $p * $q;
// ?gross = min(?net * 1.21, ?net + 100.0);
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(
//         cvar("x").isa("commodity").has(("price", cvar("p"))),
//         rel(("commodity", "x")).links(("qty", "q")).isa("order"),
//         vvar("net").assign(cvar("p").multiply(cvar("q"))),
//         vvar("gross").assign(min!(vvar("net").multiply(1.21), vvar("net").add(100.0)))
//     )
//     .get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_function_max() {
//     let query = r#"match
// $x isa commodity,
//     has price $p;
// (commodity: $x, qty: $q) isa order;
// ?net = $p * $q;
// ?gross = max(?net * 1.21, ?net + 100.0);
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(
//         cvar("x").isa("commodity").has(("price", cvar("p"))),
//         rel(("commodity", "x")).links(("qty", "q")).isa("order"),
//         vvar("net").assign(cvar("p").multiply(cvar("q"))),
//         vvar("gross").assign(max!(vvar("net").multiply(1.21), vvar("net").add(100.0)))
//     )
//     .get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_function_abs() {
//     let query = r#"match
// $x isa commodity,
//     has price $p;
// (commodity: $x, qty: $q) isa order;
// ?net = $p * $q;
// ?value = abs(?net * 1.21);
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(
//         cvar("x").isa("commodity").has(("price", cvar("p"))),
//         rel(("commodity", "x")).links(("qty", "q")).isa("order"),
//         vvar("net").assign(cvar("p").multiply(cvar("q"))),
//         vvar("value").assign(abs(vvar("net").multiply(1.21)))
//     )
//     .get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_function_ceil() {
//     let query = r#"match
// $x isa commodity,
//     has price $p;
// (commodity: $x, qty: $q) isa order;
// ?net = $p * $q;
// ?value = ceil(?net * 1.21);
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(
//         cvar("x").isa("commodity").has(("price", cvar("p"))),
//         rel(("commodity", "x")).links(("qty", "q")).isa("order"),
//         vvar("net").assign(cvar("p").multiply(cvar("q"))),
//         vvar("value").assign(ceil(vvar("net").multiply(1.21)))
//     )
//     .get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_function_floor() {
//     let query = r#"match
// $x isa commodity,
//     has price $p;
// (commodity: $x, qty: $q) isa order;
// ?net = $p * $q;
// ?value = floor(?net * 1.21);
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(
//         cvar("x").isa("commodity").has(("price", cvar("p"))),
//         rel(("commodity", "x")).links(("qty", "q")).isa("order"),
//         vvar("net").assign(cvar("p").multiply(cvar("q"))),
//         vvar("value").assign(floor(vvar("net").multiply(1.21)))
//     )
//     .get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_function_round() {
//     let query = r#"match
// $x isa commodity,
//     has price $p;
// (commodity: $x, qty: $q) isa order;
// ?net = $p * $q;
// ?value = round(?net * 1.21);
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(
//         cvar("x").isa("commodity").has(("price", cvar("p"))),
//         rel(("commodity", "x")).links(("qty", "q")).isa("order"),
//         vvar("net").assign(cvar("p").multiply(cvar("q"))),
//         vvar("value").assign(round(vvar("net").multiply(1.21)))
//     )
//     .get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_schema_query() {
//     let query = r#"match
// $x plays starring:actor;
// get;
// sort $x asc;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").plays(("starring", "actor"))).get().sort([(cvar("x"), Asc)]);
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_get_sort_on_concept_variable() {
//     let query = r#"match
// $x isa movie,
//     has rating $r;
// get;
// sort $r desc;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").isa("movie").has(("rating", cvar("r")))).get().sort([(cvar("r"), Desc)]);
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_get_sort_on_value_variable() {
//     let query = r#"match
// $x isa movie,
//     has rating $r;
// ?l = 100 - $r;
// get;
// sort ?l desc;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(
//         cvar("x").isa("movie").has(("rating", cvar("r"))),
//         vvar("l").assign(constant(100).subtract(cvar("r")))
//     )
//     .get()
//     .sort([(vvar("l"), Desc)]);
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_get_sort_multiple() {
//     let query = r#"match
// $x isa movie,
//     has title $t,
//     has rating $r;
// ?rate = $r * 100;
// get;
// sort ?rate desc, $t;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(
//         cvar("x").isa("movie").has(("title", cvar("t"))).has(("rating", cvar("r"))),
//         vvar("rate").assign(cvar("r").multiply(100)),
//     )
//     .get()
//     .sort(sort_vars!((vvar("rate"), Desc), cvar("t")));
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_get_sort_limit() {
//     let query = r#"match
// $x isa movie,
//     has rating $r;
// get;
// sort $r; limit 10;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").isa("movie").has(("rating", cvar("r")))).get().sort([cvar("r")]).limit(10);
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_get_sort_offset_limit() {
//     let query = r#"match
// $x isa movie,
//     has rating $r;
// get;
// sort $r desc; offset 10; limit 10;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").isa("movie").has(("rating", cvar("r"))))
//         .get()
//         .sort([(cvar("r"), Desc)])
//         .offset(10)
//         .limit(10);
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_get_offset_limit() {
//     let query = r#"match
// $y isa movie,
//     has title $n;
// get;
// offset 2; limit 4;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("y").isa("movie").has(("title", cvar("n")))).get().offset(2).limit(4);
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_fetch_query() {
//     let query = r#"match
// $x isa movie,
//     has title "Godfather",
//     has release-date $d;
// fetch
// $d;
// $d as date;
// $x: name, title as t, name as "Movie name";
// $x as movie: name;
// $x as "Movie name": name;
// label-a: {
//     match
//     ($d, $c) isa director;
//     fetch
//     $d: name, age;
// };
// label-b: {
//     match
//     ($d, $c) isa director;
//     get $d;
//     count;
// };"#;
//     let result = parse_query(query);
//     let parsed = match result {
//         Ok(query) => query.into_fetch(),
//         Err(error) => {
//             println!("{}", error);
//             panic!();
//         }
//     };
//
//     let projections: Vec<Projection> = vec![
//         cvar("d").into(),
//         cvar("d").label("date").into(),
//         cvar("x").map_attributes(vec!["name".into(), ("title", "t").into(), ("name", "Movie name").into()]),
//         cvar("x").label("movie").map_attribute("name"),
//         cvar("x").label("Movie name").map_attribute("name"),
//         label("label-a").map_subquery_fetch(
//             typeql_match!(rel(cvar("d")).links(cvar("c")).isa("director"))
//                 .fetch(vec![cvar("d").map_attributes(vec!["name".into(), "age".into()])]),
//         ),
//         label("label-b").map_subquery_get_aggregate(
//             typeql_match!(rel(cvar("d")).links(cvar("c")).isa("director")).get_fixed([cvar("d")]).count(),
//         ),
//     ];
//     let expected = typeql_match!(cvar("x").isa("movie").has(("title", "Godfather")).has(("release-date", cvar("d"))))
//         .fetch(projections);
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_variables_everywhere_query() {
//     let query = r#"match
// ($p: $x, $y);
// $x isa $z;
// $y "crime";
// $z sub production;
// has-genre relates $p;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(
//         rel((cvar("p"), cvar("x"))).links("y"),
//         cvar("x").isa(cvar("z")),
//         cvar("y").eq("crime"),
//         cvar("z").sub("production"),
//         type_("has-genre").relates(cvar("p")),
//     )
//     .get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_parsing_relates_type_variable() {
//     let query = r#"match
// $x isa $type;
// $type relates someRole;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").isa(cvar("type")), cvar("type").relates("someRole")).get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_or_query() {
//     let query = r#"match
// $x isa movie;
// {
//     $y "drama" isa genre;
//     ($x, $y);
// } or {
//     $x "The Muppets";
// };
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(
//         cvar("x").isa("movie"),
//         or!(and!(cvar("y").eq("drama").isa("genre"), rel("x").links("y")), cvar("x").eq("The Muppets"))
//     )
//     .get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_disjunction_not_in_conjunction() {
//     let query = r#"match
// {
//     $x isa person;
// } or {
//     $x isa company;
// };
// get;"#;
//     let res = parse_query(query);
//     assert!(res.is_err(), "{res:?}")
// }
//
// #[test]
// fn test_nested_conjunction_and_disjunction() {
//     let query = r#"match
// $y isa $p;
// {
//     ($y, $q);
// } or {
//     $x isa $p;
//     {
//         $x has first-name $y;
//     } or {
//         $x has last-name $z;
//     };
// };
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(
//         cvar("y").isa(cvar("p")),
//         or!(
//             rel("y").links("q"),
//             and!(
//                 cvar("x").isa(cvar("p")),
//                 or!(cvar("x").has(("first-name", cvar("y"))), cvar("x").has(("last-name", cvar("z"))))
//             )
//         )
//     )
//     .get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_disjunction_not_binding_conjunction() {
//     let query = r#"match
// $y isa $p;
// { ($y, $q); } or { $x isa $p; { $x has first-name $y; } or { $q has last-name $z; }; };
// get;"#;
//     let parsed = parse_query(query);
//     assert!(parsed.is_err());
// }
//
// #[test]
// fn test_aggregate_count_query() {
//     let query = r#"match
// ($x, $y) isa friendship;
// get $x, $y;
// count;"#;
//
//     let parsed = parse_query(query).unwrap().into_get_aggregate();
//     let expected = typeql_match!(rel("x").links("y").isa("friendship")).get_fixed([cvar("x"), cvar("y")]).count();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_aggregate_group_count_query() {
//     let query = r#"match
// ($x, $y) isa friendship;
// get $x, $y;
// group $x; count;"#;
//
//     let parsed = parse_query(query).unwrap().into_get_group_aggregate();
//     let expected =
//         typeql_match!(rel("x").links("y").isa("friendship")).get_fixed([cvar("x"), cvar("y")]).group(cvar("x")).count();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_single_line_group_aggregate_max_query() {
//     let query = r#"match
// $x has age $a;
// get;
// group $x; max $a;"#;
//
//     let parsed = parse_query(query).unwrap().into_get_group_aggregate();
//     let expected = typeql_match!(cvar("x").has(("age", cvar("a")))).get().group(cvar("x")).max(cvar("a"));
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_multi_line_group_aggregate_max_query() {
//     let query = r#"match
// ($x, $y) isa friendship;
// $y has age $z;
// get;
// group $x; max $z;"#;
//
//     let parsed = parse_query(query).unwrap().into_get_group_aggregate();
//     let expected = typeql_match!(rel("x").links("y").isa("friendship"), cvar("y").has(("age", cvar("z"))))
//         .get()
//         .group(cvar("x"))
//         .max(cvar("z"));
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_multi_line_filtered_group_aggregate_max_query() {
//     let query = r#"match
// ($x, $y) isa friendship;
// $y has age $z;
// get $x, $y, $z;
// group $x; max $z;"#;
//
//     let parsed = parse_query(query).unwrap().into_get_group_aggregate();
//     let expected = typeql_match!(rel("x").links("y").isa("friendship"), cvar("y").has(("age", cvar("z"))))
//         .get_fixed([cvar("x"), cvar("y"), cvar("z")])
//         .group(cvar("x"))
//         .max(cvar("z"));
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_filtered_group_aggregates_on_value_variable() {
//     let query = r#"match
// $i ($x, $s) isa income-source;
// $i has value $v,
//     has tax-rate $r;
// ?t = $r * $v;
// get $x, ?t;
// group $x; sum ?t;"#;
//
//     let parsed = parse_query(query).unwrap().into_get_group_aggregate();
//     let filter: [Variable; 2] = [cvar("x").into(), vvar("t").into()];
//     let expected = typeql_match!(
//         cvar("i").links("x").links("s").isa("income-source"),
//         cvar("i").has(("value", cvar("v"))).has(("tax-rate", cvar("r"))),
//         vvar("t").assign(cvar("r").multiply(cvar("v"))),
//     )
//     .get_fixed(filter)
//     .group(cvar("x"))
//     .sum(vvar("t"));
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn when_comparing_count_query_using_typeql_and_rust_typeql_they_are_equivalent() {
//     let query = r#"match
// $x isa movie,
//     has title "Godfather";
// get;
// count;"#;
//
//     let parsed = parse_query(query).unwrap().into_get_aggregate();
//     let expected = typeql_match!(cvar("x").isa("movie").has(("title", "Godfather"))).get().count();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_delete_query() {
//     let query = r#"match
// $x isa movie,
//     has title "The Title";
// $y isa movie;
// delete
// $x isa movie;
// $y isa movie;"#;
//
//     let parsed = parse_query(query).unwrap().into_delete();
//     let expected = typeql_match!(cvar("x").isa("movie").has(("title", "The Title")), cvar("y").isa("movie"))
//         .delete([cvar("x").isa("movie"), cvar("y").isa("movie")]);
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_insert_query() {
//     let query = r#"insert
// $x isa pokemon,
//     has name "Pichu";
// $y isa pokemon,
//     has name "Pikachu";
// $z isa pokemon,
//     has name "Raichu";
// (evolves-from: $x, evolves-to: $y) isa evolution;
// (evolves-from: $y, evolves-to: $z) isa evolution;"#;
//
//     let parsed = parse_query(query).unwrap().into_insert();
//     let expected = typeql_insert!(
//         cvar("x").isa("pokemon").has(("name", "Pichu")),
//         cvar("y").isa("pokemon").has(("name", "Pikachu")),
//         cvar("z").isa("pokemon").has(("name", "Raichu")),
//         rel(("evolves-from", "x")).links(("evolves-to", "y")).isa("evolution"),
//         rel(("evolves-from", "y")).links(("evolves-to", "z")).isa("evolution")
//     );
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_update_query() {
//     let query = r#"match
// $x isa person,
//     has name "alice",
//     has age $a;
// delete
// $x has $a;
// insert
// $x has age 25;"#;
//
//     let parsed = parse_query(query).unwrap().into_update();
//     let expected = typeql_match!(cvar("x").isa("person").has(("name", "alice")).has(("age", cvar("a"))))
//         .delete(cvar("x").has(cvar("a")))
//         .insert(cvar("x").has(("age", 25)));
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn when_parsing_as_in_define_result_is_same_as_sub() {
//     let query = r#"define
// parent sub role;
// child sub role;
// parenthood sub relation,
//     relates parent,
//     relates child;
// fatherhood sub parenthood,
//     relates father as parent,
//     relates son as child;"#;
//
//     let parsed = parse_query(query).unwrap().into_define();
//     let expected = typeql_define!(
//         type_("parent").sub("role"),
//         type_("child").sub("role"),
//         type_("parenthood").sub("relation").relates("parent").relates("child"),
//         type_("fatherhood").sub("parenthood").relates(("father", "parent")).relates(("son", "child"))
//     );
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn when_parsing_as_in_match_result_is_same_as_sub() {
//     let query = r#"match
// $f sub parenthood,
//     relates father as parent,
//     relates son as child;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected =
//         typeql_match!(cvar("f").sub("parenthood").relates(("father", "parent")).relates(("son", "child"))).get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_define_query_with_owns_overrides() {
//     let query = r#"define
// triangle sub entity;
// triangle owns side-length;
// triangle-right-angled sub triangle;
// triangle-right-angled owns hypotenuse-length as side-length;"#;
//
//     let parsed = parse_query(query).unwrap().into_define();
//     let expected = typeql_define!(
//         type_("triangle").sub("entity"),
//         type_("triangle").owns("side-length"),
//         type_("triangle-right-angled").sub("triangle"),
//         type_("triangle-right-angled").owns(("hypotenuse-length", "side-length"))
//     );
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_define_query_with_relates_overrides() {
//     let query = r#"define
// pokemon sub entity;
// evolves sub relation;
// evolves relates from,
//     relates to;
// evolves-final sub evolves;
// evolves-final relates from-final as from;"#;
//
//     let parsed = parse_query(query).unwrap().into_define();
//     let expected = typeql_define!(
//         type_("pokemon").sub("entity"),
//         type_("evolves").sub("relation"),
//         type_("evolves").relates("from").relates("to"),
//         type_("evolves-final").sub("evolves"),
//         type_("evolves-final").relates(("from-final", "from"))
//     );
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_define_query_with_plays_overrides() {
//     let query = r#"define
// pokemon sub entity;
// evolves sub relation;
// evolves relates from,
//     relates to;
// evolves-final sub evolves;
// evolves-final relates from-final as from;
// pokemon plays evolves-final:from-final as from;"#;
//
//     let parsed = parse_query(query).unwrap().into_define();
//     let expected = typeql_define!(
//         type_("pokemon").sub("entity"),
//         type_("evolves").sub("relation"),
//         type_("evolves").relates("from").relates("to"),
//         type_("evolves-final").sub("evolves"),
//         type_("evolves-final").relates(("from-final", "from")),
//         type_("pokemon").plays(("evolves-final", "from-final", "from"))
//     );
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_define_query() {
//     let query = r#"define
// pokemon sub entity;
// evolution sub relation;
// evolves-from sub role;
// evolves-to sub role;
// evolves relates from,
//     relates to;
// pokemon plays evolves:from,
//     plays evolves:to,
//     owns name;"#;
//
//     let parsed = parse_query(query).unwrap().into_define();
//     let expected = typeql_define!(
//         type_("pokemon").sub("entity"),
//         type_("evolution").sub("relation"),
//         type_("evolves-from").sub("role"),
//         type_("evolves-to").sub("role"),
//         type_("evolves").relates("from").relates("to"),
//         type_("pokemon").plays(("evolves", "from")).plays(("evolves", "to")).owns("name")
//     );
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn when_parsing_undefine_query_result_is_same_as_java_typeql() {
//     let query = r#"undefine
// pokemon sub entity;
// evolution sub relation;
// evolves-from sub role;
// evolves-to sub role;
// evolves relates from,
//     relates to;
// pokemon plays evolves:from,
//     plays evolves:to,
//     owns name;"#;
//
//     let parsed = parse_query(query).unwrap().into_undefine();
//     let expected = typeql_undefine!(
//         type_("pokemon").sub("entity"),
//         type_("evolution").sub("relation"),
//         type_("evolves-from").sub("role"),
//         type_("evolves-to").sub("role"),
//         type_("evolves").relates("from").relates("to"),
//         type_("pokemon").plays(("evolves", "from")).plays(("evolves", "to")).owns("name")
//     );
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_undefine_rule() {
//     let query = r#"undefine
// rule r;"#;
//
//     let parsed = parse_query(query).unwrap().into_undefine();
//     let expected = typeql_undefine!(rule("r"));
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_match_insert_query() {
//     let query = r#"match
// $x isa language;
// insert
// $x has name "HELLO";"#;
//
//     let parsed = parse_query(query).unwrap().into_insert();
//     let expected = typeql_match!(cvar("x").isa("language")).insert(cvar("x").has(("name", "HELLO")));
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_define_abstract_entity_query() {
//     let query = r#"define
// concrete-type sub entity;
// abstract-type sub entity,
//     abstract;"#;
//
//     let parsed = parse_query(query).unwrap().into_define();
//     let expected =
//         typeql_define!(type_("concrete-type").sub("entity"), type_("abstract-type").sub("entity").abstract_());
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_match_value_type_query() {
//     let query = r#"match
// $x value double;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").value(ValueType::Double)).get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_parsing_without_var_concept() {
//     let query = r#"match
// $_ isa person;
// get;"#;
//
//     let parsed = parse_query(query);
//     assert!(parsed.is_err());
//     let built = typeql_match!(cvar(()).isa("person")).get().validated();
//     assert!(built.is_err());
// }
//
// #[test]
// fn when_parsing_date_keyword_parse_as_the_correct_value_type() {
//     let query = r#"match
// $x value datetime;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").value(ValueType::DateTime)).get();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_define_value_type_query() {
//     let query = r#"define
// my-type sub attribute,
//     value long;"#;
//
//     let parsed = parse_query(query).unwrap().into_define();
//     let expected = typeql_define!(type_("my-type").sub("attribute").value(ValueType::Long));
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_escape_string() {
//     let input = r#"This has \"double quotes\" and a single-quoted backslash: '\\'"#;
//
//     let query = format!(
//         r#"insert
// $_ isa movie,
//     has title "{input}";"#
//     );
//
//     let parsed = parse_query(&query).unwrap().into_insert();
//     let expected = typeql_insert!(cvar(()).isa("movie").has(("title", input)));
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn when_parsing_query_with_comments_they_are_ignored() {
//     let query = "match\n\n# there's a comment here\n$x isa###WOW HERES ANOTHER###\r\nmovie;
// get; count;";
//     let uncommented = r#"match
// $x isa movie;
// get;
// count;"#;
//
//     let parsed = parse_query(query).unwrap().into_get_aggregate();
//     let expected = typeql_match!(cvar("x").isa("movie")).get().count();
//
//     assert_valid_eq_repr!(expected, parsed, uncommented);
// }
//
// #[test]
// fn test_parsing_pattern() {
//     let pattern = r#"{
//     (wife: $a, husband: $b) isa marriage;
//     $a has gender "male";
//     $b has gender "female";
// }"#;
//
//     let parsed = parse_pattern(pattern).unwrap().into_conjunction();
//     let expected = and!(
//         rel(("wife", "a")).links(("husband", "b")).isa("marriage"),
//         cvar("a").has(("gender", "male")),
//         cvar("b").has(("gender", "female"))
//     );
//
//     assert_valid_eq_repr!(expected, parsed, pattern);
// }
//
// #[test]
// fn test_parsing_patterns() {
//     let patterns = r#"(wife: $a, husband: $b) isa marriage;
//     $a has gender "male";
//     $b has gender "female";
// "#;
//
//     let parsed = parse_patterns(patterns).unwrap().into_iter().map(|p| p.into_statement()).collect::<Vec<_>>();
//     let expected: Vec<Statement> = vec![
//         Statement::Thing(rel(("wife", "a")).links(("husband", "b")).isa("marriage")),
//         Statement::Thing(cvar("a").has(("gender", "male"))),
//         Statement::Thing(cvar("b").has(("gender", "female"))),
//     ];
//
//     assert_eq!(expected, parsed);
// }
//
// #[test]
// fn test_define_rules() {
//     let query = r#"define
// rule a-rule: when {
//     $x isa person;
//     not {
//         $x has name "Alice";
//         $x has name "Bob";
//     };
//     {
//         ($x) isa friendship;
//     } or {
//         ($x) isa employment;
//     };
// } then {
//     $x has is_interesting true;
// };"#;
//
//     let parsed = parse_query(query).unwrap().into_define();
//     let expected = typeql_define!(rule("a-rule")
//         .when(and!(
//             cvar("x").isa("person"),
//             not(and!(cvar("x").has(("name", "Alice")), cvar("x").has(("name", "Bob")))),
//             or!(rel(cvar("x")).isa("friendship"), rel(cvar("x")).isa("employment"))
//         ))
//         .then(cvar("x").has(("is_interesting", true))));
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_parsing_definables() {
//     let query = r#"athlete sub person;
//       runner sub athlete;
//       sprinter sub runner;"#;
//
//     let parsed = parse_definables(query).unwrap().into_iter().map(|p| p.into_type_statement()).collect::<Vec<_>>();
//     let expected =
//         vec![type_("athlete").sub("person"), type_("runner").sub("athlete"), type_("sprinter").sub("runner")];
//
//     assert_eq!(expected, parsed);
// }
//
// #[test]
// fn test_parsing_variable_rel() {
//     let variable = "(wife: $a, husband: $b) isa marriage";
//
//     let parsed = parse_statement(variable).unwrap();
//     if let Statement::Thing(parsed_var) = parsed {
//         let expected = rel(("wife", "a")).links(("husband", "b")).isa("marriage");
//         assert_valid_eq_repr!(expected, parsed_var, variable);
//     } else {
//         panic!("Expected ThingVariable, found {variable:?}.");
//     }
// }
//
// #[test]
// fn test_parsing_variable_has() {
//     let variable = "$x has is_interesting true";
//
//     let parsed = parse_statement(variable).unwrap();
//     if let Statement::Thing(parsed_var) = parsed {
//         let expected = cvar("x").has(("is_interesting", true));
//         assert_valid_eq_repr!(expected, parsed_var, variable);
//     } else {
//         panic!("Expected ThingVariable, found {variable:?}.");
//     }
// }
//
// #[test]
// fn test_parsing_label() {
//     let label = "label_with-symbols";
//
//     let parsed = parse_label(label).unwrap();
//     let expected = Label { scope: None, name: String::from(label) };
//     assert_eq!(expected, parsed);
// }
//
// #[test]
// fn test_rule_attach_attribute_by_value_predicate() {
//     let query = r#"define
// rule attach-val: when {
//     $x has age $a;
//     ?d = $a * 365;
// } then {
//     $x has days == ?d;
// };"#;
//
//     let parsed = parse_query(query).unwrap().into_define();
//     let expected = typeql_define!(rule("attach-val")
//         .when(and!(cvar("x").has(("age", cvar("a"))), vvar("d").assign(cvar("a").multiply(365)),))
//         .then(cvar("x").has(("days", eq(vvar("d"))))));
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_rule_attach_attribute_by_value() {
//     let query = r#"define
// rule attach-val: when {
//     $x has age $a;
//     ?d = $a * 365;
// } then {
//     $x has days ?d;
// };"#;
//
//     let expected_query = r#"define
// rule attach-val: when {
//     $x has age $a;
//     ?d = $a * 365;
// } then {
//     $x has days ?d;
// };"#;
//
//     let parsed = parse_query(query).unwrap().into_define();
//     let expected = typeql_define!(rule("attach-val")
//         .when(and!(cvar("x").has(("age", cvar("a"))), vvar("d").assign(cvar("a").multiply(365)),))
//         .then(cvar("x").has(("days", vvar("d")))));
//
//     assert_eq!(expected, parsed);
//     assert_eq!(expected_query, parsed.to_string());
// }
//
// #[test]
// fn test_parsing_boolean() {
//     let query = r#"insert
// $_ has flag true;"#;
//
//     let parsed = parse_query(query).unwrap().into_insert();
//     let expected = typeql_insert!(cvar(()).has(("flag", true)));
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_parsing_aggregate_group() {
//     let query = r#"match
// $x isa movie;
// get;
// group $x;"#;
//
//     let parsed = parse_query(query).unwrap().into_get_group();
//     let expected = typeql_match!(cvar("x").isa("movie")).get().group(cvar("x"));
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_parsing_aggregate_group_count() {
//     let query = r#"match
// $x isa movie;
// get;
// group $x; count;"#;
//
//     let parsed = parse_query(query).unwrap().into_get_group_aggregate();
//     let expected = typeql_match!(cvar("x").isa("movie")).get().group(cvar("x")).count();
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_parsing_aggregate_std() {
//     let query = r#"match
// $x isa movie;
// get;
// std $x;"#;
//
//     let parsed = parse_query(query).unwrap().into_get_aggregate();
//     let expected = typeql_match!(cvar("x").isa("movie")).get().std(cvar("x"));
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_parsing_aggregate_to_string() {
//     let query = r#"match
// $x isa movie;
// get $x;
// group $x; count;"#;
//
//     assert_eq!(query, parse_query(query).unwrap().to_string());
// }
//
// #[test]
// fn when_parsing_incorrect_syntax_throw_typeql_syntax_exception_with_helpful_error() {
//     let parsed = parse_query("match\n$x isa");
//     assert!(parsed.is_err());
//     let report = parsed.unwrap_err().to_string();
//     assert!(report.contains("line 2"));
//     assert!(report.contains("$x isa"));
// }
//
// #[test]
// fn when_parsing_incorrect_syntax_trailing_query_whitespace_is_ignored() {
//     let parsed = parse_query("match\n$x isa \n");
//     assert!(parsed.is_err());
//     let report = parsed.unwrap_err().to_string();
//     assert!(report.contains("line 2"));
//     assert!(report.contains("$x isa"));
// }
//
// #[test]
// fn when_parsing_incorrect_syntax_error_message_should_retain_whitespace() {
//     let parsed = parse_query("match\n$x isa ");
//     assert!(parsed.is_err());
//     let report = parsed.unwrap_err().to_string();
//     assert!(!report.contains("match$xisa"));
// }
//
// #[test]
// fn test_syntax_error_pointer() {
//     let parsed = parse_query("match\n$x of");
//     assert!(parsed.is_err());
//     let report = parsed.unwrap_err().to_string();
//     assert!(report.contains("line 2"));
//     assert!(report.contains("$x of"));
// }
//
// #[test]
// fn test_has_variable() {
//     let query = r#"match
// $_ has title "Godfather",
//     has tmdb-vote-count $x;
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar(()).has(("title", "Godfather")).has(("tmdb-vote-count", cvar("x")))).get();
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_regex_attribute_type() {
//     let query = r#"match
// $x regex "(fe)male";
// get;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").regex("(fe)male")).get();
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_typeql_parsing_query() {
//     assert!(matches!(parse_query("match\n$x isa movie; get;"), Ok(Query::Get(_))));
// }
//
// #[test]
// fn test_parsing_key() {
//     let query = r#"match
// $x owns name @key;
// get $x;"#;
//
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").owns(("name", Key))).get_fixed([cvar("x")]);
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn test_parsing_empty_string() {
//     assert!(parse_query("").is_err());
// }
//
// #[test]
// fn test_parsing_list_one_match() {
//     let queries = "match $y isa movie;
// get;";
//     let parsed = parse_queries(queries).unwrap().map(|q| q.unwrap().into_get()).collect::<Vec<_>>();
//     let expected = vec![typeql_match!(cvar("y").isa("movie")).get()];
//     assert_eq!(parsed, expected);
// }
//
// #[test]
// fn test_parsing_list_one_insert() {
//     let queries = "insert $x isa movie;";
//     let parsed = parse_queries(queries).unwrap().map(|q| q.unwrap().into_insert()).collect::<Vec<_>>();
//     let expected = vec![typeql_insert!(cvar("x").isa("movie"))];
//     assert_eq!(parsed, expected);
// }
//
// #[test]
// fn test_parsing_list_one_insert_with_whitespace_prefix() {
//     let queries = " insert $x isa movie;";
//     let parsed = parse_queries(queries).unwrap().map(|q| q.unwrap().into_insert()).collect::<Vec<_>>();
//     let expected = vec![typeql_insert!(cvar("x").isa("movie"))];
//     assert_eq!(parsed, expected);
// }
//
// #[test]
// fn test_parsing_list_one_insert_with_prefix_comment() {
//     let queries = r#"#hola
// insert $x isa movie;"#;
//     let parsed = parse_queries(queries).unwrap().map(|q| q.unwrap().into_insert()).collect::<Vec<_>>();
//     let expected = vec![typeql_insert!(cvar("x").isa("movie"))];
//     assert_eq!(parsed, expected);
// }
//
// #[test]
// fn test_parsing_list() {
//     let queries = "insert $x isa movie; match $y isa movie; get;";
//     let parsed = parse_queries(queries).unwrap().collect::<Result<Vec<_>, _>>().unwrap();
//     let expected =
//         vec![typeql_insert!(cvar("x").isa("movie")).into(), typeql_match!(cvar("y").isa("movie")).get().into()];
//     assert_eq!(parsed, expected);
// }
//
// #[test]
// fn test_parsing_many_match_insert_without_stack_overflow() {
//     let num_queries = 10_000;
//     let query = "match\n$x isa person; insert $x has name 'bob';";
//     let queries = query.repeat(num_queries);
//
//     let mut parsed = Vec::with_capacity(num_queries);
//     parsed.extend(parse_queries(&queries).unwrap().map(|q| q.unwrap().into_insert()));
//
//     let expected = typeql_match!(cvar("x").isa("person")).insert(cvar("x").has(("name", "bob")));
//
//     assert_eq!(vec![expected; num_queries], parsed);
// }
//
// #[test]
// fn when_parsing_list_of_queries_with_syntax_error_report_error() {
//     let query_text = "define\nperson sub entity has name;"; // note no comma
//     let parsed = parse_query(query_text);
//     assert!(parsed.is_err());
//     assert!(parsed.unwrap_err().to_string().contains("person sub entity has name;"));
// }
//
// #[test]
// fn when_parsing_multiple_queries_like_one_throw() {
//     assert!(parse_query("insert\n$x isa movie; insert $y isa movie").is_err());
// }
//
// #[test]
// fn test_missing_colon() {
//     assert!(parse_query("match\n(actor $x, $y) isa has-cast;").is_err());
// }
//
// #[test]
// fn test_missing_comma() {
//     assert!(parse_query("match\n($x $y) isa has-cast;").is_err());
// }
//
// #[test]
// fn test_limit_mistake() {
//     let parsed = parse_query("match\n($x, $y); limit1;");
//     assert!(parsed.is_err());
//     let report = parsed.unwrap_err().to_string();
//     assert!(report.contains("limit1"));
// }
//
// #[test]
// fn when_parsing_aggregate_with_wrong_variable_argument_number_throw() {
//     assert!(parse_query(
//         "match\n$x isa name;
// get; group;"
//     )
//     .is_err());
// }
//
// #[test]
// fn when_parsing_aggregate_with_wrong_name_throw() {
//     assert!(parse_query(
//         "match\n$x isa name;
// get; hello $x;"
//     )
//     .is_err());
// }
//
// #[test]
// fn define_attribute_type_regex() {
//     let query = r#"define
// digit sub attribute,
//     regex "\d";"#;
//
//     let parsed = parse_query(query).unwrap().into_define();
//     let expected = typeql_define!(type_("digit").sub("attribute").regex(r"\d"));
//
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn undefine_attribute_type_regex() {
//     let query = r#"undefine
// digit regex "\d";"#;
//
//     let parsed = parse_query(query).unwrap().into_undefine();
//     let expected = typeql_undefine!(type_("digit").regex(r"\d"));
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn regex_predicate_parses_character_classes_correctly() {
//     let query = r#"match
// $x like "\d";
// get;"#;
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").like("\\d")).get();
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn regex_predicate_parses_quotes_correctly() {
//     let query = r#"match
// $x like "\"";
// get;"#;
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").like("\\\"")).get();
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn regex_predicate_parses_backslashes_correctly() {
//     let query = r#"match
// $x like "\\";
// get;"#;
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").like("\\\\")).get();
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn regex_predicate_parses_newline_correctly() {
//     let query = r#"match
// $x like "\n";
// get;"#;
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").like("\\n")).get();
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn regex_predicate_parses_forward_slashes_correctly() {
//     let query = r#"match
// $x like "\/";
// get;"#;
//     let parsed = parse_query(query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").like("/")).get();
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn when_value_equality_to_string_create_valid_query_string() {
//     let expected = typeql_match!(cvar("x").eq(cvar("y"))).get();
//     let parsed = parse_query(&expected.to_string()).unwrap().into_get();
//
//     assert_eq!(expected, parsed);
// }
//
// #[test]
// fn test_iid_constraint() {
//     let iid = "0x0123456789abcdefdeadbeef";
//     let query = format!(
//         r#"match
// $x iid {iid};
// get;"#
//     );
//
//     let parsed = parse_query(&query).unwrap().into_get();
//     let expected = typeql_match!(cvar("x").iid(iid)).get();
//     assert_valid_eq_repr!(expected, parsed, query);
// }
//
// #[test]
// fn when_parsing_invalid_iid_throw() {
//     let iid = "invalid";
//     let query = format!(
//         r#"match
// $x iid {iid};
// get;"#
//     );
//
//     let parsed = parse_query(&query);
//     assert!(parsed.is_err());
// }
//
// #[test]
// fn when_building_invalid_iid_throw() {
//     let iid = "invalid";
//     let expected = typeql_match!(cvar("x").iid(iid)).get().validated();
//     assert!(expected.is_err());
// }
//
// #[test]
// fn test_utf8_variable() {
//     let var = "人";
//     let expected = typeql_match!(cvar(var).isa("person")).get().validated();
//     assert!(expected.is_ok());
// }
//
// #[test]
// fn when_using_invalid_variable_throw() {
//     let var = "_人";
//     let expected = typeql_match!(cvar(var).isa("person")).get().validated();
//     assert!(expected.is_err());
// }
//
// #[test]
// fn test_utf8_label() {
//     let label = "人";
//     let expected = typeql_match!(cvar("x").isa(label)).get().validated();
//     assert!(expected.is_ok());
// }
//
// #[test]
// fn test_utf8_value() {
//     let value = "人";
//     let expected = typeql_match!(cvar("x").isa("person").has(("name", value))).get().validated();
//     assert!(expected.is_ok());
// }
