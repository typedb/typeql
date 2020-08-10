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
import graql.lang.pattern.Disjunction;
import graql.lang.pattern.Negation;
import graql.lang.pattern.Pattern;
import graql.lang.pattern.property.ThingProperty;
import graql.lang.pattern.property.ValueOperation;
import graql.lang.pattern.variable.ThingVariable;
import graql.lang.pattern.variable.TypeVariable;
import graql.lang.pattern.variable.UnboundVariable;
import graql.lang.query.GraqlCompute;
import graql.lang.query.GraqlDefine;
import graql.lang.query.GraqlInsert;
import graql.lang.query.GraqlQuery;
import graql.lang.query.GraqlUndefine;
import graql.lang.query.MatchClause;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static grakn.common.collection.Collections.list;
import static graql.lang.pattern.variable.UnboundVariable.hidden;

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

    public static MatchClause match(Pattern... patterns) {
        return match(Arrays.asList(patterns));
    }

    public static MatchClause match(List<? extends Pattern> patterns) {
        return new MatchClause(patterns);
    }

    public static GraqlInsert insert(ThingVariable<?>... things) {
        return new GraqlInsert(list(things));
    }

    public static GraqlInsert insert(List<ThingVariable<?>> things) {
        return new GraqlInsert(things);
    }

    public static GraqlDefine define(TypeVariable... types) {
        return new GraqlDefine(list(types));
    }

    public static GraqlDefine define(List<TypeVariable> types) {
        return new GraqlDefine(types);
    }

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
        if (patterns.size() == 1) {
            return patterns.iterator().next();
        }

        return new Disjunction<>(patterns);
    }

    public static Negation<Pattern> not(Pattern pattern) {
        return new Negation<>(pattern);
    }

    // Variable Builder Methods

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

    public static ThingProperty.Value<Long> eq(long value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Number<>(GraqlToken.Comparator.EQ, value));
    }

    public static ThingProperty.Value<Double> eq(double value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Number<>(GraqlToken.Comparator.EQ, value));
    }

    public static ThingProperty.Value<Boolean> eq(boolean value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Boolean(GraqlToken.Comparator.EQ, value));
    }

    public static ThingProperty.Value<String> eq(String value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.String(GraqlToken.Comparator.EQ, value));
    }

    public static ThingProperty.Value<LocalDateTime> eq(LocalDateTime value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.DateTime(GraqlToken.Comparator.EQ, value));
    }

    public static ThingProperty.Value<UnboundVariable> eq(UnboundVariable variable) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Variable(GraqlToken.Comparator.EQ, variable));
    }

    // Attribute value inequality property

    public static ThingProperty.Value<Long> neq(long value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Number<>(GraqlToken.Comparator.NEQ, value));
    }

    public static ThingProperty.Value<Double> neq(double value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Number<>(GraqlToken.Comparator.NEQ, value));
    }

    public static ThingProperty.Value<Boolean> neq(boolean value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Boolean(GraqlToken.Comparator.NEQ, value));
    }

    public static ThingProperty.Value<String> neq(String value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.String(GraqlToken.Comparator.NEQ, value));
    }

    public static ThingProperty.Value<LocalDateTime> neq(LocalDateTime value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.DateTime(GraqlToken.Comparator.NEQ, value));
    }

    public static ThingProperty.Value<UnboundVariable> neq(UnboundVariable variable) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Variable(GraqlToken.Comparator.NEQ, variable));
    }

    // Attribute value greater-than property

    public static ThingProperty.Value<Long> gt(long value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Number<>(GraqlToken.Comparator.GT, value));
    }

    public static ThingProperty.Value<Double> gt(double value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Number<>(GraqlToken.Comparator.GT, value));
    }

    public static ThingProperty.Value<Boolean> gt(boolean value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Boolean(GraqlToken.Comparator.GT, value));
    }

    public static ThingProperty.Value<String> gt(String value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.String(GraqlToken.Comparator.GT, value));
    }

    public static ThingProperty.Value<LocalDateTime> gt(LocalDateTime value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.DateTime(GraqlToken.Comparator.GT, value));
    }

    public static ThingProperty.Value<UnboundVariable> gt(UnboundVariable variable) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Variable(GraqlToken.Comparator.GT, variable));
    }

    // Attribute value greater-than-or-equals property

    public static ThingProperty.Value<Long> gte(long value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Number<>(GraqlToken.Comparator.GTE, value));
    }

    public static ThingProperty.Value<Double> gte(double value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Number<>(GraqlToken.Comparator.GTE, value));
    }

    public static ThingProperty.Value<Boolean> gte(boolean value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Boolean(GraqlToken.Comparator.GTE, value));
    }

    public static ThingProperty.Value<String> gte(String value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.String(GraqlToken.Comparator.GTE, value));
    }

    public static ThingProperty.Value<LocalDateTime> gte(LocalDateTime value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.DateTime(GraqlToken.Comparator.GTE, value));
    }

    public static ThingProperty.Value<UnboundVariable> gte(UnboundVariable variable) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Variable(GraqlToken.Comparator.GTE, variable));
    }

    // Attribute value less-than property

    public static ThingProperty.Value<Long> lt(long value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Number<>(GraqlToken.Comparator.LT, value));
    }

    public static ThingProperty.Value<Double> lt(double value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Number<>(GraqlToken.Comparator.LT, value));
    }

    public static ThingProperty.Value<Boolean> lt(boolean value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Boolean(GraqlToken.Comparator.LT, value));
    }

    public static ThingProperty.Value<String> lt(String value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.String(GraqlToken.Comparator.LT, value));
    }

    public static ThingProperty.Value<LocalDateTime> lt(LocalDateTime value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.DateTime(GraqlToken.Comparator.LT, value));
    }

    public static ThingProperty.Value<UnboundVariable> lt(UnboundVariable variable) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Variable(GraqlToken.Comparator.LT, variable));
    }

    // Attribute value less-than-or-equals property

    public static ThingProperty.Value<Long> lte(long value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Number<>(GraqlToken.Comparator.LTE, value));
    }

    public static ThingProperty.Value<Double> lte(double value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Number<>(GraqlToken.Comparator.LTE, value));
    }

    public static ThingProperty.Value<Boolean> lte(boolean value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Boolean(GraqlToken.Comparator.LTE, value));
    }

    public static ThingProperty.Value<String> lte(String value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.String(GraqlToken.Comparator.LTE, value));
    }

    public static ThingProperty.Value<LocalDateTime> lte(LocalDateTime value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.DateTime(GraqlToken.Comparator.LTE, value));
    }

    public static ThingProperty.Value<UnboundVariable> lte(UnboundVariable variable) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Variable(GraqlToken.Comparator.LTE, variable));
    }

    // Attribute value contains (in String) property

    public static ThingProperty.Value<String> contains(String value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.String(GraqlToken.Comparator.CONTAINS, value));
    }

    public static ThingProperty.Value<UnboundVariable> contains(UnboundVariable variable) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.Variable(GraqlToken.Comparator.CONTAINS, variable));
    }

    // Attribute value regex property

    public static ThingProperty.Value<String> like(String value) {
        return new ThingProperty.Value<>(new ValueOperation.Comparison.String(GraqlToken.Comparator.LIKE, value));
    }

}
