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

use crate::common::token::{Function as FunctionToken, Operation as OperationToken};
use crate::pattern::{UnboundConceptVariable, UnboundValueVariable, UnboundVariable, Value};

#[derive(Debug, Clone, PartialEq)]
pub enum Expression {
    Operation(Operation),
    Function(Function),
    Constant(Constant),
    Parenthesis(Parenthesis),
    Variable(UnboundVariable),
}

#[derive(Debug, Clone, PartialEq)]
pub struct Constant {
    pub value: Value,
}

#[derive(Debug, Clone, PartialEq)]
pub struct Operation {
    pub op: OperationToken,
    pub left: Box<Expression>,
    pub right: Box<Expression>,
}

#[derive(Debug, Clone, PartialEq)]
pub struct Function {
    symbol: FunctionToken,
    arg: Vec<Box<Expression>>,
}

#[derive(Debug, Clone, PartialEq)]
pub struct Parenthesis {
    inner: Box<Expression>,
}
