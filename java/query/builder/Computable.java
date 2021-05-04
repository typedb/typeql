/*
 * Copyright (C) 2021 Vaticle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
            typeList.addAll(Arrays.asList(types));

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
            typeList.addAll(Arrays.asList(types));

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
            argList.addAll(Arrays.asList(args));

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
