/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{collections::HashSet, fmt, iter};

use itertools::Itertools;

use crate::{
    common::{
        error::{collect_err, TypeQLError},
        string::indent,
        token,
        validatable::Validatable,
        Result,
    },
    pattern::{Disjunction, Normalisable, Pattern, VariablesRetrieved},
    variable::variable::VariableRef,
};

#[derive(Debug, Clone, Eq)]
pub struct Conjunction {
    pub patterns: Vec<Pattern>,
    normalised: Option<Disjunction>,
}

impl Conjunction {
    pub fn new(patterns: Vec<Pattern>) -> Self {
        Conjunction { patterns, normalised: None }
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

impl Normalisable for Conjunction {
    fn normalise(&mut self) -> Pattern {
        if self.normalised.is_none() {
            self.normalised = Some(self.compute_normalised().into_disjunction());
        }
        self.normalised.as_ref().unwrap().clone().into()
    }

    fn compute_normalised(&self) -> Pattern {
        let mut conjunctables = Vec::new();
        let mut disjunctions = Vec::new();
        self.patterns.iter().for_each(|pattern| match pattern {
            Pattern::Conjunction(conjunction) => {
                disjunctions.push(conjunction.compute_normalised().into_disjunction().patterns)
            }
            Pattern::Disjunction(disjunction) => {
                disjunctions.push(disjunction.compute_normalised().into_disjunction().patterns)
            }
            Pattern::Negation(negation) => conjunctables.push(negation.compute_normalised()),
            Pattern::Statement(variable) => conjunctables.push(variable.compute_normalised()),
        });
        disjunctions.push(vec![Conjunction::new(conjunctables).into()]);

        Disjunction::new(
            disjunctions
                .into_iter()
                .multi_cartesian_product()
                .map(|v| {
                    Conjunction::new(v.into_iter().flat_map(|c| c.into_conjunction().patterns.into_iter()).collect())
                        .into()
                })
                .collect(),
        )
        .into()
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
