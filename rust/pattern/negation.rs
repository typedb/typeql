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
    common::{error::REDUNDANT_NESTED_NEGATION, token, validatable::Validatable, Result},
    pattern::{Conjunction, Disjunction, Normalisable, Pattern, Reference},
};
use core::fmt;
use std::collections::HashSet;

#[derive(Debug, Clone, Eq)]
pub struct Negation {
    pub pattern: Box<Pattern>,
    normalised: Option<Box<Negation>>,
}

impl PartialEq for Negation {
    fn eq(&self, other: &Self) -> bool {
        self.pattern == other.pattern
    }
}

impl Negation {
    pub fn new(pattern: Pattern) -> Self {
        Self { pattern: Box::new(pattern), normalised: None }
    }

    pub fn expect_is_bounded_by(&self, bounds: &HashSet<Reference>) -> Result<()> {
        self.pattern.expect_is_bounded_by(bounds)
    }
}

impl Validatable for Negation {
    fn validate(&self) -> Result<()> {
        match self.pattern.as_ref() {
            Pattern::Negation(_) => Err(REDUNDANT_NESTED_NEGATION.format(&[]))?,
            _ => Ok(()),
        }
    }
}

impl Normalisable for Negation {
    fn normalise(&mut self) -> Pattern {
        if self.normalised.is_none() {
            self.normalised = Some(Box::new(self.compute_normalised().into_negation()));
        }
        self.normalised.as_ref().unwrap().as_ref().clone().into()
    }

    fn compute_normalised(&self) -> Pattern {
        Negation::new(match self.pattern.as_ref() {
            Pattern::Conjunction(conjunction) => conjunction.compute_normalised(),
            Pattern::Disjunction(disjunction) => disjunction.compute_normalised(),
            Pattern::Negation(_) => panic!("{}", REDUNDANT_NESTED_NEGATION.format(&[])),
            Pattern::Variable(variable) => {
                Disjunction::new(vec![Conjunction::new(vec![variable.clone().into()]).into()])
                    .into()
            }
        })
        .into()
    }
}

impl fmt::Display for Negation {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {{ {}; }}", token::Operator::Not, self.pattern)
    }
}
