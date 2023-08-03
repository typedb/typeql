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
 *
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
    public void disjunction() {
        String query = "match\n" +
                "$com isa company;\n" +
                "{\n" +
                "    $com has name $n1;\n" +
                "    $n1 \"the-company\";\n" +
                "} or {\n" +
                "    $com has name $n2;\n" +
                "    $n2 \"another-company\";\n" +
                "};";
        TypeQLGet getQuery = TypeQL.parseQuery(query).asGet();
        Disjunction<Conjunction<Conjunctable>> normalised = getQuery.match().conjunction().normalise();

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
        String query = "match\n" +
                "$com isa company;\n" +
                "not {\n" +
                "    $com has name $n1;\n" +
                "    {\n" +
                "        $n1 \"the-company\";\n" +
                "    } or {\n" +
                "        $n1 \"other-company\";\n" +
                "    };\n" +
                "}; ";
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
                "};";
        TypeQLQuery expectedQuery = TypeQL.parseQuery(expected);
        Disjunction<? extends Pattern> inner = expectedQuery.asGet().match().conjunction().patterns().get(1).asNegation().pattern().asDisjunction();
        assertEquals(expected, expectedQuery.toString());
    }
}
