/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::assert_valid_eq_repr;
use crate::parse_query;

#[test]
fn test_fetch_query() {
    let query = r#"match
$x isa movie,
    has title "Godfather",
    has release-date $d;
fetch
$d;
$d as date;
$x: name, title as t, name as "Movie name";
$x as movie: name;
$x as "Movie name": name;
label-a: {
    match
    ($d, $c) isa director;
    fetch
    $d: name, age;
};
label-b: {
    match
    ($d, $c) isa director;
    filter $d;
    count();
};"#;
    let parsed = parse_query(query).unwrap();
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
