/*
 * Copyright (C) 2022 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.vaticle.typeql.lang.pattern.variable.builder;

import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.pattern.constraint.ThingConstraint;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;

import java.time.LocalDateTime;
import java.util.function.BiFunction;

import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.EQ;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.GT;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.GTE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.LT;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.LTE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.NEQ;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.SubString.CONTAINS;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.SubString.LIKE;

public interface ThingVariableBuilder {

    interface Common<T> {

        default T isa(TypeQLToken.Type type) {
            return isa(type.toString());
        }

        default T isa(String type) {
            return constrain(new ThingConstraint.Isa(type, false));
        }

        default T isa(UnboundVariable var) {
            return constrain(new ThingConstraint.Isa(var, false));
        }

        default T isaX(TypeQLToken.Type type) {
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

        default <T> ThingVariable.Attribute eq(BiFunction<TypeQLToken.Predicate.Equality, T, ThingConstraint.Value<T>> constructor, T value) {
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

        default <T> ThingVariable.Attribute neq(BiFunction<TypeQLToken.Predicate.Equality, T, ThingConstraint.Value<T>> constructor, T value) {
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

        default <T> ThingVariable.Attribute gt(BiFunction<TypeQLToken.Predicate.Equality, T, ThingConstraint.Value<T>> constructor, T value) {
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

        default <T> ThingVariable.Attribute gte(BiFunction<TypeQLToken.Predicate.Equality, T, ThingConstraint.Value<T>> constructor, T value) {
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

        default <T> ThingVariable.Attribute lt(BiFunction<TypeQLToken.Predicate.Equality, T, ThingConstraint.Value<T>> constructor, T value) {
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

        default <T> ThingVariable.Attribute lte(BiFunction<TypeQLToken.Predicate.Equality, T, ThingConstraint.Value<T>> constructor, T value) {
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
