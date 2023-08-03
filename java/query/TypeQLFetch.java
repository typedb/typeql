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
import com.vaticle.typeql.lang.common.TypeQLArg;
import com.vaticle.typeql.lang.pattern.variable.Reference;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class TypeQLFetch implements TypeQLQuery {

    private final MatchClause match;
    private final List<Entry> entries;

    TypeQLFetch(MatchClause match, List<Entry> entries) {
        this.match = match;
        this.entries = entries;
    }

    @Override
    public TypeQLArg.QueryType type() {
        return TypeQLArg.QueryType.READ;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    @Override
    public String toString(boolean pretty) {
        return null;
    }

    static abstract class Entry {

        final Name name;

        Entry(Name name) {
            this.name = name;
        }

        public Name name() {
            return name;
        }

        @Override
        public String toString() {
            return toString(true);
        }

        public String toString(boolean pretty) {
            // TODO
            return "";
        }

        static class Variable extends Entry {

            Variable(Name name) {
                super(name);
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Variable that = (Variable) o;
                return this.name.equals(that.name);
            }

            @Override
            public int hashCode() {
                return name.hashCode();
            }
        }

        static class Attributes extends Entry {

            private final List<Attribute> attributes;

            Attributes(Name name, List<Attribute> attributes) {
                super(name);
                this.attributes = attributes;
            }

            static class Attribute {

                private final Reference.Label attribute;
                private final Name.String name;

                Attribute(Reference.Label attribute, @Nullable Name.String name) {
                    this.attribute = attribute;
                    this.name = name;
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) return true;
                    if (o == null || getClass() != o.getClass()) return false;
                    Attribute that = (Attribute) o;
                    return attribute.equals(that.attribute) && Objects.equals(name, that.name);
                }

                @Override
                public int hashCode() {
                    return Objects.hash(attribute, name);
                }
            }
        }

        static class Subquery extends Entry {

            private final Either<TypeQLFetch, TypeQLGet.Aggregate> subquery;

            Subquery(Name name, Either<TypeQLFetch, TypeQLGet.Aggregate> subquery) {
                super(name);
                this.subquery = subquery;
            }

        }

        interface Name {

            class Variable implements Name {

                private final UnboundVariable variable;
                private final String name;

                Variable(UnboundVariable variable) {
                    this(variable, null);
                }

                public Variable(UnboundVariable variable, @Nullable String name) {
                    this.variable = variable;
                    this.name = name;
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) return true;
                    if (o == null || getClass() != o.getClass()) return false;
                    Variable that = (Variable) o;
                    return variable.equals(that.variable) && Objects.equals(name, that.name);
                }

                @Override
                public int hashCode() {
                    return Objects.hash(variable, name);
                }
            }

            class String implements Name {
                java.lang.String name;

                String(java.lang.String name) {
                    this.name = name;
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) return true;
                    if (o == null || getClass() != o.getClass()) return false;
                    String that = (String) o;
                    return name.equals(that.name);
                }

                @Override
                public int hashCode() {
                    return name.hashCode();
                }
            }
        }
    }
}
