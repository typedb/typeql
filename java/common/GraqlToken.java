/*
 * Copyright (C) 2020 Grakn Labs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package graql.lang.common;

public class GraqlToken {

    public enum Type {
        THING("thing"),
        ENTITY("entity"),
        ATTRIBUTE("attribute"),
        RELATION("relation"),
        ROLE("role");

        private final String type;

        Type(final String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return this.type;
        }

        public static Type of(final String value) {
            for (Type c : Type.values()) {
                if (c.type.equals(value)) {
                    return c;
                }
            }
            return null;
        }
    }

    public enum Command {
        COMPUTE("compute"),
        MATCH("match"),
        DEFINE("define"),
        UNDEFINE("undefine"),
        INSERT("insert"),
        DELETE("delete"),
        GET("get"),
        AGGREGATE("aggregate"),
        GROUP("group");

        private final String command;

        Command(final String command) {
            this.command = command;
        }

        @Override
        public String toString() {
            return this.command;
        }

        public static Command of(final String value) {
            for (Command c : Command.values()) {
                if (c.command.equals(value)) {
                    return c;
                }
            }
            return null;
        }
    }

    public enum Filter {
        SORT("sort"),
        OFFSET("offset"),
        LIMIT("limit");

        private final String filter;

        Filter(final String filter) {
            this.filter = filter;
        }

        @Override
        public String toString() {
            return this.filter;
        }

        public static Filter of(final String value) {
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
        SPACE(" "),
        COMMA(","),
        COMMA_SPACE(", "),
        CURLY_OPEN("{"),
        CURLY_CLOSE("}"),
        PARAN_OPEN("("),
        PARAN_CLOSE(")"),
        SQUARE_OPEN("["),
        SQUARE_CLOSE("]"),
        QUOTE("\""),
        NEW_LINE("\n"),
        UNDERSCORE("_"),
        $_("$_"),
        $("$");

        private final String character;

        Char(final String character) {
            this.character = character;
        }

        @Override
        public String toString() {
            return this.character;
        }
    }

    public enum Operator {
        AND("and"),
        OR("or"),
        NOT("not");

        private final String operator;

        Operator(final String operator) {
            this.operator = operator;
        }

        @Override
        public String toString() {
            return this.operator;
        }

        public static Operator of(final String value) {
            for (Operator c : Operator.values()) {
                if (c.operator.equals(value)) {
                    return c;
                }
            }
            return null;
        }
    }

    public enum Comparator {
        NOT("not"),
        EQ("="),
        NEQ("!="),
        GT(">"),
        GTE(">="),
        LT("<"),
        LTE("<="),
        CONTAINS("contains"),
        LIKE("like");

        private final String comparator;

        Comparator(final String comparator) {
            this.comparator = comparator;
        }

        @Override
        public String toString() {
            return this.comparator;
        }

        public static Comparator of(final String value) {
            for (Comparator c : Comparator.values()) {
                if (c.comparator.equals(value)) {
                    return c;
                }
            }
            return null;
        }
    }

    public enum Schema {
        RULE("rule"),
        THEN("then"),
        WHEN("when");

        private final String name;

        Schema(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public static Schema of(final String value) {
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

        Constraint(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public static Constraint of(final String value) {
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

        Literal(final String type) {
            this.literal = type;
        }

        @Override
        public String toString() {
            return this.literal;
        }

        public static Literal of(final String value) {
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

            Method(final String method) {
                this.method = method;
            }

            @Override
            public String toString() {
                return this.method;
            }

            public static Aggregate.Method of(final String value) {
                for (Aggregate.Method m : Aggregate.Method.values()) {
                    if (m.method.equals(value)) {
                        return m;
                    }
                }
                return null;
            }
        }
    }

    public static class Compute {

        public enum Method {
            COUNT("count"),
            MIN("min"),
            MAX("max"),
            MEDIAN("median"),
            MEAN("mean"),
            STD("std"),
            SUM("sum"),
            PATH("path"),
            CENTRALITY("centrality"),
            CLUSTER("cluster");

            private final String method;

            Method(final String method) {
                this.method = method;
            }

            @Override
            public String toString() {
                return this.method;
            }

            public static Compute.Method of(final String name) {
                for (Compute.Method m : Compute.Method.values()) {
                    if (m.method.equals(name)) {
                        return m;
                    }
                }
                return null;
            }
        }

        /**
         * Graql Compute conditions keyword
         */
        public enum Condition {
            FROM("from"),
            TO("to"),
            OF("of"),
            IN("in"),
            USING("using"),
            WHERE("where");

            private final String condition;

            Condition(final String algorithm) {
                this.condition = algorithm;
            }

            @Override
            public String toString() {
                return this.condition;
            }

            public static Compute.Condition of(final String value) {
                for (Compute.Condition c : Compute.Condition.values()) {
                    if (c.condition.equals(value)) {
                        return c;
                    }
                }
                return null;
            }
        }

        /**
         * Graql Compute parameter names
         */
        public enum Param {
            MIN_K("min-k"),
            K("k"),
            CONTAINS("contains"),
            SIZE("size");

            private final String param;

            Param(final String param) {
                this.param = param;
            }

            @Override
            public String toString() {
                return this.param;
            }

            public static Compute.Param of(final String value) {
                for (Compute.Param p : Compute.Param.values()) {
                    if (p.param.equals(value)) {
                        return p;
                    }
                }
                return null;
            }
        }
    }
}
