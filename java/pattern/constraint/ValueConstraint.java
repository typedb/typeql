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

import static grakn.common.util.Objects.className;
import static graql.lang.common.GraqlToken.Char.SPACE;
import static graql.lang.common.exception.ErrorMessage.INVALID_CASTING;
import static graql.lang.common.exception.ErrorMessage.INVALID_CONSTRAINT_DATETIME_PRECISION;
import static graql.lang.common.util.Strings.escapeRegex;
import static graql.lang.common.util.Strings.quoteString;

public abstract class ValueConstraint<T> {

    private final GraqlToken.Comparator comparator;
    private final T value;
    private final int hash;

    ValueConstraint(GraqlToken.Comparator comparator, T value) {
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

    public ValueConstraint.Assignment<?> asAssignment() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Assignment.class)));
    }

    public ValueConstraint.Comparison<?> asComparison() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Comparison.class)));
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

    public boolean hasVariable() {
        return variable() != null;
    }

    public ThingVariable<?> variable() {
        return null;
    }

    @Override
    public String toString() {
        return comparator.toString() + SPACE + Strings.valueToString(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueConstraint that = (ValueConstraint) o;
        return (this.comparator.equals(that.comparator) && this.value.equals(that.value));
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public abstract static class Assignment<T> extends ValueConstraint<T> {

        Assignment(T value) {
            super(GraqlToken.Comparator.EQV, value);
        }

        @Override
        public ValueConstraint.Assignment<?> asAssignment() {
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
                    throw GraqlException.of(INVALID_CONSTRAINT_DATETIME_PRECISION.message(value));
                }
            }

            @Override
            public Assignment.DateTime asDateTime() {
                return this;
            }
        }
    }

    public abstract static class Comparison<T> extends ValueConstraint<T> {

        Comparison(GraqlToken.Comparator comparator, T value) {
            super(comparator, value);
        }

        @Override
        public ValueConstraint.Comparison<?> asComparison() {
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

            private final ThingVariable<?> variable;

            public Variable(GraqlToken.Comparator comparator, UnboundVariable variable) {
                super(comparator, variable);
                this.variable = variable.toThing();
            }

            public java.lang.String toString() {
                return comparator().toString() + SPACE + value();
            }

            @Override
            public ThingVariable<?> variable() {
                return variable;
            }

            @Override
            public Comparison.Variable asVariable() {
                return this;
            }
        }
    }
}
