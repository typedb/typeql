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

package graql.lang;

import graql.lang.parser.Parser;
import graql.lang.pattern.Conjunction;
import graql.lang.pattern.Disjunction;
import graql.lang.pattern.Negation;
import graql.lang.pattern.Pattern;
import graql.lang.query.GraqlCompute;
import graql.lang.query.GraqlDefine;
import graql.lang.query.GraqlInsert;
import graql.lang.query.GraqlQuery;
import graql.lang.query.GraqlUndefine;
import graql.lang.query.MatchClause;
import graql.lang.variable.ThingVariable;
import graql.lang.variable.TypeVariable;
import graql.lang.variable.UnscopedVariable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static grakn.common.collection.Collections.list;
import static graql.lang.variable.UnscopedVariable.hidden;

/**
 * Main class containing static methods for creating Graql queries.
 * It is recommended you statically import these methods.
 */
public class Graql {

    private static final Parser parser = new Parser();

    public static <T extends GraqlQuery> T parse(String queryString) {
        return parser.parseQueryEOF(queryString);
    }

    public static <T extends GraqlQuery> Stream<T> parseList(String queryString) {
        return parser.parseQueryListEOF(queryString);
    }

    public static Pattern parsePattern(String pattern) {
        return parser.parsePatternEOF(pattern);
    }

    public static List<? extends Pattern> parsePatternList(String pattern) {
        return parser.parsePatternListEOF(pattern);
    }

    /**
     * @param patterns an array of patterns to match in the graph
     * @return a match clause that will find matches of the given patterns
     */
    public static MatchClause match(Pattern... patterns) {
        return match(Arrays.asList(patterns));
    }

    /**
     * @param patterns a collection of patterns to match in the graph
     * @return a match clause that will find matches of the given patterns
     */
    public static MatchClause match(List<? extends Pattern> patterns) {
        return new MatchClause(and(patterns));
    }

    /**
     * @param things an array of variable patterns to insert into the graph
     * @return an insert query that will insert the given variable patterns into the graph
     */
    public static GraqlInsert insert(ThingVariable<?>... things) {
        return new GraqlInsert(list(things));
    }

    public static GraqlInsert insert(List<ThingVariable<?>> things) {
        return new GraqlInsert(things);
    }

    /**
     * @param types an array of of types to define the schema
     * @return a define query that will apply the changes described in the {@code patterns}
     */
    public static GraqlDefine define(TypeVariable... types) {
        return new GraqlDefine(list(types));
    }

    public static GraqlDefine define(List<TypeVariable> types) {
        return new GraqlDefine(types);
    }

    /**
     * @param types an array of types to undefine the schema
     * @return an undefine query that will remove the changes described in the {@code patterns}
     */
    public static GraqlUndefine undefine(TypeVariable... types) {
        return new GraqlUndefine(list(types));
    }

    public static GraqlUndefine undefine(List<TypeVariable> types) {
        return new GraqlUndefine(types);
    }

    public static GraqlCompute.Builder compute() {
        return new GraqlCompute.Builder();
    }

    // Pattern Builder Methods

    /**
     * @param patterns an array of patterns to match
     * @return a pattern that will match only when all contained patterns match
     */
    public static Conjunction<? extends Pattern> and(Pattern... patterns) {
        return and(Arrays.asList(patterns));
    }

    /**
     * @param patterns a collection of patterns to match
     * @return a pattern that will match only when all contained patterns match
     */
    public static Conjunction<? extends Pattern> and(List<? extends Pattern> patterns) {
        return new Conjunction<>(patterns);
    }

    /**
     * @param patterns an array of patterns to match
     * @return a pattern that will match when any contained pattern matches
     */
    public static Pattern or(Pattern... patterns) {
        return or(Arrays.asList(patterns));
    }

    /**
     * @param patterns a collection of patterns to match
     * @return a pattern that will match when any contained pattern matches
     */
    public static Pattern or(List<Pattern> patterns) {
        // Simplify representation when there is only one alternative
        if (patterns.size() == 1) {
            return patterns.iterator().next();
        }

        return new Disjunction<>(patterns);
    }

    /**
     * @param pattern a patterns to a negate
     * @return a pattern that will match when no contained pattern matches
     */
    public static Negation<Pattern> not(Pattern pattern) {
        return new Negation<>(pattern);
    }

    // Variable Builder Methods

    /**
     * @return a new variable with an anonymous Variable
     */
    public static UnscopedVariable var() {
        return UnscopedVariable.anonymous();
    }

    /**
     * @param name the name of the variable
     * @return a new variable with a variable of a given name
     */
    public static UnscopedVariable var(String name) {
        return UnscopedVariable.named(name);
    }

    public static TypeVariable type(Graql.Token.Type type) {
        return type(type.toString());
    }

    public static TypeVariable type(String label) {
        return hidden().type(label);
    }

    public static ThingVariable.Relation rel(String player) {
        return hidden().rel(player);
    }

    public static ThingVariable.Relation rel(String role, String player) {
        return hidden().rel(role, player);
    }

    public static ThingVariable.Relation rel(UnscopedVariable role, UnscopedVariable player) {
        return hidden().rel(role, player);
    }

    // Attribute Variable Builder Methods

    // Attribute value assignment property

    public static ThingVariable.Attribute val(long value) {
        return hidden().val(value);
    }

    public static ThingVariable.Attribute val(double value) {
        return hidden().val(value);
    }

    public static ThingVariable.Attribute val(boolean value) {
        return hidden().val(value);
    }

    public static ThingVariable.Attribute val(String value) {
        return hidden().val(value);
    }

    public static ThingVariable.Attribute val(LocalDateTime value) {
        return hidden().val(value);
    }

    // Attribute value equality property

    public static ThingVariable.Attribute eq(long value) {
        return hidden().eq(value);
    }

    public static ThingVariable.Attribute eq(double value) {
        return hidden().eq(value);
    }

    public static ThingVariable.Attribute eq(boolean value) {
        return hidden().eq(value);
    }

    public static ThingVariable.Attribute eq(String value) {
        return hidden().eq(value);
    }

    public static ThingVariable.Attribute eq(LocalDateTime value) {
        return hidden().eq(value);
    }

    public static ThingVariable.Attribute eq(UnscopedVariable variable) {
        return hidden().eq(variable);
    }

    // Attribute value inequality property

    public static ThingVariable.Attribute neq(long value) {
        return hidden().neq(value);
    }

    public static ThingVariable.Attribute neq(double value) {
        return hidden().neq(value);
    }

    public static ThingVariable.Attribute neq(boolean value) {
        return hidden().neq(value);
    }

    public static ThingVariable.Attribute neq(String value) {
        return hidden().neq(value);
    }

    public static ThingVariable.Attribute neq(LocalDateTime value) {
        return hidden().neq(value);
    }

    public static ThingVariable.Attribute neq(UnscopedVariable variable) {
        return hidden().neq(variable);
    }

    // Attribute value greater-than property

    public static ThingVariable.Attribute gt(long value) {
        return hidden().gt(value);
    }

    public static ThingVariable.Attribute gt(double value) {
        return hidden().gt(value);
    }

    public static ThingVariable.Attribute gt(boolean value) {
        return hidden().gt(value);
    }

    public static ThingVariable.Attribute gt(String value) {
        return hidden().gt(value);
    }

    public static ThingVariable.Attribute gt(LocalDateTime value) {
        return hidden().gt(value);
    }

    public static ThingVariable.Attribute gt(UnscopedVariable variable) {
        return hidden().gt(variable);
    }

    // Attribute value greater-than-or-equals property

    public static ThingVariable.Attribute gte(long value) {
        return hidden().gte(value);
    }

    public static ThingVariable.Attribute gte(double value) {
        return hidden().gte(value);
    }

    public static ThingVariable.Attribute gte(boolean value) {
        return hidden().gte(value);
    }

    public static ThingVariable.Attribute gte(String value) {
        return hidden().gte(value);
    }

    public static ThingVariable.Attribute gte(LocalDateTime value) {
        return hidden().gte(value);
    }

    public static ThingVariable.Attribute gte(UnscopedVariable variable) {
        return hidden().gte(variable);
    }

    // Attribute value less-than property

    public static ThingVariable.Attribute lt(long value) {
        return hidden().lt(value);
    }

    public static ThingVariable.Attribute lt(double value) {
        return hidden().lt(value);
    }

    public static ThingVariable.Attribute lt(boolean value) {
        return hidden().lt(value);
    }

    public static ThingVariable.Attribute lt(String value) {
        return hidden().lt(value);
    }

    public static ThingVariable.Attribute lt(LocalDateTime value) {
        return hidden().lt(value);
    }

    public static ThingVariable.Attribute lt(UnscopedVariable variable) {
        return hidden().lt(variable);
    }

    // Attribute value less-than-or-equals property

    public static ThingVariable.Attribute lte(long value) {
        return hidden().lte(value);
    }

    public static ThingVariable.Attribute lte(double value) {
        return hidden().lte(value);
    }

    public static ThingVariable.Attribute lte(boolean value) {
        return hidden().lte(value);
    }

    public static ThingVariable.Attribute lte(String value) {
        return hidden().lte(value);
    }

    public static ThingVariable.Attribute lte(LocalDateTime value) {
        return hidden().lte(value);
    }

    public static ThingVariable.Attribute lte(UnscopedVariable variable) {
        return hidden().lte(variable);
    }

    // Attribute value contains (in String) property

    public static ThingVariable.Attribute contains(String value) {
        return hidden().contains(value);
    }

    public static ThingVariable.Attribute contains(UnscopedVariable variable) {
        return hidden().contains(variable);
    }

    // Attribute value regex property

    public static ThingVariable.Attribute like(String value) {
        return hidden().like(value);
    }

    public static class Token {

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
            CONTAINS("contains"), // TODO: remove duplicate in ComputeQuery.Param
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

        public enum ValueType {
            BOOLEAN("boolean"),
            DATETIME("datetime"),
            DOUBLE("double"),
            LONG("long"),
            STRING("string");

            private final String type;

            ValueType(String type) {
                this.type = type;
            }

            @Override
            public String toString() {
                return this.type;
            }

            public static ValueType of(String value) {
                for (ValueType c : ValueType.values()) {
                    if (c.type.equals(value)) {
                        return c;
                    }
                }
                return null;
            }
        }

        public enum Order {
            ASC("asc"),
            DESC("desc");

            private final String order;

            Order(String order) {
                this.order = order;
            }

            @Override
            public String toString() {
                return this.order;
            }

            public static Order of(String value) {
                for (Order c : Order.values()) {
                    if (c.order.equals(value)) {
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

                public static Method of(String value) {
                    for (Method m : Method.values()) {
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

                public static Method of(String name) {
                    for (Method m : Method.values()) {
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

                public static Condition of(String value) {
                    for (Condition c : Condition.values()) {
                        if (c.condition.equals(value)) {
                            return c;
                        }
                    }
                    return null;
                }
            }

            /**
             * Graql Compute algorithm names
             */
            public enum Algorithm {
                DEGREE("degree"),
                K_CORE("k-core"),
                CONNECTED_COMPONENT("connected-component");

                private final String algorithm;

                Algorithm(String algorithm) {
                    this.algorithm = algorithm;
                }

                @Override
                public String toString() {
                    return this.algorithm;
                }

                public static Algorithm of(String value) {
                    for (Algorithm a : Algorithm.values()) {
                        if (a.algorithm.equals(value)) {
                            return a;
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

                public static Param of(String value) {
                    for (Param p : Param.values()) {
                        if (p.param.equals(value)) {
                            return p;
                        }
                    }
                    return null;
                }
            }
        }
    }
}
