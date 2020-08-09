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
        ROLE("role"),
        RULE("rule");

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

        Char(String character) {
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

    public enum Comparator {
        EQ("="),
        NEQ("!="),
        EQV("=="),
        NEQV("!=="),
        GT(">"),
        GTE(">="),
        LT("<"),
        LTE("<="),
        CONTAINS("contains"),
        LIKE("like");

        private final String comparator;

        Comparator(String comparator) {
            this.comparator = comparator;
        }

        @Override
        public String toString() {
            return this.comparator;
        }

        public static Comparator of(String value) {
            for (Comparator c : Comparator.values()) {
                if (c.comparator.equals(value)) {
                    return c;
                }
            }
            return null;
        }
    }

    public enum Property {
        ABSTRACT("abstract"),
        AS("as"),
        HAS("has"),
        KEY("key"),
        ID("id"), // TODO: rename to 'IID'
        ISA("isa"),
        ISAX("isa!"),
        PLAYS("plays"),
        REGEX("regex"),
        RELATES("relates"),
        SUB("sub"),
        SUBX("sub!"),
        THEN("then"),
        TYPE("type"),
        VALUE(""),
        VALUE_TYPE("value"),
        WHEN("when");

        private final String name;

        Property(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public static Property of(String value) {
            for (Property c : Property.values()) {
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

            Method(String method) {
                this.method = method;
            }

            @Override
            public String toString() {
                return this.method;
            }

            public static Compute.Method of(String name) {
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

            Condition(String algorithm) {
                this.condition = algorithm;
            }

            @Override
            public String toString() {
                return this.condition;
            }

            public static Compute.Condition of(String value) {
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

            Param(String param) {
                this.param = param;
            }

            @Override
            public String toString() {
                return this.param;
            }

            public static Compute.Param of(String value) {
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
