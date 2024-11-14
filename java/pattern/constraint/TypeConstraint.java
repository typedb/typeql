/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.typeql.lang.pattern.constraint;

import com.typedb.common.collection.Either;
import com.typedb.common.collection.Pair;
import com.typeql.lang.common.TypeQLArg;
import com.typeql.lang.common.TypeQLToken;
import com.typeql.lang.common.TypeQLToken.Annotation;
import com.typeql.lang.common.TypeQLVariable;
import com.typeql.lang.common.exception.TypeQLException;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import static com.typedb.common.collection.Collections.set;
import static com.typedb.common.util.Objects.className;
import static com.typeql.lang.common.TypeQLToken.Char.COLON;
import static com.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.typeql.lang.common.TypeQLToken.Constraint.AS;
import static com.typeql.lang.common.TypeQLToken.Constraint.OWNS;
import static com.typeql.lang.common.TypeQLToken.Constraint.PLAYS;
import static com.typeql.lang.common.TypeQLToken.Constraint.REGEX;
import static com.typeql.lang.common.TypeQLToken.Constraint.RELATES;
import static com.typeql.lang.common.TypeQLToken.Constraint.SUB;
import static com.typeql.lang.common.TypeQLToken.Constraint.SUBX;
import static com.typeql.lang.common.TypeQLToken.Constraint.TYPE;
import static com.typeql.lang.common.TypeQLToken.Constraint.VALUE_TYPE;
import static com.typeql.lang.common.TypeQLToken.Type.RELATION;
import static com.typeql.lang.common.exception.ErrorMessage.INVALID_ANNOTATION;
import static com.typeql.lang.common.exception.ErrorMessage.INVALID_ATTRIBUTE_TYPE_REGEX;
import static com.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;
import static com.typeql.lang.common.exception.ErrorMessage.MISSING_PATTERNS;
import static com.typeql.lang.common.util.Strings.escapeRegex;
import static com.typeql.lang.common.util.Strings.quoteString;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

public abstract class TypeConstraint extends Constraint {

    @Override
    public Set<TypeQLVariable.Concept> variables() {
        return emptySet();
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

    public boolean isOwns() {
        return false;
    }

    public boolean isPlays() {
        return false;
    }

    public boolean isRelates() {
        return false;
    }

    public List<Annotation> annotations() {
        return Collections.emptyList();
    }

    public TypeConstraint.Label asLabel() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Label.class)));
    }

    public TypeConstraint.Sub asSub() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Sub.class)));
    }

    public TypeConstraint.Abstract asAbstract() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Abstract.class)));
    }

    public TypeConstraint.ValueType asValueType() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(ValueType.class)));
    }

    public TypeConstraint.Regex asRegex() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Regex.class)));
    }

    public TypeConstraint.Owns asOwns() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Owns.class)));
    }

    public TypeConstraint.Plays asPlays() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Plays.class)));
    }

    public TypeConstraint.Relates asRelates() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Relates.class)));
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

        private final TypeQLVariable.Concept type;
        private final boolean isExplicit;
        private final int hash;

        public Sub(String typeLabel, boolean isExplicit) {
            this(TypeQLVariable.Concept.labelVar(typeLabel), isExplicit);
        }

        public Sub(String typeScope, String typeLabel, boolean isExplicit) {
            this(TypeQLVariable.Concept.labelVar(typeLabel, typeScope), isExplicit);
        }

        public Sub(Either<Pair<String, String>, ? extends TypeQLVariable.Concept> typeArg, boolean isExplicit) {
            this((TypeQLVariable.Concept) typeArg.apply(
                    scoped -> TypeQLVariable.Concept.labelVar(scoped.second(), scoped.first()),
                    v -> v
            ), isExplicit);
        }

        public Sub(TypeQLVariable.Concept type, boolean isExplicit) {
            if (type == null) throw new NullPointerException("Null superType");
            this.type = type;
            this.isExplicit = isExplicit;
            this.hash = Objects.hash(Sub.class, this.type, this.isExplicit);
        }

        public TypeQLVariable.Concept type() {
            return type;
        }

        public boolean isExplicit() {
            return isExplicit;
        }

        @Override
        public Set<TypeQLVariable.Concept> variables() {
            return singleton(type);
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
            return TypeQLToken.Constraint.ABSTRACT.toString();
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

        private final TypeQLArg.ValueType valueType;
        private final int hash;

        public ValueType(TypeQLArg.ValueType valueType) {
            if (valueType == null) throw new NullPointerException("Null ValueType");
            this.valueType = valueType;
            this.hash = Objects.hash(ValueType.class, this.valueType);
        }

        public TypeQLArg.ValueType valueType() {
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
                throw TypeQLException.of(INVALID_ATTRIBUTE_TYPE_REGEX.message());
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

    public static class Owns extends TypeConstraint {

        private static final Set<Annotation> VALID_ANNOTATIONS = set(Annotation.KEY, Annotation.UNIQUE);

        private final TypeQLVariable.Concept attributeType;
        private final TypeQLVariable.Concept overriddenAttributeType;
        private final List<Annotation> annotations;
        private final int hash;

        public Owns(String attributeType, Annotation... annotations) {
            this(TypeQLVariable.Concept.labelVar(attributeType), (TypeQLVariable.Concept) null, annotations);
        }

        public Owns(TypeQLVariable.Concept attributeTypeVar, Annotation... annotations) {
            this(attributeTypeVar, (TypeQLVariable.Concept) null, annotations);
        }

        public Owns(String attributeType, String overriddenAttributeType, Annotation... annotations) {
            this(TypeQLVariable.Concept.labelVar(attributeType), overriddenAttributeType == null ? null : TypeQLVariable.Concept.labelVar(overriddenAttributeType), annotations);
        }

        public Owns(TypeQLVariable.Concept attributeTypeVar, @Nullable String overriddenAttributeType, Annotation... annotations) {
            this(attributeTypeVar, overriddenAttributeType == null ? null : TypeQLVariable.Concept.labelVar(overriddenAttributeType), annotations);
        }

        public Owns(String attributeType, TypeQLVariable.Concept overriddenAttributeTypeVar, Annotation... annotations) {
            this(TypeQLVariable.Concept.labelVar(attributeType), overriddenAttributeTypeVar, annotations);
        }

        public Owns(Either<String, ? extends TypeQLVariable.Concept> attributeTypeArg, Either<String, ? extends TypeQLVariable.Concept> overriddenAttributeTypeArg, Annotation... annotations) {
            this((TypeQLVariable.Concept) attributeTypeArg.apply(TypeQLVariable.Concept::labelVar, v -> v),
                    overriddenAttributeTypeArg == null ? (TypeQLVariable.Concept) null : overriddenAttributeTypeArg.apply(TypeQLVariable.Concept::labelVar, v -> v),
                    annotations);
        }

        public Owns(TypeQLVariable.Concept attributeType, @Nullable TypeQLVariable.Concept overriddenAttributeType, Annotation... annotations) {
            this.attributeType = attributeType;
            this.overriddenAttributeType = overriddenAttributeType;
            validateAnnotations(annotations);
            this.annotations = List.of(annotations);
            this.hash = Objects.hash(Owns.class, this.attributeType, this.overriddenAttributeType, this.annotations);
        }

        private static void validateAnnotations(Annotation[] annotations) {
            for (Annotation annotation : annotations) {
                if (!VALID_ANNOTATIONS.contains(annotation)) {
                    throw TypeQLException.of(INVALID_ANNOTATION.message(annotation, "owns"));
                }
            }
        }

        public TypeQLVariable.Concept attribute() {
            return attributeType;
        }

        public Optional<TypeQLVariable.Concept> overridden() {
            return Optional.ofNullable(overriddenAttributeType);
        }

        @Override
        public Set<TypeQLVariable.Concept> variables() {
            return overriddenAttributeType == null
                    ? singleton(attributeType)
                    : set(attributeType, overriddenAttributeType);
        }

        @Override
        public boolean isOwns() {
            return true;
        }

        public TypeConstraint.Owns asOwns() {
            return this;
        }

        @Override
        public List<Annotation> annotations() {
            return annotations;
        }

        @Override
        public String toString() {
            return "" + OWNS + SPACE + attributeType +
                    (overriddenAttributeType != null ? "" + SPACE + AS + SPACE + overriddenAttributeType : "") +
                    (!annotations.isEmpty() ? SPACE + annotations.stream().map(Annotation::toString).collect(SPACE.joiner()) : "");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Owns that = (Owns) o;
            return (this.attributeType.equals(that.attributeType) &&
                    Objects.equals(this.overriddenAttributeType, that.overriddenAttributeType) &&
                    this.annotations.equals(that.annotations));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class Plays extends TypeConstraint {

        private final TypeQLVariable.Concept roleType;
        private final TypeQLVariable.Concept relationType;
        private final TypeQLVariable.Concept overriddenRoleType;
        private final int hash;

        public Plays(String relationType, String roleType) {
            this(TypeQLVariable.Concept.labelVar(roleType, relationType), (TypeQLVariable.Concept) null);
        }

        public Plays(TypeQLVariable.Concept var) {
            this(var, (TypeQLVariable.Concept) null);
        }

        public Plays(String relationType, String roleType, @Nullable String overriddenRoleType) {
            this(TypeQLVariable.Concept.labelVar(roleType, relationType), overriddenRoleType == null ? null : scopedType(overriddenRoleType));
        }

        public Plays(TypeQLVariable.Concept roleTypeVar, @Nullable String overriddenRoleType) {
            this(roleTypeVar, overriddenRoleType == null ? null : scopedType(overriddenRoleType));
        }

        public Plays(String relationType, String roleType, @Nullable TypeQLVariable.Concept overriddenRoleTypeVar) {
            this(TypeQLVariable.Concept.labelVar(roleType, relationType), overriddenRoleTypeVar);
        }

        public Plays(Either<Pair<String, String>, ? extends TypeQLVariable.Concept> roleTypeArg, Either<String, ? extends TypeQLVariable.Concept> overriddenRoleTypeArg) {
            this(roleTypeArg.apply(
                    scoped -> TypeQLVariable.Concept.labelVar(scoped.second(), scoped.first()),
                    v -> v
            ), overriddenRoleTypeArg == null ? null : overriddenRoleTypeArg.apply(Plays::scopedType, v -> v));
        }

        public Plays(TypeQLVariable.Concept roleType, @Nullable TypeQLVariable.Concept overriddenRoleType) {
            if (roleType == null) throw TypeQLException.of(MISSING_PATTERNS.message());
            this.relationType = roleType.reference().isLabel() ? roleType.reference().asLabel().scope().map(TypeQLVariable.Concept::labelVar).orElse(null) : null;
            this.roleType = roleType;
            this.overriddenRoleType = overriddenRoleType;
            this.hash = Objects.hash(Plays.class, this.relationType, this.roleType, this.overriddenRoleType);
        }

        public Optional<TypeQLVariable.Concept> relation() {
            return Optional.ofNullable(relationType);
        }

        public TypeQLVariable.Concept role() {
            return roleType;
        }

        public Optional<TypeQLVariable.Concept> overridden() {
            return Optional.ofNullable(overriddenRoleType);
        }

        private static TypeQLVariable.Concept scopedType(String roleType) {
            return TypeQLVariable.Concept.labelVar(roleType, RELATION.toString());
        }

        @Override
        public Set<TypeQLVariable.Concept> variables() {
            Set<TypeQLVariable.Concept> variables = new HashSet<>();
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
            String syntax = PLAYS.toString() + SPACE + roleType;
            if (overriddenRoleType != null) {
                String overriddenRoleTypeString = overriddenRoleType.isLabelled() ?
                        overriddenRoleType.reference().asLabel().label() :
                        overriddenRoleType.toString();
                syntax += "" + SPACE + AS + SPACE + overriddenRoleTypeString;
            }
            return syntax;
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

        private TypeQLVariable.Concept roleType;
        private TypeQLVariable.Concept overriddenRoleType;

        public Relates(String roleType) {
            this(scopedType(roleType), (TypeQLVariable.Concept) null);
        }

        public Relates(TypeQLVariable.Concept roleTypeVar) {
            this(roleTypeVar, (TypeQLVariable.Concept) null);
        }

        public Relates(String roleType, @Nullable String overriddenRoleType) {
            this(scopedType(roleType), overriddenRoleType == null ? null : scopedType(overriddenRoleType));
        }

        public Relates(TypeQLVariable.Concept roleTypeVar, @Nullable String overriddenRoleType) {
            this(roleTypeVar, overriddenRoleType == null ? null : scopedType(overriddenRoleType));
        }

        public Relates(String roleType, @Nullable TypeQLVariable.Concept overriddenRoleTypeVar) {
            this(scopedType(roleType), overriddenRoleTypeVar);
        }

        public Relates(Either<String, ? extends TypeQLVariable.Concept> roleTypeArg, @Nullable Either<String, ? extends TypeQLVariable.Concept> overriddenRoleTypeArg) {
            this(roleTypeArg.apply(Relates::scopedType, v -> v),
                    overriddenRoleTypeArg == null ? null : overriddenRoleTypeArg.apply(Relates::scopedType, v -> v));
        }

        public Relates(TypeQLVariable.Concept roleType, @Nullable TypeQLVariable.Concept overriddenRoleType) {
            if (roleType == null) throw new NullPointerException("Null role");
            this.roleType = roleType;
            this.overriddenRoleType = overriddenRoleType;
        }

        private static TypeQLVariable.Concept scopedType(String roleType) {
            return TypeQLVariable.Concept.labelVar(roleType, RELATION.toString());
        }

        public void setScope(String relationLabel) {
            if (roleType.isLabelled() && roleType.reference().asLabel().scope().isPresent()) {
                this.roleType = TypeQLVariable.Concept.labelVar(roleType.reference().asLabel().scope().get(), relationLabel);
            }
            if (overriddenRoleType != null && overriddenRoleType.isLabelled() && overriddenRoleType.reference().asLabel().scope().isPresent()) {
                this.overriddenRoleType = TypeQLVariable.Concept.labelVar(overriddenRoleType.reference().asLabel().scope().get(), relationLabel);
            }
        }

        public TypeQLVariable.Concept role() {
            return roleType;
        }

        public Optional<TypeQLVariable.Concept> overridden() {
            return Optional.ofNullable(overriddenRoleType);
        }

        @Override
        public Set<TypeQLVariable.Concept> variables() {
            return overriddenRoleType == null ? singleton(roleType) : set(roleType, overriddenRoleType);
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
            if (!roleType.isLabelled()) syntax.append(roleType);
            else syntax.append(roleType.reference().asLabel().label());
            if (overriddenRoleType != null) {
                syntax.append(SPACE).append(AS).append(SPACE);
                if (!overriddenRoleType.isLabelled()) syntax.append(overriddenRoleType);
                else syntax.append(overriddenRoleType.reference().asLabel().label());
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
