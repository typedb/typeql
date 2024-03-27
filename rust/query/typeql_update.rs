/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{error::collect_err, token, validatable::Validatable, Result},
    pattern::{ThingStatement, VariablesRetrieved},
    query::{modifier::Modifiers, writable::validate_non_empty, Sorting, TypeQLDelete},
    write_joined,
};

#[derive(Debug, Eq, PartialEq)]
pub struct TypeQLUpdate {
    pub query_delete: TypeQLDelete,
    pub insert_statements: Vec<ThingStatement>,
    pub modifiers: Modifiers,
}

impl TypeQLUpdate {
    pub fn sort(self, sorting: impl Into<Sorting>) -> Self {
        TypeQLUpdate { modifiers: self.modifiers.sort(sorting), ..self }
    }

    pub fn limit(self, limit: usize) -> Self {
        TypeQLUpdate { modifiers: self.modifiers.limit(limit), ..self }
    }

    pub fn offset(self, offset: usize) -> Self {
        TypeQLUpdate { modifiers: self.modifiers.offset(offset), ..self }
    }
}

impl Validatable for TypeQLUpdate {
    fn validate(&self) -> Result {
        let match_variables = self.query_delete.match_clause.retrieved_variables().collect();
        collect_err(
            [
                validate_non_empty(&self.insert_statements),
                self.query_delete.validate(),
                self.modifiers.sorting.as_ref().map(|s| s.validate(&match_variables)).unwrap_or(Ok(())),
            ]
            .into_iter()
            .chain(self.insert_statements.iter().map(Validatable::validate)),
        )
    }
}

impl fmt::Display for TypeQLUpdate {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        writeln!(f, "{}", self.query_delete)?;
        writeln!(f, "{}", token::Clause::Insert)?;
        write_joined!(f, ";\n", self.insert_statements)?;
        f.write_str(";")?;
        if !self.modifiers.is_empty() {
            write!(f, "\n{}", self.modifiers)
        } else {
            Ok(())
        }
    }
}
