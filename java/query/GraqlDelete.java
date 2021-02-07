/*
 * Copyright (C) 2021 Grakn Labs
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

package graql.lang.query;

import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.variable.ThingVariable;

import java.util.List;

import static grakn.common.collection.Collections.list;
import static graql.lang.common.GraqlToken.Command.DELETE;
import static graql.lang.common.exception.ErrorMessage.VARIABLE_OUT_OF_SCOPE_DELETE;
import static java.util.Objects.requireNonNull;

public class GraqlDelete extends GraqlWritable.InsertOrDelete {

    GraqlDelete(GraqlMatch.Unfiltered match, List<ThingVariable<?>> variables) {
        super(DELETE, requireNonNull(match), validDeleteVars(match, variables));
    }

    static List<ThingVariable<?>> validDeleteVars(GraqlMatch.Unfiltered match, List<ThingVariable<?>> variables) {
        variables.forEach(var -> {
            if (var.isNamed() && !match.namedVariablesUnbound().contains(var.toUnbound())) {
                throw GraqlException.of(VARIABLE_OUT_OF_SCOPE_DELETE.message(var.reference()));
            }
            var.variables().forEach(nestedVar -> {
                if (nestedVar.isNamed() && !match.namedVariablesUnbound().contains(nestedVar.toUnbound())) {
                    throw GraqlException.of(VARIABLE_OUT_OF_SCOPE_DELETE.message(nestedVar.reference()));
                }
            });
        });
        return variables;
    }

    public GraqlMatch.Unfiltered match() {
        assert match != null;
        return match;
    }

    public List<ThingVariable<?>> variables() { return variables; }

    public GraqlUpdate insert(ThingVariable<?>... things) {
        return insert(list(things));
    }

    public GraqlUpdate insert(List<ThingVariable<?>> things) {
        return new GraqlUpdate(this.match(), variables, things);
    }
}
