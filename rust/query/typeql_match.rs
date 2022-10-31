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
    common::token,
    pattern::{Conjunction, Pattern, UnboundVariable},
    query::{AggregateQueryBuilder, TypeQLDelete, TypeQLInsert, TypeQLMatchGroup, Writable},
    var, write_joined,
};
use std::fmt;

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct TypeQLMatch {
    pub conjunction: Conjunction,
    pub modifiers: Modifiers,
}

impl AggregateQueryBuilder for TypeQLMatch {}

impl TypeQLMatch {
    pub fn new(patterns: Vec<Pattern>) -> Self {
        Self { conjunction: Conjunction::new(patterns), modifiers: Modifiers::default() }
    }

    pub fn filter(self, vars: Vec<UnboundVariable>) -> Self {
        TypeQLMatch { modifiers: self.modifiers.filter(vars), ..self }
    }

    pub fn get<T: Into<String>, const N: usize>(self, vars: [T; N]) -> Self {
        self.filter(vars.into_iter().map(|s| UnboundVariable::named(s.into())).collect())
    }

    pub fn sort(self, sorting: impl Into<Sorting>) -> Self {
        TypeQLMatch { modifiers: self.modifiers.sort(sorting), ..self }
    }

    pub fn limit(self, limit: usize) -> Self {
        TypeQLMatch { modifiers: self.modifiers.limit(limit), ..self }
    }

    pub fn offset(self, offset: usize) -> Self {
        TypeQLMatch { modifiers: self.modifiers.offset(offset), ..self }
    }

    pub fn insert(self, vars: impl Writable) -> TypeQLInsert {
        TypeQLInsert { match_query: Some(self), variables: vars.vars() }
    }

    pub fn delete(self, vars: impl Writable) -> TypeQLDelete {
        TypeQLDelete { match_query: self, variables: vars.vars() }
    }

    pub fn group(self, var: impl Into<UnboundVariable>) -> TypeQLMatchGroup {
        TypeQLMatchGroup { query: self, group_var: var.into() }
    }
}

impl fmt::Display for TypeQLMatch {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", token::Command::Match)?;

        for pattern in &self.conjunction.patterns {
            write!(f, "\n{};", pattern)?;
        }

        if !self.modifiers.is_empty() {
            write!(f, "\n{}", self.modifiers)?;
        }

        Ok(())
    }
}

#[derive(Clone, Debug, Eq, PartialEq, Default)]
pub struct Modifiers {
    pub filter: Option<Filter>,
    pub sorting: Option<Sorting>,
    pub limit: Option<Limit>,
    pub offset: Option<Offset>,
}

impl Modifiers {
    pub fn is_empty(&self) -> bool {
        self.filter.is_none()
            && self.sorting.is_none()
            && self.limit.is_none()
            && self.offset.is_none()
    }

    pub fn filter(self, vars: Vec<UnboundVariable>) -> Self {
        Self { filter: Some(Filter { vars }), ..self }
    }

    pub fn sort(self, sorting: impl Into<Sorting>) -> Self {
        Self { sorting: Some(sorting.into()), ..self }
    }

    pub fn limit(self, limit: usize) -> Self {
        Self { limit: Some(Limit { limit }), ..self }
    }

    pub fn offset(self, offset: usize) -> Self {
        Self { offset: Some(Offset { offset }), ..self }
    }
}

impl fmt::Display for Modifiers {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write_joined!(f, "; ", self.filter, self.sorting, self.offset, self.limit)?;
        f.write_str(";")
    }
}

#[derive(Clone, Debug, Eq, PartialEq, Default)]
pub struct Filter {
    pub vars: Vec<UnboundVariable>,
}

impl fmt::Display for Filter {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", token::Filter::Get)?;
        write_joined!(f, ", ", self.vars)
    }
}

pub mod sorting {
    use crate::pattern::UnboundVariable;
    use std::fmt;

    #[derive(Clone, Debug, Eq, PartialEq)]
    pub struct OrderedVariable {
        pub var: UnboundVariable,
        pub order: Option<String>, // FIXME
    }

    impl OrderedVariable {
        pub fn new(var: UnboundVariable, order: &str) -> Self {
            OrderedVariable {
                var,
                order: match order {
                    "" => None,
                    order => Some(order.to_string()),
                },
            }
        }
    }

    impl fmt::Display for OrderedVariable {
        fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
            write!(f, "{}", self.var)?;
            if let Some(order) = &self.order {
                write!(f, " {}", order)?;
            }
            Ok(())
        }
    }
}

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct Sorting {
    vars: Vec<sorting::OrderedVariable>,
}

impl Sorting {
    pub fn new(vars: Vec<sorting::OrderedVariable>) -> Self {
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
        Self::new(
            ordered_vars
                .map(|(name, order)| sorting::OrderedVariable::new(var(name), order))
                .to_vec(),
        )
    }
}

impl From<Vec<UnboundVariable>> for Sorting {
    fn from(vars: Vec<UnboundVariable>) -> Self {
        Self::new(
            vars.into_iter().map(|name| sorting::OrderedVariable::new(var(name), "")).collect(),
        )
    }
}

impl fmt::Display for Sorting {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", token::Filter::Sort)?;
        write_joined!(f, ", ", self.vars)
    }
}

#[derive(Clone, Debug, Eq, PartialEq, Default)]
pub struct Limit {
    pub limit: usize,
}

impl fmt::Display for Limit {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Filter::Limit, self.limit)
    }
}

#[derive(Clone, Debug, Eq, PartialEq, Default)]
pub struct Offset {
    pub offset: usize,
}

impl fmt::Display for Offset {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Filter::Offset, self.offset)
    }
}
