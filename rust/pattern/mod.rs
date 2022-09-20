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

mod conjunction;
pub use conjunction::*;

mod variable;
pub use variable::*;

mod constraint;
pub use constraint::*;

#[cfg(test)]
mod test;

use crate::enum_getter;
use std::fmt;
use std::fmt::Display;

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Pattern {
    Conjunction(Conjunction),
    Disjunction(()),
    Negation(()),
    Variable(Variable),
}

impl Pattern {
    enum_getter!(into_variable, Variable, Variable);

    pub fn into_type_variable(self) -> TypeVariable {
        self.into_variable().into_type()
    }
}

impl<T> From<T> for Pattern
    where
        Variable: From<T>,
{
    fn from(variable: T) -> Self {
        Pattern::Variable(Variable::from(variable))
    }
}

impl Display for Pattern {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Pattern::*;
        match self {
            Conjunction(conjunction) => write!(f, "{}", conjunction),
            Disjunction(()) => todo!(),
            Negation(()) => todo!(),
            Variable(variable) => write!(f, "{}", variable),
        }
    }
}
