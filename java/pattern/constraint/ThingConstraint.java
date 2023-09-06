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

package com.vaticle.typeql.lang.pattern.constraint;

import com.vaticle.typedb.common.collection.Either;
import com.vaticle.typedb.common.collection.Pair;
import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.TypeQLVariable;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.statement.ThingStatement;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typedb.common.collection.Collections.pair;
import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COLON;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COMMA_SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.PARAN_CLOSE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.PARAN_OPEN;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Constraint.HAS;
import static com.vaticle.typeql.lang.common.TypeQLToken.Constraint.ISA;
import static com.vaticle.typeql.lang.common.TypeQLToken.Constraint.ISAX;
import static com.vaticle.typeql.lang.common.TypeQLToken.Type.RELATION;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_IID_STRING;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MISSING_CONSTRAINT_RELATION_PLAYER;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

public abstract class ThingConstraint extends Constraint {

    @Override
    public Set<? extends TypeQLVariable> variables() {
        return emptySet();
    }

    @Override
    public boolean isThing() {
        return true;
    }

    @Override
    public ThingConstraint asThing() {
        return this;
    }

    public boolean isIID() {
        return false;
    }

    public boolean isIsa() {
        return false;
    }

    public boolean isPredicate() {
        return false;
    }

    public boolean isRelation() {
        return false;
    }

    public boolean isHas() {
        return false;
    }

    public IID asIID() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(IID.class)));
    }

    public ThingConstraint.Isa asIsa() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Isa.class)));
    }

    public Predicate asPredicate() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Predicate.class)));
    }

    public ThingConstraint.Relation asRelation() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Relation.class)));
    }

    public ThingConstraint.Has asHas() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Has.class)));
    }

    public static class IID extends ThingConstraint {

        private static final Pattern REGEX = Pattern.compile("0x[0-9a-f]+");
        private final String iid;
        private final int hash;

        public IID(String iid) {
            if (iid == null) throw new NullPointerException("Null IID");
            if (!REGEX.matcher(iid).matches()) {
                throw TypeQLException.of(INVALID_IID_STRING.message(iid, REGEX.toString()));
            }
            this.iid = iid;
            this.hash = Objects.hash(IID.class, this.iid);
        }

        public String iid() {
            return iid;
        }

        @Override
        public boolean isIID() {
            return true;
        }

        @Override
        public IID asIID() {
            return this;
        }

        @Override
        public String toString() {
            return TypeQLToken.Constraint.IID.toString() + SPACE + iid;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IID that = (IID) o;
            return (this.iid.equals(that.iid));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Isa extends ThingConstraint {

        private final TypeQLVariable.Concept type;
        private final boolean isExplicit;
        private final boolean isDerived;
        private final int hash;

        public Isa(String type, boolean isExplicit) {
            this(TypeQLVariable.Concept.labelVar(type), isExplicit, false);
        }

        public Isa(TypeQLVariable.Concept typeVar, boolean isExplicit) {
            this(typeVar, isExplicit, false);
        }

        public Isa(Either<String, ? extends TypeQLVariable.Concept> typeArg, boolean isExplicit) {
            this(typeArg.apply(TypeQLVariable.Concept::labelVar, var -> var), isExplicit, false);
        }

        public Isa(TypeQLVariable.Concept type, boolean isExplicit, boolean isDerived) {
            if (type == null) throw new NullPointerException("Type provided to 'isa' is null");
            this.type = type;
            this.isExplicit = isExplicit;
            this.isDerived = isDerived;
            this.hash = Objects.hash(Isa.class, this.type, this.isExplicit, this.isDerived);
        }

        public TypeQLVariable.Concept type() {
            return type;
        }

        public boolean isExplicit() {
            return isExplicit;
        }

        public boolean isDerived() {
            return isDerived;
        }

        @Override
        public Set<TypeQLVariable.Concept> variables() {
            return singleton(type);
        }

        @Override
        public boolean isIsa() {
            return true;
        }

        @Override
        public ThingConstraint.Isa asIsa() {
            return this;
        }

        @Override
        public String toString() {
            return (isExplicit ? ISAX.toString() : ISA.toString()) + SPACE + type();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Isa that = (Isa) o;
            return (this.type.equals(that.type) &&
                    this.isExplicit == that.isExplicit &&
                    this.isDerived == that.isDerived);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Relation extends ThingConstraint {

        private final Map<Pair<TypeQLVariable.Concept, TypeQLVariable.Concept>, AtomicInteger> repetitions;
        private final List<RolePlayer> players;
        private String scope;

        public Relation(RolePlayer player) {
            this(list(player));
        }

        public Relation(List<RolePlayer> players) {
            if (players == null || players.isEmpty()) throw TypeQLException.of(MISSING_CONSTRAINT_RELATION_PLAYER);
            this.repetitions = new HashMap<>();
            this.players = new ArrayList<>();
            this.scope = RELATION.toString();
            registerPlayers(players);
        }

        private void registerPlayers(List<RolePlayer> players) {
            for (RolePlayer player : players) {
                player.setScope(scope);
                player.setRepetition(incrementRepetition(player));
                this.players.add(player);
            }
        }

        private int incrementRepetition(RolePlayer player) {
            return repetitions.computeIfAbsent(pair(player.roleType, player.player),
                    k -> new AtomicInteger(0)).incrementAndGet();
        }

        public void setScope(String relationLabel) {
            this.scope = relationLabel;
            players.forEach(player -> player.setScope(scope));
        }

        public void addPlayers(RolePlayer player) {
            if (scope != null) player.setScope(scope);
            player.setRepetition(incrementRepetition(player));
            players.add(player);
        }

        public List<RolePlayer> players() {
            return players;
        }

        @Override
        public Set<TypeQLVariable.Concept> variables() {
            Set<TypeQLVariable.Concept> variables = new HashSet<>();
            players().forEach(player -> {
                variables.add(player.player());
                if (player.roleType().isPresent()) variables.add(player.roleType().get());
            });
            return variables;
        }

        @Override
        public boolean isRelation() {
            return true;
        }

        @Override
        public ThingConstraint.Relation asRelation() {
            return this;
        }

        @Override
        public String toString() {
            return PARAN_OPEN + players().stream().map(RolePlayer::toString).collect(COMMA_SPACE.joiner()) + PARAN_CLOSE;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Relation that = (Relation) o;
            return this.players.equals(that.players);
        }

        @Override
        public int hashCode() {
            return Objects.hash(Relation.class, this.players);
        }

        public static class RolePlayer {

            private TypeQLVariable.Concept roleType;
            private final TypeQLVariable.Concept player;
            private int repetition;

            public RolePlayer(@Nullable String roleType, TypeQLVariable.Concept playerVar) {
                this(roleType == null ? (TypeQLVariable.Concept) null : TypeQLVariable.Concept.labelVar(roleType), playerVar);
            }

            public RolePlayer(TypeQLVariable.Concept playerVar) {
                this((TypeQLVariable.Concept) null, playerVar);
            }

            public RolePlayer(@Nullable Either<String, ? extends TypeQLVariable.Concept> roleTypeArg, TypeQLVariable.Concept playerVar) {
                this(roleTypeArg == null ? (TypeQLVariable.Concept) null : roleTypeArg.apply(TypeQLVariable.Concept::labelVar, var -> var), playerVar);
            }

            public RolePlayer(@Nullable TypeQLVariable.Concept roleType, TypeQLVariable.Concept player) {
                if (player == null) throw new NullPointerException("Player provided to role is null");
                this.roleType = roleType;
                this.player = player;
            }

            public Optional<TypeQLVariable.Concept> roleType() {
                return Optional.ofNullable(roleType);
            }

            public TypeQLVariable.Concept player() {
                return player;
            }

            public int repetition() {
                return repetition;
            }

            private void setScope(String relationLabel) {
                if (roleType != null && roleType.isLabelled()) {
                    this.roleType = TypeQLVariable.Concept.labelVar(roleType.reference().asLabel().label(), relationLabel);
                }
            }

            private void setRepetition(int repetition) {
                this.repetition = repetition;
            }

            @Override
            public String toString() {
                if (roleType == null) {
                    return player.toString();
                } else {
                    StringBuilder syntax = new StringBuilder();
                    if (roleType.isVisible()) syntax.append(roleType.reference().toString());
                    else syntax.append(roleType.reference().asLabel().label());
                    syntax.append(COLON).append(SPACE).append(player);
                    return syntax.toString();
                }
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                RolePlayer that = (RolePlayer) o;
                return (Objects.equals(this.roleType, that.roleType) &&
                        this.player.equals(that.player) &&
                        this.repetition == that.repetition);
            }

            @Override
            public int hashCode() {
                return Objects.hash(RolePlayer.class, roleType, player, repetition);
            }
        }
    }

    public static class Has extends ThingConstraint {

        private final String type;
        private final Either<TypeQLVariable, ThingStatement.Attribute> attribute;
        private final int hash;

        public Has(String type, Predicate predicate) {
            this(type, Either.second(
                    ThingStatement.Attribute.of(TypeQLVariable.Concept.hiddenVar(), predicate)
                            .constrain(new Isa(TypeQLVariable.Concept.labelVar(type), false, true))
            ));
        }

        public Has(String type, TypeQLVariable.Concept var) {
            this(type, Either.second(ThingStatement.Attribute.of(var)
                    .constrain(new Isa(TypeQLVariable.Concept.labelVar(type), false, true))
            ));
        }

        public Has(String type, TypeQLVariable.Value var) {
            this(type, Either.first(var));
        }

        public Has(TypeQLVariable.Concept var) {
            this(null, Either.first(var));
        }

        private Has(@Nullable String type, Either<TypeQLVariable, ThingStatement.Attribute> attribute) {
            if (attribute == null) throw new NullPointerException("Null attribute");
            this.attribute = attribute;
            this.type = type;
            this.hash = Objects.hash(Has.class, this.type, this.attribute);
        }

        public Either<TypeQLVariable, ThingStatement.Attribute> attribute() {
            return attribute;
        }

        public Optional<String> type() {
            return Optional.ofNullable(type);
        }

        @Override
        public Set<TypeQLVariable> variables() {
            Set<TypeQLVariable> variables = new HashSet<>();
            if (attribute.isFirst()) {
                variables.add(attribute.first());
            } else {
                attribute.second().variables().forEach(variables::add);
            }
            return variables;
        }

        @Override
        public boolean isHas() {
            return true;
        }

        @Override
        public ThingConstraint.Has asHas() {
            return this;
        }

        @Override
        public String toString() {
            if (attribute.isFirst()) {
                return HAS.toString() + SPACE +
                        (type != null ? type + SPACE : "") +
                        attribute.first();
            } else {
                return HAS.toString() + SPACE +
                        (type != null ? type + SPACE : "") +
                        (attribute.second().headVariable().isNamed() ? attribute.second().headVariable() : attribute.second().predicate().get());
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Has that = (Has) o;
            return Objects.equals(this.type, that.type) && this.attribute.equals(that.attribute);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Predicate extends ThingConstraint {

        private final com.vaticle.typeql.lang.pattern.constraint.Predicate<?> predicate;
        private final Set<TypeQLVariable> variables;
        private final int hash;

        public Predicate(com.vaticle.typeql.lang.pattern.constraint.Predicate<?> predicate) {
            this.predicate = predicate;
            this.variables = predicate.variables().stream().map(TypeQLVariable::cloneVar).collect(Collectors.toSet());
            this.hash = Objects.hash(Predicate.class, predicate);
        }

        @Override
        public Set<TypeQLVariable> variables() {
            return variables;
        }

        public com.vaticle.typeql.lang.pattern.constraint.Predicate<?> predicate() {
            return predicate;
        }

        @Override
        public boolean isPredicate() {
            return true;
        }

        @Override
        public Predicate asPredicate() {
            return this;
        }

        @Override
        public java.lang.String toString() {
            return predicate.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Predicate that = (Predicate) o;
            return (this.predicate.equals(that.predicate));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
