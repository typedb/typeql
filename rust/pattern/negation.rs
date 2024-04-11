/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use core::fmt;
use std::collections::HashSet;

use crate::{
    common::{error::TypeQLError, string::indent, token, validatable::Validatable, Result},
    pattern::{Conjunction, Disjunction, Normalisable, Pattern},
    variable::variable::VariableRef,
};

#[derive(Debug, Clone, Eq)]
pub struct Negation {
    pub pattern: Box<Pattern>,
    normalised: Option<Box<Negation>>,
}

impl PartialEq for Negation {
    fn eq(&self, other: &Self) -> bool {
        self.pattern == other.pattern
    }
}

impl Negation {
    pub fn new(pattern: Pattern) -> Self {
        Self { pattern: Box::new(pattern), normalised: None }
    }

    pub fn variables_recursive(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        self.pattern.variables_recursive()
    }

    pub fn validate_is_bounded_by(&self, bounds: &HashSet<VariableRef<'_>>) -> Result {
        self.pattern.validate_is_bounded_by(bounds)
    }
}

impl Validatable for Negation {
    fn validate(&self) -> Result {
        match self.pattern.as_ref() {
            Pattern::Negation(_) => Err(TypeQLError::RedundantNestedNegation)?,
            _ => Ok(()),
        }
    }
}

impl Normalisable for Negation {
    fn normalise(&mut self) -> Pattern {
        if self.normalised.is_none() {
            self.normalised = Some(Box::new(self.compute_normalised().into_negation()));
        }
        self.normalised.as_ref().unwrap().as_ref().clone().into()
    }

    fn compute_normalised(&self) -> Pattern {
        Negation::new(match self.pattern.as_ref() {
            Pattern::Conjunction(conjunction) => conjunction.compute_normalised(),
            Pattern::Disjunction(disjunction) => disjunction.compute_normalised(),
            Pattern::Negation(_) => panic!("{}", TypeQLError::RedundantNestedNegation),
            Pattern::Statement(variable) => {
                Disjunction::new(vec![Conjunction::new(vec![variable.clone().into()]).into()]).into()
            }
        })
        .into()
    }
}

impl fmt::Display for Negation {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        let pattern_string = self.pattern.to_string();
        if matches!(*self.pattern, Pattern::Conjunction(_)) {
            write!(f, "{} {}", token::LogicOperator::Not, pattern_string)
        } else if pattern_string.lines().count() > 1 {
            write!(
                f,
                "{} {}\n{};\n{}",
                token::LogicOperator::Not,
                token::Char::CurlyLeft,
                indent(&pattern_string),
                token::Char::CurlyRight
            )
        } else {
            write!(
                f,
                "{} {} {}; {}",
                token::LogicOperator::Not,
                token::Char::CurlyLeft,
                pattern_string,
                token::Char::CurlyRight
            )
        }
    }
}
