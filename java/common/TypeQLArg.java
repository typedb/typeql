/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typeql.lang.common;

import com.vaticle.typeql.lang.common.exception.TypeQLException;

import static com.vaticle.typeql.lang.common.exception.ErrorMessage.INVALID_SORTING_ORDER;

public class TypeQLArg {

    public enum QueryType {
        READ(0),
        WRITE(1);

        private final int id;
        private final boolean isWrite;

        QueryType(int id) {
            this.id = id;
            this.isWrite = id == 1;
        }

        public static QueryType of(int value) {
            for (QueryType t : values()) {
                if (t.id == value) return t;
            }
            return null;
        }

        public boolean isRead() {return !isWrite;}

        public boolean isWrite() {return isWrite;}
    }

    public enum ValueType {
        BOOLEAN("boolean"),
        DATETIME("datetime"),
        DOUBLE("double"),
        LONG("long"),
        STRING("string");

        private final String type;

        ValueType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return this.type;
        }

        public static ValueType of(String value) {
            for (ValueType c : ValueType.values()) {
                if (c.type.equals(value)) {
                    return c;
                }
            }
            return null;
        }
    }

    public enum Order {
        ASC("asc"),
        DESC("desc");

        private final String order;

        Order(String order) {
            this.order = order;
        }

        @Override
        public String toString() {
            return this.order;
        }

        public static Order of(String value) {
            for (Order c : Order.values()) {
                if (c.order.equals(value)) {
                    return c;
                }
            }
            throw TypeQLException.of(INVALID_SORTING_ORDER.message(value, ASC.order, DESC.order));
        }
    }
}
