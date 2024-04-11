/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typeql.lang.builder;

import com.vaticle.typedb.common.collection.Pair;
import com.vaticle.typeql.lang.common.Reference;
import com.vaticle.typeql.lang.common.TypeQLVariable;
import com.vaticle.typeql.lang.pattern.constraint.Predicate;
import com.vaticle.typeql.lang.pattern.constraint.ValueConstraint;
import com.vaticle.typeql.lang.pattern.expression.Expression;
import com.vaticle.typeql.lang.pattern.statement.ValueStatement;
import com.vaticle.typeql.lang.pattern.statement.builder.ValueStatementBuilder;
import com.vaticle.typeql.lang.query.TypeQLFetch;

import java.util.List;
import java.util.Set;

import static com.vaticle.typedb.common.collection.Collections.list;

public class ValueVariableBuilder extends TypeQLVariable.Value implements
        ValueStatementBuilder,
        Expression,
        TypeQLFetch.Key.Var.UnlabelledVar {

    ValueVariableBuilder(Reference.Name.Value reference) {
        super(reference);
    }

    public static ValueVariableBuilder named(String name) {
        return new ValueVariableBuilder(Reference.value(name));
    }

    @Override
    public boolean isValueVar() {
        return true;
    }

    @Override
    public ValueVariableBuilder asValueVar() {
        return this;
    }

    public ValueStatement toStatement() {
        return ValueStatement.of(this);
    }

    public ValueStatement constrain(Predicate<?> predicate) {
        return constrain(new ValueConstraint.Predicate(predicate));
    }

    public ValueStatement constrain(ValueConstraint.Predicate constraint) {
        return toStatement().constrain(constraint);
    }

    public ValueStatement constrain(ValueConstraint.Assignment constraint) {
        return toStatement().constrain(constraint);
    }

    public void collectVariables(Set<TypeQLVariable> collector) {
        collector.add(this);
    }

    @Override
    public LabelledVar asLabel(TypeQLFetch.Key.Label label) {
        return new LabelledVar(this, label);
    }

    @Override
    public Attribute fetch(Pair<Reference.Label, TypeQLFetch.Key.Label> attribute) {
        return new Attribute(this, list(attribute));
    }

    @Override
    public Attribute fetch(List<Pair<Reference.Label, Label>> attributes) {
        return new Attribute(this, attributes);
    }
}
