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

use std::collections::HashSet;
use std::fmt;

use crate::{
    common::{
        error::{collect_err, TypeQLError},
        Result,
        token,
        validatable::Validatable,
    },
    pattern::{ThingStatement, VariablesRetrieved},
    query::writable::validate_non_empty,
    write_joined,
};
use crate::query::MatchClause;
use crate::query::modifier::Modifiers;
use crate::variable::variable::VariableRef;

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct TypeQLInsert {
    pub clause_match: Option<MatchClause>,
    pub statements: Vec<ThingStatement>,
    pub modifiers: Modifiers,
}

impl TypeQLInsert {
    pub fn new(statements: Vec<ThingStatement>) -> Self {
        TypeQLInsert { clause_match: None, statements, modifiers: Modifiers::default() }
    }
}

impl Validatable for TypeQLInsert {
    fn validate(&self) -> Result {
        collect_err(
            [
                validate_non_empty(&self.statements),
                self.clause_match.as_ref().map(|m| {
                    m.validate()?;
                    let match_variables = m.retrieved_variables().collect();
                    validate_insert_in_scope_of_match(&match_variables, &self.statements)
                }).unwrap_or_else(|| Ok(())),
            ]
                .into_iter()
                .chain(self.statements.iter().map(Validatable::validate)),
        )
    }
}

fn validate_insert_in_scope_of_match(match_variables: &HashSet<VariableRef>, statements: &[ThingStatement]) -> Result {
    if statements.iter().flat_map(|s| s.variables()).any(|v| {
        match_variables.contains(&v)
    }) {
        Ok(())
    } else {
        let stmts_str = statements.iter().map(ThingStatement::to_string).collect::<Vec<String>>().join(", ");
        let bounds_str = match_variables.into_iter().map(|r| r.to_string()).collect::<Vec<String>>().join(", ");
        Err(TypeQLError::NoVariableInScopeInsert(stmts_str, bounds_str))?
    }
}

impl fmt::Display for TypeQLInsert {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if let Some(clause_match) = &self.clause_match {
            writeln!(f, "{clause_match}")?;
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
