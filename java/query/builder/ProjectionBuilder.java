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

package com.vaticle.typeql.lang.query.builder;

import com.vaticle.typedb.common.collection.Either;
import com.vaticle.typedb.common.collection.Pair;
import com.vaticle.typeql.lang.common.Reference;
import com.vaticle.typeql.lang.common.TypeQLVariable;
import com.vaticle.typeql.lang.query.TypeQLFetch;
import com.vaticle.typeql.lang.query.TypeQLGet;

import java.util.List;
import java.util.stream.Stream;

public interface ProjectionBuilder {

    interface VariableProjection<T extends TypeQLVariable> {

        T getThis();

        default TypeQLFetch.Projection.Variable project() {
            return new TypeQLFetch.Projection.Variable(new TypeQLFetch.Projection.Key.Variable(getThis()));
        }

        default TypeQLFetch.Projection.Variable projectAs(String label) {
            return new TypeQLFetch.Projection.Variable(new TypeQLFetch.Projection.Key.Variable(getThis(), TypeQLFetch.Projection.Key.Label.of(label)));
        }
    }

    interface AttributeProjection {

        default TypeQLFetch.Projection.Attribute projectAttr(String attribute) {
            return projectAttr(Reference.label(attribute));
        }

        default TypeQLFetch.Projection.Attribute projectAttr(Reference.Label label) {
            return projectAttr(new Pair<>(label, null));
        }

        default TypeQLFetch.Projection.Attribute projectAttr(String attribute, String label) {
            return projectAttr(Reference.label(attribute), TypeQLFetch.Projection.Key.Label.of(label));
        }

        default TypeQLFetch.Projection.Attribute projectAttr(Reference.Label attribute, TypeQLFetch.Projection.Key.Label label) {
            return projectAttr(new Pair<>(attribute, label));
        }

        default TypeQLFetch.Projection.Attribute projectAttrs(List<Pair<Reference.Label, TypeQLFetch.Projection.Key.Label>> attributes) {
            return projectAttrs(attributes.stream());
        }

        TypeQLFetch.Projection.Attribute projectAttr(Pair<Reference.Label, TypeQLFetch.Projection.Key.Label> attribute);

        TypeQLFetch.Projection.Attribute projectAttrs(Stream<Pair<Reference.Label, TypeQLFetch.Projection.Key.Label>> attributes);

    }

    interface SubqueryProjection {

        default TypeQLFetch.Projection.Subquery subquery(TypeQLFetch fetch) {
            return projectSubquery(Either.first(fetch));
        }

        default TypeQLFetch.Projection.Subquery subquery(TypeQLGet.Aggregate aggregate) {
            return projectSubquery(Either.second(aggregate));
        }

        TypeQLFetch.Projection.Subquery projectSubquery(Either<TypeQLFetch, TypeQLGet.Aggregate> subquery);
    }
}
