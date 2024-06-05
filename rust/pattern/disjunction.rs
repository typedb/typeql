/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{collections::HashSet, fmt};

use crate::{
    common::{error::collect_err, Result, string::indent, token, validatable::Validatable},
    pattern::Pattern,
    variable::variable::VariableRef,
};

#[derive(Debug, Clone, Eq)]
pub struct Disjunction {
    pub patterns: Vec<Pattern>,
    normalised: Option<Box<Disjunction>>,
}

impl PartialEq for Disjunction {
    fn eq(&self, other: &Self) -> bool {
        self.patterns == other.patterns
    }
}

impl Disjunction {
    pub fn new(patterns: Vec<Pattern>) -> Self {
        Disjunction { patterns, normalised: None }
    }

    pub fn variables_recursive(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        Box::new(self.patterns.iter().flat_map(|p| p.variables_recursive()))
    }

    pub fn validate_is_bounded_by(&self, bounds: &HashSet<VariableRef<'_>>) -> Result {
        collect_err(self.patterns.iter().map(|p| p.validate_is_bounded_by(bounds)))
    }
}

impl Validatable for Disjunction {
    fn validate(&self) -> Result {
        Ok(())
    }
}

impl fmt::Display for Disjunction {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str(
            &self
                .patterns
                .iter()
                .map(|pattern| match pattern {
                    Pattern::Conjunction(conjunction) => conjunction.to_string(),
                    other => format!(
                        "{}\n{};\n{}",
                        token::Char::CurlyLeft,
                        indent(&other.to_string()),
                        token::Char::CurlyRight
                    ),
                })
                .collect::<Vec<_>>()
                .join(&format!(" {} ", token::LogicOperator::Or)),
        )
    }
}
