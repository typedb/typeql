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

use crate::common::token::Operator::Not;
use crate::{ErrorMessage, Pattern, Variable};
use core::fmt;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Negation {
    pub pattern: Box<Pattern>,
}

impl Negation {
    pub fn into_pattern(self) -> Pattern {
        self.into()
    }
}

impl Into<Pattern> for Negation {
    fn into(self) -> Pattern {
        Pattern::Negation(self)
    }
}

impl From<Pattern> for Negation {
    fn from(pattern: Pattern) -> Self {
        Negation { pattern: Box::new(pattern) }
    }
}

impl<T: Into<Variable>> From<T> for Negation {
    fn from(variable: T) -> Self {
        Negation { pattern: Box::new(variable.into().into_pattern()) }
    }
}

impl<T: Into<Variable>> TryFrom<Result<T, ErrorMessage>> for Negation {
    type Error = ErrorMessage;

    fn try_from(variable: Result<T, ErrorMessage>) -> Result<Self, Self::Error> {
        Ok(Negation { pattern: Box::new(variable?.into().into_pattern()) })
    }
}

impl fmt::Display for Negation {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {{ {}; }}", Not, self.pattern)
    }
}
