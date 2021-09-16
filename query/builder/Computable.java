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

package com.vaticle.typeql.lang.query.builder;

import com.vaticle.typeql.lang.common.TypeQLArg;
import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.exception.TypeQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.vaticle.typedb.common.collection.Collections.list;

public interface Computable {

    TypeQLToken.Compute.Method method();

    Set<TypeQLToken.Compute.Condition> conditionsRequired();

    Optional<TypeQLException> getException();

    interface Directional<T extends Computable.Directional> extends Computable {

        String from();

        String to();

        T from(String fromID);

        T to(String toID);
    }

    interface Targetable<T extends Computable.Targetable> extends Computable {

        Set<String> of();

        default T of(String type, String... types) {
            ArrayList<String> typeList = new ArrayList<>(types.length + 1);
            typeList.add(type);
            typeList.addAll(list(types));

            return of(typeList);
        }

        T of(Collection<String> types);
    }

    interface Scopeable<T extends Computable.Scopeable> extends Computable {

        Set<String> in();

        boolean includesAttributes();

        default T in(String type, String... types) {
            ArrayList<String> typeList = new ArrayList<>(types.length + 1);
            typeList.add(type);
            typeList.addAll(list(types));

            return in(typeList);
        }

        T in(Collection<String> types);

        T attributes(boolean include);
    }

    interface Configurable<T extends Computable.Configurable,
            U extends Computable.Argument, V extends Computable.Arguments> extends Computable {

        TypeQLArg.Algorithm using();

        V where();

        T using(TypeQLArg.Algorithm algorithm);

        @SuppressWarnings("unchecked")
        default T where(U arg, U... args) {
            ArrayList<U> argList = new ArrayList<>(args.length + 1);
            argList.add(arg);
            argList.addAll(list(args));

            return where(argList);
        }

        T where(List<U> args);

        Set<TypeQLArg.Algorithm> algorithmsAccepted();

        Map<TypeQLArg.Algorithm, Set<TypeQLToken.Compute.Param>> argumentsAccepted();

        Map<TypeQLArg.Algorithm, Map<TypeQLToken.Compute.Param, Object>> argumentsDefault();
    }

    interface Argument<T> {

        TypeQLToken.Compute.Param type();

        T value();
    }

    interface Arguments {

        Optional<Long> minK();

        Optional<Long> k();

        Optional<Long> size();

        Optional<String> contains();
    }
}
