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
