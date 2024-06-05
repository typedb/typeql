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
    query::{modifier::Modifiers, writable::validate_non_empty, MatchClause, Sorting},
    write_joined,
};
use crate::variable::variable::VariableRef;

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct TypeQLInsert {
    pub match_clause: Option<MatchClause>,
    pub statements: Vec<ThingStatement>,
    pub modifiers: Modifiers,
}

impl TypeQLInsert {
    pub fn new(statements: Vec<ThingStatement>) -> Self {
        TypeQLInsert { match_clause: None, statements, modifiers: Modifiers::default() }
    }

    pub fn sort(self, sorting: impl Into<Sorting>) -> Self {
        TypeQLInsert { modifiers: self.modifiers.sort(sorting), ..self }
    }

    pub fn limit(self, limit: usize) -> Self {
        TypeQLInsert { modifiers: self.modifiers.limit(limit), ..self }
    }

    pub fn offset(self, offset: usize) -> Self {
        TypeQLInsert { modifiers: self.modifiers.offset(offset), ..self }
    }

    fn validate_modifiers_have_match_clause(&self) -> Result {
        if !self.modifiers.is_empty() && self.match_clause.is_none() {
            Err(TypeQLError::InsertModifiersRequireMatch { insert: self.to_string() })?
        } else {
            Ok(())
        }
    }
}

impl Validatable for TypeQLInsert {
    fn validate(&self) -> Result {
        collect_err(
            [
                validate_non_empty(&self.statements),
                self.validate_modifiers_have_match_clause(),
                self.match_clause
                    .as_ref()
                    .map(|m| {
                        m.validate()?;
                        let match_variables = m.retrieved_variables().collect();
                        validate_insert_in_scope_of_match(&match_variables, &self.statements)
                    })
                    .unwrap_or_else(|| Ok(())),
            ]
            .into_iter()
            .chain(self.statements.iter().map(Validatable::validate)),
        )
    }
}

fn validate_insert_in_scope_of_match(
    match_variables: &HashSet<VariableRef<'_>>,
    statements: &[ThingStatement],
) -> Result {
    if statements.iter().flat_map(|s| s.variables()).any(|v| match_variables.contains(&v)) {
        Ok(())
    } else {
        let stmts_str = statements.iter().map(ThingStatement::to_string).collect::<Vec<String>>().join(", ");
        let bounds_str = match_variables.iter().map(VariableRef::to_string).collect::<Vec<String>>().join(", ");
        Err(TypeQLError::InsertClauseNotBound { insert_statements: stmts_str, bounds: bounds_str })?
    }
}

impl fmt::Display for TypeQLInsert {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if let Some(match_clause) = &self.match_clause {
            writeln!(f, "{match_clause}")?;
        }

        writeln!(f, "{}", token::Clause::Insert)?;
        write_joined!(f, ";\n", self.statements)?;
        f.write_str(";")?;
        if !self.modifiers.is_empty() {
            write!(f, "\n{}", self.modifiers)
        } else {
            Ok(())
        }
    }
}
