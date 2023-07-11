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

use std::collections::HashSet;
use std::fmt;
use crate::common::token::{self, Function as FunctionToken, Operation as OperationToken};
use crate::pattern::{UnboundConceptVariable, UnboundValueVariable, UnboundVariable, Value, Variable};
use crate::pattern::Value::String;

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
            Expression::Operation(operation) => { write!(f, "{operation}") }
            Expression::Function(function) => { write!(f, "{function}") }
            Expression::Constant(constant) => { write!(f, "{constant}") }
            Expression::Parenthesis(parenthesis) => { write!(f, "{parenthesis}") }
            Expression::Variable(variable) => { write!(f, "{variable}") }
        }

    }
}

impl Expression {
    pub(crate) fn collect_variables(&self, collector: &mut HashSet<UnboundVariable>) {
        match self {
            Expression::Operation(operation) => operation.collect_variables(collector),
            Expression::Function(function) => function.collect_variables(collector),
            Expression::Constant(constant) => constant.collect_variables(collector),
            Expression::Parenthesis(parenthesis) => parenthesis.collect_variables(collector),
            Expression::Variable(variable) => {
                collector.insert(variable.clone());
            }
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Constant {
    pub(crate) value: Value,
}

impl Constant {
    pub(crate) fn collect_variables(&self, collector: &mut HashSet<UnboundVariable>) {}
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
    pub(crate) fn collect_variables(&self, collector: &mut HashSet<UnboundVariable>) {
        self.left.collect_variables(collector);
        self.right.collect_variables(collector);
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
    pub(crate) fn collect_variables(&self, collector: &mut HashSet<UnboundVariable>) {
        let _ = self.args.iter().map(|arg| arg.collect_variables(collector));
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
    pub(crate) fn collect_variables(&self, collector: &mut HashSet<UnboundVariable>) {
        self.inner.collect_variables(collector);
    }
}
impl fmt::Display for Parenthesis {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "( {} )", self.inner)
    }
}
