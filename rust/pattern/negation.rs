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
    common::{
        error::{ErrorMessage, REDUNDANT_NESTED_NEGATION},
        token,
        validatable::Validatable,
    },
    pattern::{Pattern, Reference},
};
use core::fmt;
use std::collections::HashSet;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Negation {
    pub pattern: Box<Pattern>,
}

impl Negation {
    pub fn new(pattern: Pattern) -> Self {
        Self { pattern: Box::new(pattern) }
    }

    pub fn expect_is_bounded_by(
        &self,
        bounds: &HashSet<Reference>,
    ) -> Result<(), Vec<ErrorMessage>> {
        self.pattern.expect_is_bounded_by(bounds)
    }
}

impl Validatable for Negation {
    fn validate(&self) -> Result<(), Vec<ErrorMessage>> {
        match self.pattern.as_ref() {
            Pattern::Negation(_) => Err(vec![REDUNDANT_NESTED_NEGATION.format(&[])]),
            _ => Ok(()),
        }
    }
}

impl fmt::Display for Negation {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {{ {}; }}", token::Operator::Not, self.pattern)
    }
}
