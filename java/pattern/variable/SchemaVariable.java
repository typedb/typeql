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

import grakn.common.collection.Collections;
import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.constraint.Constraint;
import graql.lang.pattern.constraint.SchemaConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static graql.lang.common.exception.ErrorMessage.ILLEGAL_CONSTRAINT_REPETITION;

/*
TODO we still need the builder to add `when` and `then`

we could also make this parametrised, and have the SchemaVariable contain all the constraints for both
type and schema. It makes sense to put all Type and Schema constraints in one list, IF they are subtyping each other

Alternatively, we could have a flat structure with no subtyping
 */
public class SchemaVariable extends BoundVariable {

    private SchemaConstraint.Then thenConstraint;
    private SchemaConstraint.When whenConstraint;

    private final List<SchemaConstraint> constraints;

    SchemaVariable(Reference reference) {
        super(reference);
        constraints = new ArrayList<>();
    }

    @Override
    public SchemaVariable asSchema() {
        return this;
    }

    @Override
    public List<? extends SchemaConstraint> constraints() {
        return constraints;
    }

    public Optional<SchemaConstraint.Then> then() {
        return Optional.ofNullable(thenConstraint);
    }

    public Optional<SchemaConstraint.When> when() {
        return Optional.ofNullable(whenConstraint);
    }

//     TODO need builder to override from
//    @Override
    public SchemaVariable constrain(SchemaConstraint.Then constraint) {
        if (thenConstraint != null) {
            throw GraqlException.of(ILLEGAL_CONSTRAINT_REPETITION.message(reference, SchemaConstraint.Then.class, constraint));
        }
        thenConstraint = constraint;
        constraints.add(constraint);
        return this;
    }

//     TODO need builder to override from
//    @Override
    public SchemaVariable constrain(SchemaConstraint.When constraint) {
        if (whenConstraint != null) {
            throw GraqlException.of(ILLEGAL_CONSTRAINT_REPETITION.message(reference, SchemaConstraint.When.class, constraint));
        }
        whenConstraint = constraint;
        constraints.add(constraint);
        return this;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
