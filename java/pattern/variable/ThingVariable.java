/*
 * Copyright (C) 2021 Vaticle
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

package com.vaticle.typeql.lang.pattern.variable;

import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.constraint.ConceptConstraint;
import com.vaticle.typeql.lang.pattern.constraint.ThingConstraint;
import com.vaticle.typeql.lang.pattern.variable.builder.ThingVariableBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COMMA_SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.ILLEGAL_CONSTRAINT_REPETITION;
import static java.util.stream.Collectors.joining;

public abstract class ThingVariable<T extends ThingVariable<T>> extends BoundVariable {

    ThingConstraint.IID iidConstraint;
    ThingConstraint.Isa isaConstraint;
    ConceptConstraint.Is isConstraint;
    ThingConstraint.Value<?> valueConstraint;
    ThingConstraint.Relation relationConstraint;
    List<ThingConstraint.Has> hasConstraints;
    List<ThingConstraint> constraints;

    ThingVariable(Reference reference) {
        super(reference);
        this.hasConstraints = new LinkedList<>();
        this.constraints = new LinkedList<>();
    }

    abstract T getThis();

    @Override
    public List<ThingConstraint> constraints() {
        return constraints;
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
        return Optional.ofNullable(iidConstraint);
    }

    public Optional<ThingConstraint.Isa> isa() {
        return Optional.ofNullable(isaConstraint);
    }

    public Optional<ConceptConstraint.Is> is() {
        return Optional.ofNullable(isConstraint);
    }

    public Optional<ThingConstraint.Value<?>> value() {
        return Optional.ofNullable(valueConstraint);
    }

    public Optional<ThingConstraint.Relation> relation() {
        return Optional.ofNullable(relationConstraint);
    }

    public List<ThingConstraint.Has> has() {
        return hasConstraints;
    }

    public T constrain(ThingConstraint.Isa constraint) {
        if (isaConstraint != null) {
            throw TypeQLException.of(ILLEGAL_CONSTRAINT_REPETITION.message(reference, ThingConstraint.Isa.class, constraint));
        } else if (constraint.type().label().isPresent() && relation().isPresent()) {
            relationConstraint.setScope(constraint.type().label().get().label());
        }
        isaConstraint = constraint;
        constraints.add(constraint);
        return getThis();
    }

    public T constrain(ThingConstraint.Has constraint) {
        hasConstraints.add(constraint);
        constraints.add(constraint);
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
        if (o == null || !ThingVariable.class.isAssignableFrom(o.getClass())) return false;
        ThingVariable<?> that = (ThingVariable<?>) o;

        return (this.reference.equals(that.reference) && this.constraints.equals(that.constraints));
    }

    @Override
    public final int hashCode() {
        return Objects.hash(reference, constraints);
    }

    public static class Thing extends ThingVariable<Thing> implements ThingVariableBuilder.Common<Thing> {

        Thing(Reference reference) {
            super(reference);
        }

        Thing(Reference reference, ThingConstraint.IID iidConstraint) {
            super(reference);
            this.iidConstraint = iidConstraint;
            constraints.add(iidConstraint);
        }

        @Override
        ThingVariable.Thing getThis() {
            return this;
        }

        private String thingSyntax() {
            if (isa().isPresent()) return isaSyntax();
            else if (iid().isPresent()) return iid().get().toString();
            else if (is().isPresent()) return is().get().toString();
            else return "";
        }

        @Override
        public String toString() {
            StringBuilder syntax = new StringBuilder();
            if (isVisible()) syntax.append(reference.syntax());

            String constraints = Stream.of(thingSyntax(), hasSyntax())
                    .filter(s -> !s.isEmpty()).collect(joining(COMMA_SPACE.toString()));

            if (!constraints.isEmpty()) syntax.append(SPACE).append(constraints);
            return syntax.toString();
        }
    }

    public static class Relation extends ThingVariable<Relation> implements ThingVariableBuilder.Relation,
                                                                            ThingVariableBuilder.Common<Relation> {

        Relation(Reference reference, ThingConstraint.Relation relationConstraint) {
            super(reference);
            this.relationConstraint = relationConstraint;
            constraints.add(relationConstraint);
        }

        @Override
        ThingVariable.Relation getThis() {
            return this;
        }

        @Override
        public ThingVariable.Relation constrain(ThingConstraint.Relation.RolePlayer rolePlayer) {
            relationConstraint.addPlayers(rolePlayer);
            return this;
        }

        @Override
        public String toString() {
            assert relation().isPresent();
            StringBuilder syntax = new StringBuilder();
            if (isVisible()) syntax.append(reference.syntax()).append(SPACE);
            syntax.append(relation().get());

            String constraints = Stream.of(isaSyntax(), hasSyntax())
                    .filter(s -> !s.isEmpty()).collect(joining(COMMA_SPACE.toString()));

            if (!constraints.isEmpty()) syntax.append(SPACE).append(constraints);
            return syntax.toString();
        }
    }

    public static class Attribute extends ThingVariable<Attribute> implements ThingVariableBuilder.Common<Attribute> {

        Attribute(Reference reference, ThingConstraint.Value<?> valueConstraint) {
            super(reference);
            this.valueConstraint = valueConstraint;
            constraints.add(valueConstraint);
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

            String constraints = Stream.of(isaSyntax(), hasSyntax())
                    .filter(s -> !s.isEmpty()).collect(joining(COMMA_SPACE.toString()));

            if (!constraints.isEmpty()) syntax.append(SPACE).append(constraints);
            return syntax.toString();
        }
    }
}
