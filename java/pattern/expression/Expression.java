/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typeql.lang.pattern.expression;

import com.vaticle.typedb.common.collection.Pair;
import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.TypeQLVariable;
import com.vaticle.typeql.lang.common.exception.TypeQLException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.COMMA_SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.PARAN_CLOSE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.PARAN_OPEN;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;

public interface Expression {

    default Set<TypeQLVariable> variables() {
        Set<TypeQLVariable> collector = new HashSet<>();
        collectVariables(collector);
        return collector;
    }

    void collectVariables(Set<TypeQLVariable> collector);

    default Expression.Operation add(Expression other) {
        return new Expression.Operation(TypeQLToken.Expression.Operation.ADD, this, other);
    }

    default Expression.Operation subtract(Expression other) {
        return new Expression.Operation(TypeQLToken.Expression.Operation.SUBTRACT, this, other);
    }

    default Expression.Operation multiply(Expression other) {
        return new Expression.Operation(TypeQLToken.Expression.Operation.MULTIPLY, this, other);
    }

    default Expression.Operation divide(Expression other) {
        return new Expression.Operation(TypeQLToken.Expression.Operation.DIVIDE, this, other);
    }

    default Expression.Operation modulo(Expression other) {
        return new Expression.Operation(TypeQLToken.Expression.Operation.MODULO, this, other);
    }

    default Expression.Operation power(Expression other) {
        return new Expression.Operation(TypeQLToken.Expression.Operation.POWER, this, other);
    }

    default boolean isOperation() {
        return false;
    }

    default Expression.Operation asOperation() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Expression.Operation.class)));
    }

    default boolean isFunction() {
        return false;
    }

    default Expression.Function asFunction() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Expression.Function.class)));
    }

    default boolean isParenthesis() {
        return false;
    }

    default Parenthesis asParenthesis() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Parenthesis.class)));
    }

    default boolean isConstant() {
        return false;
    }

    default Expression.Constant<?> asConstant() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Expression.Constant.class)));
    }

    default boolean isValueVar() {
        return false;
    }

    default TypeQLVariable.Value asValueVar() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLVariable.Value.class)));
    }

    default boolean isConceptVar() {
        return false;
    }

    default TypeQLVariable.Concept asConceptVar() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLVariable.Concept.class)));
    }

    class Operation implements Expression {

        private final TypeQLToken.Expression.Operation op;
        private final Expression a;
        private final Expression b;
        private final int hash;

        public Operation(TypeQLToken.Expression.Operation op, Expression a, Expression b) {
            this.op = op;
            this.a = a;
            this.b = b;
            this.hash = Objects.hash(op, a, b);
        }

        public TypeQLToken.Expression.Operation operator() {
            return op;
        }

        public Pair<Expression, Expression> operands() {
            return new Pair<>(a, b);
        }

        @Override
        public boolean isOperation() {
            return true;
        }

        @Override
        public Operation asOperation() {
            return this;
        }

        @Override
        public void collectVariables(Set<TypeQLVariable> collector) {
            a.collectVariables(collector);
            b.collectVariables(collector);
        }

        @Override
        public String toString() {
            return a.toString() + " " + op + " " + b.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Operation that = (Operation) o;
            return (this.op.equals(that.op) && this.a.equals(that.a) && this.b.equals(that.b));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    class Function implements Expression {
        private final TypeQLToken.Expression.Function symbol;
        private final List<Expression> args;
        private final int hash;

        public Function(TypeQLToken.Expression.Function symbol, List<Expression> args) {
            this.symbol = symbol;
            this.args = args;
            this.hash = Objects.hash(this.symbol, this.args);
        }

        public TypeQLToken.Expression.Function symbol() {
            return symbol;
        }

        public List<Expression> arguments() {
            return args;
        }

        @Override
        public boolean isFunction() {
            return true;
        }

        @Override
        public Function asFunction() {
            return this;
        }

        @Override
        public void collectVariables(Set<TypeQLVariable> collector) {
            args.forEach(arg -> arg.collectVariables(collector));
        }

        @Override
        public String toString() {
            return symbol + "(" + args.stream().map(Expression::toString).collect(COMMA_SPACE.joiner()) + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Function that = (Function) o;
            return (this.symbol.equals(that.symbol) && this.args.equals(that.args));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    abstract class Constant<T> implements Expression {
        T value;

        public Constant(T value) {
            this.value = value;
        }

        @Override
        public boolean isConstant() {
            return true;
        }

        @Override
        public Constant<T> asConstant() {
            return this;
        }

        @Override
        public void collectVariables(Set<TypeQLVariable> collector) {
        }

        @Override
        public java.lang.String toString() {
            return value.toString();
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

        public Constant.Long asLong() {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Constant.Long.class)));
        }

        public Constant.Double asDouble() {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Constant.Double.class)));
        }

        public Constant.Boolean asBoolean() {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Constant.Boolean.class)));
        }

        public Constant.String asString() {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Constant.String.class)));
        }

        public Constant.DateTime asDateTime() {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Constant.DateTime.class)));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Constant<?> that = (Constant<?>) o;
            return this.value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        public static class Long extends Constant<java.lang.Long> {
            public Long(java.lang.Long value) {
                super(value);
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

        public static class Double extends Constant<java.lang.Double> {
            public Double(java.lang.Double value) {
                super(value);
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

        public static class Boolean extends Constant<java.lang.Boolean> {
            public Boolean(java.lang.Boolean value) {
                super(value);
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

        public static class String extends Constant<java.lang.String> {
            public String(java.lang.String value) {
                super(value);
            }

            @Override
            public java.lang.String toString() {
                return TypeQLToken.Char.QUOTE_DOUBLE + value() + TypeQLToken.Char.QUOTE_DOUBLE;
            }

            @Override
            public boolean isString() {
                return true;
            }

            @Override
            public String asString() {
                return this;
            }
        }

        public static class DateTime extends Constant<java.time.LocalDateTime> {
            public DateTime(java.time.LocalDateTime value) {
                super(value);
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
    }

    class Parenthesis implements Expression {
        private final Expression inner;
        private final int hash;

        public Parenthesis(Expression inner) {
            this.inner = inner;
            this.hash = Objects.hash(Parenthesis.class, inner);
        }

        public Expression inner() {
            return inner;
        }

        @Override
        public boolean isParenthesis() {
            return true;
        }

        @Override
        public Parenthesis asParenthesis() {
            return this;
        }

        @Override
        public void collectVariables(Set<TypeQLVariable> collector) {
            inner.collectVariables(collector);
        }

        @Override
        public String toString() {
            return PARAN_OPEN.toString() + SPACE + inner.toString() + SPACE + PARAN_CLOSE;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Parenthesis that = (Parenthesis) o;
            return this.inner.equals(that.inner);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
