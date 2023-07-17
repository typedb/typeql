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
    common::token::{Function as FunctionToken, Operation as OperationToken},
    pattern::{Reference, UnboundConceptVariable, UnboundValueVariable, UnboundVariable},
};
use crate::pattern::expression::{Expression, Function, Operation};

pub trait ExpressionBuilder {
    fn add(self, right: impl Into<Expression>) -> Expression;
    fn subtract(self, right: impl Into<Expression>) -> Expression;
    fn multiply(self, right: impl Into<Expression>) -> Expression;
    fn divide(self, right: impl Into<Expression>) -> Expression;
    fn modulo(self, right: impl Into<Expression>) -> Expression;
    fn power(self, right: impl Into<Expression>) -> Expression;
    fn abs(arg: impl Into<Expression>) -> Expression;
    fn ceil(arg: impl Into<Expression>) -> Expression;
    fn floor(arg: impl Into<Expression>) -> Expression;
    fn max<const N: usize>(args: [impl Into<Expression>; N]) -> Expression;
    fn min<const N: usize>(args: [impl Into<Expression>; N]) -> Expression;
    fn round(arg: impl Into<Expression>) -> Expression;
}

impl<T: Into<Expression>> ExpressionBuilder for T {
    fn add(self, right: impl Into<Expression>) -> Expression {
        Expression::Operation(Operation::new(OperationToken::Add, self, right))
    }

    fn subtract(self, right: impl Into<Expression>) -> Expression {
        Expression::Operation(Operation::new(OperationToken::Subtract, self, right))
    }

    fn multiply(self, right: impl Into<Expression>) -> Expression {
        Expression::Operation(Operation::new(OperationToken::Multiply, self, right))
    }

    fn divide(self, right: impl Into<Expression>) -> Expression {
        Expression::Operation(Operation::new(OperationToken::Divide, self, right))
    }

    fn modulo(self, right: impl Into<Expression>) -> Expression {
        Expression::Operation(Operation::new(OperationToken::Modulo, self, right))
    }

    fn power(self, right: impl Into<Expression>) -> Expression {
        Expression::Operation(Operation::new(OperationToken::Power, self, right))
    }

    fn abs(arg: impl Into<Expression>) -> Expression {
        Expression::Function(Function { function_name: FunctionToken::Abs, args: vec![Box::from(arg.into())] })
    }

    fn ceil(arg: impl Into<Expression>) -> Expression {
        Expression::Function(Function { function_name: FunctionToken::Ceil, args: vec![Box::from(arg.into())] })
    }

    fn floor(arg: impl Into<Expression>) -> Expression {
        Expression::Function(Function { function_name: FunctionToken::Floor, args: vec![Box::from(arg.into())] })
    }

    fn max<const N: usize>(args: [impl Into<Expression>; N]) -> Expression {
        Expression::Function(Function {
            function_name: FunctionToken::Max,
            args: args.into_iter().map(|arg| Box::new(arg.into())).collect(),
        })
    }

    fn min<const N: usize>(args: [impl Into<Expression>; N]) -> Expression {
        Expression::Function(Function {
            function_name: FunctionToken::Min,
            args: args.into_iter().map(|arg| Box::new(arg.into())).collect(),
        })
    }

    fn round(arg: impl Into<Expression>) -> Expression {
        Expression::Function(Function { function_name: FunctionToken::Round, args: vec![Box::from(arg.into())] })
    }
}

#[macro_export]
macro_rules! max {
    ($($args:expr),*) => {{
        Expression::max([$($args, )*])
    }}
}

#[macro_export]
macro_rules! min {
    ($($args:expr),*) => {{
        Expression::min([$($args, )*])
    }}
}
