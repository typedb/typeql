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

import graql.lang.common.exception.GraqlException;

import static grakn.common.util.Objects.className;
import static graql.lang.common.exception.ErrorMessage.INVALID_CASTING;

public class GraqlToken {

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

    public interface Comparator {

        default boolean isEquality() {
            return false;
        }

        default boolean isSubString() {
            return false;
        }

        default Equality asEquality() {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Equality.class)));
        }

        default SubString asSubString() {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(SubString.class)));
        }

        enum Equality implements Comparator {
            EQ("="),
            NEQ("!="),
            GT(">"),
            GTE(">="),
            LT("<"),
            LTE("<=");

            private final String comparator;

            Equality(String comparator) {
                this.comparator = comparator;
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
                return this.comparator;
            }

            public static Equality of(String value) {
                for (Equality c : Equality.values()) {
                    if (c.comparator.equals(value)) {
                        return c;
                    }
                }
                return null;
            }
        }

        enum SubString implements Comparator {
            CONTAINS("contains"),
            LIKE("like");

            private final String comparator;

            SubString(String comparator) {
                this.comparator = comparator;
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
                return this.comparator;
            }

            public static SubString of(String value) {
                for (SubString c : SubString.values()) {
                    if (c.comparator.equals(value)) {
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
