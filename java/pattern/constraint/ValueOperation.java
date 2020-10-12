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

import graql.lang.common.GraqlToken;
import graql.lang.common.exception.GraqlException;
import graql.lang.common.util.Strings;
import graql.lang.pattern.variable.ThingVariable;
import graql.lang.pattern.variable.UnboundVariable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static grakn.common.util.Objects.className;
import static graql.lang.common.GraqlToken.Char.SPACE;
import static graql.lang.common.exception.ErrorMessage.INVALID_CASTING;
import static graql.lang.common.exception.ErrorMessage.INVALID_CONSTRAINT_DATETIME_PRECISION;
import static graql.lang.common.util.Strings.escapeRegex;
import static graql.lang.common.util.Strings.quoteString;

public abstract class ValueOperation<T> {

    private final GraqlToken.Comparator comparator;
    private final T value;
    private final int hash;

    ValueOperation(final GraqlToken.Comparator comparator, final T value) {
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

    public boolean isAssignment() {
        return false;
    }

    public boolean isComparison() {
        return false;
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

    public ValueOperation.Assignment<?> asAssignment() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Assignment.class)));
    }

    public ValueOperation.Comparison<?> asComparison() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Comparison.class)));
    }

    public Optional<ThingVariable<?>> variable() {
        return Optional.empty();
    }

    @Override
    public String toString() {
        return comparator.toString() + SPACE + Strings.valueToString(value);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ValueOperation<?> that = (ValueOperation<?>) o;
        return (this.comparator.equals(that.comparator) && this.value.equals(that.value));
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public abstract static class Assignment<T> extends ValueOperation<T> {

        Assignment(final T value) {
            super(GraqlToken.Comparator.EQV, value);
        }

        @Override
        public boolean isAssignment() {
            return true;
        }

        @Override
        public ValueOperation.Assignment<?> asAssignment() {
            return this;
        }

        public Assignment.Long asLong() {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Long.class)));
        }

        public Assignment.Double asDouble() {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Double.class)));
        }

        public Assignment.Boolean asBoolean() {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Boolean.class)));
        }

        public Assignment.String asString() {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(String.class)));
        }

        public Assignment.DateTime asDateTime() {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(DateTime.class)));
        }

        public java.lang.String toString() {
            return Strings.valueToString(value());
        }

        public static class Long extends Assignment<java.lang.Long> {

            public Long(final long value) {
                super(value);
            }

            @Override
            public boolean isLong() {
                return true;
            }

            @Override
            public Assignment.Long asLong() {
                return this;
            }
        }

        public static class Double extends Assignment<java.lang.Double> {

            public Double(final double value) {
                super(value);
            }

            @Override
            public boolean isDouble() {
                return true;
            }

            @Override
            public Assignment.Double asDouble() {
                return this;
            }
        }

        public static class Boolean extends Assignment<java.lang.Boolean> {

            public Boolean(final boolean value) {
                super(value);
            }

            @Override
            public boolean isBoolean() {
                return true;
            }

            @Override
            public Assignment.Boolean asBoolean() {
                return this;
            }
        }

        public static class String extends Assignment<java.lang.String> {

            public String(final java.lang.String value) {
                super(value);
            }

            @Override
            public boolean isString() {
                return true;
            }

            @Override
            public Assignment.String asString() {
                return this;
            }
        }

        public static class DateTime extends Assignment<LocalDateTime> {

            public DateTime(final LocalDateTime value) {
                super(value);

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
            public Assignment.DateTime asDateTime() {
                return this;
            }
        }
    }

    public abstract static class Comparison<T> extends ValueOperation<T> {

        Comparison(final GraqlToken.Comparator comparator, final T value) {
            super(comparator, value);
        }

        @Override
        public boolean isComparison() {
            return true;
        }

        @Override
        public ValueOperation.Comparison<?> asComparison() {
            return this;
        }

        public Comparison.Long asLong() {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Long.class)));
        }

        public Comparison.Double asDouble() {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Double.class)));
        }

        public Comparison.Boolean asBoolean() {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Boolean.class)));
        }

        public Comparison.String asString() {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(String.class)));
        }

        public Comparison.DateTime asDateTime() {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(DateTime.class)));
        }

        public Comparison.Variable asVariable() {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Variable.class)));
        }

        public static class Long extends Comparison<java.lang.Long> {

            public Long(final GraqlToken.Comparator comparator, final long value) {
                super(comparator, value);
            }

            @Override
            public boolean isLong() {
                return true;
            }

            @Override
            public Comparison.Long asLong() {
                return this;
            }
        }

        public static class Double extends Comparison<java.lang.Double> {

            public Double(final GraqlToken.Comparator comparator, final double value) {
                super(comparator, value);
            }

            @Override
            public boolean isDouble() {
                return true;
            }

            @Override
            public Comparison.Double asDouble() {
                return this;
            }
        }

        public static class Boolean extends Comparison<java.lang.Boolean> {

            public Boolean(final GraqlToken.Comparator comparator, final boolean value) {
                super(comparator, value);
            }

            @Override
            public boolean isBoolean() {
                return true;
            }

            @Override
            public Comparison.Boolean asBoolean() {
                return this;
            }
        }

        public static class String extends Comparison<java.lang.String> {

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

                operation.append(comparator()).append(SPACE);
                if (comparator().equals(GraqlToken.Comparator.LIKE)) {
                    operation.append(quoteString(escapeRegex(value())));
                } else {
                    operation.append(quoteString(value()));
                }

                return operation.toString();
            }

            @Override
            public Comparison.String asString() {
                return this;
            }
        }

        public static class DateTime extends Comparison<LocalDateTime> {

            public DateTime(final GraqlToken.Comparator comparator, final LocalDateTime value) {
                super(comparator, value);
            }

            @Override
            public boolean isDateTime() {
                return true;
            }

            @Override
            public Comparison.DateTime asDateTime() {
                return this;
            }
        }

        public static class Variable extends Comparison<UnboundVariable> {

            private final ThingVariable<?> variable;

            public Variable(final GraqlToken.Comparator comparator, final UnboundVariable variable) {
                super(comparator, variable);
                this.variable = variable.toThing();
            }

            @Override
            public Optional<ThingVariable<?>> variable() {
                return Optional.of(variable);
            }

            @Override
            public boolean isVariable() {
                return true;
            }

            @Override
            public Comparison.Variable asVariable() {
                return this;
            }

            @Override
            public java.lang.String toString() {
                return comparator().toString() + SPACE + value();
            }
        }
    }
}
