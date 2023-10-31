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

use std::{collections::HashSet, fmt};

use crate::{
    common::{
        error::{collect_err, TypeQLError},
        token, Result,
    },
    variable::variable::VariableRef,
    write_joined,
};

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

    use crate::{
        common::token,
        variable::{ConceptVariable, ValueVariable, Variable},
    };

    #[derive(Clone, Debug, Eq, PartialEq)]
    pub struct SortVariable {
        pub variable: Variable,
        pub order: Option<token::Order>,
    }

    impl From<Variable> for SortVariable {
        fn from(variable: Variable) -> Self {
            SortVariable { variable, order: None }
        }
    }

    impl From<(Variable, token::Order)> for SortVariable {
        fn from(ordered_var: (Variable, token::Order)) -> Self {
            let (variable, order) = ordered_var;
            SortVariable { variable, order: Some(order) }
        }
    }

    impl From<(Variable, Option<token::Order>)> for SortVariable {
        fn from(ordered_var: (Variable, Option<token::Order>)) -> Self {
            let (variable, order) = ordered_var;
            SortVariable { variable, order }
        }
    }

    impl From<ConceptVariable> for SortVariable {
        fn from(variable: ConceptVariable) -> Self {
            SortVariable { variable: variable.into(), order: None }
        }
    }

    impl From<(ConceptVariable, token::Order)> for SortVariable {
        fn from(ordered_var: (ConceptVariable, token::Order)) -> Self {
            let (variable, order) = ordered_var;
            SortVariable { variable: variable.into(), order: Some(order) }
        }
    }

    impl From<(ConceptVariable, Option<token::Order>)> for SortVariable {
        fn from(ordered_var: (ConceptVariable, Option<token::Order>)) -> Self {
            let (variable, order) = ordered_var;
            SortVariable { variable: variable.into(), order }
        }
    }

    impl From<ValueVariable> for SortVariable {
        fn from(variable: ValueVariable) -> Self {
            SortVariable { variable: variable.into(), order: None }
        }
    }

    impl From<(ValueVariable, token::Order)> for SortVariable {
        fn from(ordered_var: (ValueVariable, token::Order)) -> Self {
            let (variable, order) = ordered_var;
            SortVariable { variable: variable.into(), order: Some(order) }
        }
    }

    impl From<(ValueVariable, Option<token::Order>)> for SortVariable {
        fn from(ordered_var: (ValueVariable, Option<token::Order>)) -> Self {
            let (variable, order) = ordered_var;
            SortVariable { variable: variable.into(), order }
        }
    }

    impl fmt::Display for SortVariable {
        fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
            write!(f, "{}", self.variable)?;
            if let Some(order) = self.order {
                write!(f, " {order}")?;
            }
            Ok(())
        }
    }
}

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct Sorting {
    pub(crate) vars: Vec<sorting::SortVariable>,
}

impl Sorting {
    pub fn new(vars: Vec<sorting::SortVariable>) -> Self {
        Sorting { vars }
    }

    pub(crate) fn validate(&self, available_variables: &HashSet<VariableRef<'_>>) -> Result {
        collect_err(self.vars.iter().map(|r| {
            available_variables
                .contains(&r.variable.as_ref())
                .then_some(())
                .ok_or_else(|| TypeQLError::SortVarNotBound(r.variable.clone()).into())
        }))
    }
}

impl<const N: usize, T: Into<sorting::SortVariable>> From<[T; N]> for Sorting {
    fn from(ordered_vars: [T; N]) -> Self {
        Self::new(ordered_vars.map(|ordered_var| ordered_var.into()).to_vec())
    }
}

impl<'a, T: Into<sorting::SortVariable> + Clone> From<&'a [T]> for Sorting {
    fn from(ordered_vars: &'a [T]) -> Self {
        Self::new(ordered_vars.iter().map(|ordered_var| ordered_var.clone().into()).collect())
    }
}

impl fmt::Display for Sorting {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", token::Modifier::Sort)?;
        write_joined!(f, ", ", self.vars)
    }
}

#[derive(Clone, Debug, Eq, PartialEq, Default)]
pub struct Limit {
    pub limit: usize,
}

impl fmt::Display for Limit {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Modifier::Limit, self.limit)
    }
}

#[derive(Clone, Debug, Eq, PartialEq, Default)]
pub struct Offset {
    pub offset: usize,
}

impl fmt::Display for Offset {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Modifier::Offset, self.offset)
    }
}
