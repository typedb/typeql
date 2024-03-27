/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typeql.lang.pattern.statement.builder;

import com.vaticle.typeql.lang.pattern.constraint.ValueConstraint;
import com.vaticle.typeql.lang.pattern.expression.Expression;
import com.vaticle.typeql.lang.pattern.statement.ValueStatement;

public interface ValueStatementBuilder extends PredicateBuilder<ValueStatement> {

    default ValueStatement assign(Expression expression) {
        return constrain(new ValueConstraint.Assignment(expression));
    }

    ValueStatement constrain(ValueConstraint.Predicate constraint);

    ValueStatement constrain(ValueConstraint.Assignment constraint);
}
