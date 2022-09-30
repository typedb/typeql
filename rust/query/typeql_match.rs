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
        Self { conjunction, modifiers: Modifiers::default() }
    }

    pub fn into_query(self) -> Query {
        Query::Match(self)
    }

    pub fn filter(self, vars: Vec<UnboundVariable>) -> TypeQLMatch {
        TypeQLMatch { modifiers: self.modifiers.filter(vars), ..self }
    }
}

impl MatchQueryBuilder for TypeQLMatch {
    fn get<T: Into<String>, const N: usize>(self, vars: [T; N]) -> Self {
        self.filter(vars.into_iter().map(|s| UnboundVariable::named(s.into())).collect())
    }

    fn sort(self, sorting: impl Into<Sorting>) -> TypeQLMatch {
        TypeQLMatch { modifiers: self.modifiers.sort(sorting), ..self }
    }

    fn limit(self, limit: usize) -> TypeQLMatch {
        TypeQLMatch { modifiers: self.modifiers.limit(limit), ..self }
    }

    fn offset(self, offset: usize) -> TypeQLMatch {
        TypeQLMatch { modifiers: self.modifiers.offset(offset), ..self }
    }
}

impl Display for TypeQLMatch {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
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
    pub limit: Option<usize>,
    pub offset: Option<usize>,
}

impl Modifiers {
    pub fn is_empty(&self) -> bool {
        self.filter.is_empty()
            && self.sorting.is_none()
            && self.limit.is_none()
            && self.offset.is_none()
    }

    pub fn filter(self, vars: Vec<UnboundVariable>) -> Self {
        Self { filter: vars, ..self }
    }

    pub fn sort(self, sorting: impl Into<Sorting>) -> Self {
        Self { sorting: Some(sorting.into()), ..self }
    }

    pub fn limit(self, limit: usize) -> Self {
        Self { limit: Some(limit), ..self }
    }

    pub fn offset(self, offset: usize) -> Self {
        Self { offset: Some(offset), ..self }
    }
}

impl Display for Modifiers {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        let mut trail = "";
        if !self.filter.is_empty() {
            f.write_str("get ")?;
            write_joined!(f, self.filter, ", ")?;
            f.write_str(";")?;
            trail = " ";
        }
        if let Some(sorting) = &self.sorting {
            write!(f, "{}sort {};", trail, sorting)?;
            trail = " ";
        }
        if let Some(offset) = &self.offset {
            write!(f, "{}offset {};", trail, offset)?;
            trail = " ";
        }
        if let Some(limit) = &self.limit {
            write!(f, "{}limit {};", trail, limit)?;
        }
        Ok(())
    }
}

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct OrderedVariable {
    pub var: UnboundVariable,
    pub order: Option<String>, // FIXME
}

impl OrderedVariable {
    fn new(var: UnboundVariable, order: &str) -> OrderedVariable {
        OrderedVariable {
            var,
            order: match order {
                "" => None,
                order => Some(order.to_string()),
            },
        }
    }
}

impl Display for OrderedVariable {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.var)?;
        if let Some(order) = &self.order {
            write!(f, " {}", order)?;
        }
        Ok(())
    }
}

#[derive(Debug, Eq, PartialEq)]
pub struct Sorting {
    vars: Vec<OrderedVariable>,
}

impl Sorting {
    pub fn new(vars: Vec<OrderedVariable>) -> Self {
        Sorting { vars }
    }
}

impl From<&str> for Sorting {
    fn from(var_name: &str) -> Self {
        Self::from([(var_name, "")])
    }
}

impl<const N: usize> From<([(&str, &str); N])> for Sorting {
    fn from(ordered_vars: [(&str, &str); N]) -> Self {
        Self::new(ordered_vars.map(|(name, order)| OrderedVariable::new(var(name), order)).to_vec())
    }
}

impl From<Vec<UnboundVariable>> for Sorting {
    fn from(vars: Vec<UnboundVariable>) -> Self {
        Self::new(vars.into_iter().map(|name| OrderedVariable::new(var(name), "")).collect())
    }
}

impl Display for Sorting {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        write_joined!(f, self.vars, ", ")
    }
}
