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

package com.vaticle.typedb.common.exception;

import java.util.HashMap;
import java.util.Map;

public abstract class ErrorMessage {

    private static Map<String, Map<Integer, ErrorMessage>> errors = new HashMap<>();
    private static int maxCodeNumber = 0;
    private static int maxCodeDigits = 0;

    private final String codePrefix;
    private final int codeNumber;
    private final String message;
    private String code = null;

    protected ErrorMessage(String codePrefix, int codeNumber, String messagePrefix, String messageBody) {
        this.codePrefix = codePrefix;
        this.codeNumber = codeNumber;
        this.message = messagePrefix + ": " + messageBody;

        assert errors.get(codePrefix) == null || errors.get(codePrefix).get(codeNumber) == null;
        errors.computeIfAbsent(codePrefix, s -> new HashMap<>()).put(codeNumber, this);
        maxCodeNumber = Math.max(codeNumber, maxCodeNumber);
        maxCodeDigits = (int) Math.ceil(Math.log10(maxCodeNumber));
    }

    public String code() {
        if (code != null) return code;

        StringBuilder zeros = new StringBuilder();
        for (int digits = (int) Math.floor(Math.log10(codeNumber)) + 1; digits < maxCodeDigits; digits++) {
            zeros.append("0");
        }

        code = codePrefix + zeros.toString() + codeNumber;
        return code;
    }

    public String message(Object... parameters) {
        return String.format(toString(), parameters);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", code(), message);
    }
}
