/*
 * GRAKN.AI - THE KNOWLEDGE GRAPH
 * Copyright (C) 2019 Grakn Labs Ltd
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

package graql.lang.property;

import graql.lang.Graql;
import graql.lang.statement.StatementType;

import static graql.lang.util.StringUtil.escapeRegex;
import static graql.lang.util.StringUtil.quoteString;

/**
 * Represents the {@code regex} property on a AttributeType. This property can be queried and inserted.
 * This property introduces a validation constraint on instances of this AttributeType, stating that their
 * values must conform to the given regular expression.
 */
public class RegexProperty extends VarProperty {

    private final String regex;

    public RegexProperty(String regex) {
        if (regex == null) {
            throw new NullPointerException("Null regex");
        }
        this.regex = regex;
    }

    public String regex() {
        return regex;
    }

    @Override
    public String keyword() {
        return Graql.Token.Property.REGEX.toString();
    }

    @Override
    public String property() {
        return quoteString(escapeRegex(regex()));
    }

    @Override
    public boolean isUnique() {
        return true;
    }

    @Override
    public Class statementClass() {
        return StatementType.class;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RegexProperty) {
            RegexProperty that = (RegexProperty) o;
            return (this.regex.equals(that.regex()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.regex.hashCode();
        return h;
    }
}
