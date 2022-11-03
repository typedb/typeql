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
import com.vaticle.typeql.lang.pattern.constraint.ThingConstraint;
import com.vaticle.typeql.lang.pattern.constraint.ValueConstraint;
import com.vaticle.typeql.lang.pattern.constraint.Predicate;
import com.vaticle.typeql.lang.pattern.schema.Rule;
import com.vaticle.typeql.lang.pattern.variable.BoundVariable;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;
import com.vaticle.typeql.lang.pattern.variable.TypeVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundConceptVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundValueVariable;
import com.vaticle.typeql.lang.pattern.variable.builder.ExpressionBuilder;
import com.vaticle.typeql.lang.query.TypeQLDefine;
import com.vaticle.typeql.lang.query.TypeQLInsert;
import com.vaticle.typeql.lang.query.TypeQLMatch;
import com.vaticle.typeql.lang.query.TypeQLQuery;
import com.vaticle.typeql.lang.query.TypeQLUndefine;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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
import static com.vaticle.typeql.lang.pattern.variable.UnboundConceptVariable.hidden;

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

    public static BoundVariable parseVariable(String variable) {
        return parser.parseVariableEOF(variable);
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

    public static TypeQLMatch.Unfiltered match(Pattern... patterns) {
        return match(list(patterns));
    }

    public static TypeQLMatch.Unfiltered match(List<? extends Pattern> patterns) {
        return new TypeQLMatch.Unfiltered(patterns);
    }

    public static TypeQLInsert insert(ThingVariable<?>... things) {
        return new TypeQLInsert(list(things));
    }

    public static TypeQLInsert insert(List<ThingVariable<?>> things) {
        return new TypeQLInsert(things);
    }

    public static TypeQLDefine define(Definable... definables) {
        return new TypeQLDefine(list(definables));
    }

    public static TypeQLDefine define(List<Definable> definables) {
        return new TypeQLDefine(definables);
    }

    public static TypeQLUndefine undefine(TypeVariable... types) {
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

    public static UnboundConceptVariable cVar() {
        return UnboundConceptVariable.anonymous();
    }

    public static UnboundConceptVariable cVar(String name) {
        return UnboundConceptVariable.named(name);
    }

    public static UnboundValueVariable vVar(String name) {
        return UnboundValueVariable.named(name);
    }

    public static TypeVariable type(TypeQLToken.Type type) {
        return type(type.toString());
    }

    public static TypeVariable type(String label) {
        return hidden().type(label);
    }

    public static ThingVariable.Relation rel(UnboundConceptVariable playerVar) {
        return hidden().rel(playerVar);
    }

    public static ThingVariable.Relation rel(String roleType, UnboundConceptVariable playerVar) {
        return hidden().rel(roleType, playerVar);
    }

    public static ThingVariable.Relation rel(UnboundConceptVariable roleType, UnboundConceptVariable playerVar) {
        return hidden().rel(roleType, playerVar);
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

    public static ThingConstraint.Predicate eq(UnboundConceptVariable variable) {
        return new ThingConstraint.Predicate(new Predicate.ThingVariable(EQ, variable.toThing()));
    }

    public static ThingConstraint.Predicate eq(UnboundValueVariable variable) {
        return new ThingConstraint.Predicate(new Predicate.ValueVariable(EQ, variable.toValue()));
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

    public static ThingConstraint.Predicate neq(UnboundConceptVariable variable) {
        return new ThingConstraint.Predicate(new Predicate.ThingVariable(NEQ, variable.toThing()));
    }

    public static ThingConstraint.Predicate neq(UnboundValueVariable variable) {
        return new ThingConstraint.Predicate(new Predicate.ValueVariable(NEQ, variable.toValue()));
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

    public static ThingConstraint.Predicate gt(UnboundConceptVariable variable) {
        return new ThingConstraint.Predicate(new Predicate.ThingVariable(GT, variable.toThing()));
    }

    public static ThingConstraint.Predicate gt(UnboundValueVariable variable) {
        return new ThingConstraint.Predicate(new Predicate.ValueVariable(GT, variable.toValue()));
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

    public static ThingConstraint.Predicate gte(UnboundConceptVariable variable) {
        return new ThingConstraint.Predicate(new Predicate.ThingVariable(GTE, variable.toThing()));
    }

    public static ThingConstraint.Predicate gte(UnboundValueVariable variable) {
        return new ThingConstraint.Predicate(new Predicate.ValueVariable(GTE, variable.toValue()));
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

    public static ThingConstraint.Predicate lt(UnboundConceptVariable variable) {
        return new ThingConstraint.Predicate(new Predicate.ThingVariable(LT, variable.toThing()));
    }

    public static ThingConstraint.Predicate lt(UnboundValueVariable variable) {
        return new ThingConstraint.Predicate(new Predicate.ValueVariable(LT, variable.toValue()));
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

    public static ThingConstraint.Predicate lte(UnboundConceptVariable variable) {
        return new ThingConstraint.Predicate(new Predicate.ThingVariable(LTE, variable.toThing()));
    }

    public static ThingConstraint.Predicate lte(UnboundValueVariable variable) {
        return new ThingConstraint.Predicate(new Predicate.ValueVariable(LTE, variable.toValue()));
    }

    public static ThingConstraint.Predicate contains(String value) {
        return new ThingConstraint.Predicate(new Predicate.String(CONTAINS, value));
    }

    public static ThingConstraint.Predicate like(String value) {
        return new ThingConstraint.Predicate(new Predicate.String(LIKE, value));
    }

    public static abstract class Expression {
        public static ValueConstraint.Assignment.Expression.Operation plus(ExpressionBuilder<?> a, ExpressionBuilder<?> b) {
            return new ValueConstraint.Assignment.Expression.Operation(TypeQLToken.Expression.Operation.PLUS, a.toExpression(), b.toExpression());
        }

        public static ValueConstraint.Assignment.Expression.Operation minus(ExpressionBuilder<?> a, ExpressionBuilder<?> b) {
            return new ValueConstraint.Assignment.Expression.Operation(TypeQLToken.Expression.Operation.MINUS, a.toExpression(), b.toExpression());
        }

        public static ValueConstraint.Assignment.Expression.Operation times(ExpressionBuilder<?> a, ExpressionBuilder<?> b) {
            return new ValueConstraint.Assignment.Expression.Operation(TypeQLToken.Expression.Operation.TIMES, a.toExpression(), b.toExpression());
        }

        public static ValueConstraint.Assignment.Expression.Operation div(ExpressionBuilder<?> a, ExpressionBuilder<?> b) {
            return new ValueConstraint.Assignment.Expression.Operation(TypeQLToken.Expression.Operation.DIV, a.toExpression(), b.toExpression());
        }

        public static ValueConstraint.Assignment.Expression.Operation pow(ExpressionBuilder<?> a, ExpressionBuilder<?> b) {
            return new ValueConstraint.Assignment.Expression.Operation(TypeQLToken.Expression.Operation.POW, a.toExpression(), b.toExpression());
        }

        public static ValueConstraint.Assignment.Expression.Function func(TypeQLToken.Expression.Function funcId, ExpressionBuilder<?>... args) {
            return func(funcId, list(args));
        }

        public static ValueConstraint.Assignment.Expression.Function func(TypeQLToken.Expression.Function funcId, List<ExpressionBuilder<?>> args) {
            return new ValueConstraint.Assignment.Expression.Function(funcId, args.stream().map(ExpressionBuilder::toExpression).collect(Collectors.toList()));
        }

        public static ValueConstraint.Assignment.Expression.Bracketed bracketed(ExpressionBuilder<?> nestedExpr) {
            return new ValueConstraint.Assignment.Expression.Bracketed(nestedExpr.toExpression());
        }

        public static ValueConstraint.Assignment.Expression.Constant.Boolean constant(boolean value) {
            return new ValueConstraint.Assignment.Expression.Constant.Boolean(value);
        }

        public static ValueConstraint.Assignment.Expression.Constant.Long constant(long value) {
            return new ValueConstraint.Assignment.Expression.Constant.Long(value);
        }

        public static ValueConstraint.Assignment.Expression.Constant.Double constant(double value) {
            return new ValueConstraint.Assignment.Expression.Constant.Double(value);
        }

        public static ValueConstraint.Assignment.Expression.Constant.String constant(String value) {
            return new ValueConstraint.Assignment.Expression.Constant.String(value);
        }

        public static ValueConstraint.Assignment.Expression.Constant.DateTime constant(LocalDateTime value) {
            return new ValueConstraint.Assignment.Expression.Constant.DateTime(value);
        }
    }
}
