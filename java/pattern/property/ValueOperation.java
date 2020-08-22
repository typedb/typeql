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

import graql.lang.common.GraqlToken;
import graql.lang.common.exception.GraqlException;
import graql.lang.common.util.Strings;
import graql.lang.pattern.variable.ThingVariable;
import graql.lang.pattern.variable.UnboundVariable;

import java.time.LocalDateTime;
import java.util.Objects;

import static graql.lang.common.GraqlToken.Char.SPACE;
import static graql.lang.common.exception.ErrorMessage.INVALID_CAST_EXCEPTION;
import static graql.lang.common.exception.ErrorMessage.INVALID_PROPERTY_DATETIME_PRECISION;
import static graql.lang.common.util.Strings.escapeRegex;
import static graql.lang.common.util.Strings.quoteString;

public abstract class ValueOperation<T> {

    private final GraqlToken.Comparator comparator;
    private final T value;
    private final int hash;

    ValueOperation(GraqlToken.Comparator comparator, T value) {
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

    public ValueOperation.Assignment<?> asAssignment() {
        throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                ValueOperation.class.getSimpleName(), Assignment.class.getSimpleName()
        ));
    }

    public ValueOperation.Comparison<?> asComparison() {
        throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                ValueOperation.class.getSimpleName(), Comparison.class.getSimpleName()
        ));
    }

    public boolean isAssignment() {
        return (this instanceof Assignment<?>);
    }

    public boolean isComparison() {
        return (this instanceof Comparison<?>);
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
        ValueOperation that = (ValueOperation) o;
        return (this.comparator.equals(that.comparator) && this.value.equals(that.value));
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public abstract static class Assignment<T> extends ValueOperation<T> {

        Assignment(T value) {
            super(GraqlToken.Comparator.EQV, value);
        }

        @Override
        public ValueOperation.Assignment<?> asAssignment() {
            return this;
        }

        public Assignment.Long asLong() {
            throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                    Assignment.class.getSimpleName(), Long.class.getSimpleName()
            ));
        }

        public Assignment.Double asDouble() {
            throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                    Assignment.class.getSimpleName(), Double.class.getSimpleName()
            ));
        }

        public Assignment.Boolean asBoolean() {
            throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                    Assignment.class.getSimpleName(), Boolean.class.getSimpleName()
            ));
        }

        public Assignment.String asString() {
            throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                    Assignment.class.getSimpleName(), String.class.getSimpleName()
            ));
        }

        public Assignment.DateTime asDateTime() {
            throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                    Assignment.class.getSimpleName(), DateTime.class.getSimpleName()
            ));
        }

        public java.lang.String toString() {
            return Strings.valueToString(value());
        }

        public static class Long extends Assignment<java.lang.Long> {

            public Long(long value) {
                super(value);
            }

            @Override
            public Assignment.Long asLong() {
                return this;
            }
        }

        public static class Double extends Assignment<java.lang.Double> {

            public Double(double value) {
                super(value);
            }

            @Override
            public Assignment.Double asDouble() {
                return this;
            }
        }

        public static class Boolean extends Assignment<java.lang.Boolean> {

            public Boolean(boolean value) {
                super(value);
            }

            @Override
            public Assignment.Boolean asBoolean() {
                return this;
            }
        }

        public static class String extends Assignment<java.lang.String> {

            public String(java.lang.String value) {
                super(value);
            }

            @Override
            public Assignment.String asString() {
                return this;
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

            @Override
            public Assignment.DateTime asDateTime() {
                return this;
            }
        }
    }

    public abstract static class Comparison<T> extends ValueOperation<T> {

        Comparison(GraqlToken.Comparator comparator, T value) {
            super(comparator, value);
        }

        @Override
        public ValueOperation.Comparison<?> asComparison() {
            return this;
        }

        public Comparison.Long asLong() {
            throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                    Comparison.class.getSimpleName(), Long.class.getSimpleName()
            ));
        }

        public Comparison.Double asDouble() {
            throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                    Comparison.class.getSimpleName(), Double.class.getSimpleName()
            ));
        }

        public Comparison.Boolean asBoolean() {
            throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                    Comparison.class.getSimpleName(), Boolean.class.getSimpleName()
            ));
        }

        public Comparison.String asString() {
            throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                    Comparison.class.getSimpleName(), String.class.getSimpleName()
            ));
        }

        public Comparison.DateTime asDateTime() {
            throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                    Comparison.class.getSimpleName(), DateTime.class.getSimpleName()
            ));
        }

        public Comparison.Variable asVariable() {
            throw GraqlException.create(INVALID_CAST_EXCEPTION.message(
                    Comparison.class.getSimpleName(), Variable.class.getSimpleName()
            ));
        }

        public static class Long extends Comparison<java.lang.Long> {

            public Long(GraqlToken.Comparator comparator, long value) {
                super(comparator, value);
            }

            @Override
            public Comparison.Long asLong() {
                return this;
            }
        }

        public static class Double extends Comparison<java.lang.Double> {

            public Double(GraqlToken.Comparator comparator, double value) {
                super(comparator, value);
            }

            @Override
            public Comparison.Double asDouble() {
                return this;
            }
        }

        public static class Boolean extends Comparison<java.lang.Boolean> {

            public Boolean(GraqlToken.Comparator comparator, boolean value) {
                super(comparator, value);
            }

            @Override
            public Comparison.Boolean asBoolean() {
                return this;
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

            @Override
            public Comparison.String asString() {
                return this;
            }
        }

        public static class DateTime extends Comparison<LocalDateTime> {

            public DateTime(GraqlToken.Comparator comparator, LocalDateTime value) {
                super(comparator, value);
            }

            @Override
            public Comparison.DateTime asDateTime() {
                return this;
            }
        }

        public static class Variable extends Comparison<UnboundVariable> {

            public Variable(GraqlToken.Comparator comparator, UnboundVariable var) {
                super(comparator, var);
            }

            public java.lang.String toString() {
                return comparator().toString() + SPACE + value();
            }

            @Override
            public ThingVariable<?> variable() { return value().asThing(); }

            @Override
            public Comparison.Variable asVariable() {
                return this;
            }
        }
    }
}
