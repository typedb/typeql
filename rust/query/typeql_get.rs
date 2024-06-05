/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{collections::HashSet, fmt};

use itertools::Itertools;

use crate::{
    common::{
        error::{collect_err, TypeQLError},
        token,
        validatable::Validatable,
        Result,
    },
    pattern::{Conjunction, VariablesRetrieved},
    query::{
        modifier::{Modifiers, Sorting},
        AggregateQueryBuilder, MatchClause, TypeQLGetGroup,
    },
    write_joined,
};
use crate::variable::Variable;
use crate::variable::variable::VariableRef;

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct TypeQLGet {
    pub match_clause: MatchClause,
    pub filter: Filter,
    pub modifiers: Modifiers,
}

impl AggregateQueryBuilder for TypeQLGet {}

impl TypeQLGet {
    pub fn new(match_clause: MatchClause) -> Self {
        TypeQLGet { match_clause, filter: Filter::default(), modifiers: Modifiers::default() }
    }

    pub fn sort(self, sorting: impl Into<Sorting>) -> Self {
        TypeQLGet { modifiers: self.modifiers.sort(sorting), ..self }
    }

    pub fn limit(self, limit: usize) -> Self {
        TypeQLGet { modifiers: self.modifiers.limit(limit), ..self }
    }

    pub fn offset(self, offset: usize) -> Self {
        TypeQLGet { modifiers: self.modifiers.offset(offset), ..self }
    }

    pub fn group(self, var: impl Into<Variable>) -> TypeQLGetGroup {
        TypeQLGetGroup { query: self, group_var: var.into() }
    }
}

impl Validatable for TypeQLGet {
    fn validate(&self) -> Result {
        let match_variables = self.match_clause.retrieved_variables().collect();
        let filter_vars = HashSet::from_iter(self.filter.vars.iter().map(Variable::as_ref));
        let retrieved_variables = if self.filter.vars.is_empty() { &match_variables } else { &filter_vars };
        collect_err([
            self.match_clause.validate(),
            validate_filters_are_in_scope(&match_variables, &self.filter),
            self.modifiers.sorting.as_ref().map(|s| s.validate(retrieved_variables)).unwrap_or(Ok(())),
        ])
    }
}

impl VariablesRetrieved for TypeQLGet {
    fn retrieved_variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        if !self.filter.vars.is_empty() {
            Box::new(self.filter.vars.iter().map(Variable::as_ref))
        } else {
            self.match_clause.retrieved_variables()
        }
    }
}

fn validate_filters_are_in_scope(match_variables: &HashSet<VariableRef<'_>>, filter: &Filter) -> Result {
    let mut seen = HashSet::new();
    collect_err(filter.vars.iter().map(|variable| {
        if !variable.is_named() {
            Err(TypeQLError::VariableNotNamed.into())
        } else if !match_variables.contains(&variable.as_ref()) {
            Err(TypeQLError::GetVarNotBound { variable: variable.clone() }.into())
        } else if seen.contains(&variable) {
            Err(TypeQLError::GetVarRepeating { variable: variable.clone() }.into())
        } else {
            seen.insert(variable);
            Ok(())
        }
    }))
}

impl fmt::Display for TypeQLGet {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.match_clause)?;
        write!(f, "\n{}", self.filter)?;
        if !self.modifiers.is_empty() {
            write!(f, "\n{}", self.modifiers)
        } else {
            Ok(())
        }
    }
}

#[derive(Clone, Debug, Eq, PartialEq, Default)]
pub struct Filter {
    pub vars: Vec<Variable>,
}

impl fmt::Display for Filter {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", token::Clause::Get)?;
        if !self.vars.is_empty() {
            write!(f, " ")?;
            write_joined!(f, ", ", self.vars)?;
        }
        write!(f, ";")
    }
}
