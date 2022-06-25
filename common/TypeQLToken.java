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

package com.vaticle.typeql.lang.common;

import com.vaticle.typeql.lang.common.exception.TypeQLException;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;

public class TypeQLToken {

    public enum Type {
        THING("thing"),
        ENTITY("entity"),
        ATTRIBUTE("attribute"),
        RELATION("relation"),
        ROLE("role");

        private final String type;

        Type(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return this.type;
        }

        public static Type of(String value) {
            for (Type c : Type.values()) {
                if (c.type.equals(value)) {
                    return c;
                }
            }
            return null;
        }
    }

    public enum Command {
        DEFINE("define"),
        UNDEFINE("undefine"),
        INSERT("insert"),
        DELETE("delete"),
        MATCH("match"),
        AGGREGATE("aggregate"),
        GROUP("group");

        private final String command;

        Command(String command) {
            this.command = command;
        }

        @Override
        public String toString() {
            return this.command;
        }

        public static Command of(String value) {
            for (Command c : Command.values()) {
                if (c.command.equals(value)) {
                    return c;
                }
            }
            return null;
        }
    }

    public enum Filter {
        GET("get"),
        SORT("sort"),
        OFFSET("offset"),
        LIMIT("limit");

        private final String filter;

        Filter(String filter) {
            this.filter = filter;
        }

        @Override
        public String toString() {
            return this.filter;
        }

        public static Filter of(String value) {
            for (Filter c : Filter.values()) {
                if (c.filter.equals(value)) {
                    return c;
                }
            }
            return null;
        }
    }

    public enum Char {
        EQUAL("="),
        COLON(":"),
        SEMICOLON(";"),
        SEMICOLON_NEW_LINE(";\n"),
        SPACE(" "),
        COMMA(","),
        COMMA_SPACE(", "),
        COMMA_NEW_LINE(",\n"),
        CURLY_OPEN("{"),
        CURLY_CLOSE("}"),
        PARAN_OPEN("("),
        PARAN_CLOSE(")"),
        SQUARE_OPEN("["),
        SQUARE_CLOSE("]"),
        QUOTE_DOUBLE("\""),
        QUOTE_SINGLE("'"),
        NEW_LINE("\n"),
        INDENTATION("    "),
        UNDERSCORE("_"),
        $_("$_"),
        $("$");

        private final String character;

        Char(String character) {
            this.character = character;
        }

        public Collector<CharSequence, ?, String> joiner() {
            return Collectors.joining(character);
        }

        @Override
        public String toString() {
            return this.character;
        }

        public static Char of(String value) {
            for (Char c : Char.values()) {
                if (c.character.equals(value)) {
                    return c;
                }
            }
            return null;
        }
    }

    public enum Operator {
        AND("and"),
        OR("or"),
        NOT("not");

        private final String operator;

        Operator(String operator) {
            this.operator = operator;
        }

        @Override
        public String toString() {
            return this.operator;
        }

        public static Operator of(String value) {
            for (Operator c : Operator.values()) {
                if (c.operator.equals(value)) {
                    return c;
                }
            }
            return null;
        }
    }

    public interface Predicate {

        default boolean isEquality() {
            return false;
        }

        default boolean isSubString() {
            return false;
        }

        default Equality asEquality() {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Equality.class)));
        }

        default SubString asSubString() {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(SubString.class)));
        }

        enum Equality implements Predicate {
            EQ("="),
            NEQ("!="),
            GT(">"),
            GTE(">="),
            LT("<"),
            LTE("<=");

            private final String predicate;

            Equality(String predicate) {
                this.predicate = predicate;
            }

            @Override
            public boolean isEquality() {
                return true;
            }

            @Override
            public Equality asEquality() {
                return this;
            }

            @Override
            public String toString() {
                return this.predicate;
            }

            public static Equality of(String value) {
                for (Equality c : Equality.values()) {
                    if (c.predicate.equals(value)) {
                        return c;
                    }
                }
                return null;
            }
        }

        enum SubString implements Predicate {
            CONTAINS("contains"),
            LIKE("like");

            private final String predicate;

            SubString(String predicate) {
                this.predicate = predicate;
            }

            @Override
            public boolean isSubString() {
                return true;
            }

            @Override
            public SubString asSubString() {
                return this;
            }

            @Override
            public String toString() {
                return this.predicate;
            }

            public static SubString of(String value) {
                for (SubString c : SubString.values()) {
                    if (c.predicate.equals(value)) {
                        return c;
                    }
                }
                return null;
            }
        }
    }

    public enum Schema {
        RULE("rule"),
        THEN("then"),
        WHEN("when");

        private final String name;

        Schema(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public static Schema of(String value) {
            for (Schema c : Schema.values()) {
                if (c.name.equals(value)) {
                    return c;
                }
            }
            return null;
        }
    }

    public enum Constraint {
        ABSTRACT("abstract"),
        AS("as"),
        HAS("has"),
        IID("iid"),
        IS("is"),
        IS_KEY("@key"),
        ISA("isa"),
        ISAX("isa!"),
        OWNS("owns"),
        PLAYS("plays"),
        REGEX("regex"),
        RELATES("relates"),
        SUB("sub"),
        SUBX("sub!"),
        TYPE("type"),
        VALUE(""),
        VALUE_TYPE("value");

        private final String name;

        Constraint(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public static Constraint of(String value) {
            for (Constraint c : Constraint.values()) {
                if (c.name.equals(value)) {
                    return c;
                }
            }
            return null;
        }
    }

    public enum Literal {
        TRUE("true"),
        FALSE("false");

        private final String literal;

        Literal(String type) {
            this.literal = type;
        }

        @Override
        public String toString() {
            return this.literal;
        }

        public static Literal of(String value) {
            for (Literal c : Literal.values()) {
                if (c.literal.equals(value)) {
                    return c;
                }
            }
            return null;
        }
    }

    public static class Aggregate {

        public enum Method {
            COUNT("count"),
            MAX("max"),
            MEAN("mean"),
            MEDIAN("median"),
            MIN("min"),
            STD("std"),
            SUM("sum");

            private final String method;

            Method(String method) {
                this.method = method;
            }

            @Override
            public String toString() {
                return this.method;
            }

            public static Aggregate.Method of(String value) {
                for (Aggregate.Method m : Aggregate.Method.values()) {
                    if (m.method.equals(value)) {
                        return m;
                    }
                }
                return null;
            }
        }
    }

}
