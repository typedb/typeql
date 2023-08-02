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

import com.vaticle.typeql.lang.pattern.Pattern;
import com.vaticle.typeql.lang.pattern.variable.BoundVariable;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;
import com.vaticle.typeql.lang.pattern.variable.Variable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.vaticle.typeql.lang.common.TypeQLToken.Char.NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Command.DELETE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Command.INSERT;
import static com.vaticle.typeql.lang.pattern.Pattern.validateNamesUnique;
import static com.vaticle.typeql.lang.query.TypeQLDelete.validDeleteVars;
import static com.vaticle.typeql.lang.query.TypeQLInsert.validInsertVars;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class TypeQLUpdate extends TypeQLWritable {

    private final List<ThingVariable<?>> deleteVariables;
    private final List<ThingVariable<?>> insertVariables;
    private final int hash;

    private List<UnboundVariable> namedDeleteVariablesUnbound;
    private List<UnboundVariable> namedInsertVariablesUnbound;

    public TypeQLUpdate(TypeQLGet.Unmodified match, List<ThingVariable<?>> deleteVariables,
                        List<ThingVariable<?>> insertVariables) {
        super(match);
        this.deleteVariables = validDeleteVars(match, deleteVariables);
        this.insertVariables = validInsertVars(match, insertVariables);
        Stream<Pattern> patterns = concat(
                Stream.ofNullable(match).filter(Objects::nonNull).flatMap(TypeQLGet::patternsRecursive),
                concat(deleteVariables.stream(), insertVariables.stream())
        );
        validateNamesUnique(patterns);
        this.hash = Objects.hash(match, deleteVariables, insertVariables);
    }

    public TypeQLGet.Unmodified match() {
        assert match != null;
        return match;
    }


    public List<ThingVariable<?>> deleteVariables() {
        return deleteVariables;
    }

    public List<ThingVariable<?>> insertVariables() {
        return insertVariables;
    }

    public List<UnboundVariable> namedDeleteVariablesUnbound() {
        if (namedDeleteVariablesUnbound == null) {
            namedDeleteVariablesUnbound = deleteVariables.stream().flatMap(v -> concat(Stream.of(v), v.variables()))
                    .filter(Variable::isNamedConcept).map(BoundVariable::toUnbound).distinct().collect(toList());
        }
        return namedDeleteVariablesUnbound;
    }

    public List<UnboundVariable> namedInsertVariablesUnbound() {
        if (namedInsertVariablesUnbound == null) {
            namedInsertVariablesUnbound = insertVariables.stream().flatMap(v -> concat(Stream.of(v), v.variables()))
                    .filter(Variable::isNamed).map(BoundVariable::toUnbound).distinct().collect(toList());
        }
        return namedInsertVariablesUnbound;
    }

    @Override
    public String toString(boolean pretty) {
        StringBuilder query = new StringBuilder();
        query.append(match.toString(pretty)).append(NEW_LINE);
        appendClause(query, DELETE, deleteVariables.stream().map(v -> v.toString(pretty)), pretty);
        query.append(NEW_LINE);
        appendClause(query, INSERT, insertVariables.stream().map(v -> v.toString(pretty)), pretty);
        return query.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeQLUpdate that = (TypeQLUpdate) o;
        return (this.match.equals(that.match) &&
                this.deleteVariables.equals(that.deleteVariables) &&
                this.insertVariables.equals(that.insertVariables));
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
