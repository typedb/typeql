/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.typeql.lang.pattern;

import com.typeql.lang.common.exception.ErrorMessage;
import com.typeql.lang.common.exception.TypeQLException;
import com.typeql.lang.pattern.statement.Statement;

import static com.vaticle.typedb.common.util.Objects.className;

public interface Conjunctable extends Pattern {

    @Override
    default boolean isStatement() { return false; }

    @Override
    default boolean isNegation() { return false; }

    @Override
    default boolean isConjunctable() { return true; }

    @Override
    default Statement asStatement() {
        throw TypeQLException.of(ErrorMessage.INVALID_CASTING.message(className(this.getClass()), className(Statement.class)));
    }

    @Override
    default Negation<? extends Pattern> asNegation() {
        throw TypeQLException.of(ErrorMessage.INVALID_CASTING.message(className(this.getClass()), className(Negation.class)));
    }

    @Override
    default Conjunctable asConjunctable() {
        return this;
    }
}
