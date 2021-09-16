/*
 * Copyright (C) 2021 Vaticle
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

package com.vaticle.typeql.lang.pattern.variable;

import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.Conjunctable;
import com.vaticle.typeql.lang.pattern.Pattern;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.vaticle.typedb.common.collection.Collections.list;
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
        return list(this);
    }
}
