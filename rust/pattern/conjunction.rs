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
        error::{collect_err, MATCH_HAS_UNBOUNDED_NESTED_PATTERN},
        string::indent,
        validatable::Validatable,
        Result,
    },
    pattern::{NamedReferences, Pattern, Reference},
};
use std::{collections::HashSet, fmt, iter};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Conjunction {
    pub patterns: Vec<Pattern>,
}

impl Conjunction {
    pub fn new(patterns: Vec<Pattern>) -> Self {
        Conjunction { patterns }
    }

    pub fn references(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        Box::new(
            self.patterns
                .iter()
                .filter(|p| matches!(p, Pattern::Variable(_) | Pattern::Conjunction(_)))
                .flat_map(|p| match p {
                    Pattern::Variable(v) => v.references(),
                    Pattern::Conjunction(c) => c.references(),
                    _ => unreachable!(),
                }),
        )
    }

    pub fn has_named_variables(&self) -> bool {
        self.references().any(|r| r.is_name())
    }

    pub fn expect_is_bounded_by(&self, bounds: &HashSet<Reference>) -> Result<()> {
        let names = self.named_references();
        let combined_bounds = bounds.union(&names).cloned().collect();
        collect_err(
            &mut iter::once(expect_bounded(&names, &bounds, &self))
                .chain(self.patterns.iter().map(|p| p.expect_is_bounded_by(&combined_bounds))),
        )
    }
}

fn expect_bounded(
    names: &HashSet<Reference>,
    bounds: &HashSet<Reference>,
    conjunction: &Conjunction,
) -> Result<()> {
    if bounds.is_disjoint(names) {
        Err(MATCH_HAS_UNBOUNDED_NESTED_PATTERN
            .format(&[&conjunction.to_string().replace('\n', " ")]))?;
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

impl fmt::Display for Conjunction {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("{\n")?;
        f.write_str(
            &self.patterns.iter().map(|p| indent(&p.to_string()) + ";\n").collect::<String>(),
        )?;
        f.write_str("}")
    }
}
