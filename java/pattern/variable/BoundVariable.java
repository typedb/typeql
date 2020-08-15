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

package graql.lang.pattern.variable;

import graql.lang.pattern.Pattern;

import java.util.LinkedHashMap;
import java.util.List;

import static grakn.common.collection.Collections.list;

public abstract class BoundVariable<T extends BoundVariable<T>> extends Variable<T> implements Pattern {

    BoundVariable(Identity identity) {
        super(identity);
    }

    abstract T merge(T variable);

    abstract T asAnonymousWithID(int id);

    public static <T extends BoundVariable<T>> List<T> asGraph(List<T> variables) {
        LinkedHashMap<T, T> graph = new LinkedHashMap<>();
        int id = 0;

        for (T variable : variables) {
            if (!variable.isAnonymous()) {
                if (graph.containsKey(variable.withoutProperties())) {
                    T merged = graph.get(variable.withoutProperties()).merge(variable);
                    graph.put(variable.withoutProperties(), merged);
                } else {
                    graph.put(variable.withoutProperties(), variable);
                }
            } else {
                T convertedVar = variable.asAnonymousWithID(id++);
                graph.put(convertedVar.withoutProperties(), convertedVar);
            }
        }

        return list(graph.values());
    }
}
