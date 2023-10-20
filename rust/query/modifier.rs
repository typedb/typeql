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

use crate::write_joined;
use crate::common::token;

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

    use crate::common::token;
    use crate::variable::{ConceptVariable, ValueVariable, Variable};

    #[derive(Clone, Debug, Eq, PartialEq)]
    pub enum SortedVariable {
        Concept(ConceptVariable, Option<token::Order>),
        Value(ValueVariable, Option<token::Order>),
    }

    impl SortedVariable {
        pub fn new_concept(var: ConceptVariable, order: Option<token::Order>) -> Self {
            SortedVariable::Concept(var, order)
        }

        pub fn new_value(var: ValueVariable, order: Option<token::Order>) -> Self {
            SortedVariable::Value(var, order)
        }
    }

    impl From<Variable> for SortedVariable {
        fn from(variable: Variable) -> Self {
            match variable {
                Variable::Concept(var) => SortedVariable::Concept(var, None),
                Variable::Value(var) => SortedVariable::Value(var, None)
            }
        }
    }

    impl From<(Variable, token::Order)> for SortedVariable {
        fn from(ordered_var: (Variable, token::Order)) -> Self {
            let (v, order) = ordered_var;
            match v {
                Variable::Concept(var) => SortedVariable::Concept(var, Some(order)),
                Variable::Value(var) => SortedVariable::Value(var, Some(order))
            }
        }
    }

    impl From<ConceptVariable> for SortedVariable {
        fn from(variable: ConceptVariable) -> Self {
            SortedVariable::Concept(variable, None)
        }
    }

    impl From<(ConceptVariable, token::Order)> for SortedVariable {
        fn from(ordered_var: (ConceptVariable, token::Order)) -> Self {
            let (variable, order) = ordered_var;
            SortedVariable::Concept(variable, Some(order))
        }
    }

    impl From<ValueVariable> for SortedVariable {
        fn from(variable: ValueVariable) -> Self {
            SortedVariable::Value(variable, None)
        }
    }

    impl From<(ValueVariable, token::Order)> for SortedVariable {
        fn from(ordered_var: (ValueVariable, token::Order)) -> Self {
            let (variable, order) = ordered_var;
            SortedVariable::Value(variable, Some(order))
        }
    }

    impl fmt::Display for SortedVariable {
        fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
            match self {
                SortedVariable::Concept(var, order) => {
                    write!(f, "{}", var)?;
                    if let Some(order) = order {
                        write!(f, " {order}")?;
                    }
                }
                SortedVariable::Value(var, order) => {
                    write!(f, "{}", var)?;
                    if let Some(order) = order {
                        write!(f, " {order}")?;
                    }
                }
            }
            Ok(())
        }
    }
}

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct Sorting {
    pub(crate) vars: Vec<sorting::SortedVariable>,
}

impl Sorting {
    pub fn new(vars: Vec<sorting::SortedVariable>) -> Self {
        Sorting { vars }
    }
}

impl<const N: usize, T: Into<sorting::SortedVariable>> From<[T; N]> for Sorting {
    fn from(ordered_vars: [T; N]) -> Self {
        Self::new(ordered_vars.map(|ordered_var| ordered_var.into()).to_vec())
    }
}

impl<'a, T: Into<sorting::SortedVariable> + Clone> From<&'a [T]> for Sorting {
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
