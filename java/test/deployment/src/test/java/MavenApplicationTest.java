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

package graql.lang.test.deployment.src.test.java;

import graql.lang.Graql;
import graql.lang.query.GraqlMatch;
import graql.lang.query.GraqlQuery;
import org.junit.Test;

import static graql.lang.Graql.and;
import static graql.lang.Graql.match;
import static graql.lang.Graql.or;
import static graql.lang.Graql.rel;
import static graql.lang.Graql.var;
import static org.junit.Assert.assertEquals;

public class MavenApplicationTest {
    private void assertQueryEquals(GraqlQuery expected, GraqlQuery parsed, String query) {
        assertEquals(expected, parsed);
        assertEquals(expected, Graql.parseQuery(parsed.toString()));
        assertEquals(query, expected.toString());
    }

    @Test
    public void testRelationQuery() {
        String query = "match\n" +
                "$brando 'Marl B' isa name;\n" +
                "(actor: $brando, $char, production-with-cast: $prod);\n" +
                "get $char, $prod;";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();

        GraqlMatch expected = match(
                var("brando").val("Marl B").isa("name"),
                rel("actor", "brando").rel("char").rel("production-with-cast", "prod")
        ).get("char", "prod");

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }

    @Test
    public void testPredicateQuery1() {
        String query = "match\n" +
                "$x isa movie, has title $t;\n" +
                "{ $t 'Apocalypse Now'; } or { $t < 'Juno'; $t > 'Godfather'; } or { $t 'Spy'; };\n" +
                "$t !== 'Apocalypse Now';";
        GraqlMatch parsed = Graql.parseQuery(query).asMatch();

        GraqlMatch expected = match(
                var("x").isa("movie").has("title", var("t")),
                or(
                        var("t").val("Apocalypse Now"),
                        and(
                                var("t").lt("Juno"),
                                var("t").gt("Godfather")
                        ),
                        var("t").val("Spy")
                ),
                var("t").neq("Apocalypse Now")
        );

        assertQueryEquals(expected, parsed, query.replace("'", "\""));
    }
}
