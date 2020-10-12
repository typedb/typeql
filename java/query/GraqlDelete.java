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

package graql.lang.query;

import graql.lang.common.GraqlToken;
import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.variable.ThingVariable;

import java.util.List;

import static graql.lang.common.exception.ErrorMessage.VARIABLE_OUT_OF_SCOPE;
import static java.util.Objects.requireNonNull;

public class GraqlDelete extends GraqlWritable {

    GraqlDelete(final GraqlMatch.Unfiltered match, final List<ThingVariable<?>> variables) {
        super(GraqlToken.Command.DELETE, requireNonNull(match), validVariables(match, variables));
    }

    static List<ThingVariable<?>> validVariables(final GraqlMatch.Unfiltered match, final List<ThingVariable<?>> variables) {
        variables.forEach(var -> {
            if (var.isNamed() && !match.variablesNamedUnbound().contains(var.toUnbound())) {
                throw GraqlException.of(VARIABLE_OUT_OF_SCOPE.message(var.reference()));
            }
            var.variables().forEach(nestedVar -> {
                if (nestedVar.isNamed() && !match.variablesNamedUnbound().contains(nestedVar.toUnbound())) {
                    throw GraqlException.of(VARIABLE_OUT_OF_SCOPE.message(nestedVar.reference()));
                }
            });
        });
        return variables;
    }

    public GraqlMatch.Unfiltered match() {
        assert super.nullableMatch() != null;
        return super.nullableMatch();
    }
}
