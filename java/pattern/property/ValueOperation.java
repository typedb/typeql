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
import graql.lang.pattern.variable.UnscopedVariable;

import java.time.LocalDateTime;
import java.util.Objects;

import static graql.lang.common.GraqlToken.Char.SPACE;
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

    public abstract static class Comparison<T> extends ValueOperation<T> {

        Comparison(GraqlToken.Comparator comparator, T value) {
            super(comparator, value);
        }

        public static Comparison<?> of(GraqlToken.Comparator comparator, Object value) {
            if (value instanceof Long) {
                return new Comparison.Number<>(comparator, (Long) value);
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
