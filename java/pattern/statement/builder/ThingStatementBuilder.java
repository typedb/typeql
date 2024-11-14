/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.typeql.lang.pattern.statement.builder;

import com.typeql.lang.common.TypeQLToken;
import com.typeql.lang.common.TypeQLVariable;
import com.typeql.lang.pattern.constraint.Predicate;
import com.typeql.lang.pattern.constraint.ThingConstraint;
import com.typeql.lang.pattern.statement.ThingStatement;

import java.time.LocalDateTime;

import static com.typeql.lang.common.TypeQLToken.Predicate.Equality.EQ;

public interface ThingStatementBuilder {

    interface Common<T> {

        default T isa(TypeQLToken.Type type) {
            return isa(type.toString());
        }

        default T isa(String type) {
            return constrain(new ThingConstraint.Isa(type, false));
        }

        default T isa(TypeQLVariable.Concept var) {
            return constrain(new ThingConstraint.Isa(var, false));
        }

        default T isaX(TypeQLToken.Type type) {
            return isa(type.toString());
        }

        default T isaX(String type) {
            return constrain(new ThingConstraint.Isa(type, true));
        }

        default T isaX(TypeQLVariable.Concept var) {
            return constrain(new ThingConstraint.Isa(var, true));
        }

        default T has(String type, long value) {
            return has(type, new ThingConstraint.Predicate(new Predicate.Long(EQ, value)));
        }

        default T has(String type, double value) {
            return has(type, new ThingConstraint.Predicate(new Predicate.Double(EQ, value)));
        }

        default T has(String type, boolean value) {
            return has(type, new ThingConstraint.Predicate(new Predicate.Boolean(EQ, value)));
        }

        default T has(String type, String value) {
            return has(type, new ThingConstraint.Predicate(new Predicate.String(EQ, value)));
        }

        default T has(String type, LocalDateTime value) {
            return has(type, new ThingConstraint.Predicate(new Predicate.DateTime(EQ, value)));
        }

        default T has(String type, TypeQLVariable.Value value) {
            return has(type, new ThingConstraint.Predicate(new Predicate.Variable(EQ, value)));
        }

        default T has(String type, ThingConstraint.Predicate predicate) {
            return constrain(new ThingConstraint.Has(type, predicate));
        }

        default T has(String type, TypeQLVariable.Concept variable) {
            return constrain(new ThingConstraint.Has(type, variable));
        }

        default T has(TypeQLVariable.Concept variable) {
            return constrain(new ThingConstraint.Has(variable));
        }

        T constrain(ThingConstraint.Isa constraint);

        T constrain(ThingConstraint.Has constraint);
    }

    interface Thing {

        default ThingStatement.Thing iid(String iid) {
            return constrain(new ThingConstraint.IID(iid));
        }

        ThingStatement.Thing constrain(ThingConstraint.IID constraint);
    }

    interface Relation {

        default ThingStatement.Relation rel(TypeQLVariable.Concept playerVar) {
            return constrain(new ThingConstraint.Relation.RolePlayer(playerVar));
        }

        default ThingStatement.Relation rel(String roleType, TypeQLVariable.Concept playerVar) {
            return constrain(new ThingConstraint.Relation.RolePlayer(roleType, playerVar));
        }

        default ThingStatement.Relation rel(TypeQLVariable.Concept roleTypeVar, TypeQLVariable.Concept playerVar) {
            return constrain(new ThingConstraint.Relation.RolePlayer(roleTypeVar, playerVar));
        }

        ThingStatement.Relation constrain(ThingConstraint.Relation.RolePlayer rolePlayer);
    }

    interface Attribute extends PredicateBuilder<ThingStatement.Attribute> {

        ThingStatement.Attribute constrain(ThingConstraint.Predicate constraint);
    }
}
