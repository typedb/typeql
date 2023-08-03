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

package com.vaticle.typeql.lang.query.test;

import com.vaticle.typeql.lang.TypeQL;
import com.vaticle.typeql.lang.common.exception.ErrorMessage;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;
import com.vaticle.typeql.lang.query.TypeQLDelete;
import com.vaticle.typeql.lang.query.TypeQLGet;
import com.vaticle.typeql.lang.query.TypeQLQuery;
import com.vaticle.typeql.lang.query.TypeQLQuery.MatchClause;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.TypeQL.cVar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TypeQLDeleteTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private final MatchClause match1 = TypeQL.match(cVar("x").isa("movie"));
    private final MatchClause match2 = TypeQL.match(cVar("y").isa("movie"));

    private final List<ThingVariable<?>> delete1 = list(cVar("x").isa("movie"));
    private final List<ThingVariable<?>> delete2 = list(cVar("y").isa("movie"));

    @Test
    public void deleteQueriesWithTheSameMatchAndVarsAreEqual() {
        TypeQLDelete query1 = match1.delete(delete1);
        TypeQLDelete query2 = match1.delete(delete1);
        assertEquals(query1, query2);
        assertEquals(query1.hashCode(), query2.hashCode());
    }

    @Test
    public void deleteQueriesWithDifferentMatchesOrVarsAreDifferent() {
        TypeQLDelete query1 = match1.delete(delete1);
        TypeQLDelete query2 = match2.delete(delete2);
        assertNotEquals(query1, query2);
    }

    @Test
    public void deleteQueryWithNewUnboundVariablesThrows() {
        exception.expect(TypeQLException.class);
        exception.expectMessage("The deleted variable '$y' is out of scope of the match query.");
        TypeQLDelete query = match1.delete(delete2);
    }

    @Test
    public void deleteQueryWithoutVariablesThrows() {
        exception.expect(TypeQLException.class);
        exception.expectMessage(ErrorMessage.MISSING_PATTERNS.message());
        TypeQLDelete query = match1.delete(new ArrayList<>());
    }

    @Test
    public void deleteQueryWithBuilderWithoutVariablesThrows() {
        exception.expect(TypeQLException.class);
        exception.expectMessage(ErrorMessage.MISSING_PATTERNS.message());
        TypeQLDelete query = match1.delete();
    }
}