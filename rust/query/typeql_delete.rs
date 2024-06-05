/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{collections::HashSet, fmt};

use crate::{
    common::{
        error::{collect_err, TypeQLError},
        token,
        validatable::Validatable,
        Result,
    },
    pattern::{ThingStatement, VariablesRetrieved},
    query::{modifier::Modifiers, writable::validate_non_empty, MatchClause, Sorting, TypeQLUpdate, Writable},
    write_joined,
};
use crate::variable::variable::VariableRef;

#[derive(Debug, Eq, PartialEq)]
pub struct TypeQLDelete {
    pub match_clause: MatchClause,
    pub statements: Vec<ThingStatement>,
    pub modifiers: Modifiers,
}

impl TypeQLDelete {
    pub fn insert(self, writable: impl Writable) -> TypeQLUpdate {
        TypeQLUpdate { query_delete: self, insert_statements: writable.statements(), modifiers: Default::default() }
    }

    pub fn sort(self, sorting: impl Into<Sorting>) -> Self {
        TypeQLDelete { modifiers: self.modifiers.sort(sorting), ..self }
    }

    pub fn limit(self, limit: usize) -> Self {
        TypeQLDelete { modifiers: self.modifiers.limit(limit), ..self }
    }

    pub fn offset(self, offset: usize) -> Self {
        TypeQLDelete { modifiers: self.modifiers.offset(offset), ..self }
    }
}

impl Validatable for TypeQLDelete {
    fn validate(&self) -> Result {
        let match_variables = self.match_clause.retrieved_variables().collect();
        collect_err(
            ([
                self.match_clause.validate(),
                validate_delete_in_scope(&match_variables, &self.statements),
                validate_non_empty(&self.statements),
                self.modifiers.sorting.as_ref().map(|s| s.validate(&match_variables)).unwrap_or(Ok(())),
            ]
            .into_iter())
            .chain(self.statements.iter().map(Validatable::validate)),
        )
    }
}

fn validate_delete_in_scope(scope_variables: &HashSet<VariableRef<'_>>, statements: &[ThingStatement]) -> Result {
    collect_err(statements.iter().flat_map(|v| v.variables()).filter(|r| r.is_name()).map(|r| -> Result {
        if scope_variables.contains(&r) {
            Ok(())
        } else {
            Err(TypeQLError::DeleteVarNotBound { variable: r.to_owned() })?
        }
    }))
}

impl fmt::Display for TypeQLDelete {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        writeln!(f, "{}", self.match_clause)?;
        writeln!(f, "{}", token::Clause::Delete)?;
        write_joined!(f, ";\n", self.statements)?;
        f.write_str(";")?;
        if !self.modifiers.is_empty() {
            write!(f, "\n{}", self.modifiers)
        } else {
            Ok(())
        }
    }
}
