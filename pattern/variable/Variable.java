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

import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.constraint.Constraint;

import java.util.List;
import java.util.stream.Stream;

import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;

public abstract class Variable {

    final Reference reference;

    Variable(Reference reference) {
        this.reference = reference;
    }

    public abstract List<? extends Constraint<?>> constraints();

    public boolean isUnbound() {
        return false;
    }

    public boolean isBound() {
        return false;
    }

    public boolean isConcept() {
        return false;
    }

    public boolean isType() {
        return false;
    }

    public boolean isThing() {
        return false;
    }

    public UnboundVariable asUnbound() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(UnboundVariable.class)));
    }

    public BoundVariable asBound() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(BoundVariable.class)));
    }

    public Stream<BoundVariable> variables() {
        return constraints().stream().flatMap(constraint -> constraint.variables().stream());
    }

    public Reference.Type type() {
        return reference.type();
    }

    public String name() {
        switch (reference.type()) {
            case NAME:
                return reference.asName().name();
            case LABEL:
            case ANONYMOUS:
                return null;
            default:
                assert false;
                return null;
        }
    }

    public Reference reference() {
        return reference;
    }

    public boolean isNamed() {
        return reference.isName();
    }

    public boolean isLabelled() {
        return reference.isLabel();
    }

    public boolean isAnonymised() {
        return reference.isAnonymous();
    }

    public boolean isVisible() {
        return reference.isVisible();
    }

    @Override
    public abstract String toString();

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
}
