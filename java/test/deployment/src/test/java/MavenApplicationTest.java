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
        assertEquals(expected, Graql.parse(parsed.toString()));
        assertEquals(query, expected.toString());
    }

    @Test
    public void testRelationQuery() {
        String query = "match\n" +
                "$brando 'Marl B' isa name;\n" +
                "(actor: $brando, $char, production-with-cast: $prod);\n" +
                "get $char, $prod;";
        GraqlMatch parsed = Graql.parse(query).asMatch();

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
                "$t !== 'Apocalypse Now';\n" +
                "get;";
        GraqlMatch parsed = Graql.parse(query).asMatch();

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
