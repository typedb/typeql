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
 */

package com.vaticle.typeql.lang.test.deployment.src.test.java;

import com.vaticle.typeql.lang.TypeQL;
import com.vaticle.typeql.lang.query.TypeQLGet;
import com.vaticle.typeql.lang.query.TypeQLQuery;
import org.junit.Test;

import static com.vaticle.typeql.lang.TypeQL.and;
import static com.vaticle.typeql.lang.TypeQL.match;
import static com.vaticle.typeql.lang.TypeQL.or;
import static com.vaticle.typeql.lang.TypeQL.rel;
import static org.junit.Assert.assertEquals;

public class MavenApplicationTest {
    private void assertQueryEquals(TypeQLQuery expected, TypeQLQuery parsed, String query) {
        assertEquals(expected, parsed);
        assertEquals(expected, TypeQL.parseQuery(parsed.toString()));
        assertEquals(query, expected.toString());
    }

    @Test
    public void testRelationQuery() {
        final String query = "match\n" +
                "$brando 'Marl B' isa name;\n" +
                "(actor: $brando, $char, production-with-cast: $prod);\n" +
                "get $char, $prod;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asMatch();

        TypeQLGet expected = match(
                TypeQL.cVar("brando").eq("Marl B").isa("name"),
                rel("actor", TypeQL.cVar("brando")).rel(TypeQL.cVar("char")).rel("production-with-cast", TypeQL.cVar("prod"))
        ).get(TypeQL.cVar("char"), TypeQL.cVar("prod"));

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testPredicateQuery1() {
        final String query = "match\n" +
                "$x isa movie,\n" +
                "    has title $t;\n" +
                "{\n" +
                "    $t 'Apocalypse Now';\n" +
                "} or {\n" +
                "    $t < 'Juno';\n" +
                "    $t > 'Godfather';\n" +
                "} or {\n" +
                "    $t 'Spy';\n" +
                "};\n" +
                "$t != 'Apocalypse Now';";
        TypeQLGet parsed = TypeQL.parseQuery(query).asMatch();

        TypeQLGet expected = match(
                TypeQL.cVar("x").isa("movie").has("title", TypeQL.cVar("t")),
                or(
                        TypeQL.cVar("t").eq("Apocalypse Now"),
                        and(
                                TypeQL.cVar("t").lt("Juno"),
                                TypeQL.cVar("t").gt("Godfather")
                        ),
                        TypeQL.cVar("t").eq("Spy")
                ),
                TypeQL.cVar("t").neq("Apocalypse Now")
        );

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }
}
