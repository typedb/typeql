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

use std::fmt;
use crate::common::token::{self, Function as FunctionToken, Operation as OperationToken};
use crate::pattern::{UnboundConceptVariable, UnboundValueVariable, UnboundVariable, Value};
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

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Constant {
    pub value: Value,
}

impl fmt::Display for Constant {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.value)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Operation {
    pub op: OperationToken,
    pub left: Box<Expression>,
    pub right: Box<Expression>,
}

impl fmt::Display for Operation {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {} {}", self.left, self.op, self.right)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Function {
    symbol: FunctionToken,
    args: Vec<Box<Expression>>,
}

impl fmt::Display for Function {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}({})", self.symbol, self.args.iter().map(|expr| expr.to_string()).collect::<Vec<_>>().join(", "))
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Parenthesis {
    inner: Box<Expression>,
}

impl fmt::Display for Parenthesis {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "( {} )", self.inner)
    }
}