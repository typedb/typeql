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

import graql.lang.common.GraqlToken;
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
import static graql.lang.pattern.variable.UnboundVariable.hidden;

public class Graql {

    private static final Parser parser = new Parser();

    public static <T extends GraqlQuery> T parseQuery(final String queryString) {
        return parser.parseQueryEOF(queryString);
    }

    public static <T extends GraqlQuery> Stream<T> parseQueries(final String queryString) {
        return parser.parseQueriesEOF(queryString);
    }

    public static Pattern parsePattern(final String pattern) {
        return parser.parsePatternEOF(pattern);
    }

    public static List<? extends Pattern> parsePatterns(final String pattern) {
        return parser.parsePatternsEOF(pattern);
    }

    public static List<Definable> parseDefinables(final String pattern) { return parser.parseDefinablesEOF(pattern); }

    public static Rule parseRule(final String pattern) { return parser.parseSchemaRuleEOF(pattern).asRule(); }

    public static BoundVariable parseVariable(final String variable) {
        return parser.parseVariableEOF(variable);
    }

    public static GraqlMatch.Unfiltered match(final Pattern... patterns) {
        return match(Arrays.asList(patterns));
    }

    public static GraqlMatch.Unfiltered match(final List<? extends Pattern> patterns) {
        return new GraqlMatch.Unfiltered(patterns);
    }

    public static GraqlInsert insert(final ThingVariable<?>... things) {
        return new GraqlInsert(list(things));
    }

    public static GraqlInsert insert(final List<ThingVariable<?>> things) {
        return new GraqlInsert(things);
    }

    public static GraqlDefine define(final Definable... definables) {
        return new GraqlDefine(list(definables));
    }

    public static GraqlDefine define(final List<Definable> definables) {
        return new GraqlDefine(definables);
    }

    public static GraqlUndefine undefine(final TypeVariable... types) {
        return new GraqlUndefine(list(types));
    }

    public static GraqlUndefine undefine(final List<Definable> definables) {
        return new GraqlUndefine(definables);
    }

    public static GraqlCompute.Builder compute() {
        return new GraqlCompute.Builder();
    }

    // Pattern Builder Methods

    public static Conjunction<? extends Pattern> and(final Pattern... patterns) {
        return and(Arrays.asList(patterns));
    }

    public static Conjunction<? extends Pattern> and(final List<? extends Pattern> patterns) {
        return new Conjunction<>(patterns);
    }

    public static Pattern or(final Pattern... patterns) {
        return or(Arrays.asList(patterns));
    }

    public static Pattern or(final List<Pattern> patterns) {
        // Simplify representation when there is only one alternative
        if (patterns.size() == 1) {
            return patterns.iterator().next();
        }

        return new Disjunction<>(patterns);
    }

    public static Negation<Pattern> not(final Pattern pattern) {
        return new Negation<>(pattern);
    }

    public static Rule rule(final String label) { return new Rule(label); }

    // Variable Builder Methods

    public static UnboundVariable var() {
        return UnboundVariable.anonymous();
    }

    public static UnboundVariable var(final String name) {
        return UnboundVariable.named(name);
    }

    public static TypeVariable type(final GraqlToken.Type type) {
        return type(type.toString());
    }

    public static TypeVariable type(final String label) {
        return hidden().type(label);
    }

    public static ThingVariable.Relation rel(final String playerVar) {
        return hidden().rel(playerVar);
    }

    public static ThingVariable.Relation rel(final UnboundVariable playerVar) {
        return hidden().rel(playerVar);
    }

    public static ThingVariable.Relation rel(final String roleType, final String playerVar) {
        return hidden().rel(roleType, playerVar);
    }

    public static ThingVariable.Relation rel(final String roleType, final UnboundVariable playerVar) {
        return hidden().rel(roleType, playerVar);
    }

    public static ThingVariable.Relation rel(final UnboundVariable roleType, final UnboundVariable playerVar) {
        return hidden().rel(roleType, playerVar);
    }

    // Attribute value equality constraint

    public static ThingConstraint.Value.Long eq(final long value) {
        return new ThingConstraint.Value.Long(GraqlToken.Comparator.EQ, value);
    }

    public static ThingConstraint.Value.Double eq(final double value) {
        return new ThingConstraint.Value.Double(GraqlToken.Comparator.EQ, value);
    }

    public static ThingConstraint.Value.Boolean eq(final boolean value) {
        return new ThingConstraint.Value.Boolean(GraqlToken.Comparator.EQ, value);
    }

    public static ThingConstraint.Value.String eq(final String value) {
        return new ThingConstraint.Value.String(GraqlToken.Comparator.EQ, value);
    }

    public static ThingConstraint.Value.DateTime eq(final LocalDateTime value) {
        return new ThingConstraint.Value.DateTime(GraqlToken.Comparator.EQ, value);
    }

    public static ThingConstraint.Value.Variable eq(final UnboundVariable variable) {
        return new ThingConstraint.Value.Variable(GraqlToken.Comparator.EQ, variable);
    }

    // Attribute value inequality constraint

    public static ThingConstraint.Value.Long neq(final long value) {
        return new ThingConstraint.Value.Long(GraqlToken.Comparator.NEQ, value);
    }

    public static ThingConstraint.Value.Double neq(final double value) {
        return new ThingConstraint.Value.Double(GraqlToken.Comparator.NEQ, value);
    }

    public static ThingConstraint.Value.Boolean neq(final boolean value) {
        return new ThingConstraint.Value.Boolean(GraqlToken.Comparator.NEQ, value);
    }

    public static ThingConstraint.Value.String neq(final String value) {
        return new ThingConstraint.Value.String(GraqlToken.Comparator.NEQ, value);
    }

    public static ThingConstraint.Value.DateTime neq(final LocalDateTime value) {
        return new ThingConstraint.Value.DateTime(GraqlToken.Comparator.NEQ, value);
    }

    public static ThingConstraint.Value.Variable neq(final UnboundVariable variable) {
        return new ThingConstraint.Value.Variable(GraqlToken.Comparator.NEQ, variable);
    }

    // Attribute value greater-than constraint

    public static ThingConstraint.Value.Long gt(final long value) {
        return new ThingConstraint.Value.Long(GraqlToken.Comparator.GT, value);
    }

    public static ThingConstraint.Value.Double gt(final double value) {
        return new ThingConstraint.Value.Double(GraqlToken.Comparator.GT, value);
    }

    public static ThingConstraint.Value.Boolean gt(final boolean value) {
        return new ThingConstraint.Value.Boolean(GraqlToken.Comparator.GT, value);
    }

    public static ThingConstraint.Value.String gt(final String value) {
        return new ThingConstraint.Value.String(GraqlToken.Comparator.GT, value);
    }

    public static ThingConstraint.Value.DateTime gt(final LocalDateTime value) {
        return new ThingConstraint.Value.DateTime(GraqlToken.Comparator.GT, value);
    }

    public static ThingConstraint.Value.Variable gt(final UnboundVariable variable) {
        return new ThingConstraint.Value.Variable(GraqlToken.Comparator.GT, variable);
    }

    // Attribute value greater-than-or-equals constraint

    public static ThingConstraint.Value.Long gte(final long value) {
        return new ThingConstraint.Value.Long(GraqlToken.Comparator.GTE, value);
    }

    public static ThingConstraint.Value.Double gte(final double value) {
        return new ThingConstraint.Value.Double(GraqlToken.Comparator.GTE, value);
    }

    public static ThingConstraint.Value.Boolean gte(final boolean value) {
        return new ThingConstraint.Value.Boolean(GraqlToken.Comparator.GTE, value);
    }

    public static ThingConstraint.Value.String gte(final String value) {
        return new ThingConstraint.Value.String(GraqlToken.Comparator.GTE, value);
    }

    public static ThingConstraint.Value.DateTime gte(final LocalDateTime value) {
        return new ThingConstraint.Value.DateTime(GraqlToken.Comparator.GTE, value);
    }

    public static ThingConstraint.Value.Variable gte(final UnboundVariable variable) {
        return new ThingConstraint.Value.Variable(GraqlToken.Comparator.GTE, variable);
    }

    // Attribute value less-than constraint

    public static ThingConstraint.Value.Long lt(final long value) {
        return new ThingConstraint.Value.Long(GraqlToken.Comparator.LT, value);
    }

    public static ThingConstraint.Value.Double lt(final double value) {
        return new ThingConstraint.Value.Double(GraqlToken.Comparator.LT, value);
    }

    public static ThingConstraint.Value.Boolean lt(final boolean value) {
        return new ThingConstraint.Value.Boolean(GraqlToken.Comparator.LT, value);
    }

    public static ThingConstraint.Value.String lt(final String value) {
        return new ThingConstraint.Value.String(GraqlToken.Comparator.LT, value);
    }

    public static ThingConstraint.Value.DateTime lt(final LocalDateTime value) {
        return new ThingConstraint.Value.DateTime(GraqlToken.Comparator.LT, value);
    }

    public static ThingConstraint.Value.Variable lt(final UnboundVariable variable) {
        return new ThingConstraint.Value.Variable(GraqlToken.Comparator.LT, variable);
    }

    // Attribute value less-than-or-equals constraint

    public static ThingConstraint.Value.Long lte(final long value) {
        return new ThingConstraint.Value.Long(GraqlToken.Comparator.LTE, value);
    }

    public static ThingConstraint.Value.Double lte(final double value) {
        return new ThingConstraint.Value.Double(GraqlToken.Comparator.LTE, value);
    }

    public static ThingConstraint.Value.Boolean lte(final boolean value) {
        return new ThingConstraint.Value.Boolean(GraqlToken.Comparator.LTE, value);
    }

    public static ThingConstraint.Value.String lte(final String value) {
        return new ThingConstraint.Value.String(GraqlToken.Comparator.LTE, value);
    }

    public static ThingConstraint.Value.DateTime lte(final LocalDateTime value) {
        return new ThingConstraint.Value.DateTime(GraqlToken.Comparator.LTE, value);
    }

    public static ThingConstraint.Value.Variable lte(final UnboundVariable variable) {
        return new ThingConstraint.Value.Variable(GraqlToken.Comparator.LTE, variable);
    }

    // Attribute value contains (in String) constraint

    public static ThingConstraint.Value.String contains(final String value) {
        return new ThingConstraint.Value.String(GraqlToken.Comparator.CONTAINS, value);
    }

    public static ThingConstraint.Value.Variable contains(final UnboundVariable variable) {
        return new ThingConstraint.Value.Variable(GraqlToken.Comparator.CONTAINS, variable);
    }

    // Attribute value regex constraint

    public static ThingConstraint.Value.String like(final String value) {
        return new ThingConstraint.Value.String(GraqlToken.Comparator.LIKE, value);
    }

}
