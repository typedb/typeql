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
 */

use std::collections::HashSet;
use std::fmt;
use crate::common::token;
use crate::common::validatable::Validatable;
use crate::pattern::{Conjunction, NamedReferences, Pattern, Reference};

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct MatchClause {
    conjunction: Conjunction,
}

impl MatchClause {
    pub fn new(conjunction: Conjunction) -> Self {
        Self { conjunction }
    }

    pub fn from_patterns(patterns: Vec<Pattern>) -> Self {
        Self::new(Conjunction::new(patterns))
    }
}

impl Validatable for MatchClause {
    fn validate(&self) -> crate::common::Result<()> {
        todo!()
    }
}

impl NamedReferences for MatchClause {
    fn named_references(&self) -> HashSet<Reference> {
        self.conjunction.named_references()
    }
}

impl fmt::Display for MatchClause {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", token::Command::Match)?;

        for pattern in &self.conjunction.patterns {
            write!(f, "\n{pattern};")?;
        }

        Ok(())
    }
}