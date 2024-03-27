/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typeql.lang.pattern;

import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.schema.Rule;
import com.vaticle.typeql.lang.pattern.statement.TypeStatement;

import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;

public interface Definable {

    default boolean isRule() {
        return false;
    }

    default boolean isTypeStatement() {
        return false;
    }

    default Rule asRule() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Rule.class)));
    }

    default TypeStatement asTypeStatement() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeStatement.class)));
    }

    String toString(boolean pretty);
}
