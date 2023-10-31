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
import com.vaticle.typeql.lang.pattern.Conjunctable;
import com.vaticle.typeql.lang.pattern.Pattern;
import com.vaticle.typeql.lang.pattern.constraint.Constraint;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MATCH_HAS_UNBOUNDED_NESTED_PATTERN;

public abstract class Statement implements Conjunctable {

    public abstract TypeQLVariable headVariable();

    public Stream<TypeQLVariable> constraintVariables() {
        return constraints().stream().flatMap(constraint -> constraint.variables().stream());
    }

    public Stream<TypeQLVariable> variables() {
        return Stream.concat(Stream.of(headVariable()), constraintVariables());
    }

    public abstract List<? extends Constraint> constraints();

    @Override
    public void validateIsBoundedBy(Set<TypeQLVariable> bounds) {
        if (variables().noneMatch(bounds::contains)) {
            throw TypeQLException.of(MATCH_HAS_UNBOUNDED_NESTED_PATTERN.message(toString()));
        }
    }

    public boolean isConcept() {
        return false;
    }

    public ConceptStatement asConcept() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(ConceptStatement.class)));
    }

    public boolean isType() {
        return false;
    }

    public TypeStatement asType() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeStatement.class)));
    }

    public boolean isThing() {
        return false;
    }

    public ThingStatement<?> asThing() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(ThingStatement.class)));
    }

    public boolean isValue() {
        return false;
    }

    public ValueStatement asValue() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(ValueStatement.class)));
    }

    @Override
    public Statement normalise() {
        return this;
    }

    @Override
    public boolean isStatement() {
        return true;
    }

    @Override
    public Statement asStatement() {
        return this;
    }

    @Override
    public List<? extends Pattern> patterns() {
        return list(this);
    }

    @Override
    public String toString() {
        return toString(true);
    }
}
