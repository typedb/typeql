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

import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.property.Property;
import graql.lang.pattern.property.TypeProperty;
import graql.lang.pattern.variable.builder.TypeVariableBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static grakn.common.collection.Collections.set;
import static graql.lang.common.GraqlToken.Char.COMMA_SPACE;
import static graql.lang.common.GraqlToken.Char.SPACE;
import static graql.lang.common.exception.ErrorMessage.ILLEGAL_PROPERTY_REPETITION;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class TypeBoundVariable extends BoundVariable<TypeBoundVariable> implements TypeVariableBuilder {

    private final Map<Class<? extends TypeProperty>, TypeProperty.Singular> singular;
    private final Map<Class<? extends TypeProperty>, List<TypeProperty.Repeatable>> repeating;
    private final List<TypeProperty> ordered;

    TypeBoundVariable(Reference reference, TypeProperty property) {
        super(reference);
        this.singular = new HashMap<>();
        this.repeating = new HashMap<>();
        this.ordered = new ArrayList<>();
        if (property != null) {
            if (property.isSingular()) asTypeWith(property.asSingular());
            else asTypeWith(property.asRepeatable());
        }
    }

    private TypeBoundVariable(Reference reference,
                              Map<Class<? extends TypeProperty>, TypeProperty.Singular> singular,
                              Map<Class<? extends TypeProperty>, List<TypeProperty.Repeatable>> repeating,
                              List<TypeProperty> ordered) {
        super(reference);
        this.singular = new HashMap<>(singular);
        this.repeating = new HashMap<>(repeating);
        this.ordered = new ArrayList<>(ordered);
    }

    @Override
    public TypeBoundVariable getThis() {
        return this;
    }

    @Override
    public Set<TypeProperty> properties() {
        return set(ordered);
    }

    @Override
    public boolean isType() {
        return true;
    }

    @Override
    public TypeBoundVariable asType() {
        return this;
    }

    private void addSingularProperties(TypeProperty.Singular property) {
        if (singular.containsKey(property.getClass()) && !singular.get(property.getClass()).equals(property)) {
            throw GraqlException.create(ILLEGAL_PROPERTY_REPETITION.message(reference, singular.get(property.getClass()), property));
        } else if (!singular.containsKey(property.getClass())) {
            singular.put(property.getClass(), property);
        }
    }

    @Override
    TypeBoundVariable merge(TypeBoundVariable variable) {
        TypeBoundVariable merged = new TypeBoundVariable(reference, singular, repeating, ordered);
        variable.singular.values().forEach(property -> {
            merged.addSingularProperties(property);
            merged.ordered.add(property);
        });
        variable.repeating.forEach((clazz, list) -> {
            merged.repeating.computeIfAbsent(clazz, c -> new ArrayList<>()).addAll(list);
            merged.ordered.addAll(list);
        });
        return merged;
    }

    @Override
    public TypeBoundVariable asTypeWith(TypeProperty.Singular property) {
        addSingularProperties(property);
        ordered.add(property);
        return this;
    }

    @Override
    public TypeBoundVariable asTypeWith(TypeProperty.Repeatable property) {
        if (label().isPresent() && property instanceof TypeProperty.Relates) {
            ((TypeProperty.Relates) property).setScope(label().get().label());
        }

        repeating.computeIfAbsent(property.getClass(), c -> new ArrayList<>()).add(property);
        ordered.add(property);
        return this;
    }

    public Optional<TypeProperty.Label> label() {
        return Optional.ofNullable(singular.get(TypeProperty.Label.class)).map(TypeProperty::asLabel);
    }

    public Optional<TypeProperty.Sub> sub() {
        return Optional.ofNullable(singular.get(TypeProperty.Sub.class)).map(TypeProperty::asSub);
    }

    public Optional<TypeProperty.Abstract> abstractFlag() {
        return Optional.ofNullable(singular.get(TypeProperty.Abstract.class)).map(TypeProperty::asAbstract);
    }

    public Optional<TypeProperty.ValueType> valueType() {
        return Optional.ofNullable(singular.get(TypeProperty.ValueType.class)).map(TypeProperty::asValueType);
    }

    public Optional<TypeProperty.Regex> regex() {
        return Optional.ofNullable(singular.get(TypeProperty.Regex.class)).map(TypeProperty::asRegex);
    }

    public Optional<TypeProperty.Then> then() {
        return Optional.ofNullable(singular.get(TypeProperty.Then.class)).map(TypeProperty::asThen);
    }

    public Optional<TypeProperty.When> when() {
        return Optional.ofNullable(singular.get(TypeProperty.When.class)).map(TypeProperty::asWhen);
    }

    public List<TypeProperty.Owns> owns() {
        return repeating.computeIfAbsent(TypeProperty.Owns.class, c -> new ArrayList<>())
                .stream().map(TypeProperty::asOwns).collect(toList());
    }

    public List<TypeProperty.Plays> plays() {
        return repeating.computeIfAbsent(TypeProperty.Plays.class, c -> new ArrayList<>())
                .stream().map(TypeProperty::asPlays).collect(toList());
    }

    public List<TypeProperty.Relates> relates() {
        return repeating.computeIfAbsent(TypeProperty.Relates.class, c -> new ArrayList<>())
                .stream().map(TypeProperty::asRelates).collect(toList());
    }

    @Override
    public String toString() {
        StringBuilder syntax = new StringBuilder();

        if (isVisible()) {
            syntax.append(reference.syntax());
            if (!ordered.isEmpty()) {
                syntax.append(SPACE);
                syntax.append(ordered.stream().map(Property::toString).collect(joining(COMMA_SPACE.toString())));
            }
        } else if (label().isPresent()) {
            syntax.append(label().get().scopedLabel());
            if (ordered.size() > 1) {
                syntax.append(SPACE).append(ordered.stream().filter(p -> !(p instanceof TypeProperty.Label))
                                                    .map(Property::toString).collect(joining(COMMA_SPACE.toString())));
            }
        } else {
            // This should only be called by debuggers trying to print nested variables
            syntax.append(reference);
        }
        return syntax.toString();
    }
}
