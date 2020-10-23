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

package graql.lang.pattern.variable.builder;

import graql.lang.common.GraqlToken;
import graql.lang.pattern.constraint.ThingConstraint;
import graql.lang.pattern.variable.ThingVariable;
import graql.lang.pattern.variable.UnboundVariable;

import java.time.LocalDateTime;
import java.util.function.BiFunction;

import static graql.lang.common.GraqlToken.Comparator.*;

public interface ThingVariableBuilder {

    interface Common<T> {

        default T isa(final GraqlToken.Type type) {
            return isa(type.toString());
        }

        default T isa(final String type) {
            return constrain(new ThingConstraint.Isa(type, false));
        }

        default T isa(final UnboundVariable var) {
            return constrain(new ThingConstraint.Isa(var, false));
        }

        default T isaX(final GraqlToken.Type type) {
            return isa(type.toString());
        }

        default T isaX(final String type) {
            return constrain(new ThingConstraint.Isa(type, true));
        }

        default T isaX(final UnboundVariable var) {
            return constrain(new ThingConstraint.Isa(var, true));
        }

        default T has(final String type, final long value) {
            return has(type, new ThingConstraint.Value.Long(EQ, value));
        }

        default T has(final String type, final double value) {
            return has(type, new ThingConstraint.Value.Double(EQ, value));
        }

        default T has(final String type, final boolean value) {
            return has(type, new ThingConstraint.Value.Boolean(EQ, value));
        }

        default T has(final String type, final String value) {
            return has(type, new ThingConstraint.Value.String(EQ, value));
        }

        default T has(final String type, final LocalDateTime value) {
            return has(type, new ThingConstraint.Value.DateTime(EQ, value));
        }

        default T has(final String type, final ThingConstraint.Value<?> value) {
            return constrain(new ThingConstraint.Has(type, value));
        }

        default T has(final String type, final UnboundVariable variable) {
            return constrain(new ThingConstraint.Has(type, variable));
        }

        T constrain(ThingConstraint.Isa constraint);

        T constrain(ThingConstraint.Has constraint);
    }

    interface Thing {

        default ThingVariable.Thing iid(final String iid) {
            return constrain(new ThingConstraint.IID(iid));
        }

        ThingVariable.Thing constrain(ThingConstraint.IID constraint);
    }

    interface Relation {

        default ThingVariable.Relation rel(final String playerVar) {
            return rel(UnboundVariable.named(playerVar));
        }

        default ThingVariable.Relation rel(final UnboundVariable playerVar) {
            return constrain(new ThingConstraint.Relation.RolePlayer(playerVar));
        }

        default ThingVariable.Relation rel(final String roleType, final String playerVar) {
            return constrain(new ThingConstraint.Relation.RolePlayer(roleType, UnboundVariable.named(playerVar)));
        }

        default ThingVariable.Relation rel(final String roleType, final UnboundVariable playerVar) {
            return constrain(new ThingConstraint.Relation.RolePlayer(roleType, playerVar));
        }

        default ThingVariable.Relation rel(final UnboundVariable roleTypeVar, final UnboundVariable playerVar) {
            return constrain(new ThingConstraint.Relation.RolePlayer(roleTypeVar, playerVar));
        }

        ThingVariable.Relation constrain(ThingConstraint.Relation.RolePlayer rolePlayer);
    }

    interface Attribute {

        // Attribute value equality constraint

        default ThingVariable.Attribute eq(final long value) {
            return eq(ThingConstraint.Value.Long::new, value);
        }

        default ThingVariable.Attribute eq(final double value) {
            return eq(ThingConstraint.Value.Double::new, value);
        }

        default ThingVariable.Attribute eq(final boolean value) {
            return eq(ThingConstraint.Value.Boolean::new, value);
        }

        default ThingVariable.Attribute eq(final String value) {
            return eq(ThingConstraint.Value.String::new, value);
        }

        default ThingVariable.Attribute eq(final LocalDateTime value) {
            return eq(ThingConstraint.Value.DateTime::new, value);
        }

        default ThingVariable.Attribute eq(final UnboundVariable variable) {
            return operation(new ThingConstraint.Value.Variable(EQ, variable));
        }

        default <T> ThingVariable.Attribute eq(final BiFunction<GraqlToken.Comparator, T, ThingConstraint.Value<T>> constructor, final T value) {
            return operation(constructor.apply(EQ, value));
        }

        // Attribute value inequality constraint

        default ThingVariable.Attribute neq(final long value) {
            return neq(ThingConstraint.Value.Long::new, value);
        }

        default ThingVariable.Attribute neq(final double value) {
            return neq(ThingConstraint.Value.Double::new, value);
        }

        default ThingVariable.Attribute neq(final boolean value) {
            return neq(ThingConstraint.Value.Boolean::new, value);
        }

        default ThingVariable.Attribute neq(final String value) {
            return neq(ThingConstraint.Value.String::new, value);
        }

        default ThingVariable.Attribute neq(final LocalDateTime value) {
            return neq(ThingConstraint.Value.DateTime::new, value);
        }

        default ThingVariable.Attribute neq(final UnboundVariable variable) {
            return operation(new ThingConstraint.Value.Variable(NEQ, variable));
        }

        default <T> ThingVariable.Attribute neq(final BiFunction<GraqlToken.Comparator, T, ThingConstraint.Value<T>> constructor, final T value) {
            return operation(constructor.apply(NEQ, value));
        }

        // Attribute value greater-than constraint

        default ThingVariable.Attribute gt(final long value) {
            return gt(ThingConstraint.Value.Long::new, value);
        }

        default ThingVariable.Attribute gt(final double value) {
            return gt(ThingConstraint.Value.Double::new, value);
        }

        default ThingVariable.Attribute gt(final boolean value) {
            return gt(ThingConstraint.Value.Boolean::new, value);
        }

        default ThingVariable.Attribute gt(final String value) {
            return gt(ThingConstraint.Value.String::new, value);
        }

        default ThingVariable.Attribute gt(final LocalDateTime value) {
            return gt(ThingConstraint.Value.DateTime::new, value);
        }

        default ThingVariable.Attribute gt(final UnboundVariable variable) {
            return operation(new ThingConstraint.Value.Variable(GT, variable));
        }

        default <T> ThingVariable.Attribute gt(final BiFunction<GraqlToken.Comparator, T, ThingConstraint.Value<T>> constructor, final T value) {
            return operation(constructor.apply(GT, value));
        }

        // Attribute value greater-than-or-equals constraint

        default ThingVariable.Attribute gte(final long value) {
            return gte(ThingConstraint.Value.Long::new, value);
        }

        default ThingVariable.Attribute gte(final double value) {
            return gte(ThingConstraint.Value.Double::new, value);
        }

        default ThingVariable.Attribute gte(final boolean value) {
            return gte(ThingConstraint.Value.Boolean::new, value);
        }

        default ThingVariable.Attribute gte(final String value) {
            return gte(ThingConstraint.Value.String::new, value);
        }

        default ThingVariable.Attribute gte(final LocalDateTime value) {
            return gte(ThingConstraint.Value.DateTime::new, value);
        }

        default ThingVariable.Attribute gte(final UnboundVariable variable) {
            return operation(new ThingConstraint.Value.Variable(GTE, variable));
        }

        default <T> ThingVariable.Attribute gte(final BiFunction<GraqlToken.Comparator, T, ThingConstraint.Value<T>> constructor, final T value) {
            return operation(constructor.apply(GTE, value));
        }

        // Attribute value less-than constraint

        default ThingVariable.Attribute lt(final long value) {
            return lt(ThingConstraint.Value.Long::new, value);
        }

        default ThingVariable.Attribute lt(final double value) {
            return lt(ThingConstraint.Value.Double::new, value);
        }

        default ThingVariable.Attribute lt(final boolean value) {
            return lt(ThingConstraint.Value.Boolean::new, value);
        }

        default ThingVariable.Attribute lt(final String value) {
            return lt(ThingConstraint.Value.String::new, value);
        }

        default ThingVariable.Attribute lt(final LocalDateTime value) {
            return lt(ThingConstraint.Value.DateTime::new, value);
        }

        default ThingVariable.Attribute lt(final UnboundVariable variable) {
            return operation(new ThingConstraint.Value.Variable(LT, variable));
        }

        default <T> ThingVariable.Attribute lt(final BiFunction<GraqlToken.Comparator, T, ThingConstraint.Value<T>> constructor, final T value) {
            return operation(constructor.apply(LT, value));
        }

        // Attribute value less-than-or-equals constraint

        default ThingVariable.Attribute lte(final long value) {
            return lte(ThingConstraint.Value.Long::new, value);
        }

        default ThingVariable.Attribute lte(final double value) {
            return lte(ThingConstraint.Value.Double::new, value);
        }

        default ThingVariable.Attribute lte(final boolean value) {
            return lte(ThingConstraint.Value.Boolean::new, value);
        }

        default ThingVariable.Attribute lte(final String value) {
            return lte(ThingConstraint.Value.String::new, value);
        }

        default ThingVariable.Attribute lte(final LocalDateTime value) {
            return lte(ThingConstraint.Value.DateTime::new, value);
        }

        default ThingVariable.Attribute lte(final UnboundVariable variable) {
            return operation(new ThingConstraint.Value.Variable(LTE, variable));
        }

        default <T> ThingVariable.Attribute lte(final BiFunction<GraqlToken.Comparator, T, ThingConstraint.Value<T>> constructor, final T value) {
            return operation(constructor.apply(LTE, value));
        }

        // Attribute value contains (in String) constraint

        default ThingVariable.Attribute contains(final String value) {
            return contains(ThingConstraint.Value.String::new, value);
        }

        default ThingVariable.Attribute contains(final UnboundVariable variable) {
            return operation(new ThingConstraint.Value.Variable(CONTAINS, variable));
        }

        default <T> ThingVariable.Attribute contains(final BiFunction<GraqlToken.Comparator, T, ThingConstraint.Value<T>> constructor, final T value) {
            return operation(constructor.apply(CONTAINS, value));
        }

        // Attribute value like (regex) constraint

        default ThingVariable.Attribute like(final String value) {
            return operation(new ThingConstraint.Value.String(GraqlToken.Comparator.LIKE, value));
        }

        default ThingVariable.Attribute operation(final ThingConstraint.Value<?> operation) {
            return constrain(operation);
        }

        ThingVariable.Attribute constrain(final ThingConstraint.Value<?> constraint);
    }
}
