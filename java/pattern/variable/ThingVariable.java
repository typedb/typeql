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

public abstract class ThingVariable<T extends ThingVariable<T>> extends BoundVariable<ThingVariable<?>> {

    final Map<Class<? extends ThingProperty.Singular>, ThingProperty.Singular> singular;
    final Map<Class<? extends ThingProperty.Repeatable>, List<ThingProperty.Repeatable>> repeating;

    public ThingVariable(Identity identity, ThingProperty property) {
        super(identity);
        this.singular = new HashMap<>();
        this.repeating = new HashMap<>();
        if (property != null) {
            if (property.isSingular()) asSameThingWith(property.asSingular());
            else asSameThingWith(property.asRepeatable());
        }
    }

    ThingVariable(Identity identity,
                  Map<Class<? extends ThingProperty.Singular>, ThingProperty.Singular> singular,
                  Map<Class<? extends ThingProperty.Repeatable>, List<ThingProperty.Repeatable>> repeating) {
        super(identity);
        this.singular = new HashMap<>(singular);
        this.repeating = new HashMap<>(repeating);
    }

    abstract T getThis();

    public abstract T withoutProperties();

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
    public ThingVariable<?> asThing() {
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
            throw GraqlException.create(ILLEGAL_PROPERTY_REPETITION.message(identity, singular.get(property.getClass()), property));
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
    ThingVariable.Merged merge(ThingVariable<?> variable) {
        ThingVariable.Merged merged = new ThingVariable.Merged(identity, singular, repeating);
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

    static class Merged extends ThingVariable<Merged> {

        Merged(Identity identity) {
            super(identity, null);
        }

        @Override
        ThingVariable<?> setAnonymousWithID(int id) {
            throw GraqlException.create(INVALID_CONVERT_OPERATION.message());
        }

        Merged(Identity identity,
               Map<Class<? extends ThingProperty.Singular>, ThingProperty.Singular> singularProperties,
               Map<Class<? extends ThingProperty.Repeatable>, List<ThingProperty.Repeatable>> repeatingProperties) {
            super(identity, singularProperties, repeatingProperties);
        }

        @Override
        ThingVariable.Merged getThis() {
            return this;
        }

        @Override
        public ThingVariable.Merged withoutProperties() {
            return new ThingVariable.Merged(identity);
        }

        @Override
        public String toString() {
            StringBuilder syntax = new StringBuilder();
            Predicate<ThingProperty> filter = p -> true;
            if (isVisible()) {
                syntax.append(identity.syntax());
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

    public static class Thing extends ThingVariable<Thing> implements ThingVariableBuilder<Thing> {

        Thing(Identity identity, ThingProperty property) {
            super(identity, property);
        }

        @Override
        ThingVariable.Thing getThis() {
            return this;
        }

        @Override
        public ThingVariable.Thing withoutProperties() {
            return new ThingVariable.Thing(identity, null);
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
            if (isVisible()) syntax.append(identity.syntax());

            String properties = Stream.of(thingSyntax(), hasSyntax())
                    .filter(s -> !s.isEmpty()).collect(joining(COMMA_SPACE.toString()));

            if (!properties.isEmpty()) syntax.append(SPACE).append(properties);
            return syntax.toString();
        }
    }

    public static class Relation extends ThingVariable<Relation> implements ThingVariableBuilder.Relation,
                                                                            ThingVariableBuilder<Relation> {

        Relation(Identity identity, ThingProperty.Relation property) {
            super(identity, property);
        }

        @Override
        ThingVariable.Relation getThis() {
            return this;
        }

        @Override
        public ThingVariable.Relation withoutProperties() {
            return new ThingVariable.Relation(identity, null);
        }

        @Override
        public ThingVariable.Relation asRelationWith(ThingProperty.Relation.RolePlayer rolePlayer) {
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
            if (isVisible()) syntax.append(identity.syntax()).append(SPACE);
            syntax.append(relation().get());

            String properties = Stream.of(isaSyntax(), hasSyntax())
                    .filter(s -> !s.isEmpty()).collect(joining(COMMA_SPACE.toString()));

            if (!properties.isEmpty()) syntax.append(SPACE).append(properties);
            return syntax.toString();
        }
    }

    public static class Attribute extends ThingVariable<Attribute> implements ThingVariableBuilder<Attribute> {

        Attribute(Identity identity, ThingProperty property) {
            super(identity, property);
        }

        @Override
        ThingVariable.Attribute getThis() {
            return this;
        }

        @Override
        public ThingVariable.Attribute withoutProperties() {
            return new ThingVariable.Attribute(identity, null);
        }

        @Override
        public String toString() {
            assert value().isPresent();
            StringBuilder syntax = new StringBuilder();
            if (isVisible()) syntax.append(identity.syntax()).append(SPACE);
            syntax.append(value().get());

            String properties = Stream.of(isaSyntax(), hasSyntax())
                    .filter(s -> !s.isEmpty()).collect(joining(COMMA_SPACE.toString()));

            if (!properties.isEmpty()) syntax.append(SPACE).append(properties);
            return syntax.toString();
        }
    }
}
