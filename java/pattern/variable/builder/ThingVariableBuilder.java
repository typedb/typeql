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

package graql.lang.pattern.variable.builder;

import graql.lang.common.GraqlToken;
import graql.lang.pattern.constraint.ThingConstraint;
import graql.lang.pattern.variable.ThingVariable;
import graql.lang.pattern.variable.UnboundVariable;

import java.time.LocalDateTime;
import java.util.function.BiFunction;

import static graql.lang.common.GraqlToken.Predicate.Equality.EQ;
import static graql.lang.common.GraqlToken.Predicate.Equality.GT;
import static graql.lang.common.GraqlToken.Predicate.Equality.GTE;
import static graql.lang.common.GraqlToken.Predicate.Equality.LT;
import static graql.lang.common.GraqlToken.Predicate.Equality.LTE;
import static graql.lang.common.GraqlToken.Predicate.Equality.NEQ;
import static graql.lang.common.GraqlToken.Predicate.SubString.CONTAINS;
import static graql.lang.common.GraqlToken.Predicate.SubString.LIKE;

public interface ThingVariableBuilder {

    interface Common<T> {

        default T isa(GraqlToken.Type type) {
            return isa(type.toString());
        }

        default T isa(String type) {
            return constrain(new ThingConstraint.Isa(type, false));
        }

        default T isa(UnboundVariable var) {
            return constrain(new ThingConstraint.Isa(var, false));
        }

        default T isaX(GraqlToken.Type type) {
            return isa(type.toString());
        }

        default T isaX(String type) {
            return constrain(new ThingConstraint.Isa(type, true));
        }

        default T isaX(UnboundVariable var) {
            return constrain(new ThingConstraint.Isa(var, true));
        }

        default T has(String type, long value) {
            return has(type, new ThingConstraint.Value.Long(EQ, value));
        }

        default T has(String type, double value) {
            return has(type, new ThingConstraint.Value.Double(EQ, value));
        }

        default T has(String type, boolean value) {
            return has(type, new ThingConstraint.Value.Boolean(EQ, value));
        }

        default T has(String type, String value) {
            return has(type, new ThingConstraint.Value.String(EQ, value));
        }

        default T has(String type, LocalDateTime value) {
            return has(type, new ThingConstraint.Value.DateTime(EQ, value));
        }

        default T has(String type, ThingConstraint.Value<?> value) {
            return constrain(new ThingConstraint.Has(type, value));
        }

        default T has(String type, UnboundVariable variable) {
            return constrain(new ThingConstraint.Has(type, variable));
        }

        default T has(UnboundVariable variable) {
            return constrain(new ThingConstraint.Has(variable));
        }

        T constrain(ThingConstraint.Isa constraint);

        T constrain(ThingConstraint.Has constraint);
    }

    interface Thing {

        default ThingVariable.Thing iid(String iid) {
            return constrain(new ThingConstraint.IID(iid));
        }

        ThingVariable.Thing constrain(ThingConstraint.IID constraint);
    }

    interface Relation {

        default ThingVariable.Relation rel(String playerVar) {
            return rel(UnboundVariable.named(playerVar));
        }

        default ThingVariable.Relation rel(UnboundVariable playerVar) {
            return constrain(new ThingConstraint.Relation.RolePlayer(playerVar));
        }

        default ThingVariable.Relation rel(String roleType, String playerVar) {
            return constrain(new ThingConstraint.Relation.RolePlayer(roleType, UnboundVariable.named(playerVar)));
        }

        default ThingVariable.Relation rel(String roleType, UnboundVariable playerVar) {
            return constrain(new ThingConstraint.Relation.RolePlayer(roleType, playerVar));
        }

        default ThingVariable.Relation rel(UnboundVariable roleTypeVar, UnboundVariable playerVar) {
            return constrain(new ThingConstraint.Relation.RolePlayer(roleTypeVar, playerVar));
        }

        ThingVariable.Relation constrain(ThingConstraint.Relation.RolePlayer rolePlayer);
    }

    interface Attribute {

        // Attribute value equality constraint

        default ThingVariable.Attribute eq(long value) {
            return eq(ThingConstraint.Value.Long::new, value);
        }

        default ThingVariable.Attribute eq(double value) {
            return eq(ThingConstraint.Value.Double::new, value);
        }

        default ThingVariable.Attribute eq(boolean value) {
            return eq(ThingConstraint.Value.Boolean::new, value);
        }

        default ThingVariable.Attribute eq(String value) {
            return eq(ThingConstraint.Value.String::new, value);
        }

        default ThingVariable.Attribute eq(LocalDateTime value) {
            return eq(ThingConstraint.Value.DateTime::new, value);
        }

        default ThingVariable.Attribute eq(UnboundVariable variable) {
            return constrain(new ThingConstraint.Value.Variable(EQ, variable));
        }

        default <T> ThingVariable.Attribute eq(BiFunction<GraqlToken.Predicate.Equality, T, ThingConstraint.Value<T>> constructor, T value) {
            return constrain(constructor.apply(EQ, value));
        }

        // Attribute value inequality constraint

        default ThingVariable.Attribute neq(long value) {
            return neq(ThingConstraint.Value.Long::new, value);
        }

        default ThingVariable.Attribute neq(double value) {
            return neq(ThingConstraint.Value.Double::new, value);
        }

        default ThingVariable.Attribute neq(boolean value) {
            return neq(ThingConstraint.Value.Boolean::new, value);
        }

        default ThingVariable.Attribute neq(String value) {
            return neq(ThingConstraint.Value.String::new, value);
        }

        default ThingVariable.Attribute neq(LocalDateTime value) {
            return neq(ThingConstraint.Value.DateTime::new, value);
        }

        default ThingVariable.Attribute neq(UnboundVariable variable) {
            return constrain(new ThingConstraint.Value.Variable(NEQ, variable));
        }

        default <T> ThingVariable.Attribute neq(BiFunction<GraqlToken.Predicate.Equality, T, ThingConstraint.Value<T>> constructor, T value) {
            return constrain(constructor.apply(NEQ, value));
        }

        // Attribute value greater-than constraint

        default ThingVariable.Attribute gt(long value) {
            return gt(ThingConstraint.Value.Long::new, value);
        }

        default ThingVariable.Attribute gt(double value) {
            return gt(ThingConstraint.Value.Double::new, value);
        }

        default ThingVariable.Attribute gt(boolean value) {
            return gt(ThingConstraint.Value.Boolean::new, value);
        }

        default ThingVariable.Attribute gt(String value) {
            return gt(ThingConstraint.Value.String::new, value);
        }

        default ThingVariable.Attribute gt(LocalDateTime value) {
            return gt(ThingConstraint.Value.DateTime::new, value);
        }

        default ThingVariable.Attribute gt(UnboundVariable variable) {
            return constrain(new ThingConstraint.Value.Variable(GT, variable));
        }

        default <T> ThingVariable.Attribute gt(BiFunction<GraqlToken.Predicate.Equality, T, ThingConstraint.Value<T>> constructor, T value) {
            return constrain(constructor.apply(GT, value));
        }

        // Attribute value greater-than-or-equals constraint

        default ThingVariable.Attribute gte(long value) {
            return gte(ThingConstraint.Value.Long::new, value);
        }

        default ThingVariable.Attribute gte(double value) {
            return gte(ThingConstraint.Value.Double::new, value);
        }

        default ThingVariable.Attribute gte(boolean value) {
            return gte(ThingConstraint.Value.Boolean::new, value);
        }

        default ThingVariable.Attribute gte(String value) {
            return gte(ThingConstraint.Value.String::new, value);
        }

        default ThingVariable.Attribute gte(LocalDateTime value) {
            return gte(ThingConstraint.Value.DateTime::new, value);
        }

        default ThingVariable.Attribute gte(UnboundVariable variable) {
            return constrain(new ThingConstraint.Value.Variable(GTE, variable));
        }

        default <T> ThingVariable.Attribute gte(BiFunction<GraqlToken.Predicate.Equality, T, ThingConstraint.Value<T>> constructor, T value) {
            return constrain(constructor.apply(GTE, value));
        }

        // Attribute value less-than constraint

        default ThingVariable.Attribute lt(long value) {
            return lt(ThingConstraint.Value.Long::new, value);
        }

        default ThingVariable.Attribute lt(double value) {
            return lt(ThingConstraint.Value.Double::new, value);
        }

        default ThingVariable.Attribute lt(boolean value) {
            return lt(ThingConstraint.Value.Boolean::new, value);
        }

        default ThingVariable.Attribute lt(String value) {
            return lt(ThingConstraint.Value.String::new, value);
        }

        default ThingVariable.Attribute lt(LocalDateTime value) {
            return lt(ThingConstraint.Value.DateTime::new, value);
        }

        default ThingVariable.Attribute lt(UnboundVariable variable) {
            return constrain(new ThingConstraint.Value.Variable(LT, variable));
        }

        default <T> ThingVariable.Attribute lt(BiFunction<GraqlToken.Predicate.Equality, T, ThingConstraint.Value<T>> constructor, T value) {
            return constrain(constructor.apply(LT, value));
        }

        // Attribute value less-than-or-equals constraint

        default ThingVariable.Attribute lte(long value) {
            return lte(ThingConstraint.Value.Long::new, value);
        }

        default ThingVariable.Attribute lte(double value) {
            return lte(ThingConstraint.Value.Double::new, value);
        }

        default ThingVariable.Attribute lte(boolean value) {
            return lte(ThingConstraint.Value.Boolean::new, value);
        }

        default ThingVariable.Attribute lte(String value) {
            return lte(ThingConstraint.Value.String::new, value);
        }

        default ThingVariable.Attribute lte(LocalDateTime value) {
            return lte(ThingConstraint.Value.DateTime::new, value);
        }

        default ThingVariable.Attribute lte(UnboundVariable variable) {
            return constrain(new ThingConstraint.Value.Variable(LTE, variable));
        }

        default <T> ThingVariable.Attribute lte(BiFunction<GraqlToken.Predicate.Equality, T, ThingConstraint.Value<T>> constructor, T value) {
            return constrain(constructor.apply(LTE, value));
        }

        default ThingVariable.Attribute contains(String value) {
            return constrain(new ThingConstraint.Value.String(CONTAINS, value));
        }

        default ThingVariable.Attribute like(String regex) {
            return constrain(new ThingConstraint.Value.String(LIKE, regex));
        }

        ThingVariable.Attribute constrain(ThingConstraint.Value<?> constraint);
    }
}
