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
    variable::variable::VariableRef,
    write_joined,
};

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
            Err(TypeQLError::InsertModifiersRequireMatch(self.to_string()))?
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
        let bounds_str = match_variables.into_iter().map(|r| r.to_string()).collect::<Vec<String>>().join(", ");
        Err(TypeQLError::InsertClauseNotBound(stmts_str, bounds_str))?
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
