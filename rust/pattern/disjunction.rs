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

use std::{collections::HashSet, fmt};

use crate::{
    common::{error::collect_err, string::indent, token, validatable::Validatable, Result},
    pattern::{Conjunction, Normalisable, Pattern, Reference},
};

#[derive(Debug, Clone, Eq)]
pub struct Disjunction {
    pub patterns: Vec<Pattern>,
    normalised: Option<Box<Disjunction>>,
}

impl PartialEq for Disjunction {
    fn eq(&self, other: &Self) -> bool {
        self.patterns == other.patterns
    }
}

impl Disjunction {
    pub fn new(patterns: Vec<Pattern>) -> Self {
        Disjunction { patterns, normalised: None }
    }

    pub fn references(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        Box::new(self.patterns.iter().flat_map(|p| p.references()))
    }

    pub fn expect_is_bounded_by(&self, bounds: &HashSet<Reference>) -> Result<()> {
        collect_err(&mut self.patterns.iter().map(|p| p.expect_is_bounded_by(bounds)))
    }
}

impl Validatable for Disjunction {
    fn validate(&self) -> Result<()> {
        Ok(())
    }
}

impl Normalisable for Disjunction {
    fn normalise(&mut self) -> Pattern {
        if self.normalised.is_none() {
            self.normalised = Some(Box::new(self.compute_normalised().into_disjunction()));
        }
        self.normalised.as_ref().unwrap().as_ref().clone().into()
    }

    fn compute_normalised(&self) -> Pattern {
        Disjunction::new(
            self.patterns
                .iter()
                .flat_map(|pattern| match pattern {
                    Pattern::Conjunction(conjunction) => {
                        conjunction.compute_normalised().into_disjunction().patterns.into_iter()
                    }
                    Pattern::Disjunction(disjunction) => {
                        disjunction.compute_normalised().into_disjunction().patterns.into_iter()
                    }
                    Pattern::Negation(negation) => {
                        vec![Conjunction::new(vec![negation.compute_normalised()]).into()].into_iter()
                    }
                    Pattern::Variable(variable) => {
                        vec![Conjunction::new(vec![variable.clone().into()]).into()].into_iter()
                    }
                })
                .collect(),
        )
        .into()
    }
}

impl fmt::Display for Disjunction {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str(
            &self
                .patterns
                .iter()
                .map(|pattern| match pattern {
                    Pattern::Conjunction(conjunction) => conjunction.to_string(),
                    other => format!("{{\n{};\n}}", indent(&other.to_string())),
                })
                .collect::<Vec<_>>()
                .join(&format!(" {} ", token::Operator::Or)),
        )
    }
}
