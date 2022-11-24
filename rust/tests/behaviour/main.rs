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

use cucumber::{gherkin::Step, given, when, then, World};

use typeql_lang::parse_query;

#[derive(Debug, Default, World)]
pub struct TypeQLWorld;

fn main() {
    futures::executor::block_on(TypeQLWorld::cucumber().filter_run(
        "../vaticle_typedb_behaviour", |_, _, sc| !sc.tags.iter().any(|t| t == "ignore")
    ));
}

#[given("reasoning schema")]
#[given("typeql define")]
#[then("typeql define")]
#[when("typeql define")]
async fn typeql_define(_: &mut TypeQLWorld, step: &Step) {
    let parsed = parse_query(&step.docstring.as_ref().unwrap()).unwrap();
    assert_eq!(parsed, parse_query(&parsed.to_string()).unwrap());
}

#[given("typeql undefine")]
#[when("typeql undefine")]
async fn typeql_undefine(_: &mut TypeQLWorld, step: &Step) {
    let parsed = parse_query(&step.docstring.as_ref().unwrap()).unwrap();
    assert_eq!(parsed, parse_query(&parsed.to_string()).unwrap());
}

#[given("reasoning data")]
#[given("typeql insert")]
#[then("typeql insert")]
#[when("get answers of typeql insert")]
#[when("typeql insert")]
async fn typeql_insert(_: &mut TypeQLWorld, step: &Step) {
    let parsed = parse_query(&step.docstring.as_ref().unwrap()).unwrap();
    assert_eq!(parsed, parse_query(&parsed.to_string()).unwrap());
}

#[given("typeql delete")]
#[then("typeql delete")]
#[when("typeql delete")]
async fn typeql_delete(_: &mut TypeQLWorld, step: &Step) {
    let parsed = parse_query(&step.docstring.as_ref().unwrap()).unwrap();
    assert_eq!(parsed, parse_query(&parsed.to_string()).unwrap());
}

#[when("typeql update")]
async fn typeql_update(_: &mut TypeQLWorld, step: &Step) {
    let parsed = parse_query(&step.docstring.as_ref().unwrap()).unwrap();
    assert_eq!(parsed, parse_query(&parsed.to_string()).unwrap());
}

#[given("get answers of typeql insert")]
#[given("get answers of typeql match")]
#[given("reasoning query")]
#[then("get answer of typeql match aggregate")]
#[then("get answers of typeql match group aggregate")]
#[then("get answers of typeql match")]
#[then("verify answer set is equivalent for query")]
#[when("get answer of typeql match aggregate")]
#[when("get answers of typeql match group aggregate")]
#[when("get answers of typeql match group")]
#[when("get answers of typeql match")]
async fn typeql_match(_: &mut TypeQLWorld, step: &Step) {
    let parsed = parse_query(&step.docstring.as_ref().unwrap()).unwrap();
    assert_eq!(parsed, parse_query(&parsed.to_string()).unwrap());
}

#[given("connection close all sessions")]
#[given("connection does not have any database")]
#[given("connection has been opened")]
#[given("transaction commits")]
#[given("transaction is initialised")]
#[given("typeql define; throws exception")]
#[given("typeql insert; throws exception")]
#[given("uniquely identify answer concepts")]
#[given("verifier is initialised")]
#[given(regex = "connection create database: .*")]
#[given(regex = r"^answer size is: .*$")]
#[given(regex = r"^connection open data session for database: .*$")]
#[given(regex = r"^connection open schema session for database: .*$")]
#[given(regex = r"^session opens transaction of type: .*$")]
#[then("aggregate answer is not a number")]
#[then("answer groups are")]
#[then("answers contain explanation tree")]
#[then("concept identifiers are")]
#[then("each answer satisfies")]
#[then("group aggregate values are")]
#[then("order of answer concepts is")]
#[then("rules are")]
#[then("session opens transaction of type: read")]
#[then("session opens transaction of type: write")]
#[then("session transaction closes")]
#[then("session transaction is open: false")]
#[then("templated typeql match; throws exception")]
#[then("transaction commits")]
#[then("transaction commits; throws exception")]
#[then("typeql define; throws exception")]
#[then("typeql delete; throws exception")]
#[then("typeql insert; throws exception")]
#[then("typeql match aggregate; throws exception")]
#[then("typeql match group; throws exception")]
#[then("typeql match; throws exception")]
#[then("typeql undefine; throws exception")]
#[then("typeql update; throws exception")]
#[then("uniquely identify answer concepts")]
#[then("verify answers are complete")]
#[then("verify answers are sound")]
#[then(regex = r"^aggregate value is: .*$")]
#[then(regex = r"^answer size is: .*$")]
#[then(regex = r"^rules contain: .*$")]
#[then(regex = r"^rules do not contain: .*$")]
#[then(regex = r"^verify answer size is: .*$")]
#[then(regex = r"^verify answers are consistent across .* executions$")]
#[when("connection close all sessions")]
#[when("session opens transaction of type: read")]
#[when("session opens transaction of type: write")]
#[when("typeql define; throws exception")]
#[when("typeql delete; throws exception")]
#[when("typeql insert; throws exception")]
#[when("typeql match; throws exception")]
#[when("typeql undefine; throws exception")]
#[when("typeql update; throws exception")]
#[when(regex = r"^connection open data session for database: .*$")]
#[when(regex = r"^connection open schema session for database: .*$")]
async fn do_nothing(_: &mut TypeQLWorld) {}
