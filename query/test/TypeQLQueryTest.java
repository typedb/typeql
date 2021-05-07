/*
 * Copyright (C) 2021 Vaticle
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
 */

package com.vaticle.typeql.lang.query.test;

import com.vaticle.typeql.lang.TypeQL;
import com.vaticle.typeql.lang.common.TypeQLArg;
import com.vaticle.typeql.lang.query.TypeQLCompute;
import com.vaticle.typeql.lang.query.TypeQLDefine;
import com.vaticle.typeql.lang.query.TypeQLInsert;
import com.vaticle.typeql.lang.query.TypeQLMatch;
import com.vaticle.typeql.lang.query.TypeQLQuery;
import org.junit.Test;

import static com.vaticle.typeql.lang.TypeQL.and;
import static com.vaticle.typeql.lang.TypeQL.lte;
import static com.vaticle.typeql.lang.TypeQL.match;
import static com.vaticle.typeql.lang.TypeQL.or;
import static com.vaticle.typeql.lang.TypeQL.rel;
import static com.vaticle.typeql.lang.TypeQL.rule;
import static com.vaticle.typeql.lang.TypeQL.type;
import static com.vaticle.typeql.lang.TypeQL.var;
import static com.vaticle.typeql.lang.common.TypeQLArg.Algorithm.CONNECTED_COMPONENT;
import static com.vaticle.typeql.lang.common.TypeQLArg.Algorithm.DEGREE;
import static com.vaticle.typeql.lang.common.TypeQLArg.Algorithm.K_CORE;
import static com.vaticle.typeql.lang.query.TypeQLCompute.Argument.k;
import static com.vaticle.typeql.lang.query.TypeQLCompute.Argument.minK;
import static com.vaticle.typeql.lang.query.TypeQLCompute.Argument.size;
import static org.junit.Assert.assertEquals;

// TODO: This test should be split into one TypeQL query test class each
public class TypeQLQueryTest {

    @Test
    public void testSimpleGetQueryToString() {
        assertSameStringRepresentation(match(var("x").isa("movie").has("title", "Godfather")));
    }

    @Test
    public void testComplexQueryToString() {
        TypeQLMatch query = match(
                var("x").isa("movie"),
                var().rel("x").rel("y"),
                or(
                        var("y").isa("person"),
                        and(
                                var("y").neq("crime"),
                                var("y").neq("book")
                        )
                ),
                var("y").has("name", var("n"))
        ).get("x", "y", "n").sort("n").offset(4).limit(8);

        assertEquivalent(query, query.toString());
    }

    @Test
    public void testQueryWithResourcesToString() {
        assertSameStringRepresentation(match(var("x").has("tmdb-vote-count", lte(400))));
    }

    @Test
    public void testQueryWithSubToString() {
        assertSameStringRepresentation(match(var("x").sub(var("y"))));
    }

    @Test
    public void testQueryWithPlaysToString() {
        assertSameStringRepresentation(match(var("x").plays(var("y"))));
    }

    @Test
    public void testQueryWithRelatesToString() {
        assertSameStringRepresentation(match(var("x").relates(var("y"))));
    }

    @Test
    public void testQueryWithValueTypeToString() {
        assertSameStringRepresentation(match(var("x").value(TypeQLArg.ValueType.LONG)));
    }

    @Test
    public void testQueryIsAbstractToString() {
        assertSameStringRepresentation(match(var("x").isAbstract()));
    }

    @Test
    public void testQueryWithRuleThenToString() {
        TypeQLDefine query = TypeQL.define(rule("a-rule").when(and(TypeQL.parsePatterns("$x isa movie;"))).then(TypeQL.parseVariable("$x has name 'Ghostbusters'").asThing()));
        assertValidToString(query);
    }

    private void assertValidToString(TypeQLQuery query) {
        //No need to execute the insert query
        TypeQLQuery parsedQuery = TypeQL.parseQuery(query.toString());
        assertEquals(query.toString(), parsedQuery.toString());
    }

    @Test
    public void testInsertQueryToString() {
        assertEquals("insert $x isa movie;", TypeQL.insert(var("x").isa("movie")).toString());
    }

    @Test
    public void testEscapeStrings() {
        assertEquals("insert $x \"hello\nworld\";", TypeQL.insert(var("x").eq("hello\nworld")).toString());
        assertEquals("insert $x \"hello\\nworld\";", TypeQL.insert(var("x").eq("hello\\nworld")).toString());
    }

    @Test
    public void testOwns() {
        assertEquals("define person owns thingy;", TypeQL.define(type("person").owns("thingy")).toString());
    }

    @Test
    public void testComputeQueryToString() {
        assertEquals("compute count;", TypeQL.compute().count().toString());
    }

    @Test
    public void testComputeQuerySubgraphToString() {
        TypeQLCompute query = TypeQL.compute().centrality().using(DEGREE).in("movie", "person");
        assertEquivalent(query, "compute centrality in [movie, person], using degree;");
    }

    @Test
    public void testClusterToString() {
        TypeQLCompute connectedcomponent = TypeQL.compute().cluster().using(CONNECTED_COMPONENT).in("movie", "person");
        assertEquivalent(connectedcomponent, "compute cluster in [movie, person], using connected-component;");

        TypeQLCompute kcore = TypeQL.compute().cluster().using(K_CORE).in("movie", "person");
        assertEquivalent(kcore, "compute cluster in [movie, person], using k-core;");
    }

    @Test
    public void testCCSizeToString() {
        TypeQLCompute query = TypeQL.compute().cluster().using(CONNECTED_COMPONENT).in("movie", "person").where(size(10));
        assertEquivalent(query, "compute cluster in [movie, person], using connected-component, where size=10;");
    }

    @Test
    public void testKCoreToString() {
        TypeQLCompute query = TypeQL.compute().cluster().using(K_CORE).in("movie", "person").where(k(10));
        assertEquivalent(query, "compute cluster in [movie, person], using k-core, where k=10;");
    }

    @Test
    public void testCentralityOf() {
        TypeQLCompute query = TypeQL.compute().centrality().using(DEGREE).in("movie", "person").of("person");
        assertEquivalent(query, "compute centrality of person, in [movie, person], using degree;");

        query = TypeQL.compute().centrality().using(K_CORE).in("movie", "person").of("person").where(minK(5));
        assertEquivalent(query, "compute centrality of person, in [movie, person], using k-core, where min-k=5;");
    }

    @Test
    public void testRepeatRoleplayerToString() {
        assertEquals("match ($x, $x);", match(rel("x").rel("x")).toString());
    }

    @Test
    public void testMatchInsertToString() {
        TypeQLInsert query = match(var("x").isa("movie")).insert(var("x").has("title", "hello"));
        assertEquals("match $x isa movie;\ninsert $x has title \"hello\";", query.toString());
    }

    @Test
    public void testZeroToString() {
        assertEquals("match $x 0.0;", match(var("x").eq(0.0)).toString());
    }

    @Test
    public void testExponentsToString() {
        assertEquals("match $x 1000000000.0;", match(var("x").eq(1_000_000_000.0)).toString());
    }

    @Test
    public void testDecimalToString() {
        assertEquals("match $x 0.0001;", match(var("x").eq(0.0001)).toString());
    }

    @Test
    public void whenCallingToStringOnDeleteQuery_ItLooksLikeOriginalQuery() {
        final String query = "match $x isa movie;\n" +
                "delete $x isa movie;";
        assertEquals(query, TypeQL.parseQuery(query).toString());
    }

    private void assertSameStringRepresentation(TypeQLMatch query) {
        assertEquals(query.toString(), TypeQL.parseQuery(query.toString()).toString());
    }

    private void assertEquivalent(TypeQLQuery query, String queryString) {
        assertEquals(queryString, query.toString());
        assertEquals(query.toString(), TypeQL.parseQuery(queryString).toString());
    }
}