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

package graql.lang.property;

import grakn.common.util.Either;
import graql.lang.Graql;
import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.Conjunction;
import graql.lang.pattern.Pattern;
import graql.lang.variable.TypeVariable;
import graql.lang.variable.UnscopedVariable;
import graql.lang.variable.Variable;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static graql.lang.Graql.Token.Char.CURLY_CLOSE;
import static graql.lang.Graql.Token.Char.CURLY_OPEN;
import static graql.lang.Graql.Token.Char.SEMICOLON;
import static graql.lang.Graql.Token.Char.SPACE;
import static graql.lang.Graql.Token.Property.AS;
import static graql.lang.Graql.Token.Property.HAS;
import static graql.lang.Graql.Token.Property.KEY;
import static graql.lang.Graql.Token.Property.PLAYS;
import static graql.lang.Graql.Token.Property.REGEX;
import static graql.lang.Graql.Token.Property.RELATES;
import static graql.lang.Graql.Token.Property.SUB;
import static graql.lang.Graql.Token.Property.SUBX;
import static graql.lang.Graql.Token.Property.THEN;
import static graql.lang.Graql.Token.Property.TYPE;
import static graql.lang.Graql.Token.Property.VALUE_TYPE;
import static graql.lang.Graql.Token.Property.WHEN;
import static graql.lang.common.util.Strings.escapeRegex;
import static graql.lang.common.util.Strings.quoteString;
import static java.util.stream.Collectors.joining;

public abstract class TypeProperty extends Property {

    public boolean isSingular() {
        return false;
    }

    public boolean isRepeatable() {
        return false;
    }

    public TypeProperty.Singular asSingular() {
        throw GraqlException.invalidCastException(TypeProperty.Repeatable.class, TypeProperty.Singular.class);
    }

    public TypeProperty.Repeatable asRepeatable() {
        throw GraqlException.invalidCastException(TypeProperty.Singular.class, TypeProperty.Repeatable.class);
    }

    public TypeProperty.Label asLabel() {
        throw GraqlException.invalidCastException(TypeProperty.class, TypeProperty.Label.class);
    }

    public TypeProperty.Sub asSub() {
        throw GraqlException.invalidCastException(TypeProperty.class, TypeProperty.Sub.class);
    }

    public TypeProperty.Abstract asAbstract() {
        throw GraqlException.invalidCastException(TypeProperty.class, TypeProperty.Abstract.class);
    }

    public TypeProperty.ValueType asValueType() {
        throw GraqlException.invalidCastException(TypeProperty.class, TypeProperty.ValueType.class);
    }

    public TypeProperty.Regex asRegex() {
        throw GraqlException.invalidCastException(TypeProperty.class, TypeProperty.Regex.class);
    }

    public TypeProperty.Then asThen() {
        throw GraqlException.invalidCastException(TypeProperty.class, TypeProperty.Then.class);
    }

    public TypeProperty.When asWhen() {
        throw GraqlException.invalidCastException(TypeProperty.class, TypeProperty.When.class);
    }

    public TypeProperty.Has asHas() {
        throw GraqlException.invalidCastException(TypeProperty.class, TypeProperty.Has.class);
    }

    public TypeProperty.Plays asPlays() {
        throw GraqlException.invalidCastException(TypeProperty.class, TypeProperty.Plays.class);
    }

    public TypeProperty.Relates asRelates() {
        throw GraqlException.invalidCastException(TypeProperty.class, TypeProperty.Relates.class);
    }

    public static abstract class Singular extends TypeProperty {

        @Override
        public boolean isSingular() {
            return true;
        }

        @Override
        public TypeProperty.Singular asSingular() {
            return this;
        }
    }

    public static abstract class Repeatable extends TypeProperty {

        @Override
        public boolean isRepeatable() {
            return true;
        }

        @Override
        public TypeProperty.Repeatable asRepeatable() {
            return this;
        }
    }

    public static class Label extends TypeProperty.Singular {

        private final String label;
        private final int hash;

        public Label(String label) {
            if (label == null) throw new NullPointerException("Null label");
            this.label = label;
            this.hash = Objects.hash(label);
        }

        public String label() {
            return label;
        }

        @Override
        public Stream<Variable> variables() {
            return Stream.of();
        }

        @Override
        public TypeProperty.Label asLabel() {
            return this;
        }

        @Override
        public String toString() {
            return TYPE.toString() + SPACE + label();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Label that = (Label) o;
            return (this.label.equals(that.label));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Sub extends TypeProperty.Singular {

        private final TypeVariable type;
        private final boolean isExplicit;
        private final int hash;

        public Sub(String type, boolean isExplicit) {
            this(Graql.type(type), isExplicit);
        }

        public Sub(UnscopedVariable var, boolean isExplicit) {
            this(var.asType(), isExplicit);
        }

        public Sub(Either<String, UnscopedVariable> arg, boolean isExplicit) {
            this(arg.apply(Graql::type, UnscopedVariable::asType), isExplicit);
        }

        private Sub(TypeVariable type, boolean isExplicit) {
            if (type == null) throw new NullPointerException("Null superType");
            this.type = type;
            this.isExplicit = isExplicit;
            this.hash = Objects.hash(type, isExplicit);
        }

        public TypeVariable type() {
            return type;
        }

        @Override
        public Stream<Variable> variables() {
            return Stream.of(type);
        }

        @Override
        public TypeProperty.Sub asSub() {
            return this;
        }

        @Override
        public String toString() {
            return (isExplicit ? SUBX.toString() : SUB.toString()) + SPACE + type();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Sub that = (Sub) o;
            return (this.type.equals(that.type) && this.isExplicit == that.isExplicit);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Abstract extends TypeProperty.Singular {

        private final int hash;

        public Abstract() {
            this.hash = Objects.hash(Abstract.class);
        }

        @Override
        public Stream<Variable> variables() {
            return Stream.of();
        }

        @Override
        public TypeProperty.Abstract asAbstract() {
            return this;
        }

        @Override
        public String toString() {
            return Graql.Token.Property.ABSTRACT.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            return o != null && getClass() == o.getClass();
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class ValueType extends TypeProperty.Singular {

        private final Graql.Token.ValueType valueType;
        private final int hash;

        public ValueType(Graql.Token.ValueType valueType) {
            if (valueType == null) throw new NullPointerException("Null ValueType");
            this.valueType = valueType;
            this.hash = Objects.hash(this.valueType);
        }

        public Graql.Token.ValueType valueType() {
            return valueType;
        }

        @Override
        public Stream<Variable> variables() {
            return Stream.of();
        }

        @Override
        public TypeProperty.ValueType asValueType() {
            return this;
        }

        @Override
        public String toString() {
            return VALUE_TYPE.toString() + SPACE + valueType.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ValueType that = (ValueType) o;
            return (this.valueType.equals(that.valueType));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Regex extends TypeProperty.Singular {

        private final String regex;
        private final int hash;

        public Regex(String regex) {
            if (regex == null) throw new NullPointerException("Null regex");
            this.regex = regex;
            this.hash = Objects.hash(regex);
        }

        public String regex() {
            return regex;
        }

        @Override
        public Stream<Variable> variables() {
            return Stream.of();
        }

        @Override
        public TypeProperty.Regex asRegex() {
            return this;
        }

        @Override
        public String toString() {
            return REGEX.toString() + SPACE + quoteString(escapeRegex(regex()));
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Regex that = (Regex) o;
            return (this.regex.equals(that.regex));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    // TODO: Move this out of TypeProperty and create its own class
    public static class Then extends TypeProperty.Singular {

        private final Pattern pattern;
        private final int hash;

        public Then(Pattern pattern) {
            if (pattern == null) throw new NullPointerException("Null pattern");
            this.pattern = pattern;
            this.hash = Objects.hash(pattern);
        }

        public Pattern pattern() {
            return pattern;
        }

        @Override
        public Stream<Variable> variables() {
            return Stream.of();
        }

        @Override
        public TypeProperty.Then asThen() {
            return this;
        }

        @Override
        public String toString() {
            StringBuilder syntax = new StringBuilder();
            syntax.append(THEN).append(SPACE).append(CURLY_OPEN).append(SPACE);
            if (pattern instanceof Conjunction) {
                syntax.append(((Conjunction<?>) pattern).patterns()
                                      .stream().map(Object::toString)
                                      .collect(joining("" + SEMICOLON + SPACE)));
            } else {
                syntax.append(pattern.toString());
            }
            syntax.append(SEMICOLON).append(SPACE).append(CURLY_CLOSE);
            return syntax.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Then that = (Then) o;
            return (this.pattern.equals(that.pattern));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    // TODO: Move this out of TypeProperty and create its own class
    public static class When extends TypeProperty.Singular {

        private final Pattern pattern;
        private final int hash;

        public When(Pattern pattern) {
            if (pattern == null) throw new NullPointerException("Null Pattern");
            this.pattern = pattern;
            this.hash = Objects.hash(pattern);
        }

        public Pattern pattern() {
            return pattern;
        }

        @Override
        public Stream<Variable> variables() {
            return Stream.of();
        }

        @Override
        public TypeProperty.When asWhen() {
            return this;
        }

        @Override
        public String toString() {
            StringBuilder syntax = new StringBuilder();
            syntax.append(WHEN).append(SPACE).append(CURLY_OPEN).append(SPACE);
            if (pattern instanceof Conjunction) {
                syntax.append(((Conjunction<?>) pattern).patterns()
                                      .stream().map(Object::toString)
                                      .collect(joining("" + SEMICOLON + SPACE)));
            } else {
                syntax.append(pattern);
            }
            syntax.append(SEMICOLON).append(SPACE).append(CURLY_CLOSE);
            return syntax.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || getClass() != o.getClass()) return false;
            When that = (When) o;
            return (this.pattern.equals(that.pattern));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Has extends TypeProperty.Repeatable {

        private final TypeVariable attributeType;
        private final boolean isKey;
        private final int hash;

        public Has(String type, boolean isKey) {
            this(Graql.type(type), isKey);
        }

        public Has(UnscopedVariable var, boolean isKey) {
            this(var.asType(), isKey);
        }

        public Has(Either<String, UnscopedVariable> arg, boolean isKey) {
            this(arg.apply(Graql::type, UnscopedVariable::asType), isKey);
        }

        private Has(TypeVariable attributeType, boolean isKey) {
            this.attributeType = attributeType;
            this.isKey = isKey;
            this.hash = Objects.hash(attributeType, isKey);
        }

        public TypeVariable attribute() {
            return attributeType;
        }

        public boolean isKey() {
            return isKey;
        }

        @Override
        public Stream<Variable> variables() {
            return Stream.of(attributeType);
        }

        @Override
        public TypeProperty.Has asHas() {
            return this;
        }

        @Override
        public String toString() {
            return (isKey ? KEY.toString() : HAS.toString()) + SPACE + attributeType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Has that = (Has) o;
            return (this.attributeType.equals(that.attributeType) && this.isKey == that.isKey);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Plays extends TypeProperty.Repeatable {

        private final TypeVariable roleType;
        private final int hash;

        public Plays(String roleType) {
            this(Graql.type(roleType));
        }

        public Plays(UnscopedVariable var) {
            this(var.asType());
        }

        public Plays(Either<String, UnscopedVariable> arg) {
            this(arg.apply(Graql::type, UnscopedVariable::asType));
        }

        private Plays(TypeVariable roleType) {
            if (roleType == null) throw new NullPointerException("Null role");
            this.roleType = roleType;
            this.hash = Objects.hash(roleType);
        }

        public TypeVariable role() {
            return roleType;
        }

        @Override
        public Stream<Variable> variables() {
            return Stream.of(roleType);
        }

        @Override
        public TypeProperty.Plays asPlays() {
            return this;
        }

        @Override
        public String toString() {
            return PLAYS.toString() + SPACE + roleType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Plays that = (Plays) o;
            return (this.roleType.equals(that.roleType));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Relates extends TypeProperty.Repeatable {

        private final TypeVariable roleType;
        private final TypeVariable overriddenRoleType;
        private final int hash;

        public Relates(String roleType) {
            this(Graql.type(roleType), null);
        }

        public Relates(UnscopedVariable roleTypeVar) {
            this(roleTypeVar.asType(), null);
        }

        public Relates(String roleType, String overriddenRoleType) {
            this(Graql.type(roleType), overriddenRoleType == null ? null : Graql.type(overriddenRoleType));
        }

        public Relates(UnscopedVariable roleTypeVar, String overriddenRoleType) {
            this(roleTypeVar.asType(), overriddenRoleType == null ? null : Graql.type(overriddenRoleType));
        }

        public Relates(String roleType, UnscopedVariable overriddenRoleTypeVar) {
            this(Graql.type(roleType), overriddenRoleTypeVar == null ? null : overriddenRoleTypeVar.asType());
        }

        public Relates(UnscopedVariable roleTypeVar, UnscopedVariable overriddenRoleTypeVar) {
            this(roleTypeVar.asType(), overriddenRoleTypeVar == null ? null : overriddenRoleTypeVar.asType());
        }

        public Relates(Either<String, UnscopedVariable> roleTypeArg,
                       Either<String, UnscopedVariable> overriddenRoleTypeArg) {
            this(roleTypeArg.apply(Graql::type, UnscopedVariable::asType),
                 overriddenRoleTypeArg == null ? null : overriddenRoleTypeArg.apply(Graql::type, UnscopedVariable::asType));
        }

        private Relates(TypeVariable roleType, @Nullable TypeVariable overriddenRoleType) {
            if (roleType == null) throw new NullPointerException("Null role");
            this.roleType = roleType;
            this.overriddenRoleType = overriddenRoleType;
            this.hash = Objects.hash(roleType, overriddenRoleType);
        }

        public TypeVariable role() {
            return roleType;
        }

        public Optional<TypeVariable> overridden() {
            return Optional.ofNullable(overriddenRoleType);
        }

        @Override
        public Stream<Variable> variables() {
            return overriddenRoleType == null ? Stream.of(roleType) : Stream.of(roleType, overriddenRoleType);
        }

        @Override
        public TypeProperty.Relates asRelates() {
            return this;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(RELATES).append(SPACE).append(roleType);
            if (overriddenRoleType != null)
                builder.append(SPACE).append(AS).append(SPACE).append(overriddenRoleType);
            return builder.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Relates that = (Relates) o;
            return (this.roleType.equals(that.roleType) &&
                    Objects.equals(this.overriddenRoleType, that.overriddenRoleType));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
