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
import graql.lang.common.exception.GraqlException;
import graql.lang.common.util.Strings;
import graql.lang.pattern.variable.ThingVariable;
import graql.lang.pattern.variable.TypeVariable;
import graql.lang.pattern.variable.UnscopedVariable;
import graql.lang.pattern.variable.Variable;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import static graql.lang.common.exception.ErrorMessage.INVALID_PROPERTY_DATETIME_PRECISION;
import static graql.lang.common.util.Strings.escapeRegex;
import static graql.lang.common.util.Strings.quoteString;
import static graql.lang.pattern.variable.UnscopedVariable.hidden;
import static java.util.stream.Collectors.joining;

public abstract class ThingProperty extends Property {

    public boolean isSingular() {
        return false;
    }

    public boolean isRepeatable() {
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

    public ThingProperty.ID asID() {
        throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                Singular.class.getCanonicalName(), ID.class.getCanonicalName()
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

    // TODO: Rename to IID
    public static class ID extends ThingProperty.Singular {

        private final String id;
        private final int hash;

        public ID(String id) {
            if (id == null) throw new NullPointerException("Null id");
            this.id = id;
            this.hash = Objects.hash(this.id);
        }

        public String id() {
            return id;
        }

        @Override
        public Stream<Variable> variables() {
            return Stream.of();
        }

        @Override
        public ThingProperty.ID asID() {
            return this;
        }

        @Override
        public String toString() {
            return GraqlToken.Property.ID.toString() + SPACE + id;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ThingProperty.ID that = (ThingProperty.ID) o;
            return (this.id.equals(that.id));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Isa extends ThingProperty.Singular {

        private final TypeVariable type;
        private final boolean isExplicit;
        private final int hash;

        public Isa(String type, boolean isExplicit) {
            this(hidden().type(type), isExplicit);
        }

        public Isa(UnscopedVariable typeVar, boolean isExplicit) {
            this(typeVar.asType(), isExplicit);
        }

        public Isa(Either<String, UnscopedVariable> typeArg, boolean isExplicit) {
            this(typeArg.apply(label -> hidden().type(label), UnscopedVariable::asType), isExplicit);
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

        @Override
        public Stream<Variable> variables() {
            return Stream.of(type);
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

        private final ThingVariable<?> variable;
        private final int hash;

        public NEQ(UnscopedVariable var) {
            if (var == null) throw new NullPointerException("Null var");
            this.variable = var.asThing();
            this.hash = Objects.hash(var);
        }

        public ThingVariable<?> variable() {
            return variable;
        }

        @Override
        public Stream<Variable> variables() {
            return Stream.of(variable());
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

        private final Operation<T> operation;
        private final int hash;

        public Value(Operation<T> operation) {
            if (operation == null) throw new NullPointerException("Null operation");
            this.operation = operation;
            this.hash = Objects.hash(this.operation);
        }

        public Operation<T> operation() {
            return operation;
        }

        @Override
        public Stream<Variable> variables() {
            return operation.variable() != null ? Stream.of(operation.variable()) : Stream.empty();
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

        public abstract static class Operation<T> {

            private final GraqlToken.Comparator comparator;
            private final T value;
            private final int hash;

            Operation(GraqlToken.Comparator comparator, T value) {
                this.comparator = comparator;
                this.value = value;
                this.hash = Objects.hash(this.comparator, this.value);
            }

            public GraqlToken.Comparator comparator() {
                return comparator;
            }

            public T value() {
                return value;
            }

            public boolean isValueEquality() {
                return comparator.equals(GraqlToken.Comparator.EQV) && !hasVariable();
            }

            public boolean hasVariable() { return variable() != null;}

            public ThingVariable<?> variable() { return null;}

            @Override
            public String toString() {
                return comparator.toString() + SPACE + Strings.valueToString(value);
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Operation that = (Operation) o;
                return (this.comparator.equals(that.comparator) && this.value.equals(that.value));
            }

            @Override
            public int hashCode() {
                return hash;
            }

            public abstract static class Assignment<T> extends Operation<T> {

                Assignment(T value) {
                    super(GraqlToken.Comparator.EQV, value);
                }

                public java.lang.String toString() {
                    return Strings.valueToString(value());
                }

                public static class Number<N extends java.lang.Number> extends Assignment<N> {

                    public Number(N value) {
                        super(value);
                    }
                }

                public static class Boolean extends Assignment<java.lang.Boolean> {

                    public Boolean(boolean value) {
                        super(value);
                    }
                }

                public static class String extends Assignment<java.lang.String> {

                    public String(java.lang.String value) {
                        super(value);
                    }
                }

                public static class DateTime extends Assignment<LocalDateTime> {

                    public DateTime(LocalDateTime value) {
                        super(value);

                        // validate precision of fractional seconds, which are stored as nanos in LocalDateTime
                        int nanos = value.toLocalTime().getNano();
                        long nanosPerMilli = 1000000L;
                        long remainder = nanos % nanosPerMilli;
                        if (remainder != 0) {
                            throw GraqlException.create(INVALID_PROPERTY_DATETIME_PRECISION.message(value));
                        }
                    }
                }
            }

            public abstract static class Comparison<T> extends Operation<T> {

                Comparison(GraqlToken.Comparator comparator, T value) {
                    super(comparator, value);
                }

                // TODO: remove INTEGER and FLOAT
                public static Comparison<?> of(GraqlToken.Comparator comparator, Object value) {
                    if (value instanceof Integer) {
                        return new Comparison.Number<>(comparator, (Integer) value);
                    } else if (value instanceof Long) {
                        return new Comparison.Number<>(comparator, (Long) value);
                    } else if (value instanceof Float) {
                        return new Comparison.Number<>(comparator, (Float) value);
                    } else if (value instanceof Double) {
                        return new Comparison.Number<>(comparator, (Double) value);
                    } else if (value instanceof java.lang.Boolean) {
                        return new Comparison.Boolean(comparator, (java.lang.Boolean) value);
                    } else if (value instanceof java.lang.String) {
                        return new Comparison.String(comparator, (java.lang.String) value);
                    } else if (value instanceof LocalDateTime) {
                        return new Comparison.DateTime(comparator, (LocalDateTime) value);
                    } else if (value instanceof UnscopedVariable) {
                        return new Comparison.Variable(comparator, (UnscopedVariable) value);
                    } else {
                        throw new UnsupportedOperationException("Unsupported Value Comparison for class: " + value.getClass());
                    }
                }

                public static class Number<N extends java.lang.Number> extends Comparison<N> {

                    public Number(GraqlToken.Comparator comparator, N value) {
                        super(comparator, value);
                    }
                }

                public static class Boolean extends Comparison<java.lang.Boolean> {

                    public Boolean(GraqlToken.Comparator comparator, boolean value) {
                        super(comparator, value);
                    }
                }

                public static class String extends Comparison<java.lang.String> {

                    public String(GraqlToken.Comparator comparator, java.lang.String value) {
                        super(comparator, value);
                    }

                    @Override
                    public java.lang.String toString() {
                        StringBuilder operation = new StringBuilder();

                        operation.append(comparator()).append(SPACE);
                        if (comparator().equals(GraqlToken.Comparator.LIKE)) {
                            operation.append(quoteString(escapeRegex(value())));
                        } else {
                            operation.append(quoteString(value()));
                        }

                        return operation.toString();
                    }
                }

                public static class DateTime extends Comparison<LocalDateTime> {

                    public DateTime(GraqlToken.Comparator comparator, LocalDateTime value) {
                        super(comparator, value);
                    }
                }

                public static class Variable extends Comparison<UnscopedVariable> {

                    public Variable(GraqlToken.Comparator comparator, UnscopedVariable var) {
                        super(comparator, var);
                    }

                    public java.lang.String toString() {
                        return comparator().toString() + SPACE + value();
                    }

                    @Override
                    public ThingVariable<?> variable() { return value().asThing(); }
                }
            }
        }
    }

    public static class Relation extends ThingProperty.Singular {

        private final List<RolePlayer> players;
        private final int hash;

        public Relation(RolePlayer player) {
            this(list(player));
        }

        public Relation(List<RolePlayer> players) {
            if (players == null) throw new NullPointerException("Null relationPlayers");
            this.players = new ArrayList<>(players);
            this.hash = Objects.hash(this.players);
        }

        public void player(RolePlayer player) {
            this.players.add(player);
        }

        public List<RolePlayer> players() {
            return players;
        }

        @Override
        public Stream<Variable> variables() {
            return players().stream().flatMap(player -> {
                Stream.Builder<Variable> stream = Stream.builder();
                stream.add(player.player());
                player.roleType().ifPresent(stream::add);
                return stream.build();
            });
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
            return hash;
        }

        public static class RolePlayer {

            private final TypeVariable roleType;
            private final ThingVariable<?> player;
            private final int hash;

            public RolePlayer(String roleType, UnscopedVariable playerVar) {
                this(roleType == null ? null : hidden().type(roleType), playerVar.asThing());
            }

            public RolePlayer(UnscopedVariable roleTypeVar, UnscopedVariable playerVar) {
                this(roleTypeVar == null ? null : roleTypeVar.asType(), playerVar.asThing());
            }

            public RolePlayer(UnscopedVariable playerVar) {
                this(null, playerVar.asThing());
            }

            public RolePlayer(Either<String, UnscopedVariable> roleTypeArg, UnscopedVariable playerVar) {
                this(roleTypeArg == null ? null : roleTypeArg.apply(label -> hidden().type(label), UnscopedVariable::asType), playerVar.asThing());
            }

            private RolePlayer(@Nullable TypeVariable roleType, ThingVariable<?> player) {
                if (player == null) throw new NullPointerException("Null player");
                this.roleType = roleType;
                this.player = player;
                this.hash = Objects.hash(roleType, player);
            }

            public Optional<TypeVariable> roleType() {
                return Optional.ofNullable(roleType);
            }

            public ThingVariable<?> player() {
                return player;
            }

            @Override
            public String toString() {
                return (roleType == null ? "" : ("" + roleType + COLON + SPACE)) + player;
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
                return hash;
            }
        }
    }

    public static class Has extends ThingProperty.Repeatable {

        private final String type;
        private final ThingVariable<?> variable;
        private final int hash;

        public Has(String type, ThingProperty.Value<?> value) {
            this(type, hidden().asAttributeWith(value));
        }

        public Has(String type, UnscopedVariable var) {
            this(type, var.asThing());
        }

        // TODO: this need to be made private, and all value comparison builders on Graql API needs to be more strict
        public Has(String type, ThingVariable<?> variable) {
            if (type == null || variable == null) throw new NullPointerException("Null type/attribute");
            this.type = type;
            this.variable = variable;
            this.hash = Objects.hash(this.type, this.variable);
        }

        public String type() {
            return type;
        }

        public ThingVariable<?> variable() {
            return variable;
        }

        @Override
        public Stream<Variable> variables() {
            return Stream.of(variable);
        }

        @Override
        public ThingProperty.Has asHas() {
            return this;
        }

        @Override
        public String toString() {
            return String.valueOf(HAS) + SPACE + type + SPACE + variable;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Has that = (Has) o;
            return (type.equals(that.type) && variable.equals(that.variable));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
