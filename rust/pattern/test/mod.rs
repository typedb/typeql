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
    and,
    builder::cvar,
    not, or, parse_query,
    pattern::{Conjunction, Disjunction, Normalisable, ThingVariableBuilder},
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
};"#;

    let mut parsed = parse_query(query).unwrap().into_match();
    let normalised = parsed.conjunction.normalise().into_disjunction();

    assert_eq!(
        normalised,
        or!(
            and!(cvar("com").has(("name", cvar("n1"))), cvar("n1").eq("the-company"), cvar("com").isa("company"),),
            and!(cvar("com").has(("name", cvar("n2"))), cvar("n2").eq("another-company"), cvar("com").isa("company"),)
        )
        .into_disjunction()
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
};"#;

    let mut parsed = parse_query(query).unwrap().into_match();
    let normalised = parsed.conjunction.normalise().into_disjunction();

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
