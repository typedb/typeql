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

use std::fmt;
use crate::{common, write_joined};
use crate::common::error::TypeQLError;
use crate::common::token;
use crate::pattern::UnboundVariable;

#[derive(Clone, Debug, Eq, PartialEq, Default)]
pub struct Modifiers {
    pub sorting: Option<Sorting>,
    pub limit: Option<Limit>,
    pub offset: Option<Offset>,
}

impl Modifiers {
    pub fn is_empty(&self) -> bool {
        self.sorting.is_none() && self.limit.is_none() && self.offset.is_none()
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
        if !self.is_empty() {
            write_joined!(f, "; ", self.sorting, self.offset, self.limit)?;
            f.write_str(";")
        } else {
            Ok(())
        }
    }
}

pub mod sorting {
    use std::fmt;

    use crate::{common::token, pattern::UnboundVariable};

    #[derive(Clone, Debug, Eq, PartialEq)]
    pub struct OrderedVariable {
        pub var: UnboundVariable,
        pub order: Option<token::Order>,
    }

    impl OrderedVariable {
        pub fn new(var: UnboundVariable, order: Option<token::Order>) -> Self {
            OrderedVariable { var, order }
        }
    }

    impl<T: Into<UnboundVariable>> From<(T, token::Order)> for OrderedVariable {
        fn from(ordered_var: (T, token::Order)) -> Self {
            let (variable, order) = ordered_var;
            Self::new(variable.into(), Some(order))
        }
    }

    impl<T: Into<UnboundVariable>> From<T> for OrderedVariable {
        fn from(variable: T) -> Self {
            Self::new(variable.into(), None)
        }
    }

    impl fmt::Display for OrderedVariable {
        fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
            write!(f, "{}", self.var)?;
            if let Some(order) = &self.order {
                write!(f, " {order}")?;
            }
            Ok(())
        }
    }
}

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct Sorting {
    pub(crate) vars: Vec<sorting::OrderedVariable>,
}

impl Sorting {
    pub fn new(vars: Vec<sorting::OrderedVariable>) -> Self {
        Sorting { vars }
    }

    pub fn get_order(&self, var: UnboundVariable) -> common::Result<token::Order> {
        self.vars
            .iter()
            .find_map(|v| (v.var == var).then_some(v.order.unwrap_or(token::Order::Asc)))
            .ok_or_else(|| TypeQLError::VariableNotSorted(var).into())
    }
}

impl<const N: usize, T: Into<sorting::OrderedVariable>> From<[T; N]> for Sorting {
    fn from(ordered_vars: [T; N]) -> Self {
        Self::new(ordered_vars.map(|ordered_var| ordered_var.into()).to_vec())
    }
}

impl<'a, T: Into<sorting::OrderedVariable> + Clone> From<&'a [T]> for Sorting {
    fn from(ordered_vars: &'a [T]) -> Self {
        Self::new(ordered_vars.iter().map(|ordered_var| ordered_var.clone().into()).collect())
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
