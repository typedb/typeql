/*
 * Copyright (C) 2021 Grakn Labs
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

import graql.lang.common.GraqlToken;
import graql.lang.common.exception.GraqlException;
import graql.lang.parser.Parser;
import graql.lang.pattern.Conjunction;
import graql.lang.pattern.Definable;
import graql.lang.pattern.Disjunction;
import graql.lang.pattern.Negation;
import graql.lang.pattern.Pattern;
import graql.lang.pattern.constraint.ThingConstraint;
import graql.lang.pattern.schema.Rule;
import graql.lang.pattern.variable.BoundVariable;
import graql.lang.pattern.variable.ThingVariable;
import graql.lang.pattern.variable.TypeVariable;
import graql.lang.pattern.variable.UnboundVariable;
import graql.lang.query.GraqlCompute;
import graql.lang.query.GraqlDefine;
import graql.lang.query.GraqlInsert;
import graql.lang.query.GraqlMatch;
import graql.lang.query.GraqlQuery;
import graql.lang.query.GraqlUndefine;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static grakn.common.collection.Collections.list;
import static graql.lang.common.GraqlToken.Predicate.Equality.EQ;
import static graql.lang.common.GraqlToken.Predicate.Equality.GT;
import static graql.lang.common.GraqlToken.Predicate.Equality.GTE;
import static graql.lang.common.GraqlToken.Predicate.Equality.LT;
import static graql.lang.common.GraqlToken.Predicate.Equality.LTE;
import static graql.lang.common.GraqlToken.Predicate.Equality.NEQ;
import static graql.lang.common.GraqlToken.Predicate.SubString.CONTAINS;
import static graql.lang.common.GraqlToken.Predicate.SubString.LIKE;
import static graql.lang.common.exception.ErrorMessage.ILLEGAL_CHAR_IN_LABEL;
import static graql.lang.pattern.variable.UnboundVariable.hidden;

public class Graql {

    private static final Parser parser = new Parser();

    public static <T extends GraqlQuery> T parseQuery(String queryString) {
        return parser.parseQueryEOF(queryString);
    }

    public static <T extends GraqlQuery> Stream<T> parseQueries(String queryString) {
        return parser.parseQueriesEOF(queryString);
    }

    public static Pattern parsePattern(String pattern) {
        return parser.parsePatternEOF(pattern);
    }

    public static List<? extends Pattern> parsePatterns(String pattern) {
        return parser.parsePatternsEOF(pattern);
    }

    public static List<Definable> parseDefinables(String pattern) { return parser.parseDefinablesEOF(pattern); }

    public static Rule parseRule(String pattern) { return parser.parseSchemaRuleEOF(pattern).asRule(); }

    public static BoundVariable parseVariable(String variable) {
        return parser.parseVariableEOF(variable);
    }

    public static String parseLabel(String label) {
        String parsedLabel;
        try {
            parsedLabel = parser.parseLabelEOF(label);
        } catch (GraqlException e) {
            throw GraqlException.of(ILLEGAL_CHAR_IN_LABEL.message(label));
        }
        if (!parsedLabel.equals(label)) throw GraqlException.of(ILLEGAL_CHAR_IN_LABEL.message(label)); // e.g: 'abc#123'
        return parsedLabel;
    }

    public static GraqlMatch.Unfiltered match(Pattern... patterns) {
        return match(Arrays.asList(patterns));
    }

    public static GraqlMatch.Unfiltered match(List<? extends Pattern> patterns) {
        return new GraqlMatch.Unfiltered(patterns);
    }

    public static GraqlInsert insert(ThingVariable<?>... things) {
        return new GraqlInsert(list(things));
    }

    public static GraqlInsert insert(List<ThingVariable<?>> things) {
        return new GraqlInsert(things);
    }

    public static GraqlDefine define(Definable... definables) {
        return new GraqlDefine(list(definables));
    }

    public static GraqlDefine define(List<Definable> definables) {
        return new GraqlDefine(definables);
    }

    public static GraqlUndefine undefine(TypeVariable... types) {
        return new GraqlUndefine(list(types));
    }

    public static GraqlUndefine undefine(List<Definable> definables) {
        return new GraqlUndefine(definables);
    }

    public static GraqlCompute.Builder compute() {
        return new GraqlCompute.Builder();
    }

    // Pattern Builder Methods

    public static Conjunction<? extends Pattern> and(Pattern... patterns) {
        return and(Arrays.asList(patterns));
    }

    public static Conjunction<? extends Pattern> and(List<? extends Pattern> patterns) {
        return new Conjunction<>(patterns);
    }

    public static Pattern or(Pattern... patterns) {
        return or(Arrays.asList(patterns));
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

    public static UnboundVariable var() {
        return UnboundVariable.anonymous();
    }

    public static UnboundVariable var(String name) {
        return UnboundVariable.named(name);
    }

    public static TypeVariable type(GraqlToken.Type type) {
        return type(type.toString());
    }

    public static TypeVariable type(String label) {
        return hidden().type(label);
    }

    public static ThingVariable.Relation rel(String playerVar) {
        return hidden().rel(playerVar);
    }

    public static ThingVariable.Relation rel(UnboundVariable playerVar) {
        return hidden().rel(playerVar);
    }

    public static ThingVariable.Relation rel(String roleType, String playerVar) {
        return hidden().rel(roleType, playerVar);
    }

    public static ThingVariable.Relation rel(String roleType, UnboundVariable playerVar) {
        return hidden().rel(roleType, playerVar);
    }

    public static ThingVariable.Relation rel(UnboundVariable roleType, UnboundVariable playerVar) {
        return hidden().rel(roleType, playerVar);
    }

    public static ThingConstraint.Value.Long eq(long value) {
        return new ThingConstraint.Value.Long(EQ, value);
    }

    public static ThingConstraint.Value.Double eq(double value) {
        return new ThingConstraint.Value.Double(EQ, value);
    }

    public static ThingConstraint.Value.Boolean eq(boolean value) {
        return new ThingConstraint.Value.Boolean(EQ, value);
    }

    public static ThingConstraint.Value.String eq(String value) {
        return new ThingConstraint.Value.String(EQ, value);
    }

    public static ThingConstraint.Value.DateTime eq(LocalDateTime value) {
        return new ThingConstraint.Value.DateTime(EQ, value);
    }

    public static ThingConstraint.Value.Variable eq(UnboundVariable variable) {
        return new ThingConstraint.Value.Variable(EQ, variable);
    }

    public static ThingConstraint.Value.Long neq(long value) {
        return new ThingConstraint.Value.Long(NEQ, value);
    }

    public static ThingConstraint.Value.Double neq(double value) {
        return new ThingConstraint.Value.Double(NEQ, value);
    }

    public static ThingConstraint.Value.Boolean neq(boolean value) {
        return new ThingConstraint.Value.Boolean(NEQ, value);
    }

    public static ThingConstraint.Value.String neq(String value) {
        return new ThingConstraint.Value.String(NEQ, value);
    }

    public static ThingConstraint.Value.DateTime neq(LocalDateTime value) {
        return new ThingConstraint.Value.DateTime(NEQ, value);
    }

    public static ThingConstraint.Value.Variable neq(UnboundVariable variable) {
        return new ThingConstraint.Value.Variable(NEQ, variable);
    }

    public static ThingConstraint.Value.Long gt(long value) {
        return new ThingConstraint.Value.Long(GT, value);
    }

    public static ThingConstraint.Value.Double gt(double value) {
        return new ThingConstraint.Value.Double(GT, value);
    }

    public static ThingConstraint.Value.Boolean gt(boolean value) {
        return new ThingConstraint.Value.Boolean(GT, value);
    }

    public static ThingConstraint.Value.String gt(String value) {
        return new ThingConstraint.Value.String(GT, value);
    }

    public static ThingConstraint.Value.DateTime gt(LocalDateTime value) {
        return new ThingConstraint.Value.DateTime(GT, value);
    }

    public static ThingConstraint.Value.Variable gt(UnboundVariable variable) {
        return new ThingConstraint.Value.Variable(GT, variable);
    }

    public static ThingConstraint.Value.Long gte(long value) {
        return new ThingConstraint.Value.Long(GTE, value);
    }

    public static ThingConstraint.Value.Double gte(double value) {
        return new ThingConstraint.Value.Double(GTE, value);
    }

    public static ThingConstraint.Value.Boolean gte(boolean value) {
        return new ThingConstraint.Value.Boolean(GTE, value);
    }

    public static ThingConstraint.Value.String gte(String value) {
        return new ThingConstraint.Value.String(GTE, value);
    }

    public static ThingConstraint.Value.DateTime gte(LocalDateTime value) {
        return new ThingConstraint.Value.DateTime(GTE, value);
    }

    public static ThingConstraint.Value.Variable gte(UnboundVariable variable) {
        return new ThingConstraint.Value.Variable(GTE, variable);
    }

    public static ThingConstraint.Value.Long lt(long value) {
        return new ThingConstraint.Value.Long(LT, value);
    }

    public static ThingConstraint.Value.Double lt(double value) {
        return new ThingConstraint.Value.Double(LT, value);
    }

    public static ThingConstraint.Value.Boolean lt(boolean value) {
        return new ThingConstraint.Value.Boolean(LT, value);
    }

    public static ThingConstraint.Value.String lt(String value) {
        return new ThingConstraint.Value.String(LT, value);
    }

    public static ThingConstraint.Value.DateTime lt(LocalDateTime value) {
        return new ThingConstraint.Value.DateTime(LT, value);
    }

    public static ThingConstraint.Value.Variable lt(UnboundVariable variable) {
        return new ThingConstraint.Value.Variable(LT, variable);
    }

    public static ThingConstraint.Value.Long lte(long value) {
        return new ThingConstraint.Value.Long(LTE, value);
    }

    public static ThingConstraint.Value.Double lte(double value) {
        return new ThingConstraint.Value.Double(LTE, value);
    }

    public static ThingConstraint.Value.Boolean lte(boolean value) {
        return new ThingConstraint.Value.Boolean(LTE, value);
    }

    public static ThingConstraint.Value.String lte(String value) {
        return new ThingConstraint.Value.String(LTE, value);
    }

    public static ThingConstraint.Value.DateTime lte(LocalDateTime value) {
        return new ThingConstraint.Value.DateTime(LTE, value);
    }

    public static ThingConstraint.Value.Variable lte(UnboundVariable variable) {
        return new ThingConstraint.Value.Variable(LTE, variable);
    }

    public static ThingConstraint.Value.String contains(String value) {
        return new ThingConstraint.Value.String(CONTAINS, value);
    }

    public static ThingConstraint.Value.String like(String value) {
        return new ThingConstraint.Value.String(LIKE, value);
    }

}
