/*
 * Copyright (C) 2021 Vaticle
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

package com.vaticle.typeql.lang.pattern;

import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.exception.ErrorMessage;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.CURLY_CLOSE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.CURLY_OPEN;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SEMICOLON;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;

/**
 * A class representing a negation of patterns. All inner patterns must not match in a query.
 *
 * @param <T> the type of patterns in this negation
 */
public class Negation<T extends Pattern> implements Conjunctable {

    private final T pattern;
    private Negation<Disjunction<Conjunction<Conjunctable>>> normalised;

    public Negation(T pattern) {
        if (pattern == null) throw new NullPointerException("Null patterns");
        else if (pattern.isNegation()) throw TypeQLException.of(ErrorMessage.REDUNDANT_NESTED_NEGATION);
        this.pattern = pattern;
    }

    public T pattern() { return pattern; }

    @Override
    public List<? extends Pattern> patterns() {
        return list(pattern);
    }

    @Override
    public void validateIsBoundedBy(Set<UnboundVariable> bounds) {
        if (pattern.isNegation()) {
            throw TypeQLException.of(ErrorMessage.ILLEGAL_STATE);
        } else {
            pattern.validateIsBoundedBy(bounds);
        }
    }

    @Override
    public Negation<Disjunction<Conjunction<Conjunctable>>> normalise() {
        if (normalised == null) {
            if (pattern.isNegation()) {
                throw TypeQLException.of(ErrorMessage.ILLEGAL_STATE);
            } else if (pattern.isVariable()) {
                normalised = new Negation<>(new Disjunction<>(list(new Conjunction<>(list(pattern.asVariable())))));
            } else {
                if (pattern.isConjunction()) normalised = new Negation<>(pattern.asConjunction().normalise());
                else normalised = new Negation<>(pattern.asDisjunction().normalise());
            }
        }
        return normalised;
    }

    @Override
    public boolean isNegation() { return true; }

    @Override
    public Negation<?> asNegation() { return this; }

    @Override
    public String toString() {
        StringBuilder negation = new StringBuilder();
        negation.append(TypeQLToken.Operator.NOT).append(SPACE);

        if (pattern.isConjunction()) {
            negation.append(pattern.toString());
        } else {
            negation.append(CURLY_OPEN).append(SPACE);
            negation.append(pattern.toString()).append(SEMICOLON);
            negation.append(SPACE).append(CURLY_CLOSE);
        }

        return negation.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Negation<?> negation = (Negation<?>) o;
        return Objects.equals(pattern, negation.pattern);
    }

    @Override
    public int hashCode() {
        return pattern.hashCode();
    }
}

