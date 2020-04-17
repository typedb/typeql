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

import graql.lang.Graql;
import graql.lang.exception.GraqlException;
import graql.lang.query.builder.Filterable;
import graql.lang.statement.Statement;
import graql.lang.statement.Variable;

import javax.annotation.CheckReturnValue;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

/**
 * A query for deleting concepts from a match clause clause.
 * The delete operation to perform is based on what Statement objects
 * are provided to it. Only those properties will be deleted.
 */
public class GraqlDelete extends GraqlQuery {

    private final MatchClause match;
    private final List<Statement> delete;

    public GraqlDelete(MatchClause match, List<Statement> delete) {
        if (match == null) {
            throw new NullPointerException("Null match");
        }
        this.match = match;

        if (delete == null) {
            throw new NullPointerException("Null delete");
        }

        delete.forEach(statement ->{
            for (Variable var : statement.variables()) {
                if (!match.getSelectedNames().contains(var)) {
                    throw GraqlException.deleteVariableUnbound(var.toString());
                }
            }
        });
        this.delete = delete;
    }

    @CheckReturnValue
    public MatchClause match() {
        return match;
    }

    public List<Statement> statements() {
        return delete;
    }

    public Set<Variable> vars() {
        if (delete.isEmpty()) return match.getPatterns().variables();
        return delete.stream().flatMap(statement -> statement.variables().stream()).collect(Collectors.toSet());
    }

    @Override @SuppressWarnings("Duplicates")
    public String toString() {
        StringBuilder query = new StringBuilder(match().toString());
        query.append(Graql.Token.Char.NEW_LINE);

        query.append(Graql.Token.Command.DELETE);
        if (delete.size() > 1) {
            query.append(Graql.Token.Char.NEW_LINE);
        } else {
            query.append(Graql.Token.Char.SPACE);
        }
        query.append(delete.stream()
            .map(Statement::toString)
            .collect(Collectors.joining(Graql.Token.Char.NEW_LINE.toString())));

        return query.toString();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass()) && !o.getClass().isAssignableFrom(getClass())) {
            return false;
        }

        GraqlDelete that = (GraqlDelete) o;

        // It is important that we use vars() (the method) and not vars (the property)
        // vars (the property) stores the variables as the user defined
        // vars() (the method) returns match.vars() if vars (the property) is empty
        // we want to compare vars() (the method) which determines the final value
        return (this.match().equals(that.match()) &&
                this.vars().equals(that.vars()));
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        // It is important that we use vars() (the method) and not vars (the property)
        // For reasons explained in the equals() method above
        h ^= this.vars().hashCode();
        h *= 1000003;
        h ^= this.match().hashCode();
        return h;
    }
}
