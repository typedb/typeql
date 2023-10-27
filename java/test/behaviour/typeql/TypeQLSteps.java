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

package com.vaticle.typeql.lang.test.behaviour.typeql;

import com.vaticle.typeql.lang.TypeQL;
import com.vaticle.typeql.lang.query.TypeQLDefine;
import com.vaticle.typeql.lang.query.TypeQLDelete;
import com.vaticle.typeql.lang.query.TypeQLFetch;
import com.vaticle.typeql.lang.query.TypeQLGet;
import com.vaticle.typeql.lang.query.TypeQLInsert;
import com.vaticle.typeql.lang.query.TypeQLQuery;
import com.vaticle.typeql.lang.query.TypeQLUndefine;
import com.vaticle.typeql.lang.query.TypeQLUpdate;
import io.cucumber.java.en.Given;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TypeQLSteps {

    @Given("typeql define")
    @Given("reasoning schema")
    @Given("typeql define without commit")
    @Given("for each session, typeql define")
    public void typeql_define(String query) {
        TypeQLDefine parsed = TypeQL.parseQuery(query).asDefine();
        assertEquals(parsed, TypeQL.parseQuery(parsed.toString()));
    }

    @Given("typeql undefine")
    @Given("typeql undefine without commit")
    public void typeql_undefine(String query) {
        TypeQLUndefine parsed = TypeQL.parseQuery(query).asUndefine();
        assertEquals(parsed, TypeQL.parseQuery(parsed.toString()));
    }

    @Given("typeql insert")
    @Given("reasoning data")
    @Given("get answers of typeql insert")
    @Given("typeql insert without commit")
    @Given("for each session, typeql insert")
    public void typeql_insert(String query) {
        TypeQLInsert parsed = TypeQL.parseQuery(query).asInsert();
        assertEquals(parsed, TypeQL.parseQuery(parsed.toString()));
        parsed.match().ifPresent(match -> match.conjunction().normalise());
    }

    @Given("typeql delete")
    public void typeql_delete(String query) {
        TypeQLDelete parsed = TypeQL.parseQuery(query).asDelete();
        assertEquals(parsed, TypeQL.parseQuery(parsed.toString()));
        parsed.match().get().conjunction().normalise();
    }

    @Given("typeql update")
    public void typeql_update(String query) {
        TypeQLUpdate parsed = TypeQL.parseQuery(query).asUpdate();
        assertEquals(parsed, TypeQL.parseQuery(parsed.toString()));
        parsed.match().get().conjunction().normalise();
    }


    @Given("for typeql query")
    @Given("reasoning query")
    @Given("get answers of typeql get")
    @Given("get answers of typeql get group")
    @Given("get answer of typeql get aggregate")
    @Given("get answers of typeql get group aggregate")
    @Given("verify answer set is equivalent for query")
    public void typeql_get(String query) {
        TypeQLQuery parsed = TypeQL.parseQuery(query);
        assertEquals(parsed, TypeQL.parseQuery(parsed.toString()));
        if (parsed instanceof TypeQLGet) {
            parsed.asGet().match().conjunction().normalise();
        }
    }

    @Given("get answers of typeql fetch")
    public void typeql_fetch(String query) {
        TypeQLFetch parsed = TypeQL.parseQuery(query).asFetch();
        assertEquals(parsed, TypeQL.parseQuery(parsed.toString()));
        parsed.match().conjunction().normalise();
    }

    @Given("typeql get; throws exception")
    @Given("typeql get group; throws exception")
    @Given("typeql get aggregate; throws exception")
    @Given("typeql fetch; throws exception")
    @Given("templated typeql get; throws exception")
    @Given("typeql insert; throws exception")
    @Given("typeql delete; throws exception")
    @Given("typeql define; throws exception")
    @Given("typeql undefine; throws exception")
    @Given("typeql update; throws exception")
    public void do_nothing_with_throws(String query) {
    }

    @Given("typeql get; throws exception containing {string}")
    public void do_nothing_with_throws_exception_containing(String exception, String query) {
    }

    @Given("typedb starts")
    @Given("connection opens with default authentication")
    @Given("transaction commits")
    @Given("aggregate answer is not a number")
    @Given("connection has been opened")
    @Given("transaction is initialised")
    @Given("the integrity is validated")
    @Given("verifier is initialised")
    @Given("verify answers are complete")
    @Given("verify answers are sound")
    @Given("connection close all sessions")
    @Given("connection delete all databases")
    @Given("session transaction closes")
    @Given("for each session, transaction closes")
    @Given("transaction commits; throws exception")
    @Given("connection does not have any database")
    @Given("for each session, transaction commits")
    public void do_nothing() {
    }

    @Given("set time-zone is: {}")
    @Given("rules contain: {}")
    @Given("answer size is: {}")
    @Given("each answer satisfies")
    @Given("fetch answers are")
    @Given("aggregate value is: {}")
    @Given("number of groups is: {}")
    @Given("rules do not contain: {}")
    @Given("each answer does not satisfy")
    @Given("verify answer size is: {}")
    @Given("connection create database: {}")
    @Given("session transaction is open: {}")
    @Given("session opens transaction of type: {}")
    @Given("connection open data session for database: {}")
    @Given("connection open schema session for database: {}")
    @Given("for each session, open transactions of type: {}")
    @Given("for each session, open transactions with reasoning of type: {}")
    @Given("verify answers are consistent across {} executions")
    public void do_nothing_with_arg(String ignored) {
    }

    @Given("connection open data sessions for databases:")
    @Given("connection open schema sessions for databases:")
    public void do_nothing_with_list(List<String> ignored) {
    }

    @Given("answer groups are")
    @Given("group aggregate values are")
    @Given("order of answer concepts is")
    @Given("uniquely identify answer concepts")
    public void do_nothing_with_list_of_map(List<Map<String, String>> ignored) {
    }

    @Given("rules are")
    @Given("group identifiers are")
    @Given("concept identifiers are")
    @Given("answers contain explanation tree")
    public void do_nothing_with_map_of_map(Map<String, Map<String, String>> ignored) {
    }
}
