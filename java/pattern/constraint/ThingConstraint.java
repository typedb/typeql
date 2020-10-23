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
import graql.lang.common.util.Strings;
import graql.lang.pattern.variable.BoundVariable;
import graql.lang.pattern.variable.ThingVariable;
import graql.lang.pattern.variable.TypeVariable;
import graql.lang.pattern.variable.UnboundVariable;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

import static grakn.common.collection.Collections.list;
import static grakn.common.collection.Collections.set;
import static grakn.common.util.Objects.className;
import static graql.lang.common.GraqlToken.Char.*;
import static graql.lang.common.GraqlToken.Comparator.EQ;
import static graql.lang.common.GraqlToken.Comparator.LIKE;
import static graql.lang.common.GraqlToken.Constraint.*;
import static graql.lang.common.exception.ErrorMessage.*;
import static graql.lang.common.util.Strings.escapeRegex;
import static graql.lang.common.util.Strings.quoteString;
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

    public Value<?> asValue() {
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

        public IID(final String iid) {
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
        public boolean equals(final Object o) {
            if (o == this) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final IID that = (IID) o;
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

        public Isa(final String type, final boolean isExplicit) {
            this(hidden().type(type), isExplicit);
        }

        public Isa(final UnboundVariable typeVar, final boolean isExplicit) {
            this(typeVar.toType(), isExplicit);
        }

        public Isa(final Either<String, UnboundVariable> typeArg, final boolean isExplicit) {
            this(typeArg.apply(label -> hidden().type(label), UnboundVariable::toType), isExplicit);
        }

        private Isa(final TypeVariable type, final boolean isExplicit) {
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
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Isa that = (Isa) o;
            return (this.type.equals(that.type) && this.isExplicit == that.isExplicit);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Relation extends ThingConstraint {

        private final List<RolePlayer> players;
        private String scope;

        public Relation(final RolePlayer player) {
            this(list(player));
        }

        public Relation(final List<RolePlayer> players) {
            if (players == null || players.isEmpty()) {
                throw GraqlException.of(MISSING_CONSTRAINT_RELATION_PLAYER.message());
            }
            this.players = new ArrayList<>(players);
        }

        public void setScope(final String relationLabel) {
            this.scope = relationLabel;
            players.forEach(player -> player.setScope(scope));
        }

        public boolean hasScope() {
            return scope != null;
        }

        public void addPlayers(final RolePlayer player) {
            if (scope != null) player.setScope(scope);
            players.add(player);
        }

        public List<RolePlayer> players() {
            return players;
        }

        @Override
        public Set<BoundVariable> variables() {
            final Set<BoundVariable> variables = new HashSet<>();
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
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Relation that = (Relation) o;
            return (this.players.equals(that.players));
        }

        @Override
        public int hashCode() {
            return Objects.hash(Relation.class, this.players);
        }

        public static class RolePlayer {

            private TypeVariable roleType;
            private final ThingVariable<?> player;

            public RolePlayer(final String roleType, final UnboundVariable playerVar) {
                this(roleType == null ? null : hidden().type(roleType), playerVar.toThing());
            }

            public RolePlayer(final UnboundVariable roleTypeVar, final UnboundVariable playerVar) {
                this(roleTypeVar == null ? null : roleTypeVar.toType(), playerVar.toThing());
            }

            public RolePlayer(final UnboundVariable playerVar) {
                this(null, playerVar.toThing());
            }

            public RolePlayer(final Either<String, UnboundVariable> roleTypeArg, final UnboundVariable playerVar) {
                this(roleTypeArg == null ? null : roleTypeArg.apply(label -> hidden().type(label), UnboundVariable::toType), playerVar.toThing());
            }

            private RolePlayer(@Nullable final TypeVariable roleType, final ThingVariable<?> player) {
                if (player == null) throw new NullPointerException("Null player");
                this.roleType = roleType;
                this.player = player;
            }

            public void setScope(final String relationLabel) {
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
                    final StringBuilder syntax = new StringBuilder();
                    if (roleType.isVisible()) syntax.append(roleType.reference().toString());
                    else syntax.append(roleType.label().get().label());
                    syntax.append(COLON).append(SPACE).append(player);
                    return syntax.toString();
                }
            }

            @Override
            public boolean equals(final Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                final RolePlayer that = (RolePlayer) o;
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

        public Has(final String type, final ThingConstraint.Value<?> value) {
            this(hidden().type(type), hidden().constrain(value));
        }

        public Has(final String type, final UnboundVariable var) {
            this(hidden().type(type), var.toThing());
        }

        private Has(final TypeVariable type, final ThingVariable<?> attribute) {
            if (type == null || attribute == null) throw new NullPointerException("Null type/attribute");
            this.type = type;
            if (attribute.isNamed()) this.attribute = attribute; // TODO: Should we not always set the ISA type?
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
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Has that = (Has) o;
            return (this.type.equals(that.type) && this.attribute.equals(that.attribute));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public abstract static class Value<T> extends ThingConstraint {

        private final GraqlToken.Comparator comparator;
        private final T value;
        private final int hash;

        Value(final GraqlToken.Comparator comparator, final T value) {
            this.comparator = comparator;
            this.value = value;
            this.hash = Objects.hash(Value.class, this.comparator, this.value);
        }

        @Override
        public boolean isValue() {
            return true;
        }

        @Override
        public Value<?> asValue() {
            return this;
        }

        public Long asLong() {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Long.class)));
        }

        public Double asDouble() {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Double.class)));
        }

        public Boolean asBoolean() {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Boolean.class)));
        }

        public String asString() {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(String.class)));
        }

        public DateTime asDateTime() {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(DateTime.class)));
        }

        public Variable asVariable() {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Variable.class)));
        }

        public GraqlToken.Comparator comparator() {
            return comparator;
        }

        public T value() {
            return value;
        }

        public boolean isLong() {
            return false;
        }

        public boolean isDouble() {
            return false;
        }

        public boolean isBoolean() {
            return false;
        }

        public boolean isString() {
            return false;
        }

        public boolean isDateTime() {
            return false;
        }

        public boolean isVariable() {
            return false;
        }

        @Override
        public Set<BoundVariable> variables() {
            return set();
        }

        @Override
        public java.lang.String toString() {
            if (comparator.equals(EQ) && !isVariable()) return Strings.valueToString(value);
            else return comparator.toString() + SPACE + Strings.valueToString(value);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Value<?> that = (Value<?>) o;
            return (this.comparator.equals(that.comparator) && this.value.equals(that.value));
        }

        @Override
        public int hashCode() {
            return hash;
        }

        public static class Long extends Value<java.lang.Long> {

            public Long(final GraqlToken.Comparator comparator, final long value) {
                super(comparator, value);
            }

            @Override
            public boolean isLong() {
                return true;
            }

            @Override
            public Long asLong() {
                return this;
            }
        }

        public static class Double extends Value<java.lang.Double> {

            public Double(final GraqlToken.Comparator comparator, final double value) {
                super(comparator, value);
            }

            @Override
            public boolean isDouble() {
                return true;
            }

            @Override
            public Double asDouble() {
                return this;
            }
        }

        public static class Boolean extends Value<java.lang.Boolean> {

            public Boolean(final GraqlToken.Comparator comparator, final boolean value) {
                super(comparator, value);
            }

            @Override
            public boolean isBoolean() {
                return true;
            }

            @Override
            public Boolean asBoolean() {
                return this;
            }
        }

        public static class String extends Value<java.lang.String> {

            public String(final GraqlToken.Comparator comparator, final java.lang.String value) {
                super(comparator, value);
            }

            @Override
            public boolean isString() {
                return true;
            }

            @Override
            public java.lang.String toString() {
                final StringBuilder operation = new StringBuilder();

                if (comparator().equals(LIKE)) {
                    operation.append(LIKE).append(SPACE).append(quoteString(escapeRegex(value())));
                } else if (comparator().equals(EQ)) {
                    operation.append(quoteString(value()));
                } else {
                    operation.append(comparator()).append(SPACE).append(quoteString(value()));
                }

                return operation.toString();
            }

            @Override
            public String asString() {
                return this;
            }
        }

        public static class DateTime extends Value<LocalDateTime> {

            public DateTime(final GraqlToken.Comparator comparator, final LocalDateTime value) {
                super(comparator, value);
                // validate precision of fractional seconds, which are stored as nanos in LocalDateTime
                final int nanos = value.toLocalTime().getNano();
                final long nanosPerMilli = 1000000L;
                final long remainder = nanos % nanosPerMilli;
                if (remainder != 0) {
                    throw GraqlException.of(INVALID_CONSTRAINT_DATETIME_PRECISION.message(value));
                }
            }

            @Override
            public boolean isDateTime() {
                return true;
            }

            @Override
            public DateTime asDateTime() {
                return this;
            }
        }

        public static class Variable extends Value<ThingVariable<?>> {

            public Variable(final GraqlToken.Comparator comparator, final UnboundVariable variable) {
                this(comparator, variable.toThing());
            }

            private Variable(final GraqlToken.Comparator comparator, final ThingVariable<?> variable) {
                super(comparator, variable);
            }

            @Override
            public Set<BoundVariable> variables() {
                return set(value());
            }

            @Override
            public boolean isVariable() {
                return true;
            }

            @Override
            public Variable asVariable() {
                return this;
            }
        }
    }
}
