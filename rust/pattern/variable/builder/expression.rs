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

use crate::{
    common::token::{Function as FunctionToken, Operation as OperationToken},
    pattern::{Reference, UnboundVariable, Value},
};

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
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Constant {
    pub(crate) value: Value,
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
    pub fn references_recursive(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        self.inner.references_recursive()
    }
}

impl fmt::Display for Parenthesis {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "( {} )", self.inner)
    }
}
