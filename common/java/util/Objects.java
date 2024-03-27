/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typedb.common.util;

public class Objects {

    /**
     * For any class, get its name including any classes/interfaces it is nested inside, but excluding its package name.
     * The class name should start with an uppercase character, and the package name should not contain uppercase characters.
     *
     * @param clazz The class.
     * @return The class name including any classes/interfaces it is nested inside, but excluding its package name.
     */
    public static String className(Class<?> clazz) {
        for (int i = 0; i < clazz.getCanonicalName().length(); i++) {
            if (Character.isUpperCase(clazz.getCanonicalName().charAt(i))) {
                return clazz.getCanonicalName().substring(i);
            }
        }
        return clazz.getSimpleName();
    }
}
