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

package com.vaticle.typeql.lang.pattern.variable;

import com.vaticle.typeql.lang.pattern.constraint.ConceptConstraint;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;

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
        return reference.toString() + SPACE + isConstraint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConceptVariable that = (ConceptVariable) o;
        return (this.reference.equals(that.reference) &&
                Objects.equals(this.isConstraint, that.isConstraint));
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
