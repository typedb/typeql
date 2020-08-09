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
import graql.lang.pattern.variable.ThingVariable;
import graql.lang.pattern.variable.TypeVariable;
import graql.lang.pattern.variable.UnscopedVariable;
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
import static graql.lang.pattern.variable.UnscopedVariable.hidden;

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
        return new MatchClause(patterns);
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

    public static TypeVariable type(GraqlToken.Type type) {
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

}
