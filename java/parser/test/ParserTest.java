/*
 * Copyright (C) 2021 Grakn Labs
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

package graql.lang.parser.test;

import graql.lang.Graql;
import graql.lang.common.GraqlArg;
import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.Conjunction;
import graql.lang.pattern.Pattern;
import graql.lang.pattern.variable.ThingVariable;
import graql.lang.query.GraqlCompute;
import graql.lang.query.GraqlDefine;
import graql.lang.query.GraqlDelete;
import graql.lang.query.GraqlInsert;
import graql.lang.query.GraqlMatch;
import graql.lang.query.GraqlQuery;
import graql.lang.query.GraqlUndefine;
import graql.lang.query.GraqlUpdate;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static graql.lang.Graql.and;
import static graql.lang.Graql.define;
import static graql.lang.Graql.gte;
import static graql.lang.Graql.insert;
import static graql.lang.Graql.lt;
import static graql.lang.Graql.lte;
import static graql.lang.Graql.match;
import static graql.lang.Graql.not;
import static graql.lang.Graql.or;
import static graql.lang.Graql.parseQuery;
import static graql.lang.Graql.rel;
import static graql.lang.Graql.rule;
import static graql.lang.Graql.type;
import static graql.lang.Graql.undefine;
import static graql.lang.Graql.var;
import static graql.lang.common.GraqlArg.Algorithm.CONNECTED_COMPONENT;
import static graql.lang.common.GraqlArg.Algorithm.K_CORE;
import static graql.lang.query.GraqlCompute.Argument.k;
import static graql.lang.query.GraqlCompute.Argument.size;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ParserTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private void assertQueryEquals(GraqlQuery expected, GraqlQuery parsed, String query) {
        assertEquals(expected, parsed);
        assertEquals(expected, Graql.parseQuery(parsed.toString()));
        assertEquals(query, expected.toString());
    }

    private void assertQueryEquals(Pattern expected, Pattern parsed, String query) {
        assertEquals(expected, parsed);
        assertEquals(expected, Graql.parsePattern(parsed.toString()));
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
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(var("x").isa("movie"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testNamedTypeVariable() {
        String query = "match $a type attribute_label; get $a;";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(var("a").type("attribute_label")).get("a");

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testLabesWithSpecialCharacters() {
        String query = "define col_a+1230b/1100 sub entity;";
        GraqlDefine parsed = Graql.parseQuery(query).asDefine();
        GraqlDefine expected = Graql.define(type("col_a+1230b/1100").sub("entity"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testParseStringWithSlash() {
        final String query = "match $x isa person, has name 'alice/bob';";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(var("x").isa("person").has("name", "alice/bob"));

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testRelationQuery() {
        final String query = "match\n" +
                "$brando 'Marl B' isa name;\n" +
                "(actor: $brando, $char, production-with-cast: $prod);\n" +
                "get $char, $prod;";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();

        GraqlMatch expected = match(
                var("brando").eq("Marl B").isa("name"),
                rel("actor", "brando").rel("char").rel("production-with-cast", "prod")
        ).get("char", "prod");

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testRoleTypeScopedGlobally() {
        String query = "match $x relates spouse;";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(var("x").relates("spouse"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testRoleTypeScopedSpecifically() {
        String query = "match $m relates spouse;";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(var("m").relates("spouse"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testRoleTypeNotScoped() {
        String query = "match marriage relates $s;";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(type("marriage").relates(var("s")));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testPredicateQuery1() {
        final String query = "match\n" +
                "$x isa movie, has title $t;\n" +
                "{ $t 'Apocalypse Now'; } or { $t < 'Juno'; $t > 'Godfather'; } or { $t 'Spy'; };\n" +
                "$t != 'Apocalypse Now';";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();

        GraqlMatch expected = match(
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
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();

        GraqlMatch expected = match(
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
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();

        GraqlMatch expected = match(
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
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();

        GraqlMatch expected = match(
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
        GraqlMatch parsed = Graql.parseQuery(query);

        GraqlMatch exepcted = match(
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
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(var("s1").eq(var("s2")));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testMoviesReleasedAfterOrAtTheSameTimeAsSpy() {
        final String query = "match\n" +
                "$x has release-date >= $r;\n" +
                "$_ has title 'Spy', has release-date $r;";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();

        GraqlMatch expected = match(
                var("x").has("release-date", gte(var("r"))),
                var().has("title", "Spy").has("release-date", var("r"))
        );

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testPredicates() {
        final String query = "match $x has release-date < 1986-03-03T00:00, has tmdb-vote-count 100, has tmdb-vote-average <= 9.0;";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();

        GraqlMatch expected = match(
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
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(var("x").has("release-date", LocalDateTime.of(1000, 11, 12, 13, 14, 15)));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDate_HandleBigYears() {
        GraqlMatch expected = match(var("x").has("release-date", LocalDate.of(12345, 12, 25).atStartOfDay()));
        final String query = "match $x has release-date +12345-12-25T00:00;";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDate_HandleSmallYears() {
        final String query = "match $x has release-date 0867-01-01T00:00;";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(var("x").has("release-date", LocalDate.of(867, 1, 1).atStartOfDay()));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDate_HandleNegativeYears() {
        final String query = "match $x has release-date -3200-01-01T00:00;";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(var("x").has("release-date", LocalDate.of(-3200, 1, 1).atStartOfDay()));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDate_HandleMillis() {
        final String query = "match $x has release-date 1000-11-12T13:14:15.123;";
        GraqlMatch expected = match(var("x").has("release-date", LocalDateTime.of(1000, 11, 12, 13, 14, 15, 123000000)));
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDate_HandleMillisShorthand() {
        final String query = "match $x has release-date 1000-11-12T13:14:15.1;";
        final String parsedQueryString = "match $x has release-date 1000-11-12T13:14:15.100;";
        GraqlMatch expected = match(var("x").has("release-date", LocalDateTime.of(1000, 11, 12, 13, 14, 15, 100000000)));
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        assertQueryEquals(expected, parsed, parsedQueryString);
    }


    @Test
    public void whenParsingDate_ErrorWhenHandlingOverPreciseDecimalSeconds() {
        final String query = "match $x has release-date 1000-11-12T13:14:15.000123456;";
        exception.expect(GraqlException.class);
        exception.expectMessage(Matchers.containsString("no viable alternative"));
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
    }

    @Test
    public void whenParsingDateTime_ErrorWhenHandlingOverPreciseNanos() {
        exception.expect(GraqlException.class);
        exception.expectMessage(Matchers.containsString("more precise than 1 millisecond"));
        GraqlMatch apiQuery = match(var("x").has("release-date", LocalDateTime.of(1000, 11, 12, 13, 14, 15, 123450000)));
    }


    @Test
    public void testLongPredicateQuery() {
        final String query = "match $x isa movie, has tmdb-vote-count <= 400;";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(var("x").isa("movie").has("tmdb-vote-count", lte(400)));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testSchemaQuery() {
        final String query = "match $x plays starring:actor; sort $x asc;";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(var("x").plays("starring", "actor")).sort("x", "asc");

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testGetSort() {
        final String query = "match $x isa movie, has rating $r; sort $r desc;";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(
                var("x").isa("movie").has("rating", var("r"))
        ).sort("r", "desc");

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testGetSortLimit() {
        final String query = "match $x isa movie, has rating $r; sort $r; limit 10;";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(
                var("x").isa("movie").has("rating", var("r"))
        ).sort("r").limit(10);

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testGetSortOffsetLimit() {
        final String query = "match $x isa movie, has rating $r; sort $r desc; offset 10; limit 10;";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(
                var("x").isa("movie").has("rating", var("r"))
        ).sort("r", "desc").offset(10).limit(10);

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testGetOffsetLimit() {
        final String query = "match $y isa movie, has title $n; offset 2; limit 4;";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(
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
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(
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
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(var("x").isa(var("type")), var("type").relates("someRole"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testOrQuery() {
        final String query = "match\n" +
                "$x isa movie;\n" +
                "{ $y 'drama' isa genre; ($x, $y); } or { $x 'The Muppets'; };";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(
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
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(
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
        GraqlMatch.Aggregate parsed = parseQuery(query).asMatchAggregate();
        GraqlMatch.Aggregate expected = match(rel("x").rel("y").isa("friendship")).get("x", "y").count();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testAggregateGroupCountQuery() {
        final String query = "match ($x, $y) isa friendship; get $x, $y; group $x; count;";
        GraqlMatch.Group.Aggregate parsed = parseQuery(query).asMatchGroupAggregate();
        GraqlMatch.Group.Aggregate expected = match(rel("x").rel("y").isa("friendship")).get("x", "y").group("x").count();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testSingleLineGroupAggregateMaxQuery() {
        final String query = "match $x has age $a; group $x; max $a;";
        GraqlMatch.Group.Aggregate parsed = parseQuery(query).asMatchGroupAggregate();
        GraqlMatch.Group.Aggregate expected = match(var("x").has("age", var("a"))).group("x").max("a");

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testMultiLineGroupAggregateMaxQuery() {
        final String query = "match\n" +
                "($x, $y) isa friendship;\n" +
                "$y has age $z;\n" +
                "group $x; max $z;";
        GraqlMatch.Group.Aggregate parsed = parseQuery(query).asMatchGroupAggregate();
        GraqlMatch.Group.Aggregate expected = match(
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
        GraqlMatch.Group.Aggregate parsed = parseQuery(query).asMatchGroupAggregate();
        GraqlMatch.Group.Aggregate expected = match(
                rel("x").rel("y").isa("friendship"),
                var("y").has("age", var("z"))
        ).get("x", "y", "z").group("x").max("z");

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenComparingCountQueryUsingGraqlAndJavaGraql_TheyAreEquivalent() {
        final String query = "match $x isa movie, has title \"Godfather\"; count;";
        GraqlMatch.Aggregate parsed = parseQuery(query).asMatchAggregate();
        GraqlMatch.Aggregate expected = match(var("x").isa("movie").has("title", "Godfather")).count();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testInsertQuery() {
        final String query = "insert $_ isa movie, has title \"The Title\";";
        GraqlInsert parsed = Graql.parseQuery(query).asInsert();
        GraqlInsert expected = insert(var().isa("movie").has("title", "The Title"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDeleteQuery_ResultIsSameAsJavaGraql() {
        final String query = "match\n" +
                "$x isa movie, has title 'The Title';\n" +
                "$y isa movie;\n" +
                "delete\n" +
                "$x isa movie;\n" +
                "$y isa movie;";
        GraqlDelete parsed = Graql.parseQuery(query).asDelete();
        GraqlDelete expected = match(
                var("x").isa("movie").has("title", "The Title"),
                var("y").isa("movie")
        ).delete(var("x").isa("movie"), var("y").isa("movie"));

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void whenParsingInsertQuery_ResultIsSameAsJavaGraql() {
        final String query = "insert\n" +
                "$x isa pokemon, has name 'Pichu';\n" +
                "$y isa pokemon, has name 'Pikachu';\n" +
                "$z isa pokemon, has name 'Raichu';\n" +
                "(evolves-from: $x, evolves-to: $y) isa evolution;\n" +
                "(evolves-from: $y, evolves-to: $z) isa evolution;";
        GraqlInsert parsed = Graql.parseQuery(query).asInsert();
        GraqlInsert expected = insert(
                var("x").isa("pokemon").has("name", "Pichu"),
                var("y").isa("pokemon").has("name", "Pikachu"),
                var("z").isa("pokemon").has("name", "Raichu"),
                rel("evolves-from", "x").rel("evolves-to", "y").isa("evolution"),
                rel("evolves-from", "y").rel("evolves-to", "z").isa("evolution")
        );

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void whenParsingUpdateQuery_ResultIssameAsJavaGraql() {
        String query = "match $x isa person, has name 'alice', has age $a;\n" +
                "delete $x has $a;\n" +
                "insert $x has age 25;";
        GraqlUpdate parsed = Graql.parseQuery(query).asUpdate();
        GraqlUpdate expected = match(var("x").isa("person").has("name", "alice").has("age", var("a")))
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
        GraqlDefine parsed = Graql.parseQuery(query).asDefine();

        GraqlDefine expected = define(
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
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(
                var("f").sub("parenthood")
                        .relates("father", "parent")
                        .relates("son", "child")
        );

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDefineQueryWithOwnsOverrides_ResultIsSameAsJavaGraql() {
        final String query = "define\n" +
                "triangle sub entity;\n" +
                "triangle owns side-length;\n" +
                "triangle-right-angled sub triangle;\n" +
                "triangle-right-angled owns hypotenuse-length as side-length;";
        GraqlDefine parsed = Graql.parseQuery(query).asDefine();

        GraqlDefine expected = define(
                type("triangle").sub("entity"),
                type("triangle").owns("side-length"),
                type("triangle-right-angled").sub("triangle"),
                type("triangle-right-angled").owns("hypotenuse-length", "side-length")
        );
        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDefineQueryWithRelatesOverrides_ResultIsSameAsJavaGraql() {
        final String query = "define\n" +
                "pokemon sub entity;\n" +
                "evolves sub relation;\n" +
                "evolves relates from, relates to;\n" +
                "evolves-final sub evolves;\n" +
                "evolves-final relates from-final as from;";
        GraqlDefine parsed = Graql.parseQuery(query).asDefine();

        GraqlDefine expected = define(
                type("pokemon").sub("entity"),
                type("evolves").sub("relation"),
                type("evolves").relates("from").relates("to"),
                type("evolves-final").sub("evolves"),
                type("evolves-final").relates("from-final", "from")
        );
        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void whenParsingDefineQueryWithPlaysOverrides_ResultIsSameAsJavaGraql() {
        final String query = "define\n" +
                "pokemon sub entity;\n" +
                "evolves sub relation;\n" +
                "evolves relates from, relates to;\n" +
                "evolves-final sub evolves;\n" +
                "evolves-final relates from-final as from;\n" +
                "pokemon plays evolves-final:from-final as from;";
        GraqlDefine parsed = Graql.parseQuery(query).asDefine();

        GraqlDefine expected = define(
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
    public void whenParsingDefineQuery_ResultIsSameAsJavaGraql() {
        final String query = "define\n" +
                "pokemon sub entity;\n" +
                "evolution sub relation;\n" +
                "evolves-from sub role;\n" +
                "evolves-to sub role;\n" +
                "evolves relates from, relates to;\n" +
                "pokemon plays evolves:from, plays evolves:to, owns name;";
        GraqlDefine parsed = Graql.parseQuery(query).asDefine();

        GraqlDefine expected = define(
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
    public void whenParsingUndefineQuery_ResultIsSameAsJavaGraql() {
        final String query = "undefine\n" +
                "pokemon sub entity;\n" +
                "evolution sub relation;\n" +
                "evolves-from sub role;\n" +
                "evolves-to sub role;\n" +
                "evolves relates from, relates to;\n" +
                "pokemon plays evolves:from, plays evolves:to, owns name;";
        GraqlUndefine parsed = Graql.parseQuery(query).asUndefine();

        GraqlUndefine expected = undefine(
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
        GraqlInsert parsed = Graql.parseQuery(query).asInsert();
        GraqlInsert expected = match(var("x").isa("language"))
                .insert(var("x").has("name", "HELLO"));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testDefineAbstractEntityQuery() {
        final String query = "define\n" +
                "concrete-type sub entity;\n" +
                "abstract-type sub entity, abstract;";
        GraqlDefine parsed = Graql.parseQuery(query).asDefine();
        GraqlDefine expected = define(
                type("concrete-type").sub("entity"),
                type("abstract-type").sub("entity").isAbstract()
        );

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testMatchValueTypeQuery() {
        final String query = "match $x value double;";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(var("x").value(GraqlArg.ValueType.DOUBLE));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testParseWithoutVar() {
        String query = "match $_ isa person;";
        assertThrows(() -> Graql.parseQuery(query).asMatch());
        assertThrows(() -> match(var().isa("person")));
    }

    @Test
    public void whenParsingDateKeyword_ParseAsTheCorrectValueType() {
        final String query = "match $x value datetime;";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(var("x").value(GraqlArg.ValueType.DATETIME));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testDefineValueTypeQuery() {
        final String query = "define my-type sub attribute, value long;";
        GraqlDefine parsed = Graql.parseQuery(query).asDefine();
        GraqlDefine expected = define(type("my-type").sub("attribute").value(GraqlArg.ValueType.LONG));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testEscapeString() {
        // ANTLR will see this as a string that looks like:
        // "This has \"double quotes\" and a single-quoted backslash: '\\'"
        final String input = "This has \\\"double quotes\\\" and a single-quoted backslash: \\'\\\\\\'";

        final String query = "insert $_ isa movie, has title \"" + input + "\";";
        GraqlInsert parsed = Graql.parseQuery(query).asInsert();
        GraqlInsert expected = insert(var().isa("movie").has("title", input));

        assertQueryEquals(expected, parsed, query);
    }


    @Test
    public void whenParsingQueryWithComments_TheyAreIgnored() {
        final String query = "match \n# there's a comment here\n$x isa###WOW HERES ANOTHER###\r\nmovie; count;";
        GraqlMatch.Aggregate parsed = parseQuery(query).asMatchAggregate();
        GraqlMatch.Aggregate expected = match(var("x").isa("movie")).count();

        assertEquals(expected, parsed);
        assertEquals(expected, parseQuery(parsed.toString()));
    }

    @Test
    public void testParsingPattern() {
        final String pattern = "{ (wife: $a, husband: $b) isa marriage; $a has gender 'male'; $b has gender 'female'; }";
        Pattern parsed = Graql.parsePattern(pattern);
        Pattern expected = Graql.and(
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

        GraqlDefine expected = define(rule("all-movies-are-drama").when(whenPattern).then(thenPattern));
        final String query = "define rule all-movies-are-drama: when { " + when + " } then { " + then + " };";
        GraqlDefine parsed = Graql.parseQuery(query).asDefine();

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testQueryParserWithoutGraph() {
        final String queryString = "match $x isa movie; get $x;";
        GraqlMatch query = parseQuery("match $x isa movie; get $x;").asMatch();
        assertEquals(queryString, query.toString());
    }

    @Test
    public void testParseBoolean() {
        final String query = "insert $_ has flag true;";
        GraqlInsert parsed = Graql.parseQuery(query).asInsert();
        GraqlInsert expected = insert(var().has("flag", true));

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testParseAggregateGroup() {
        final String query = "match $x isa movie; group $x;";
        GraqlMatch.Group parsed = parseQuery(query).asMatchGroup();
        GraqlMatch.Group expected = match(var("x").isa("movie")).group("x");

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testParseAggregateGroupCount() {
        final String query = "match $x isa movie; group $x; count;";
        GraqlMatch.Group.Aggregate parsed = parseQuery(query).asMatchGroupAggregate();
        GraqlMatch.Group.Aggregate expected = match(var("x").isa("movie")).group("x").count();

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testParseAggregateStd() {
        final String query = "match $x isa movie; std $x;";
        GraqlMatch.Aggregate parsed = parseQuery(query).asMatchAggregate();
        GraqlMatch.Aggregate expected = match(var("x").isa("movie")).std("x");

        assertQueryEquals(expected, parsed, query);
    }

    @Test
    public void testParseAggregateToString() {
        final String query = "match $x isa movie; get $x; group $x; count;";
        assertEquals(query, parseQuery(query).toString());
    }

    // ===============================================================================================================//
    // Test Graql Compute queries
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
        GraqlCompute expected = Graql.compute().cluster().using(CONNECTED_COMPONENT).in("movie", "person").where(size(10));
        GraqlCompute parsed = Graql.parseQuery(
                "compute cluster in [movie, person], using connected-component, where [size = 10];").asComputeCluster();

        assertEquals(expected, parsed);
    }

    @Test
    public void testParseComputeClusterUsingCCWithSizeTwice() {
        GraqlCompute expected =
                Graql.compute().cluster().using(CONNECTED_COMPONENT).in("movie", "person").where(size(10), size(15));

        GraqlCompute parsed = Graql.parseQuery(
                "compute cluster in [movie, person], using connected-component, where [size = 10, size = 15];").asComputeCluster();

        assertEquals(expected, parsed);
    }

    @Test
    public void testParseComputeClusterUsingKCore() {
        assertParseEquivalence("compute cluster in [movie, person], using k-core;");
    }

    @Test
    public void testParseComputeClusterUsingKCoreWithK() {
        GraqlCompute expected = Graql.compute().cluster().using(K_CORE).in("movie", "person").where(k(10));
        GraqlCompute parsed = Graql.parseQuery(
                "compute cluster in [movie, person], using k-core, where k = 10;").asComputeCluster();

        assertEquals(expected, parsed);
    }

    @Test
    public void testParseComputeClusterUsingKCoreWithKTwice() {
        GraqlCompute expected = Graql.compute().cluster().using(K_CORE).in("movie", "person").where(k(10));
        GraqlCompute parsed = Graql.parseQuery(
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
    public void whenParseIncorrectSyntax_ThrowGraqlSyntaxExceptionWithHelpfulError() {
        exception.expect(GraqlException.class);
        exception.expectMessage(allOf(
                containsString("syntax error"), containsString("line 1"),
                containsString("\nmatch $x isa "),
                containsString("\n             ^")
        ));
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match $x isa ");
    }

    @Test
    public void whenParseIncorrectSyntax_ErrorMessageShouldRetainWhitespace() {
        exception.expect(GraqlException.class);
        exception.expectMessage(Matchers.not(containsString("match$xisa")));
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match $x isa ");
    }

    @Test
    public void testSyntaxErrorPointer() {
        exception.expect(GraqlException.class);
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
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(var().has("title", "Godfather").has("tmdb-vote-count", var("x")));

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testRegexAttributeType() {
        final String query = "match $x regex '(fe)?male';";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();
        GraqlMatch expected = match(var("x").regex("(fe)?male"));

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testGraqlParseQuery() {
        assertTrue(parseQuery("match $x isa movie;") instanceof GraqlMatch);
    }

    @Test
    public void testParseKey() {
        assertEquals("match $x owns name @key; get $x;", parseQuery("match $x owns name @key; get $x;").toString());
    }

    @Test
    public void testParseEmptyString() {
        exception.expect(GraqlException.class);
        Graql.parseQuery("");
    }

    @Test
    public void testParseListOneMatch() {
        final String getString = "match $y isa movie;";
        List<GraqlQuery> queries = Graql.parseQueries(getString).collect(toList());

        assertEquals(Arrays.asList(match(var("y").isa("movie"))), queries);
    }

    @Test
    public void testParseListOneInsert() {
        final String insertString = "insert $x isa movie;";
        List<GraqlQuery> queries = Graql.parseQueries(insertString).collect(toList());

        assertEquals(Arrays.asList(insert(var("x").isa("movie"))), queries);
    }

    @Test
    public void testParseListOneInsertWithWhitespacePrefix() {
        final String insertString = " insert $x isa movie;";
        List<GraqlQuery> queries = Graql.parseQueries(insertString).collect(toList());

        assertEquals(Arrays.asList(insert(var("x").isa("movie"))), queries);
    }

    @Test
    public void testParseListOneInsertWithPrefixComment() {
        final String insertString = "#hola\ninsert $x isa movie;";
        List<GraqlQuery> queries = Graql.parseQueries(insertString).collect(toList());

        assertEquals(Arrays.asList(insert(var("x").isa("movie"))), queries);
    }

    @Test
    public void testParseList() {
        final String insertString = "insert $x isa movie;";
        final String getString = "match $y isa movie;";
        List<GraqlQuery> queries = Graql.parseQueries(insertString + getString).collect(toList());

        assertEquals(Arrays.asList(insert(var("x").isa("movie")), match(var("y").isa("movie"))), queries);
    }

    @Test
    public void testParseManyMatchInsertWithoutStackOverflow() {
        final int numQueries = 10_000;
        final String matchInsertString = "match $x isa person; insert $x has name 'bob';\n";
        StringBuilder longQuery = new StringBuilder();
        for (int i = 0; i < numQueries; i++) {
            longQuery.append(matchInsertString);
        }

        GraqlInsert matchInsert = match(var("x").isa("person")).insert(var("x").has("name", "bob"));
        List<GraqlInsert> queries = Graql.<GraqlInsert>parseQueries(longQuery.toString()).collect(toList());

        assertEquals(Collections.nCopies(numQueries, matchInsert), queries);
    }

    @Test
    public void whenParsingAListOfQueriesWithASyntaxError_ReportError() {
        final String queryText = "define person sub entity has name;"; // note no semicolon

        exception.expect(GraqlException.class);
        exception.expectMessage("define person sub entity has name;"); // Message should refer to line

        //noinspection ResultOfMethodCallIgnored
        Graql.parseQuery(queryText);
    }

    @SuppressWarnings("CheckReturnValue")
    @Test(expected = GraqlException.class)
    public void whenParsingMultipleQueriesLikeOne_Throw() {
        //noinspection ResultOfMethodCallIgnored
        parseQuery("insert $x isa movie; insert $y isa movie");
    }

    @Test
    public void testMissingColon() {
        exception.expect(GraqlException.class);
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match (actor $x, $y) isa has-cast;");
    }

    @Test
    public void testMissingComma() {
        exception.expect(GraqlException.class);
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match ($x $y) isa has-cast;");
    }

    @Test
    public void testLimitMistake() {
        exception.expect(GraqlException.class);
        exception.expectMessage("limit1");
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match ($x, $y); limit1;");
    }

    @Test
    public void whenParsingAggregateWithWrongVariableArgumentNumber_Throw() {
        exception.expect(GraqlException.class);
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match $x isa name; group;");
    }

    @Test
    public void whenParsingAggregateWithWrongName_Throw() {
        exception.expect(GraqlException.class);
        //noinspection ResultOfMethodCallIgnored
        parseQuery("match $x isa name; hello $x;");
    }

    @Test
    public void defineAttributeTypeRegex() {
        final String query = "define digit sub attribute, regex '\\d';";
        GraqlDefine parsed = parseQuery(query);
        GraqlDefine expected = define(type("digit").sub("attribute").regex("\\d"));
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void undefineAttributeTypeRegex() {
        final String query = "undefine digit regex '\\d';";
        GraqlUndefine parsed = parseQuery(query);
        GraqlUndefine expected = undefine(type("digit").regex("\\d"));
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void regexPredicateParsesCharacterClassesCorrectly() {
        final String query = "match $x like '\\d';";
        GraqlMatch parsed = parseQuery(query);
        GraqlMatch expected = match(var("x").like("\\d"));
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void regexPredicateParsesQuotesCorrectly() {
        final String query = "match $x like '\\\"';";
        GraqlMatch parsed = parseQuery(query);
        GraqlMatch expected = match(var("x").like("\\\""));
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void regexPredicateParsesBackslashesCorrectly() {
        final String query = "match $x like '\\\\';";
        GraqlMatch parsed = parseQuery(query);
        GraqlMatch expected = match(var("x").like("\\\\"));
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void regexPredicateParsesNewlineCorrectly() {
        final String query = "match $x like '\\n';";
        GraqlMatch parsed = parseQuery(query);
        GraqlMatch expected = match(var("x").like("\\n"));
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void regexPredicateParsesForwardSlashesCorrectly() {
        final String query = "match $x like '\\/';";
        GraqlMatch parsed = parseQuery(query);
        GraqlMatch expected = match(var("x").like("/"));
        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void whenValueEqualityToString_CreateValidQueryString() {
        GraqlMatch expected = match(var("x").eq(var("y")));
        GraqlMatch parsed = Graql.parseQuery(expected.toString());
        assertEquals(expected, parsed);
    }

    private static void assertParseEquivalence(String query) {
        assertEquals(query, parseQuery(query).toString());
    }
}