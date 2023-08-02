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
import com.vaticle.typeql.lang.pattern.Pattern;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.vaticle.typeql.lang.common.TypeQLToken.Command.INSERT;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.NO_VARIABLE_IN_SCOPE_INSERT;
import static com.vaticle.typeql.lang.pattern.Pattern.validateNamesUnique;
import static java.util.stream.Stream.concat;

public class TypeQLInsert extends TypeQLWritable.InsertOrDelete {

    public TypeQLInsert(List<ThingVariable<?>> variables) {
        super(INSERT, null, variables, Modifiers.EMPTY);
        validateNamesUnique(patterns);
    }

    public TypeQLInsert(MatchClause match, List<ThingVariable<?>> variables) {
        this(match, variables, Modifiers.EMPTY);
    }

    public TypeQLInsert(MatchClause match, List<ThingVariable<?>> variables, Modifiers modifiers) {
        super(INSERT, match, validInsertVars(match, variables), modifiers);
        Stream<Pattern> patterns = concat(
                Stream.ofNullable(match).filter(Objects::nonNull).flatMap(MatchClause::patternsRecursive),
                variables.stream()
        );
        validateNamesUnique(patterns);
    }

    static List<ThingVariable<?>> validInsertVars(@Nullable MatchClause match, List<ThingVariable<?>> variables) {
        if (match != null) {
            if (variables.stream().noneMatch(var -> var.isNamed() && match.namedVariablesUnbound().contains(var.toUnbound())
                    || var.variables().anyMatch(nestedVar -> match.namedVariablesUnbound().contains(nestedVar.toUnbound())))) {
                throw TypeQLException.of(NO_VARIABLE_IN_SCOPE_INSERT.message(variables, match.namedVariablesUnbound()));
            }
        }
        return variables;
    }

    public Optional<MatchClause> match() {
        return Optional.ofNullable(match);
    }

    public List<ThingVariable<?>> variables() {
        return variables;
    }
}
