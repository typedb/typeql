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

use super::Expression;
use crate::{
    common::token,
    pattern::{Reference, SubExpression},
    write_joined,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Function {
    pub(crate) function_name: token::Function,
    pub(crate) args: Vec<Box<Expression>>,
}

impl Function {
    pub fn references_recursive(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        Box::new(self.args.iter().flat_map(|expr| expr.references_recursive()))
    }
}

impl SubExpression for Function {}

impl fmt::Display for Function {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}(", self.function_name)?;
        write_joined!(f, ", ", self.args)?;
        write!(f, ")")
    }
}
