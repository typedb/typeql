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

package com.vaticle.typeql.lang.pattern.variable.builder;

import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.pattern.constraint.ValueConstraint.Assignment.Expression;

public interface ExpressionBuilder<T extends Expression> {

     T toExpression();

     default Expression.Operation plus(ExpressionBuilder<?> other) {
          return new Expression.Operation(TypeQLToken.Expression.Operation.PLUS, toExpression(), other.toExpression());
     }

     default Expression.Operation minus(ExpressionBuilder<?> other) {
          return new Expression.Operation(TypeQLToken.Expression.Operation.MINUS, toExpression(), other.toExpression());
     }

     default Expression.Operation times(ExpressionBuilder<?> other) {
          return new Expression.Operation(TypeQLToken.Expression.Operation.TIMES, toExpression(), other.toExpression());
     }

     default Expression.Operation div(ExpressionBuilder<?> other) {
          return new Expression.Operation(TypeQLToken.Expression.Operation.DIV, toExpression(), other.toExpression());
     }

     default Expression.Operation mod(ExpressionBuilder<?> other) {
          return new Expression.Operation(TypeQLToken.Expression.Operation.MOD, toExpression(), other.toExpression());
     }

     default Expression.Operation pow(ExpressionBuilder<?> other) {
          return new Expression.Operation(TypeQLToken.Expression.Operation.POW, toExpression(), other.toExpression());
     }
}
