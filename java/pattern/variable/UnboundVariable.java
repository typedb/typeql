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

import graql.lang.pattern.constraint.Constraint;
import graql.lang.pattern.constraint.ThingConstraint;
import graql.lang.pattern.constraint.TypeConstraint;
import graql.lang.pattern.variable.builder.ThingVariableBuilder;
import graql.lang.pattern.variable.builder.TypeVariableBuilder;

import java.util.Collections;
import java.util.List;

public class UnboundVariable extends Variable implements TypeVariableBuilder,
                                                         ThingVariableBuilder.Common<ThingVariable.Thing>,
                                                         ThingVariableBuilder.Thing,
                                                         ThingVariableBuilder.Relation,
                                                         ThingVariableBuilder.Attribute {

    UnboundVariable(final Reference reference) {
        super(reference);
    }

    public static UnboundVariable of(final Reference reference) {
        return new UnboundVariable(reference);
    }

    public static UnboundVariable named(final String name) {
        return of(Reference.named(name));
    }

    public static UnboundVariable anonymous() {
        return of(Reference.anonymous(true));
    }

    public static UnboundVariable hidden() {
        return of(Reference.anonymous(false));
    }

    public TypeVariable toType() {
        return new TypeVariable(reference);
    }

    public ThingVariable<?> toThing() {
        return new ThingVariable.Thing(reference);
    }

    @Override
    public List<Constraint<?>> constraints() {
        return Collections.emptyList();
    }

    @Override
    public TypeVariable constrain(final TypeConstraint.Label constraint) {
        return new TypeVariable(Reference.label(constraint.scopedLabel())).constrain(constraint);
    }

    @Override
    public TypeVariable constrain(final TypeConstraint.Sub constraint) {
        return new TypeVariable(reference).constrain(constraint);
    }

    @Override
    public TypeVariable constrain(final TypeConstraint.Abstract constraint) {
        return new TypeVariable(reference).constrain(constraint);
    }

    @Override
    public TypeVariable constrain(final TypeConstraint.ValueType constraint) {
        return new TypeVariable(reference).constrain(constraint);
    }

    @Override
    public TypeVariable constrain(final TypeConstraint.Regex constraint) {
        return new TypeVariable(reference).constrain(constraint);
    }

    @Override
    public TypeVariable constrain(final TypeConstraint.Then constraint) {
        return new TypeVariable(reference).constrain(constraint);
    }

    @Override
    public TypeVariable constrain(final TypeConstraint.When constraint) {
        return new TypeVariable(reference).constrain(constraint);
    }

    @Override
    public TypeVariable constrain(final TypeConstraint.Owns constraint) {
        return new TypeVariable(reference).constrain(constraint);
    }

    @Override
    public TypeVariable constrain(final TypeConstraint.Plays constraint) {
        return new TypeVariable(reference).constrain(constraint);
    }

    @Override
    public TypeVariable constrain(final TypeConstraint.Relates constraint) {
        return new TypeVariable(reference).constrain(constraint);
    }

    @Override
    public ThingVariable.Thing constrain(final ThingConstraint.Isa constraint) {
        return new ThingVariable.Thing(reference).constrain(constraint);
    }

    @Override
    public ThingVariable.Thing constrain(final ThingConstraint.Has constraint) {
        return new ThingVariable.Thing(reference).constrain(constraint);
    }

    @Override
    public ThingVariable.Thing constrain(final ThingConstraint.IID constraint) {
        return new ThingVariable.Thing(reference, constraint);
    }

    @Override
    public ThingVariable.Thing constrain(final ThingConstraint.NEQ constraint) {
        return new ThingVariable.Thing(reference, constraint);
    }

    @Override
    public ThingVariable.Attribute constrain(final ThingConstraint.Value<?> constraint) {
        return new ThingVariable.Attribute(reference, constraint);
    }

    @Override
    public ThingVariable.Relation constrain(final ThingConstraint.Relation.RolePlayer rolePlayer) {
        return constrain(new ThingConstraint.Relation(rolePlayer));
    }

    public ThingVariable.Relation constrain(final ThingConstraint.Relation constraint) {
        return new ThingVariable.Relation(reference, constraint);
    }

    @Override
    public String toString() {
        return reference.syntax();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final UnboundVariable that = (UnboundVariable) o;
        return this.reference.equals(that.reference);
    }

    @Override
    public int hashCode() {
        return reference.hashCode();
    }
}
