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

import graql.lang.common.exception.ErrorMessage;
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

import static grakn.common.collection.Collections.list;
import static graql.lang.common.GraqlToken.Char.COMMA_SPACE;
import static graql.lang.common.GraqlToken.Char.SPACE;
import static graql.lang.common.exception.ErrorMessage.ILLEGAL_PROPERTY_REPETITION;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public abstract class ThingVariable<T extends ThingVariable<T>> extends BoundVariable<ThingVariable<?>> {

    final Map<Class<? extends ThingProperty.Singular>, ThingProperty.Singular> singularProperties;
    final Map<Class<? extends ThingProperty.Repeatable>, List<ThingProperty.Repeatable>> repeatingProperties;

    public ThingVariable(Identity identity, ThingProperty property) {
        super(identity);
        this.singularProperties = new HashMap<>();
        this.repeatingProperties = new HashMap<>();
        if (property != null) {
            if (property.isSingular()) asSameThingWith(property.asSingular());
            else asSameThingWith(property.asRepeatable());
        }
    }

    ThingVariable(Identity identity,
                  Map<Class<? extends ThingProperty.Singular>, ThingProperty.Singular> singularProperties,
                  Map<Class<? extends ThingProperty.Repeatable>, List<ThingProperty.Repeatable>> repeatingProperties) {
        super(identity);
        this.singularProperties = new HashMap<>(singularProperties);
        this.repeatingProperties = new HashMap<>(repeatingProperties);
    }

    public abstract T getThis();

    public abstract T withoutProperties();

    @Override
    public Set<ThingProperty> properties() {
        return Stream.concat(
                singularProperties.values().stream(),
                repeatingProperties.values().stream().flatMap(Collection::stream)
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

    public Optional<ThingProperty.IID> iidProperty() {
        return Optional.ofNullable(singularProperties.get(ThingProperty.IID.class)).map(ThingProperty::asIID);
    }

    public Optional<ThingProperty.Isa> isaProperty() {
        return Optional.ofNullable(singularProperties.get(ThingProperty.Isa.class)).map(ThingProperty::asIsa);
    }

    public Optional<ThingProperty.NEQ> neqProperty() {
        return Optional.ofNullable(singularProperties.get(ThingProperty.NEQ.class)).map(ThingProperty::asNEQ);
    }

    public Optional<ThingProperty.Value> valueProperty() {
        return Optional.ofNullable(singularProperties.get(ThingProperty.Value.class)).map(ThingProperty::asValue);
    }

    public Optional<ThingProperty.Relation> relationProperty() {
        return Optional.ofNullable(singularProperties.get(ThingProperty.Relation.class)).map(ThingProperty::asRelation);
    }

    public List<ThingProperty.Has> hasProperty() {
        return repeatingProperties.computeIfAbsent(ThingProperty.Has.class, c -> new ArrayList<>())
                .stream().map(ThingProperty::asHas).collect(toList());
    }

    void addSingularProperties(ThingProperty.Singular property) {
        if (singularProperties.containsKey(property.getClass())) {
            throw GraqlException.create(ILLEGAL_PROPERTY_REPETITION.message(
                    withoutProperties().toString(),
                    singularProperties.get(property.getClass()).toString(),
                    property.toString()
            ));
        }
        singularProperties.put(property.getClass(), property);
    }

    @Override
    ThingVariable.Merged merge(ThingVariable<?> variable) {
        ThingVariable.Merged merged = new ThingVariable.Merged(identity, singularProperties, repeatingProperties);
        variable.singularProperties.values().forEach(merged::addSingularProperties);
        variable.repeatingProperties.forEach(
                (clazz, list) -> merged.repeatingProperties.computeIfAbsent(clazz, c -> new ArrayList<>()).addAll(list)
        );
        return merged;
    }

    public T asSameThingWith(ThingProperty.Singular property) {
        addSingularProperties(property);
        return getThis();
    }

    public T asSameThingWith(ThingProperty.Repeatable property) {
        repeatingProperties.computeIfAbsent(property.getClass(), c -> new ArrayList<>()).add(property);
        return getThis();
    }

    String isaSyntax() {
        if (isaProperty().isPresent()) return isaProperty().get().toString();
        else return "";
    }

    String hasSyntax() {
        return hasProperty().stream().map(ThingProperty.Has::toString).collect(joining(COMMA_SPACE.toString()));
    }

    @Override
    public abstract String toString();

    static class Merged extends ThingVariable<Merged> {

        Merged(Identity identity) {
            super(identity, null);
        }

        @Override
        ThingVariable<?> asAnonymousWithID(int id) {
            throw GraqlException.create(ErrorMessage.INVALID_CONVERT_OPERATION.message());
        }

        Merged(Identity identity,
               Map<Class<? extends ThingProperty.Singular>, ThingProperty.Singular> singularProperties,
               Map<Class<? extends ThingProperty.Repeatable>, List<ThingProperty.Repeatable>> repeatingProperties) {
            super(identity, singularProperties, repeatingProperties);
        }

        @Override
        public Merged getThis() {
            return this;
        }

        @Override
        public Merged withoutProperties() {
            return new ThingVariable.Merged(identity);
        }

        @Override
        public String toString() {
            StringBuilder syntax = new StringBuilder();
            Predicate<ThingProperty> filter = p -> true;
            if (isVisible()) {
                syntax.append(identity.syntax());
            } else if (relationProperty().isPresent()) {
                syntax.append(SPACE).append(relationProperty().get());
                filter = p -> !(p instanceof ThingProperty.Relation);
            } else if (valueProperty().isPresent()) {
                syntax.append(SPACE).append(valueProperty().get());
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

        private Thing(Identity.AnonymousWithID identity,
                      Map<Class<? extends ThingProperty.Singular>, ThingProperty.Singular> singularProperties,
                      Map<Class<? extends ThingProperty.Repeatable>, List<ThingProperty.Repeatable>> repeatingProperties) {
            super(identity, singularProperties, repeatingProperties);
        }

        @Override
        public ThingVariable.Thing getThis() {
            return this;
        }

        @Override
        ThingVariable.Thing asAnonymousWithID(int id) {
            return new ThingVariable.Thing(Identity.anonymous(identity.isVisible, id), singularProperties, repeatingProperties);
        }

        @Override
        public ThingVariable.Thing withoutProperties() {
            return new ThingVariable.Thing(identity, null);
        }

        private String thingSyntax() {
            if (isaProperty().isPresent()) return isaSyntax();
            else if (iidProperty().isPresent()) return iidProperty().get().toString();
            else if (neqProperty().isPresent()) return neqProperty().get().toString();
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

        private ThingProperty.Relation relationProperty;

        Relation(Identity identity, ThingProperty.Relation property) {
            super(identity, property);
            this.relationProperty = property;
        }

        public Relation(Identity.AnonymousWithID identity,
                        Map<Class<? extends ThingProperty.Singular>, ThingProperty.Singular> singularProperties,
                        Map<Class<? extends ThingProperty.Repeatable>, List<ThingProperty.Repeatable>> repeatingProperties) {
            super(identity, singularProperties, repeatingProperties);
        }

        @Override
        public ThingVariable.Relation getThis() {
            return this;
        }

        @Override
        ThingVariable.Relation asAnonymousWithID(int id) {
            return new ThingVariable.Relation(Identity.anonymous(identity.isVisible, id), singularProperties, repeatingProperties);
        }

        @Override
        public ThingVariable.Relation withoutProperties() {
            return new ThingVariable.Relation(identity, null);
        }

        @Override
        public ThingVariable.Relation asRelationWith(ThingProperty.Relation.RolePlayer rolePlayer) {
            ThingProperty.Relation relProp = singularProperties.get(ThingProperty.Relation.class).asRelation();
            this.singularProperties.put(ThingProperty.Relation.class, new ThingProperty.Relation(list(relProp.players(), rolePlayer)));
            this.relationProperty.player(rolePlayer);
            return this;
        }

        @Override
        public String toString() {
            assert relationProperty().isPresent();
            StringBuilder syntax = new StringBuilder();
            if (isVisible()) syntax.append(identity.syntax()).append(SPACE);
            syntax.append(relationProperty().get());

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

        public Attribute(Identity.AnonymousWithID identity,
                         Map<Class<? extends ThingProperty.Singular>, ThingProperty.Singular> singularProperties,
                         Map<Class<? extends ThingProperty.Repeatable>, List<ThingProperty.Repeatable>> repeatingProperties) {
            super(identity, singularProperties, repeatingProperties);
        }

        @Override
        public ThingVariable.Attribute getThis() {
            return this;
        }

        @Override
        ThingVariable.Attribute asAnonymousWithID(int id) {
            return new ThingVariable.Attribute(Identity.anonymous(identity.isVisible, id), singularProperties, repeatingProperties);
        }

        @Override
        public ThingVariable.Attribute withoutProperties() {
            return new ThingVariable.Attribute(identity, null);
        }

        @Override
        public String toString() {
            assert valueProperty().isPresent();
            StringBuilder syntax = new StringBuilder();
            if (isVisible()) syntax.append(identity.syntax()).append(SPACE);
            syntax.append(valueProperty().get());

            String properties = Stream.of(isaSyntax(), hasSyntax())
                    .filter(s -> !s.isEmpty()).collect(joining(COMMA_SPACE.toString()));

            if (!properties.isEmpty()) syntax.append(SPACE).append(properties);
            return syntax.toString();
        }
    }
}
