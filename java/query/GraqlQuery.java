/*
 * Copyright (C) 2020 Grakn Labs
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

package graql.lang.query;

import graql.lang.common.exception.GraqlException;

import static grakn.common.util.Objects.className;
import static graql.lang.common.exception.ErrorMessage.INVALID_CASTING;

public abstract class GraqlQuery {

    public GraqlDefine asDefine() {
        if (this instanceof GraqlDefine) {
            return (GraqlDefine) this;
        } else {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(GraqlDefine.class)));
        }
    }

    public GraqlUndefine asUndefine() {
        if (this instanceof GraqlUndefine) {
            return (GraqlUndefine) this;
        } else {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(GraqlUndefine.class)));
        }
    }

    public GraqlInsert asInsert() {
        if (this instanceof GraqlInsert) {
            return (GraqlInsert) this;
        } else {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(GraqlInsert.class)));
        }
    }

    public GraqlDelete asDelete() {
        if (this instanceof GraqlDelete) {
            return (GraqlDelete) this;
        } else {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(GraqlDelete.class)));
        }
    }

    public GraqlMatch asMatch() {
        if (this instanceof GraqlMatch) {
            return (GraqlMatch) this;
        } else {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(GraqlMatch.class)));
        }
    }

    public GraqlMatch.Aggregate asMatchAggregate() {
        if (this instanceof GraqlMatch.Aggregate) {
            return (GraqlMatch.Aggregate) this;
        } else {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(GraqlMatch.Aggregate.class)));
        }
    }

    public GraqlMatch.Group asMatchGroup() {
        if (this instanceof GraqlMatch.Group) {
            return (GraqlMatch.Group) this;
        } else {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(GraqlMatch.Group.class)));
        }
    }

    public GraqlMatch.Group.Aggregate asMatchGroupAggregate() {
        if (this instanceof GraqlMatch.Group.Aggregate) {
            return (GraqlMatch.Group.Aggregate) this;
        } else {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(GraqlMatch.Group.Aggregate.class)));
        }
    }

    public GraqlCompute.Statistics asComputeStatistics() {
        if (this instanceof GraqlCompute.Statistics) {
            return (GraqlCompute.Statistics) this;
        } else {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(GraqlCompute.Statistics.class)));
        }
    }

    public GraqlCompute.Path asComputePath() {
        if (this instanceof GraqlCompute.Path) {
            return (GraqlCompute.Path) this;
        } else {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(GraqlCompute.Path.class)));
        }
    }

    public GraqlCompute.Centrality asComputeCentrality() {
        if (this instanceof GraqlCompute.Centrality) {
            return (GraqlCompute.Centrality) this;
        } else {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(GraqlCompute.Centrality.class)));
        }
    }

    public GraqlCompute.Cluster asComputeCluster() {
        if (this instanceof GraqlCompute.Cluster) {
            return (GraqlCompute.Cluster) this;
        } else {
            throw GraqlException.of(INVALID_CASTING.message(className(this.getClass()), className(GraqlCompute.Cluster.class)));
        }
    }

    @Override
    public abstract String toString();
}
