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

package com.vaticle.typeql.lang.parser.test;

import com.vaticle.typeql.lang.TypeQL;
import com.vaticle.typeql.lang.TypeQL.Expression;
import com.vaticle.typeql.lang.common.TypeQLArg;
import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.Conjunction;
import com.vaticle.typeql.lang.pattern.Pattern;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;
import com.vaticle.typeql.lang.query.TypeQLDefine;
import com.vaticle.typeql.lang.query.TypeQLDelete;
import com.vaticle.typeql.lang.query.TypeQLInsert;
import com.vaticle.typeql.lang.query.TypeQLGet;
import com.vaticle.typeql.lang.query.TypeQLQuery;
import com.vaticle.typeql.lang.query.TypeQLUndefine;
import com.vaticle.typeql.lang.query.TypeQLUpdate;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typedb.common.collection.Collections.pair;
import static com.vaticle.typeql.lang.TypeQL.and;
import static com.vaticle.typeql.lang.TypeQL.cVar;
import static com.vaticle.typeql.lang.TypeQL.define;
import static com.vaticle.typeql.lang.TypeQL.gte;
import static com.vaticle.typeql.lang.TypeQL.insert;
import static com.vaticle.typeql.lang.TypeQL.lt;
import static com.vaticle.typeql.lang.TypeQL.lte;
import static com.vaticle.typeql.lang.TypeQL.match;
import static com.vaticle.typeql.lang.TypeQL.not;
import static com.vaticle.typeql.lang.TypeQL.or;
import static com.vaticle.typeql.lang.TypeQL.parseQuery;
import static com.vaticle.typeql.lang.TypeQL.rel;
import static com.vaticle.typeql.lang.TypeQL.rule;
import static com.vaticle.typeql.lang.TypeQL.type;
import static com.vaticle.typeql.lang.TypeQL.undefine;
import static com.vaticle.typeql.lang.TypeQL.vVar;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ParserTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private void assertQueryEquals(TypeQLQuery expected, TypeQLQuery parsed, String query) {
        assertEquals(expected, parsed);
        assertEquals(expected, TypeQL.parseQuery(parsed.toString()));
        assertEquals(query, expected.toString());
    }

    private void assertQueryEquals(Pattern expected, Pattern parsed, String query) {
        assertEquals(expected, parsed);
        assertEquals(expected, TypeQL.parsePattern(parsed.toString()));
        assertEquals(query, expected.toString());
    }

    private void assertThrows(Runnable function) {
        try {
            function.run();
            fail();
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSimpleQuery() {
        final String query = "match\n" +
                "$x isa movie;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(cVar("x").isa("movie")).get();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testNamedTypeVariable() {
        String query = "match\n" +
                "$a type attribute_label;\n" +
                "get $a;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(cVar("a").type("attribute_label")).get(cVar("a"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testParseStringWithSlash() {
        final String query = "match\n" +
                "" +
                "$x isa person,\n" +
                "    has name 'alice/bob';";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(cVar("x").isa("person").has("name", "alice/bob")).get();

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testRelationQuery() {
        final String query = "match\n" +
                "" +
                "$brando 'Marl B' isa name;\n" +
                "(actor: $brando, $char, production-with-cast: $prod);\n" +
                "get $char, $prod;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();

        TypeQLGet expected = match(
                cVar("brando").eq("Marl B").isa("name"),
                rel("actor", TypeQL.cVar("brando")).rel(TypeQL.cVar("char")).rel("production-with-cast", TypeQL.cVar("prod"))
        ).get(cVar("char"), cVar("prod"));

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testRoleTypeScopedGlobally() {
        String query = "match\n" +
                "$x relates spouse;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(cVar("x").relates("spouse")).get();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testRoleTypeNotScoped() {
        String query = "match\n" +
                "marriage relates $s;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(type("marriage").relates(cVar("s"))).get();

        assertQueryEquals(expected, parsed, query);
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
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();

        TypeQLGet expected = match(
                cVar("x").isa("movie").has("title", cVar("t")),
                or(
                        cVar("t").eq("Apocalypse Now"),
                        and(
                                cVar("t").lt("Juno"),
                                cVar("t").gt("Godfather")
                        ),
                        cVar("t").eq("Spy")
                ),
                cVar("t").neq("Apocalypse Now")
        ).get();

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testPredicateQuery2() {
        final String query = "match\n" +
                "$x isa movie,\n" +
                "    has title $t;\n" +
                "{\n" +
                "    $t <= 'Juno';\n" +
                "    $t >= 'Godfather';\n" +
                "    $t != 'Heat';\n" +
                "} or {\n" +
                "    $t 'The Muppets';\n" +
                "};";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();

        TypeQLGet expected = match(
                cVar("x").isa("movie").has("title", cVar("t")),
                or(
                        and(
                                cVar("t").lte("Juno"),
                                cVar("t").gte("Godfather"),
                                cVar("t").neq("Heat")
                        ),
                        cVar("t").eq("The Muppets")
                )
        ).get();

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testPredicateQuery3() {
        final String query = "match\n" +
                "($x, $y);\n" +
                "$y isa person,\n" +
                "    has name $n;\n" +
                "{\n" +
                "    $n contains 'ar';\n" +
                "} or {\n" +
                "    $n like '^M.*$';\n" +
                "};";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();

        TypeQLGet expected = match(
                rel(cVar("x")).rel(cVar("y")),
                cVar("y").isa("person").has("name", cVar("n")),
                or(cVar("n").contains("ar"), cVar("n").like("^M.*$"))
        ).get();

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testPredicateQuery4() {
        final String query = "match\n" +
                "$x has age $y;\n" +
                "$y >= $z;\n" +
                "$z 18 isa age;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();

        TypeQLGet expected = match(
                cVar("x").has("age", cVar("y")),
                cVar("y").gte(cVar("z")),
                cVar("z").eq(18).isa("age")
        ).get();;

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testConceptVariable() {
        final String query = "match\n" +
                "$x sub $z;\n" +
                "$y sub $z;\n" +
                "$a isa $x;\n" +
                "$b isa $y;\n" +
                "not { $x is $y; };\n" +
                "not { $a is $b; };";
        TypeQLGet parsed = TypeQL.parseQuery(query);

        TypeQLGet expected = match(
                cVar("x").sub(cVar("z")), cVar("y").sub(cVar("z")),
                cVar("a").isa(cVar("x")), cVar("b").isa(cVar("y")),
                not(cVar("x").is("y")),
                not(cVar("a").is("b"))
        ).get();;

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testValueEqualsVariableQuery() {
        final String query = "match\n$s1 == $s2;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(cVar("s1").eq(cVar("s2"))).get();;

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testValueEqualsVariableQuery_backwardsCompatibility() {
        // Remove when we fully deprecate '=' for ThingVariable equality.
        final String query = "match\n$s1 = $s2;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(cVar("s1").eq(cVar("s2"))).get();;

        assertEquals(expected, parsed);
        assertEquals(expected, TypeQL.parseQuery(parsed.toString()));
    }

    @Test
    public void testMoviesReleasedAfterOrAtTheSameTimeAsSpy() {
        final String query = "match\n" +
                "$x has release-date >= $r;\n" +
                "$_ has title 'Spy',\n" +
                "    has release-date $r;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();

        TypeQLGet expected = match(
                cVar("x").has("release-date", gte(cVar("r"))),
                cVar().has("title", "Spy").has("release-date", cVar("r"))
        ).get();;

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testPredicates() {
        final String query = "match\n" +
                "$x has release-date < 1986-03-03T00:00,\n" +
                "    has tmdb-vote-count 100,\n" +
                "    has tmdb-vote-average <= 9.0;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();

        TypeQLGet expected = match(
                cVar("x")
                        .has("release-date", lt(LocalDate.of(1986, 3, 3).atStartOfDay()))
                        .has("tmdb-vote-count", 100)
                        .has("tmdb-vote-average", lte(9.0))
        ).get();;

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDate_HandleTime() {
        final String query = "match\n$x has release-date 1000-11-12T13:14:15;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(cVar("x").has("release-date", LocalDateTime.of(1000, 11, 12, 13, 14, 15))).get();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDate_HandleBigYears() {
        TypeQLGet expected = match(cVar("x").has("release-date", LocalDate.of(12345, 12, 25).atStartOfDay())).get();
        final String query = "match\n$x has release-date +12345-12-25T00:00;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDate_HandleSmallYears() {
        final String query = "match\n$x has release-date 0867-01-01T00:00;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(cVar("x").has("release-date", LocalDate.of(867, 1, 1).atStartOfDay())).get();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDate_HandleNegativeYears() {
        final String query = "match\n$x has release-date -3200-01-01T00:00;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(cVar("x").has("release-date", LocalDate.of(-3200, 1, 1).atStartOfDay())).get();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDate_HandleMillis() {
        final String query = "match\n$x has release-date 1000-11-12T13:14:15.123;";
        TypeQLGet expected = match(cVar("x").has("release-date", LocalDateTime.of(1000, 11, 12, 13, 14, 15, 123000000))).get();
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDate_HandleMillisShorthand() {
        final String query = "match\n$x has release-date 1000-11-12T13:14:15.1;";
        final String parsedQueryString = "match\n$x has release-date 1000-11-12T13:14:15.100;";
        TypeQLGet expected = match(cVar("x").has("release-date", LocalDateTime.of(1000, 11, 12, 13, 14, 15, 100000000))).get();
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        assertQueryEquals(expected, parsed, parsedQueryString);
    }


    @Test
    public void whenParsingDate_ErrorWhenHandlingOverPreciseDecimalSeconds() {
        final String query = "match\n$x has release-date 1000-11-12T13:14:15.000123456;";
        exception.expect(TypeQLException.class);
        exception.expectMessage(Matchers.containsString("no viable alternative"));
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
    }

    @Test
    public void whenParsingDateTime_ErrorWhenHandlingOverPreciseNanos() {
        exception.expect(TypeQLException.class);
        exception.expectMessage(Matchers.containsString("more precise than 1 millisecond"));
        TypeQLGet apiQuery = match(cVar("x").has("release-date", LocalDateTime.of(1000, 11, 12, 13, 14, 15, 123450000))).get();
    }

    @Test
    public void testLongPredicateQuery() {
        final String query = "match\n" +
                "$x isa movie,\n" +
                "    has tmdb-vote-count <= 400;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(cVar("x").isa("movie").has("tmdb-vote-count", lte(400))).get();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testAttributeQueryByValueVariable() {
        final String query = "match\n" +
                "?x = 5;\n" +
                "$a == ?x isa age;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(
                vVar("x").assign(Expression.constant(5)),
                cVar("a").eq(vVar("x")).isa("age")).get();
        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testVariableNameClashThrows() {
        final String query = "match\n" +
                "$z isa person, has age $y;\n" +
                "?y = $y;\n";
        assertThrows(() -> parseQuery(query));
    }

    @Test
    public void testAssignOps() {
        final String query = "match\n" +
                "$x isa commodity,\n" +
                "    has price $p;\n" +
                "(commodity: $x, qty: $q) isa order;\n" +
                "?net = $p * $q;\n" +
                "?gross = ?net * ( 1.0 + 0.21 );";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(
                cVar("x").isa("commodity").has("price", cVar("p")),
                rel("commodity", TypeQL.cVar("x")).rel("qty", TypeQL.cVar("q")).isa("order"),
                vVar("net").assign(cVar("p").mul(cVar("q"))),
                vVar("gross").assign(
                        vVar("net").mul(Expression.parenthesis(Expression.constant(1.0).add(Expression.constant(0.21))))
                )
        ).get();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testOpPrecedence() {
        final String query = "match\n" +
                "?res = $a / $b * $c + $d ^ $e ^ $f / $g;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(
                vVar("res").assign(
                        cVar("a")
                                .div(cVar("b"))
                                .mul(cVar("c"))
                                .add(cVar("d").pow(cVar("e").pow(cVar("f"))).div(cVar("g")))
                )
        ).get();
        assertQueryEquals(expected, parsed, query);
    }


    @Test
    public void testFuncParenthesisPrecedence() {
        final String query = "match\n" +
                "?res = $a + ( round($b + ?c) + $d ) * ?e;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(
                vVar("res").assign(
                        cVar("a").add(Expression.parenthesis(
                                Expression.round(cVar("b").add(vVar("c"))).add(cVar("d"))
                        ).mul(vVar("e"))))).get();
        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testAssignFunction() {
        final String query = "match\n" +
                "$x isa commodity,\n" +
                "    has price $p;\n" +
                "(commodity: $x, qty: $q) isa order;\n" +
                "?net = $p * $q;\n" +
                "?gross = min(?net * 1.21, ?net + 100.0);" +
                "";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(
                cVar("x").isa("commodity").has("price", cVar("p")),
                rel("commodity", TypeQL.cVar("x")).rel("qty", TypeQL.cVar("q")).isa("order"),
                vVar("net").assign(cVar("p").mul(cVar("q"))),
                vVar("gross").assign(Expression.min(
                        vVar("net").mul(Expression.constant(1.21)),
                        vVar("net").add(Expression.constant(100.0))
                ))
        ).get();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testSchemaQuery() {
        final String query = "match\n" +
                "$x plays starring:actor;\n" +
                "sort $x asc;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(cVar("x").plays("starring", "actor")).get().sort(pair(cVar("x"), "asc"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testGetSort() {
        final String query = "match\n" +
                "$x isa movie,\n" +
                "    has rating $r;\n" +
                "sort $r desc;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(
                cVar("x").isa("movie").has("rating", cVar("r"))
        ).get().sort(pair(cVar("r"), "desc"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testGetSortOnValueVariable() {
        final String query = "match\n" +
                "$x isa movie,\n" +
                "    has rating $r;\n" +
                "?l = 100 - $r;\n" +
                "sort ?l desc;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        com.vaticle.typeql.lang.pattern.variable.builder.Expression a = Expression.constant(100);
        com.vaticle.typeql.lang.pattern.variable.builder.Expression b = cVar("r");
        TypeQLGet expected = match(
                cVar("x").isa("movie").has("rating", cVar("r")),
                vVar("l").assign(a.sub(b))
        ).get().sort(pair(vVar("l"), "desc"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testGetSortLimit() {
        final String query = "match\n" +
                "$x isa movie,\n" +
                "    has rating $r;\n" +
                "sort $r; limit 10;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(
                cVar("x").isa("movie").has("rating", cVar("r"))
        ).get().sort(cVar("r")).limit(10);

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testGetSortOffsetLimit() {
        final String query = "match\n" +
                "$x isa movie,\n" +
                "    has rating $r;\n" +
                "sort $r desc, $x asc; offset 10; limit 10;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(
                cVar("x").isa("movie").has("rating", cVar("r"))
        ).get().sort(pair(cVar("r"), "desc"), pair(cVar("x"), "asc")).offset(10).limit(10);

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testGetOffsetLimit() {
        final String query = "match\n" +
                "$y isa movie,\n" +
                "    has title $n;\n" +
                "offset 2; limit 4;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(
                cVar("y").isa("movie").has("title", cVar("n"))
        ).get().offset(2).limit(4);

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testVariablesEverywhereQuery() {
        final String query = "match\n" +
                "($p: $x, $y);\n" +
                "$x isa $z;\n" +
                "$y 'crime';\n" +
                "$z sub production;\n" +
                "has-genre relates $p;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(
                rel(cVar("p"), cVar("x")).rel(cVar("y")),
                cVar("x").isa(cVar("z")),
                cVar("y").eq("crime"),
                cVar("z").sub("production"),
                type("has-genre").relates(cVar("p"))
        ).get();

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testParseRelatesTypeVariable() {
        final String query = "match\n" +
                "$x isa $type;\n" +
                "$type relates someRole;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(cVar("x").isa(cVar("type")), cVar("type").relates("someRole")).get();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testOrQuery() {
        final String query = "match\n" +
                "$x isa movie;\n" +
                "{\n" +
                "    $y 'drama' isa genre;\n" +
                "    ($x, $y);\n" +
                "} or {\n" +
                "    $x 'The Muppets';\n" +
                "};";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(
                cVar("x").isa("movie"),
                or(
                        and(
                                cVar("y").eq("drama").isa("genre"),
                                rel(cVar("x")).rel(cVar("y"))
                        ),
                        cVar("x").eq("The Muppets")
                )
        ).get();

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testDisjunctionNotInConjunction() {
        String query = "match\n" +
                "{\n" +
                "    $x isa person;\n" +
                "} or {\n" +
                "    $x isa company;\n" +
                "};";
        assertThrows(() -> parseQuery(query).asGet());
    }


    @Test
    public void testNestedConjunctionAndDisjunction() {
        String query = "match\n" +
                "$y isa $p;\n" +
                "{\n" +
                "    ($y, $q);\n" +
                "} or {\n" +
                "    $x isa $p;\n" +
                "    {\n" +
                "        $x has first-name $y;\n" +
                "    } or {\n" +
                "        $x has last-name $z;\n" +
                "    };\n" +
                "};";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(
                cVar("y").isa(cVar("p")),
                or(rel(cVar("y")).rel(cVar("q")),
                        and(cVar("x").isa(cVar("p")),
                                or(cVar("x").has("first-name", cVar("y")),
                                        cVar("x").has("last-name", cVar("z")))))).get();
        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testDisjunctionNotBindingConjunction() {
        String query = "match\n" +
                "$y isa $p;\n" +
                "{ ($y, $q); } or { $x isa $p; { $x has first-name $y; } or { $q has last-name $z; }; };";
        assertThrows(() -> parseQuery(query).asGet());
    }

    @Test
    public void testAggregateCountQuery() {
        final String query = "match\n" +
                "($x, $y) isa friendship;\n" +
                "get $x, $y;\n" +
                "count;";
        TypeQLGet.Aggregate parsed = parseQuery(query).asGetAggregate();
        TypeQLGet.Aggregate expected = match(rel(cVar("x")).rel(cVar("y")).isa("friendship")).get(cVar("x"), cVar("y")).count();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testAggregateGroupCountQuery() {
        final String query = "match\n" +
                "($x, $y) isa friendship;\n" +
                "get $x, $y;\n" +
                "group $x; count;";
        TypeQLGet.Group.Aggregate parsed = parseQuery(query).asGetGroupAggregate();
        TypeQLGet.Group.Aggregate expected = match(rel(cVar("x")).rel(cVar("y")).isa("friendship")).get(cVar("x"), cVar("y")).group(cVar("x")).count();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testSingleLineGroupAggregateMaxQuery() {
        final String query = "match\n" +
                "$x has age $a;\n" +
                "group $x; max $a;";
        TypeQLGet.Group.Aggregate parsed = parseQuery(query).asGetGroupAggregate();
        TypeQLGet.Group.Aggregate expected = match(cVar("x").has("age", cVar("a"))).get().group(cVar("x")).max(cVar("a"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testMultiLineGroupAggregateMaxQuery() {
        final String query = "match\n" +
                "($x, $y) isa friendship;\n" +
                "$y has age $z;\n" +
                "group $x; max $z;";
        TypeQLGet.Group.Aggregate parsed = parseQuery(query).asGetGroupAggregate();
        TypeQLGet.Group.Aggregate expected = match(
                rel(cVar("x")).rel(cVar("y")).isa("friendship"),
                cVar("y").has("age", cVar("z"))
        ).get().group(cVar("x")).max(cVar("z"));


        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testMultiLineFilteredGroupAggregateMaxQuery() {
        final String query = "match\n" +
                "($x, $y) isa friendship;\n" +
                "$y has age $z;\n" +
                "get $x, $y, $z;\n" +
                "group $x; max $z;";
        TypeQLGet.Group.Aggregate parsed = parseQuery(query).asGetGroupAggregate();
        TypeQLGet.Group.Aggregate expected = match(
                rel(cVar("x")).rel(cVar("y")).isa("friendship"),
                cVar("y").has("age", cVar("z"))
        ).get(cVar("x"), cVar("y"), cVar("z")).group(cVar("x")).max(cVar("z"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testFilteredGroupAggregatesOnValueVariable() {
        final String query = "match\n" +
                "$i ($x, $s) isa income-source;\n" +
                "$i has value $v,\n" +
                "    has tax-rate $r;\n" +
                "?t = $r * $v;\n" +
                "get $x, ?t;\n" +
                "group $x; sum ?t;";
        TypeQLGet.Group.Aggregate parsed = parseQuery(query).asGetGroupAggregate();
        com.vaticle.typeql.lang.pattern.variable.builder.Expression a = cVar("r");
        com.vaticle.typeql.lang.pattern.variable.builder.Expression b = cVar("v");
        TypeQLGet.Group.Aggregate expected = match(
                cVar("i").rel(cVar("x")).rel(cVar("s")).isa("income-source"),
                cVar("i").has("value", cVar("v")).has("tax-rate", cVar("r")),
                vVar("t").assign(a.mul(b))
        ).get(list(cVar("x"), vVar("t"))).group(cVar("x")).sum(vVar("t"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenComparingCountQueryUsingTypeQLAndJavaTypeQL_TheyAreEquivalent() {
        final String query = "match\n" +
                "$x isa movie,\n" +
                "    has title \"Godfather\";\n" +
                "count;";
        TypeQLGet.Aggregate parsed = parseQuery(query).asGetAggregate();
        TypeQLGet.Aggregate expected = match(cVar("x").isa("movie").has("title", "Godfather")).get().count();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testInsertQuery() {
        final String query = "insert\n$_ isa movie,\n" +
                "    has title \"The Title\";";
        TypeQLInsert parsed = TypeQL.parseQuery(query).asInsert();
        TypeQLInsert expected = insert(cVar().isa("movie").has("title", "The Title"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDeleteQuery_ResultIsSameAsJavaTypeQL() {
        final String query = "match\n" +
                "$x isa movie,\n" +
                "    has title 'The Title';\n" +
                "$y isa movie;\n" +
                "delete\n" +
                "$x isa movie;\n" +
                "$y isa movie;";
        TypeQLDelete parsed = TypeQL.parseQuery(query).asDelete();
        TypeQLDelete expected = match(
                cVar("x").isa("movie").has("title", "The Title"),
                cVar("y").isa("movie")
        ).delete(cVar("x").isa("movie"), cVar("y").isa("movie"));

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void whenParsingInsertQuery_ResultIsSameAsJavaTypeQL() {
        final String query = "insert\n" +
                "$x isa pokemon,\n" +
                "    has name 'Pichu';\n" +
                "$y isa pokemon,\n" +
                "    has name 'Pikachu';\n" +
                "$z isa pokemon,\n" +
                "    has name 'Raichu';\n" +
                "(evolves-from: $x, evolves-to: $y) isa evolution;\n" +
                "(evolves-from: $y, evolves-to: $z) isa evolution;";
        TypeQLInsert parsed = TypeQL.parseQuery(query).asInsert();
        TypeQLInsert expected = insert(
                cVar("x").isa("pokemon").has("name", "Pichu"),
                cVar("y").isa("pokemon").has("name", "Pikachu"),
                cVar("z").isa("pokemon").has("name", "Raichu"),
                rel("evolves-from", TypeQL.cVar("x")).rel("evolves-to", TypeQL.cVar("y")).isa("evolution"),
                rel("evolves-from", TypeQL.cVar("y")).rel("evolves-to", TypeQL.cVar("z")).isa("evolution")
        );

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void whenParsingUpdateQuery_ResultIssameAsJavaTypeQL() {
        String query = "match\n" +
                "$x isa person,\n" +
                "    has name 'alice',\n" +
                "    has age $a;\n" +
                "delete\n" +
                "$x has $a;\n" +
                "insert\n" +
                "$x has age 25;";
        TypeQLUpdate parsed = TypeQL.parseQuery(query).asUpdate();
        TypeQLUpdate expected = match(cVar("x").isa("person").has("name", "alice").has("age", cVar("a")))
                .delete(cVar("x").has(cVar("a")))
                .insert(cVar("x").has("age", 25));
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void whenParsingAsInDefine_ResultIsSameAsSub() {
        final String query = "define\n" +
                "parent sub role;\n" +
                "child sub role;\n" +
                "parenthood sub relation,\n" +
                "    relates parent,\n" +
                "    relates child;\n" +
                "fatherhood sub parenthood,\n" +
                "    relates father as parent,\n" +
                "    relates son as child;";
        TypeQLDefine parsed = TypeQL.parseQuery(query).asDefine();

        TypeQLDefine expected = define(
                type("parent").sub("role"),
                type("child").sub("role"),
                type("parenthood").sub("relation")
                        .relates("parent")
                        .relates("child"),
                type("fatherhood").sub("parenthood")
                        .relates("father", "parent")
                        .relates("son", "child")
        );

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingAsInMatch_ResultIsSameAsSub() {
        final String query = "match\n" +
                "$f sub parenthood,\n" +
                "    relates father as parent,\n" +
                "    relates son as child;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(
                cVar("f").sub("parenthood")
                        .relates("father", "parent")
                        .relates("son", "child")
        ).get();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDefineQueryWithOwnsOverrides_ResultIsSameAsJavaTypeQL() {
        final String query = "define\n" +
                "triangle sub entity;\n" +
                "triangle owns side-length;\n" +
                "triangle-right-angled sub triangle;\n" +
                "triangle-right-angled owns hypotenuse-length as side-length;";
        TypeQLDefine parsed = TypeQL.parseQuery(query).asDefine();

        TypeQLDefine expected = define(
                type("triangle").sub("entity"),
                type("triangle").owns("side-length"),
                type("triangle-right-angled").sub("triangle"),
                type("triangle-right-angled").owns("hypotenuse-length", "side-length")
        );
        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDefineQueryWithRelatesOverrides_ResultIsSameAsJavaTypeQL() {
        final String query = "define\n" +
                "pokemon sub entity;\n" +
                "evolves sub relation;\n" +
                "evolves relates from,\n" +
                "    relates to;\n" +
                "evolves-final sub evolves;\n" +
                "evolves-final relates from-final as from;";
        TypeQLDefine parsed = TypeQL.parseQuery(query).asDefine();

        TypeQLDefine expected = define(
                type("pokemon").sub("entity"),
                type("evolves").sub("relation"),
                type("evolves").relates("from").relates("to"),
                type("evolves-final").sub("evolves"),
                type("evolves-final").relates("from-final", "from")
        );
        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDefineQueryWithPlaysOverrides_ResultIsSameAsJavaTypeQL() {
        final String query = "define\n" +
                "pokemon sub entity;\n" +
                "evolves sub relation;\n" +
                "evolves relates from,\n" +
                "    relates to;\n" +
                "evolves-final sub evolves;\n" +
                "evolves-final relates from-final as from;\n" +
                "pokemon plays evolves-final:from-final as from;";
        TypeQLDefine parsed = TypeQL.parseQuery(query).asDefine();

        TypeQLDefine expected = define(
                type("pokemon").sub("entity"),
                type("evolves").sub("relation"),
                type("evolves").relates("from").relates("to"),
                type("evolves-final").sub("evolves"),
                type("evolves-final").relates("from-final", "from"),
                type("pokemon").plays("evolves-final", "from-final", "from")
        );
        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDefineQuery_ResultIsSameAsJavaTypeQL() {
        final String query = "define\n" +
                "pokemon sub entity;\n" +
                "evolution sub relation;\n" +
                "evolves-from sub role;\n" +
                "evolves-to sub role;\n" +
                "evolves relates from,\n" +
                "    relates to;\n" +
                "pokemon plays evolves:from,\n" +
                "    plays evolves:to,\n" +
                "    owns name;";
        TypeQLDefine parsed = TypeQL.parseQuery(query).asDefine();

        TypeQLDefine expected = define(
                type("pokemon").sub("entity"),
                type("evolution").sub("relation"),
                type("evolves-from").sub("role"),
                type("evolves-to").sub("role"),
                type("evolves").relates("from").relates("to"),
                type("pokemon").plays("evolves", "from").plays("evolves", "to").owns("name")
        );

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingUndefineQuery_ResultIsSameAsJavaTypeQL() {
        final String query = "undefine\n" +
                "pokemon sub entity;\n" +
                "evolution sub relation;\n" +
                "evolves-from sub role;\n" +
                "evolves-to sub role;\n" +
                "evolves relates from,\n" +
                "    relates to;\n" +
                "pokemon plays evolves:from,\n" +
                "    plays evolves:to,\n" +
                "    owns name;";
        TypeQLUndefine parsed = TypeQL.parseQuery(query).asUndefine();

        TypeQLUndefine expected = undefine(
                type("pokemon").sub("entity"),
                type("evolution").sub("relation"),
                type("evolves-from").sub("role"),
                type("evolves-to").sub("role"),
                type("evolves").relates("from").relates("to"),
                type("pokemon").plays("evolves", "from").plays("evolves", "to").owns("name")
        );

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testMatchInsertQuery() {
        final String query = "match\n" +
                "$x isa language;\n" +
                "insert\n$x has name \"HELLO\";";
        TypeQLInsert parsed = TypeQL.parseQuery(query).asInsert();
        TypeQLInsert expected = match(cVar("x").isa("language"))
                .insert(cVar("x").has("name", "HELLO"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testDefineAbstractEntityQuery() {
        final String query = "define\n" +
                "concrete-type sub entity;\n" +
                "abstract-type sub entity,\n" +
                "    abstract;";
        TypeQLDefine parsed = TypeQL.parseQuery(query).asDefine();
        TypeQLDefine expected = define(
                type("concrete-type").sub("entity"),
                type("abstract-type").sub("entity").isAbstract()
        );

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testMatchValueTypeQuery() {
        final String query = "match\n$x value double;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(cVar("x").value(TypeQLArg.ValueType.DOUBLE)).get();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testParseWithoutVar() {
        String query = "match\n$_ isa person;";
        assertThrows(() -> TypeQL.parseQuery(query).asGet());
        assertThrows(() -> match(cVar().isa("person")));
    }

    @Test
    public void whenParsingDateKeyword_ParseAsTheCorrectValueType() {
        final String query = "match\n$x value datetime;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(cVar("x").value(TypeQLArg.ValueType.DATETIME)).get();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testDefineValueTypeQuery() {
        final String query = "define\n" +
                "my-type sub attribute,\n" +
                "    value long;";
        TypeQLDefine parsed = TypeQL.parseQuery(query).asDefine();
        TypeQLDefine expected = define(type("my-type").sub("attribute").value(TypeQLArg.ValueType.LONG));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testEscapeString() {
        // ANTLR will see this as a string that looks like:
        // "This has \"double quotes\" and a single-quoted backslash: '\\'"
        final String input = "This has \\\"double quotes\\\" and a single-quoted backslash: \\'\\\\\\'";

        final String query = "insert\n" +
                "$_ isa movie,\n" +
                "    has title \"" + input + "\";";
        TypeQLInsert parsed = TypeQL.parseQuery(query).asInsert();
        TypeQLInsert expected = insert(cVar().isa("movie").has("title", input));

        assertQueryEquals(expected, parsed, query);
    }


    @Test
    public void whenParsingQueryWithComments_TheyAreIgnored() {
        final String query = "match\n" +
                "\n# there's a comment here\n$x isa###WOW HERES ANOTHER###\r\nmovie; count;";
        TypeQLGet.Aggregate parsed = parseQuery(query).asGetAggregate();
        TypeQLGet.Aggregate expected = match(cVar("x").isa("movie")).get().count();

        assertEquals(expected, parsed);
        assertEquals(expected, parseQuery(parsed.toString()));
    }

    @Test
    public void testParsingPattern() {
        final String pattern = "{\n" +
                "    (wife: $a, husband: $b) isa marriage;\n" +
                "    $a has gender 'male';\n" +
                "    $b has gender 'female';\n" +
                "}";
        Pattern parsed = TypeQL.parsePattern(pattern);
        Pattern expected = TypeQL.and(
                rel("wife", TypeQL.cVar("a")).rel("husband", TypeQL.cVar("b")).isa("marriage"),
                cVar("a").has("gender", "male"),
                cVar("b").has("gender", "female")
        );

        assertQueryEquals(expected, parsed, pattern.replace("'", "\""));
    }

    @Test
    public void testDefineRules() {
        final String when =
                "    $x isa person;\n" +
                        "    not {\n" +
                        "        $x has name 'Alice';\n" +
                        "        $x has name 'Bob';\n" +
                        "    };\n" +
                        "    {\n" +
                        "        ($x) isa friendship;\n" +
                        "    } or {\n" +
                        "        ($x) isa employment;\n" +
                        "    };";
        final String then = "$x has is_interesting true;";
        Conjunction<? extends Pattern> whenPattern = and(
                cVar("x").isa("person"),
                not(and(cVar("x").has("name", "Alice"), cVar("x").has("name", "Bob"))),
                or(rel(cVar("x")).isa("friendship"), rel(cVar("x")).isa("employment"))
        );
        ThingVariable<?> thenPattern = cVar("x").has("is_interesting", true);

        TypeQLDefine expected = define(rule("interesting-friendships").when(whenPattern).then(thenPattern));
        final String query = "define\n" +
                "rule interesting-friendships: when {\n" +
                when + "\n" +
                "} then {\n" +
                "    " + then + "\n" +
                "};";
        TypeQLDefine parsed = TypeQL.parseQuery(query).asDefine();

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testRuleAttachAttributeByValue() {
        com.vaticle.typeql.lang.pattern.variable.builder.Expression a = cVar("a");
        com.vaticle.typeql.lang.pattern.variable.builder.Expression b = Expression.constant(365);
        Conjunction<? extends Pattern> whenPattern = and(
                cVar("x").has("age", cVar("a")),
                vVar("d").assign(a.mul(b))
        );
        ThingVariable<?> thenPattern = cVar("x").has("days", vVar("d"));
        TypeQLDefine expected = define(rule("attach-val").when(whenPattern).then(thenPattern));

        final String query = "define\n" +
                "rule attach-val: when {\n" +
                "    $x has age $a;\n" +
                "    ?d = $a * 365;\n" +
                "} then {\n" +
                "    $x has days == ?d;\n" +
                "};";
        TypeQLDefine parsed = TypeQL.parseQuery(query).asDefine();

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testParseBoolean() {
        final String query = "insert\n$_ has flag true;";
        TypeQLInsert parsed = TypeQL.parseQuery(query).asInsert();
        TypeQLInsert expected = insert(cVar().has("flag", true));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testParseAggregateGroup() {
        final String query = "match\n" +
                "$x isa movie;\n" +
                "group $x;";
        TypeQLGet.Group parsed = parseQuery(query).asGetGroup();
        TypeQLGet.Group expected = match(cVar("x").isa("movie")).get().group(cVar("x"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testParseAggregateGroupCount() {
        final String query = "match\n" +
                "$x isa movie;\n" +
                "group $x; count;";
        TypeQLGet.Group.Aggregate parsed = parseQuery(query).asGetGroupAggregate();
        TypeQLGet.Group.Aggregate expected = match(cVar("x").isa("movie")).get().group(cVar("x")).count();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testParseAggregateStd() {
        final String query = "match\n" +
                "$x isa movie;\n" +
                "std $x;";
        TypeQLGet.Aggregate parsed = parseQuery(query).asGetAggregate();
        TypeQLGet.Aggregate expected = match(cVar("x").isa("movie")).get().std(cVar("x"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testParseAggregateToString() {
        final String query = "match\n" +
                "$x isa movie;\n" +
                "get $x;\n" +
                "group $x; count;";
        assertEquals(query, parseQuery(query).toString());
    }

    // ===============================================================================================================//


    @Test
    public void whenParseIncorrectSyntax_ThrowTypeQLSyntaxExceptionWithHelpfulError() {
        exception.expect(TypeQLException.class);
        exception.expectMessage(allOf(
                containsString("syntax error"), containsString("line 2"),
                containsString("\n$x isa"),
                containsString("\n      ^")
        ));
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match\n$x isa");
    }

    @Test
    public void whenParseIncorrectSyntax_TrailingQueryWhitespaceIsIgnored() {
        exception.expect(TypeQLException.class);
        exception.expectMessage(allOf(
                containsString("syntax error"),
                containsString("line 2"),
                containsString("\n$x isa"),
                containsString("\n      ^")
        ));
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match\n$x isa \n");
    }

    @Test
    public void whenParseIncorrectSyntax_ErrorMessageShouldRetainWhitespace() {
        exception.expect(TypeQLException.class);
        exception.expectMessage(Matchers.not(containsString("match$xisa")));
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match\n$x isa ");
    }

    @Test
    public void testSyntaxErrorPointer() {
        exception.expect(TypeQLException.class);
        exception.expectMessage(allOf(
                containsString("\n$x of"),
                containsString("\n   ^")
        ));
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match\n$x of");
    }

    @Test
    public void testHasVariable() {
        final String query = "match\n" +
                "$_ has title 'Godfather',\n" +
                "    has tmdb-vote-count $x;";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(cVar().has("title", "Godfather").has("tmdb-vote-count", cVar("x"))).get();

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testRegexAttributeType() {
        final String query = "match\n" +
                "$x regex '(fe)?male';";
        TypeQLGet parsed = TypeQL.parseQuery(query).asGet();
        TypeQLGet expected = match(cVar("x").regex("(fe)?male")).get();

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testTypeQLParseQuery() {
        assertTrue(parseQuery("match\n$x isa movie;") instanceof TypeQLGet);
    }

    @Test
    public void testParseAnnotations() {
        final String defineString = "define\n" +
                "e1 owns a1 @key;\n" +
                "e2 owns a2 @unique;";
        assertEquals(
                TypeQL.define(
                        type("e1").owns("a1", TypeQLToken.Annotation.KEY),
                        type("e2").owns("a2", TypeQLToken.Annotation.UNIQUE)),
                parseQuery(defineString));
    }

    @Test
    public void testParseEmptyString() {
        exception.expect(TypeQLException.class);
        TypeQL.parseQuery("");
    }

    @Test
    public void testParseListOneMatch() {
        final String getString = "match\n$y isa movie;";
        List<TypeQLQuery> queries = TypeQL.parseQueries(getString).collect(toList());

        assertEquals(list(match(cVar("y").isa("movie"))), queries);
    }

    @Test
    public void testParseListOneInsert() {
        final String insertString = "insert\n$x isa movie;";
        List<TypeQLQuery> queries = TypeQL.parseQueries(insertString).collect(toList());

        assertEquals(list(insert(cVar("x").isa("movie"))), queries);
    }

    @Test
    public void testParseListOneInsertWithWhitespacePrefix() {
        final String insertString = " insert $x isa movie;";
        List<TypeQLQuery> queries = TypeQL.parseQueries(insertString).collect(toList());

        assertEquals(list(insert(cVar("x").isa("movie"))), queries);
    }

    @Test
    public void testParseListOneInsertWithPrefixComment() {
        final String insertString = "#hola\ninsert $x isa movie;";
        List<TypeQLQuery> queries = TypeQL.parseQueries(insertString).collect(toList());

        assertEquals(list(insert(cVar("x").isa("movie"))), queries);
    }

    @Test
    public void testParseList() {
        final String insertString = "insert\n$x isa movie;";
        final String getString = "match\n$y isa movie;";
        List<TypeQLQuery> queries = TypeQL.parseQueries(insertString + getString).collect(toList());

        assertEquals(list(insert(cVar("x").isa("movie")), match(cVar("y").isa("movie"))), queries);
    }

    @Test
    public void testParseManyMatchInsertWithoutStackOverflow() {
        final int numQueries = 10_000;
        final String matchInsertString = "match\n$x isa person; insert $x has name 'bob';\n";
        StringBuilder longQuery = new StringBuilder();
        for (int i = 0; i < numQueries; i++) {
            longQuery.append(matchInsertString);
        }

        TypeQLInsert matchInsert = match(cVar("x").isa("person")).insert(cVar("x").has("name", "bob"));
        List<TypeQLInsert> queries = TypeQL.<TypeQLInsert>parseQueries(longQuery.toString()).collect(toList());

        assertEquals(Collections.nCopies(numQueries, matchInsert), queries);
    }

    @Test
    public void whenParsingAListOfQueriesWithASyntaxError_ReportError() {
        final String queryText = "define\nperson sub entity has name;"; // note no semicolon

        exception.expect(TypeQLException.class);
        exception.expectMessage("\nperson sub entity has name;"); // Message should refer to line

        //noinspection ResultOfMethodCallIgnored
        TypeQL.parseQuery(queryText);
    }

    @SuppressWarnings("CheckReturnValue")
    @Test(expected = TypeQLException.class)
    public void whenParsingMultipleQueriesLikeOne_Throw() {
        //noinspection ResultOfMethodCallIgnored
        parseQuery("insert\n$x isa movie; insert $y isa movie");
    }

    @Test
    public void testMissingColon() {
        exception.expect(TypeQLException.class);
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match\n(actor $x, $y) isa has-cast;");
    }

    @Test
    public void testMissingComma() {
        exception.expect(TypeQLException.class);
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match\n($x $y) isa has-cast;");
    }

    @Test
    public void testLimitMistake() {
        exception.expect(TypeQLException.class);
        exception.expectMessage("limit1");
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match\n($x, $y); limit1;");
    }

    @Test
    public void whenParsingAggregateWithWrongVariableArgumentNumber_Throw() {
        exception.expect(TypeQLException.class);
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match\n$x isa name; group;");
    }

    @Test
    public void whenParsingAggregateWithWrongName_Throw() {
        exception.expect(TypeQLException.class);
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match\n$x isa name; hello $x;");
    }

    @Test
    public void defineAttributeTypeRegex() {
        final String query = "define\n" +
                "digit sub attribute,\n" +
                "    regex '\\d';";
        TypeQLDefine parsed = parseQuery(query);
        TypeQLDefine expected = define(type("digit").sub("attribute").regex("\\d"));
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void undefineAttributeTypeRegex() {
        final String query = "undefine\ndigit regex '\\d';";
        TypeQLUndefine parsed = parseQuery(query);
        TypeQLUndefine expected = undefine(type("digit").regex("\\d"));
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void regexPredicateParsesCharacterClassesCorrectly() {
        final String query = "match\n$x like '\\d';";
        TypeQLGet parsed = parseQuery(query);
        TypeQLGet expected = match(cVar("x").like("\\d")).get();
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void regexPredicateParsesQuotesCorrectly() {
        final String query = "match\n$x like '\\\"';";
        TypeQLGet parsed = parseQuery(query);
        TypeQLGet expected = match(cVar("x").like("\\\"")).get();
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void regexPredicateParsesBackslashesCorrectly() {
        final String query = "match\n$x like '\\\\';";
        TypeQLGet parsed = parseQuery(query);
        TypeQLGet expected = match(cVar("x").like("\\\\")).get();
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void regexPredicateParsesNewlineCorrectly() {
        final String query = "match\n$x like '\\n';";
        TypeQLGet parsed = parseQuery(query);
        TypeQLGet expected = match(cVar("x").like("\\n")).get();
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void regexPredicateParsesForwardSlashesCorrectly() {
        final String query = "match\n$x like '\\/';";
        TypeQLGet parsed = parseQuery(query);
        TypeQLGet expected = match(cVar("x").like("/")).get();
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void whenValueEqualityToString_CreateValidQueryString() {
        TypeQLGet expected = match(cVar("x").eq(cVar("y"))).get();
        TypeQLGet parsed = TypeQL.parseQuery(expected.toString());
        assertEquals(expected, parsed);
    }

    private static void assertParseEquivalence(String query) {
        assertEquals(query, parseQuery(query).toString());
    }
}
