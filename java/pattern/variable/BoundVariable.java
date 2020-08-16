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
import graql.lang.pattern.property.Property;
import graql.lang.pattern.property.TypeProperty;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import static grakn.common.collection.Collections.list;
import static java.util.stream.Collectors.toSet;

public abstract class BoundVariable<T extends BoundVariable<T>> extends Variable<T> implements Pattern {

    BoundVariable(Identity identity) {
        super(identity);
    }

    public abstract T withoutProperties();

    abstract T merge(T variable);

    abstract T asAnonymousWithID(int id);

    public static List<TypeVariable> asTypeGraph(List<TypeVariable> variables) {
        LinkedHashMap<TypeVariable, TypeVariable> graph = new LinkedHashMap<>();
        LinkedList<TypeVariable> list = new LinkedList<>(variables);

        while (!list.isEmpty()) {
            TypeVariable variable = list.removeFirst();
            assert variable.isLabelled();
            list.addAll(variable.properties().stream().flatMap(TypeProperty::variables).collect(toSet()));
            if (graph.containsKey(variable.withoutProperties())) {
                TypeVariable merged = graph.get(variable.withoutProperties()).merge(variable);
                graph.put(variable.withoutProperties(), merged);
            } else {
                graph.put(variable.withoutProperties(), variable);
            }
        }

        return list(graph.values());
    }

    public static List<BoundVariable<?>> asGraph(List<ThingVariable<?>> variables) {
        LinkedHashMap<BoundVariable<?>, BoundVariable<?>> graph = new LinkedHashMap<>();
        LinkedList<BoundVariable<?>> list = new LinkedList<>(variables);
        int id = 0;

        while (!list.isEmpty()) {
            BoundVariable<?> variable = list.removeFirst();
            list.addAll(variable.properties().stream().flatMap(Property::variables).collect(toSet()));
            if (!variable.isAnonymous()) {
                if (graph.containsKey(variable.withoutProperties())) {
                    BoundVariable<?> existing = graph.get(variable.withoutProperties());
                    BoundVariable<?> merged;
                    if (existing.isThing()) merged = existing.asThing().merge(variable.asThing());
                    else merged = existing.asType().merge(variable.asType());
                    graph.put(variable.withoutProperties(), merged);
                } else {
                    graph.put(variable.withoutProperties(), variable);
                }
            } else {
                BoundVariable<?> convertedVar = variable.asAnonymousWithID(id++);
                graph.put(convertedVar.withoutProperties(), convertedVar);
            }
        }

        return list(graph.values());
    }
}
