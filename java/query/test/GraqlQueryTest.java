/*
 * Copyright (C) 2020 Grakn Labs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package graql.lang.query.test;

import graql.lang.Graql;
import graql.lang.common.GraqlArg;
import graql.lang.query.GraqlCompute;
import graql.lang.query.GraqlDefine;
import graql.lang.query.GraqlInsert;
import graql.lang.query.GraqlMatch;
import graql.lang.query.GraqlQuery;
import org.junit.Test;

import static graql.lang.Graql.and;
import static graql.lang.Graql.lte;
import static graql.lang.Graql.match;
import static graql.lang.Graql.or;
import static graql.lang.Graql.rel;
import static graql.lang.Graql.rule;
import static graql.lang.Graql.type;
import static graql.lang.Graql.var;
import static graql.lang.common.GraqlArg.Algorithm.CONNECTED_COMPONENT;
import static graql.lang.common.GraqlArg.Algorithm.DEGREE;
import static graql.lang.common.GraqlArg.Algorithm.K_CORE;
import static graql.lang.query.GraqlCompute.Argument.k;
import static graql.lang.query.GraqlCompute.Argument.minK;
import static graql.lang.query.GraqlCompute.Argument.size;
import static org.junit.Assert.assertEquals;

// TODO: This test should be split into one Graql query test class each
public class GraqlQueryTest {

    @Test
    public void testSimpleGetQueryToString() {
        assertSameStringRepresentation(match(var("x").isa("movie").has("title", "Godfather")));
    }

    @Test
    public void testComplexQueryToString() {
        final GraqlMatch query = match(
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
        assertSameStringRepresentation(match(var("x").value(GraqlArg.ValueType.LONG)));
    }

    @Test
    public void testQueryIsAbstractToString() {
        assertSameStringRepresentation(match(var("x").isAbstract()));
    }

    @Test
    public void testQueryWithRuleThenToString() {
        final GraqlDefine query = Graql.define(rule("a-rule").when(and(Graql.parsePatterns("$x isa movie;"))).then(Graql.parseVariable("$x isa movie").asThing()));
        assertValidToString(query);
    }

    private void assertValidToString(final GraqlQuery query) {
        //No need to execute the insert query
        final GraqlQuery parsedQuery = Graql.parseQuery(query.toString());
        assertEquals(query.toString(), parsedQuery.toString());
    }

    @Test
    public void testInsertQueryToString() {
        assertEquals("insert $x isa movie;", Graql.insert(var("x").isa("movie")).toString());
    }

    @Test
    public void testEscapeStrings() {
        assertEquals("insert $x \"hello\nworld\";", Graql.insert(var("x").val("hello\nworld")).toString());
        assertEquals("insert $x \"hello\\nworld\";", Graql.insert(var("x").val("hello\\nworld")).toString());
    }

    @Test
    public void testOwns() {
        assertEquals("define person owns thingy;", Graql.define(type("person").owns("thingy")).toString());
    }

    @Test
    public void testComputeQueryToString() {
        assertEquals("compute count;", Graql.compute().count().toString());
    }

    @Test
    public void testComputeQuerySubgraphToString() {
        final GraqlCompute query = Graql.compute().centrality().using(DEGREE).in("movie", "person");
        assertEquivalent(query, "compute centrality in [movie, person], using degree;");
    }

    @Test
    public void testClusterToString() {
        final GraqlCompute connectedcomponent = Graql.compute().cluster().using(CONNECTED_COMPONENT).in("movie", "person");
        assertEquivalent(connectedcomponent, "compute cluster in [movie, person], using connected-component;");

        final GraqlCompute kcore = Graql.compute().cluster().using(K_CORE).in("movie", "person");
        assertEquivalent(kcore, "compute cluster in [movie, person], using k-core;");
    }

    @Test
    public void testCCSizeToString() {
        final GraqlCompute query = Graql.compute().cluster().using(CONNECTED_COMPONENT).in("movie", "person").where(size(10));
        assertEquivalent(query, "compute cluster in [movie, person], using connected-component, where size=10;");
    }

    @Test
    public void testKCoreToString() {
        final GraqlCompute query = Graql.compute().cluster().using(K_CORE).in("movie", "person").where(k(10));
        assertEquivalent(query, "compute cluster in [movie, person], using k-core, where k=10;");
    }

    @Test
    public void testCentralityOf() {
        GraqlCompute query = Graql.compute().centrality().using(DEGREE).in("movie", "person").of("person");
        assertEquivalent(query, "compute centrality of person, in [movie, person], using degree;");

        query = Graql.compute().centrality().using(K_CORE).in("movie", "person").of("person").where(minK(5));
        assertEquivalent(query, "compute centrality of person, in [movie, person], using k-core, where min-k=5;");
    }

    @Test
    public void testRepeatRoleplayerToString() {
        assertEquals("match ($x, $x);", match(rel("x").rel("x")).toString());
    }

    @Test
    public void testMatchInsertToString() {
        final GraqlInsert query = match(var("x").isa("movie")).insert(var("x").has("title", "hello"));
        assertEquals("match $x isa movie;\ninsert $x has title \"hello\";", query.toString());
    }

    @Test
    public void testZeroToString() {
        assertEquals("match $x 0.0;", match(var("x").val(0.0)).toString());
    }

    @Test
    public void testExponentsToString() {
        assertEquals("match $x 1000000000.0;", match(var("x").val(1_000_000_000.0)).toString());
    }

    @Test
    public void testDecimalToString() {
        assertEquals("match $x 0.0001;", match(var("x").val(0.0001)).toString());
    }

    @Test
    public void whenCallingToStringOnDeleteQuery_ItLooksLikeOriginalQuery() {
        final String query = "match $x isa movie;\n" +
                "delete $x isa movie;";
        assertEquals(query, Graql.parseQuery(query).toString());
    }

    @Test
    public void whenCallingToStringOnAQueryWithAContainsPredicate_ResultIsCorrect() {
        final GraqlMatch.Unfiltered match = match(var("x").contains(var("y")));

        assertEquals("match $x contains $y;", match.toString());
    }

    private void assertSameStringRepresentation(final GraqlMatch query) {
        assertEquals(query.toString(), Graql.parseQuery(query.toString()).toString());
    }

    private void assertEquivalent(final GraqlQuery query, final String queryString) {
        assertEquals(queryString, query.toString());
        assertEquals(query.toString(), Graql.parseQuery(queryString).toString());
    }
}