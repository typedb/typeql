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
import com.vaticle.typeql.lang.pattern.Definable;
import com.vaticle.typeql.lang.pattern.constraint.Constraint;
import com.vaticle.typeql.lang.pattern.constraint.TypeConstraint;
import com.vaticle.typeql.lang.pattern.statement.builder.TypeStatementBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.vaticle.typedb.common.collection.Collections.set;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COMMA;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COMMA_NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.ILLEGAL_CONSTRAINT_REPETITION;
import static com.vaticle.typeql.lang.common.util.Strings.indent;

public class TypeStatement extends Statement implements TypeStatementBuilder, Definable {

    private final TypeQLVariable.Concept variable;
    private TypeConstraint.Label labelConstraint;
    private TypeConstraint.Sub subConstraint;
    private TypeConstraint.Abstract abstractConstraint;
    private TypeConstraint.ValueType valueTypeConstraint;
    private TypeConstraint.Regex regexConstraint;

    private final List<TypeConstraint.Owns> ownsConstraints;
    private final List<TypeConstraint.Plays> playsConstraints;
    private final List<TypeConstraint.Relates> relatesConstraints;

    private final List<TypeConstraint> constraints;

    private TypeStatement(TypeQLVariable.Concept variable) {
        this.variable = variable;
        this.ownsConstraints = new LinkedList<>();
        this.playsConstraints = new LinkedList<>();
        this.relatesConstraints = new LinkedList<>();
        this.constraints = new LinkedList<>();
    }

    public static TypeStatement of(TypeQLVariable.Concept variable) {
        return new TypeStatement(variable);
    }

    @Override
    public TypeQLVariable.Concept headVariable() {
        return variable;
    }

    @Override
    public List<TypeConstraint> constraints() {
        return constraints;
    }

    @Override
    public boolean isType() {
        return true;
    }

    @Override
    public TypeStatement asType() {
        return this;
    }

    @Override
    public TypeStatement constrain(TypeConstraint.Label constraint) {
        if (labelConstraint != null) {
            throw TypeQLException.of(ILLEGAL_CONSTRAINT_REPETITION.message(variable, TypeConstraint.Label.class, constraint));
        }
        labelConstraint = constraint;
        constraints.add(constraint);
        relatesConstraints.forEach(rel -> rel.setScope(constraint.label()));
        return this;
    }

    @Override
    public TypeStatement constrain(TypeConstraint.Sub constraint) {
        if (subConstraint != null) {
            throw TypeQLException.of(ILLEGAL_CONSTRAINT_REPETITION.message(variable, TypeConstraint.Sub.class, constraint));
        }
        subConstraint = constraint;
        constraints.add(constraint);
        return this;
    }

    @Override
    public TypeStatement constrain(TypeConstraint.Abstract constraint) {
        if (abstractConstraint != null) {
            throw TypeQLException.of(ILLEGAL_CONSTRAINT_REPETITION.message(variable, TypeConstraint.Abstract.class, constraint));
        }
        abstractConstraint = constraint;
        constraints.add(constraint);
        return this;
    }

    @Override
    public TypeStatement constrain(TypeConstraint.ValueType constraint) {
        if (valueTypeConstraint != null) {
            throw TypeQLException.of(ILLEGAL_CONSTRAINT_REPETITION.message(variable, TypeConstraint.ValueType.class, constraint));
        }
        valueTypeConstraint = constraint;
        constraints.add(constraint);
        return this;
    }

    @Override
    public TypeStatement constrain(TypeConstraint.Regex constraint) {
        if (regexConstraint != null) {
            throw TypeQLException.of(ILLEGAL_CONSTRAINT_REPETITION.message(variable, TypeConstraint.Regex.class, constraint));
        }
        regexConstraint = constraint;
        constraints.add(constraint);
        return this;
    }


    @Override
    public TypeStatement constrain(TypeConstraint.Owns constraint) {
        ownsConstraints.add(constraint);
        constraints.add(constraint);
        return this;
    }

    @Override
    public TypeStatement constrain(TypeConstraint.Plays constraint) {
        playsConstraints.add(constraint);
        constraints.add(constraint);
        return this;
    }

    @Override
    public TypeStatement constrain(TypeConstraint.Relates constraint) {
        if (label().isPresent()) {
            constraint.setScope(label().get().label());
        }
        relatesConstraints.add(constraint);
        constraints.add(constraint);
        return this;
    }

    public Optional<TypeConstraint.Label> label() {
        return Optional.ofNullable(labelConstraint);
    }

    public Optional<TypeConstraint.Sub> sub() {
        return Optional.ofNullable(subConstraint);
    }

    public Optional<TypeConstraint.Abstract> abstractConstraint() {
        return Optional.ofNullable(abstractConstraint);
    }

    public Optional<TypeConstraint.ValueType> valueType() {
        return Optional.ofNullable(valueTypeConstraint);
    }

    public Optional<TypeConstraint.Regex> regex() {
        return Optional.ofNullable(regexConstraint);
    }

    public List<TypeConstraint.Owns> owns() {
        return ownsConstraints;
    }

    public List<TypeConstraint.Plays> plays() {
        return playsConstraints;
    }

    public List<TypeConstraint.Relates> relates() {
        return relatesConstraints;
    }

    @Override
    public String toString(boolean pretty) {
        StringBuilder syntax = new StringBuilder();
        if (variable.isVisible() || variable.isLabelled()) {
            syntax.append(variable.isVisible() ? variable : variable.reference().asLabel().scopedLabel());
            Stream<TypeConstraint> consStream = variable.isLabelled() ? constraints().stream().filter(c -> !c.isLabel()) : constraints.stream();
            String consStr;
            if (pretty) {
                consStr = indent(consStream.map(Constraint::toString).collect(COMMA_NEW_LINE.joiner())).trim();
            } else {
                consStr = consStream.map(Constraint::toString).collect(COMMA.joiner());
            }
            if (!consStr.isEmpty()) syntax.append(SPACE).append(consStr);
        } else {
            // This should only be called by debuggers trying to print nested variables
            syntax.append(variable);
        }
        return syntax.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeStatement that = (TypeStatement) o;
        return (this.variable.equals(that.variable) &&
                set(this.constraints).equals(set(that.constraints)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.variable, set(this.constraints));
    }

    @Override
    public boolean isTypeStatement() {
        return true;
    }

    @Override
    public TypeStatement asTypeStatement() {
        return this;
    }
}
