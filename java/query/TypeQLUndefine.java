/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.typeql.lang.query;

import com.typeql.lang.common.TypeQLToken;
import com.typeql.lang.pattern.Definable;

import java.util.List;

public class TypeQLUndefine extends TypeQLDefinable {

    public TypeQLUndefine(List<Definable> definables) {
        super(TypeQLToken.Clause.UNDEFINE, definables);
    }
}
