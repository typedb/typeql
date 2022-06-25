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
 */

package com.vaticle.typeql.lang.common;

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
            return null;
        }
    }
}
