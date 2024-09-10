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
fetch {
    "d_only": $d,
    "d optional attr": $d.title,
    "d many attr": [ $d.name ],
    "d everything": { $d.* },
    "d object": {
        "entry single 1": $x.name,
        "entry single 2": $d + 10,
        "entry single 3":
            match
            $x has name $n;
            reduce count($n);,
        "entry object": {
            "all": { $x.* }
        },
        "entry list": [
            match
            ($x, $y) isa bla;
            fetch {
                "y values": { $y.* }
            }
        ]
    }
}"#;
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
