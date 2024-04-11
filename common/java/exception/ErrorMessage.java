/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
