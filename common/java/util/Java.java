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
