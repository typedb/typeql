/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typeql.lang.common.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

import static com.vaticle.typeql.lang.common.TypeQLToken.Char.INDENTATION;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.NEW_LINE;

public class Strings {

    public static String unescapeRegex(String regex) {
        return regex.replaceAll("\\\\/", "/");
    }

    public static String escapeRegex(String regex) {
        return regex.replaceAll("/", "\\\\/");
    }

    /**
     * @param string a string to quote and escape
     * @return a string, surrounded with double quotes and escaped
     */
    public static String quoteString(String string) {
        return "\"" + string + "\"";
    }

    public static String indent(Object object) {
        return indent(object.toString());
    }

    public static String indent(String string) {
        return Arrays.stream(string.split("\n")).map(s -> INDENTATION + s).collect(NEW_LINE.joiner());
    }

    /**
     * @param value a value in the graph
     * @return the string representation of the value (using quotes if it is already a string)
     */
    public static String valueToString(Object value) {
        if (value instanceof String) {
            return quoteString((String) value);
        } else if (value instanceof Double) {
            DecimalFormat df = new DecimalFormat("#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            df.setMinimumFractionDigits(1);
            df.setMaximumFractionDigits(12);
            df.setMinimumIntegerDigits(1);
            return df.format(value);
        } else {
            return value.toString();
        }
    }
}
