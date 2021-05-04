/*
 * Copyright (C) 2021 Vaticle
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

package com.vaticle.typeql.lang.pattern.test;

import com.vaticle.typeql.lang.TypeQL;
import com.vaticle.typeql.lang.pattern.Conjunctable;
import com.vaticle.typeql.lang.pattern.Conjunction;
import com.vaticle.typeql.lang.pattern.Disjunction;
import com.vaticle.typeql.lang.pattern.Pattern;
import com.vaticle.typeql.lang.query.TypeQLMatch;
import com.vaticle.typeql.lang.query.TypeQLQuery;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NormalisationTest {

    @Test
    public void disjunction() {
        String query = "match $com isa company; {$com has name $n1; $n1 \"the-company\";} or {$com has name $n2; $n2 \"another-company\";};";
        TypeQLMatch typeqlMatch = TypeQL.parseQuery(query).asMatch();
        Disjunction<Conjunction<Conjunctable>> normalised = typeqlMatch.conjunction().normalise();

        List<Conjunction<Conjunctable>> disjunction = normalised.patterns();
        assertTrue(disjunction.size() == 2);
        Conjunction<? extends Pattern> partA = TypeQL.parsePattern("{ $com isa company; $com has name $n1; $n1 \"the-company\"; }").asConjunction();
        Conjunction<? extends Pattern> partB = TypeQL.parsePattern("{ $com isa company; $com has name $n2; $n2 \"another-company\";}").asConjunction();
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
        TypeQLMatch typeqlMatch = TypeQL.parseQuery(query).asMatch();
        Disjunction<Conjunction<Conjunctable>> normalised = typeqlMatch.conjunction().normalise();

        String expected = "match $com isa company; not { " +
                "{ $com has name $n1; $n1 \"the-company\"; } or { $com has name $n1; $n1 \"other-company\"; }; };";
        TypeQLQuery expectedQuery = TypeQL.parseQuery(expected);
        Disjunction<? extends Pattern> inner = expectedQuery.asMatch().conjunction().patterns().get(1).asNegation().pattern().asDisjunction();
        assertEquals(expected, expectedQuery.toString().replace("\n", " "));
    }
}
