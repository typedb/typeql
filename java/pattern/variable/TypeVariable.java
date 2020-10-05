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

package graql.lang.pattern.variable;

import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.Definable;
import graql.lang.pattern.constraint.Constraint;
import graql.lang.pattern.constraint.TypeConstraint;
import graql.lang.pattern.variable.builder.TypeVariableBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static grakn.common.collection.Collections.set;
import static graql.lang.common.GraqlToken.Char.COMMA_SPACE;
import static graql.lang.common.GraqlToken.Char.SPACE;
import static graql.lang.common.exception.ErrorMessage.ILLEGAL_CONSTRAINT_REPETITION;
import static java.util.stream.Collectors.joining;

public class TypeVariable extends BoundVariable implements TypeVariableBuilder, Definable {

    private TypeConstraint.Label labelConstraint;
    private TypeConstraint.Sub subConstraint;
    private TypeConstraint.Abstract abstractConstraint;
    private TypeConstraint.ValueType valueTypeConstraint;
    private TypeConstraint.Regex regexConstraint;

    private final List<TypeConstraint.Owns> ownsConstraints;
    private final List<TypeConstraint.Plays> playsConstraints;
    private final List<TypeConstraint.Relates> relatesConstraints;

    private final List<TypeConstraint> constraints;

    TypeVariable(Reference reference) {
        super(reference);
        this.ownsConstraints = new LinkedList<>();
        this.playsConstraints = new LinkedList<>();
        this.relatesConstraints = new LinkedList<>();
        this.constraints = new LinkedList<>();
    }

    @Override
    public List<TypeConstraint> constraints() {
        return constraints;
    }

    @Override
    public boolean isType() {
        return true;
    }

    @Override
    public TypeVariable asType() {
        return this;
    }

    @Override
    public TypeVariable constrain(TypeConstraint.Label constraint) {
        if (labelConstraint != null) {
            throw GraqlException.of(ILLEGAL_CONSTRAINT_REPETITION.message(reference, TypeConstraint.Label.class, constraint));
        }
        labelConstraint = constraint;
        constraints.add(constraint);
        return this;
    }

    @Override
    public TypeVariable constrain(TypeConstraint.Sub constraint) {
        if (subConstraint != null) {
            throw GraqlException.of(ILLEGAL_CONSTRAINT_REPETITION.message(reference, TypeConstraint.Sub.class, constraint));
        }
        subConstraint = constraint;
        constraints.add(constraint);
        return this;
    }

    @Override
    public TypeVariable constrain(TypeConstraint.Abstract constraint) {
        if (abstractConstraint != null) {
            throw GraqlException.of(ILLEGAL_CONSTRAINT_REPETITION.message(reference, TypeConstraint.Abstract.class, constraint));
        }
        abstractConstraint = constraint;
        constraints.add(constraint);
        return this;
    }

    @Override
    public TypeVariable constrain(TypeConstraint.ValueType constraint) {
        if (valueTypeConstraint != null) {
            throw GraqlException.of(ILLEGAL_CONSTRAINT_REPETITION.message(reference, TypeConstraint.ValueType.class, constraint));
        }
        valueTypeConstraint = constraint;
        constraints.add(constraint);
        return this;
    }

    @Override
    public TypeVariable constrain(TypeConstraint.Regex constraint) {
        if (regexConstraint != null) {
            throw GraqlException.of(ILLEGAL_CONSTRAINT_REPETITION.message(reference, TypeConstraint.Regex.class, constraint));
        }
        regexConstraint = constraint;
        constraints.add(constraint);
        return this;
    }


    @Override
    public TypeVariable constrain(TypeConstraint.Owns constraint) {
        ownsConstraints.add(constraint);
        constraints.add(constraint);
        return this;
    }

    @Override
    public TypeVariable constrain(TypeConstraint.Plays constraint) {
        playsConstraints.add(constraint);
        constraints.add(constraint);
        return this;
    }

    @Override
    public TypeVariable constrain(TypeConstraint.Relates constraint) {
        if (label().isPresent()) {
            constraint.setScope(label().get().label());
        }
        relatesConstraints.add(constraint);
        constraints.add(constraint);
        return this;
    }

    public Optional<TypeConstraint.Label> label() {
        return Optional.ofNullable(labelConstraint);
    }

    public Optional<TypeConstraint.Sub> sub() {
        return Optional.ofNullable(subConstraint);
    }

    public Optional<TypeConstraint.Abstract> abstractConstraint() {
        return Optional.ofNullable(abstractConstraint);
    }

    public Optional<TypeConstraint.ValueType> valueType() {
        return Optional.ofNullable(valueTypeConstraint);
    }

    public Optional<TypeConstraint.Regex> regex() {
        return Optional.ofNullable(regexConstraint);
    }

    public List<TypeConstraint.Owns> owns() {
        return ownsConstraints;
    }

    public List<TypeConstraint.Plays> plays() {
        return playsConstraints;
    }

    public List<TypeConstraint.Relates> relates() {
        return relatesConstraints;
    }

    @Override
    public String toString() {
        StringBuilder syntax = new StringBuilder();

        if (isVisible()) {
            syntax.append(reference.syntax());
            if (!constraints.isEmpty()) {
                syntax.append(SPACE);
                syntax.append(constraints.stream().map(Constraint::toString).collect(joining(COMMA_SPACE.toString())));
            }
        } else if (label().isPresent()) {
            syntax.append(label().get().scopedLabel());
            if (constraints.size() > 1) {
                syntax.append(SPACE).append(constraints.stream().filter(p -> !(p instanceof TypeConstraint.Label))
                                                    .map(Constraint::toString).collect(joining(COMMA_SPACE.toString())));
            }
        } else {
            // This should only be called by debuggers trying to print nested variables
            syntax.append(reference);
        }
        return syntax.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeVariable that = (TypeVariable) o;
        return (this.reference.equals(that.reference) &&
                set(this.constraints).equals(set(that.constraints)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.reference, set(this.constraints));
    }

    @Override
    public boolean isTypeVariable() {
        return true;
    }
}
