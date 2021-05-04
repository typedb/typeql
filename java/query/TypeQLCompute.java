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

package com.vaticle.typeql.lang.query;

import com.vaticle.typeql.lang.common.TypeQLArg;
import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.query.builder.Computable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static com.vaticle.typedb.common.collection.Collections.map;
import static com.vaticle.typedb.common.collection.Collections.pair;
import static com.vaticle.typedb.common.collection.Collections.set;
import static com.vaticle.typeql.lang.common.TypeQLToken.Predicate.Equality.EQ;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_COMPUTE_ARGUMENT;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_COMPUTE_METHOD_ALGORITHM;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MISSING_COMPUTE_CONDITION;
import static java.util.stream.Collectors.joining;


/**
 * TypeQL Compute Query: to perform distributed analytics OLAP computation on TypeDB
 */
public abstract class TypeQLCompute extends TypeQLQuery implements Computable {

    private TypeQLToken.Compute.Method method;
    boolean includeAttributes;

    // All these condition constraints need to start off as NULL,
    // they will be initialised when the user provides input
    String fromID = null;
    String toID = null;
    Set<String> ofTypes = null;
    Set<String> inTypes = null;
    TypeQLArg.Algorithm algorithm = null;
    Arguments arguments = null;
    // But 'arguments' will also be set when where() is called for cluster/centrality

    TypeQLCompute(TypeQLToken.Compute.Method method, boolean includeAttributes) {
        this.method = method;
        this.includeAttributes = includeAttributes;
    }

    @Override
    public TypeQLArg.QueryType type() {
        return TypeQLArg.QueryType.WRITE;
    }

    @Override
    public final TypeQLToken.Compute.Method method() {
        return method;
    }

    public Set<String> in() {
        if (this.inTypes == null) return set();
        return inTypes;
    }

    public boolean includesAttributes() {
        return includeAttributes;
    }

    public final boolean isValid() {
        return !getException().isPresent();
    }

    @Override
    public final String toString() {
        StringBuilder query = new StringBuilder();

        query.append(TypeQLToken.Command.COMPUTE).append(TypeQLToken.Char.SPACE).append(method);
        if (!printConditions().isEmpty()) query.append(TypeQLToken.Char.SPACE).append(printConditions());
        query.append(TypeQLToken.Char.SEMICOLON);

        return query.toString();
    }

    private String printConditions() {
        List<String> conditionsList = new ArrayList<>();

        // It is important that we check for whether each condition is NULL, rather than using the getters.
        // Because, we want to know the user provided conditions, rather than the default conditions from the getters.
        // The exception is for arguments. It needs to be set internally for the query object to have default argument
        // values. However, we can query for .getParameters() to get user provided argument parameters.
        if (fromID != null) conditionsList.add(str(TypeQLToken.Compute.Condition.FROM, TypeQLToken.Char.SPACE, fromID));
        if (toID != null) conditionsList.add(str(TypeQLToken.Compute.Condition.TO, TypeQLToken.Char.SPACE, toID));
        if (ofTypes != null) conditionsList.add(printOf());
        if (inTypes != null) conditionsList.add(printIn());
        if (algorithm != null) conditionsList.add(printAlgorithm());
        if (arguments != null && !arguments.getParameters().isEmpty()) conditionsList.add(printArguments());

        return conditionsList.stream().collect(joining(TypeQLToken.Char.COMMA_SPACE.toString()));
    }

    private String printOf() {
        if (ofTypes != null) return str(TypeQLToken.Compute.Condition.OF, TypeQLToken.Char.SPACE, printTypes(ofTypes));

        return "";
    }

    private String printIn() {
        if (inTypes != null) return str(TypeQLToken.Compute.Condition.IN, TypeQLToken.Char.SPACE, printTypes(inTypes));

        return "";
    }

    private String printTypes(Set<String> types) {
        StringBuilder inTypesString = new StringBuilder();

        if (!types.isEmpty()) {
            if (types.size() == 1) {
                inTypesString.append(types.iterator().next());
            } else {
                inTypesString.append(TypeQLToken.Char.SQUARE_OPEN);
                inTypesString.append(inTypes.stream().collect(joining(TypeQLToken.Char.COMMA_SPACE.toString())));
                inTypesString.append(TypeQLToken.Char.SQUARE_CLOSE);
            }
        }

        return inTypesString.toString();
    }

    private String printAlgorithm() {
        if (algorithm != null) return str(TypeQLToken.Compute.Condition.USING, TypeQLToken.Char.SPACE, algorithm);

        return "";
    }

    private String printArguments() {
        if (arguments == null) return "";

        List<String> argumentsList = new ArrayList<>();
        StringBuilder argumentsString = new StringBuilder();

        for (TypeQLToken.Compute.Param param : arguments.getParameters()) {
            argumentsList.add(str(param, EQ, arguments.getArgument(param).get()));
        }

        if (!argumentsList.isEmpty()) {
            argumentsString.append(str(TypeQLToken.Compute.Condition.WHERE, TypeQLToken.Char.SPACE));
            if (argumentsList.size() == 1) argumentsString.append(argumentsList.get(0));
            else {
                argumentsString.append(TypeQLToken.Char.SQUARE_OPEN);
                argumentsString.append(argumentsList.stream().collect(joining(TypeQLToken.Char.COMMA_SPACE.toString())));
                argumentsString.append(TypeQLToken.Char.SQUARE_CLOSE);
            }
        }

        return argumentsString.toString();
    }

    private String str(Object... objects) {
        StringBuilder builder = new StringBuilder();
        for (Object obj : objects) builder.append(obj.toString());
        return builder.toString();
    }

    public static class Builder {

        public TypeQLCompute.Statistics.Count count() {
            return new TypeQLCompute.Statistics.Count();
        }

        public TypeQLCompute.Statistics.Value max() {
            return new TypeQLCompute.Statistics.Value(TypeQLToken.Compute.Method.MAX);
        }

        public TypeQLCompute.Statistics.Value min() {
            return new TypeQLCompute.Statistics.Value(TypeQLToken.Compute.Method.MIN);
        }

        public TypeQLCompute.Statistics.Value mean() {
            return new TypeQLCompute.Statistics.Value(TypeQLToken.Compute.Method.MEAN);
        }

        public TypeQLCompute.Statistics.Value median() {
            return new TypeQLCompute.Statistics.Value(TypeQLToken.Compute.Method.MEDIAN);
        }

        public TypeQLCompute.Statistics.Value sum() {
            return new TypeQLCompute.Statistics.Value(TypeQLToken.Compute.Method.SUM);
        }

        public TypeQLCompute.Statistics.Value std() {
            return new TypeQLCompute.Statistics.Value(TypeQLToken.Compute.Method.STD);
        }

        public TypeQLCompute.Path path() {
            return new TypeQLCompute.Path();
        }

        public TypeQLCompute.Centrality centrality() {
            return new TypeQLCompute.Centrality();
        }

        public TypeQLCompute.Cluster cluster() {
            return new TypeQLCompute.Cluster();
        }

    }

    public static abstract class Statistics extends TypeQLCompute {

        Statistics(TypeQLToken.Compute.Method method, boolean includeAttributes) {
            super(method, includeAttributes);
        }

        public TypeQLCompute.Statistics.Count asCount() {
            if (this instanceof TypeQLCompute.Statistics.Count) {
                return (TypeQLCompute.Statistics.Count) this;
            } else {
                throw TypeQLException.of("This is not a TypeQLCompute.Statistics.Count query");
            }
        }

        public TypeQLCompute.Statistics.Value asValue() {
            if (this instanceof TypeQLCompute.Statistics.Value) {
                return (TypeQLCompute.Statistics.Value) this;
            } else {
                throw TypeQLException.of("This is not a TypeQLCompute.Statistics.Value query");
            }
        }

        public static class Count extends TypeQLCompute.Statistics
                implements Computable.Scopeable<TypeQLCompute.Statistics.Count> {

            Count() {
                super(TypeQLToken.Compute.Method.COUNT, true);
            }

            @Override
            public TypeQLCompute.Statistics.Count in(Collection<String> types) {
                this.inTypes = set(types);
                return this;
            }

            @Override
            public TypeQLCompute.Statistics.Count attributes(boolean include) {
                return this;
            }

            @Override
            public Set<TypeQLToken.Compute.Condition> conditionsRequired() {
                return set();
            }

            @Override
            public Optional<TypeQLException> getException() {
                return Optional.empty();
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                TypeQLCompute.Statistics.Count that = (TypeQLCompute.Statistics.Count) o;

                return (this.method().equals(that.method()) &&
                        this.in().equals(that.in()) &&
                        this.includesAttributes() == that.includesAttributes());
            }

            @Override
            public int hashCode() {
                int result = Objects.hashCode(method());
                result = 31 * result + Objects.hashCode(in());
                result = 31 * result + Objects.hashCode(includesAttributes());

                return result;
            }
        }

        public static class Value extends TypeQLCompute.Statistics
                implements Computable.Targetable<Value>,
                           Computable.Scopeable<Value> {

            Value(TypeQLToken.Compute.Method method) {
                super(method, true);
            }

            @Override
            public final Set<String> of() {
                return ofTypes == null ? set() : ofTypes;
            }

            @Override
            public TypeQLCompute.Statistics.Value of(Collection<String> types) {
                this.ofTypes = set(types);
                return this;
            }

            @Override
            public TypeQLCompute.Statistics.Value in(Collection<String> types) {
                this.inTypes = set(types);
                return this;
            }

            @Override
            public TypeQLCompute.Statistics.Value attributes(boolean include) {
                return this;
            }

            @Override
            public Set<TypeQLToken.Compute.Condition> conditionsRequired() {
                return set(TypeQLToken.Compute.Condition.OF);
            }

            @Override
            public Optional<TypeQLException> getException() {
                if (ofTypes == null) {
                    return Optional.of(TypeQLException.of(MISSING_COMPUTE_CONDITION.message(
                            this.method(), conditionsRequired()
                    )));
                } else {
                    return Optional.empty();
                }
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                TypeQLCompute.Statistics.Value that = (TypeQLCompute.Statistics.Value) o;

                return (this.method().equals(that.method()) &&
                        this.of().equals(that.of()) &&
                        this.in().equals(that.in()) &&
                        this.includesAttributes() == that.includesAttributes());
            }

            @Override
            public int hashCode() {
                int result = Objects.hashCode(method());
                result = 31 * result + Objects.hashCode(of());
                result = 31 * result + Objects.hashCode(in());
                result = 31 * result + Objects.hashCode(includesAttributes());

                return result;
            }
        }
    }

    public static class Path extends TypeQLCompute
            implements Computable.Directional<TypeQLCompute.Path>,
                       Computable.Scopeable<TypeQLCompute.Path> {

        Path() {
            super(TypeQLToken.Compute.Method.PATH, false);
        }

        @Override
        public final String from() {
            return fromID;
        }

        @Override
        public final String to() {
            return toID;
        }

        @Override
        public TypeQLCompute.Path from(String fromID) {
            this.fromID = fromID;
            return this;
        }

        @Override
        public TypeQLCompute.Path to(String toID) {
            this.toID = toID;
            return this;
        }

        @Override
        public TypeQLCompute.Path in(Collection<String> types) {
            this.inTypes = set(types);
            return this;
        }

        @Override
        public TypeQLCompute.Path attributes(boolean include) {
            this.includeAttributes = include;
            return this;
        }

        @Override
        public Set<TypeQLToken.Compute.Condition> conditionsRequired() {
            return set(TypeQLToken.Compute.Condition.FROM, TypeQLToken.Compute.Condition.TO);
        }

        @Override
        public Optional<TypeQLException> getException() {
            if (fromID == null || toID == null) {
                return Optional.of(TypeQLException.of(MISSING_COMPUTE_CONDITION.message(
                        this.method(), conditionsRequired()
                )));
            } else {
                return Optional.empty();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TypeQLCompute.Path that = (TypeQLCompute.Path) o;

            return (this.method().equals(that.method()) &&
                    this.from().equals(that.from()) &&
                    this.to().equals(that.to()) &&
                    this.in().equals(that.in()) &&
                    this.includesAttributes() == that.includesAttributes());
        }

        @Override
        public int hashCode() {
            int result = Objects.hashCode(method());
            result = 31 * result + Objects.hashCode(from());
            result = 31 * result + Objects.hashCode(to());
            result = 31 * result + Objects.hashCode(in());
            result = 31 * result + Objects.hashCode(includesAttributes());

            return result;
        }
    }

    private static abstract class Configurable<T extends Configurable> extends TypeQLCompute
            implements Computable.Scopeable<T>,
                       Computable.Configurable<T, TypeQLCompute.Argument, TypeQLCompute.Arguments> {

        Configurable(TypeQLToken.Compute.Method method, boolean includeAttributes) {
            super(method, includeAttributes);
        }

        protected abstract T self();

        @Override
        public TypeQLCompute.Arguments where() {
            TypeQLCompute.Arguments args = arguments;
            if (args == null) {
                args = new TypeQLCompute.Arguments();
            }
            if (argumentsDefault().containsKey(using())) {
                args.setDefaults(argumentsDefault().get(using()));
            }
            return args;
        }


        @Override
        public TypeQLArg.Algorithm using() {
            if (algorithm == null) {
                return TypeQLArg.Algorithm.DEGREE;
            } else {
                return algorithm;
            }
        }

        @Override
        public T in(Collection<String> types) {
            this.inTypes = set(types);
            return self();
        }

        @Override
        public T attributes(boolean include) {
            this.includeAttributes = include;
            return self();
        }

        @Override
        public T using(TypeQLArg.Algorithm algorithm) {
            this.algorithm = algorithm;
            return self();
        }

        @Override
        public T where(List<TypeQLCompute.Argument> args) {
            if (this.arguments == null) this.arguments = new TypeQLCompute.Arguments();
            for (TypeQLCompute.Argument<?> arg : args) this.arguments.setArgument(arg);
            return self();
        }


        @Override
        public Set<TypeQLToken.Compute.Condition> conditionsRequired() {
            return set(TypeQLToken.Compute.Condition.USING);
        }

        @Override
        public Optional<TypeQLException> getException() {
            if (!algorithmsAccepted().contains(using())) {
                return Optional.of(TypeQLException.of(INVALID_COMPUTE_METHOD_ALGORITHM.message(method(), algorithmsAccepted())));
            }

            // Check that the provided arguments are accepted for the current query method and algorithm
            for (TypeQLToken.Compute.Param param : this.where().getParameters()) {
                if (!argumentsAccepted().get(this.using()).contains(param)) {
                    return Optional.of(TypeQLException.of(INVALID_COMPUTE_ARGUMENT.message(
                            this.method(), this.using(), argumentsAccepted().get(this.using())
                    )));
                }
            }

            return Optional.empty();
        }
    }

    public static class Centrality extends TypeQLCompute.Configurable<TypeQLCompute.Centrality>
            implements Computable.Targetable<TypeQLCompute.Centrality> {

        final static long DEFAULT_MIN_K = 2L;

        Centrality() {
            super(TypeQLToken.Compute.Method.CENTRALITY, true);
        }

        @Override
        protected TypeQLCompute.Centrality self() {
            return this;
        }

        @Override
        public final Set<String> of() {
            return ofTypes == null ? set() : ofTypes;
        }

        @Override
        public TypeQLCompute.Centrality of(Collection<String> types) {
            this.ofTypes = set(types);
            return this;
        }

        @Override
        public Set<TypeQLArg.Algorithm> algorithmsAccepted() {
            return set(TypeQLArg.Algorithm.DEGREE, TypeQLArg.Algorithm.K_CORE);
        }

        @Override
        public Map<TypeQLArg.Algorithm, Set<TypeQLToken.Compute.Param>> argumentsAccepted() {
            return map(pair(TypeQLArg.Algorithm.K_CORE, set(TypeQLToken.Compute.Param.MIN_K)));
        }

        @Override
        public Map<TypeQLArg.Algorithm, Map<TypeQLToken.Compute.Param, Object>> argumentsDefault() {
            return map(pair(TypeQLArg.Algorithm.K_CORE, map(pair(TypeQLToken.Compute.Param.MIN_K, DEFAULT_MIN_K))));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TypeQLCompute.Centrality that = (TypeQLCompute.Centrality) o;

            return (this.method().equals(that.method()) &&
                    this.of().equals(that.of()) &&
                    this.in().equals(that.in()) &&
                    this.using().equals(that.using()) &&
                    this.where().equals(that.where()) &&
                    this.includesAttributes() == that.includesAttributes());
        }

        @Override
        public int hashCode() {
            int result = Objects.hashCode(method());
            result = 31 * result + Objects.hashCode(of());
            result = 31 * result + Objects.hashCode(in());
            result = 31 * result + Objects.hashCode(using());
            result = 31 * result + Objects.hashCode(where());
            result = 31 * result + Objects.hashCode(includesAttributes());

            return result;
        }
    }

    public static class Cluster extends TypeQLCompute.Configurable<TypeQLCompute.Cluster> {

        final static long DEFAULT_K = 2L;

        Cluster() {
            super(TypeQLToken.Compute.Method.CLUSTER, false);
        }

        @Override
        protected TypeQLCompute.Cluster self() {
            return this;
        }

        @Override
        public TypeQLArg.Algorithm using() {
            if (algorithm == null) {
                return TypeQLArg.Algorithm.CONNECTED_COMPONENT;
            } else {
                return algorithm;
            }
        }

        @Override
        public Set<TypeQLArg.Algorithm> algorithmsAccepted() {
            return set(TypeQLArg.Algorithm.CONNECTED_COMPONENT, TypeQLArg.Algorithm.K_CORE);
        }

        @Override
        public Map<TypeQLArg.Algorithm, Set<TypeQLToken.Compute.Param>> argumentsAccepted() {
            return map(pair(TypeQLArg.Algorithm.K_CORE, set(TypeQLToken.Compute.Param.K)),
                       pair(TypeQLArg.Algorithm.CONNECTED_COMPONENT, set(TypeQLToken.Compute.Param.SIZE, TypeQLToken.Compute.Param.CONTAINS)));
        }

        @Override
        public Map<TypeQLArg.Algorithm, Map<TypeQLToken.Compute.Param, Object>> argumentsDefault() {
            return map(pair(TypeQLArg.Algorithm.K_CORE, map(pair(TypeQLToken.Compute.Param.K, DEFAULT_K))));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TypeQLCompute.Cluster that = (TypeQLCompute.Cluster) o;

            return (this.method().equals(that.method()) &&
                    this.in().equals(that.in()) &&
                    this.using().equals(that.using()) &&
                    this.where().equals(that.where()) &&
                    this.includesAttributes() == that.includesAttributes());
        }

        @Override
        public int hashCode() {
            int result = Objects.hashCode(method());
            result = 31 * result + Objects.hashCode(in());
            result = 31 * result + Objects.hashCode(using());
            result = 31 * result + Objects.hashCode(where());
            result = 31 * result + Objects.hashCode(includesAttributes());

            return result;
        }
    }

    /**
     * TypeQL Compute argument objects to be passed into the query
     *
     * @param <T>
     */
    public static class Argument<T> implements Computable.Argument<T> {

        private TypeQLToken.Compute.Param param;
        private T value;

        private Argument(TypeQLToken.Compute.Param param, T value) {
            this.param = param;
            this.value = value;
        }

        @Override
        public final TypeQLToken.Compute.Param type() {
            return this.param;
        }

        @Override
        public final T value() {
            return this.value;
        }

        public static Argument<Long> minK(long minK) {
            return new Argument<>(TypeQLToken.Compute.Param.MIN_K, minK);
        }

        public static Argument<Long> k(long k) {
            return new Argument<>(TypeQLToken.Compute.Param.K, k);
        }

        public static Argument<Long> size(long size) {
            return new Argument<>(TypeQLToken.Compute.Param.SIZE, size);
        }

        public static Argument<String> contains(String conceptId) {
            return new Argument<>(TypeQLToken.Compute.Param.CONTAINS, conceptId);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Argument<?> that = (Argument<?>) o;

            return (this.type().equals(that.type()) &&
                    this.value().equals(that.value()));
        }

        @Override
        public int hashCode() {
            int result = param.hashCode();
            result = 31 * result + value.hashCode();

            return result;
        }
    }

    /**
     * Argument inner class to provide access Compute Query arguments
     */
    public static class Arguments implements Computable.Arguments {

        private LinkedHashMap<TypeQLToken.Compute.Param, Argument> argumentsOrdered = new LinkedHashMap<>();
        private Map<TypeQLToken.Compute.Param, Object> defaults = new HashMap<>();

        private final Map<TypeQLToken.Compute.Param, Supplier<Optional<?>>> argumentsMap = argumentsMap();

        private Map<TypeQLToken.Compute.Param, Supplier<Optional<?>>> argumentsMap() {
            Map<TypeQLToken.Compute.Param, Supplier<Optional<?>>> arguments = new HashMap<>();
            arguments.put(TypeQLToken.Compute.Param.MIN_K, this::minK);
            arguments.put(TypeQLToken.Compute.Param.K, this::k);
            arguments.put(TypeQLToken.Compute.Param.SIZE, this::size);
            arguments.put(TypeQLToken.Compute.Param.CONTAINS, this::contains);

            return arguments;
        }

        private void setArgument(Argument<?> arg) {
            argumentsOrdered.remove(arg.type());
            argumentsOrdered.put(arg.type(), arg);
        }

        private void setDefaults(Map<TypeQLToken.Compute.Param, Object> defaults) {
            this.defaults = defaults;
        }

        Optional<?> getArgument(TypeQLToken.Compute.Param param) {
            return argumentsMap.get(param).get();
        }

        public Set<TypeQLToken.Compute.Param> getParameters() {
            return argumentsOrdered.keySet();
        }

        @Override
        public Optional<Long> minK() {
            Long minK = (Long) getArgumentValue(TypeQLToken.Compute.Param.MIN_K);
            if (minK != null) {
                return Optional.of(minK);

            } else if (defaults.containsKey(TypeQLToken.Compute.Param.MIN_K)) {
                return Optional.of((Long) defaults.get(TypeQLToken.Compute.Param.MIN_K));

            } else {
                return Optional.empty();
            }
        }

        @Override
        public Optional<Long> k() {
            Long minK = (Long) getArgumentValue(TypeQLToken.Compute.Param.K);
            if (minK != null) {
                return Optional.of(minK);

            } else if (defaults.containsKey(TypeQLToken.Compute.Param.K)) {
                return Optional.of((Long) defaults.get(TypeQLToken.Compute.Param.K));

            } else {
                return Optional.empty();
            }
        }

        @Override
        public Optional<Long> size() {
            return Optional.ofNullable((Long) getArgumentValue(TypeQLToken.Compute.Param.SIZE));
        }

        @Override
        public Optional<String> contains() {
            return Optional.ofNullable((String) getArgumentValue(TypeQLToken.Compute.Param.CONTAINS));
        }

        private Object getArgumentValue(TypeQLToken.Compute.Param param) {
            return argumentsOrdered.get(param) != null ? argumentsOrdered.get(param).value() : null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Arguments that = (Arguments) o;

            return (this.minK().equals(that.minK()) &&
                    this.k().equals(that.k()) &&
                    this.size().equals(that.size()) &&
                    this.contains().equals(that.contains()));
        }

        @Override
        public int hashCode() {
            int h = 1;
            h *= 1000003;
            h ^= this.argumentsOrdered.hashCode();

            return h;
        }
    }
}
