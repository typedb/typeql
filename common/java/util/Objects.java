/*
 * Copyright (C) 2022 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
