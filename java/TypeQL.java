/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.typeql.lang;

import com.typeql.grammar.TypeQLLexer;
import com.typeql.lang.common.TypeQLToken;
import com.typeql.lang.common.exception.TypeQLException;
import com.typeql.lang.parser.Parser;
import com.typeql.lang.pattern.Conjunction;
import com.typeql.lang.pattern.Definable;
import com.typeql.lang.pattern.Disjunction;
import com.typeql.lang.pattern.Negation;
import com.typeql.lang.pattern.Pattern;
import com.typeql.lang.pattern.constraint.Predicate;
import com.typeql.lang.pattern.constraint.ThingConstraint;
import com.typeql.lang.pattern.schema.Rule;
import com.typeql.lang.pattern.statement.Statement;
import com.typeql.lang.pattern.statement.ThingStatement;
import com.typeql.lang.pattern.statement.TypeStatement;
import com.typeql.lang.common.TypeQLVariable;
import com.typeql.lang.query.TypeQLDefine;
import com.typeql.lang.query.TypeQLFetch;
import com.typeql.lang.query.TypeQLInsert;
import com.typeql.lang.query.TypeQLQuery;
import com.typeql.lang.query.TypeQLUndefine;
import com.typeql.lang.builder.ConceptVariableBuilder;
import com.typeql.lang.builder.ValueVariableBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.typeql.lang.common.TypeQLToken.Predicate.Equality.EQ;
import static com.typeql.lang.common.TypeQLToken.Predicate.Equality.GT;
import static com.typeql.lang.common.TypeQLToken.Predicate.Equality.GTE;
import static com.typeql.lang.common.TypeQLToken.Predicate.Equality.LT;
import static com.typeql.lang.common.TypeQLToken.Predicate.Equality.LTE;
import static com.typeql.lang.common.TypeQLToken.Predicate.Equality.NEQ;
import static com.typeql.lang.common.TypeQLToken.Predicate.SubString.CONTAINS;
import static com.typeql.lang.common.TypeQLToken.Predicate.SubString.LIKE;
import static com.typeql.lang.common.exception.ErrorMessage.INVALID_TYPE_LABEL;

public class TypeQL {

    private static final Parser parser = new Parser();

    public static <T extends TypeQLQuery> T parseQuery(String queryString) {
        return parser.parseQueryEOF(queryString);
    }

    public static <T extends TypeQLQuery> Stream<T> parseQueries(String queryString) {
        return parser.parseQueriesEOF(queryString);
    }

    public static Pattern parsePattern(String pattern) {
        return parser.parsePatternEOF(pattern);
    }

    public static List<? extends Pattern> parsePatterns(String pattern) {
        return parser.parsePatternsEOF(pattern);
    }

    public static List<Definable> parseDefinables(String pattern) {
        return parser.parseDefinablesEOF(pattern);
    }

    public static Rule parseRule(String pattern) {
        return parser.parseSchemaRuleEOF(pattern).asRule();
    }

    public static Statement parseStatement(String statement) {
        return parser.parseStatementEOF(statement);
    }

    public static String parseLabel(String label) {
        String parsedLabel;
        try {
            parsedLabel = parser.parseLabelEOF(label);
        } catch (TypeQLException e) {
            throw TypeQLException.of(INVALID_TYPE_LABEL.message(label));
        }
        if (!parsedLabel.equals(label))
            throw TypeQLException.of(INVALID_TYPE_LABEL.message(label)); // e.g: 'abc#123'
        return parsedLabel;
    }

    public static TypeQLLexer lexer(String string) {
        return parser.lexer(string);
    }

    public static TypeQLQuery.MatchClause match(Pattern... patterns) {
        return match(list(patterns));
    }

    public static TypeQLQuery.MatchClause match(List<? extends Pattern> patterns) {
        return new TypeQLQuery.MatchClause(new Conjunction<>(patterns));
    }

    public static TypeQLInsert insert(ThingStatement<?>... things) {
        return new TypeQLInsert(list(things));
    }

    public static TypeQLInsert insert(List<ThingStatement<?>> things) {
        return new TypeQLInsert(things);
    }

    public static TypeQLDefine define(Definable... definables) {
        return new TypeQLDefine(list(definables));
    }

    public static TypeQLDefine define(List<Definable> definables) {
        return new TypeQLDefine(definables);
    }

    public static TypeQLUndefine undefine(TypeStatement... types) {
        return new TypeQLUndefine(list(types));
    }

    public static TypeQLUndefine undefine(List<Definable> definables) {
        return new TypeQLUndefine(definables);
    }

    public static Conjunction<? extends Pattern> and(Pattern... patterns) {
        return and(list(patterns));
    }

    public static Conjunction<? extends Pattern> and(List<? extends Pattern> patterns) {
        return new Conjunction<>(patterns);
    }

    public static Pattern or(Pattern... patterns) {
        return or(list(patterns));
    }

    public static Pattern or(List<Pattern> patterns) {
        // Simplify representation when there is only one alternative
        if (patterns.size() == 1) return patterns.iterator().next();
        return new Disjunction<>(patterns);
    }

    public static Negation<Pattern> not(Pattern pattern) {
        return new Negation<>(pattern);
    }

    public static Rule rule(String label) {
        return new Rule(label);
    }

    public static TypeQLFetch.Key.Label label(String label) {
        return TypeQLFetch.Key.Label.of(label);
    }

    public static ConceptVariableBuilder cVar() {
        return ConceptVariableBuilder.anonymous();
    }

    public static ConceptVariableBuilder cVar(String name) {
        return ConceptVariableBuilder.named(name);
    }

    public static ValueVariableBuilder vVar(String name) {
        return ValueVariableBuilder.named(name);
    }

    public static TypeStatement type(TypeQLToken.Type type) {
        return type(type.toString());
    }

    public static TypeStatement type(String label) {
        return ConceptVariableBuilder.label(label);
    }

    public static ThingStatement.Relation rel(ConceptVariableBuilder playerVar) {
        return ConceptVariableBuilder.hidden().rel(playerVar);
    }

    public static ThingStatement.Relation rel(String roleType, ConceptVariableBuilder playerVar) {
        return ConceptVariableBuilder.hidden().rel(roleType, playerVar);
    }

    public static ThingStatement.Relation rel(ConceptVariableBuilder roleType, ConceptVariableBuilder playerVar) {
        return ConceptVariableBuilder.hidden().rel(roleType, playerVar);
    }

    public static ThingConstraint.Predicate eq(long value) {
        return new ThingConstraint.Predicate(new Predicate.Long(EQ, value));
    }

    public static ThingConstraint.Predicate eq(double value) {
        return new ThingConstraint.Predicate(new Predicate.Double(EQ, value));
    }

    public static ThingConstraint.Predicate eq(boolean value) {
        return new ThingConstraint.Predicate(new Predicate.Boolean(EQ, value));
    }

    public static ThingConstraint.Predicate eq(String value) {
        return new ThingConstraint.Predicate(new Predicate.String(EQ, value));
    }

    public static ThingConstraint.Predicate eq(LocalDateTime value) {
        return new ThingConstraint.Predicate(new Predicate.DateTime(EQ, value));
    }

    public static ThingConstraint.Predicate eq(TypeQLVariable variable) {
        return new ThingConstraint.Predicate(new Predicate.Variable(EQ, variable));
    }

    public static ThingConstraint.Predicate neq(long value) {
        return new ThingConstraint.Predicate(new Predicate.Long(NEQ, value));
    }

    public static ThingConstraint.Predicate neq(double value) {
        return new ThingConstraint.Predicate(new Predicate.Double(NEQ, value));
    }

    public static ThingConstraint.Predicate neq(boolean value) {
        return new ThingConstraint.Predicate(new Predicate.Boolean(NEQ, value));
    }

    public static ThingConstraint.Predicate neq(String value) {
        return new ThingConstraint.Predicate(new Predicate.String(NEQ, value));
    }

    public static ThingConstraint.Predicate neq(LocalDateTime value) {
        return new ThingConstraint.Predicate(new Predicate.DateTime(NEQ, value));
    }

    public static ThingConstraint.Predicate neq(TypeQLVariable variable) {
        return new ThingConstraint.Predicate(new Predicate.Variable(NEQ, variable));
    }

    public static ThingConstraint.Predicate gt(long value) {
        return new ThingConstraint.Predicate(new Predicate.Long(GT, value));
    }

    public static ThingConstraint.Predicate gt(double value) {
        return new ThingConstraint.Predicate(new Predicate.Double(GT, value));
    }

    public static ThingConstraint.Predicate gt(boolean value) {
        return new ThingConstraint.Predicate(new Predicate.Boolean(GT, value));
    }

    public static ThingConstraint.Predicate gt(String value) {
        return new ThingConstraint.Predicate(new Predicate.String(GT, value));
    }

    public static ThingConstraint.Predicate gt(LocalDateTime value) {
        return new ThingConstraint.Predicate(new Predicate.DateTime(GT, value));
    }

    public static ThingConstraint.Predicate gt(TypeQLVariable variable) {
        return new ThingConstraint.Predicate(new Predicate.Variable(GT, variable));
    }

    public static ThingConstraint.Predicate gte(long value) {
        return new ThingConstraint.Predicate(new Predicate.Long(GTE, value));
    }

    public static ThingConstraint.Predicate gte(double value) {
        return new ThingConstraint.Predicate(new Predicate.Double(GTE, value));
    }

    public static ThingConstraint.Predicate gte(boolean value) {
        return new ThingConstraint.Predicate(new Predicate.Boolean(GTE, value));
    }

    public static ThingConstraint.Predicate gte(String value) {
        return new ThingConstraint.Predicate(new Predicate.String(GTE, value));
    }

    public static ThingConstraint.Predicate gte(LocalDateTime value) {
        return new ThingConstraint.Predicate(new Predicate.DateTime(GTE, value));
    }

    public static ThingConstraint.Predicate gte(TypeQLVariable variable) {
        return new ThingConstraint.Predicate(new Predicate.Variable(GTE, variable));
    }

    public static ThingConstraint.Predicate lt(long value) {
        return new ThingConstraint.Predicate(new Predicate.Long(LT, value));
    }

    public static ThingConstraint.Predicate lt(double value) {
        return new ThingConstraint.Predicate(new Predicate.Double(LT, value));
    }

    public static ThingConstraint.Predicate lt(boolean value) {
        return new ThingConstraint.Predicate(new Predicate.Boolean(LT, value));
    }

    public static ThingConstraint.Predicate lt(String value) {
        return new ThingConstraint.Predicate(new Predicate.String(LT, value));
    }

    public static ThingConstraint.Predicate lt(LocalDateTime value) {
        return new ThingConstraint.Predicate(new Predicate.DateTime(LT, value));
    }

    public static ThingConstraint.Predicate lt(TypeQLVariable variable) {
        return new ThingConstraint.Predicate(new Predicate.Variable(LT, variable));
    }

    public static ThingConstraint.Predicate lte(long value) {
        return new ThingConstraint.Predicate(new Predicate.Long(LTE, value));
    }

    public static ThingConstraint.Predicate lte(double value) {
        return new ThingConstraint.Predicate(new Predicate.Double(LTE, value));
    }

    public static ThingConstraint.Predicate lte(boolean value) {
        return new ThingConstraint.Predicate(new Predicate.Boolean(LTE, value));
    }

    public static ThingConstraint.Predicate lte(String value) {
        return new ThingConstraint.Predicate(new Predicate.String(LTE, value));
    }

    public static ThingConstraint.Predicate lte(LocalDateTime value) {
        return new ThingConstraint.Predicate(new Predicate.DateTime(LTE, value));
    }

    public static ThingConstraint.Predicate lte(TypeQLVariable variable) {
        return new ThingConstraint.Predicate(new Predicate.Variable(LTE, variable));
    }

    public static ThingConstraint.Predicate contains(String value) {
        return new ThingConstraint.Predicate(new Predicate.String(CONTAINS, value));
    }

    public static ThingConstraint.Predicate like(String value) {
        return new ThingConstraint.Predicate(new Predicate.String(LIKE, value));
    }

    public static abstract class Expression {

        public static com.typeql.lang.pattern.expression.Expression.Function min(com.typeql.lang.pattern.expression.Expression... args) {
            return min(list(args));
        }

        public static com.typeql.lang.pattern.expression.Expression.Function min(List<com.typeql.lang.pattern.expression.Expression> args) {
            return function(TypeQLToken.Expression.Function.MIN, args);
        }

        public static com.typeql.lang.pattern.expression.Expression.Function max(com.typeql.lang.pattern.expression.Expression... args) {
            return max(list(args));
        }

        public static com.typeql.lang.pattern.expression.Expression.Function max(List<com.typeql.lang.pattern.expression.Expression> args) {
            return function(TypeQLToken.Expression.Function.MAX, args);
        }

        public static com.typeql.lang.pattern.expression.Expression.Function floor(com.typeql.lang.pattern.expression.Expression... args) {
            return floor(list(args));
        }

        public static com.typeql.lang.pattern.expression.Expression.Function floor(List<com.typeql.lang.pattern.expression.Expression> args) {
            return function(TypeQLToken.Expression.Function.FLOOR, args);
        }

        public static com.typeql.lang.pattern.expression.Expression.Function ceil(com.typeql.lang.pattern.expression.Expression... args) {
            return ceil(list(args));
        }

        public static com.typeql.lang.pattern.expression.Expression.Function ceil(List<com.typeql.lang.pattern.expression.Expression> args) {
            return function(TypeQLToken.Expression.Function.CEIL, args);
        }

        public static com.typeql.lang.pattern.expression.Expression.Function round(com.typeql.lang.pattern.expression.Expression... args) {
            return round(list(args));
        }

        public static com.typeql.lang.pattern.expression.Expression.Function round(List<com.typeql.lang.pattern.expression.Expression> args) {
            return function(TypeQLToken.Expression.Function.ROUND, args);
        }

        public static com.typeql.lang.pattern.expression.Expression.Function abs(com.typeql.lang.pattern.expression.Expression... args) {
            return abs(list(args));
        }

        public static com.typeql.lang.pattern.expression.Expression.Function abs(List<com.typeql.lang.pattern.expression.Expression> args) {
            return function(TypeQLToken.Expression.Function.ABS, args);
        }

        public static com.typeql.lang.pattern.expression.Expression.Function function(TypeQLToken.Expression.Function function, List<com.typeql.lang.pattern.expression.Expression> args) {
            return new com.typeql.lang.pattern.expression.Expression.Function(function, args);
        }

        public static com.typeql.lang.pattern.expression.Expression.Parenthesis parenthesis(com.typeql.lang.pattern.expression.Expression expression) {
            return new com.typeql.lang.pattern.expression.Expression.Parenthesis(expression);
        }

        public static com.typeql.lang.pattern.expression.Expression.Constant.Boolean constant(boolean value) {
            return new com.typeql.lang.pattern.expression.Expression.Constant.Boolean(value);
        }

        public static com.typeql.lang.pattern.expression.Expression.Constant.Long constant(long value) {
            return new com.typeql.lang.pattern.expression.Expression.Constant.Long(value);
        }

        public static com.typeql.lang.pattern.expression.Expression.Constant.Double constant(double value) {
            return new com.typeql.lang.pattern.expression.Expression.Constant.Double(value);
        }

        public static com.typeql.lang.pattern.expression.Expression.Constant.String constant(String value) {
            return new com.typeql.lang.pattern.expression.Expression.Constant.String(value);
        }

        public static com.typeql.lang.pattern.expression.Expression.Constant.DateTime constant(LocalDateTime value) {
            return new com.typeql.lang.pattern.expression.Expression.Constant.DateTime(value);
        }

        public static com.typeql.lang.pattern.expression.Expression add(
                com.typeql.lang.pattern.expression.Expression a,
                com.typeql.lang.pattern.expression.Expression b
        ) {
            return a.add(b);
        }

        public static com.typeql.lang.pattern.expression.Expression sub(
                com.typeql.lang.pattern.expression.Expression a,
                com.typeql.lang.pattern.expression.Expression b
        ) {
            return a.subtract(b);
        }

        public static com.typeql.lang.pattern.expression.Expression mul(
                com.typeql.lang.pattern.expression.Expression a,
                com.typeql.lang.pattern.expression.Expression b
        ) {
            return a.multiply(b);
        }

        public static com.typeql.lang.pattern.expression.Expression div(
                com.typeql.lang.pattern.expression.Expression a,
                com.typeql.lang.pattern.expression.Expression b
        ) {
            return a.divide(b);
        }

        public static com.typeql.lang.pattern.expression.Expression mod(
                com.typeql.lang.pattern.expression.Expression a,
                com.typeql.lang.pattern.expression.Expression b
        ) {
            return a.modulo(b);
        }

        public static com.typeql.lang.pattern.expression.Expression pow(
                com.typeql.lang.pattern.expression.Expression a,
                com.typeql.lang.pattern.expression.Expression b
        ) {
            return a.power(b);
        }

    }
}
