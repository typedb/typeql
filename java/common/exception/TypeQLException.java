/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typeql.lang.common.exception;


public class TypeQLException extends RuntimeException {

    protected TypeQLException(String error) {
        super(error);
    }

    public static TypeQLException of(ErrorMessage errorMessage) {
        return new TypeQLException(errorMessage.message());
    }

    public static TypeQLException of(String error) {
        return new TypeQLException(error);
    }
}