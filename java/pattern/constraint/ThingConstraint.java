/*
 * Copyright (C) 2020 Grakn Labs
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

package graql.lang.pattern.constraint;

import grakn.common.collection.Either;
import graql.lang.common.GraqlToken;
import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.variable.BoundVariable;
import graql.lang.pattern.variable.ThingVariable;
import graql.lang.pattern.variable.TypeVariable;
import graql.lang.pattern.variable.UnboundVariable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import static grakn.common.collection.Collections.list;
import static grakn.common.collection.Collections.set;
import static grakn.common.util.Objects.className;
import static graql.lang.common.GraqlToken.Char.COLON;
import static graql.lang.common.GraqlToken.Char.COMMA_SPACE;
import static graql.lang.common.GraqlToken.Char.PARAN_CLOSE;
import static graql.lang.common.GraqlToken.Char.PARAN_OPEN;
import static graql.lang.common.GraqlToken.Char.SPACE;
import static graql.lang.common.GraqlToken.Constraint.HAS;
import static graql.lang.common.GraqlToken.Constraint.ISA;
import static graql.lang.common.GraqlToken.Constraint.ISAX;
import static graql.lang.common.exception.ErrorMessage.INVALID_CASTING;
import static graql.lang.common.exception.ErrorMessage.INVALID_IID_STRING;
import static graql.lang.common.exception.ErrorMessage.MISSING_CONSTRAINT_RELATION_PLAYER;
import static graql.lang.pattern.variable.UnboundVariable.hidden;
import static java.util.stream.Collectors.joining;

public abstract class ThingConstraint extends Constraint<BoundVariable> {

    @Override
    public Set<BoundVariable> variables() {
        return set();
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

    public boolean isNEQ() {
        return false;
    }

    public boolean isValue() {
        return false;
    }

    public boolean isRelation() {
        return false;
    }

    public boolean isHas() {
        return false;
    }

    public IID asIID() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(IID.class)));
    }

    public ThingConstraint.Isa asIsa() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Isa.class)));
    }

    public ThingConstraint.NEQ asNEQ() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(NEQ.class)));
    }

    public ThingConstraint.Value<?> asValue() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Value.class)));
    }

    public ThingConstraint.Relation asRelation() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Relation.class)));
    }

    public ThingConstraint.Has asHas() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Has.class)));
    }

    public static class IID extends ThingConstraint {

        private static final Pattern REGEX = Pattern.compile("0x[0-9a-f]+");
        private final String iid;
        private final int hash;

        public IID(String iid) {
            if (iid == null) throw new NullPointerException("Null IID");
            if (!REGEX.matcher(iid).matches()) {
                throw GraqlException.of(INVALID_IID_STRING.message(iid, REGEX.toString()));
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
            return GraqlToken.Constraint.IID.toString() + SPACE + iid;
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

        private final TypeVariable type;
        private final boolean isExplicit;
        private final int hash;

        public Isa(String type, boolean isExplicit) {
            this(hidden().type(type), isExplicit);
        }

        public Isa(UnboundVariable typeVar, boolean isExplicit) {
            this(typeVar.toType(), isExplicit);
        }

        public Isa(Either<String, UnboundVariable> typeArg, boolean isExplicit) {
            this(typeArg.apply(label -> hidden().type(label), UnboundVariable::toType), isExplicit);
        }

        private Isa(TypeVariable type, boolean isExplicit) {
            if (type == null) {
                throw new NullPointerException("Null type");
            }
            this.type = type;
            this.isExplicit = isExplicit;
            this.hash = Objects.hash(Isa.class, this.type, this.isExplicit);
        }

        public TypeVariable type() {
            return type;
        }

        public boolean isExplicit() {
            return isExplicit;
        }

        @Override
        public Set<BoundVariable> variables() {
            return set(type);
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
            return (this.type.equals(that.type) && this.isExplicit == that.isExplicit);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class NEQ extends ThingConstraint {

        private final ThingVariable<?> variable;
        private final int hash;

        public NEQ(UnboundVariable variable) {
            this(variable.toThing());
        }

        private NEQ(ThingVariable<?> variable) {
            if (variable == null) throw new NullPointerException("Null var");
            this.variable = variable;
            this.hash = Objects.hash(NEQ.class, this.variable);
        }

        public ThingVariable<?> variable() {
            return variable;
        }

        @Override
        public Set<BoundVariable> variables() {
            return set(variable());
        }

        @Override
        public boolean isNEQ() {
            return true;
        }

        @Override
        public ThingConstraint.NEQ asNEQ() {
            return this;
        }

        @Override
        public String toString() {
            return GraqlToken.Comparator.NEQ.toString() + SPACE + variable();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NEQ that = (NEQ) o;
            return (this.variable.equals(that.variable));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Value<T> extends ThingConstraint {

        private final ValueOperation<T> operation;
        private final int hash;

        public Value(ValueOperation<T> operation) {
            if (operation == null) throw new NullPointerException("Null operation");
            this.operation = operation;
            this.hash = Objects.hash(Value.class, this.operation);
        }

        public ValueOperation<T> operation() {
            return operation;
        }

        @Override
        public Set<BoundVariable> variables() {
            return operation.variable().isPresent() ? set(operation.variable().get()) : set();
        }

        @Override
        public boolean isValue() {
            return true;
        }

        @Override
        public ThingConstraint.Value<?> asValue() {
            return this;
        }

        @Override
        public String toString() {
            return operation().toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Value<?> that = (Value<?>) o;
            return (this.operation.equals(that.operation));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Relation extends ThingConstraint {

        private final List<RolePlayer> players;
        private String scope;

        public Relation(RolePlayer player) {
            this(list(player));
        }

        public Relation(List<RolePlayer> players) {
            if (players == null || players.isEmpty()) {
                throw GraqlException.of(MISSING_CONSTRAINT_RELATION_PLAYER.message());
            }
            this.players = new ArrayList<>(players);
        }

        public void setScope(String relationLabel) {
            this.scope = relationLabel;
            players.forEach(player -> player.setScope(scope));
        }

        public boolean hasScope() {
            return scope != null;
        }

        public void addPlayers(RolePlayer player) {
            if (scope != null) player.setScope(scope);
            players.add(player);
        }

        public List<RolePlayer> players() {
            return players;
        }

        @Override
        public Set<BoundVariable> variables() {
            Set<BoundVariable> variables = new HashSet<>();
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
            return PARAN_OPEN + players().stream().map(RolePlayer::toString).collect(joining(COMMA_SPACE.toString())) + PARAN_CLOSE;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Relation that = (Relation) o;
            return (this.players.equals(that.players));
        }

        @Override
        public int hashCode() {
            return Objects.hash(Relation.class, this.players);
        }

        public static class RolePlayer {

            private TypeVariable roleType;
            private final ThingVariable<?> player;

            public RolePlayer(String roleType, UnboundVariable playerVar) {
                this(roleType == null ? null : hidden().type(roleType), playerVar.toThing());
            }

            public RolePlayer(UnboundVariable roleTypeVar, UnboundVariable playerVar) {
                this(roleTypeVar == null ? null : roleTypeVar.toType(), playerVar.toThing());
            }

            public RolePlayer(UnboundVariable playerVar) {
                this(null, playerVar.toThing());
            }

            public RolePlayer(Either<String, UnboundVariable> roleTypeArg, UnboundVariable playerVar) {
                this(roleTypeArg == null ? null : roleTypeArg.apply(label -> hidden().type(label), UnboundVariable::toType), playerVar.toThing());
            }

            private RolePlayer(@Nullable TypeVariable roleType, ThingVariable<?> player) {
                if (player == null) throw new NullPointerException("Null player");
                this.roleType = roleType;
                this.player = player;
            }

            public void setScope(String relationLabel) {
                if (roleType != null && roleType.label().isPresent()) {
                    this.roleType = hidden().type(relationLabel, roleType.label().get().label());
                }
            }

            public Optional<TypeVariable> roleType() {
                return Optional.ofNullable(roleType);
            }

            public ThingVariable<?> player() {
                return player;
            }

            @Override
            public String toString() {
                if (roleType == null) {
                    return player.toString();
                } else {
                    StringBuilder syntax = new StringBuilder();
                    if (roleType.isVisible()) syntax.append(roleType.reference().toString());
                    else syntax.append(roleType.label().get().label());
                    syntax.append(COLON).append(SPACE).append(player);
                    return syntax.toString();
                }
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                RolePlayer that = (RolePlayer) o;
                return (Objects.equals(this.roleType, that.roleType)) && (this.player.equals(that.player));
            }

            @Override
            public int hashCode() {
                return Objects.hash(RolePlayer.class, roleType, player);
            }
        }
    }

    public static class Has extends ThingConstraint {

        private final TypeVariable type;
        private final ThingVariable<?> attribute;
        private final int hash;

        public Has(String type, ThingConstraint.Value<?> value) {
            this(hidden().type(type), hidden().constrain(value));
        }

        public Has(String type, UnboundVariable var) {
            this(hidden().type(type), var.toThing());
        }

        private Has(TypeVariable type, ThingVariable<?> attribute) {
            if (type == null || attribute == null) throw new NullPointerException("Null type/attribute");
            this.type = type;
            if (attribute.isNamed())
                this.attribute = attribute; // TODO: is this needed? Should we not always set the ISA type?
            else this.attribute = attribute.constrain(new Isa(type, false));
            this.hash = Objects.hash(Has.class, this.type, this.attribute);
        }

        public TypeVariable type() {
            return type;
        }

        public ThingVariable<?> attribute() {
            return attribute;
        }

        @Override
        public Set<BoundVariable> variables() {
            return set(attribute);
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
            return String.valueOf(HAS) + SPACE +
                    type.label().get().label() + SPACE +
                    (attribute.isNamed() ? attribute.reference() : attribute.value().get());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Has that = (Has) o;
            return (this.type.equals(that.type) && this.attribute.equals(that.attribute));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
