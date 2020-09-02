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
import graql.lang.pattern.property.ThingProperty;
import graql.lang.pattern.variable.builder.ThingVariableBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static graql.lang.common.GraqlToken.Char.COMMA_SPACE;
import static graql.lang.common.GraqlToken.Char.SPACE;
import static graql.lang.common.exception.ErrorMessage.ILLEGAL_PROPERTY_REPETITION;
import static graql.lang.common.exception.ErrorMessage.INVALID_CONVERT_OPERATION;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public abstract class ThingBoundVariable<T extends ThingBoundVariable<T>> extends BoundVariable<ThingBoundVariable<?>> {

    final Map<Class<? extends ThingProperty.Singular>, ThingProperty.Singular> singular;
    final Map<Class<? extends ThingProperty.Repeatable>, List<ThingProperty.Repeatable>> repeating;

    public ThingBoundVariable(Reference reference, ThingProperty property) {
        super(reference);
        this.singular = new HashMap<>();
        this.repeating = new HashMap<>();
        if (property != null) {
            if (property.isSingular()) asSameThingWith(property.asSingular());
            else asSameThingWith(property.asRepeatable());
        }
    }

    ThingBoundVariable(Reference reference,
                       Map<Class<? extends ThingProperty.Singular>, ThingProperty.Singular> singular,
                       Map<Class<? extends ThingProperty.Repeatable>, List<ThingProperty.Repeatable>> repeating) {
        super(reference);
        this.singular = new HashMap<>(singular);
        this.repeating = new HashMap<>(repeating);
    }

    abstract T getThis();

    @Override
    public Set<ThingProperty> properties() {
        return Stream.concat(
                singular.values().stream(),
                repeating.values().stream().flatMap(Collection::stream)
        ).collect(Collectors.toSet());
    }

    @Override
    public boolean isThing() {
        return true;
    }

    @Override
    public ThingBoundVariable<?> toThing() {
        return this;
    }

    public Optional<ThingProperty.IID> iid() {
        return Optional.ofNullable(singular.get(ThingProperty.IID.class)).map(ThingProperty::asIID);
    }

    public Optional<ThingProperty.Isa> isa() {
        return Optional.ofNullable(singular.get(ThingProperty.Isa.class)).map(ThingProperty::asIsa);
    }

    public Optional<ThingProperty.NEQ> neq() {
        return Optional.ofNullable(singular.get(ThingProperty.NEQ.class)).map(ThingProperty::asNEQ);
    }

    public Optional<ThingProperty.Value> value() {
        return Optional.ofNullable(singular.get(ThingProperty.Value.class)).map(ThingProperty::asValue);
    }

    public Optional<ThingProperty.Relation> relation() {
        return Optional.ofNullable(singular.get(ThingProperty.Relation.class)).map(ThingProperty::asRelation);
    }

    public List<ThingProperty.Has> has() {
        return repeating.computeIfAbsent(ThingProperty.Has.class, c -> new ArrayList<>())
                .stream().map(ThingProperty::asHas).collect(toList());
    }

    void addSingularProperties(ThingProperty.Singular property) {
        if (singular.containsKey(property.getClass()) && !singular.get(property.getClass()).equals(property)) {
            throw GraqlException.create(ILLEGAL_PROPERTY_REPETITION.message(reference, singular.get(property.getClass()), property));
        } else if (property.isIsa() && property.asIsa().type().label().isPresent() && relation().isPresent()) {
            relation().get().setScope(property.asIsa().type().label().get().label());
        } else if (property.isRelation() && isa().isPresent() && isa().get().type().label().isPresent()) {
            property.asRelation().setScope(isa().get().type().label().get().label());
        }

        if (!singular.containsKey(property.getClass())) {
            singular.put(property.getClass(), property);
        }
    }

    @Override
    ThingBoundVariable.Merged merge(ThingBoundVariable<?> variable) {
        ThingBoundVariable.Merged merged = new ThingBoundVariable.Merged(reference, singular, repeating);
        variable.singular.values().forEach(merged::addSingularProperties);
        variable.repeating.forEach(
                (clazz, list) -> merged.repeating.computeIfAbsent(clazz, c -> new ArrayList<>()).addAll(list)
        );
        return merged;
    }

    public T asSameThingWith(ThingProperty.Singular property) {
        addSingularProperties(property);
        return getThis();
    }

    public T asSameThingWith(ThingProperty.Repeatable property) {
        repeating.computeIfAbsent(property.getClass(), c -> new ArrayList<>()).add(property);
        return getThis();
    }

    String isaSyntax() {
        if (isa().isPresent()) return isa().get().toString();
        else return "";
    }

    String hasSyntax() {
        return has().stream().map(ThingProperty.Has::toString).collect(joining(COMMA_SPACE.toString()));
    }

    @Override
    public abstract String toString();

    static class Merged extends ThingBoundVariable<Merged> {

        Merged(Reference reference,
               Map<Class<? extends ThingProperty.Singular>, ThingProperty.Singular> singularProperties,
               Map<Class<? extends ThingProperty.Repeatable>, List<ThingProperty.Repeatable>> repeatingProperties) {
            super(reference, singularProperties, repeatingProperties);
        }

        @Override
        ThingBoundVariable.Merged getThis() {
            return this;
        }

        @Override
        ThingBoundVariable<?> setAnonymousWithID(int id) {
            throw GraqlException.create(INVALID_CONVERT_OPERATION.message());
        }

        @Override
        public String toString() {
            StringBuilder syntax = new StringBuilder();
            Predicate<ThingProperty> filter = p -> true;
            if (isVisible()) {
                syntax.append(reference.syntax());
            } else if (relation().isPresent()) {
                syntax.append(SPACE).append(relation().get());
                filter = p -> !(p instanceof ThingProperty.Relation);
            } else if (value().isPresent()) {
                syntax.append(SPACE).append(value().get());
                filter = p -> !(p instanceof ThingProperty.Value<?>);
            } else {
                assert false;
                return null;
            }

            String properties = properties().stream().filter(filter).map(Property::toString).collect(joining(COMMA_SPACE.toString()));
            if (!properties.isEmpty()) syntax.append(SPACE).append(properties);
            return syntax.toString();
        }
    }

    public static class Thing extends ThingBoundVariable<Thing> implements ThingVariableBuilder.Common<Thing> {

        Thing(Reference reference, ThingProperty property) {
            super(reference, property);
        }

        @Override
        ThingBoundVariable.Thing getThis() {
            return this;
        }

        private String thingSyntax() {
            if (isa().isPresent()) return isaSyntax();
            else if (iid().isPresent()) return iid().get().toString();
            else if (neq().isPresent()) return neq().get().toString();
            else return "";
        }

        @Override
        public String toString() {
            StringBuilder syntax = new StringBuilder();
            if (isVisible()) syntax.append(reference.syntax());

            String properties = Stream.of(thingSyntax(), hasSyntax())
                    .filter(s -> !s.isEmpty()).collect(joining(COMMA_SPACE.toString()));

            if (!properties.isEmpty()) syntax.append(SPACE).append(properties);
            return syntax.toString();
        }
    }

    public static class Relation extends ThingBoundVariable<Relation> implements ThingVariableBuilder.Relation,
                                                                                 ThingVariableBuilder.Common<Relation> {

        Relation(Reference reference, ThingProperty.Relation property) {
            super(reference, property);
        }

        @Override
        ThingBoundVariable.Relation getThis() {
            return this;
        }

        @Override
        public ThingBoundVariable.Relation asRelationWith(ThingProperty.Relation.RolePlayer rolePlayer) {
            ThingProperty.Relation relationProperty = singular.get(ThingProperty.Relation.class).asRelation();
            relationProperty.addPlayers(rolePlayer);
            if (isa().isPresent() && !relationProperty.hasScope()) {
                relationProperty.setScope(isa().get().type().label().get().label());
            }
            this.singular.put(ThingProperty.Relation.class, relationProperty);
            return this;
        }

        @Override
        public String toString() {
            assert relation().isPresent();
            StringBuilder syntax = new StringBuilder();
            if (isVisible()) syntax.append(reference.syntax()).append(SPACE);
            syntax.append(relation().get());

            String properties = Stream.of(isaSyntax(), hasSyntax())
                    .filter(s -> !s.isEmpty()).collect(joining(COMMA_SPACE.toString()));

            if (!properties.isEmpty()) syntax.append(SPACE).append(properties);
            return syntax.toString();
        }
    }

    public static class Attribute extends ThingBoundVariable<Attribute> implements ThingVariableBuilder.Common<Attribute> {

        Attribute(Reference reference, ThingProperty property) {
            super(reference, property);
        }

        @Override
        ThingBoundVariable.Attribute getThis() {
            return this;
        }

        @Override
        public String toString() {
            assert value().isPresent();
            StringBuilder syntax = new StringBuilder();
            if (isVisible()) syntax.append(reference.syntax()).append(SPACE);
            syntax.append(value().get());

            String properties = Stream.of(isaSyntax(), hasSyntax())
                    .filter(s -> !s.isEmpty()).collect(joining(COMMA_SPACE.toString()));

            if (!properties.isEmpty()) syntax.append(SPACE).append(properties);
            return syntax.toString();
        }
    }
}
