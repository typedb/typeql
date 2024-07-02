/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

mod autogenerated;
mod match_queries;
mod schema_queries;

use crate::parse_query;

macro_rules! assert_valid_eq_repr {
    ($expected:ident, $parsed:ident, $query:ident) => {
        let parsed = $parsed;
        // let expected = $expected;
        let query = $query;
        // assert_eq!(format!("{:#}", expected), query, "\n{expected:#}\n\n{query}\n");
        assert_eq!(format!("{:#}", parsed), query, "\n{parsed:#}\n\n{query}\n");
        // assert_eq!(parsed, expected.into());
    };
}
use assert_valid_eq_repr;

// #[test]
fn tmp() {
    let query_string = r#"
    define
       name sub attribute, value string @regex("^(foo|bar)$") @values("foo", "bar");
       person sub entity;
       person owns name[] @card(0, *);
    "#;
    let result = parse_query(query_string);
    eprintln!("{result:#?}");
    eprintln!("{:#}", result.unwrap());
    panic!();

    // let query_fn = r#"
    // define
    // fun test_stream_1($x: person) -> {name}:
    //   match
    //    $x isa person, has name $name;
    //   return {$x};

    // fun test_stream_many($x: person) -> {name, age, dob}:
    //   match
    //     $x isa person, has name $name, has age $age, has dob $dob;
    //   filter $name, $age, $dob;
    //   sort $name;
    //   offset 10;
    //   limit 10;
    //   return { $name, $age, $dob };

    // fun test_single_1($x: person) -> long:
    //   match
    //     $x isa person;
    //   return count($x);

    // fun test_single_many($x: person) -> long, long:
    //   match
    //     $x isa person, has age $a;
    //   return count($x), sum($a);

    // fun test_stream_optional($x: person) -> { name, age?, dob }:
    //   match
    //     $x isa person, has name $name;
    //     try { $x has age $age; };
    //     $y in get_all_dob($x);
    //   return { $x, $age, $y };

    // fun test_single_optional($x: person) -> name?, long, double?:
    //   match
    //     $x isa person, has age $age;
    //     try { $one_name = get_a_name($x); };
    //   return $one_name, count($x), std($age);
    //  "#;
    // let result = TypeQLParser::parse(Rule::eof_query, query_fn);
    // dbg!("{}", &result);

    // let query_list_card = r#"
    // match
    // $x is $y;

    // $x > $y;
    // $x like "abc";
    // $x like $y;
    // $x == $y;

    // $x = $y;
    // $x = 10 + 11;

    // person sub attribute @abstract,
    //   value long @values(1,2,3);

    // $person sub attribute @abstract;
    // $person sub $parent, value string @regex("abc");

    // $person type person;

    // $x sub entity,
    //     owns age as abstract_age @card(0,*) @key @unique,
    //     owns name[] @card(0, *) @distinct,
    //     owns $attr[];

    // $x sub relation,
    //     relates friend @card(10, 100) @cascade,
    //     relates best-friend[] @distinct,
    //     relates $role[];
    // get;
    // "#;
    // let result = TypeQLParser::parse(Rule::eof_query, query_list_card);
    // dbg!("{}", &result);
}

// #[test]
// fn when_parsing_date_error_when_handling_overly_precise_nanos() {
//     let validated = typeql_match!(var("x").has((
//         "release-date",
//         NaiveDateTime::new(
//             NaiveDate::from_ymd_opt(1000, 11, 12).unwrap(),
//             NaiveTime::from_hms_nano_opt(13, 14, 15, 123450000).unwrap(),
//         ),
//     )))
//     ;
//     assert!(validated.is_err());
//     assert!(validated.unwrap_err().to_string().contains("more precise than 1 millisecond"));
// }

// #[test]
// fn test_builder_precedence_operators() {
//     let query = r#"match
// $a = ($b + $c) * $d;"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(var("a").assign(cvar("b").add(cvar("c")).multiply(cvar("d"))));
//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_builder_associativity_left() {
//     let query = r#"match
// $a = $b - ($c - $d);"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(var("a").assign(cvar("b").subtract(cvar("c").subtract(cvar("d")))));
//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_builder_associativity_right() {
//     let query = r#"match
// $a = ($b ^ $c) ^ $d;"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(var("a").assign(cvar("b").power(cvar("c")).power(cvar("d"))));
//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_parenthesis_preserving() {
//     let query = r#"match
// $a = $b + ($c + $d) + $e * ($f * $g);"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(var("a")
//         .assign(var("b").add(cvar("c").add(cvar("d"))).add(cvar("e").multiply(cvar("f").multiply(cvar("g"))))))
//     ;
//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_parenthesis_not_adding_unnecessary() {
//     let query = r#"match
// $a = $b + $c + $d + $e * $f * $g;"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(var("a")
//         .assign(var("b").add(cvar("c")).add(cvar("d")).add(cvar("e").multiply(cvar("f")).multiply(cvar("g")))))
//     ;
//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_function_min() {
//     let query = r#"match
// $x isa commodity,
//     has price $p;
// (commodity: $x, qty: $q) isa order;
// $net = $p * $q;
// $gross = min($net * 1.21, $net + 100.0);"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(
//         var("x").isa("commodity").has(("price", cvar("p"))),
//         rel(("commodity", "x")).links(("qty", "q")).isa("order"),
//         var("net").assign(cvar("p").multiply(cvar("q"))),
//         var("gross").assign(min!(vvar("net").multiply(1.21), vvar("net").add(100.0)))
//     )
//     ;

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_function_max() {
//     let query = r#"match
// $x isa commodity,
//     has price $p;
// (commodity: $x, qty: $q) isa order;
// $net = $p * $q;
// $gross = max($net * 1.21, $net + 100.0);"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(
//         var("x").isa("commodity").has(("price", cvar("p"))),
//         rel(("commodity", "x")).links(("qty", "q")).isa("order"),
//         var("net").assign(cvar("p").multiply(cvar("q"))),
//         var("gross").assign(max!(vvar("net").multiply(1.21), vvar("net").add(100.0)))
//     )
//     ;

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_function_abs() {
//     let query = r#"match
// $x isa commodity,
//     has price $p;
// (commodity: $x, qty: $q) isa order;
// $net = $p * $q;
// $value = abs($net * 1.21);"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(
//         var("x").isa("commodity").has(("price", cvar("p"))),
//         rel(("commodity", "x")).links(("qty", "q")).isa("order"),
//         var("net").assign(cvar("p").multiply(cvar("q"))),
//         var("value").assign(abs(vvar("net").multiply(1.21)))
//     )
//     ;

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_function_ceil() {
//     let query = r#"match
// $x isa commodity,
//     has price $p;
// (commodity: $x, qty: $q) isa order;
// $net = $p * $q;
// $value = ceil($net * 1.21);"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(
//         var("x").isa("commodity").has(("price", cvar("p"))),
//         rel(("commodity", "x")).links(("qty", "q")).isa("order"),
//         var("net").assign(cvar("p").multiply(cvar("q"))),
//         var("value").assign(ceil(vvar("net").multiply(1.21)))
//     )
//     ;

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_function_floor() {
//     let query = r#"match
// $x isa commodity,
//     has price $p;
// (commodity: $x, qty: $q) isa order;
// $net = $p * $q;
// $value = floor($net * 1.21);"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(
//         var("x").isa("commodity").has(("price", cvar("p"))),
//         rel(("commodity", "x")).links(("qty", "q")).isa("order"),
//         var("net").assign(cvar("p").multiply(cvar("q"))),
//         var("value").assign(floor(vvar("net").multiply(1.21)))
//     )
//     ;

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_function_round() {
//     let query = r#"match
// $x isa commodity,
//     has price $p;
// (commodity: $x, qty: $q) isa order;
// $net = $p * $q;
// $value = round($net * 1.21);"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(
//         var("x").isa("commodity").has(("price", cvar("p"))),
//         rel(("commodity", "x")).links(("qty", "q")).isa("order"),
//         var("net").assign(cvar("p").multiply(cvar("q"))),
//         var("value").assign(round(vvar("net").multiply(1.21)))
//     )
//     ;

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_schema_query() {
//     let query = r#"match
// $x plays starring:actor;
// sort $x asc;"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(var("x").plays(("starring", "actor"))).sort([(cvar("x"), Asc)]);

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_get_sort_on_concept_variable() {
//     let query = r#"match
// $x isa movie,
//     has rating $r;
// sort $r desc;"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(var("x").isa("movie").has(("rating", cvar("r")))).sort([(cvar("r"), Desc)]);

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_get_sort_on_value_variable() {
//     let query = r#"match
// $x isa movie,
//     has rating $r;
// $l = 100 - $r;
// sort $l desc;"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(
//         var("x").isa("movie").has(("rating", cvar("r"))),
//         var("l").assign(constant(100).subtract(cvar("r")))
//     )
//
//     .sort([(var("l"), Desc)]);

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_get_sort_multiple() {
//     let query = r#"match
// $x isa movie,
//     has title $t,
//     has rating $r;
// $rate = $r * 100;
// sort $rate desc, $t;"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(
//         var("x").isa("movie").has(("title", cvar("t"))).has(("rating", cvar("r"))),
//         var("rate").assign(cvar("r").multiply(100)),
//     )
//
//     .sort(sort_vars!((var("rate"), Desc), cvar("t")));

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_get_sort_limit() {
//     let query = r#"match
// $x isa movie,
//     has rating $r;
// sort $r; limit 10;"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(var("x").isa("movie").has(("rating", cvar("r")))).sort([cvar("r")]).limit(10);

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_get_sort_offset_limit() {
//     let query = r#"match
// $x isa movie,
//     has rating $r;
// sort $r desc; offset 10; limit 10;"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(var("x").isa("movie").has(("rating", cvar("r"))))
//
//         .sort([(var("r"), Desc)])
//         .offset(10)
//         .limit(10);

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_get_offset_limit() {
//     let query = r#"match
// $y isa movie,
//     has title $n;
// offset 2; limit 4;"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(var("y").isa("movie").has(("title", cvar("n")))).offset(2).limit(4);

//     assert_valid_eq_repr!(expected, parsed, query);
// }

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

//     let projections: Vec<Projection> = vec![
//         var("d").into(),
//         var("d").label("date").into(),
//         var("x").map_attributes(vec!["name".into(), ("title", "t").into(), ("name", "Movie name").into()]),
//         var("x").label("movie").map_attribute("name"),
//         var("x").label("Movie name").map_attribute("name"),
//         label("label-a").map_subquery_fetch(
//             typeql_match!(rel(var("d")).links(cvar("c")).isa("director"))
//                 .fetch(vec![var("d").map_attributes(vec!["name".into(), "age".into()])]),
//         ),
//         label("label-b").map_subquery_get_aggregate(
//             typeql_match!(rel(var("d")).links(cvar("c")).isa("director")).get_fixed([cvar("d")]).count(),
//         ),
//     ];
//     let expected = typeql_match!(var("x").isa("movie").has(("title", "Godfather")).has(("release-date", cvar("d"))))
//         .fetch(projections);

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_variables_everywhere_query() {
//     let query = r#"match
// ($p: $x, $y);
// $x isa $z;
// $y "crime";
// $z sub production;
// has-genre relates $p;"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(
//         rel((var("p"), cvar("x"))).links("y"),
//         var("x").isa(cvar("z")),
//         var("y").eq("crime"),
//         var("z").sub("production"),
//         type_("has-genre").relates(var("p")),
//     )
//     ;

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_parsing_relates_type_variable() {
//     let query = r#"match
// $x isa $type;
// $type relates someRole;"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(var("x").isa(cvar("type")), cvar("type").relates("someRole"));

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_or_query() {
//     let query = r#"match
// $x isa movie;
// {
//     $y "drama" isa genre;
//     ($x, $y);
// } or {
//     $x "The Muppets";
// };"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(
//         var("x").isa("movie"),
//         or!(and!(var("y").eq("drama").isa("genre"), rel("x").links("y")), cvar("x").eq("The Muppets"))
//     )
//     ;

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_disjunction_not_in_conjunction() {
//     let query = r#"match
// {
//     $x isa person;
// } or {
//     $x isa company;
// };"#;
//     let res = parse_query(query);
//     assert!(res.is_err(), "{res:?}")
// }

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
// };"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(
//         var("y").isa(cvar("p")),
//         or!(
//             rel("y").links("q"),
//             and!(
//                 var("x").isa(cvar("p")),
//                 or!(var("x").has(("first-name", cvar("y"))), cvar("x").has(("last-name", cvar("z"))))
//             )
//         )
//     )
//     ;

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_disjunction_not_binding_conjunction() {
//     let query = r#"match
// $y isa $p;
// { ($y, $q); } or { $x isa $p; { $x has first-name $y; } or { $q has last-name $z; }; };"#;
//     let parsed = parse_query(query);
//     assert!(parsed.is_err());
// }

// #[test]
// fn test_aggregate_count_query() {
//     let query = r#"match
// ($x, $y) isa friendship;
// get $x, $y;
// count;"#;

//     let parsed = parse_query(query).unwrap().into_get_aggregate();
//     let expected = typeql_match!(rel("x").links("y").isa("friendship")).get_fixed([var("x"), cvar("y")]).count();

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_aggregate_group_count_query() {
//     let query = r#"match
// ($x, $y) isa friendship;
// get $x, $y;
// group $x; count;"#;

//     let parsed = parse_query(query).unwrap().into_get_group_aggregate();
//     let expected =
//         typeql_match!(rel("x").links("y").isa("friendship")).get_fixed([var("x"), cvar("y")]).group(cvar("x")).count();

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_single_line_group_aggregate_max_query() {
//     let query = r#"match
// $x has age $a;
// group $x; max $a;"#;

//     let parsed = parse_query(query).unwrap().into_get_group_aggregate();
//     let expected = typeql_match!(var("x").has(("age", cvar("a")))).group(cvar("x")).max(cvar("a"));

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_multi_line_group_aggregate_max_query() {
//     let query = r#"match
// ($x, $y) isa friendship;
// $y has age $z;
// group $x; max $z;"#;

//     let parsed = parse_query(query).unwrap().into_get_group_aggregate();
//     let expected = typeql_match!(rel("x").links("y").isa("friendship"), var("y").has(("age", cvar("z"))))
//
//         .group(var("x"))
//         .max(var("z"));

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_multi_line_filtered_group_aggregate_max_query() {
//     let query = r#"match
// ($x, $y) isa friendship;
// $y has age $z;
// get $x, $y, $z;
// group $x; max $z;"#;

//     let parsed = parse_query(query).unwrap().into_get_group_aggregate();
//     let expected = typeql_match!(rel("x").links("y").isa("friendship"), var("y").has(("age", cvar("z"))))
//         .get_fixed([var("x"), cvar("y"), cvar("z")])
//         .group(var("x"))
//         .max(var("z"));

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_filtered_group_aggregates_on_value_variable() {
//     let query = r#"match
// $i ($x, $s) isa income-source;
// $i has value $v,
//     has tax-rate $r;
// $t = $r * $v;
// get $x, $t;
// group $x; sum $t;"#;

//     let parsed = parse_query(query).unwrap().into_get_group_aggregate();
//     let filter: [Variable; 2] = [var("x").into(), vvar("t").into()];
//     let expected = typeql_match!(
//         var("i").links("x").links("s").isa("income-source"),
//         var("i").has(("value", cvar("v"))).has(("tax-rate", cvar("r"))),
//         var("t").assign(cvar("r").multiply(cvar("v"))),
//     )
//     .get_fixed(filter)
//     .group(var("x"))
//     .sum(var("t"));

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn when_comparing_count_query_using_typeql_and_rust_typeql_they_are_equivalent() {
//     let query = r#"match
// $x isa movie,
//     has title "Godfather";
// count;"#;

//     let parsed = parse_query(query).unwrap().into_get_aggregate();
//     let expected = typeql_match!(var("x").isa("movie").has(("title", "Godfather"))).count();

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_delete_query() {
//     let query = r#"match
// $x isa movie,
//     has title "The Title";
// $y isa movie;
// delete
// $x isa movie;
// $y isa movie;"#;

//     let parsed = parse_query(query).unwrap().into_delete();
//     let expected = typeql_match!(var("x").isa("movie").has(("title", "The Title")), cvar("y").isa("movie"))
//         .delete([var("x").isa("movie"), cvar("y").isa("movie")]);

//     assert_valid_eq_repr!(expected, parsed, query);
// }

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

//     let parsed = parse_query(query).unwrap().into_insert();
//     let expected = typeql_insert!(
//         var("x").isa("pokemon").has(("name", "Pichu")),
//         var("y").isa("pokemon").has(("name", "Pikachu")),
//         var("z").isa("pokemon").has(("name", "Raichu")),
//         rel(("evolves-from", "x")).links(("evolves-to", "y")).isa("evolution"),
//         rel(("evolves-from", "y")).links(("evolves-to", "z")).isa("evolution")
//     );

//     assert_valid_eq_repr!(expected, parsed, query);
// }

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

//     let parsed = parse_query(query).unwrap().into_update();
//     let expected = typeql_match!(var("x").isa("person").has(("name", "alice")).has(("age", cvar("a"))))
//         .delete(var("x").has(cvar("a")))
//         .insert(var("x").has(("age", 25)));

//     assert_valid_eq_repr!(expected, parsed, query);
// }

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

//     let parsed = parse_query(query).unwrap().into_schema().into_define();
//     let expected = typeql_define!(
//         type_("parent").sub("role"),
//         type_("child").sub("role"),
//         type_("parenthood").sub("relation").relates("parent").relates("child"),
//         type_("fatherhood").sub("parenthood").relates(("father", "parent")).relates(("son", "child"))
//     );

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn when_parsing_as_in_match_result_is_same_as_sub() {
//     let query = r#"match
// $f sub parenthood,
//     relates father as parent,
//     relates son as child;"#;

//     let parsed = parse_query(query).unwrap();
//     let expected =
//         typeql_match!(var("f").sub("parenthood").relates(("father", "parent")).relates(("son", "child")));

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_match_insert_query() {
//     let query = r#"match
// $x isa language;
// insert
// $x has name "HELLO";"#;

//     let parsed = parse_query(query).unwrap().into_insert();
//     let expected = typeql_match!(var("x").isa("language")).insert(cvar("x").has(("name", "HELLO")));

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_match_value_type_query() {
//     let query = r#"match
// $x value double;"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(var("x").value(ValueType::Double));

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_parsing_without_var_concept() {
//     let query = r#"match
// $_ isa person;"#;

//     let parsed = parse_query(query);
//     assert!(parsed.is_err());
//     let built = typeql_match!(var(()).isa("person"));
//     assert!(built.is_err());
// }

// #[test]
// fn when_parsing_date_keyword_parse_as_the_correct_value_type() {
//     let query = r#"match
// $x value datetime;"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(var("x").value(ValueType::DateTime));

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_escape_string() {
//     let input = r#"This has \"double quotes\" and a single-quoted backslash: '\\'"#;

//     let query = format!(
//         r#"insert
// $_ isa movie,
//     has title "{input}";"#
//     );

//     let parsed = parse_query(&query).unwrap().into_insert();
//     let expected = typeql_insert!(var(()).isa("movie").has(("title", input)));
//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn when_parsing_query_with_comments_they_are_ignored() {
//     let query = "match\n\n# there's a comment here\n$x isa###WOW HERES ANOTHER###\r\nmovie; count;";
//     let uncommented = r#"match
// $x isa movie;
// count;"#;

//     let parsed = parse_query(query).unwrap().into_get_aggregate();
//     let expected = typeql_match!(var("x").isa("movie")).count();

//     assert_valid_eq_repr!(expected, parsed, uncommented);
// }

// #[test]
// fn test_parsing_pattern() {
//     let pattern = r#"{
//     (wife: $a, husband: $b) isa marriage;
//     $a has gender "male";
//     $b has gender "female";
// }"#;

//     let parsed = parse_pattern(pattern).unwrap().into_conjunction();
//     let expected = and!(
//         rel(("wife", "a")).links(("husband", "b")).isa("marriage"),
//         var("a").has(("gender", "male")),
//         var("b").has(("gender", "female"))
//     );

//     assert_valid_eq_repr!(expected, parsed, pattern);
// }

// #[test]
// fn test_parsing_patterns() {
//     let patterns = r#"(wife: $a, husband: $b) isa marriage;
//     $a has gender "male";
//     $b has gender "female";
// "#;

//     let parsed = parse_patterns(patterns).unwrap().into_iter().map(|p| p.into_statement()).collect::<Vec<_>>();
//     let expected: Vec<Statement> = vec![
//         Statement::Thing(rel(("wife", "a")).links(("husband", "b")).isa("marriage")),
//         Statement::Thing(var("a").has(("gender", "male"))),
//         Statement::Thing(var("b").has(("gender", "female"))),
//     ];

//     assert_eq!(expected, parsed);
// }

// #[test]
// fn test_parsing_definables() {
//     let query = r#"athlete sub person;
//       runner sub athlete;
//       sprinter sub runner;"#;

//     let parsed = parse_definables(query).unwrap().into_iter().map(|p| p.into_type_statement()).collect::<Vec<_>>();
//     let expected =
//         vec![type_("athlete").sub("person"), type_("runner").sub("athlete"), type_("sprinter").sub("runner")];

//     assert_eq!(expected, parsed);
// }

// #[test]
// fn test_parsing_variable_rel() {
//     let variable = "(wife: $a, husband: $b) isa marriage";

//     let parsed = parse_statement(variable).unwrap();
//     if let Statement::Thing(parsed_var) = parsed {
//         let expected = rel(("wife", "a")).links(("husband", "b")).isa("marriage");
//         assert_valid_eq_repr!(expected, parsed_var, variable);
//     } else {
//         panic!("Expected ThingVariable, found {variable:?}.");
//     }
// }

// #[test]
// fn test_parsing_variable_has() {
//     let variable = "$x has is_interesting true";

//     let parsed = parse_statement(variable).unwrap();
//     if let Statement::Thing(parsed_var) = parsed {
//         let expected = var("x").has(("is_interesting", true));
//         assert_valid_eq_repr!(expected, parsed_var, variable);
//     } else {
//         panic!("Expected ThingVariable, found {variable:?}.");
//     }
// }

// #[test]
// fn test_parsing_label() {
//     let label = "label_with-symbols";

//     let parsed = parse_label(label).unwrap();
//     let expected = Label { scope: None, name: String::from(label) };
//     assert_eq!(expected, parsed);
// }

// #[test]
// fn test_parsing_boolean() {
//     let query = r#"insert
// $_ has flag true;"#;

//     let parsed = parse_query(query).unwrap().into_insert();
//     let expected = typeql_insert!(var(()).has(("flag", true)));
//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_parsing_aggregate_group() {
//     let query = r#"match
// $x isa movie;
// group $x;"#;

//     let parsed = parse_query(query).unwrap().into_get_group();
//     let expected = typeql_match!(var("x").isa("movie")).group(cvar("x"));

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_parsing_aggregate_group_count() {
//     let query = r#"match
// $x isa movie;
// group $x; count;"#;

//     let parsed = parse_query(query).unwrap().into_get_group_aggregate();
//     let expected = typeql_match!(var("x").isa("movie")).group(cvar("x")).count();

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_parsing_aggregate_std() {
//     let query = r#"match
// $x isa movie;
// std $x;"#;

//     let parsed = parse_query(query).unwrap().into_get_aggregate();
//     let expected = typeql_match!(var("x").isa("movie")).std(cvar("x"));

//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_parsing_aggregate_to_string() {
//     let query = r#"match
// $x isa movie;
// get $x;
// group $x; count;"#;

//     assert_eq!(query, parse_query(query).unwrap().to_string());
// }

// #[test]
// fn when_parsing_incorrect_syntax_throw_typeql_syntax_exception_with_helpful_error() {
//     let parsed = parse_query("match\n$x isa");
//     assert!(parsed.is_err());
//     let report = parsed.unwrap_err().to_string();
//     assert!(report.contains("line 2"));
//     assert!(report.contains("$x isa"));
// }

// #[test]
// fn when_parsing_incorrect_syntax_trailing_query_whitespace_is_ignored() {
//     let parsed = parse_query("match\n$x isa \n");
//     assert!(parsed.is_err());
//     let report = parsed.unwrap_err().to_string();
//     assert!(report.contains("line 2"));
//     assert!(report.contains("$x isa"));
// }

// #[test]
// fn when_parsing_incorrect_syntax_error_message_should_retain_whitespace() {
//     let parsed = parse_query("match\n$x isa ");
//     assert!(parsed.is_err());
//     let report = parsed.unwrap_err().to_string();
//     assert!(!report.contains("match$xisa"));
// }

// #[test]
// fn test_syntax_error_pointer() {
//     let parsed = parse_query("match\n$x of");
//     assert!(parsed.is_err());
//     let report = parsed.unwrap_err().to_string();
//     assert!(report.contains("line 2"));
//     assert!(report.contains("$x of"));
// }

// #[test]
// fn test_has_variable() {
//     let query = r#"match
// $_ has title "Godfather",
//     has tmdb-vote-count $x;"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(var(()).has(("title", "Godfather")).has(("tmdb-vote-count", cvar("x"))));
//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_regex_attribute_type() {
//     let query = r#"match
// $x regex "(fe)male";"#;

//     let parsed = parse_query(query).unwrap();
//     let expected = typeql_match!(var("x").regex("(fe)male"));
//     assert_valid_eq_repr!(expected, parsed, query);
// }

// #[test]
// fn test_typeql_parsing_query() {
//     assert!(matches!(parse_query("match\n$x isa movie; get;"), Ok(Query::Get(_))));
// }

// #[test]
fn test_parsing_key() {
    let query = r#"match
$x owns name @key;"#;

    let parsed = parse_query(query).unwrap();
    // let expected = typeql_match!(var("x").owns(("name", Key))).get_fixed([cvar("x")]);
    assert_valid_eq_repr!(expected, parsed, query);
}

// #[test]
fn test_parsing_empty_string() {
    assert!(parse_query("").is_err());
}

// #[test]
// fn test_parsing_list_one_match() {
// let queries = "match $y isa movie;";
// let parsed = parse_queries(queries).unwrap().collect_vec();
// let expected = vec![typeql_match!(var("y").isa("movie"))];
// assert_eq!(parsed, expected);
// }

// #[test]
// fn test_parsing_list_one_insert() {
// let queries = "insert $x isa movie;";
// let parsed = parse_queries(queries).unwrap().collect_vec();
// let expected = vec![typeql_insert!(var("x").isa("movie"))];
// assert_eq!(parsed, expected);
// }

// #[test]
// fn test_parsing_list_one_insert_with_whitespace_prefix() {
// let queries = " insert $x isa movie;";
// let parsed = parse_queries(queries).unwrap().collect_vec();
// let expected = vec![typeql_insert!(var("x").isa("movie"))];
// assert_eq!(parsed, expected);
// }

// #[test]
// fn test_parsing_list_one_insert_with_prefix_comment() {
// let queries = r#"#hola
// insert $x isa movie;"#;
// let parsed = parse_queries(queries).unwrap().collect_vec();
// let expected = vec![typeql_insert!(var("x").isa("movie"))];
// assert_eq!(parsed, expected);
// }

// #[test]
// fn test_parsing_list() {
// let queries = "insert $x isa movie; match $y isa movie;";
// let parsed = parse_queries(queries).unwrap().collect_vec();
// let expected = vec![typeql_insert!(var("x").isa("movie")).into(), typeql_match!(cvar("y").isa("movie")).into()];
// assert_eq!(parsed, expected);
// }

// #[test]
// fn test_parsing_many_match_insert_without_stack_overflow() {
//     let num_queries = 10_000;
//     let query = "match $x isa person; insert $x has name 'bob';";
//     let queries = query.repeat(num_queries);

//     let mut parsed = Vec::with_capacity(num_queries);
//     parsed.extend(parse_queries(&queries).unwrap());

//     let expected = typeql_match!(var("x").isa("person")).insert(cvar("x").has(("name", "bob")));

//     assert_eq!(vec![expected; num_queries], parsed);
// }

// #[test]
// fn when_parsing_list_of_queries_with_syntax_error_report_error() {
//     let query_text = "define\nperson sub entity has name;"; // note no comma
//     let parsed = parse_query(query_text);
//     assert!(parsed.is_err());
//     assert!(parsed.unwrap_err().to_string().contains("person sub entity has name;"));
// }

// #[test]
// fn when_parsing_multiple_queries_like_one_throw() {
//     assert!(parse_query("insert\n$x isa movie; insert $y isa movie").is_err());
// }

// #[test]
fn test_missing_colon() {
    assert!(parse_query("match\n(actor $x, $y) isa has-cast;").is_err());
}

// #[test]
fn test_missing_comma() {
    assert!(parse_query("match\n($x $y) isa has-cast;").is_err());
}

// #[test]
fn test_limit_mistake() {
    let parsed = parse_query("match\n($x, $y); limit1;");
    assert!(parsed.is_err());
    let report = parsed.unwrap_err().to_string();
    assert!(report.contains("limit1"));
}

// #[test]
// fn when_parsing_aggregate_with_wrong_variable_argument_number_throw() {
//     assert!(parse_query("match\n$x isa name; group;").is_err());
// }

// #[test]
// fn when_parsing_aggregate_with_wrong_name_throw() {
//     assert!(parse_query("match\n$x isa name; hello $x;").is_err());
// }

// #[test]
fn regex_predicate_parses_character_classes_correctly() {
    let query = r#"match
$x like "\d";"#;
    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("x").like(r"\d"));
    assert_valid_eq_repr!(expected, parsed, query);
}

// #[test]
fn regex_predicate_parses_quotes_correctly() {
    let query = r#"match
$x like "\"";"#;
    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("x").like(r#"\""#));
    assert_valid_eq_repr!(expected, parsed, query);
}

// #[test]
fn regex_predicate_parses_backslashes_correctly() {
    let query = r#"match
$x like "\\";"#;
    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("x").like(r"\\"));
    assert_valid_eq_repr!(expected, parsed, query);
}

// #[test]
fn regex_predicate_parses_newline_correctly() {
    let query = r#"match
$x like "\n";"#;
    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("x").like(r"\n"));
    assert_valid_eq_repr!(expected, parsed, query);
}

// #[test]
fn regex_predicate_parses_forward_slashes_correctly() {
    let query = r#"match
$x like "\/";"#;
    let parsed = parse_query(query).unwrap();
    // let expected = match_!(var("x").like("/"));
    assert_valid_eq_repr!(expected, parsed, query);
}

// #[test]
// fn when_value_equality_to_string_create_valid_query_string() {
//     let expected = typeql_match!(var("x").eq(cvar("y")));
//     let parsed = parse_query(&expected.to_string()).unwrap();
//     assert_eq!(expected, parsed);
// }

// #[test]
fn test_iid_constraint() {
    let iid = "0x0123456789abcdefdeadbeef";
    let query = format!(
        r#"match
$x iid {iid};"#
    );

    let parsed = parse_query(&query).unwrap();
    // let expected = match_!(var("x").iid(iid));
    assert_valid_eq_repr!(expected, parsed, query);
}

// #[test]
fn when_parsing_invalid_iid_throw() {
    let iid = "invalid";
    let query = format!(
        r#"match
$x iid {iid};"#
    );

    let parsed = parse_query(&query);
    assert!(parsed.is_err());
}

// #[test]
// fn when_building_invalid_iid_throw() {
//     let iid = "invalid";
//     let expected = typeql_match!(var("x").iid(iid));
//     assert!(expected.is_err());
// }

// #[test]
// fn test_utf8_variable() {
//     let var = "人";
//     let expected = typeql_match!(var(var).isa("person"));
//     assert!(expected.is_ok());
// }

// #[test]
// fn when_using_invalid_variable_throw() {
//     let var = "_人";
//     let expected = typeql_match!(var(var).isa("person"));
//     assert!(expected.is_err());
// }

// #[test]
// fn test_utf8_label() {
//     let label = "人";
//     let expected = typeql_match!(var("x").isa(label));
//     assert!(expected.is_ok());
// }

// #[test]
// fn test_utf8_value() {
//     let value = "人";
//     let expected = typeql_match!(var("x").isa("person").has(("name", value)));
//     assert!(expected.is_ok());
// }
