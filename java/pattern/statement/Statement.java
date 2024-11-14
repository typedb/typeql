/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.typeql.lang.pattern.statement;

import com.typeql.lang.common.TypeQLVariable;
import com.typeql.lang.common.exception.TypeQLException;
import com.typeql.lang.pattern.Conjunctable;
import com.typeql.lang.pattern.Pattern;
import com.typeql.lang.pattern.constraint.Constraint;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.typedb.common.collection.Collections.list;
import static com.typedb.common.util.Objects.className;
import static com.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;
import static com.typeql.lang.common.exception.ErrorMessage.MATCH_HAS_UNBOUNDED_NESTED_PATTERN;

public abstract class Statement implements Conjunctable {

    public abstract TypeQLVariable headVariable();

    public Stream<TypeQLVariable> constraintVariables() {
        return constraints().stream().flatMap(constraint -> constraint.variables().stream());
    }

    public Stream<TypeQLVariable> variables() {
        return Stream.concat(Stream.of(headVariable()), constraintVariables());
    }

    public abstract List<? extends Constraint> constraints();

    @Override
    public void validateIsBoundedBy(Set<TypeQLVariable> bounds) {
        if (variables().noneMatch(bounds::contains)) {
            throw TypeQLException.of(MATCH_HAS_UNBOUNDED_NESTED_PATTERN.message(toString()));
        }
    }

    public boolean isConcept() {
        return false;
    }

    public ConceptStatement asConcept() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(ConceptStatement.class)));
    }

    public boolean isType() {
        return false;
    }

    public TypeStatement asType() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeStatement.class)));
    }

    public boolean isThing() {
        return false;
    }

    public ThingStatement<?> asThing() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(ThingStatement.class)));
    }

    public boolean isValue() {
        return false;
    }

    public ValueStatement asValue() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(ValueStatement.class)));
    }

    @Override
    public Statement normalise() {
        return this;
    }

    @Override
    public boolean isStatement() {
        return true;
    }

    @Override
    public Statement asStatement() {
        return this;
    }

    @Override
    public List<? extends Pattern> patterns() {
        return list(this);
    }

    @Override
    public String toString() {
        return toString(true);
    }
}
