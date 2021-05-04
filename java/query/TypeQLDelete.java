/*
 * Copyright (C) 2021 Vaticle
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

package com.vaticle.typeql.lang.query;

import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;

import java.util.List;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.common.TypeQLToken.Command.DELETE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.VARIABLE_OUT_OF_SCOPE_DELETE;
import static java.util.Objects.requireNonNull;

public class TypeQLDelete extends TypeQLWritable.InsertOrDelete {

    TypeQLDelete(TypeQLMatch.Unfiltered match, List<ThingVariable<?>> variables) {
        super(DELETE, requireNonNull(match), validDeleteVars(match, variables));
    }

    static List<ThingVariable<?>> validDeleteVars(TypeQLMatch.Unfiltered match, List<ThingVariable<?>> variables) {
        variables.forEach(var -> {
            if (var.isNamed() && !match.namedVariablesUnbound().contains(var.toUnbound())) {
                throw TypeQLException.of(VARIABLE_OUT_OF_SCOPE_DELETE.message(var.reference()));
            }
            var.variables().forEach(nestedVar -> {
                if (nestedVar.isNamed() && !match.namedVariablesUnbound().contains(nestedVar.toUnbound())) {
                    throw TypeQLException.of(VARIABLE_OUT_OF_SCOPE_DELETE.message(nestedVar.reference()));
                }
            });
        });
        return variables;
    }

    public TypeQLMatch.Unfiltered match() {
        assert match != null;
        return match;
    }

    public List<ThingVariable<?>> variables() { return variables; }

    public TypeQLUpdate insert(ThingVariable<?>... things) {
        return insert(list(things));
    }

    public TypeQLUpdate insert(List<ThingVariable<?>> things) {
        return new TypeQLUpdate(this.match(), variables, things);
    }
}
