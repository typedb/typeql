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
import graql.lang.pattern.constraint.Constraint;
import graql.lang.pattern.constraint.ThingConstraint;
import graql.lang.pattern.variable.builder.ThingVariableBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static graql.lang.common.GraqlToken.Char.COMMA_SPACE;
import static graql.lang.common.GraqlToken.Char.SPACE;
import static graql.lang.common.exception.ErrorMessage.ILLEGAL_CONSTRAINT_REPETITION;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public abstract class ThingVariable<T extends ThingVariable<T>> extends BoundVariable {

    final Map<Class<? extends ThingConstraint.Singular>, ThingConstraint.Singular> singular;
    final Map<Class<? extends ThingConstraint.Repeatable>, List<ThingConstraint.Repeatable>> repeating;

    public ThingVariable(Reference reference, ThingConstraint constraint) {
        super(reference);
        this.singular = new HashMap<>();
        this.repeating = new HashMap<>();
        if (constraint != null) {
            if (constraint.isSingular()) asSameThingWith(constraint.asSingular());
            else asSameThingWith(constraint.asRepeatable());
        }
    }

    ThingVariable(Reference reference,
                  Map<Class<? extends ThingConstraint.Singular>, ThingConstraint.Singular> singular,
                  Map<Class<? extends ThingConstraint.Repeatable>, List<ThingConstraint.Repeatable>> repeating) {
        super(reference);
        this.singular = new HashMap<>(singular);
        this.repeating = new HashMap<>(repeating);
    }

    abstract T getThis();

    @Override
    public Stream<ThingConstraint> properties() {
        return Stream.concat(
                singular.values().stream(),
                repeating.values().stream().flatMap(Collection::stream)
        );
    }

    @Override
    public boolean isThing() {
        return true;
    }

    @Override
    public ThingVariable<?> asThing() {
        return this;
    }

    public Optional<ThingConstraint.IID> iid() {
        return Optional.ofNullable(singular.get(ThingConstraint.IID.class)).map(ThingConstraint::asIID);
    }

    public Optional<ThingConstraint.Isa> isa() {
        return Optional.ofNullable(singular.get(ThingConstraint.Isa.class)).map(ThingConstraint::asIsa);
    }

    public Optional<ThingConstraint.NEQ> neq() {
        return Optional.ofNullable(singular.get(ThingConstraint.NEQ.class)).map(ThingConstraint::asNEQ);
    }

    public Optional<ThingConstraint.Value> value() {
        return Optional.ofNullable(singular.get(ThingConstraint.Value.class)).map(ThingConstraint::asValue);
    }

    public Optional<ThingConstraint.Relation> relation() {
        return Optional.ofNullable(singular.get(ThingConstraint.Relation.class)).map(ThingConstraint::asRelation);
    }

    public List<ThingConstraint.Has> has() {
        return repeating.computeIfAbsent(ThingConstraint.Has.class, c -> new ArrayList<>())
                .stream().map(ThingConstraint::asHas).collect(toList());
    }

    void addSingularProperties(ThingConstraint.Singular constraint) {
        if (singular.containsKey(constraint.getClass()) && !singular.get(constraint.getClass()).equals(constraint)) {
            throw GraqlException.create(ILLEGAL_CONSTRAINT_REPETITION.message(reference, singular.get(constraint.getClass()), constraint));
        } else if (constraint.isIsa() && constraint.asIsa().type().label().isPresent() && relation().isPresent()) {
            relation().get().setScope(constraint.asIsa().type().label().get().label());
        } else if (constraint.isRelation() && isa().isPresent() && isa().get().type().label().isPresent()) {
            constraint.asRelation().setScope(isa().get().type().label().get().label());
        }

        if (!singular.containsKey(constraint.getClass())) {
            singular.put(constraint.getClass(), constraint);
        }
    }

    ThingVariable.Merged merge(ThingVariable<?> variable) {
        ThingVariable.Merged merged = new ThingVariable.Merged(reference, singular, repeating);
        variable.singular.values().forEach(merged::addSingularProperties);
        variable.repeating.forEach(
                (clazz, list) -> merged.repeating.computeIfAbsent(clazz, c -> new ArrayList<>()).addAll(list)
        );
        return merged;
    }

    public T asSameThingWith(ThingConstraint.Singular constraint) {
        addSingularProperties(constraint);
        return getThis();
    }

    public T asSameThingWith(ThingConstraint.Repeatable constraint) {
        repeating.computeIfAbsent(constraint.getClass(), c -> new ArrayList<>()).add(constraint);
        return getThis();
    }

    String isaSyntax() {
        if (isa().isPresent()) return isa().get().toString();
        else return "";
    }

    String hasSyntax() {
        return has().stream().map(ThingConstraint.Has::toString).collect(joining(COMMA_SPACE.toString()));
    }

    @Override
    public abstract String toString();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass().isAssignableFrom(ThingVariable.class)) return false;
        ThingVariable<?> that = (ThingVariable<?>) o;

        return (this.reference.equals(that.reference) &&
                this.properties().collect(toSet()).equals(that.properties().collect(toSet())));
    }

    @Override
    public final int hashCode() {
        return Objects.hash(reference, properties().collect(toSet()));
    }

    static class Merged extends ThingVariable<Merged> {

        Merged(Reference reference,
               Map<Class<? extends ThingConstraint.Singular>, ThingConstraint.Singular> singularProperties,
               Map<Class<? extends ThingConstraint.Repeatable>, List<ThingConstraint.Repeatable>> repeatingProperties) {
            super(reference, singularProperties, repeatingProperties);
        }

        @Override
        ThingVariable.Merged getThis() {
            return this;
        }

        @Override
        public String toString() {
            StringBuilder syntax = new StringBuilder();
            Predicate<ThingConstraint> filter = p -> true;
            if (isVisible()) {
                syntax.append(reference.syntax());
            } else if (relation().isPresent()) {
                syntax.append(SPACE).append(relation().get());
                filter = p -> !(p instanceof ThingConstraint.Relation);
            } else if (value().isPresent()) {
                syntax.append(SPACE).append(value().get());
                filter = p -> !(p instanceof ThingConstraint.Value<?>);
            } else {
                assert false;
                return null;
            }

            String properties = properties().filter(filter).map(Constraint::toString).collect(joining(COMMA_SPACE.toString()));
            if (!properties.isEmpty()) syntax.append(SPACE).append(properties);
            return syntax.toString();
        }
    }

    public static class Thing extends ThingVariable<Thing> implements ThingVariableBuilder.Common<Thing> {

        Thing(Reference reference, ThingConstraint constraint) {
            super(reference, constraint);
        }

        @Override
        ThingVariable.Thing getThis() {
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

    public static class Relation extends ThingVariable<Relation> implements ThingVariableBuilder.Relation,
                                                                            ThingVariableBuilder.Common<Relation> {

        Relation(Reference reference, ThingConstraint.Relation constraint) {
            super(reference, constraint);
        }

        @Override
        ThingVariable.Relation getThis() {
            return this;
        }

        @Override
        public ThingVariable.Relation asRelationWith(ThingConstraint.Relation.RolePlayer rolePlayer) {
            ThingConstraint.Relation relationConstraint = singular.get(ThingConstraint.Relation.class).asRelation();
            relationConstraint.addPlayers(rolePlayer);
            if (isa().isPresent() && !relationConstraint.hasScope()) {
                relationConstraint.setScope(isa().get().type().label().get().label());
            }
            this.singular.put(ThingConstraint.Relation.class, relationConstraint);
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

    public static class Attribute extends ThingVariable<Attribute> implements ThingVariableBuilder.Common<Attribute> {

        Attribute(Reference reference, ThingConstraint constraint) {
            super(reference, constraint);
        }

        @Override
        ThingVariable.Attribute getThis() {
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
