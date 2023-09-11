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
import com.vaticle.typeql.lang.common.Reference;
import com.vaticle.typeql.lang.common.TypeQLArg;
import com.vaticle.typeql.lang.common.TypeQLVariable;
import com.vaticle.typeql.lang.query.builder.ProjectionBuilder;
import com.vaticle.typeql.lang.query.builder.Sortable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COLON;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COMMA_SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.CURLY_CLOSE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.CURLY_OPEN;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Command.FETCH;
import static com.vaticle.typeql.lang.common.TypeQLToken.Projection.AS;
import static com.vaticle.typeql.lang.common.util.Strings.indent;
import static com.vaticle.typeql.lang.common.util.Strings.quoteString;
import static com.vaticle.typeql.lang.query.TypeQLQuery.appendClause;
import static com.vaticle.typeql.lang.query.TypeQLQuery.appendModifiers;
import static java.util.stream.Collectors.toList;

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

    public MatchClause match() {
        return match;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    @Override
    public String toString(boolean pretty) {
        StringBuilder query = new StringBuilder(match.toString(pretty)).append(NEW_LINE);
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
        public TypeQLFetch modifiers(Modifiers modifier) {
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

    public interface Key {

        String toString(boolean pretty);

        interface Var extends Key, Projection, ProjectionBuilder.Attribute {

            default Var key() {
                return this;
            }

            interface UnlabelledVar extends Var {
                default LabelledVar asLabel(String label) {
                    return asLabel(Label.of(label));
                }

                LabelledVar asLabel(Label label);

            }

            class LabelledVar implements Var {

                private final TypeQLVariable variable;
                private final Label label;

                public LabelledVar(TypeQLVariable variable, Label label) {
                    this.variable = variable;
                    this.label = label;
                }

                @Override
                public LabelledVar key() {
                    return this;
                }

                @Override
                public Attribute projectAttr(Pair<Reference.Label, Label> attribute) {
                    return new Attribute(this, list(attribute));
                }

                @Override
                public Attribute projectAttrs(Stream<Pair<Reference.Label, Label>> attributes) {
                    return new Attribute(this, attributes.collect(toList()));
                }

                @Override
                public String toString() {
                    return toString(true);
                }

                @Override
                public String toString(boolean pretty) {
                    return variable.toString(pretty) + SPACE + AS + SPACE + label.toString(pretty);
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) return true;
                    if (o == null || getClass() != o.getClass()) return false;
                    LabelledVar that = (LabelledVar) o;
                    return variable.equals(that.variable) && label.equals(that.label);
                }

                @Override
                public int hashCode() {
                    return Objects.hash(variable, label);
                }
            }
        }

        class Label implements Key, ProjectionBuilder.Subquery {

            Either<String, String> quotedOrUnquoted;

            private Label(Either<String, String> quotedOrUnquoted) {
                this.quotedOrUnquoted = quotedOrUnquoted;
            }

            public static Label of(String label) {
                if (Reference.Name.REGEX.matcher(label).matches()) {
                    return unquoted(label);
                } else {
                    return quoted(label);
                }
            }

            public static Label quoted(String label) {
                return new Label(Either.first(label));
            }

            public static Label unquoted(String label) {
                return new Label(Either.second(label));
            }

            @Override
            public String toString() {
                return toString(true);
            }

            @Override
            public String toString(boolean pretty) {
                if (quotedOrUnquoted.isFirst()) return quoteString(quotedOrUnquoted.first());
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

            @Override
            public Projection.Subquery projectSubquery(Either<TypeQLFetch, TypeQLGet.Aggregate> subquery) {
                return new Projection.Subquery(this, subquery);
            }
        }
    }

    public interface Projection {

        Key key();

        String toString(boolean pretty);

        class Attribute implements Projection, ProjectionBuilder.Attribute {

            private final Key key;
            private final List<Pair<Reference.Label, Key.Label>> attributes;

            public Attribute(Key key, List<Pair<Reference.Label, Key.Label>> attributes) {
                this.key = key;
                this.attributes = attributes;
            }

            @Override
            public Key key() {
                return key;
            }

            @Override
            public String toString(boolean pretty) {
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
                return key.equals(that.key) && attributes.equals(that.attributes);
            }

            @Override
            public int hashCode() {
                return Objects.hash(key, attributes);
            }

            @Override
            public Attribute projectAttr(Pair<Reference.Label, Key.Label> attribute) {
                return new Attribute(key(), list(attributes, attribute));
            }

            @Override
            public Attribute projectAttrs(Stream<Pair<Reference.Label, Key.Label>> attributes) {
                return new Attribute(key(), Stream.concat(this.attributes.stream(), attributes).collect(toList()));
            }
        }

        class Subquery implements Projection {

            private final Key.Label key;
            private final Either<TypeQLFetch, TypeQLGet.Aggregate> subquery;

            public Subquery(Key.Label key, Either<TypeQLFetch, TypeQLGet.Aggregate> subquery) {
                this.key = key;
                this.subquery = subquery;
            }

            @Override
            public Key key() {
                return key;
            }

            @Override
            public String toString(boolean pretty) {
                StringBuilder builder = new StringBuilder();
                builder.append(key.toString(pretty)).append(COLON).append(SPACE).append(CURLY_OPEN);
                if (pretty) builder.append(NEW_LINE);
                else builder.append(SPACE);
                if (subquery.isFirst()) builder.append(indent(subquery.first().toString(pretty)));
                else builder.append(indent(subquery.second().toString(pretty)));
                if (pretty) builder.append(NEW_LINE);
                else builder.append(SPACE);
                return builder.append(CURLY_CLOSE).toString();
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Subquery that = (Subquery) o;
                return key.equals(that.key) && subquery.equals(that.subquery);
            }

            @Override
            public int hashCode() {
                return Objects.hash(key, subquery);
            }
        }
    }
}
