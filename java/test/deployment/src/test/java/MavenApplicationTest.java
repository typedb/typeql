/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();

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
                "$t != 'Apocalypse Now';\n" +
                "get;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();

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
        ).get();

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }
}
