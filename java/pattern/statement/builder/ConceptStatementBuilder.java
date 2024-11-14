/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.typeql.lang.pattern.statement.builder;

import com.typeql.lang.common.TypeQLVariable;
import com.typeql.lang.pattern.constraint.ConceptConstraint;
import com.typeql.lang.pattern.statement.ConceptStatement;

public interface ConceptStatementBuilder {

    default ConceptStatement is(String name) {
        return is(TypeQLVariable.Concept.nameVar(name));
    }

    default ConceptStatement is(TypeQLVariable.Concept var) {
        return constrain(new ConceptConstraint.Is(var));
    }

    ConceptStatement constrain(ConceptConstraint.Is constraint);
}
