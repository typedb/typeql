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
import graql.lang.statement.Label;
import graql.lang.statement.Statement;
import graql.lang.statement.StatementAttribute;
import graql.lang.statement.StatementRelation;
import graql.lang.statement.StatementType;
import graql.lang.statement.Variable;

import javax.annotation.CheckReturnValue;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Main class containing static methods for creating Graql queries.
 * It is recommended you statically import these methods.
 */
public class Graql {

    private static final Parser parser = new Parser();

    @CheckReturnValue
    public static <T extends GraqlQuery> T parse(String queryString) {
        return parser.parseQueryEOF(queryString);
    }

    @CheckReturnValue
    public static <T extends GraqlQuery> Stream<T> parseList(String queryString) {
        return parser.parseQueryListEOF(queryString);
    }

    @CheckReturnValue
    public static Pattern parsePattern(String pattern) {
        return parser.parsePatternEOF(pattern);
    }

    @CheckReturnValue
    public static List<Pattern> parsePatternList(String pattern) {
        return parser.parsePatternListEOF(pattern).collect(Collectors.toList());
    }

    /**
     * @param patterns an array of patterns to match in the graph
     * @return a match clause that will find matches of the given patterns
     */
    @CheckReturnValue
    public static MatchClause match(Pattern... patterns) {
        return match(Arrays.asList(patterns));
    }

    /**
     * @param patterns a collection of patterns to match in the graph
     * @return a match clause that will find matches of the given patterns
     */
    @CheckReturnValue
    public static MatchClause match(Collection<? extends Pattern> patterns) {
        return new MatchClause(and(Collections.unmodifiableSet(new LinkedHashSet<>(patterns))));
    }

    /**
     * @param statements an array of variable patterns to insert into the graph
     * @return an insert query that will insert the given variable patterns into the graph
     */
    @CheckReturnValue
    public static GraqlInsert insert(Statement... statements) {
        return insert(Arrays.asList(statements));
    }

    /**
     * @param statements a collection of variable patterns to insert into the graph
     * @return an insert query that will insert the given variable patterns into the graph
     */
    @CheckReturnValue
    public static GraqlInsert insert(Collection<? extends Statement> statements) {
        return new GraqlInsert(null, Collections.unmodifiableList(new ArrayList<>(statements)));
    }

    /**
     * @param statements an array of of statements to define the schema
     * @return a define query that will apply the changes described in the {@code patterns}
     */
    @CheckReturnValue
    public static GraqlDefine define(Statement... statements) {
        return define(Arrays.asList(statements));
    }

    /**
     * @param statements a collection of statements to define the schema
     * @return a define query that will apply the changes described in the {@code patterns}
     */
    @CheckReturnValue
    public static GraqlDefine define(Collection<? extends Statement> statements) {
        return new GraqlDefine(Collections.unmodifiableList(new ArrayList<>(statements)));
    }

    /**
     * @param statements an array of statements to undefine the schema
     * @return an undefine query that will remove the changes described in the {@code patterns}
     */
    @CheckReturnValue
    public static GraqlUndefine undefine(Statement... statements) {
        return undefine(Arrays.asList(statements));
    }

    /**
     * @param statements a collection of statements to undefine the schema
     * @return an undefine query that will remove the changes described in the {@code patterns}
     */
    @CheckReturnValue
    public static GraqlUndefine undefine(Collection<? extends Statement> statements) {
        return new GraqlUndefine(Collections.unmodifiableList(new ArrayList<>(statements)));
    }

    @CheckReturnValue
    public static GraqlCompute.Builder compute() {
        return new GraqlCompute.Builder();
    }

    // Pattern Builder Methods

    /**
     * @param patterns an array of patterns to match
     * @return a pattern that will match only when all contained patterns match
     */
    @CheckReturnValue
    public static Conjunction<?> and(Pattern... patterns) {
        return and(Arrays.asList(patterns));
    }

    /**
     * @param patterns a collection of patterns to match
     * @return a pattern that will match only when all contained patterns match
     */
    @CheckReturnValue
    public static Conjunction<?> and(Collection<? extends Pattern> patterns) {
        return and(new LinkedHashSet<>(patterns));
    }

    @CheckReturnValue
    public static <T extends Pattern> Conjunction<T> and(Set<T> patterns) {
        return new Conjunction<>(patterns);
    }

    /**
     * @param patterns an array of patterns to match
     * @return a pattern that will match when any contained pattern matches
     */
    @CheckReturnValue
    public static Pattern or(Pattern... patterns) {
        return or(Arrays.asList(patterns));
    }

    /**
     * @param patterns a collection of patterns to match
     * @return a pattern that will match when any contained pattern matches
     */
    @CheckReturnValue
    public static Pattern or(Collection<? extends Pattern> patterns) {
        // Simplify representation when there is only one alternative
        if (patterns.size() == 1) {
            return patterns.iterator().next();
        }

        return or(new LinkedHashSet<>(patterns));
    }

    @CheckReturnValue
    public static <T extends Pattern> Disjunction<T> or(Set<T> patterns) {
        return new Disjunction<>(patterns);
    }

    /**
     * @param pattern a patterns to a negate
     * @return a pattern that will match when no contained pattern matches
     */
    @CheckReturnValue
    public static Negation<Pattern> not(Pattern pattern) {
        return new Negation<>(pattern);
    }

    // Statement Builder Methods

    /**
     * @return a new statement with an anonymous Variable
     */
    @CheckReturnValue
    public static Statement var() {
        return var(new Variable());
    }

    /**
     * @param name the name of the variable
     * @return a new statement with a variable of a given name
     */
    @CheckReturnValue
    public static Statement var(String name) {
        return var(new Variable(name));
    }

    /**
     * @param var a variable to create a statement
     * @return a new statement with a provided variable
     */
    @CheckReturnValue
    public static Statement var(Variable var) {
        return new Statement(var);
    }

    @CheckReturnValue
    private static Statement hiddenVar() {
        return var(new Variable(false));
    }

    @CheckReturnValue
    public static StatementType type(Label label) {
        return hiddenVar().type(label);
    }

    @CheckReturnValue
    public static StatementType type(String label) {
        return hiddenVar().type(label);
    }

    @CheckReturnValue
    public static StatementType type(String label, String scope) {
        return hiddenVar().type(label, scope);
    }

    @CheckReturnValue
    public static StatementRelation rel(String player) {
        return hiddenVar().rel(player);
    }

    @CheckReturnValue
    public static StatementRelation rel(String role, String player) {
        return hiddenVar().rel(role, player);
    }

    @CheckReturnValue
    public static StatementRelation rel(Statement role, Statement player) {
        return hiddenVar().rel(role, player);
    }

    // Attribute Statement Builder Methods

    // Attribute value assignment property

    @CheckReturnValue
    public static StatementAttribute val(long value) {
        return hiddenVar().val(value);
    }

    @CheckReturnValue
    public static StatementAttribute val(double value) {
        return hiddenVar().val(value);
    }

    @CheckReturnValue
    public static StatementAttribute val(boolean value) {
        return hiddenVar().val(value);
    }

    @CheckReturnValue
    public static StatementAttribute val(String value) {
        return hiddenVar().val(value);
    }

    @CheckReturnValue
    public static StatementAttribute val(LocalDateTime value) {
        return hiddenVar().val(value);
    }

    // Attribute value equality property

    @CheckReturnValue
    public static StatementAttribute eq(long value) {
        return hiddenVar().eq(value);
    }

    @CheckReturnValue
    public static StatementAttribute eq(double value) {
        return hiddenVar().eq(value);
    }

    @CheckReturnValue
    public static StatementAttribute eq(boolean value) {
        return hiddenVar().eq(value);
    }

    @CheckReturnValue
    public static StatementAttribute eq(String value) {
        return hiddenVar().eq(value);
    }

    @CheckReturnValue
    public static StatementAttribute eq(LocalDateTime value) {
        return hiddenVar().eq(value);
    }

    @CheckReturnValue
    public static StatementAttribute eq(Statement variable) {
        return hiddenVar().eq(variable);
    }

    // Attribute value inequality property

    @CheckReturnValue
    public static StatementAttribute neq(long value) {
        return hiddenVar().neq(value);
    }

    @CheckReturnValue
    public static StatementAttribute neq(double value) {
        return hiddenVar().neq(value);
    }

    @CheckReturnValue
    public static StatementAttribute neq(boolean value) {
        return hiddenVar().neq(value);
    }

    @CheckReturnValue
    public static StatementAttribute neq(String value) {
        return hiddenVar().neq(value);
    }

    @CheckReturnValue
    public static StatementAttribute neq(LocalDateTime value) {
        return hiddenVar().neq(value);
    }

    @CheckReturnValue
    public static StatementAttribute neq(Statement variable) {
        return hiddenVar().neq(variable);
    }

    // Attribute value greater-than property

    @CheckReturnValue
    public static StatementAttribute gt(long value) {
        return hiddenVar().gt(value);
    }

    @CheckReturnValue
    public static StatementAttribute gt(double value) {
        return hiddenVar().gt(value);
    }

    @CheckReturnValue
    public static StatementAttribute gt(boolean value) {
        return hiddenVar().gt(value);
    }

    @CheckReturnValue
    public static StatementAttribute gt(String value) {
        return hiddenVar().gt(value);
    }

    @CheckReturnValue
    public static StatementAttribute gt(LocalDateTime value) {
        return hiddenVar().gt(value);
    }

    @CheckReturnValue
    public static StatementAttribute gt(Statement variable) {
        return hiddenVar().gt(variable);
    }

    // Attribute value greater-than-or-equals property

    @CheckReturnValue
    public static StatementAttribute gte(long value) {
        return hiddenVar().gte(value);
    }

    @CheckReturnValue
    public static StatementAttribute gte(double value) {
        return hiddenVar().gte(value);
    }

    @CheckReturnValue
    public static StatementAttribute gte(boolean value) {
        return hiddenVar().gte(value);
    }

    @CheckReturnValue
    public static StatementAttribute gte(String value) {
        return hiddenVar().gte(value);
    }

    @CheckReturnValue
    public static StatementAttribute gte(LocalDateTime value) {
        return hiddenVar().gte(value);
    }

    @CheckReturnValue
    public static StatementAttribute gte(Statement variable) {
        return hiddenVar().gte(variable);
    }

    // Attribute value less-than property

    @CheckReturnValue
    public static StatementAttribute lt(long value) {
        return hiddenVar().lt(value);
    }

    @CheckReturnValue
    public static StatementAttribute lt(double value) {
        return hiddenVar().lt(value);
    }

    @CheckReturnValue
    public static StatementAttribute lt(boolean value) {
        return hiddenVar().lt(value);
    }

    @CheckReturnValue
    public static StatementAttribute lt(String value) {
        return hiddenVar().lt(value);
    }

    @CheckReturnValue
    public static StatementAttribute lt(LocalDateTime value) {
        return hiddenVar().lt(value);
    }

    @CheckReturnValue
    public static StatementAttribute lt(Statement variable) {
        return hiddenVar().lt(variable);
    }

    // Attribute value less-than-or-equals property

    @CheckReturnValue
    public static StatementAttribute lte(long value) {
        return hiddenVar().lte(value);
    }

    @CheckReturnValue
    public static StatementAttribute lte(double value) {
        return hiddenVar().lte(value);
    }

    @CheckReturnValue
    public static StatementAttribute lte(boolean value) {
        return hiddenVar().lte(value);
    }

    @CheckReturnValue
    public static StatementAttribute lte(String value) {
        return hiddenVar().lte(value);
    }

    @CheckReturnValue
    public static StatementAttribute lte(LocalDateTime value) {
        return hiddenVar().lte(value);
    }

    @CheckReturnValue
    public static StatementAttribute lte(Statement variable) {
        return hiddenVar().lte(variable);
    }

    // Attribute value contains (in String) property

    @CheckReturnValue
    public static StatementAttribute contains(String value) {
        return hiddenVar().contains(value);
    }

    @CheckReturnValue
    public static StatementAttribute contains(Statement variable) {
        return hiddenVar().contains(variable);
    }

    // Attribute value regex property

    @CheckReturnValue
    public static StatementAttribute like(String value) {
        return hiddenVar().like(value);
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
            VALUE(""),
            VALUE_TYPE("value"),
            HAS("has"),
            KEY("key"),
            ID("id"),
            ABSTRACT("abstract"),
            ISA("isa"),
            ISAX("isa!"),
            TYPE("type"),
            PLAYS("plays"),
            REGEX("regex"),
            RELATES("relates"),
            SUB("sub"),
            SUBX("sub!"),
            THEN("then"),
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
