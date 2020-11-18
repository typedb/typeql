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

package graql.lang.pattern.variable;

import graql.lang.pattern.constraint.ConceptConstraint;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static grakn.common.collection.Collections.list;
import static graql.lang.common.GraqlToken.Char.SPACE;

public class ConceptVariable extends BoundVariable {

    private final ConceptConstraint.Is isConstraint;
    private final int hash;

    ConceptVariable(Reference reference) {
        this(reference, null);
    }

    ConceptVariable(Reference reference, @Nullable ConceptConstraint.Is isConstraint) {
        super(reference);
        this.isConstraint = isConstraint;
        this.hash = Objects.hash(this.reference, this.isConstraint);
    }

    @Override
    public boolean isConcept() {
        return true;
    }

    @Override
    public ConceptVariable asConcept() {
        return this;
    }

    @Override
    public List<ConceptConstraint> constraints() {
        return (isConstraint != null) ? list(isConstraint) : Collections.emptyList();
    }

    public Optional<ConceptConstraint.Is> is() {
        return Optional.ofNullable(isConstraint);
    }

    @Override
    public String toString() {
        if (isConstraint == null) return reference.toString();
        return reference.toString() + SPACE + isConstraint.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ConceptVariable that = (ConceptVariable) o;
        return (this.reference.equals(that.reference) &&
                Objects.equals(this.isConstraint, that.isConstraint));
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
