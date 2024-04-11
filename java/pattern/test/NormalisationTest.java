/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typeql.lang.pattern.test;

import com.vaticle.typeql.lang.TypeQL;
import com.vaticle.typeql.lang.pattern.Conjunctable;
import com.vaticle.typeql.lang.pattern.Conjunction;
import com.vaticle.typeql.lang.pattern.Disjunction;
import com.vaticle.typeql.lang.pattern.Pattern;
import com.vaticle.typeql.lang.query.TypeQLGet;
import com.vaticle.typeql.lang.query.TypeQLQuery;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NormalisationTest {

    @Test
    public void test() {
        String query = "match $x has name $y; get $x;";
        TypeQLGet get = TypeQL.parseQuery(query).asGet();

        System.out.println(query);
    }

    @Test
    public void disjunction() {
        String query = "match\n" +
                "$com isa company;\n" +
                "{\n" +
                "    $com has name $n1;\n" +
                "    $n1 \"the-company\";\n" +
                "} or {\n" +
                "    $com has name $n2;\n" +
                "    $n2 \"another-company\";\n" +
                "};\n" +
                "get;";
        TypeQLGet getQuery = TypeQL.parseQuery(query).asGet();
        Disjunction<Conjunction<Conjunctable>> normalised = getQuery.match().conjunction().normalise();

        List<Conjunction<Conjunctable>> disjunction = normalised.patterns();
        assertTrue(disjunction.size() == 2);
        Conjunction<? extends Pattern> partA = TypeQL.parsePattern("{ $com isa company; $com has name $n1; $n1 \"the-company\"; }").asConjunction();
        Conjunction<? extends Pattern> partB = TypeQL.parsePattern("{ $com isa company; $com has name $n2; $n2 \"another-company\";}").asConjunction();
        disjunction.get(0).statements().forEach(var -> {
            assertEquals(partA.statements().filter(stmt -> stmt.equals(var)).count(), 1);
        });
        disjunction.get(1).statements().forEach(var -> {
            assertEquals(partB.statements().filter(stmt -> stmt.equals(var)).count(), 1);
        });
    }

    @Test
    public void negatedDisjunction() {
        String query = "match\n" +
                "$com isa company;\n" +
                "not {\n" +
                "    $com has name $n1;\n" +
                "    {\n" +
                "        $n1 \"the-company\";\n" +
                "    } or {\n" +
                "        $n1 \"other-company\";\n" +
                "    };\n" +
                "};\n" +
                "get; ";
        TypeQLGet getQuery = TypeQL.parseQuery(query).asGet();
        Disjunction<Conjunction<Conjunctable>> normalised = getQuery.match().conjunction().normalise();

        String expected = "match\n" +
                "$com isa company;\n" +
                "not {\n" +
                "    {\n" +
                "        $com has name $n1;\n" +
                "        $n1 \"the-company\";\n" +
                "    } or {\n" +
                "        $com has name $n1;\n" +
                "        $n1 \"other-company\";\n" +
                "    };\n" +
                "};\n" +
                "get;";
        TypeQLQuery expectedQuery = TypeQL.parseQuery(expected);
        Disjunction<? extends Pattern> inner = expectedQuery.asGet().match().conjunction().patterns().get(1).asNegation().pattern().asDisjunction();
        assertEquals(expected, expectedQuery.toString());
    }
}
