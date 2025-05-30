/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.typeql.lang.query.test;

import com.typeql.lang.TypeQL;
import com.typeql.lang.TypeQL.Expression;
import com.typeql.lang.common.TypeQLArg;
import com.typeql.lang.query.TypeQLDefine;
import com.typeql.lang.query.TypeQLGet;
import com.typeql.lang.query.TypeQLInsert;
import com.typeql.lang.query.TypeQLQuery;
import org.junit.Test;

import static com.typeql.lang.TypeQL.and;
import static com.typeql.lang.TypeQL.cVar;
import static com.typeql.lang.TypeQL.label;
import static com.typeql.lang.TypeQL.lte;
import static com.typeql.lang.TypeQL.match;
import static com.typeql.lang.TypeQL.or;
import static com.typeql.lang.TypeQL.rel;
import static com.typeql.lang.TypeQL.rule;
import static com.typeql.lang.TypeQL.type;
import static com.typeql.lang.TypeQL.vVar;
import static org.junit.Assert.assertEquals;

// TODO: This test should be split into one TypeQL query test class each
public class TypeQLQueryTest {

    @Test
    public void testSimpleGetQueryToString() {
        assertSameStringRepresentation(match(cVar("x").isa("movie").has("title", "Godfather")).get());
    }

    @Test
    public void testComplexQueryToString() {
        TypeQLGet query = match(
                cVar("x").isa("movie"),
                cVar().rel(cVar("x")).rel(cVar("y")),
                or(
                        cVar("y").isa("person"),
                        and(
                                cVar("y").neq("crime"),
                                cVar("y").neq("book")
                        )
                ),
                cVar("y").has("name", cVar("n"))
        ).get(cVar("x"), cVar("y"), cVar("n")).sort(cVar("n")).offset(4).limit(8);

        assertEquivalent(query, query.toString());
    }

    @Test
    public void testQueryWithResourcesToString() {
        assertSameStringRepresentation(match(cVar("x").has("tmdb-vote-count", lte(400))).get());
    }

    @Test
    public void testQueryWithSubToString() {
        assertSameStringRepresentation(match(cVar("x").sub(cVar("y"))).get());
    }

    @Test
    public void testQueryWithPlaysToString() {
        assertSameStringRepresentation(match(cVar("x").plays(cVar("y"))).get());
    }

    @Test
    public void testQueryWithRelatesToString() {
        assertSameStringRepresentation(match(cVar("x").relates(cVar("y"))).get());
    }

    @Test
    public void testQueryWithValueTypeToString() {
        assertSameStringRepresentation(match(cVar("x").value(TypeQLArg.ValueType.LONG)).get());
    }

    @Test
    public void testQueryIsAbstractToString() {
        assertSameStringRepresentation(match(cVar("x").isAbstract()).get());
    }

    @Test
    public void testQueryWithRuleThenToString() {
        TypeQLDefine query = TypeQL.define(rule("a-rule").when(and(TypeQL.parsePatterns("$x isa movie;"))).then(TypeQL.parseStatement("$x has name 'Ghostbusters'").asThing()));
        assertValidToString(query);
    }

    private void assertValidToString(TypeQLQuery query) {
        //No need to execute the insert query
        TypeQLQuery parsedQuery = TypeQL.parseQuery(query.toString());
        assertEquals(query.toString(), parsedQuery.toString());
    }

    @Test
    public void testInsertQueryToString() {
        assertEquals("insert\n$x isa movie;", TypeQL.insert(cVar("x").isa("movie")).toString());
    }

    @Test
    public void testEscapeStrings() {
        assertEquals("insert\n$x \"hello\nworld\";", TypeQL.insert(cVar("x").eq("hello\nworld")).toString());
        assertEquals("insert\n$x \"hello\\nworld\";", TypeQL.insert(cVar("x").eq("hello\\nworld")).toString());
    }

    @Test
    public void testOwns() {
        assertEquals("define\nperson owns thingy;", TypeQL.define(type("person").owns("thingy")).toString());
    }

    @Test
    public void testRepeatRoleplayerToString() {
        assertEquals("match\n($x, $x);", match(rel(cVar("x")).rel(cVar("x"))).toString());
    }

    @Test
    public void testMatchInsertToString() {
        TypeQLInsert query = match(cVar("x").isa("movie")).insert(cVar("x").has("title", "hello"));
        assertEquals("match\n$x isa movie;\ninsert\n$x has title \"hello\";", query.toString());
    }

    @Test
    public void testMatchInsertWithValueVariable() {
        TypeQLInsert query = match(vVar("x").assign(Expression.constant(2))).insert(cVar("a").eq(vVar("x")).isa("prime"));
        assertEquals("match\n?x = 2;\ninsert\n$a == ?x isa prime;", query.toString());
    }

    @Test
    public void testMatchInsertOwnershipWithValueVariable() {
        TypeQLInsert query = match(
                vVar("x").assign(Expression.constant(2))
        ).insert(cVar("p").has("prime", vVar("x")));
        assertEquals("match\n?x = 2;\ninsert\n$p has prime == ?x;", query.toString());
    }

    @Test
    public void testZeroToString() {
        assertEquals("match\n$x 0.0;", match(cVar("x").eq(0.0)).toString());
    }

    @Test
    public void testExponentsToString() {
        assertEquals("match\n$x 1000000000.0;", match(cVar("x").eq(1_000_000_000.0)).toString());
    }

    @Test
    public void testDecimalToString() {
        assertEquals("match\n$x 0.0001;", match(cVar("x").eq(0.0001)).toString());
    }

    @Test
    public void whenCallingToStringOnDeleteQuery_ItLooksLikeOriginalQuery() {
        final String query = "match\n$x isa movie;\n" +
                "delete\n$x isa movie;";
        assertEquals(query, TypeQL.parseQuery(query).toString());
    }

    @Test
    public void testFetch() {
        String query = "match\n" +
                "$x isa person,\n" +
                "    has id $id;\n" +
                "fetch\n" +
                "$id;\n" +
                "$x: name, age, email;\n" +
                "children: {\n" +
                "    match\n" +
                "    ($c, $x) isa parenthood;\n" +
                "    fetch\n" +
                "    $c: name;\n" +
                "};";
        assertEquivalent(TypeQL.parseQuery(query), query);
    }

    @Test
    public void testFetchBuilder() {
        match(
                cVar("x").isa("person").has("id", cVar("id"))
        ).fetch(
                cVar("id"),
                cVar("x").fetch("name").fetch("age").fetch("email"),
                label("children").fetch(
                        match(
                                rel(cVar("c")).rel(cVar("x")).isa("parenthood")
                        ).fetch(
                                cVar("c").fetch("name")
                        )
                )
        );
        // TODO make test
    }

    private void assertSameStringRepresentation(TypeQLGet query) {
        assertEquals(query.toString(), TypeQL.parseQuery(query.toString()).toString());
    }

    private void assertEquivalent(TypeQLQuery query, String queryString) {
        assertEquals(queryString, query.toString());
        assertEquals(query.toString(), TypeQL.parseQuery(queryString).toString());
    }
}