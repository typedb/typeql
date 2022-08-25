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
use std::fmt::Display;

use crate::query::*;
use crate::write_joined;

#[derive(Debug, Eq, PartialEq)]
pub struct TypeQLMatch {
    pub conjunction: Conjunction,
    pub filter: Vec<UnboundVariable>,
}

impl TypeQLMatch {
    pub fn into_query(self) -> Query {
        Query::Match(self)
    }

    pub fn filter(self, vars: Vec<Pattern>) -> TypeQLMatch {
        TypeQLMatch {
            conjunction: self.conjunction,
            filter: vars
                .into_iter()
                .map(Pattern::into_unbound_variable)
                .collect(),
        }
    }
}

impl Display for TypeQLMatch {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("match")?;

        for pattern in &self.conjunction.patterns {
            write!(f, "\n{};", pattern)?;
        }

        if !self.filter.is_empty() {
            f.write_str("\n")?; // separate because there is only meant to be one newline before all modifiers
            f.write_str("get ")?;
            write_joined!(f, self.filter, ", ")?;
            f.write_str(";")?;
        }

        Ok(())
    }
}
