/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{collections::HashSet, fmt, iter};

use crate::{
    common::{
        error::{collect_err, TypeQLError},
        Result,
        string::indent,
        token,
        validatable::Validatable,
    },
    pattern::{Disjunction, Pattern, VariablesRetrieved},
};
use crate::variable::variable::VariableRef;

#[derive(Debug, Clone, Eq)]
pub struct Conjunction {
    pub patterns: Vec<Pattern>,
}

impl Conjunction {
    pub fn new(patterns: Vec<Pattern>) -> Self {
        Conjunction { patterns }
    }

    pub fn variables_recursive(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        Box::new(self.patterns.iter().flat_map(|p| p.variables_recursive()))
    }

    pub fn validate_is_bounded_by(&self, bounds: &HashSet<VariableRef<'_>>) -> Result {
        let names = self.retrieved_variables().collect();
        let combined_bounds = bounds.union(&names).cloned().collect();
        collect_err(
            iter::once(validate_bounded(&names, bounds, self))
                .chain(self.patterns.iter().map(|p| p.validate_is_bounded_by(&combined_bounds))),
        )
    }
}

fn validate_bounded(
    names: &HashSet<VariableRef<'_>>,
    bounds: &HashSet<VariableRef<'_>>,
    conjunction: &Conjunction,
) -> Result {
    if bounds.is_disjoint(names) {
        Err(TypeQLError::MatchHasUnboundedNestedPattern { pattern: conjunction.clone().into() })?;
    }
    Ok(())
}

impl PartialEq for Conjunction {
    fn eq(&self, other: &Self) -> bool {
        self.patterns == other.patterns
    }
}

impl VariablesRetrieved for Conjunction {
    fn retrieved_variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        Box::new(
            self.patterns.iter().filter(|p| matches!(p, Pattern::Statement(_) | Pattern::Conjunction(_))).flat_map(
                |p| match p {
                    Pattern::Statement(v) => v.variables(),
                    Pattern::Conjunction(c) => c.retrieved_variables(),
                    _ => unreachable!(),
                },
            ),
        )
    }
}

impl Validatable for Conjunction {
    fn validate(&self) -> Result {
        Ok(())
    }
}

impl fmt::Display for Conjunction {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str(token::Char::CurlyLeft.as_str())?;
        f.write_str("\n")?;
        f.write_str(&self.patterns.iter().map(|p| indent(&p.to_string()) + ";\n").collect::<String>())?;
        f.write_str(token::Char::CurlyRight.as_str())
    }
}
