/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.typeql.lang.query.test;

import com.typeql.lang.TypeQL;
import com.typeql.lang.common.exception.ErrorMessage;
import com.typeql.lang.common.exception.TypeQLException;
import com.typeql.lang.pattern.statement.ThingStatement;
import com.typeql.lang.query.TypeQLDelete;
import com.typeql.lang.query.TypeQLQuery.MatchClause;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.typeql.lang.TypeQL.cVar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TypeQLDeleteTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private final MatchClause match1 = TypeQL.match(cVar("x").isa("movie"));
    private final MatchClause match2 = TypeQL.match(cVar("y").isa("movie"));

    private final List<ThingStatement<?>> delete1 = list(cVar("x").isa("movie"));
    private final List<ThingStatement<?>> delete2 = list(cVar("y").isa("movie"));

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