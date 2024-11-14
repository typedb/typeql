/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.typeql.lang.query.builder;

import com.typedb.common.collection.Either;
import com.typedb.common.collection.Pair;
import com.typeql.lang.common.Reference;
import com.typeql.lang.query.TypeQLFetch;
import com.typeql.lang.query.TypeQLGet;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ProjectionBuilder {

    interface Attribute {

        default TypeQLFetch.Projection.Attribute fetch(String... attributes) {
            return fetch(Stream.of(attributes)
                    .map(attr -> new Pair<>(Reference.label(attr), (TypeQLFetch.Key.Label) null))
                    .collect(Collectors.toList()));
        }

        default TypeQLFetch.Projection.Attribute fetch(Reference.Label... labels) {
            return fetch(Stream.of(labels)
                    .map(label -> new Pair<>(label, (TypeQLFetch.Key.Label) null))
                    .collect(Collectors.toList()));
        }

        default TypeQLFetch.Projection.Attribute fetch(String attribute, String label) {
            return fetch(Reference.label(attribute), TypeQLFetch.Key.Label.of(label));
        }

        default TypeQLFetch.Projection.Attribute fetch(Reference.Label attribute, TypeQLFetch.Key.Label label) {
            return fetch(new Pair<>(attribute, label));
        }

        TypeQLFetch.Projection.Attribute fetch(List<Pair<Reference.Label, TypeQLFetch.Key.Label>> attributes);

        TypeQLFetch.Projection.Attribute fetch(Pair<Reference.Label, TypeQLFetch.Key.Label> attribute);
    }

    interface Subquery {

        default TypeQLFetch.Projection.Subquery fetch(TypeQLFetch fetch) {
            return fetch(Either.first(fetch));
        }

        default TypeQLFetch.Projection.Subquery fetch(TypeQLGet.Aggregate aggregate) {
            return fetch(Either.second(aggregate));
        }

        TypeQLFetch.Projection.Subquery fetch(Either<TypeQLFetch, TypeQLGet.Aggregate> subquery);
    }
}
