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
import graql.lang.pattern.constraint.Constraint;
import graql.lang.pattern.constraint.TypeConstraint;
import graql.lang.pattern.variable.builder.TypeVariableBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static grakn.common.collection.Collections.set;
import static graql.lang.common.GraqlToken.Char.COMMA_SPACE;
import static graql.lang.common.GraqlToken.Char.SPACE;
import static graql.lang.common.exception.ErrorMessage.ILLEGAL_CONSTRAINT_REPETITION;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class TypeVariable extends BoundVariable implements TypeVariableBuilder {

    private final Map<Class<? extends TypeConstraint>, TypeConstraint.Singular> singular;
    private final Map<Class<? extends TypeConstraint>, List<TypeConstraint.Repeatable>> repeating;
    private final List<TypeConstraint> ordered;

    TypeVariable(Reference reference, TypeConstraint constraint) {
        super(reference);
        this.singular = new HashMap<>();
        this.repeating = new HashMap<>();
        this.ordered = new ArrayList<>();
        if (constraint != null) {
            if (constraint.isSingular()) asTypeWith(constraint.asSingular());
            else asTypeWith(constraint.asRepeatable());
        }
    }

    private TypeVariable(Reference reference,
                         Map<Class<? extends TypeConstraint>, TypeConstraint.Singular> singular,
                         Map<Class<? extends TypeConstraint>, List<TypeConstraint.Repeatable>> repeating,
                         List<TypeConstraint> ordered) {
        super(reference);
        this.singular = new HashMap<>(singular);
        this.repeating = new HashMap<>(repeating);
        this.ordered = new ArrayList<>(ordered);
    }

    @Override
    public Stream<TypeConstraint> properties() {
        return ordered.stream();
    }

    @Override
    public boolean isType() {
        return true;
    }

    @Override
    public TypeVariable asType() {
        return this;
    }

    private void addSingularProperties(TypeConstraint.Singular constraint) {
        if (singular.containsKey(constraint.getClass()) && !singular.get(constraint.getClass()).equals(constraint)) {
            throw GraqlException.of(ILLEGAL_CONSTRAINT_REPETITION.message(reference, singular.get(constraint.getClass()), constraint));
        } else if (!singular.containsKey(constraint.getClass())) {
            singular.put(constraint.getClass(), constraint);
        }
    }

    TypeVariable merge(TypeVariable variable) {
        TypeVariable merged = new TypeVariable(reference, singular, repeating, ordered);
        variable.singular.values().forEach(constraint -> {
            merged.addSingularProperties(constraint);
            merged.ordered.add(constraint);
        });
        variable.repeating.forEach((clazz, list) -> {
            merged.repeating.computeIfAbsent(clazz, c -> new ArrayList<>()).addAll(list);
            merged.ordered.addAll(list);
        });
        return merged;
    }

    @Override
    public TypeVariable asTypeWith(TypeConstraint.Singular constraint) {
        addSingularProperties(constraint);
        ordered.add(constraint);
        return this;
    }

    @Override
    public TypeVariable asTypeWith(TypeConstraint.Repeatable constraint) {
        if (label().isPresent() && constraint instanceof TypeConstraint.Relates) {
            ((TypeConstraint.Relates) constraint).setScope(label().get().label());
        }

        repeating.computeIfAbsent(constraint.getClass(), c -> new ArrayList<>()).add(constraint);
        ordered.add(constraint);
        return this;
    }

    public Optional<TypeConstraint.Label> label() {
        return Optional.ofNullable(singular.get(TypeConstraint.Label.class)).map(TypeConstraint::asLabel);
    }

    public Optional<TypeConstraint.Sub> sub() {
        return Optional.ofNullable(singular.get(TypeConstraint.Sub.class)).map(TypeConstraint::asSub);
    }

    public Optional<TypeConstraint.Abstract> abstractFlag() {
        return Optional.ofNullable(singular.get(TypeConstraint.Abstract.class)).map(TypeConstraint::asAbstract);
    }

    public Optional<TypeConstraint.ValueType> valueType() {
        return Optional.ofNullable(singular.get(TypeConstraint.ValueType.class)).map(TypeConstraint::asValueType);
    }

    public Optional<TypeConstraint.Regex> regex() {
        return Optional.ofNullable(singular.get(TypeConstraint.Regex.class)).map(TypeConstraint::asRegex);
    }

    public Optional<TypeConstraint.Then> then() {
        return Optional.ofNullable(singular.get(TypeConstraint.Then.class)).map(TypeConstraint::asThen);
    }

    public Optional<TypeConstraint.When> when() {
        return Optional.ofNullable(singular.get(TypeConstraint.When.class)).map(TypeConstraint::asWhen);
    }

    public List<TypeConstraint.Owns> owns() {
        return repeating.computeIfAbsent(TypeConstraint.Owns.class, c -> new ArrayList<>())
                .stream().map(TypeConstraint::asOwns).collect(toList());
    }

    public List<TypeConstraint.Plays> plays() {
        return repeating.computeIfAbsent(TypeConstraint.Plays.class, c -> new ArrayList<>())
                .stream().map(TypeConstraint::asPlays).collect(toList());
    }

    public List<TypeConstraint.Relates> relates() {
        return repeating.computeIfAbsent(TypeConstraint.Relates.class, c -> new ArrayList<>())
                .stream().map(TypeConstraint::asRelates).collect(toList());
    }

    @Override
    public String toString() {
        StringBuilder syntax = new StringBuilder();

        if (isVisible()) {
            syntax.append(reference.syntax());
            if (!ordered.isEmpty()) {
                syntax.append(SPACE);
                syntax.append(ordered.stream().map(Constraint::toString).collect(joining(COMMA_SPACE.toString())));
            }
        } else if (label().isPresent()) {
            syntax.append(label().get().scopedLabel());
            if (ordered.size() > 1) {
                syntax.append(SPACE).append(ordered.stream().filter(p -> !(p instanceof TypeConstraint.Label))
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
                set(this.ordered).equals(set(that.ordered)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.reference, set(this.ordered));
    }
}
