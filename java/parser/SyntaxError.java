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

package graql.lang.parser;

import java.util.Objects;

import static graql.lang.common.exception.ErrorMessage.SYNTAX_ERROR_DETAILED;
import static graql.lang.common.exception.ErrorMessage.SYNTAX_ERROR_NO_DETAILS;

public class SyntaxError {

    private final String queryLine;
    private final int line;
    private final int charPositionInLine;
    private final String msg;
    private final int hash;

    public SyntaxError(String queryLine, int line, int charPositionInLine, String msg) {
        if (msg == null) throw new NullPointerException("Null msg");
        this.queryLine = queryLine;
        this.line = line;
        this.charPositionInLine = charPositionInLine;
        this.msg = msg;
        this.hash = Objects.hash(this.queryLine, this.line, this.charPositionInLine, this.msg);
    }

    private String spaces(int len) {
        final char ch = ' ';
        final char[] output = new char[len];
        for (int i = len - 1; i >= 0; i--) {
            output[i] = ch;
        }
        return new String(output);
    }

    @Override
    public String toString() {
        if (queryLine == null) {
            return SYNTAX_ERROR_NO_DETAILS.message(line, msg);
        } else {
            // Error message appearance:
            //
            // syntax error at line 1:
            // match $
            //       ^
            // blah blah antlr blah
            final String pointer = spaces(charPositionInLine) + "^";
            return SYNTAX_ERROR_DETAILED.message(line, queryLine, pointer, msg);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SyntaxError) {
            final SyntaxError that = (SyntaxError) o;
            return (Objects.equals(this.queryLine, that.queryLine) &&
                    this.line == that.line &&
                    this.charPositionInLine == that.charPositionInLine &&
                    this.msg.equals(that.msg));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
