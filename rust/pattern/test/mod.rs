/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{
    and,
    builder::cvar,
    not, or, parse_query,
    pattern::{Disjunction, Normalisable, ThingStatementBuilder},
};

#[test]
fn disjunction() {
    let query = r#"match
$com isa company;
{
    $com has name $n1;
    $n1 "the-company";
} or {
    $com has name $n2;
    $n2 "another-company";
};
get;"#;

    let mut parsed = parse_query(query).unwrap().into_get();
    let normalised = parsed.match_clause.conjunction.normalise().into_disjunction();

    assert_eq!(
        normalised,
        or!(
            and!(cvar("com").has(("name", cvar("n1"))), cvar("n1").eq("the-company"), cvar("com").isa("company"),),
            and!(cvar("com").has(("name", cvar("n2"))), cvar("n2").eq("another-company"), cvar("com").isa("company"),)
        )
    );
}

#[test]
fn negated_disjunction() {
    let query = r#"match
$com isa company;
not {
    $com has name $n1;
    {
        $n1 "the-company";
    } or {
        $n1 "another-company";
    };
};
get;"#;

    let mut parsed = parse_query(query).unwrap().into_get();
    let normalised = parsed.match_clause.conjunction.normalise().into_disjunction();

    assert_eq!(
        normalised,
        Disjunction::new(vec![and!(
            cvar("com").isa("company"),
            not(or!(
                and!(cvar("n1").eq("the-company"), cvar("com").has(("name", cvar("n1")))),
                and!(cvar("n1").eq("another-company"), cvar("com").has(("name", cvar("n1")))),
            ))
        )
        .into()])
    );
}
