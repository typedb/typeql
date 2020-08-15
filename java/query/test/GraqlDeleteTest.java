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

package graql.lang.query.test;

import graql.lang.Graql;
import graql.lang.common.exception.ErrorMessage;
import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.variable.ThingVariable;
import graql.lang.query.GraqlDelete;
import graql.lang.query.MatchClause;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static grakn.common.collection.Collections.list;
import static graql.lang.Graql.var;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class GraqlDeleteTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private final MatchClause match1 = Graql.match(var("x").isa("movie"));
    private final MatchClause match2 = Graql.match(var("y").isa("movie"));

    private final List<ThingVariable<?>> delete1 = list(var("x").isa("movie"));
    private final List<ThingVariable<?>> delete2 = list(var("y").isa("movie"));

    @Test
    public void deleteQueriesWithTheSameMatchAndVarsAreEqual() {
        GraqlDelete query1 = match1.delete(delete1);
        GraqlDelete query2 = match1.delete(delete1);
        assertEquals(query1, query2);
        assertEquals(query1.hashCode(), query2.hashCode());
    }

    @Test
    public void deleteQueriesWithDifferentMatchesOrVarsAreDifferent() {
        GraqlDelete query1 = match1.delete(delete1);
        GraqlDelete query2 = match2.delete(delete2);
        assertNotEquals(query1, query2);
    }

    @Test
    public void deleteQueryWithNewUnboundVariablesThrows() {
        exception.expect(GraqlException.class);
        exception.expectMessage("The variable '$y' is out of scope of the query.");
        GraqlDelete query = match1.delete(delete2);
    }

    @Test
    public void deleteQueryWithoutVariablesThrows() {
        exception.expect(GraqlException.class);
        exception.expectMessage(ErrorMessage.MISSING_PATTERNS.message());
        GraqlDelete query = match1.delete(new ArrayList<>());
    }

    @Test
    public void deleteQueryWithBuilderWithoutVariablesThrows() {
        exception.expect(GraqlException.class);
        exception.expectMessage(ErrorMessage.MISSING_PATTERNS.message());
        GraqlDelete query = match1.delete();
    }
}