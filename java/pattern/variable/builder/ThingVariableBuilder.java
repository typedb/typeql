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
import graql.lang.pattern.constraint.ValueOperation;
import graql.lang.pattern.variable.ThingVariable;
import graql.lang.pattern.variable.UnboundVariable;

import java.time.LocalDateTime;
import java.util.function.BiFunction;

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
            return has(type, new ThingConstraint.Value<>(new ValueOperation.Assignment.Long(value)));
        }

        default T has(final String type, final double value) {
            return has(type, new ThingConstraint.Value<>(new ValueOperation.Assignment.Double(value)));
        }

        default T has(final String type, final boolean value) {
            return has(type, new ThingConstraint.Value<>(new ValueOperation.Assignment.Boolean(value)));
        }

        default T has(final String type, final String value) {
            return has(type, new ThingConstraint.Value<>(new ValueOperation.Assignment.String(value)));
        }

        default T has(final String type, final LocalDateTime value) {
            return has(type, new ThingConstraint.Value<>(new ValueOperation.Assignment.DateTime(value)));
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

        default ThingVariable.Thing not(final String var) {
            return not(UnboundVariable.named(var));
        }

        default ThingVariable.Thing not(final UnboundVariable var) {
            return constrain(new ThingConstraint.NEQ(var));
        }

        ThingVariable.Thing constrain(ThingConstraint.IID constraint);

        ThingVariable.Thing constrain(ThingConstraint.NEQ constraint);
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

        // Attribute value assignment constraint

        default ThingVariable.Attribute val(final long value) {
            return operation(new ValueOperation.Assignment.Long(value));
        }

        default ThingVariable.Attribute val(final double value) {
            return operation(new ValueOperation.Assignment.Double(value));
        }

        default ThingVariable.Attribute val(final boolean value) {
            return operation(new ValueOperation.Assignment.Boolean(value));
        }

        default ThingVariable.Attribute val(final String value) {
            return operation(new ValueOperation.Assignment.String(value));
        }

        default ThingVariable.Attribute val(final LocalDateTime value) {
            return operation(new ValueOperation.Assignment.DateTime(value));
        }

        // Attribute value equality constraint

        default ThingVariable.Attribute eq(final long value) {
            return eq(ValueOperation.Comparison.Long::new, value);
        }

        default ThingVariable.Attribute eq(final double value) {
            return eq(ValueOperation.Comparison.Double::new, value);
        }

        default ThingVariable.Attribute eq(final boolean value) {
            return eq(ValueOperation.Comparison.Boolean::new, value);
        }

        default ThingVariable.Attribute eq(final String value) {
            return eq(ValueOperation.Comparison.String::new, value);
        }

        default ThingVariable.Attribute eq(final LocalDateTime value) {
            return eq(ValueOperation.Comparison.DateTime::new, value);
        }

        default ThingVariable.Attribute eq(final UnboundVariable variable) {
            return eq(ValueOperation.Comparison.Variable::new, variable);
        }

        default <T> ThingVariable.Attribute eq(final BiFunction<GraqlToken.Comparator, T, ValueOperation.Comparison<T>> constructor, final T value) {
            return operation(constructor.apply(GraqlToken.Comparator.EQV, value));
        }

        // Attribute value inequality constraint

        default ThingVariable.Attribute neq(final long value) {
            return neq(ValueOperation.Comparison.Long::new, value);
        }

        default ThingVariable.Attribute neq(final double value) {
            return neq(ValueOperation.Comparison.Double::new, value);
        }

        default ThingVariable.Attribute neq(final boolean value) {
            return neq(ValueOperation.Comparison.Boolean::new, value);
        }

        default ThingVariable.Attribute neq(final String value) {
            return neq(ValueOperation.Comparison.String::new, value);
        }

        default ThingVariable.Attribute neq(final LocalDateTime value) {
            return neq(ValueOperation.Comparison.DateTime::new, value);
        }

        default ThingVariable.Attribute neq(final UnboundVariable variable) {
            return neq(ValueOperation.Comparison.Variable::new, variable);
        }

        default <T> ThingVariable.Attribute neq(final BiFunction<GraqlToken.Comparator, T, ValueOperation.Comparison<T>> constructor, final T value) {
            return operation(constructor.apply(GraqlToken.Comparator.NEQV, value));
        }

        // Attribute value greater-than constraint

        default ThingVariable.Attribute gt(final long value) {
            return gt(ValueOperation.Comparison.Long::new, value);
        }

        default ThingVariable.Attribute gt(final double value) {
            return gt(ValueOperation.Comparison.Double::new, value);
        }

        default ThingVariable.Attribute gt(final boolean value) {
            return gt(ValueOperation.Comparison.Boolean::new, value);
        }

        default ThingVariable.Attribute gt(final String value) {
            return gt(ValueOperation.Comparison.String::new, value);
        }

        default ThingVariable.Attribute gt(final LocalDateTime value) {
            return gt(ValueOperation.Comparison.DateTime::new, value);
        }

        default ThingVariable.Attribute gt(final UnboundVariable variable) {
            return gt(ValueOperation.Comparison.Variable::new, variable);
        }

        default <T> ThingVariable.Attribute gt(final BiFunction<GraqlToken.Comparator, T, ValueOperation.Comparison<T>> constructor, final T value) {
            return operation(constructor.apply(GraqlToken.Comparator.GT, value));
        }

        // Attribute value greater-than-or-equals constraint

        default ThingVariable.Attribute gte(final long value) {
            return gte(ValueOperation.Comparison.Long::new, value);
        }

        default ThingVariable.Attribute gte(final double value) {
            return gte(ValueOperation.Comparison.Double::new, value);
        }

        default ThingVariable.Attribute gte(final boolean value) {
            return gte(ValueOperation.Comparison.Boolean::new, value);
        }

        default ThingVariable.Attribute gte(final String value) {
            return gte(ValueOperation.Comparison.String::new, value);
        }

        default ThingVariable.Attribute gte(final LocalDateTime value) {
            return gte(ValueOperation.Comparison.DateTime::new, value);
        }

        default ThingVariable.Attribute gte(final UnboundVariable variable) {
            return gte(ValueOperation.Comparison.Variable::new, variable);
        }

        default <T> ThingVariable.Attribute gte(final BiFunction<GraqlToken.Comparator, T, ValueOperation.Comparison<T>> constructor, final T value) {
            return operation(constructor.apply(GraqlToken.Comparator.GTE, value));
        }

        // Attribute value less-than constraint

        default ThingVariable.Attribute lt(final long value) {
            return lt(ValueOperation.Comparison.Long::new, value);
        }

        default ThingVariable.Attribute lt(final double value) {
            return lt(ValueOperation.Comparison.Double::new, value);
        }

        default ThingVariable.Attribute lt(final boolean value) {
            return lt(ValueOperation.Comparison.Boolean::new, value);
        }

        default ThingVariable.Attribute lt(final String value) {
            return lt(ValueOperation.Comparison.String::new, value);
        }

        default ThingVariable.Attribute lt(final LocalDateTime value) {
            return lt(ValueOperation.Comparison.DateTime::new, value);
        }

        default ThingVariable.Attribute lt(final UnboundVariable variable) {
            return lt(ValueOperation.Comparison.Variable::new, variable);
        }

        default <T> ThingVariable.Attribute lt(final BiFunction<GraqlToken.Comparator, T, ValueOperation.Comparison<T>> constructor, final T value) {
            return operation(constructor.apply(GraqlToken.Comparator.LT, value));
        }

        // Attribute value less-than-or-equals constraint

        default ThingVariable.Attribute lte(final long value) {
            return lte(ValueOperation.Comparison.Long::new, value);
        }

        default ThingVariable.Attribute lte(final double value) {
            return lte(ValueOperation.Comparison.Double::new, value);
        }

        default ThingVariable.Attribute lte(final boolean value) {
            return lte(ValueOperation.Comparison.Boolean::new, value);
        }

        default ThingVariable.Attribute lte(final String value) {
            return lte(ValueOperation.Comparison.String::new, value);
        }

        default ThingVariable.Attribute lte(final LocalDateTime value) {
            return lte(ValueOperation.Comparison.DateTime::new, value);
        }

        default ThingVariable.Attribute lte(final UnboundVariable variable) {
            return lte(ValueOperation.Comparison.Variable::new, variable);
        }

        default <T> ThingVariable.Attribute lte(final BiFunction<GraqlToken.Comparator, T, ValueOperation.Comparison<T>> constructor, final T value) {
            return operation(constructor.apply(GraqlToken.Comparator.LTE, value));
        }

        // Attribute value contains (in String) constraint

        default ThingVariable.Attribute contains(final String value) {
            return contains(ValueOperation.Comparison.String::new, value);
        }

        default ThingVariable.Attribute contains(final UnboundVariable variable) {
            return contains(ValueOperation.Comparison.Variable::new, variable);
        }

        default <T> ThingVariable.Attribute contains(final BiFunction<GraqlToken.Comparator, T, ValueOperation.Comparison<T>> constructor, final T value) {
            return operation(constructor.apply(GraqlToken.Comparator.CONTAINS, value));
        }

        // Attribute value like (regex) constraint

        default ThingVariable.Attribute like(final String value) {
            return operation(new ValueOperation.Comparison.String(GraqlToken.Comparator.LIKE, value));
        }

        default ThingVariable.Attribute operation(final ValueOperation<?> operation) {
            return constrain(new ThingConstraint.Value<>(operation));
        }

        ThingVariable.Attribute constrain(ThingConstraint.Value<?> constraint);
    }
}
