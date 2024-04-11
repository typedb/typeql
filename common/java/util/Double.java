/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typedb.common.util;

public class Double {

    public static final double EPSILON = 1e-10;

    public static boolean equalsApproximate(double first, double second) {
        return equalsApproximate(first, second, EPSILON);
    }

    public static boolean equalsApproximate(double first, double second, double epsilon) {
        return Math.abs(first - second) < epsilon;
    }
}
