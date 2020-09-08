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
import graql.lang.pattern.constraint.ValueConstraint;
import graql.lang.pattern.variable.ThingVariable;
import graql.lang.pattern.variable.UnboundVariable;

import java.time.LocalDateTime;
import java.util.function.BiFunction;

public interface ThingVariableBuilder {

    interface Common<T> {

        default T isa(GraqlToken.Type type) {
            return isa(type.toString());
        }

        default T isa(String type) {
            return asSameThingWith(new ThingConstraint.Isa(type, false));
        }

        default T isa(UnboundVariable var) {
            return asSameThingWith(new ThingConstraint.Isa(var, false));
        }

        default T isaX(GraqlToken.Type type) {
            return isa(type.toString());
        }

        default T isaX(String type) {
            return asSameThingWith(new ThingConstraint.Isa(type, true));
        }

        default T isaX(UnboundVariable var) {
            return asSameThingWith(new ThingConstraint.Isa(var, true));
        }

        default T has(String type, long value) {
            return has(type, new ThingConstraint.Value<>(new ValueConstraint.Assignment.Long(value)));
        }

        default T has(String type, double value) {
            return has(type, new ThingConstraint.Value<>(new ValueConstraint.Assignment.Double(value)));
        }

        default T has(String type, boolean value) {
            return has(type, new ThingConstraint.Value<>(new ValueConstraint.Assignment.Boolean(value)));
        }

        default T has(String type, String value) {
            return has(type, new ThingConstraint.Value<>(new ValueConstraint.Assignment.String(value)));
        }

        default T has(String type, LocalDateTime value) {
            return has(type, new ThingConstraint.Value<>(new ValueConstraint.Assignment.DateTime(value)));
        }

        default T has(String type, ThingConstraint.Value<?> value) {
            return asSameThingWith(new ThingConstraint.Has(type, value));
        }

        default T has(String type, UnboundVariable variable) {
            return asSameThingWith(new ThingConstraint.Has(type, variable));
        }

        T asSameThingWith(ThingConstraint.Singular constraint);

        T asSameThingWith(ThingConstraint.Repeatable constraint);
    }

    interface Thing {

        default ThingVariable.Thing iid(String iid) {
            return asThingWith(new ThingConstraint.IID(iid));
        }

        default ThingVariable.Thing not(String var) {
            return not(UnboundVariable.named(var));
        }

        default ThingVariable.Thing not(UnboundVariable var) {
            return asThingWith(new ThingConstraint.NEQ(var));
        }

        ThingVariable.Thing asThingWith(ThingConstraint.Singular constraint);
    }

    interface Relation {

        default ThingVariable.Relation rel(String playerVar) {
            return rel(UnboundVariable.named(playerVar));
        }

        default ThingVariable.Relation rel(UnboundVariable playerVar) {
            return asRelationWith(new ThingConstraint.Relation.RolePlayer(playerVar));
        }

        default ThingVariable.Relation rel(String roleType, String playerVar) {
            return asRelationWith(new ThingConstraint.Relation.RolePlayer(roleType, UnboundVariable.named(playerVar)));
        }

        default ThingVariable.Relation rel(String roleType, UnboundVariable playerVar) {
            return asRelationWith(new ThingConstraint.Relation.RolePlayer(roleType, playerVar));
        }

        default ThingVariable.Relation rel(UnboundVariable roleTypeVar, UnboundVariable playerVar) {
            return asRelationWith(new ThingConstraint.Relation.RolePlayer(roleTypeVar, playerVar));
        }

        ThingVariable.Relation asRelationWith(ThingConstraint.Relation.RolePlayer rolePlayer);
    }

    interface Attribute {

        // Attribute value assignment constraint

        default ThingVariable.Attribute val(long value) {
            return operation(new ValueConstraint.Assignment.Long(value));
        }

        default ThingVariable.Attribute val(double value) {
            return operation(new ValueConstraint.Assignment.Double(value));
        }

        default ThingVariable.Attribute val(boolean value) {
            return operation(new ValueConstraint.Assignment.Boolean(value));
        }

        default ThingVariable.Attribute val(String value) {
            return operation(new ValueConstraint.Assignment.String(value));
        }

        default ThingVariable.Attribute val(LocalDateTime value) {
            return operation(new ValueConstraint.Assignment.DateTime(value));
        }

        // Attribute value equality constraint

        default ThingVariable.Attribute eq(long value) {
            return eq(ValueConstraint.Comparison.Long::new, value);
        }

        default ThingVariable.Attribute eq(double value) {
            return eq(ValueConstraint.Comparison.Double::new, value);
        }

        default ThingVariable.Attribute eq(boolean value) {
            return eq(ValueConstraint.Comparison.Boolean::new, value);
        }

        default ThingVariable.Attribute eq(String value) {
            return eq(ValueConstraint.Comparison.String::new, value);
        }

        default ThingVariable.Attribute eq(LocalDateTime value) {
            return eq(ValueConstraint.Comparison.DateTime::new, value);
        }

        default ThingVariable.Attribute eq(UnboundVariable variable) {
            return eq(ValueConstraint.Comparison.Variable::new, variable);
        }

        default <T> ThingVariable.Attribute eq(BiFunction<GraqlToken.Comparator, T, ValueConstraint.Comparison<T>> constructor, T value) {
            return operation(constructor.apply(GraqlToken.Comparator.EQV, value));
        }

        // Attribute value inequality constraint

        default ThingVariable.Attribute neq(long value) {
            return neq(ValueConstraint.Comparison.Long::new, value);
        }

        default ThingVariable.Attribute neq(double value) {
            return neq(ValueConstraint.Comparison.Double::new, value);
        }

        default ThingVariable.Attribute neq(boolean value) {
            return neq(ValueConstraint.Comparison.Boolean::new, value);
        }

        default ThingVariable.Attribute neq(String value) {
            return neq(ValueConstraint.Comparison.String::new, value);
        }

        default ThingVariable.Attribute neq(LocalDateTime value) {
            return neq(ValueConstraint.Comparison.DateTime::new, value);
        }

        default ThingVariable.Attribute neq(UnboundVariable variable) {
            return neq(ValueConstraint.Comparison.Variable::new, variable);
        }

        default <T> ThingVariable.Attribute neq(BiFunction<GraqlToken.Comparator, T, ValueConstraint.Comparison<T>> constructor, T value) {
            return operation(constructor.apply(GraqlToken.Comparator.NEQV, value));
        }

        // Attribute value greater-than constraint

        default ThingVariable.Attribute gt(long value) {
            return gt(ValueConstraint.Comparison.Long::new, value);
        }

        default ThingVariable.Attribute gt(double value) {
            return gt(ValueConstraint.Comparison.Double::new, value);
        }

        default ThingVariable.Attribute gt(boolean value) {
            return gt(ValueConstraint.Comparison.Boolean::new, value);
        }

        default ThingVariable.Attribute gt(String value) {
            return gt(ValueConstraint.Comparison.String::new, value);
        }

        default ThingVariable.Attribute gt(LocalDateTime value) {
            return gt(ValueConstraint.Comparison.DateTime::new, value);
        }

        default ThingVariable.Attribute gt(UnboundVariable variable) {
            return gt(ValueConstraint.Comparison.Variable::new, variable);
        }

        default <T> ThingVariable.Attribute gt(BiFunction<GraqlToken.Comparator, T, ValueConstraint.Comparison<T>> constructor, T value) {
            return operation(constructor.apply(GraqlToken.Comparator.GT, value));
        }

        // Attribute value greater-than-or-equals constraint

        default ThingVariable.Attribute gte(long value) {
            return gte(ValueConstraint.Comparison.Long::new, value);
        }

        default ThingVariable.Attribute gte(double value) {
            return gte(ValueConstraint.Comparison.Double::new, value);
        }

        default ThingVariable.Attribute gte(boolean value) {
            return gte(ValueConstraint.Comparison.Boolean::new, value);
        }

        default ThingVariable.Attribute gte(String value) {
            return gte(ValueConstraint.Comparison.String::new, value);
        }

        default ThingVariable.Attribute gte(LocalDateTime value) {
            return gte(ValueConstraint.Comparison.DateTime::new, value);
        }

        default ThingVariable.Attribute gte(UnboundVariable variable) {
            return gte(ValueConstraint.Comparison.Variable::new, variable);
        }

        default <T> ThingVariable.Attribute gte(BiFunction<GraqlToken.Comparator, T, ValueConstraint.Comparison<T>> constructor, T value) {
            return operation(constructor.apply(GraqlToken.Comparator.GTE, value));
        }

        // Attribute value less-than constraint

        default ThingVariable.Attribute lt(long value) {
            return lt(ValueConstraint.Comparison.Long::new, value);
        }

        default ThingVariable.Attribute lt(double value) {
            return lt(ValueConstraint.Comparison.Double::new, value);
        }

        default ThingVariable.Attribute lt(boolean value) {
            return lt(ValueConstraint.Comparison.Boolean::new, value);
        }

        default ThingVariable.Attribute lt(String value) {
            return lt(ValueConstraint.Comparison.String::new, value);
        }

        default ThingVariable.Attribute lt(LocalDateTime value) {
            return lt(ValueConstraint.Comparison.DateTime::new, value);
        }

        default ThingVariable.Attribute lt(UnboundVariable variable) {
            return lt(ValueConstraint.Comparison.Variable::new, variable);
        }

        default <T> ThingVariable.Attribute lt(BiFunction<GraqlToken.Comparator, T, ValueConstraint.Comparison<T>> constructor, T value) {
            return operation(constructor.apply(GraqlToken.Comparator.LT, value));
        }

        // Attribute value less-than-or-equals constraint

        default ThingVariable.Attribute lte(long value) {
            return lte(ValueConstraint.Comparison.Long::new, value);
        }

        default ThingVariable.Attribute lte(double value) {
            return lte(ValueConstraint.Comparison.Double::new, value);
        }

        default ThingVariable.Attribute lte(boolean value) {
            return lte(ValueConstraint.Comparison.Boolean::new, value);
        }

        default ThingVariable.Attribute lte(String value) {
            return lte(ValueConstraint.Comparison.String::new, value);
        }

        default ThingVariable.Attribute lte(LocalDateTime value) {
            return lte(ValueConstraint.Comparison.DateTime::new, value);
        }

        default ThingVariable.Attribute lte(UnboundVariable variable) {
            return lte(ValueConstraint.Comparison.Variable::new, variable);
        }

        default <T> ThingVariable.Attribute lte(BiFunction<GraqlToken.Comparator, T, ValueConstraint.Comparison<T>> constructor, T value) {
            return operation(constructor.apply(GraqlToken.Comparator.LTE, value));
        }

        // Attribute value contains (in String) constraint

        default ThingVariable.Attribute contains(String value) {
            return contains(ValueConstraint.Comparison.String::new, value);
        }

        default ThingVariable.Attribute contains(UnboundVariable variable) {
            return contains(ValueConstraint.Comparison.Variable::new, variable);
        }

        default <T> ThingVariable.Attribute contains(BiFunction<GraqlToken.Comparator, T, ValueConstraint.Comparison<T>> constructor, T value) {
            return operation(constructor.apply(GraqlToken.Comparator.CONTAINS, value));
        }

        // Attribute value like (regex) constraint

        default ThingVariable.Attribute like(String value) {
            return operation(new ValueConstraint.Comparison.String(GraqlToken.Comparator.LIKE, value));
        }

        default ThingVariable.Attribute operation(ValueConstraint<?> operation) {
            return asAttributeWith(new ThingConstraint.Value<>(operation));
        }

        ThingVariable.Attribute asAttributeWith(ThingConstraint.Value<?> constraint);
    }
}
