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

package com.vaticle.typeql.lang.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.SYNTAX_ERROR_DETAILED;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.SYNTAX_ERROR_NO_DETAILS;

/**
 * ANTLR error listener that listens for syntax errors, and record them.
 * View the errors with {@link ErrorListener#toString()}.
 */
public class ErrorListener extends BaseErrorListener {

    private final List<String> queryLines;
    private final List<SyntaxError> errors = new ArrayList<>();

    private ErrorListener(List<String> queryLines) {
        this.queryLines = queryLines;
    }

    public static ErrorListener of(String query) {
        List<String> queryLines = list(query.split("\n"));
        return new ErrorListener(queryLines);
    }

    @Override
    public void syntaxError(
            Recognizer<?, ?> recognizer, Object offendingSymbol,
            int line, int charPositionInLine, String msg, RecognitionException e
    ) {
        errors.add(new SyntaxError(queryLines.get(line - 1), line, charPositionInLine, msg));
    }

    @Override
    public String toString() {
        return errors.stream().map(SyntaxError::toString).collect(Collectors.joining("\n\n"));
    }

    private static class SyntaxError {

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
            if (o == this) return true;
            if (!(o instanceof SyntaxError)) return false;
            SyntaxError that = (SyntaxError) o;
            return (Objects.equals(this.queryLine, that.queryLine) &&
                    this.line == that.line &&
                    this.charPositionInLine == that.charPositionInLine &&
                    this.msg.equals(that.msg));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}

