/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.typeql.lang.builder;

import com.vaticle.typedb.common.collection.Pair;
import com.typeql.lang.common.Reference;
import com.typeql.lang.common.TypeQLVariable;
import com.typeql.lang.pattern.constraint.ConceptConstraint;
import com.typeql.lang.pattern.constraint.Predicate;
import com.typeql.lang.pattern.constraint.ThingConstraint;
import com.typeql.lang.pattern.constraint.TypeConstraint;
import com.typeql.lang.pattern.expression.Expression;
import com.typeql.lang.pattern.statement.ConceptStatement;
import com.typeql.lang.pattern.statement.ThingStatement;
import com.typeql.lang.pattern.statement.TypeStatement;
import com.typeql.lang.pattern.statement.builder.ConceptStatementBuilder;
import com.typeql.lang.pattern.statement.builder.ThingStatementBuilder;
import com.typeql.lang.pattern.statement.builder.TypeStatementBuilder;
import com.typeql.lang.query.TypeQLFetch;

import java.util.List;
import java.util.Set;

import static com.vaticle.typedb.common.collection.Collections.list;

public class ConceptVariableBuilder extends TypeQLVariable.Concept implements
        ConceptStatementBuilder,
        TypeStatementBuilder,
        ThingStatementBuilder.Common<ThingStatement.Thing>,
        ThingStatementBuilder.Thing,
        ThingStatementBuilder.Relation,
        ThingStatementBuilder.Attribute,
        Expression,
        TypeQLFetch.Key.Var.UnlabelledVar {

    private ConceptVariableBuilder(Reference reference) {
        super(reference);
    }

    public static ConceptVariableBuilder named(String name) {
        return new ConceptVariableBuilder(Reference.concept(name));
    }

    public static ConceptVariableBuilder anonymous() {
        return new ConceptVariableBuilder(Reference.anonymous(true));
    }

    public static ConceptVariableBuilder hidden() {
        return new ConceptVariableBuilder(Reference.anonymous(false));
    }

    public static TypeStatement label(String label) {
        return new ConceptVariableBuilder(Reference.label(label)).toTypeStatement();
    }

    public static TypeStatement label(String label, String scope) {
        return new ConceptVariableBuilder(Reference.label(label, scope)).toTypeStatement();
    }

    @Override
    public boolean isConceptVar() {
        return true;
    }

    @Override
    public ConceptVariableBuilder asConceptVar() {
        return this;
    }

    public ConceptStatement toConceptStatement() {
        return ConceptStatement.of(this);
    }

    public TypeStatement toTypeStatement() {
        return TypeStatement.of(this);
    }

    public ThingStatement.Thing toThingStatement() {
        return ThingStatement.Thing.of(this);
    }

    @Override
    public void collectVariables(Set<TypeQLVariable> collector) {
        collector.add(this);
    }

    @Override
    public TypeStatement constrain(TypeConstraint.Label constraint) {
        TypeQLVariable.Concept variable;
        if (reference.isAnonymous())
            variable = TypeQLVariable.Concept.labelVar(constraint.label(), constraint.scope().orElse(null));
        else variable = this;
        return TypeStatement.of(variable).constrain(constraint);
    }

    @Override
    public TypeStatement constrain(TypeConstraint.Sub constraint) {
        return toTypeStatement().constrain(constraint);
    }

    @Override
    public TypeStatement constrain(TypeConstraint.Abstract constraint) {
        return toTypeStatement().constrain(constraint);
    }

    @Override
    public TypeStatement constrain(TypeConstraint.ValueType constraint) {
        return toTypeStatement().constrain(constraint);
    }

    @Override
    public TypeStatement constrain(TypeConstraint.Regex constraint) {
        return toTypeStatement().constrain(constraint);
    }

    @Override
    public TypeStatement constrain(TypeConstraint.Owns constraint) {
        return toTypeStatement().constrain(constraint);
    }

    @Override
    public TypeStatement constrain(TypeConstraint.Plays constraint) {
        return toTypeStatement().constrain(constraint);
    }

    @Override
    public TypeStatement constrain(TypeConstraint.Relates constraint) {
        return toTypeStatement().constrain(constraint);
    }

    @Override
    public ThingStatement.Thing constrain(ThingConstraint.Isa constraint) {
        return ThingStatement.Thing.of(this).constrain(constraint);
    }

    @Override
    public ThingStatement.Thing constrain(ThingConstraint.Has constraint) {
        return ThingStatement.Thing.of(this).constrain(constraint);
    }

    @Override
    public ThingStatement.Thing constrain(ThingConstraint.IID constraint) {
        return ThingStatement.Thing.of(this, constraint);
    }

    @Override
    public ConceptStatement constrain(ConceptConstraint.Is constraint) {
        return ConceptStatement.of(this, constraint);
    }

    @Override
    public ThingStatement.Attribute constrain(Predicate<?> predicate) {
        return constrain(new ThingConstraint.Predicate(predicate));
    }

    @Override
    public ThingStatement.Attribute constrain(ThingConstraint.Predicate constraint) {
        return ThingStatement.Attribute.of(this, constraint);
    }

    @Override
    public ThingStatement.Relation constrain(ThingConstraint.Relation.RolePlayer rolePlayer) {
        return constrain(new ThingConstraint.Relation(rolePlayer));
    }

    public ThingStatement.Relation constrain(ThingConstraint.Relation constraint) {
        return ThingStatement.Relation.of(this, constraint);
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
