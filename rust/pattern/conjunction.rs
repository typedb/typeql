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
        error::{ErrorMessage, MATCH_HAS_UNBOUNDED_NESTED_PATTERN},
        string::indent,
    },
    pattern::{Pattern, Reference},
};
use std::{collections::HashSet, fmt};

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
        self.references().filter(|r| r.is_name()).next().is_some()
    }

    pub fn names(&self) -> HashSet<String> {
        self.references()
            .filter(|r| r.is_name())
            .map(|r| match r {
                Reference::Name(s) => s.clone(),
                _ => unreachable!(),
            })
            .collect()
    }

    pub fn expect_is_bounded_by(&self, bounds: &HashSet<String>) -> Result<(), ErrorMessage> {
        let names = self.names();
        if names.is_disjoint(bounds) {
            Err(MATCH_HAS_UNBOUNDED_NESTED_PATTERN.format(&[&self.to_string().replace("\n", " ")]))?;
        }
        let bounds = bounds.union(&names).cloned().collect();
        self.patterns.iter().map(|p| p.expect_is_bounded_by(&bounds)).collect()
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
