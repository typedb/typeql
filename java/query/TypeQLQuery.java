/*
 * Copyright (C) 2021 Vaticle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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

    public TypeQLCompute.Statistics asComputeStatistics() {
        if (this instanceof TypeQLCompute.Statistics) {
            return (TypeQLCompute.Statistics) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLCompute.Statistics.class)));
        }
    }

    public TypeQLCompute.Path asComputePath() {
        if (this instanceof TypeQLCompute.Path) {
            return (TypeQLCompute.Path) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLCompute.Path.class)));
        }
    }

    public TypeQLCompute.Centrality asComputeCentrality() {
        if (this instanceof TypeQLCompute.Centrality) {
            return (TypeQLCompute.Centrality) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLCompute.Centrality.class)));
        }
    }

    public TypeQLCompute.Cluster asComputeCluster() {
        if (this instanceof TypeQLCompute.Cluster) {
            return (TypeQLCompute.Cluster) this;
        } else {
            throw TypeQLException.of(INVALID_CASTING.message(className(this.getClass()), className(TypeQLCompute.Cluster.class)));
        }
    }

    @Override
    public abstract String toString();
}
