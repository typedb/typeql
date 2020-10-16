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
 *
 */

package graql.lang.pattern.test;

import graql.lang.Graql;
import graql.lang.pattern.Conjunctable;
import graql.lang.pattern.Conjunction;
import graql.lang.pattern.Disjunction;
import graql.lang.pattern.Pattern;
import graql.lang.query.GraqlMatch;
import graql.lang.query.GraqlQuery;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NormalisationTest {

    @Test
    public void disjunction() {
        String query = "match $com isa company; {$com has name $n1; $n1 \"the-company\";} or {$com has name $n2; $n2 \"another-company\";};";
        GraqlMatch graqlMatch = Graql.parseQuery(query).asMatch();
        Disjunction<Conjunction<Conjunctable>> normalised = graqlMatch.conjunction().normalise();

        List<Conjunction<Conjunctable>> disjunction = normalised.patterns();
        assertTrue(disjunction.size() == 2);
        Conjunction<? extends Pattern> partA = Graql.parsePattern("{ $com isa company; $com has name $n1; $n1 \"the-company\"; }").asConjunction();
        Conjunction<? extends Pattern> partB = Graql.parsePattern("{ $com isa company; $com has name $n2; $n2 \"another-company\";}").asConjunction();
        disjunction.get(0).variables().forEach(var -> {
            assertEquals(partA.variables().filter(variable -> variable.equals(var)).count(), 1);
        });
        disjunction.get(1).variables().forEach(var -> {
            assertEquals(partB.variables().filter(variable -> variable.equals(var)).count(), 1);
        });
    }

    @Test
    public void negatedDisjunction() {
        String query = "match $com isa company; not { $com has name $n1; { $n1 \"the-company\"; } or { $n1 \"other-company\"; }; }; ";
        GraqlMatch graqlMatch = Graql.parseQuery(query).asMatch();
        Disjunction<Conjunction<Conjunctable>> normalised = graqlMatch.conjunction().normalise();

        String expected = "match $com isa company; not { " +
                "{ $com has name $n1; $n1 \"the-company\"; } or { $com has name $n1; $n1 \"other-company\"; }; };";
        GraqlQuery expectedQuery = Graql.parseQuery(expected);
        Disjunction<? extends Pattern> inner = expectedQuery.asMatch().conjunction().patterns().get(1).asNegation().pattern().asDisjunction();
        assertEquals(expected, expectedQuery.toString().replace("\n", " "));
    }
}
