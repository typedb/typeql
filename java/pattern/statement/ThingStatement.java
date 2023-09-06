/*
 * Copyright (C) 2022 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.vaticle.typeql.lang.pattern.statement;

import com.vaticle.typeql.lang.common.TypeQLVariable;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.constraint.ThingConstraint;
import com.vaticle.typeql.lang.pattern.statement.builder.ThingStatementBuilder;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COMMA;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COMMA_NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.ILLEGAL_CONSTRAINT_REPETITION;
import static com.vaticle.typeql.lang.common.util.Strings.indent;

public abstract class ThingStatement<T extends ThingStatement<T>> extends Statement {

    final TypeQLVariable.Concept variable;
    ThingConstraint.IID iidConstraint;
    ThingConstraint.Isa isaConstraint;
    ThingConstraint.Predicate predicateConstraint;
    ThingConstraint.Relation relationConstraint;
    List<ThingConstraint.Has> hasConstraints;
    List<ThingConstraint> constraints;

    ThingStatement(TypeQLVariable.Concept variable) {
        this.variable = variable;
        this.hasConstraints = new LinkedList<>();
        this.constraints = new LinkedList<>();
    }

    abstract T getThis();

    @Override
    public TypeQLVariable.Concept headVariable() {
        return variable;
    }

    @Override
    public List<ThingConstraint> constraints() {
        return constraints;
    }

    @Override
    public boolean isThing() {
        return true;
    }

    @Override
    public ThingStatement<?> asThing() {
        return this;
    }

    public Optional<ThingConstraint.IID> iid() {
        return Optional.ofNullable(iidConstraint);
    }

    public Optional<ThingConstraint.Isa> isa() {
        return Optional.ofNullable(isaConstraint);
    }

    public Optional<ThingConstraint.Predicate> predicate() {
        return Optional.ofNullable(predicateConstraint);
    }

    public Optional<ThingConstraint.Relation> relation() {
        return Optional.ofNullable(relationConstraint);
    }

    public List<ThingConstraint.Has> has() {
        return hasConstraints;
    }

    public T constrain(ThingConstraint.Isa constraint) {
        if (isaConstraint != null) {
            throw TypeQLException.of(ILLEGAL_CONSTRAINT_REPETITION.message(variable, ThingConstraint.Isa.class, constraint));
        } else if (constraint.type().isLabelled() && relation().isPresent()) {
            relationConstraint.setScope(constraint.type().reference().asLabel().label());
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
        return has().stream().map(ThingConstraint.Has::toString).collect(COMMA_NEW_LINE.joiner());
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !ThingStatement.class.isAssignableFrom(o.getClass())) return false;
        ThingStatement<?> that = (ThingStatement<?>) o;

        return (this.variable.equals(that.variable) && this.constraints.equals(that.constraints));
    }

    @Override
    public final int hashCode() {
        return Objects.hash(variable, constraints);
    }

    public static class Thing extends ThingStatement<Thing> implements ThingStatementBuilder.Common<Thing> {

        private Thing(TypeQLVariable.Concept variable) {
            super(variable);
        }

        private Thing(TypeQLVariable.Concept variable, ThingConstraint.IID iidConstraint) {
            super(variable);
            this.iidConstraint = iidConstraint;
            constraints.add(iidConstraint);
        }

        public static Thing of(TypeQLVariable.Concept variable) {
            return new Thing(variable);
        }

        public static Thing of(TypeQLVariable.Concept variable, ThingConstraint.IID iidConstraint) {
            return new Thing(variable, iidConstraint);
        }

        @Override
        ThingStatement.Thing getThis() {
            return this;
        }

        private String thingSyntax() {
            if (isa().isPresent()) return isaSyntax();
            else if (iid().isPresent()) return iid().get().toString();
            else return "";
        }

        @Override
        public String toString(boolean pretty) {
            StringBuilder thing = new StringBuilder();
            if (variable.isVisible()) thing.append(variable);
            String constraints;
            if (pretty) {
                constraints = Stream.of(thingSyntax(), hasSyntax()).filter(s -> !s.isEmpty()).collect(COMMA_NEW_LINE.joiner());
                constraints = indent(constraints).trim();
            } else {
                constraints = Stream.of(thingSyntax(), hasSyntax()).filter(s -> !s.isEmpty()).collect(COMMA.joiner());
            }
            if (!constraints.isEmpty()) thing.append(SPACE).append(constraints);
            return thing.toString();
        }
    }

    public static class Relation extends ThingStatement<Relation> implements ThingStatementBuilder.Relation,
            ThingStatementBuilder.Common<Relation> {

        private Relation(TypeQLVariable.Concept variable, ThingConstraint.Relation relationConstraint) {
            super(variable);
            this.relationConstraint = relationConstraint;
            constraints.add(relationConstraint);
        }

        public static Relation of(TypeQLVariable.Concept variable, ThingConstraint.Relation relationConstraint) {
            return new Relation(variable, relationConstraint);
        }

        @Override
        ThingStatement.Relation getThis() {
            return this;
        }

        @Override
        public ThingStatement.Relation constrain(ThingConstraint.Relation.RolePlayer rolePlayer) {
            relationConstraint.addPlayers(rolePlayer);
            return this;
        }

        @Override
        public String toString(boolean pretty) {
            assert relation().isPresent();
            StringBuilder relation = new StringBuilder();
            if (variable.isVisible()) relation.append(variable).append(SPACE);
            relation.append(relation().get());
            String constraints;
            if (pretty) {
                constraints = Stream.of(isaSyntax(), hasSyntax())
                        .filter(s -> !s.isEmpty()).collect(COMMA_NEW_LINE.joiner());
                constraints = indent(constraints).trim();
            } else {
                constraints = Stream.of(isaSyntax(), hasSyntax()).filter(s -> !s.isEmpty()).collect(COMMA.joiner());
            }
            if (!constraints.isEmpty()) relation.append(SPACE).append(constraints);
            return relation.toString();
        }
    }

    public static class Attribute extends ThingStatement<Attribute> implements ThingStatementBuilder.Common<Attribute> {

        private Attribute(TypeQLVariable.Concept variable, @Nullable ThingConstraint.Predicate predicateConstraint) {
            super(variable);
            this.predicateConstraint = predicateConstraint;
            if (predicateConstraint != null) constraints.add(predicateConstraint);
        }

        public static Attribute of(TypeQLVariable.Concept variable) {
            return new Attribute(variable, null);
        }

        public static Attribute of(TypeQLVariable.Concept variable, ThingConstraint.Predicate predicateConstraint) {
            return new Attribute(variable, predicateConstraint);
        }

        @Override
        ThingStatement.Attribute getThis() {
            return this;
        }

        @Override
        public String toString(boolean pretty) {
            StringBuilder attribute = new StringBuilder();
            if (variable.isVisible()) attribute.append(variable).append(SPACE);
            predicate().ifPresent(attribute::append);
            String constraints;
            if (pretty) {
                constraints = Stream.of(isaSyntax(), hasSyntax())
                        .filter(s -> !s.isEmpty()).collect(COMMA_NEW_LINE.joiner());
                constraints = indent(constraints).trim();
            } else {
                constraints = Stream.of(isaSyntax(), hasSyntax())
                        .filter(s -> !s.isEmpty()).collect(COMMA.joiner());
            }
            if (!constraints.isEmpty()) attribute.append(SPACE).append(constraints);
            return attribute.toString();
        }
    }
}
