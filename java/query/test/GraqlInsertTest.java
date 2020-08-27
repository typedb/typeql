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
import graql.lang.pattern.variable.ThingVariable;
import graql.lang.query.GraqlInsert;
import graql.lang.query.GraqlMatch;
import org.junit.Test;

import java.util.List;

import static grakn.common.collection.Collections.list;
import static graql.lang.Graql.var;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class GraqlInsertTest {

    private final GraqlMatch.Unfiltered match1 = Graql.match(var("x").isa("movie"));
    private final GraqlMatch.Unfiltered match2 = Graql.match(var("y").isa("movie"));

    private final List<ThingVariable<?>> vars1 = list(var("x").asThing());
    private final List<ThingVariable<?>> vars2 = list(var("y").asThing());

    @Test
    public void insertQueriesWithTheSameVarsAndQueryAreEqual() {
        GraqlInsert query1 = match1.insert(vars1);
        GraqlInsert query2 = match1.insert(vars1);

        assertEquals(query1, query2);
        assertEquals(query1.hashCode(), query2.hashCode());
    }

    @Test
    public void insertQueriesWithTheSameVarsAndGraphAreEqual() {
        GraqlInsert query1 = Graql.insert(vars1);
        GraqlInsert query2 = Graql.insert(vars1);

        assertEquals(query1, query2);
        assertEquals(query1.hashCode(), query2.hashCode());
    }

    @Test
    public void insertQueriesWithDifferentMatchesAreDifferent() {
        GraqlInsert query1 = match1.insert(vars1);
        GraqlInsert query2 = match2.insert(vars1);

        assertNotEquals(query1, query2);
    }

    @Test
    public void insertQueriesWithDifferentVarsAreDifferent() {
        GraqlInsert query1 = match1.insert(vars1);
        GraqlInsert query2 = match1.insert(vars2);

        assertNotEquals(query1, query2);
    }
}