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

use std::{collections::HashSet, fmt, iter};

use itertools::Itertools;

use crate::{
    common::{
        error::{collect_err, TypeQLError},
        string::indent,
        validatable::Validatable,
        Result,
    },
    pattern::{Disjunction, NamedReferences, Normalisable, Pattern, Reference},
};

#[derive(Debug, Clone, Eq)]
pub struct Conjunction {
    pub patterns: Vec<Pattern>,
    normalised: Option<Disjunction>,
}

impl PartialEq for Conjunction {
    fn eq(&self, other: &Self) -> bool {
        self.patterns == other.patterns
    }
}

impl Conjunction {
    pub fn new(patterns: Vec<Pattern>) -> Self {
        Conjunction { patterns, normalised: None }
    }

    pub fn references(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        // Box::new(self.patterns.iter().filter(|p| matches!(p, Pattern::Variable(_) | Pattern::Conjunction(_))).flat_map(
        //     |p| match p {
        //         Pattern::Conjunction(c) => c.references(),
        //         Pattern::Disjunction(c) => c.references(),
        //         Pattern::Negation(c) => c.references(),
        //         Pattern::Variable(v) => v.references(),
        //     },
        // ))
        Box::new(self.patterns.iter().flat_map(|p| p.references()))
    }

    pub fn has_named_variables(&self) -> bool {
        self.references().any(|r| r.is_name())
    }

    pub fn expect_is_bounded_by(&self, bounds: &HashSet<Reference>) -> Result<()> {
        let names = self.named_references();
        let combined_bounds = bounds.union(&names).cloned().collect();
        collect_err(
            &mut iter::once(expect_bounded(&names, bounds, self))
                .chain(self.patterns.iter().map(|p| p.expect_is_bounded_by(&combined_bounds))),
        )
    }
}

fn expect_bounded(names: &HashSet<Reference>, bounds: &HashSet<Reference>, conjunction: &Conjunction) -> Result<()> {
    if bounds.is_disjoint(names) {
        Err(TypeQLError::MatchHasUnboundedNestedPattern(conjunction.clone().into()))?;
    }
    Ok(())
}

impl NamedReferences for Conjunction {
    fn named_references(&self) -> HashSet<Reference> {
        self.references().filter(|r| r.is_name()).cloned().collect()
    }
}

impl Validatable for Conjunction {
    fn validate(&self) -> Result<()> {
        Ok(())
    }
}

impl Normalisable for Conjunction {
    fn normalise(&mut self) -> Pattern {
        if self.normalised.is_none() {
            self.normalised = Some(self.compute_normalised().into_disjunction());
        }
        self.normalised.as_ref().unwrap().clone().into()
    }

    fn compute_normalised(&self) -> Pattern {
        let mut conjunctables = Vec::new();
        let mut disjunctions = Vec::new();
        self.patterns.iter().for_each(|pattern| match pattern {
            Pattern::Conjunction(conjunction) => {
                disjunctions.push(conjunction.compute_normalised().into_disjunction().patterns)
            }
            Pattern::Disjunction(disjunction) => {
                disjunctions.push(disjunction.compute_normalised().into_disjunction().patterns)
            }
            Pattern::Negation(negation) => conjunctables.push(negation.compute_normalised()),
            Pattern::Variable(variable) => conjunctables.push(variable.compute_normalised()),
        });
        disjunctions.push(vec![Conjunction::new(conjunctables).into()]);

        Disjunction::new(
            disjunctions
                .into_iter()
                .multi_cartesian_product()
                .map(|v| {
                    Conjunction::new(v.into_iter().flat_map(|c| c.into_conjunction().patterns.into_iter()).collect())
                        .into()
                })
                .collect(),
        )
        .into()
    }
}

impl fmt::Display for Conjunction {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("{\n")?;
        f.write_str(&self.patterns.iter().map(|p| indent(&p.to_string()) + ";\n").collect::<String>())?;
        f.write_str("}")
    }
}
