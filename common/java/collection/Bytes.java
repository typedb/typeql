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

package com.vaticle.typedb.common.collection;

import java.util.Map;

import static com.vaticle.typedb.common.collection.Collections.map;
import static com.vaticle.typedb.common.collection.Collections.pair;

public class Bytes {

    private static final String PREFIX = "0x";
    // TODO: convert HEX_ARRAY to byte[] once upgraded to Java 9+
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
    private static final Map<Character, Integer> HEX_MAP = map(pair('0', 0), pair('1', 1), pair('2', 2), pair('3', 3),
                                                               pair('4', 4), pair('5', 5), pair('6', 6), pair('7', 7),
                                                               pair('8', 8), pair('9', 9), pair('a', 10), pair('b', 11),
                                                               pair('c', 12), pair('d', 13), pair('e', 14), pair('f', 15));

    public static byte[] hexStringToBytes(String hexString) {
        assert hexString.length() % 2 == 0;
        assert hexString.startsWith(PREFIX);

        hexString = hexString.replace(PREFIX, "");

        final int len = hexString.length();
        final byte[] bytes = new byte[len / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((HEX_MAP.get(hexString.charAt(i * 2)) << 4) + HEX_MAP.get(hexString.charAt((i * 2) + 1)));
        }
        return bytes;
    }

    public static String bytesToHexString(byte[] bytes) {
        final char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            final int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        // TODO when hexChars is a byte[]: return new String(hexChars, StandardCharsets.UTF_8);
        return PREFIX + new String(hexChars);
    }
}
