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
    pub modifiers: Modifiers,
}

impl TypeQLMatch {
    pub fn new(conjunction: Conjunction) -> Self {
          Self {
              conjunction, modifiers: Modifiers::default()
          }
    }

    pub fn into_query(self) -> Query {
        Query::Match(self)
    }

    pub fn filter(self, vars: Vec<Pattern>) -> TypeQLMatch {
        TypeQLMatch {
            modifiers: self.modifiers.filter(vars),
            ..self
        }
    }

    pub fn sort(self, sorting: impl Into<Sorting>) -> TypeQLMatch {
        TypeQLMatch {
            modifiers: self.modifiers.sort(sorting),
            ..self
        }
    }
}

impl Display for TypeQLMatch {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("match")?;

        for pattern in &self.conjunction.patterns {
            write!(f, "\n{};", pattern)?;
        }

        if !self.modifiers.is_empty() {
            write!(f, "\n{}", self.modifiers)?;
        }

        Ok(())
    }
}

#[derive(Debug, Eq, PartialEq, Default)]
pub struct Modifiers {
    pub filter: Vec<UnboundVariable>,
    pub sorting: Option<Sorting>,
}

impl Modifiers {
    pub fn is_empty(&self) -> bool {
        self.filter.is_empty() && self.sorting.is_none()
    }

    pub fn filter(self, vars: Vec<Pattern>) -> Self {
        Self {
            filter: vars
                .into_iter()
                .map(Pattern::into_unbound_variable)
                .collect(),
            ..self
        }
    }

    pub fn sort(self, sorting: impl Into<Sorting>) -> Self {
        Self {
            sorting: Some(sorting.into()),
            ..self
        }
    }
}

impl Display for Modifiers {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if !self.filter.is_empty() {
            f.write_str("get ")?;
            write_joined!(f, self.filter, ", ")?;
            f.write_str(";")?;
        }
        if let Some(sorting) = &self.sorting {
            write!(f, "{};", sorting)?;
        }
        Ok(())
    }
}

#[derive(Debug, Eq, PartialEq)]
pub struct Sorting {
    vars: Vec<UnboundVariable>,
    order: String, // FIXME token
}

impl Sorting {
    pub fn new(vars: Vec<UnboundVariable>, order: &str) -> Self {
        Sorting {
            vars,
            order: order.to_string(),
        }
    }
}

impl From<&str> for Sorting {
    fn from(var_name: &str) -> Self {
        Self::new(vec![var(var_name)], "asc")
    }
}
impl<const N: usize> From<([&str; N], &'static str)> for Sorting {
    fn from((vars, order): ([&str; N], &'static str)) -> Self {
        Self::new(vars.map(var).to_vec(), order)
    }
}
impl From<Vec<Pattern>> for Sorting {
    fn from(vars: Vec<Pattern>) -> Self {
        Self::new(vars.into_iter().map(Pattern::into_unbound_variable).collect(), "asc")
    }
}

impl Display for Sorting {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        f.write_str("sort ")?;
        write_joined!(f, self.vars, ", ")?;
        write!(f, " {}", self.order)?;
        Ok(())
    }
}

