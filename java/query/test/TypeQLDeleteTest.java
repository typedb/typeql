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
 */

package com.vaticle.typeql.lang.query.test;

import com.vaticle.typeql.lang.TypeQL;
import com.vaticle.typeql.lang.common.exception.ErrorMessage;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;
import com.vaticle.typeql.lang.query.TypeQLDelete;
import com.vaticle.typeql.lang.query.TypeQLMatch;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.TypeQL.var;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TypeQLDeleteTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private final TypeQLMatch.Unfiltered match1 = TypeQL.match(var("x").isa("movie"));
    private final TypeQLMatch.Unfiltered match2 = TypeQL.match(var("y").isa("movie"));

    private final List<ThingVariable<?>> delete1 = list(var("x").isa("movie"));
    private final List<ThingVariable<?>> delete2 = list(var("y").isa("movie"));

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