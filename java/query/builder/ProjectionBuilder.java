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
import com.vaticle.typeql.lang.query.TypeQLFetch;
import com.vaticle.typeql.lang.query.TypeQLGet;

import java.util.List;
import java.util.stream.Stream;

public interface ProjectionBuilder {

    interface Attribute {

        default TypeQLFetch.Projection.Attribute map(String attribute) {
            return map(Reference.label(attribute));
        }

        default TypeQLFetch.Projection.Attribute map(Reference.Label label) {
            return map(new Pair<>(label, null));
        }

        default TypeQLFetch.Projection.Attribute map(String attribute, String label) {
            return map(Reference.label(attribute), TypeQLFetch.Key.Label.of(label));
        }

        default TypeQLFetch.Projection.Attribute map(Reference.Label attribute, TypeQLFetch.Key.Label label) {
            return map(new Pair<>(attribute, label));
        }

        default TypeQLFetch.Projection.Attribute map(List<Pair<Reference.Label, TypeQLFetch.Key.Label>> attributes) {
            return map(attributes.stream());
        }

        TypeQLFetch.Projection.Attribute map(Pair<Reference.Label, TypeQLFetch.Key.Label> attribute);

        TypeQLFetch.Projection.Attribute map(Stream<Pair<Reference.Label, TypeQLFetch.Key.Label>> attributes);

    }

    interface Subquery {

        default TypeQLFetch.Projection.Subquery map(TypeQLFetch fetch) {
            return map(Either.first(fetch));
        }

        default TypeQLFetch.Projection.Subquery map(TypeQLGet.Aggregate aggregate) {
            return map(Either.second(aggregate));
        }

        TypeQLFetch.Projection.Subquery map(Either<TypeQLFetch, TypeQLGet.Aggregate> subquery);
    }
}
