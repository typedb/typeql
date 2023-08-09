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

package com.vaticle.typeql.lang.query;

import com.vaticle.typedb.common.collection.Either;
import com.vaticle.typedb.common.collection.Pair;
import com.vaticle.typeql.lang.common.TypeQLArg;
import com.vaticle.typeql.lang.pattern.variable.Reference;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;
import com.vaticle.typeql.lang.query.builder.Sortable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COLON;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COMMA_SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.QUOTE_DOUBLE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Command.FETCH;
import static com.vaticle.typeql.lang.common.TypeQLToken.Projection.AS;
import static com.vaticle.typeql.lang.query.TypeQLQuery.appendClause;
import static com.vaticle.typeql.lang.query.TypeQLQuery.appendModifiers;

public class TypeQLFetch implements TypeQLQuery {

    final MatchClause match;
    final List<Projection> projections;
    final Modifiers modifiers;

    TypeQLFetch(MatchClause match, List<Projection> projections) {
        this(match, projections, Modifiers.EMPTY);
    }

    public TypeQLFetch(MatchClause match, List<Projection> projections, Modifiers modifiers) {
        this.match = match;
        this.projections = projections;
        this.modifiers = modifiers;
    }

    @Override
    public TypeQLArg.QueryType type() {
        return TypeQLArg.QueryType.READ;
    }

    public Modifiers modifiers() {
        return modifiers;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    @Override
    public String toString(boolean pretty) {
        StringBuilder query = new StringBuilder(match.toString(pretty));
        Stream<String> projections = this.projections.stream().map(projection -> projection.toString(pretty));
        appendClause(query, FETCH, projections, pretty);
        appendModifiers(query, modifiers, pretty);
        return query.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!getClass().isAssignableFrom(o.getClass()) && !o.getClass().isAssignableFrom(getClass())) {
            return false;
        }
        TypeQLFetch that = (TypeQLFetch) o;
        return match.equals(that.match) && projections.equals(that.projections) && modifiers.equals(that.modifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(match, projections, modifiers);
    }

    public static class Unmodified extends TypeQLFetch implements TypeQLQuery.Unmodified<TypeQLFetch, TypeQLFetch.Sorted, TypeQLFetch.Offset, TypeQLFetch.Limited> {

        public Unmodified(MatchClause match, List<Projection> projections) {
            super(match, projections, Modifiers.EMPTY);
        }

        @Override
        public TypeQLFetch modifier(Modifiers modifier) {
            return new TypeQLFetch(match, projections, modifier);
        }

        @Override
        public TypeQLFetch.Sorted sort(Sortable.Sorting sorting) {
            return new TypeQLFetch.Sorted(this, sorting);
        }

        @Override
        public TypeQLFetch.Offset offset(long offset) {
            return new TypeQLFetch.Offset(this, offset);
        }

        @Override
        public TypeQLFetch.Limited limit(long limit) {
            return new TypeQLFetch.Limited(this, limit);
        }
    }

    public static class Sorted extends TypeQLFetch implements TypeQLQuery.Sorted<TypeQLFetch.Offset, TypeQLFetch.Limited> {

        public Sorted(TypeQLFetch delete, Sortable.Sorting sorting) {
            super(delete.match, delete.projections, new Modifiers(sorting, delete.modifiers.offset, delete.modifiers.limit));
        }

        @Override
        public TypeQLFetch.Offset offset(long offset) {
            return new TypeQLFetch.Offset(this, offset);
        }

        @Override
        public TypeQLFetch.Limited limit(long limit) {
            return new TypeQLFetch.Limited(this, limit);
        }
    }

    public static class Offset extends TypeQLFetch implements TypeQLQuery.Offset<TypeQLFetch.Limited> {

        public Offset(TypeQLFetch delete, long offset) {
            super(delete.match, delete.projections, new Modifiers(delete.modifiers.sorting, offset, delete.modifiers.limit));
        }

        @Override
        public TypeQLFetch.Limited limit(long limit) {
            return new TypeQLFetch.Limited(this, limit);
        }
    }

    public static class Limited extends TypeQLFetch implements TypeQLQuery.Limited {

        public Limited(TypeQLFetch delete, long limit) {
            super(delete.match, delete.projections, new Modifiers(delete.modifiers.sorting, delete.modifiers.offset, limit));
        }
    }

    public static abstract class Projection {

        final Key key;

        Projection(Key key) {
            this.key = key;
        }

        public Key key() {
            return key;
        }

        @Override
        public String toString() {
            return toString(true);
        }

        abstract String toString(boolean pretty);

        public abstract int hashCode();

        public abstract boolean equals(Object object);

        public static class Variable extends Projection {

            public Variable(Key.Variable key) {
                super(key);
            }

            @Override
            public String toString(boolean pretty) {
                return key.toString(pretty);
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Variable that = (Variable) o;
                return this.key.equals(that.key);
            }

            @Override
            public int hashCode() {
                return key.hashCode();
            }
        }

        public static class Attribute extends Projection {

            private final List<Pair<Reference.Label, Key.Label>> attributes;

            public Attribute(Key.Variable key, List<Pair<Reference.Label, Key.Label>> attributes) {
                super(key);
                this.attributes = attributes;
            }

            @Override
            String toString(boolean pretty) {
                StringBuilder builder = new StringBuilder();
                builder.append(key.toString(pretty)).append(COLON).append(SPACE);
                String attrs = attributes.stream().map(pair -> {
                    if (pair.second() == null) return pair.first().toString();
                    else return pair.first() + SPACE.toString() + AS + SPACE + pair.second();
                }).collect(COMMA_SPACE.joiner());
                builder.append(attrs);
                return builder.toString();
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Attribute that = (Attribute) o;
                return attributes.equals(that.attributes);
            }

            @Override
            public int hashCode() {
                return attributes.hashCode();
            }
        }

        public static class Subquery extends Projection {

            private final Either<TypeQLFetch, TypeQLGet.Aggregate> subquery;

            public Subquery(Key.Label key, Either<TypeQLFetch, TypeQLGet.Aggregate> subquery) {
                super(key);
                this.subquery = subquery;
            }

            @Override
            public String toString(boolean pretty) {
                StringBuilder builder = new StringBuilder();
                builder.append(key.toString(pretty)).append(COLON).append(SPACE);
                if (subquery.isFirst()) builder.append(subquery.first().toString(pretty));
                else builder.append(subquery.second().toString(pretty));
                return builder.toString();
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Subquery that = (Subquery) o;
                return subquery.equals(that.subquery);
            }

            @Override
            public int hashCode() {
                return subquery.hashCode();
            }
        }

        public interface Key {

            String toString(boolean pretty);

            class Variable implements Key {

                private final UnboundVariable variable;
                private final Label label;

                Variable(UnboundVariable variable) {
                    this(variable, null);
                }

                public Variable(UnboundVariable variable, @Nullable Label label) {
                    this.variable = variable;
                    this.label = label;
                }

                @Override
                public String toString(boolean pretty) {
                    return label == null ? variable.toString(pretty) : variable.toString(pretty) + AS + label.toString(pretty);
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) return true;
                    if (o == null || getClass() != o.getClass()) return false;
                    Variable that = (Variable) o;
                    return variable.equals(that.variable) && Objects.equals(label, that.label);
                }

                @Override
                public int hashCode() {
                    return Objects.hash(variable, label);
                }
            }

            class Label implements Key {

                Either<String, String> quotedOrUnquoted;

                public Label(Either<String, String> quotedOrUnquoted) {
                    this.quotedOrUnquoted = quotedOrUnquoted;
                }

                @Override
                public String toString(boolean pretty) {
                    // TODO: what about escape behaviour?
                    if (quotedOrUnquoted.isFirst()) return QUOTE_DOUBLE + quotedOrUnquoted.first() + QUOTE_DOUBLE;
                    else return quotedOrUnquoted.second();
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) return true;
                    if (o == null || getClass() != o.getClass()) return false;
                    Label that = (Label) o;
                    return quotedOrUnquoted.equals(that.quotedOrUnquoted);
                }

                @Override
                public int hashCode() {
                    return quotedOrUnquoted.hashCode();
                }
            }
        }
    }
}
