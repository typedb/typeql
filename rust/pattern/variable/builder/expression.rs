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

use crate::{
    common::token::ArithmeticOperator,
    pattern::expression::{Expression, Operation},
};

pub trait SubExpression: Into<Expression> {}

pub trait ExpressionBuilder {
    fn add(self, right: impl Into<Expression>) -> Operation;
    fn subtract(self, right: impl Into<Expression>) -> Operation;
    fn multiply(self, right: impl Into<Expression>) -> Operation;
    fn divide(self, right: impl Into<Expression>) -> Operation;
    fn modulo(self, right: impl Into<Expression>) -> Operation;
    fn power(self, right: impl Into<Expression>) -> Operation;
}

impl ExpressionBuilder for Expression {
    fn add(self, right: impl Into<Expression>) -> Operation {
        Operation::new(ArithmeticOperator::Add, self, right)
    }

    fn subtract(self, right: impl Into<Expression>) -> Operation {
        Operation::new(ArithmeticOperator::Subtract, self, right)
    }

    fn multiply(self, right: impl Into<Expression>) -> Operation {
        Operation::new(ArithmeticOperator::Multiply, self, right)
    }

    fn divide(self, right: impl Into<Expression>) -> Operation {
        Operation::new(ArithmeticOperator::Divide, self, right)
    }

    fn modulo(self, right: impl Into<Expression>) -> Operation {
        Operation::new(ArithmeticOperator::Modulo, self, right)
    }

    fn power(self, right: impl Into<Expression>) -> Operation {
        Operation::new(ArithmeticOperator::Power, self, right)
    }
}

impl<U: SubExpression> ExpressionBuilder for U {
    fn add(self, right: impl Into<Expression>) -> Operation {
        self.into().add(right)
    }

    fn subtract(self, right: impl Into<Expression>) -> Operation {
        self.into().subtract(right)
    }

    fn multiply(self, right: impl Into<Expression>) -> Operation {
        self.into().multiply(right)
    }

    fn divide(self, right: impl Into<Expression>) -> Operation {
        self.into().divide(right)
    }

    fn modulo(self, right: impl Into<Expression>) -> Operation {
        self.into().modulo(right)
    }

    fn power(self, right: impl Into<Expression>) -> Operation {
        self.into().power(right)
    }
}
