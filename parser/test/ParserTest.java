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

package com.vaticle.typeql.lang.parser.test;

import com.vaticle.typeql.lang.TypeQL;
import com.vaticle.typeql.lang.common.TypeQLArg;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.Conjunction;
import com.vaticle.typeql.lang.pattern.Pattern;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;
import com.vaticle.typeql.lang.query.TypeQLCompute;
import com.vaticle.typeql.lang.query.TypeQLDefine;
import com.vaticle.typeql.lang.query.TypeQLDelete;
import com.vaticle.typeql.lang.query.TypeQLInsert;
import com.vaticle.typeql.lang.query.TypeQLMatch;
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
import static com.vaticle.typeql.lang.TypeQL.and;
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
import static com.vaticle.typeql.lang.TypeQL.var;
import static com.vaticle.typeql.lang.common.TypeQLArg.Algorithm.CONNECTED_COMPONENT;
import static com.vaticle.typeql.lang.common.TypeQLArg.Algorithm.K_CORE;
import static com.vaticle.typeql.lang.query.TypeQLCompute.Argument.k;
import static com.vaticle.typeql.lang.query.TypeQLCompute.Argument.size;
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
        } catch (Throwable e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSimpleQuery() {
        final String query = "match $x isa movie;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(var("x").isa("movie"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testNamedTypeVariable() {
        String query = "match $a type attribute_label; get $a;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(var("a").type("attribute_label")).get("a");

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testParseStringWithSlash() {
        final String query = "match $x isa person, has name 'alice/bob';";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(var("x").isa("person").has("name", "alice/bob"));

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testRelationQuery() {
        final String query = "match\n" +
                "$brando 'Marl B' isa name;\n" +
                "(actor: $brando, $char, production-with-cast: $prod);\n" +
                "get $char, $prod;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();

        TypeQLMatch expected = match(
                var("brando").eq("Marl B").isa("name"),
                rel("actor", "brando").rel("char").rel("production-with-cast", "prod")
        ).get("char", "prod");

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testRoleTypeScopedGlobally() {
        String query = "match $x relates spouse;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(var("x").relates("spouse"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testRoleTypeScopedSpecifically() {
        String query = "match $m relates spouse;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(var("m").relates("spouse"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testRoleTypeNotScoped() {
        String query = "match marriage relates $s;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(type("marriage").relates(var("s")));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testPredicateQuery1() {
        final String query = "match\n" +
                "$x isa movie, has title $t;\n" +
                "{ $t 'Apocalypse Now'; } or { $t < 'Juno'; $t > 'Godfather'; } or { $t 'Spy'; };\n" +
                "$t != 'Apocalypse Now';";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();

        TypeQLMatch expected = match(
                var("x").isa("movie").has("title", var("t")),
                or(
                        var("t").eq("Apocalypse Now"),
                        and(
                                var("t").lt("Juno"),
                                var("t").gt("Godfather")
                        ),
                        var("t").eq("Spy")
                ),
                var("t").neq("Apocalypse Now")
        );

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testPredicateQuery2() {
        final String query = "match\n" +
                "$x isa movie, has title $t;\n" +
                "{ $t <= 'Juno'; $t >= 'Godfather'; $t != 'Heat'; } or { $t 'The Muppets'; };";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();

        TypeQLMatch expected = match(
                var("x").isa("movie").has("title", var("t")),
                or(
                        and(
                                var("t").lte("Juno"),
                                var("t").gte("Godfather"),
                                var("t").neq("Heat")
                        ),
                        var("t").eq("The Muppets")
                )
        );

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testPredicateQuery3() {
        final String query = "match\n" +
                "($x, $y);\n" +
                "$y isa person, has name $n;\n" +
                "{ $n contains 'ar'; } or { $n like '^M.*$'; };";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();

        TypeQLMatch expected = match(
                rel("x").rel("y"),
                var("y").isa("person").has("name", var("n")),
                or(var("n").contains("ar"), var("n").like("^M.*$"))
        );

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testPredicateQuery4() {
        final String query = "match\n" +
                "$x has age $y;\n" +
                "$y >= $z;\n" +
                "$z 18 isa age;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();

        TypeQLMatch expected = match(
                var("x").has("age", var("y")),
                var("y").gte(var("z")),
                var("z").eq(18).isa("age")
        );

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
        TypeQLMatch parsed = TypeQL.parseQuery(query);

        TypeQLMatch exepcted = match(
                var("x").sub(var("z")), var("y").sub(var("z")),
                var("a").isa(var("x")), var("b").isa(var("y")),
                not(var("x").is("y")),
                not(var("a").is("b"))
        );

        assertQueryEquals(exepcted, parsed, query);
    }

    @Test
    public void testValueEqualsVariableQuery() {
        final String query = "match $s1 = $s2;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(var("s1").eq(var("s2")));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testMoviesReleasedAfterOrAtTheSameTimeAsSpy() {
        final String query = "match\n" +
                "$x has release-date >= $r;\n" +
                "$_ has title 'Spy', has release-date $r;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();

        TypeQLMatch expected = match(
                var("x").has("release-date", gte(var("r"))),
                var().has("title", "Spy").has("release-date", var("r"))
        );

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testPredicates() {
        final String query = "match $x has release-date < 1986-03-03T00:00, has tmdb-vote-count 100, has tmdb-vote-average <= 9.0;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();

        TypeQLMatch expected = match(
                var("x")
                        .has("release-date", lt(LocalDate.of(1986, 3, 3).atStartOfDay()))
                        .has("tmdb-vote-count", 100)
                        .has("tmdb-vote-average", lte(9.0))
        );

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDate_HandleTime() {
        final String query = "match $x has release-date 1000-11-12T13:14:15;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(var("x").has("release-date", LocalDateTime.of(1000, 11, 12, 13, 14, 15)));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDate_HandleBigYears() {
        TypeQLMatch expected = match(var("x").has("release-date", LocalDate.of(12345, 12, 25).atStartOfDay()));
        final String query = "match $x has release-date +12345-12-25T00:00;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDate_HandleSmallYears() {
        final String query = "match $x has release-date 0867-01-01T00:00;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(var("x").has("release-date", LocalDate.of(867, 1, 1).atStartOfDay()));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDate_HandleNegativeYears() {
        final String query = "match $x has release-date -3200-01-01T00:00;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(var("x").has("release-date", LocalDate.of(-3200, 1, 1).atStartOfDay()));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDate_HandleMillis() {
        final String query = "match $x has release-date 1000-11-12T13:14:15.123;";
        TypeQLMatch expected = match(var("x").has("release-date", LocalDateTime.of(1000, 11, 12, 13, 14, 15, 123000000)));
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDate_HandleMillisShorthand() {
        final String query = "match $x has release-date 1000-11-12T13:14:15.1;";
        final String parsedQueryString = "match $x has release-date 1000-11-12T13:14:15.100;";
        TypeQLMatch expected = match(var("x").has("release-date", LocalDateTime.of(1000, 11, 12, 13, 14, 15, 100000000)));
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        assertQueryEquals(expected, parsed, parsedQueryString);
    }


    @Test
    public void whenParsingDate_ErrorWhenHandlingOverPreciseDecimalSeconds() {
        final String query = "match $x has release-date 1000-11-12T13:14:15.000123456;";
        exception.expect(TypeQLException.class);
        exception.expectMessage(Matchers.containsString("no viable alternative"));
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
    }

    @Test
    public void whenParsingDateTime_ErrorWhenHandlingOverPreciseNanos() {
        exception.expect(TypeQLException.class);
        exception.expectMessage(Matchers.containsString("more precise than 1 millisecond"));
        TypeQLMatch apiQuery = match(var("x").has("release-date", LocalDateTime.of(1000, 11, 12, 13, 14, 15, 123450000)));
    }

    @Test
    public void testLongPredicateQuery() {
        final String query = "match $x isa movie, has tmdb-vote-count <= 400;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(var("x").isa("movie").has("tmdb-vote-count", lte(400)));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testSchemaQuery() {
        final String query = "match $x plays starring:actor; sort $x asc;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(var("x").plays("starring", "actor")).sort(list("x"), "asc");

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testGetSort() {
        final String query = "match $x isa movie, has rating $r; sort $r desc;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(
                var("x").isa("movie").has("rating", var("r"))
        ).sort(list("r"), "desc");

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testGetSortLimit() {
        final String query = "match $x isa movie, has rating $r; sort $r; limit 10;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(
                var("x").isa("movie").has("rating", var("r"))
        ).sort("r").limit(10);

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testGetSortOffsetLimit() {
        final String query = "match $x isa movie, has rating $r; sort $r desc; offset 10; limit 10;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(
                var("x").isa("movie").has("rating", var("r"))
        ).sort(list("r"), "desc").offset(10).limit(10);

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testGetOffsetLimit() {
        final String query = "match $y isa movie, has title $n; offset 2; limit 4;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(
                var("y").isa("movie").has("title", var("n"))
        ).offset(2).limit(4);

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
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(
                rel(var("p"), var("x")).rel("y"),
                var("x").isa(var("z")),
                var("y").eq("crime"),
                var("z").sub("production"),
                type("has-genre").relates(var("p"))
        );

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testParseRelatesTypeVariable() {
        final String query = "match\n" +
                "$x isa $type;\n" +
                "$type relates someRole;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(var("x").isa(var("type")), var("type").relates("someRole"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testOrQuery() {
        final String query = "match\n" +
                "$x isa movie;\n" +
                "{ $y 'drama' isa genre; ($x, $y); } or { $x 'The Muppets'; };";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(
                var("x").isa("movie"),
                or(
                        and(
                                var("y").eq("drama").isa("genre"),
                                rel("x").rel("y")
                        ),
                        var("x").eq("The Muppets")
                )
        );

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testDisjunctionNotInConjunction() {
        String query = "match {$x isa person;} or {$x isa company;};";
        assertThrows(() -> parseQuery(query).asMatch());
    }


    @Test
    public void testNestedConjunctionAndDisjunction() {
        String query = "match\n" +
                "$y isa $p;\n" +
                "{ ($y, $q); } or { $x isa $p; { $x has first-name $y; } or { $x has last-name $z; }; };";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(
                var("y").isa(var("p")),
                or(rel("y").rel("q"),
                   and(var("x").isa(var("p")),
                       or(var("x").has("first-name", var("y")),
                          var("x").has("last-name", var("z"))))));
        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testDisjunctionNotBindingConjunction() {
        String query = "match\n" +
                "$y isa $p;\n" +
                "{ ($y, $q); } or { $x isa $p; { $x has first-name $y; } or { $q has last-name $z; }; };";
        assertThrows(() -> parseQuery(query).asMatch());
    }

    @Test
    public void testAggregateCountQuery() {
        final String query = "match ($x, $y) isa friendship; get $x, $y; count;";
        TypeQLMatch.Aggregate parsed = parseQuery(query).asMatchAggregate();
        TypeQLMatch.Aggregate expected = match(rel("x").rel("y").isa("friendship")).get("x", "y").count();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testAggregateGroupCountQuery() {
        final String query = "match ($x, $y) isa friendship; get $x, $y; group $x; count;";
        TypeQLMatch.Group.Aggregate parsed = parseQuery(query).asMatchGroupAggregate();
        TypeQLMatch.Group.Aggregate expected = match(rel("x").rel("y").isa("friendship")).get("x", "y").group("x").count();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testSingleLineGroupAggregateMaxQuery() {
        final String query = "match $x has age $a; group $x; max $a;";
        TypeQLMatch.Group.Aggregate parsed = parseQuery(query).asMatchGroupAggregate();
        TypeQLMatch.Group.Aggregate expected = match(var("x").has("age", var("a"))).group("x").max("a");

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testMultiLineGroupAggregateMaxQuery() {
        final String query = "match\n" +
                "($x, $y) isa friendship;\n" +
                "$y has age $z;\n" +
                "group $x; max $z;";
        TypeQLMatch.Group.Aggregate parsed = parseQuery(query).asMatchGroupAggregate();
        TypeQLMatch.Group.Aggregate expected = match(
                rel("x").rel("y").isa("friendship"),
                var("y").has("age", var("z"))
        ).group("x").max("z");

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testMultiLineFilteredGroupAggregateMaxQuery() {
        final String query = "match\n" +
                "($x, $y) isa friendship;\n" +
                "$y has age $z;\n" +
                "get $x, $y, $z; group $x; max $z;";
        TypeQLMatch.Group.Aggregate parsed = parseQuery(query).asMatchGroupAggregate();
        TypeQLMatch.Group.Aggregate expected = match(
                rel("x").rel("y").isa("friendship"),
                var("y").has("age", var("z"))
        ).get("x", "y", "z").group("x").max("z");

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenComparingCountQueryUsingTypeQLAndJavaTypeQL_TheyAreEquivalent() {
        final String query = "match $x isa movie, has title \"Godfather\"; count;";
        TypeQLMatch.Aggregate parsed = parseQuery(query).asMatchAggregate();
        TypeQLMatch.Aggregate expected = match(var("x").isa("movie").has("title", "Godfather")).count();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testInsertQuery() {
        final String query = "insert $_ isa movie, has title \"The Title\";";
        TypeQLInsert parsed = TypeQL.parseQuery(query).asInsert();
        TypeQLInsert expected = insert(var().isa("movie").has("title", "The Title"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDeleteQuery_ResultIsSameAsJavaTypeQL() {
        final String query = "match\n" +
                "$x isa movie, has title 'The Title';\n" +
                "$y isa movie;\n" +
                "delete\n" +
                "$x isa movie;\n" +
                "$y isa movie;";
        TypeQLDelete parsed = TypeQL.parseQuery(query).asDelete();
        TypeQLDelete expected = match(
                var("x").isa("movie").has("title", "The Title"),
                var("y").isa("movie")
        ).delete(var("x").isa("movie"), var("y").isa("movie"));

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void whenParsingInsertQuery_ResultIsSameAsJavaTypeQL() {
        final String query = "insert\n" +
                "$x isa pokemon, has name 'Pichu';\n" +
                "$y isa pokemon, has name 'Pikachu';\n" +
                "$z isa pokemon, has name 'Raichu';\n" +
                "(evolves-from: $x, evolves-to: $y) isa evolution;\n" +
                "(evolves-from: $y, evolves-to: $z) isa evolution;";
        TypeQLInsert parsed = TypeQL.parseQuery(query).asInsert();
        TypeQLInsert expected = insert(
                var("x").isa("pokemon").has("name", "Pichu"),
                var("y").isa("pokemon").has("name", "Pikachu"),
                var("z").isa("pokemon").has("name", "Raichu"),
                rel("evolves-from", "x").rel("evolves-to", "y").isa("evolution"),
                rel("evolves-from", "y").rel("evolves-to", "z").isa("evolution")
        );

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void whenParsingUpdateQuery_ResultIssameAsJavaTypeQL() {
        String query = "match $x isa person, has name 'alice', has age $a;\n" +
                "delete $x has $a;\n" +
                "insert $x has age 25;";
        TypeQLUpdate parsed = TypeQL.parseQuery(query).asUpdate();
        TypeQLUpdate expected = match(var("x").isa("person").has("name", "alice").has("age", var("a")))
                .delete(var("x").has(var("a")))
                .insert(var("x").has("age", 25));
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void whenParsingAsInDefine_ResultIsSameAsSub() {
        final String query = "define\n" +
                "parent sub role;\n" +
                "child sub role;\n" +
                "parenthood sub relation, relates parent, relates child;\n" +
                "fatherhood sub parenthood, relates father as parent, relates son as child;";
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
        final String query = "match $f sub parenthood, relates father as parent, relates son as child;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(
                var("f").sub("parenthood")
                        .relates("father", "parent")
                        .relates("son", "child")
        );

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
                "evolves relates from, relates to;\n" +
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
                "evolves relates from, relates to;\n" +
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
                "evolves relates from, relates to;\n" +
                "pokemon plays evolves:from, plays evolves:to, owns name;";
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
                "evolves relates from, relates to;\n" +
                "pokemon plays evolves:from, plays evolves:to, owns name;";
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
        final String query = "match $x isa language;\n" +
                "insert $x has name \"HELLO\";";
        TypeQLInsert parsed = TypeQL.parseQuery(query).asInsert();
        TypeQLInsert expected = match(var("x").isa("language"))
                .insert(var("x").has("name", "HELLO"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testDefineAbstractEntityQuery() {
        final String query = "define\n" +
                "concrete-type sub entity;\n" +
                "abstract-type sub entity, abstract;";
        TypeQLDefine parsed = TypeQL.parseQuery(query).asDefine();
        TypeQLDefine expected = define(
                type("concrete-type").sub("entity"),
                type("abstract-type").sub("entity").isAbstract()
        );

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testMatchValueTypeQuery() {
        final String query = "match $x value double;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(var("x").value(TypeQLArg.ValueType.DOUBLE));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testParseWithoutVar() {
        String query = "match $_ isa person;";
        assertThrows(() -> TypeQL.parseQuery(query).asMatch());
        assertThrows(() -> match(var().isa("person")));
    }

    @Test
    public void whenParsingDateKeyword_ParseAsTheCorrectValueType() {
        final String query = "match $x value datetime;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(var("x").value(TypeQLArg.ValueType.DATETIME));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testDefineValueTypeQuery() {
        final String query = "define my-type sub attribute, value long;";
        TypeQLDefine parsed = TypeQL.parseQuery(query).asDefine();
        TypeQLDefine expected = define(type("my-type").sub("attribute").value(TypeQLArg.ValueType.LONG));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testEscapeString() {
        // ANTLR will see this as a string that looks like:
        // "This has \"double quotes\" and a single-quoted backslash: '\\'"
        final String input = "This has \\\"double quotes\\\" and a single-quoted backslash: \\'\\\\\\'";

        final String query = "insert $_ isa movie, has title \"" + input + "\";";
        TypeQLInsert parsed = TypeQL.parseQuery(query).asInsert();
        TypeQLInsert expected = insert(var().isa("movie").has("title", input));

        assertQueryEquals(expected, parsed, query);
    }


    @Test
    public void whenParsingQueryWithComments_TheyAreIgnored() {
        final String query = "match \n# there's a comment here\n$x isa###WOW HERES ANOTHER###\r\nmovie; count;";
        TypeQLMatch.Aggregate parsed = parseQuery(query).asMatchAggregate();
        TypeQLMatch.Aggregate expected = match(var("x").isa("movie")).count();

        assertEquals(expected, parsed);
        assertEquals(expected, parseQuery(parsed.toString()));
    }

    @Test
    public void testParsingPattern() {
        final String pattern = "{ (wife: $a, husband: $b) isa marriage; $a has gender 'male'; $b has gender 'female'; }";
        Pattern parsed = TypeQL.parsePattern(pattern);
        Pattern expected = TypeQL.and(
                rel("wife", "a").rel("husband", "b").isa("marriage"),
                var("a").has("gender", "male"),
                var("b").has("gender", "female")
        );

        assertQueryEquals(expected, parsed, pattern.replace("'", "\""));
    }

    @Test
    public void testDefineRules() {
        final String when = "$x isa movie;";
        final String then = "$x has genre 'drama';";
        Conjunction<? extends Pattern> whenPattern = and((var("x").isa("movie")));
        ThingVariable<?> thenPattern = var("x").has("genre", "drama");

        TypeQLDefine expected = define(rule("all-movies-are-drama").when(whenPattern).then(thenPattern));
        final String query = "define rule all-movies-are-drama: when { " + when + " } then { " + then + " };";
        TypeQLDefine parsed = TypeQL.parseQuery(query).asDefine();

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testQueryParserWithoutGraph() {
        final String queryString = "match $x isa movie; get $x;";
        TypeQLMatch query = parseQuery("match $x isa movie; get $x;").asMatch();
        assertEquals(queryString, query.toString());
    }

    @Test
    public void testParseBoolean() {
        final String query = "insert $_ has flag true;";
        TypeQLInsert parsed = TypeQL.parseQuery(query).asInsert();
        TypeQLInsert expected = insert(var().has("flag", true));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testParseAggregateGroup() {
        final String query = "match $x isa movie; group $x;";
        TypeQLMatch.Group parsed = parseQuery(query).asMatchGroup();
        TypeQLMatch.Group expected = match(var("x").isa("movie")).group("x");

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testParseAggregateGroupCount() {
        final String query = "match $x isa movie; group $x; count;";
        TypeQLMatch.Group.Aggregate parsed = parseQuery(query).asMatchGroupAggregate();
        TypeQLMatch.Group.Aggregate expected = match(var("x").isa("movie")).group("x").count();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testParseAggregateStd() {
        final String query = "match $x isa movie; std $x;";
        TypeQLMatch.Aggregate parsed = parseQuery(query).asMatchAggregate();
        TypeQLMatch.Aggregate expected = match(var("x").isa("movie")).std("x");

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testParseAggregateToString() {
        final String query = "match $x isa movie; get $x; group $x; count;";
        assertEquals(query, parseQuery(query).toString());
    }

    // ===============================================================================================================//
    // Test TypeQL Compute queries
    // ===============================================================================================================//
    @Test
    public void testParseComputeCount() {
        assertParseEquivalence("compute count;");
    }

    @Test
    public void testParseComputeCountWithSubgraph() {
        assertParseEquivalence("compute count in [movie, person];");
    }

    @Test
    public void testParseComputeClusterUsingCC() {
        assertParseEquivalence("compute cluster in [movie, person], using connected-component;");
    }

    @Test
    public void testParseComputeClusterUsingCCWithSize() {
        TypeQLCompute expected = TypeQL.compute().cluster().using(CONNECTED_COMPONENT).in("movie", "person").where(size(10));
        TypeQLCompute parsed = TypeQL.parseQuery(
                "compute cluster in [movie, person], using connected-component, where [size = 10];").asComputeCluster();

        assertEquals(expected, parsed);
    }

    @Test
    public void testParseComputeClusterUsingCCWithSizeTwice() {
        TypeQLCompute expected =
                TypeQL.compute().cluster().using(CONNECTED_COMPONENT).in("movie", "person").where(size(10), size(15));

        TypeQLCompute parsed = TypeQL.parseQuery(
                "compute cluster in [movie, person], using connected-component, where [size = 10, size = 15];").asComputeCluster();

        assertEquals(expected, parsed);
    }

    @Test
    public void testParseComputeClusterUsingKCore() {
        assertParseEquivalence("compute cluster in [movie, person], using k-core;");
    }

    @Test
    public void testParseComputeClusterUsingKCoreWithK() {
        TypeQLCompute expected = TypeQL.compute().cluster().using(K_CORE).in("movie", "person").where(k(10));
        TypeQLCompute parsed = TypeQL.parseQuery(
                "compute cluster in [movie, person], using k-core, where k = 10;").asComputeCluster();

        assertEquals(expected, parsed);
    }

    @Test
    public void testParseComputeClusterUsingKCoreWithKTwice() {
        TypeQLCompute expected = TypeQL.compute().cluster().using(K_CORE).in("movie", "person").where(k(10));
        TypeQLCompute parsed = TypeQL.parseQuery(
                "compute cluster in [movie, person], using k-core, where [k = 5, k = 10];").asComputeCluster();

        assertEquals(expected, parsed);
    }

    @Test
    public void testParseComputeDegree() {
        assertParseEquivalence("compute centrality in movie, using degree;");
    }

    @Test
    public void testParseComputeCoreness() {
        assertParseEquivalence("compute centrality in movie, using k-core, where min-k=3;");
    }

    @Test
    public void testParseComputeMax() {
        assertParseEquivalence("compute max of person, in movie;");
    }

    @Test
    public void testParseComputeMean() {
        assertParseEquivalence("compute mean of person, in movie;");
    }

    @Test
    public void testParseComputeMedian() {
        assertParseEquivalence("compute median of person, in movie;");
    }

    @Test
    public void testParseComputeMin() {
        assertParseEquivalence("compute min of movie, in person;");
    }

    @Test
    public void testParseComputePath() {
        assertParseEquivalence("compute path from 0x83cb2, to 0x4ba92, in person;");
    }

    @Test
    public void testParseComputePathWithMultipleInTypes() {
        assertParseEquivalence("compute path from 0x83cb2, to 0x4ba92, in [person, marriage];");
    }

    @Test
    public void testParseComputeStd() {
        assertParseEquivalence("compute std of movie;");
    }

    @Test
    public void testParseComputeSum() {
        assertParseEquivalence("compute sum of movie, in person;");
    }

    // ===============================================================================================================//


    @Test
    public void whenParseIncorrectSyntax_ThrowTypeQLSyntaxExceptionWithHelpfulError() {
        exception.expect(TypeQLException.class);
        exception.expectMessage(allOf(
                containsString("syntax error"), containsString("line 1"),
                containsString("\nmatch $x isa"),
                containsString("\n            ^")
        ));
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match $x isa");
    }

    @Test
    public void whenParseIncorrectSyntax_TrailingQueryWhitespaceIsIgnored() {
        exception.expect(TypeQLException.class);
        exception.expectMessage(allOf(
                containsString("syntax error"), containsString("line 1"),
                containsString("\nmatch $x isa"),
                containsString("\n            ^")
        ));
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match $x isa \n");
    }

    @Test
    public void whenParseIncorrectSyntax_ErrorMessageShouldRetainWhitespace() {
        exception.expect(TypeQLException.class);
        exception.expectMessage(Matchers.not(containsString("match$xisa")));
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match $x isa ");
    }

    @Test
    public void testSyntaxErrorPointer() {
        exception.expect(TypeQLException.class);
        exception.expectMessage(allOf(
                containsString("\nmatch $x of"),
                containsString("\n         ^")
        ));
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match $x of");
    }

    @Test
    public void testHasVariable() {
        final String query = "match $_ has title 'Godfather', has tmdb-vote-count $x;";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(var().has("title", "Godfather").has("tmdb-vote-count", var("x")));

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testRegexAttributeType() {
        final String query = "match $x regex '(fe)?male';";
        TypeQLMatch parsed = TypeQL.parseQuery(query).asMatch();
        TypeQLMatch expected = match(var("x").regex("(fe)?male"));

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testTypeQLParseQuery() {
        assertTrue(parseQuery("match $x isa movie;") instanceof TypeQLMatch);
    }

    @Test
    public void testParseKey() {
        assertEquals("match $x owns name @key; get $x;", parseQuery("match $x owns name @key; get $x;").toString());
    }

    @Test
    public void testParseEmptyString() {
        exception.expect(TypeQLException.class);
        TypeQL.parseQuery("");
    }

    @Test
    public void testParseListOneMatch() {
        final String getString = "match $y isa movie;";
        List<TypeQLQuery> queries = TypeQL.parseQueries(getString).collect(toList());

        assertEquals(list(match(var("y").isa("movie"))), queries);
    }

    @Test
    public void testParseListOneInsert() {
        final String insertString = "insert $x isa movie;";
        List<TypeQLQuery> queries = TypeQL.parseQueries(insertString).collect(toList());

        assertEquals(list(insert(var("x").isa("movie"))), queries);
    }

    @Test
    public void testParseListOneInsertWithWhitespacePrefix() {
        final String insertString = " insert $x isa movie;";
        List<TypeQLQuery> queries = TypeQL.parseQueries(insertString).collect(toList());

        assertEquals(list(insert(var("x").isa("movie"))), queries);
    }

    @Test
    public void testParseListOneInsertWithPrefixComment() {
        final String insertString = "#hola\ninsert $x isa movie;";
        List<TypeQLQuery> queries = TypeQL.parseQueries(insertString).collect(toList());

        assertEquals(list(insert(var("x").isa("movie"))), queries);
    }

    @Test
    public void testParseList() {
        final String insertString = "insert $x isa movie;";
        final String getString = "match $y isa movie;";
        List<TypeQLQuery> queries = TypeQL.parseQueries(insertString + getString).collect(toList());

        assertEquals(list(insert(var("x").isa("movie")), match(var("y").isa("movie"))), queries);
    }

    @Test
    public void testParseManyMatchInsertWithoutStackOverflow() {
        final int numQueries = 10_000;
        final String matchInsertString = "match $x isa person; insert $x has name 'bob';\n";
        StringBuilder longQuery = new StringBuilder();
        for (int i = 0; i < numQueries; i++) {
            longQuery.append(matchInsertString);
        }

        TypeQLInsert matchInsert = match(var("x").isa("person")).insert(var("x").has("name", "bob"));
        List<TypeQLInsert> queries = TypeQL.<TypeQLInsert>parseQueries(longQuery.toString()).collect(toList());

        assertEquals(Collections.nCopies(numQueries, matchInsert), queries);
    }

    @Test
    public void whenParsingAListOfQueriesWithASyntaxError_ReportError() {
        final String queryText = "define person sub entity has name;"; // note no semicolon

        exception.expect(TypeQLException.class);
        exception.expectMessage("define person sub entity has name;"); // Message should refer to line

        //noinspection ResultOfMethodCallIgnored
        TypeQL.parseQuery(queryText);
    }

    @SuppressWarnings("CheckReturnValue")
    @Test(expected = TypeQLException.class)
    public void whenParsingMultipleQueriesLikeOne_Throw() {
        //noinspection ResultOfMethodCallIgnored
        parseQuery("insert $x isa movie; insert $y isa movie");
    }

    @Test
    public void testMissingColon() {
        exception.expect(TypeQLException.class);
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match (actor $x, $y) isa has-cast;");
    }

    @Test
    public void testMissingComma() {
        exception.expect(TypeQLException.class);
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match ($x $y) isa has-cast;");
    }

    @Test
    public void testLimitMistake() {
        exception.expect(TypeQLException.class);
        exception.expectMessage("limit1");
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match ($x, $y); limit1;");
    }

    @Test
    public void whenParsingAggregateWithWrongVariableArgumentNumber_Throw() {
        exception.expect(TypeQLException.class);
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match $x isa name; group;");
    }

    @Test
    public void whenParsingAggregateWithWrongName_Throw() {
        exception.expect(TypeQLException.class);
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match $x isa name; hello $x;");
    }

    @Test
    public void defineAttributeTypeRegex() {
        final String query = "define digit sub attribute, regex '\\d';";
        TypeQLDefine parsed = parseQuery(query);
        TypeQLDefine expected = define(type("digit").sub("attribute").regex("\\d"));
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void undefineAttributeTypeRegex() {
        final String query = "undefine digit regex '\\d';";
        TypeQLUndefine parsed = parseQuery(query);
        TypeQLUndefine expected = undefine(type("digit").regex("\\d"));
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void regexPredicateParsesCharacterClassesCorrectly() {
        final String query = "match $x like '\\d';";
        TypeQLMatch parsed = parseQuery(query);
        TypeQLMatch expected = match(var("x").like("\\d"));
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void regexPredicateParsesQuotesCorrectly() {
        final String query = "match $x like '\\\"';";
        TypeQLMatch parsed = parseQuery(query);
        TypeQLMatch expected = match(var("x").like("\\\""));
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void regexPredicateParsesBackslashesCorrectly() {
        final String query = "match $x like '\\\\';";
        TypeQLMatch parsed = parseQuery(query);
        TypeQLMatch expected = match(var("x").like("\\\\"));
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void regexPredicateParsesNewlineCorrectly() {
        final String query = "match $x like '\\n';";
        TypeQLMatch parsed = parseQuery(query);
        TypeQLMatch expected = match(var("x").like("\\n"));
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void regexPredicateParsesForwardSlashesCorrectly() {
        final String query = "match $x like '\\/';";
        TypeQLMatch parsed = parseQuery(query);
        TypeQLMatch expected = match(var("x").like("/"));
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void whenValueEqualityToString_CreateValidQueryString() {
        TypeQLMatch expected = match(var("x").eq(var("y")));
        TypeQLMatch parsed = TypeQL.parseQuery(expected.toString());
        assertEquals(expected, parsed);
    }

    private static void assertParseEquivalence(String query) {
        assertEquals(query, parseQuery(query).toString());
    }
}