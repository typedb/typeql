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

package com.vaticle.typeql.lang.pattern.constraint;

import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.TypeQLVariable;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.common.util.Strings;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import static com.vaticle.typedb.common.collection.Collections.set;
import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.EQ;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.SubString.LIKE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CONSTRAINT_DATETIME_PRECISION;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MISSING_CONSTRAINT_PREDICATE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MISSING_CONSTRAINT_VALUE;
import static com.vaticle.typeql.lang.common.util.Strings.escapeRegex;
import static com.vaticle.typeql.lang.common.util.Strings.quoteString;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

public abstract class Predicate<T> {

    private final TypeQLToken.Predicate predicate;
    private final T value;
    private final int hash;

    Predicate(TypeQLToken.Predicate predicate, T value) {
        if (predicate == null) throw TypeQLException.of(MISSING_CONSTRAINT_PREDICATE);
        else if (value == null) throw TypeQLException.of(MISSING_CONSTRAINT_VALUE);

        assert !predicate.isEquality() || value instanceof Comparable || value instanceof TypeQLVariable;
        assert !predicate.isSubString() || value instanceof java.lang.String;

        this.predicate = predicate;
        this.value = value;
        this.hash = Objects.hash(Predicate.class, this.predicate, this.value);
    }

    public Set<TypeQLVariable> variables() {
        return emptySet();
    }

    public TypeQLToken.Predicate predicate() {
        return predicate;
    }

    public T value() {
        return value;
    }

    public boolean isLong() {
        return false;
    }

    public boolean isDouble() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    public boolean isDateTime() {
        return false;
    }

    public boolean isVariable() {
        return false;
    }

    public Predicate.Long asLong() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Predicate.Long.class)));
    }

    public Predicate.Double asDouble() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Predicate.Double.class)));
    }

    public Predicate.Boolean asBoolean() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Predicate.Boolean.class)));
    }

    public Predicate.String asString() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Predicate.String.class)));
    }

    public Predicate.DateTime asDateTime() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Predicate.DateTime.class)));
    }

    public Predicate.Variable asVariable() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Variable.class)));
    }

    @Override
    public java.lang.String toString() {
        if (predicate.equals(EQ) && !isVariable()) return Strings.valueToString(value);
        else return predicate.toString() + SPACE + Strings.valueToString(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Predicate<?> that = (Predicate<?>) o;
        return (this.predicate.equals(that.predicate) && this.value.equals(that.value));
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public static class Long extends Predicate<java.lang.Long> {

        public Long(TypeQLToken.Predicate.Equality predicate, long value) {
            super(predicate, value);
        }

        @Override
        public boolean isLong() {
            return true;
        }

        @Override
        public Long asLong() {
            return this;
        }
    }

    public static class Double extends Predicate<java.lang.Double> {

        public Double(TypeQLToken.Predicate.Equality predicate, double value) {
            super(predicate, value);
        }

        @Override
        public boolean isDouble() {
            return true;
        }

        @Override
        public Double asDouble() {
            return this;
        }
    }

    public static class Boolean extends Predicate<java.lang.Boolean> {

        public Boolean(TypeQLToken.Predicate.Equality predicate, boolean value) {
            super(predicate, value);
        }

        @Override
        public boolean isBoolean() {
            return true;
        }

        @Override
        public Boolean asBoolean() {
            return this;
        }
    }

    public static class String extends Predicate<java.lang.String> {

        public String(TypeQLToken.Predicate predicate, java.lang.String value) {
            super(predicate, value);
        }

        @Override
        public boolean isString() {
            return true;
        }

        @Override
        public java.lang.String toString() {
            StringBuilder operation = new StringBuilder();

            if (predicate().equals(LIKE)) {
                operation.append(LIKE).append(SPACE).append(quoteString(escapeRegex(value())));
            } else if (predicate().equals(EQ)) {
                operation.append(quoteString(value()));
            } else {
                operation.append(predicate()).append(SPACE).append(quoteString(value()));
            }

            return operation.toString();
        }

        @Override
        public String asString() {
            return this;
        }
    }

    public static class DateTime extends Predicate<LocalDateTime> {

        public DateTime(TypeQLToken.Predicate.Equality predicate, LocalDateTime value) {
            super(predicate, value);
            // validate precision of fractional seconds, which are stored as nanos in LocalDateTime
            int nanos = value.toLocalTime().getNano();
            final long nanosPerMilli = 1000000L;
            long remainder = nanos % nanosPerMilli;
            if (remainder != 0) {
                throw TypeQLException.of(INVALID_CONSTRAINT_DATETIME_PRECISION.message(value));
            }
        }

        @Override
        public boolean isDateTime() {
            return true;
        }

        @Override
        public DateTime asDateTime() {
            return this;
        }
    }

    public static class Variable extends Predicate<TypeQLVariable> {

        public Variable(TypeQLToken.Predicate.Equality predicate, TypeQLVariable variable) {
            super(predicate, variable);
        }

        @Override
        public Set<TypeQLVariable> variables() {
            return singleton(value());
        }

        @Override
        public boolean isVariable() {
            return true;
        }

        @Override
        public Variable asVariable() {
            return this;
        }
    }
}
