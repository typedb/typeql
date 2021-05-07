/*
 * Copyright (C) 2021 Vaticle
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

package com.vaticle.typeql.lang.parser;

import java.util.Objects;

import static com.vaticle.typeql.lang.common.exception.ErrorMessage.SYNTAX_ERROR_DETAILED;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.SYNTAX_ERROR_NO_DETAILS;

public class SyntaxError {

    private final String queryLine;
    private final int line;
    private final int charPositionInLine;
    private final String msg;
    private final int hash;

    public SyntaxError(String queryLine, int line, int charPositionInLine, String msg) {
        if (msg == null) throw new NullPointerException("Null msg");
        this.queryLine = queryLine;
        this.line = line;
        this.charPositionInLine = charPositionInLine;
        this.msg = msg;
        this.hash = Objects.hash(this.queryLine, this.line, this.charPositionInLine, this.msg);
    }

    private String spaces(int len) {
        final char ch = ' ';
        char[] output = new char[len];
        for (int i = len - 1; i >= 0; i--) {
            output[i] = ch;
        }
        return new String(output);
    }

    @Override
    public String toString() {
        if (queryLine == null) {
            return SYNTAX_ERROR_NO_DETAILS.message(line, msg);
        } else {
            // Error message appearance:
            //
            // syntax error at line 1:
            // match $
            //       ^
            // blah blah antlr blah
            String pointer = spaces(charPositionInLine) + "^";
            return SYNTAX_ERROR_DETAILED.message(line, queryLine, pointer, msg);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SyntaxError) {
            SyntaxError that = (SyntaxError) o;
            return (Objects.equals(this.queryLine, that.queryLine) &&
                    this.line == that.line &&
                    this.charPositionInLine == that.charPositionInLine &&
                    this.msg.equals(that.msg));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
