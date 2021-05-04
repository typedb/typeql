/*
 * Copyright (C) 2021 Vaticle
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

package com.vaticle.typeql.lang.pattern.variable;

import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.Conjunctable;
import com.vaticle.typeql.lang.pattern.Pattern;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MATCH_HAS_UNBOUNDED_NESTED_PATTERN;

public abstract class BoundVariable extends Variable implements Conjunctable {

    BoundVariable(Reference reference) {
        super(reference);
    }

    @Override
    public void validateIsBoundedBy(Set<UnboundVariable> bounds) {
        if (Stream.concat(Stream.of(this), variables()).noneMatch(v -> bounds.contains(v.toUnbound()))) {
            throw TypeQLException.of(MATCH_HAS_UNBOUNDED_NESTED_PATTERN.message(toString()));
        }
    }

    @Override
    public boolean isBound() {
        return true;
    }

    @Override
    public BoundVariable asBound() {
        return this;
    }

    public UnboundVariable toUnbound() {
        return new UnboundVariable(reference);
    }

    public ConceptVariable asConcept() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(ConceptVariable.class)));
    }

    public TypeVariable asType() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeVariable.class)));
    }

    public ThingVariable<?> asThing() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(ThingVariable.class)));
    }

    @Override
    public BoundVariable normalise() { return this; }

    @Override
    public boolean isVariable() { return true; }

    @Override
    public BoundVariable asVariable() { return this; }

    @Override
    public List<? extends Pattern> patterns() {
        return Arrays.asList(this);
    }
}
