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

mod constraint;
pub use constraint::*;

mod disjunction;
pub use disjunction::*;

mod label;
pub use label::*;

mod schema;
pub use schema::*;

mod negation;
pub use negation::*;

mod variable;
pub use variable::*;

#[cfg(test)]
mod test;

use crate::enum_getter;
use std::fmt;

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Pattern {
    Conjunction(Conjunction),
    Disjunction(Disjunction),
    Negation(Negation),
    Rule(RuleDefinition),
    RuleDeclaration(RuleDeclaration),
    Variable(Variable),
}

impl Pattern {
    enum_getter!(into_conjunction, Conjunction, Conjunction);
    enum_getter!(into_disjunction, Disjunction, Disjunction);
    enum_getter!(into_negation, Negation, Negation);
    enum_getter!(into_rule, Rule, RuleDefinition);
    enum_getter!(into_rule_declaration, RuleDeclaration, RuleDeclaration);
    enum_getter!(into_variable, Variable, Variable);
}

impl<T> From<T> for Pattern
where
    Variable: From<T>,
{
    fn from(variable: T) -> Self {
        Pattern::Variable(Variable::from(variable))
    }
}

impl From<Negation> for Pattern {
    fn from(negation: Negation) -> Self {
        Pattern::Negation(negation)
    }
}

impl fmt::Display for Pattern {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Pattern::*;
        match self {
            Conjunction(conjunction) => write!(f, "{}", conjunction),
            Disjunction(disjunction) => write!(f, "{}", disjunction),
            Negation(negation) => write!(f, "{}", negation),
            Rule(rule) => write!(f, "{}", rule),
            RuleDeclaration(rule_declaration) => write!(f, "{}", rule_declaration),
            Variable(variable) => write!(f, "{}", variable),
        }
    }
}
