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
import grakn.common.collection.Pair;
import graql.lang.common.GraqlArg;
import graql.lang.common.GraqlToken;
import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.Conjunction;
import graql.lang.pattern.Pattern;
import graql.lang.pattern.variable.TypeVariable;
import graql.lang.pattern.variable.UnboundVariable;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import static grakn.common.collection.Collections.set;
import static grakn.common.util.Objects.className;
import static graql.lang.common.GraqlToken.Char.COLON;
import static graql.lang.common.GraqlToken.Char.CURLY_CLOSE;
import static graql.lang.common.GraqlToken.Char.CURLY_OPEN;
import static graql.lang.common.GraqlToken.Char.SEMICOLON;
import static graql.lang.common.GraqlToken.Char.SPACE;
import static graql.lang.common.GraqlToken.Constraint.AS;
import static graql.lang.common.GraqlToken.Constraint.IS_KEY;
import static graql.lang.common.GraqlToken.Constraint.OWNS;
import static graql.lang.common.GraqlToken.Constraint.PLAYS;
import static graql.lang.common.GraqlToken.Constraint.REGEX;
import static graql.lang.common.GraqlToken.Constraint.RELATES;
import static graql.lang.common.GraqlToken.Constraint.SUB;
import static graql.lang.common.GraqlToken.Constraint.SUBX;
import static graql.lang.common.GraqlToken.Constraint.THEN;
import static graql.lang.common.GraqlToken.Constraint.TYPE;
import static graql.lang.common.GraqlToken.Constraint.VALUE_TYPE;
import static graql.lang.common.GraqlToken.Constraint.WHEN;
import static graql.lang.common.exception.ErrorMessage.INVALID_ATTRIBUTE_TYPE_REGEX;
import static graql.lang.common.exception.ErrorMessage.INVALID_CASTING;
import static graql.lang.common.util.Strings.escapeRegex;
import static graql.lang.common.util.Strings.quoteString;
import static graql.lang.pattern.variable.UnboundVariable.hidden;
import static java.util.stream.Collectors.joining;

public abstract class TypeConstraint extends Constraint<TypeVariable> {

    @Override
    public Set<TypeVariable> variables() {
        return set();
    }

    @Override
    public boolean isType() {
        return true;
    }

    @Override
    public TypeConstraint asType() {
        return this;
    }

    public boolean isLabel() {
        return false;
    }

    public boolean isSub() {
        return false;
    }

    public boolean isAbstract() {
        return false;
    }

    public boolean isValueType() {
        return false;
    }

    public boolean isRegex() {
        return false;
    }

    public boolean isThen() {
        return false;
    }

    public boolean isWhen() {
        return false;
    }

    public boolean isOwns() {
        return false;
    }

    public boolean isPlays() {
        return false;
    }

    public boolean isRelates() {
        return false;
    }

    public TypeConstraint.Label asLabel() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Label.class)));
    }

    public TypeConstraint.Sub asSub() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Sub.class)));
    }

    public TypeConstraint.Abstract asAbstract() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Abstract.class)));
    }

    public TypeConstraint.ValueType asValueType() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(ValueType.class)));
    }

    public TypeConstraint.Regex asRegex() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Regex.class)));
    }

    public TypeConstraint.Then asThen() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Then.class)));
    }

    public TypeConstraint.When asWhen() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(When.class)));
    }

    public TypeConstraint.Owns asOwns() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Owns.class)));
    }

    public TypeConstraint.Plays asPlays() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Plays.class)));
    }

    public TypeConstraint.Relates asRelates() {
        throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(Relates.class)));
    }

    public static class Label extends TypeConstraint {

        private final String label;
        private final String scope;
        private final int hash;

        public Label(String label) {
            this(null, label);
        }

        public Label(@Nullable String scope, String label) {
            if (label == null) throw new NullPointerException("Null label");
            this.scope = scope;
            this.label = label;
            this.hash = Objects.hash(Label.class, this.scope, this.label);
        }

        public Optional<String> scope() {
            return Optional.ofNullable(scope);
        }

        public String label() {
            return label;
        }

        public String scopedLabel() {
            if (scope != null) return scope + COLON + label;
            else return label;
        }

        @Override
        public boolean isLabel() {
            return true;
        }

        @Override
        public TypeConstraint.Label asLabel() {
            return this;
        }

        @Override
        public String toString() {
            return TYPE.toString() + SPACE + scopedLabel();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Label that = (Label) o;
            return (Objects.equals(this.scope, that.scope) && this.label.equals(that.label));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Sub extends TypeConstraint {

        private final TypeVariable type;
        private final boolean isExplicit;
        private final int hash;

        public Sub(String typeLabe, boolean isExplicit) {
            this(hidden().type(typeLabe), isExplicit);
        }

        public Sub(String typeScope, String typeLabel, boolean isExplicit) {
            this(hidden().type(typeScope, typeLabel), isExplicit);
        }

        public Sub(UnboundVariable typeVar, boolean isExplicit) {
            this(typeVar.toType(), isExplicit);
        }

        public Sub(Either<Pair<String, String>, UnboundVariable> typeArg, boolean isExplicit) {
            this(typeArg.apply(scoped -> hidden().constrain(new TypeConstraint.Label(scoped.first(), scoped.second())),
                               UnboundVariable::toType), isExplicit);
        }

        private Sub(TypeVariable type, boolean isExplicit) {
            if (type == null) throw new NullPointerException("Null superType");
            this.type = type;
            this.isExplicit = isExplicit;
            this.hash = Objects.hash(Sub.class, this.type, this.isExplicit);
        }

        public TypeVariable type() {
            return type;
        }

        public boolean isExplicit() {
            return isExplicit;
        }

        @Override
        public Set<TypeVariable> variables() {
            return set(type);
        }

        @Override
        public boolean isSub() {
            return true;
        }

        @Override
        public TypeConstraint.Sub asSub() {
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

    public static class Abstract extends TypeConstraint {

        private final int hash;

        public Abstract() {
            this.hash = Objects.hash(Abstract.class);
        }

        @Override
        public boolean isAbstract() {
            return true;
        }

        @Override
        public TypeConstraint.Abstract asAbstract() {
            return this;
        }

        @Override
        public String toString() {
            return GraqlToken.Constraint.ABSTRACT.toString();
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

    public static class ValueType extends TypeConstraint {

        private final GraqlArg.ValueType valueType;
        private final int hash;

        public ValueType(GraqlArg.ValueType valueType) {
            if (valueType == null) throw new NullPointerException("Null ValueType");
            this.valueType = valueType;
            this.hash = Objects.hash(ValueType.class, this.valueType);
        }

        public GraqlArg.ValueType valueType() {
            return valueType;
        }

        @Override
        public boolean isValueType() {
            return true;
        }

        @Override
        public TypeConstraint.ValueType asValueType() {
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

    public static class Regex extends TypeConstraint {

        private final java.util.regex.Pattern regex;
        private final int hash;

        public Regex(String regex) {
            if (regex == null) throw new NullPointerException("Null regex");
            try {
                this.regex = java.util.regex.Pattern.compile(regex);
            } catch (PatternSyntaxException exception) {
                throw GraqlException.of(INVALID_ATTRIBUTE_TYPE_REGEX.message());
            }
            this.hash = Objects.hash(Regex.class, this.regex.pattern());
        }

        public java.util.regex.Pattern regex() {
            return regex;
        }

        @Override
        public boolean isRegex() {
            return true;
        }

        @Override
        public TypeConstraint.Regex asRegex() {
            return this;
        }

        @Override
        public String toString() {
            return REGEX.toString() + SPACE + quoteString(escapeRegex(regex().pattern()));
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Regex that = (Regex) o;
            return (this.regex.pattern().equals(that.regex.pattern()));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    // TODO: Move this out of TypeConstraint and create its own class
    public static class Then extends TypeConstraint {

        private final Pattern pattern;
        private final int hash;

        public Then(Pattern pattern) {
            if (pattern == null) throw new NullPointerException("Null pattern");
            this.pattern = pattern;
            this.hash = Objects.hash(Then.class, this.pattern);
        }

        public Pattern pattern() {
            return pattern;
        }

        @Override
        public boolean isThen() {
            return true;
        }

        @Override
        public TypeConstraint.Then asThen() {
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

    // TODO: Move this out of TypeConstraint and create its own class
    public static class When extends TypeConstraint {

        private final Pattern pattern;
        private final int hash;

        public When(Pattern pattern) {
            if (pattern == null) throw new NullPointerException("Null Pattern");
            this.pattern = pattern;
            this.hash = Objects.hash(When.class, this.pattern);
        }

        public Pattern pattern() {
            return pattern;
        }

        @Override
        public boolean isWhen() {
            return true;
        }

        @Override
        public TypeConstraint.When asWhen() {
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

    public static class Owns extends TypeConstraint {

        private final TypeVariable attributeType;
        private final TypeVariable overriddenAttributeType;
        private final boolean isKey;
        private final int hash;

        public Owns(String attributeType, boolean isKey) {
            this(hidden().type(attributeType), null, isKey);
        }

        public Owns(UnboundVariable attributeTypeVar, boolean isKey) {
            this(attributeTypeVar.toType(), null, isKey);
        }

        public Owns(String attributeType, String overriddenAttributeType, boolean isKey) {
            this(hidden().type(attributeType), overriddenAttributeType == null ? null : hidden().type(overriddenAttributeType), isKey);
        }

        public Owns(UnboundVariable attributeTypeVar, String overriddenAttributeType, boolean isKey) {
            this(attributeTypeVar.toType(), overriddenAttributeType == null ? null : hidden().type(overriddenAttributeType), isKey);
        }

        public Owns(String attributeType, UnboundVariable overriddenAttributeTypeVar, boolean isKey) {
            this(hidden().type(attributeType), overriddenAttributeTypeVar == null ? null : overriddenAttributeTypeVar.toType(), isKey);
        }

        public Owns(UnboundVariable attributeTypeVar, UnboundVariable overriddenAttributeTypeVar, boolean isKey) {
            this(attributeTypeVar.toType(), overriddenAttributeTypeVar == null ? null : overriddenAttributeTypeVar.toType(), isKey);
        }

        public Owns(Either<String, UnboundVariable> attributeTypeArg, Either<String, UnboundVariable> overriddenAttributeTypeArg, boolean isKey) {
            this(attributeTypeArg.apply(label -> hidden().type(label), UnboundVariable::toType),
                 overriddenAttributeTypeArg == null ? null : overriddenAttributeTypeArg.apply(label -> hidden().type(label), UnboundVariable::toType),
                 isKey);
        }

        private Owns(TypeVariable attributeType, @Nullable TypeVariable overriddenAttributeType, boolean isKey) {
            this.attributeType = attributeType;
            this.overriddenAttributeType = overriddenAttributeType;
            this.isKey = isKey;
            this.hash = Objects.hash(Owns.class, this.attributeType, this.overriddenAttributeType, this.isKey);
        }

        public TypeVariable attribute() {
            return attributeType;
        }

        public Optional<TypeVariable> overridden() {
            return Optional.ofNullable(overriddenAttributeType);
        }

        public boolean isKey() {
            return isKey;
        }

        @Override
        public Set<TypeVariable> variables() {
            return overriddenAttributeType == null
                    ? set(attributeType)
                    : set(attributeType, overriddenAttributeType);
        }

        @Override
        public boolean isOwns() {
            return true;
        }

        @Override
        public TypeConstraint.Owns asOwns() {
            return this;
        }

        @Override
        public String toString() {
            return OWNS.toString() + SPACE + attributeType + (isKey ? "" + SPACE + IS_KEY : "");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Owns that = (Owns) o;
            return (this.attributeType.equals(that.attributeType) &&
                    Objects.equals(this.overriddenAttributeType, that.overriddenAttributeType) &&
                    this.isKey == that.isKey);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Plays extends TypeConstraint {

        private final TypeVariable roleType;
        private final TypeVariable relationType;
        private final TypeVariable overriddenRoleType;
        private final int hash;

        public Plays(String relationType, String roleType) {
            this(hidden().type(relationType, roleType), null);
        }

        public Plays(UnboundVariable var) {
            this(var.toType(), null);
        }

        public Plays(String relationType, String roleType, String overriddenRoleType) {
            this(hidden().type(relationType, roleType), overriddenRoleType == null ? null : hidden().type(overriddenRoleType));
        }

        public Plays(UnboundVariable roleTypeVar, String overriddenRoleType) {
            this(roleTypeVar.toType(), overriddenRoleType == null ? null : hidden().type(overriddenRoleType));
        }

        public Plays(String relationType, String roleType, UnboundVariable overriddenRoleTypeVar) {
            this(hidden().type(relationType, roleType), overriddenRoleTypeVar == null ? null : overriddenRoleTypeVar.toType());
        }

        public Plays(UnboundVariable roleTypeVar, UnboundVariable overriddenRoleTypeVar) {
            this(roleTypeVar.toType(), overriddenRoleTypeVar == null ? null : overriddenRoleTypeVar.toType());
        }

        public Plays(Either<Pair<String, String>, UnboundVariable> roleTypeArg, Either<String, UnboundVariable> overriddenRoleTypeArg) {
            this(roleTypeArg.apply(scoped -> hidden().constrain(new TypeConstraint.Label(scoped.first(), scoped.second())), UnboundVariable::toType),
                 overriddenRoleTypeArg == null ? null : overriddenRoleTypeArg.apply(label -> hidden().type(label), UnboundVariable::toType));
        }

        private Plays(TypeVariable roleType, @Nullable TypeVariable overriddenRoleType) {
            if (roleType == null) throw new NullPointerException("Null role");
            this.relationType = roleType.label().map(l -> hidden().type(l.scope().get())).orElse(null);
            this.roleType = roleType;
            this.overriddenRoleType = overriddenRoleType;
            this.hash = Objects.hash(Plays.class, this.relationType, this.roleType, this.overriddenRoleType);
        }

        public Optional<TypeVariable> relation() {
            return Optional.ofNullable(relationType);
        }

        public TypeVariable role() {
            return roleType;
        }

        public Optional<TypeVariable> overridden() {
            return Optional.ofNullable(overriddenRoleType);
        }

        @Override
        public Set<TypeVariable> variables() {
            Set<TypeVariable> variables = new HashSet<>();
            variables.add(roleType);
            if (relationType != null) variables.add(relationType);
            if (overriddenRoleType != null) variables.add(overriddenRoleType);
            return variables;
        }

        @Override
        public boolean isPlays() {
            return true;
        }

        @Override
        public TypeConstraint.Plays asPlays() {
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
            return (this.roleType.equals(that.roleType) &&
                    Objects.equals(this.relationType, that.relationType) &&
                    Objects.equals(this.overriddenRoleType, that.overriddenRoleType));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Relates extends TypeConstraint {

        private TypeVariable roleType;
        private TypeVariable overriddenRoleType;

        public Relates(String roleType) {
            this(hidden().type(roleType), null);
        }

        public Relates(UnboundVariable roleTypeVar) {
            this(roleTypeVar.toType(), null);
        }

        public Relates(String roleType, String overriddenRoleType) {
            this(hidden().type(roleType), overriddenRoleType == null ? null : hidden().type(overriddenRoleType));
        }

        public Relates(UnboundVariable roleTypeVar, String overriddenRoleType) {
            this(roleTypeVar.toType(), overriddenRoleType == null ? null : hidden().type(overriddenRoleType));
        }

        public Relates(String roleType, UnboundVariable overriddenRoleTypeVar) {
            this(hidden().type(roleType), overriddenRoleTypeVar == null ? null : overriddenRoleTypeVar.toType());
        }

        public Relates(UnboundVariable roleTypeVar, UnboundVariable overriddenRoleTypeVar) {
            this(roleTypeVar.toType(), overriddenRoleTypeVar == null ? null : overriddenRoleTypeVar.toType());
        }

        public Relates(Either<String, UnboundVariable> roleTypeArg, Either<String, UnboundVariable> overriddenRoleTypeArg) {
            this(roleTypeArg.apply(label -> hidden().type(label), UnboundVariable::toType),
                 overriddenRoleTypeArg == null ? null : overriddenRoleTypeArg.apply(label -> hidden().type(label), UnboundVariable::toType));
        }

        private Relates(TypeVariable roleType, @Nullable TypeVariable overriddenRoleType) {
            if (roleType == null) throw new NullPointerException("Null role");
            this.roleType = roleType;
            this.overriddenRoleType = overriddenRoleType;
        }

        public void setScope(String relationLabel) {
            if (roleType.label().isPresent()) {
                this.roleType = hidden().type(relationLabel, roleType.label().get().label());
            }
            if (overriddenRoleType != null && overriddenRoleType.label().isPresent()) {
                this.overriddenRoleType = hidden().type(relationLabel, overriddenRoleType.label().get().label());
            }
        }

        public TypeVariable role() {
            return roleType;
        }

        public Optional<TypeVariable> overridden() {
            return Optional.ofNullable(overriddenRoleType);
        }

        @Override
        public Set<TypeVariable> variables() {
            return overriddenRoleType == null ? set(roleType) : set(roleType, overriddenRoleType);
        }

        @Override
        public boolean isRelates() {
            return true;
        }

        @Override
        public TypeConstraint.Relates asRelates() {
            return this;
        }

        @Override
        public String toString() {
            StringBuilder syntax = new StringBuilder();
            syntax.append(RELATES).append(SPACE);
            if (!roleType.label().isPresent()) syntax.append(roleType);
            else syntax.append(roleType.label().get().label());
            if (overriddenRoleType != null) {
                syntax.append(SPACE).append(AS).append(SPACE);
                if (!overriddenRoleType.label().isPresent()) syntax.append(overriddenRoleType);
                else syntax.append(overriddenRoleType.label().get().label());
            }
            return syntax.toString();
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
            return Objects.hash(Relates.class, roleType, overriddenRoleType);
        }
    }
}
