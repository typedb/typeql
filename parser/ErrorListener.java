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

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.vaticle.typedb.common.collection.Collections.list;

/**
 * ANTLR error listener that listens for syntax errors.
 * When a syntax error occurs, it is recorded. Call {@link ErrorListener#hasErrors()} to see if there were errors.
 * View the errors with {@link ErrorListener#toString()}.
 */
public class ErrorListener extends BaseErrorListener {

    private final List<String> query;
    private final List<SyntaxError> errors = new ArrayList<>();

    private ErrorListener(List<String> query) {
        this.query = query;
    }

    /**
     * Create a {@link ErrorListener} without a reference to a query string.
     * This will have limited error-reporting abilities, but is necessary when dealing with very large queries
     * that should not be held in memory all at once.
     */
    public static ErrorListener withoutQueryString() {
        return new ErrorListener(null);
    }

    public static ErrorListener of(String query) {
        List<String> queryList = list(query.split("\n"));
        return new ErrorListener(queryList);
    }

    @Override
    public void syntaxError(
            Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg,
            RecognitionException e) {

        if (query == null) {
            errors.add(new SyntaxError(null, line, 0, msg));
        } else {
            errors.add(new SyntaxError(query.get(line - 1), line, charPositionInLine, msg));
        }
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void clearErrors() {
        errors.clear();
    }

    @Override
    public String toString() {
        return errors.stream().map(SyntaxError::toString).collect(Collectors.joining("\n"));
    }
}

