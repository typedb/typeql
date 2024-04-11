/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
