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
import graql.lang.pattern.property.ThingProperty;
import graql.lang.pattern.property.ValueOperation;
import graql.lang.pattern.variable.ThingBoundVariable;
import graql.lang.pattern.variable.UnboundVariable;

import java.time.LocalDateTime;
import java.util.function.BiFunction;

public interface ThingVariableBuilder {

    interface Common<T> {

        default T isa(GraqlToken.Type type) {
            return isa(type.toString());
        }

        default T isa(String type) {
            return asSameThingWith(new ThingProperty.Isa(type, false));
        }

        default T isa(UnboundVariable var) {
            return asSameThingWith(new ThingProperty.Isa(var, false));
        }

        default T isaX(GraqlToken.Type type) {
            return isa(type.toString());
        }

        default T isaX(String type) {
            return asSameThingWith(new ThingProperty.Isa(type, true));
        }

        default T isaX(UnboundVariable var) {
            return asSameThingWith(new ThingProperty.Isa(var, true));
        }

        default T has(String type, long value) {
            return has(type, new ThingProperty.Value<>(new ValueOperation.Assignment.Long(value)));
        }

        default T has(String type, double value) {
            return has(type, new ThingProperty.Value<>(new ValueOperation.Assignment.Double(value)));
        }

        default T has(String type, boolean value) {
            return has(type, new ThingProperty.Value<>(new ValueOperation.Assignment.Boolean(value)));
        }

        default T has(String type, String value) {
            return has(type, new ThingProperty.Value<>(new ValueOperation.Assignment.String(value)));
        }

        default T has(String type, LocalDateTime value) {
            return has(type, new ThingProperty.Value<>(new ValueOperation.Assignment.DateTime(value)));
        }

        default T has(String type, ThingProperty.Value<?> value) {
            return asSameThingWith(new ThingProperty.Has(type, value));
        }

        default T has(String type, UnboundVariable variable) {
            return asSameThingWith(new ThingProperty.Has(type, variable));
        }

        T asSameThingWith(ThingProperty.Singular property);

        T asSameThingWith(ThingProperty.Repeatable property);
    }

    interface Thing {

        default ThingBoundVariable.Thing iid(String iid) {
            return asThingWith(new ThingProperty.IID(iid));
        }

        default ThingBoundVariable.Thing not(String var) {
            return not(UnboundVariable.named(var));
        }

        default ThingBoundVariable.Thing not(UnboundVariable var) {
            return asThingWith(new ThingProperty.NEQ(var));
        }

        ThingBoundVariable.Thing asThingWith(ThingProperty.Singular property);
    }

    interface Relation {

        default ThingBoundVariable.Relation rel(String playerVar) {
            return rel(UnboundVariable.named(playerVar));
        }

        default ThingBoundVariable.Relation rel(UnboundVariable playerVar) {
            return asRelationWith(new ThingProperty.Relation.RolePlayer(playerVar));
        }

        default ThingBoundVariable.Relation rel(String roleType, String playerVar) {
            return asRelationWith(new ThingProperty.Relation.RolePlayer(roleType, UnboundVariable.named(playerVar)));
        }

        default ThingBoundVariable.Relation rel(String roleType, UnboundVariable playerVar) {
            return asRelationWith(new ThingProperty.Relation.RolePlayer(roleType, playerVar));
        }

        default ThingBoundVariable.Relation rel(UnboundVariable roleTypeVar, UnboundVariable playerVar) {
            return asRelationWith(new ThingProperty.Relation.RolePlayer(roleTypeVar, playerVar));
        }

        ThingBoundVariable.Relation asRelationWith(ThingProperty.Relation.RolePlayer rolePlayer);
    }

    interface Attribute {

        // Attribute value assignment property

        default ThingBoundVariable.Attribute val(long value) {
            return operation(new ValueOperation.Assignment.Long(value));
        }

        default ThingBoundVariable.Attribute val(double value) {
            return operation(new ValueOperation.Assignment.Double(value));
        }

        default ThingBoundVariable.Attribute val(boolean value) {
            return operation(new ValueOperation.Assignment.Boolean(value));
        }

        default ThingBoundVariable.Attribute val(String value) {
            return operation(new ValueOperation.Assignment.String(value));
        }

        default ThingBoundVariable.Attribute val(LocalDateTime value) {
            return operation(new ValueOperation.Assignment.DateTime(value));
        }

        // Attribute value equality property

        default ThingBoundVariable.Attribute eq(long value) {
            return eq(ValueOperation.Comparison.Long::new, value);
        }

        default ThingBoundVariable.Attribute eq(double value) {
            return eq(ValueOperation.Comparison.Double::new, value);
        }

        default ThingBoundVariable.Attribute eq(boolean value) {
            return eq(ValueOperation.Comparison.Boolean::new, value);
        }

        default ThingBoundVariable.Attribute eq(String value) {
            return eq(ValueOperation.Comparison.String::new, value);
        }

        default ThingBoundVariable.Attribute eq(LocalDateTime value) {
            return eq(ValueOperation.Comparison.DateTime::new, value);
        }

        default ThingBoundVariable.Attribute eq(UnboundVariable variable) {
            return eq(ValueOperation.Comparison.Variable::new, variable);
        }

        default <T> ThingBoundVariable.Attribute eq(BiFunction<GraqlToken.Comparator, T, ValueOperation.Comparison<T>> constructor, T value) {
            return operation(constructor.apply(GraqlToken.Comparator.EQV, value));
        }

        // Attribute value inequality property

        default ThingBoundVariable.Attribute neq(long value) {
            return neq(ValueOperation.Comparison.Long::new, value);
        }

        default ThingBoundVariable.Attribute neq(double value) {
            return neq(ValueOperation.Comparison.Double::new, value);
        }

        default ThingBoundVariable.Attribute neq(boolean value) {
            return neq(ValueOperation.Comparison.Boolean::new, value);
        }

        default ThingBoundVariable.Attribute neq(String value) {
            return neq(ValueOperation.Comparison.String::new, value);
        }

        default ThingBoundVariable.Attribute neq(LocalDateTime value) {
            return neq(ValueOperation.Comparison.DateTime::new, value);
        }

        default ThingBoundVariable.Attribute neq(UnboundVariable variable) {
            return neq(ValueOperation.Comparison.Variable::new, variable);
        }

        default <T> ThingBoundVariable.Attribute neq(BiFunction<GraqlToken.Comparator, T, ValueOperation.Comparison<T>> constructor, T value) {
            return operation(constructor.apply(GraqlToken.Comparator.NEQV, value));
        }

        // Attribute value greater-than property

        default ThingBoundVariable.Attribute gt(long value) {
            return gt(ValueOperation.Comparison.Long::new, value);
        }

        default ThingBoundVariable.Attribute gt(double value) {
            return gt(ValueOperation.Comparison.Double::new, value);
        }

        default ThingBoundVariable.Attribute gt(boolean value) {
            return gt(ValueOperation.Comparison.Boolean::new, value);
        }

        default ThingBoundVariable.Attribute gt(String value) {
            return gt(ValueOperation.Comparison.String::new, value);
        }

        default ThingBoundVariable.Attribute gt(LocalDateTime value) {
            return gt(ValueOperation.Comparison.DateTime::new, value);
        }

        default ThingBoundVariable.Attribute gt(UnboundVariable variable) {
            return gt(ValueOperation.Comparison.Variable::new, variable);
        }

        default <T> ThingBoundVariable.Attribute gt(BiFunction<GraqlToken.Comparator, T, ValueOperation.Comparison<T>> constructor, T value) {
            return operation(constructor.apply(GraqlToken.Comparator.GT, value));
        }

        // Attribute value greater-than-or-equals property

        default ThingBoundVariable.Attribute gte(long value) {
            return gte(ValueOperation.Comparison.Long::new, value);
        }

        default ThingBoundVariable.Attribute gte(double value) {
            return gte(ValueOperation.Comparison.Double::new, value);
        }

        default ThingBoundVariable.Attribute gte(boolean value) {
            return gte(ValueOperation.Comparison.Boolean::new, value);
        }

        default ThingBoundVariable.Attribute gte(String value) {
            return gte(ValueOperation.Comparison.String::new, value);
        }

        default ThingBoundVariable.Attribute gte(LocalDateTime value) {
            return gte(ValueOperation.Comparison.DateTime::new, value);
        }

        default ThingBoundVariable.Attribute gte(UnboundVariable variable) {
            return gte(ValueOperation.Comparison.Variable::new, variable);
        }

        default <T> ThingBoundVariable.Attribute gte(BiFunction<GraqlToken.Comparator, T, ValueOperation.Comparison<T>> constructor, T value) {
            return operation(constructor.apply(GraqlToken.Comparator.GTE, value));
        }

        // Attribute value less-than property

        default ThingBoundVariable.Attribute lt(long value) {
            return lt(ValueOperation.Comparison.Long::new, value);
        }

        default ThingBoundVariable.Attribute lt(double value) {
            return lt(ValueOperation.Comparison.Double::new, value);
        }

        default ThingBoundVariable.Attribute lt(boolean value) {
            return lt(ValueOperation.Comparison.Boolean::new, value);
        }

        default ThingBoundVariable.Attribute lt(String value) {
            return lt(ValueOperation.Comparison.String::new, value);
        }

        default ThingBoundVariable.Attribute lt(LocalDateTime value) {
            return lt(ValueOperation.Comparison.DateTime::new, value);
        }

        default ThingBoundVariable.Attribute lt(UnboundVariable variable) {
            return lt(ValueOperation.Comparison.Variable::new, variable);
        }

        default <T> ThingBoundVariable.Attribute lt(BiFunction<GraqlToken.Comparator, T, ValueOperation.Comparison<T>> constructor, T value) {
            return operation(constructor.apply(GraqlToken.Comparator.LT, value));
        }

        // Attribute value less-than-or-equals property

        default ThingBoundVariable.Attribute lte(long value) {
            return lte(ValueOperation.Comparison.Long::new, value);
        }

        default ThingBoundVariable.Attribute lte(double value) {
            return lte(ValueOperation.Comparison.Double::new, value);
        }

        default ThingBoundVariable.Attribute lte(boolean value) {
            return lte(ValueOperation.Comparison.Boolean::new, value);
        }

        default ThingBoundVariable.Attribute lte(String value) {
            return lte(ValueOperation.Comparison.String::new, value);
        }

        default ThingBoundVariable.Attribute lte(LocalDateTime value) {
            return lte(ValueOperation.Comparison.DateTime::new, value);
        }

        default ThingBoundVariable.Attribute lte(UnboundVariable variable) {
            return lte(ValueOperation.Comparison.Variable::new, variable);
        }

        default <T> ThingBoundVariable.Attribute lte(BiFunction<GraqlToken.Comparator, T, ValueOperation.Comparison<T>> constructor, T value) {
            return operation(constructor.apply(GraqlToken.Comparator.LTE, value));
        }

        // Attribute value contains (in String) property

        default ThingBoundVariable.Attribute contains(String value) {
            return contains(ValueOperation.Comparison.String::new, value);
        }

        default ThingBoundVariable.Attribute contains(UnboundVariable variable) {
            return contains(ValueOperation.Comparison.Variable::new, variable);
        }

        default <T> ThingBoundVariable.Attribute contains(BiFunction<GraqlToken.Comparator, T, ValueOperation.Comparison<T>> constructor, T value) {
            return operation(constructor.apply(GraqlToken.Comparator.CONTAINS, value));
        }

        // Attribute value like (regex) property

        default ThingBoundVariable.Attribute like(String value) {
            return operation(new ValueOperation.Comparison.String(GraqlToken.Comparator.LIKE, value));
        }

        default ThingBoundVariable.Attribute operation(ValueOperation<?> operation) {
            return asAttributeWith(new ThingProperty.Value<>(operation));
        }

        ThingBoundVariable.Attribute asAttributeWith(ThingProperty.Value<?> property);
    }
}
