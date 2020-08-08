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

package graql.lang.variable;

import graql.lang.exception.GraqlException;
import graql.lang.property.ThingProperty;
import graql.lang.variable.builder.ThingVariableBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static grakn.common.util.Collections.list;
import static graql.lang.Graql.Token.Char.COMMA_SPACE;
import static graql.lang.Graql.Token.Char.SPACE;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public abstract class ThingVariable<T extends ThingVariable> extends Variable {

    final Map<Class<? extends ThingProperty.Singular>, ThingProperty.Singular> singularProperties;
    private final Map<Class<? extends ThingProperty.Repeatable>, List<ThingProperty.Repeatable>> repeatingProperties;

    public ThingVariable(Identity identity, ThingProperty property) {
        super(identity);
        this.singularProperties = new HashMap<>();
        this.repeatingProperties = new HashMap<>();
        if (property != null) {
            if (property.isSingular()) asSameThingWith(property.asSingular());
            else asSameThingWith(property.asRepeatable());
        }
    }

    public abstract T getThis();

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
    public ThingVariable asThing() {
        return this;
    }

    public Optional<ThingProperty.ID> idProperty() {
        return Optional.ofNullable(singularProperties.get(ThingProperty.ID.class)).map(ThingProperty::asID);
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

    public T asSameThingWith(ThingProperty.Singular property) {
        if (singularProperties.containsKey(property.getClass())) {
            throw GraqlException.illegalRepetitions(withoutProperties().toString(),
                                                    singularProperties.get(property.getClass()).toString(),
                                                    property.toString());
        }
        singularProperties.put(property.getClass(), property);
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

    public static class Thing extends ThingVariable<Thing> implements ThingVariableBuilder<Thing> {

        Thing(Identity identity, ThingProperty property) {
            super(identity, property);
        }

        @Override
        public ThingVariable.Thing getThis() {
            return this;
        }

        @Override
        public ThingVariable.Thing withoutProperties() {
            return new ThingVariable.Thing(identity, null);
        }

        private String thingSyntax() {
            if (isaProperty().isPresent()) return isaSyntax();
            else if (idProperty().isPresent()) return idProperty().get().toString();
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

        @Override
        public ThingVariable.Relation getThis() {
            return this;
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

        @Override
        public ThingVariable.Attribute getThis() {
            return this;
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
