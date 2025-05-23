/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.typeql.lang.common;

import com.typeql.lang.common.exception.TypeQLException;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.typedb.common.util.Objects.className;
import static com.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;

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

    public enum Clause {
        DEFINE("define"),
        UNDEFINE("undefine"),
        INSERT("insert"),
        DELETE("delete"),
        MATCH("match"),
        GET("get"),
        AGGREGATE("aggregate"),
        GROUP("group"),
        FETCH("fetch");

        private final String clause;

        Clause(String clause) {
            this.clause = clause;
        }

        @Override
        public String toString() {
            return this.clause;
        }

        public static Clause of(String value) {
            for (Clause c : Clause.values()) {
                if (c.clause.equals(value)) {
                    return c;
                }
            }
            return null;
        }
    }

    public enum Modifier {
        SORT("sort"),
        OFFSET("offset"),
        LIMIT("limit");

        private final String modifier;

        Modifier(String modifier) {
            this.modifier = modifier;
        }

        @Override
        public String toString() {
            return this.modifier;
        }

        public static Modifier of(String value) {
            for (Modifier c : Modifier.values()) {
                if (c.modifier.equals(value)) {
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
        SEMICOLON_SPACE("; "),
        SEMICOLON_NEW_LINE(";\n"),
        SPACE(" "),
        COMMA(","),
        COMMA_SPACE(", "),
        AT("@"),
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
        $("$"),
        QUESTION_MARK("?");

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

        public static Operator of(String value) {
            for (Operator c : Operator.values()) {
                if (c.operator.equals(value)) {
                    return c;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return this.operator;
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
            EQ("=="),
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
        ASSIGN("="),
        HAS("has"),
        IID("iid"),
        IS("is"),
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

    public enum Annotation {
        KEY("key"),
        UNIQUE("unique");

        private final String name;

        Annotation(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return Char.AT + name;
        }

        public static Annotation of(String value) {
            for (Annotation annotation: Annotation.values()) {
                if (annotation.name.equals(value)) {
                    return annotation;
                }
            }
            return null;
        }
    }

    public enum Projection {
        AS("as");

        private final String name;

        Projection(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public static Projection of(String value) {
            for (Projection projection: Projection.values()) {
                if (projection.name.equals(value)) {
                    return projection;
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

    public static class Expression {
        public enum Operation {
            ADD("+"),
            SUBTRACT("-"),
            MULTIPLY("*"),
            DIVIDE("/"),
            MODULO("%"),
            POWER("^");

            private final String symbol;

            Operation(String symbol) {
                this.symbol = symbol;
            }

            @Override
            public String toString() {
                return this.symbol;
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

        public enum Function {
            MAX("max"),
            MIN("min"),
            FLOOR("floor"),
            CEIL("ceil"),
            ROUND("round"),
            ABS("abs");

            private final String symbol;

            Function(String symbol) {
                this.symbol = symbol;
            }
            
            @Override
            public String toString() {
                return this.symbol;
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
    }
}
