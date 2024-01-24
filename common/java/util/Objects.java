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
