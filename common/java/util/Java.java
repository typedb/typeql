/*
 * Copyright (C) 2022 Vaticle
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
 *
 */

package com.vaticle.typedb.common.util;

public class Java {
    public static Integer UNKNOWN_VERSION = -1;

    public static Integer getMajorVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            // Java 8 or lower: 1.6.0_23, 1.7.0, 1.7.0_80, 1.8.0_211
            version = version.substring(2, 3);
        } else {
            // Java 9 or higher: 9.0.1, 11.0.4, 12, 12.0.1
            int dot = version.indexOf(".");
            if (dot != -1) {
                version = version.substring(0, dot);
            }
        }
        try {
            return Integer.parseInt(version);
        } catch (Exception e) {
            return Java.UNKNOWN_VERSION;
        }
    }
}
