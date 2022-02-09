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

package com.vaticle.typeql.lang.query;

import com.vaticle.typeql.lang.common.TypeQLArg;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import static com.vaticle.typedb.common.util.Objects.className;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_CASTING;

public abstract class TypeQLQuery {

    public abstract TypeQLArg.QueryType type();

    public TypeQLDefine asDefine() {
        if (this instanceof TypeQLDefine) {
            return (TypeQLDefine) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLDefine.class)));
        }
    }

    public TypeQLUndefine asUndefine() {
        if (this instanceof TypeQLUndefine) {
            return (TypeQLUndefine) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLUndefine.class)));
        }
    }

    public TypeQLInsert asInsert() {
        if (this instanceof TypeQLInsert) {
            return (TypeQLInsert) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLInsert.class)));
        }
    }

    public TypeQLDelete asDelete() {
        if (this instanceof TypeQLDelete) {
            return (TypeQLDelete) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLDelete.class)));
        }
    }

    public TypeQLUpdate asUpdate() {
        if (this instanceof TypeQLUpdate) {
            return (TypeQLUpdate) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLUpdate.class)));
        }
    }

    public TypeQLMatch asMatch() {
        if (this instanceof TypeQLMatch) {
            return (TypeQLMatch) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLMatch.class)));
        }
    }

    public TypeQLMatch.Aggregate asMatchAggregate() {
        if (this instanceof TypeQLMatch.Aggregate) {
            return (TypeQLMatch.Aggregate) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLMatch.Aggregate.class)));
        }
    }

    public TypeQLMatch.Group asMatchGroup() {
        if (this instanceof TypeQLMatch.Group) {
            return (TypeQLMatch.Group) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLMatch.Group.class)));
        }
    }

    public TypeQLMatch.Group.Aggregate asMatchGroupAggregate() {
        if (this instanceof TypeQLMatch.Group.Aggregate) {
            return (TypeQLMatch.Group.Aggregate) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLMatch.Group.Aggregate.class)));
        }
    }

    @Override
    public abstract String toString();
}
