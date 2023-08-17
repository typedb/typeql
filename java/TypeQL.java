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

package com.vaticle.typeql.lang;

import com.vaticle.typeql.grammar.TypeQLLexer;
import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.parser.Parser;
import com.vaticle.typeql.lang.pattern.Conjunction;
import com.vaticle.typeql.lang.pattern.Definable;
import com.vaticle.typeql.lang.pattern.Disjunction;
import com.vaticle.typeql.lang.pattern.Negation;
import com.vaticle.typeql.lang.pattern.Pattern;
import com.vaticle.typeql.lang.pattern.constraint.Predicate;
import com.vaticle.typeql.lang.pattern.constraint.ThingConstraint;
import com.vaticle.typeql.lang.pattern.schema.Rule;
import com.vaticle.typeql.lang.pattern.statement.Statement;
import com.vaticle.typeql.lang.pattern.statement.ThingStatement;
import com.vaticle.typeql.lang.pattern.statement.TypeStatement;
import com.vaticle.typeql.lang.common.TypeQLVariable;
import com.vaticle.typeql.lang.query.TypeQLDefine;
import com.vaticle.typeql.lang.query.TypeQLInsert;
import com.vaticle.typeql.lang.query.TypeQLQuery;
import com.vaticle.typeql.lang.query.TypeQLUndefine;
import com.vaticle.typeql.lang.builder.ConceptVariableBuilder;
import com.vaticle.typeql.lang.builder.ValueVariableBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.EQ;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.GT;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.GTE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.LT;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.LTE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.NEQ;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.SubString.CONTAINS;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.SubString.LIKE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.ILLEGAL_CHAR_IN_LABEL;

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
            throw TypeQLException.of(ILLEGAL_CHAR_IN_LABEL.message(label));
        }
        if (!parsedLabel.equals(label))
            throw TypeQLException.of(ILLEGAL_CHAR_IN_LABEL.message(label)); // e.g: 'abc#123'
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

    // Pattern Builder Methods

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

        public static com.vaticle.typeql.lang.pattern.expression.Expression.Function min(com.vaticle.typeql.lang.pattern.expression.Expression... args) {
            return min(list(args));
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression.Function min(List<com.vaticle.typeql.lang.pattern.expression.Expression> args) {
            return function(TypeQLToken.Expression.Function.MIN, args);
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression.Function max(com.vaticle.typeql.lang.pattern.expression.Expression... args) {
            return max(list(args));
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression.Function max(List<com.vaticle.typeql.lang.pattern.expression.Expression> args) {
            return function(TypeQLToken.Expression.Function.MAX, args);
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression.Function floor(com.vaticle.typeql.lang.pattern.expression.Expression... args) {
            return floor(list(args));
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression.Function floor(List<com.vaticle.typeql.lang.pattern.expression.Expression> args) {
            return function(TypeQLToken.Expression.Function.FLOOR, args);
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression.Function ceil(com.vaticle.typeql.lang.pattern.expression.Expression... args) {
            return ceil(list(args));
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression.Function ceil(List<com.vaticle.typeql.lang.pattern.expression.Expression> args) {
            return function(TypeQLToken.Expression.Function.CEIL, args);
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression.Function round(com.vaticle.typeql.lang.pattern.expression.Expression... args) {
            return round(list(args));
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression.Function round(List<com.vaticle.typeql.lang.pattern.expression.Expression> args) {
            return function(TypeQLToken.Expression.Function.ROUND, args);
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression.Function abs(com.vaticle.typeql.lang.pattern.expression.Expression... args) {
            return abs(list(args));
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression.Function abs(List<com.vaticle.typeql.lang.pattern.expression.Expression> args) {
            return function(TypeQLToken.Expression.Function.ABS, args);
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression.Function function(TypeQLToken.Expression.Function function, List<com.vaticle.typeql.lang.pattern.expression.Expression> args) {
            return new com.vaticle.typeql.lang.pattern.expression.Expression.Function(function, args);
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression.Parenthesis parenthesis(com.vaticle.typeql.lang.pattern.expression.Expression expression) {
            return new com.vaticle.typeql.lang.pattern.expression.Expression.Parenthesis(expression);
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression.Constant.Boolean constant(boolean value) {
            return new com.vaticle.typeql.lang.pattern.expression.Expression.Constant.Boolean(value);
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression.Constant.Long constant(long value) {
            return new com.vaticle.typeql.lang.pattern.expression.Expression.Constant.Long(value);
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression.Constant.Double constant(double value) {
            return new com.vaticle.typeql.lang.pattern.expression.Expression.Constant.Double(value);
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression.Constant.String constant(String value) {
            return new com.vaticle.typeql.lang.pattern.expression.Expression.Constant.String(value);
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression.Constant.DateTime constant(LocalDateTime value) {
            return new com.vaticle.typeql.lang.pattern.expression.Expression.Constant.DateTime(value);
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression add(
                com.vaticle.typeql.lang.pattern.expression.Expression a,
                com.vaticle.typeql.lang.pattern.expression.Expression b
        ) {
            return a.add(b);
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression sub(
                com.vaticle.typeql.lang.pattern.expression.Expression a,
                com.vaticle.typeql.lang.pattern.expression.Expression b
        ) {
            return a.subtract(b);
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression mul(
                com.vaticle.typeql.lang.pattern.expression.Expression a,
                com.vaticle.typeql.lang.pattern.expression.Expression b
        ) {
            return a.multiply(b);
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression div(
                com.vaticle.typeql.lang.pattern.expression.Expression a,
                com.vaticle.typeql.lang.pattern.expression.Expression b
        ) {
            return a.divide(b);
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression mod(
                com.vaticle.typeql.lang.pattern.expression.Expression a,
                com.vaticle.typeql.lang.pattern.expression.Expression b
        ) {
            return a.modulo(b);
        }

        public static com.vaticle.typeql.lang.pattern.expression.Expression pow(
                com.vaticle.typeql.lang.pattern.expression.Expression a,
                com.vaticle.typeql.lang.pattern.expression.Expression b
        ) {
            return a.power(b);
        }

    }
}
