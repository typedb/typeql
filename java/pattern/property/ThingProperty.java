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

package graql.lang.pattern.property;

import grakn.common.collection.Either;
import graql.lang.common.GraqlToken;
import graql.lang.common.exception.ErrorMessage;
import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.variable.BoundVariable;
import graql.lang.pattern.variable.ThingBoundVariable;
import graql.lang.pattern.variable.TypeBoundVariable;
import graql.lang.pattern.variable.UnboundVariable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static grakn.common.collection.Collections.list;
import static graql.lang.common.GraqlToken.Char.COLON;
import static graql.lang.common.GraqlToken.Char.COMMA_SPACE;
import static graql.lang.common.GraqlToken.Char.PARAN_CLOSE;
import static graql.lang.common.GraqlToken.Char.PARAN_OPEN;
import static graql.lang.common.GraqlToken.Char.SPACE;
import static graql.lang.common.GraqlToken.Property.HAS;
import static graql.lang.common.GraqlToken.Property.ISA;
import static graql.lang.common.GraqlToken.Property.ISAX;
import static graql.lang.common.exception.ErrorMessage.INVALID_CAST_EXCEPTION;
import static graql.lang.common.exception.ErrorMessage.MISSING_PROPERTY_RELATION_PLAYER;
import static graql.lang.pattern.variable.UnboundVariable.hidden;
import static java.util.stream.Collectors.joining;

public abstract class ThingProperty extends Property {

    public boolean isSingular() {
        return false;
    }

    public boolean isRepeatable() {
        return false;
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

    public ThingProperty.Singular asSingular() {
        throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                Repeatable.class.getCanonicalName(), Singular.class.getCanonicalName()
        ));
    }

    public ThingProperty.Repeatable asRepeatable() {
        throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                Singular.class.getCanonicalName(), Repeatable.class.getCanonicalName()
        ));
    }

    public IID asIID() {
        throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                Singular.class.getCanonicalName(), IID.class.getCanonicalName()
        ));
    }

    public ThingProperty.Isa asIsa() {
        throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                Singular.class.getCanonicalName(), Isa.class.getCanonicalName()
        ));
    }

    public ThingProperty.NEQ asNEQ() {
        throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                Singular.class.getCanonicalName(), NEQ.class.getCanonicalName()
        ));
    }

    public ThingProperty.Value<?> asValue() {
        throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                Singular.class.getCanonicalName(), Value.class.getCanonicalName()
        ));
    }

    public ThingProperty.Relation asRelation() {
        throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                Singular.class.getCanonicalName(), Relation.class.getCanonicalName()
        ));
    }

    public ThingProperty.Has asHas() {
        throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                Singular.class.getCanonicalName(), Has.class.getCanonicalName()
        ));
    }

    public static abstract class Singular extends ThingProperty {

        @Override
        public boolean isSingular() {
            return true;
        }

        @Override
        public ThingProperty.Singular asSingular() {
            return this;
        }
    }

    public static abstract class Repeatable extends ThingProperty {

        @Override
        public boolean isRepeatable() {
            return true;
        }

        @Override
        public ThingProperty.Repeatable asRepeatable() {
            return this;
        }
    }

    public static class IID extends ThingProperty.Singular {

        private static final Pattern REGEX = Pattern.compile("0x[0-9a-f]+");
        private final String iid;
        private final int hash;

        public IID(String iid) {
            if (iid == null) throw new NullPointerException("Null IID");
            if (!REGEX.matcher(iid).matches()) {
                throw GraqlException.create(ErrorMessage.INVALID_IID_STRING.message(iid, REGEX.toString()));
            }
            this.iid = iid;
            this.hash = Objects.hash(this.iid);
        }

        public String iid() {
            return iid;
        }

        @Override
        public Stream<BoundVariable<?>> variables() {
            return Stream.of();
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
            return GraqlToken.Property.IID.toString() + SPACE + iid;
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

    public static class Isa extends ThingProperty.Singular {

        private final TypeBoundVariable type;
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

        private Isa(TypeBoundVariable type, boolean isExplicit) {
            if (type == null) {
                throw new NullPointerException("Null type");
            }
            this.type = type;
            this.isExplicit = isExplicit;
            this.hash = Objects.hash(Isa.class, this.type, this.isExplicit);
        }

        public TypeBoundVariable type() {
            return type;
        }

        @Override
        public Stream<TypeBoundVariable> variables() {
            return Stream.of(type);
        }

        @Override
        public boolean isIsa() {
            return true;
        }

        @Override
        public ThingProperty.Isa asIsa() {
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

    public static class NEQ extends ThingProperty.Singular {

        private final ThingBoundVariable<?> variable;
        private final int hash;

        public NEQ(UnboundVariable variable) {
            this(variable.toThing());
        }

        private NEQ(ThingBoundVariable<?> variable) {
            if (variable == null) throw new NullPointerException("Null var");
            this.variable = variable;
            this.hash = Objects.hash(variable);
        }

        public ThingBoundVariable<?> variable() {
            return variable;
        }

        @Override
        public Stream<ThingBoundVariable<?>> variables() {
            return Stream.of(variable());
        }

        @Override
        public boolean isNEQ() {
            return true;
        }

        @Override
        public ThingProperty.NEQ asNEQ() {
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

    public static class Value<T> extends ThingProperty.Singular {

        private final ValueOperation<T> operation;
        private final int hash;

        public Value(ValueOperation<T> operation) {
            if (operation == null) throw new NullPointerException("Null operation");
            this.operation = operation;
            this.hash = Objects.hash(this.operation);
        }

        public ValueOperation<T> operation() {
            return operation;
        }

        @Override
        public Stream<ThingBoundVariable<?>> variables() {
            return operation.variable() != null ? Stream.of(operation.variable()) : Stream.empty();
        }

        @Override
        public boolean isValue() {
            return true;
        }

        @Override
        public ThingProperty.Value<?> asValue() {
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
            Value that = (Value) o;
            return (this.operation.equals(that.operation));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Relation extends ThingProperty.Singular {

        private final List<RolePlayer> players;
        private String scope;

        public Relation(RolePlayer player) {
            this(list(player));
        }

        public Relation(List<RolePlayer> players) {
            if (players == null || players.isEmpty()) {
                throw GraqlException.create(MISSING_PROPERTY_RELATION_PLAYER.message());
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
        public Stream<BoundVariable<?>> variables() {
            return players().stream().flatMap(player -> {
                Stream.Builder<BoundVariable<?>> stream = Stream.builder();
                stream.add(player.player());
                player.roleType().ifPresent(stream::add);
                return stream.build();
            });
        }

        @Override
        public boolean isRelation() {
            return true;
        }

        @Override
        public ThingProperty.Relation asRelation() {
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
            return Objects.hash(this.players);
        }

        public static class RolePlayer {

            private TypeBoundVariable roleType;
            private final ThingBoundVariable<?> player;

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

            private RolePlayer(@Nullable TypeBoundVariable roleType, ThingBoundVariable<?> player) {
                if (player == null) throw new NullPointerException("Null player");
                this.roleType = roleType;
                this.player = player;
            }

            public void setScope(String relationLabel) {
                if (roleType != null && roleType.label().isPresent()) {
                    this.roleType = hidden().type(relationLabel, roleType.label().get().label());
                }
            }

            public Optional<TypeBoundVariable> roleType() {
                return Optional.ofNullable(roleType);
            }

            public ThingBoundVariable<?> player() {
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
                return Objects.hash(roleType, player);
            }
        }
    }

    public static class Has extends ThingProperty.Repeatable {

        private final TypeBoundVariable type;
        private final ThingBoundVariable<?> attribute;
        private final int hash;

        public Has(String type, ThingProperty.Value<?> value) {
            this(hidden().type(type), hidden().asAttributeWith(value));
        }

        public Has(String type, UnboundVariable var) {
            this(hidden().type(type), var.toThing());
        }

        private Has(TypeBoundVariable type, ThingBoundVariable<?> attribute) {
            if (type == null || attribute == null) throw new NullPointerException("Null type/attribute");
            this.type = type;
            if (attribute.isNamed()) this.attribute = attribute;
            else this.attribute = attribute.asSameThingWith(new Isa(type, false));
            this.hash = Objects.hash(this.type, this.attribute);
        }

        public TypeBoundVariable type() {
            return type;
        }

        public ThingBoundVariable<?> attribute() {
            return attribute;
        }

        @Override
        public Stream<ThingBoundVariable<?>> variables() {
            return Stream.of(attribute);
        }

        @Override
        public boolean isHas() {
            return true;
        }

        @Override
        public ThingProperty.Has asHas() {
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
            return attribute.equals(that.attribute);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
