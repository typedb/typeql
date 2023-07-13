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

use std::{fmt, iter};

use chrono::NaiveDateTime;

use crate::{
    common::token::{Function as FunctionToken, Operation as OperationToken},
    pattern::{Reference, UnboundConceptVariable, UnboundValueVariable, UnboundVariable, Value},
};

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
        Expression::Operation(Operation {
            op: OperationToken::Add,
            left: Box::new(self.into()),
            right: Box::new(right.into()),
        })
    }

    fn subtract(self, right: impl Into<Expression>) -> Expression {
        Expression::Operation(Operation {
            op: OperationToken::Subtract,
            left: Box::new(self.into()),
            right: Box::new(right.into()),
        })
    }

    fn multiply(self, right: impl Into<Expression>) -> Expression {
        Expression::Operation(Operation {
            op: OperationToken::Multiply,
            left: Box::new(self.into()),
            right: Box::new(right.into()),
        })
    }

    fn divide(self, right: impl Into<Expression>) -> Expression {
        Expression::Operation(Operation {
            op: OperationToken::Divide,
            left: Box::new(self.into()),
            right: Box::new(right.into()),
        })
    }

    fn modulo(self, right: impl Into<Expression>) -> Expression {
        Expression::Operation(Operation {
            op: OperationToken::Modulo,
            left: Box::new(self.into()),
            right: Box::new(right.into()),
        })
    }

    fn power(self, right: impl Into<Expression>) -> Expression {
        Expression::Operation(Operation {
            op: OperationToken::Power,
            left: Box::new(self.into()),
            right: Box::new(right.into()),
        })
    }

    fn abs(arg: impl Into<Expression>) -> Expression {
        Expression::Function(Function { symbol: FunctionToken::Abs, args: vec![Box::from(arg.into())] })
    }

    fn ceil(arg: impl Into<Expression>) -> Expression {
        Expression::Function(Function { symbol: FunctionToken::Ceil, args: vec![Box::from(arg.into())] })
    }

    fn floor(arg: impl Into<Expression>) -> Expression {
        Expression::Function(Function { symbol: FunctionToken::Abs, args: vec![Box::from(arg.into())] })
    }

    fn max<const N: usize>(args: [impl Into<Expression>; N]) -> Expression {
        Expression::Function(Function {
            symbol: FunctionToken::Max,
            args: args.into_iter().map(|arg| Box::new(arg.into())).collect(),
        })
    }

    fn min<const N: usize>(args: [impl Into<Expression>; N]) -> Expression {
        Expression::Function(Function {
            symbol: FunctionToken::Min,
            args: args.into_iter().map(|arg| Box::new(arg.into())).collect(),
        })
    }

    fn round(arg: impl Into<Expression>) -> Expression {
        Expression::Function(Function { symbol: FunctionToken::Abs, args: vec![Box::from(arg.into())] })
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Expression {
    Operation(Operation),
    Function(Function),
    Constant(Constant),
    Parenthesis(Parenthesis),
    Variable(UnboundVariable),
}

impl fmt::Display for Expression {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Expression::Operation(operation) => write!(f, "{operation}"),
            Expression::Function(function) => write!(f, "{function}"),
            Expression::Constant(constant) => write!(f, "{constant}"),
            Expression::Parenthesis(parenthesis) => write!(f, "{parenthesis}"),
            Expression::Variable(variable) => write!(f, "{variable}"),
        }
    }
}

impl Expression {
    pub fn references_recursive(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        match self {
            Expression::Operation(operation) => operation.references_recursive(),
            Expression::Function(function) => function.references_recursive(),
            Expression::Constant(_constant) => Box::new(iter::empty()),
            Expression::Parenthesis(parenthesis) => parenthesis.references_recursive(),
            Expression::Variable(variable) => variable.references(),
        }
    }

    pub fn add(self, right: impl Into<Expression>) -> Expression {
        Expression::Operation(Operation {
            op: OperationToken::Add,
            left: Box::new(self),
            right: Box::new(right.into()),
        })
    }
}

impl From<UnboundValueVariable> for Expression {
    fn from(variable: UnboundValueVariable) -> Self {
        Self::Variable(variable.into())
    }
}

impl From<UnboundConceptVariable> for Expression {
    fn from(variable: UnboundConceptVariable) -> Self {
        Self::Variable(variable.into())
    }
}

impl From<i64> for Expression {
    fn from(value: i64) -> Self {
        Self::Constant(value.into())
    }
}

impl From<f64> for Expression {
    fn from(value: f64) -> Self {
        Self::Constant(value.into())
    }
}

impl From<bool> for Expression {
    fn from(value: bool) -> Self {
        Self::Constant(value.into())
    }
}

impl From<String> for Expression {
    fn from(value: String) -> Self {
        Self::Constant(value.into())
    }
}

impl From<&str> for Expression {
    fn from(value: &str) -> Self {
        Self::Constant(value.into())
    }
}

impl From<NaiveDateTime> for Expression {
    fn from(value: NaiveDateTime) -> Self {
        Self::Constant(value.into())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Constant {
    pub(crate) value: Value,
}

impl<T: Into<Value>> From<T> for Constant {
    fn from(value: T) -> Self {
        Constant { value: value.into() }
    }
}

impl fmt::Display for Constant {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.value)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Operation {
    pub(crate) op: OperationToken,
    pub(crate) left: Box<Expression>,
    pub(crate) right: Box<Expression>,
}

impl Operation {
    pub fn references_recursive(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        Box::new(self.left.references_recursive().chain(self.right.references_recursive()))
    }
}

impl fmt::Display for Operation {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {} {}", self.left, self.op, self.right)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Function {
    pub(crate) symbol: FunctionToken,
    pub(crate) args: Vec<Box<Expression>>,
}

impl Function {
    pub fn references_recursive(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        Box::new(self.args.iter().flat_map(|expr| expr.references_recursive()))
    }
}

impl fmt::Display for Function {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}({})", self.symbol, self.args.iter().map(|expr| expr.to_string()).collect::<Vec<_>>().join(", "))
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Parenthesis {
    pub(crate) inner: Box<Expression>,
}

impl Parenthesis {
    pub fn new(expression: Expression) -> Self {
        Self { inner: Box::new(expression) }
    }

    pub fn references_recursive(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        self.inner.references_recursive()
    }
}

impl From<Expression> for Parenthesis {
    fn from(expression: Expression) -> Self {
        Parenthesis { inner: Box::new(expression) }
    }
}

impl fmt::Display for Parenthesis {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "( {} )", self.inner)
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
