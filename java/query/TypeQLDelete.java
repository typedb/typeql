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

package com.vaticle.typeql.lang.query;

import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;

import javax.annotation.Nullable;
import java.util.List;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.common.TypeQLToken.Command.DELETE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.VARIABLE_OUT_OF_SCOPE_DELETE;
import static java.util.Objects.requireNonNull;

public class TypeQLDelete extends TypeQLWritable.InsertOrDelete {

    TypeQLDelete(MatchClause match, List<ThingVariable<?>> variables) {
        this(match, variables, Modifiers.EMPTY);
    }

    TypeQLDelete(MatchClause match, List<ThingVariable<?>> variables, @Nullable Modifiers modifiers) {
        super(DELETE, requireNonNull(match), validDeleteVars(match, variables), modifiers == null ? Modifiers.EMPTY : modifiers);
    }

    static List<ThingVariable<?>> validDeleteVars(MatchClause match, List<ThingVariable<?>> variables) {
        variables.forEach(var -> {
            if (var.isNamedConcept() && !match.namedVariablesUnbound().contains(var.toUnbound())) {
                throw TypeQLException.of(VARIABLE_OUT_OF_SCOPE_DELETE.message(var.reference()));
            }
            var.variables().forEach(nestedVar -> {
                if (nestedVar.isNamedConcept() && !match.namedVariablesUnbound().contains(nestedVar.toUnbound())) {
                    throw TypeQLException.of(VARIABLE_OUT_OF_SCOPE_DELETE.message(nestedVar.reference()));
                }
            });
        });
        return variables;
    }

    public List<ThingVariable<?>> variables() { return variables; }

    public TypeQLUpdate insert(ThingVariable<?>... things) {
        return insert(list(things));
    }

    public TypeQLUpdate insert(List<ThingVariable<?>> things) {
        return new TypeQLUpdate(this.match().get(), variables, things);
    }
}
