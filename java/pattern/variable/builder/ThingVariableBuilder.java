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
import graql.lang.pattern.variable.ThingVariable;
import graql.lang.pattern.variable.UnscopedVariable;

import java.time.LocalDateTime;
import java.util.function.BiFunction;

import static graql.lang.pattern.variable.UnscopedVariable.hidden;

public interface ThingVariableBuilder<T> {

    default T isa(GraqlToken.Type type) {
        return isa(type.toString());
    }

    default T isa(String type) {
        return asSameThingWith(new ThingProperty.Isa(type, false));
    }

    default T isa(UnscopedVariable var) {
        return asSameThingWith(new ThingProperty.Isa(var, false));
    }

    default T isaX(GraqlToken.Type type) {
        return isa(type.toString());
    }

    default T isaX(String type) {
        return asSameThingWith(new ThingProperty.Isa(type, true));
    }

    default T isaX(UnscopedVariable var) {
        return asSameThingWith(new ThingProperty.Isa(var, true));
    }

    default T has(String type, long value) {
        return has(type, hidden().val(value));
    }

    default T has(String type, double value) {
        return has(type, hidden().val(value));
    }

    default T has(String type, boolean value) {
        return has(type, hidden().val(value));
    }

    default T has(String type, String value) {
        return has(type, hidden().val(value));
    }

    default T has(String type, LocalDateTime value) {
        return has(type, hidden().val(value));
    }

    default T has(String type, UnscopedVariable variable) {
        return asSameThingWith(new ThingProperty.Has(type, variable));
    }

    default T has(String type, ThingVariable.Attribute variable) {
        return asSameThingWith(new ThingProperty.Has(type, variable));
    }

    T asSameThingWith(ThingProperty.Singular property);

    T asSameThingWith(ThingProperty.Repeatable property);

    interface Thing {

        default ThingVariable.Thing iid(String iid) {
            return asThingWith(new ThingProperty.ID(iid));
        }

        default ThingVariable.Thing not(String var) {
            return not(UnscopedVariable.named(var));
        }

        default ThingVariable.Thing not(UnscopedVariable var) {
            return asThingWith(new ThingProperty.NEQ(var));
        }

        ThingVariable.Thing asThingWith(ThingProperty.Singular property);
    }

    interface Relation {

        default ThingVariable.Relation rel(String playerVar) {
            return rel(UnscopedVariable.named(playerVar));
        }

        default ThingVariable.Relation rel(UnscopedVariable playerVar) {
            return asRelationWith(new ThingProperty.Relation.RolePlayer(playerVar));
        }

        default ThingVariable.Relation rel(String roleType, String playerVar) {
            return asRelationWith(new ThingProperty.Relation.RolePlayer(roleType, UnscopedVariable.named(playerVar)));
        }

        default ThingVariable.Relation rel(String roleType, UnscopedVariable playerVar) {
            return asRelationWith(new ThingProperty.Relation.RolePlayer(roleType, playerVar));
        }

        default ThingVariable.Relation rel(UnscopedVariable roleTypeVar, UnscopedVariable playerVar) {
            return asRelationWith(new ThingProperty.Relation.RolePlayer(roleTypeVar, playerVar));
        }

        ThingVariable.Relation asRelationWith(ThingProperty.Relation.RolePlayer rolePlayer);
    }

    interface Attribute {

        // Attribute value assignment property

        default ThingVariable.Attribute val(long value) {
            return operation(new ThingProperty.Value.Operation.Assignment.Number<>(value));
        }

        default ThingVariable.Attribute val(double value) {
            return operation(new ThingProperty.Value.Operation.Assignment.Number<>(value));
        }

        default ThingVariable.Attribute val(boolean value) {
            return operation(new ThingProperty.Value.Operation.Assignment.Boolean(value));
        }

        default ThingVariable.Attribute val(String value) {
            return operation(new ThingProperty.Value.Operation.Assignment.String(value));
        }

        default ThingVariable.Attribute val(LocalDateTime value) {
            return operation(new ThingProperty.Value.Operation.Assignment.DateTime(value));
        }

        // Attribute value equality property

        default ThingVariable.Attribute eq(long value) {
            return eq(ThingProperty.Value.Operation.Comparison.Number::new, value);
        }

        default ThingVariable.Attribute eq(double value) {
            return eq(ThingProperty.Value.Operation.Comparison.Number::new, value);
        }

        default ThingVariable.Attribute eq(boolean value) {
            return eq(ThingProperty.Value.Operation.Comparison.Boolean::new, value);
        }

        default ThingVariable.Attribute eq(String value) {
            return eq(ThingProperty.Value.Operation.Comparison.String::new, value);
        }

        default ThingVariable.Attribute eq(LocalDateTime value) {
            return eq(ThingProperty.Value.Operation.Comparison.DateTime::new, value);
        }

        default ThingVariable.Attribute eq(UnscopedVariable variable) {
            return eq(ThingProperty.Value.Operation.Comparison.Variable::new, variable);
        }

        default <T> ThingVariable.Attribute eq(BiFunction<GraqlToken.Comparator, T, ThingProperty.Value.Operation.Comparison<T>> constructor, T value) {
            return operation(constructor.apply(GraqlToken.Comparator.EQV, value));
        }

        // Attribute value inequality property

        default ThingVariable.Attribute neq(long value) {
            return neq(ThingProperty.Value.Operation.Comparison.Number::new, value);
        }

        default ThingVariable.Attribute neq(double value) {
            return neq(ThingProperty.Value.Operation.Comparison.Number::new, value);
        }

        default ThingVariable.Attribute neq(boolean value) {
            return neq(ThingProperty.Value.Operation.Comparison.Boolean::new, value);
        }

        default ThingVariable.Attribute neq(String value) {
            return neq(ThingProperty.Value.Operation.Comparison.String::new, value);
        }

        default ThingVariable.Attribute neq(LocalDateTime value) {
            return neq(ThingProperty.Value.Operation.Comparison.DateTime::new, value);
        }

        default ThingVariable.Attribute neq(UnscopedVariable variable) {
            return neq(ThingProperty.Value.Operation.Comparison.Variable::new, variable);
        }

        default <T> ThingVariable.Attribute neq(BiFunction<GraqlToken.Comparator, T, ThingProperty.Value.Operation.Comparison<T>> constructor, T value) {
            return operation(constructor.apply(GraqlToken.Comparator.NEQV, value));
        }

        // Attribute value greater-than property

        default ThingVariable.Attribute gt(long value) {
            return gt(ThingProperty.Value.Operation.Comparison.Number::new, value);
        }

        default ThingVariable.Attribute gt(double value) {
            return gt(ThingProperty.Value.Operation.Comparison.Number::new, value);
        }

        default ThingVariable.Attribute gt(boolean value) {
            return gt(ThingProperty.Value.Operation.Comparison.Boolean::new, value);
        }

        default ThingVariable.Attribute gt(String value) {
            return gt(ThingProperty.Value.Operation.Comparison.String::new, value);
        }

        default ThingVariable.Attribute gt(LocalDateTime value) {
            return gt(ThingProperty.Value.Operation.Comparison.DateTime::new, value);
        }

        default ThingVariable.Attribute gt(UnscopedVariable variable) {
            return gt(ThingProperty.Value.Operation.Comparison.Variable::new, variable);
        }

        default <T> ThingVariable.Attribute gt(BiFunction<GraqlToken.Comparator, T, ThingProperty.Value.Operation.Comparison<T>> constructor, T value) {
            return operation(constructor.apply(GraqlToken.Comparator.GT, value));
        }

        // Attribute value greater-than-or-equals property

        default ThingVariable.Attribute gte(long value) {
            return gte(ThingProperty.Value.Operation.Comparison.Number::new, value);
        }

        default ThingVariable.Attribute gte(double value) {
            return gte(ThingProperty.Value.Operation.Comparison.Number::new, value);
        }

        default ThingVariable.Attribute gte(boolean value) {
            return gte(ThingProperty.Value.Operation.Comparison.Boolean::new, value);
        }

        default ThingVariable.Attribute gte(String value) {
            return gte(ThingProperty.Value.Operation.Comparison.String::new, value);
        }

        default ThingVariable.Attribute gte(LocalDateTime value) {
            return gte(ThingProperty.Value.Operation.Comparison.DateTime::new, value);
        }

        default ThingVariable.Attribute gte(UnscopedVariable variable) {
            return gte(ThingProperty.Value.Operation.Comparison.Variable::new, variable);
        }

        default <T> ThingVariable.Attribute gte(BiFunction<GraqlToken.Comparator, T, ThingProperty.Value.Operation.Comparison<T>> constructor, T value) {
            return operation(constructor.apply(GraqlToken.Comparator.GTE, value));
        }

        // Attribute value less-than property

        default ThingVariable.Attribute lt(long value) {
            return lt(ThingProperty.Value.Operation.Comparison.Number::new, value);
        }

        default ThingVariable.Attribute lt(double value) {
            return lt(ThingProperty.Value.Operation.Comparison.Number::new, value);
        }

        default ThingVariable.Attribute lt(boolean value) {
            return lt(ThingProperty.Value.Operation.Comparison.Boolean::new, value);
        }

        default ThingVariable.Attribute lt(String value) {
            return lt(ThingProperty.Value.Operation.Comparison.String::new, value);
        }

        default ThingVariable.Attribute lt(LocalDateTime value) {
            return lt(ThingProperty.Value.Operation.Comparison.DateTime::new, value);
        }

        default ThingVariable.Attribute lt(UnscopedVariable variable) {
            return lt(ThingProperty.Value.Operation.Comparison.Variable::new, variable);
        }

        default <T> ThingVariable.Attribute lt(BiFunction<GraqlToken.Comparator, T, ThingProperty.Value.Operation.Comparison<T>> constructor, T value) {
            return operation(constructor.apply(GraqlToken.Comparator.LT, value));
        }

        // Attribute value less-than-or-equals property

        default ThingVariable.Attribute lte(long value) {
            return lte(ThingProperty.Value.Operation.Comparison.Number::new, value);
        }

        default ThingVariable.Attribute lte(double value) {
            return lte(ThingProperty.Value.Operation.Comparison.Number::new, value);
        }

        default ThingVariable.Attribute lte(boolean value) {
            return lte(ThingProperty.Value.Operation.Comparison.Boolean::new, value);
        }

        default ThingVariable.Attribute lte(String value) {
            return lte(ThingProperty.Value.Operation.Comparison.String::new, value);
        }

        default ThingVariable.Attribute lte(LocalDateTime value) {
            return lte(ThingProperty.Value.Operation.Comparison.DateTime::new, value);
        }

        default ThingVariable.Attribute lte(UnscopedVariable variable) {
            return lte(ThingProperty.Value.Operation.Comparison.Variable::new, variable);
        }

        default <T> ThingVariable.Attribute lte(BiFunction<GraqlToken.Comparator, T, ThingProperty.Value.Operation.Comparison<T>> constructor, T value) {
            return operation(constructor.apply(GraqlToken.Comparator.LTE, value));
        }

        // Attribute value contains (in String) property

        default ThingVariable.Attribute contains(String value) {
            return contains(ThingProperty.Value.Operation.Comparison.String::new, value);
        }

        default ThingVariable.Attribute contains(UnscopedVariable variable) {
            return contains(ThingProperty.Value.Operation.Comparison.Variable::new, variable);
        }

        default <T> ThingVariable.Attribute contains(BiFunction<GraqlToken.Comparator, T, ThingProperty.Value.Operation.Comparison<T>> constructor, T value) {
            return operation(constructor.apply(GraqlToken.Comparator.CONTAINS, value));
        }

        // Attribute value like (regex) property

        default ThingVariable.Attribute like(String value) {
            return operation(new ThingProperty.Value.Operation.Comparison.String(GraqlToken.Comparator.LIKE, value));
        }

        default ThingVariable.Attribute operation(ThingProperty.Value.Operation<?> operation) {
            return asAttributeWith(new ThingProperty.Value<>(operation));
        }

        ThingVariable.Attribute asAttributeWith(ThingProperty.Value<?> property);
    }
}
