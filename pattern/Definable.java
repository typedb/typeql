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
 *
 */

package com.vaticle.typeql.lang.pattern;

import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.schema.Rule;
import com.vaticle.typeql.lang.pattern.variable.TypeVariable;

import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;

public interface Definable {

    default boolean isRule() {
        return false;
    }

    default boolean isTypeVariable() {
        return false;
    }

    default Rule asRule() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(Rule.class)));
    }

    default TypeVariable asTypeVariable() {
        throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeVariable.class)));
    }
}
