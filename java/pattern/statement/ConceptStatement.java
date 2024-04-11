/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typeql.lang.pattern.statement;

import com.vaticle.typeql.lang.common.TypeQLVariable;
import com.vaticle.typeql.lang.pattern.constraint.ConceptConstraint;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static java.util.Collections.emptyList;

public class ConceptStatement extends Statement {

    private final TypeQLVariable.Concept variable;
    private final ConceptConstraint.Is isConstraint;
    private final int hash;

    private ConceptStatement(TypeQLVariable.Concept variable, @Nullable ConceptConstraint.Is isConstraint) {
        this.variable = variable;
        this.isConstraint = isConstraint;
        this.hash = Objects.hash(this.variable, this.isConstraint);
    }

    public static ConceptStatement of(TypeQLVariable.Concept var) {
        return new ConceptStatement(var, null);
    }

    public static ConceptStatement of(TypeQLVariable.Concept var, @Nullable ConceptConstraint.Is isConstraint) {
        return new ConceptStatement(var, isConstraint);
    }

    @Override
    public TypeQLVariable.Concept headVariable() {
        return variable;
    }

    @Override
    public boolean isConcept() {
        return true;
    }

    @Override
    public ConceptStatement asConcept() {
        return this;
    }

    @Override
    public List<ConceptConstraint> constraints() {
        return (isConstraint != null) ? list(isConstraint) : emptyList();
    }

    public Optional<ConceptConstraint.Is> is() {
        return Optional.ofNullable(isConstraint);
    }

    @Override
    public String toString(boolean pretty) {
        if (isConstraint == null) return variable.toString();
        return variable.toString() + SPACE + isConstraint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConceptStatement that = (ConceptStatement) o;
        return (this.variable.equals(that.variable) && Objects.equals(this.isConstraint, that.isConstraint));
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
