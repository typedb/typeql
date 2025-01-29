/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::process::exit;

use super::assert_valid_eq_repr;
use crate::parse_query;

#[test]
fn test_fetch_query() {
    let query = r#"match
$x isa person,
    has $a;
$a isa age;
$a == 10;
fetch {
    "single attr": $a,
    "single-card attributes": $x.age,
    "single value expression": $a + 1,
    "single answer block": (
        match
        $x has name $name;
        return first $name;
    ),
    "reduce answer block": (
        match
        $x has name $name;
        return count($name);
    ),
    "list positional return block": [
        match
        $x has name $n,
            has age $a;
        return { $n, $a };
    ],
    "list pipeline": [
        match
        $x has name $n,
            has age $a;
        fetch {
            "name": $n
        };
    ],
    "list higher-card attributes": [ $x.name ],
    "list attributes": $x.name[],
    "all attributes": { $x.* }
};"#;
    // TODO: Include list expression function
    // # "list expression function": [ all_names($x) ],
    let parsed = parse_query(query);
    let parsed = match parsed {
        Ok(parsed) => parsed,
        Err(err) => {
            println!("{}", err);
            exit(1)
        }
    };
    // let projections: Vec<Projection> = vec![
    // var("d").into(),
    // var("d").label("date").into(),
    // var("x").map_attributes(vec!["name".into(), ("title", "t").into(), ("name", "Movie name").into()]),
    // var("x").label("movie").map_attribute("name"),
    // var("x").label("Movie name").map_attribute("name"),
    // label("label-a").map_subquery_fetch(
    // typeql_match!(rel(var("d")).links(cvar("c")).isa("director"))
    // .fetch(vec![var("d").map_attributes(vec!["name".into(), "age".into()])]),
    // ),
    // label("label-b").map_subquery_get_aggregate(
    // typeql_match!(rel(var("d")).links(cvar("c")).isa("director")).get_fixed([cvar("d")]).count(),
    // ),
    // ];
    // let expected = typeql_match!(var("x").isa("movie").has(("title", "Godfather")).has(("release-date", cvar("d"))))
    // .fetch(projections);
    assert_valid_eq_repr!(expected, parsed, query);
}
