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

use std::fmt;

use crate::{
    common::{
        error::{collect_err, TypeQLError},
        token,
        validatable::Validatable,
        Result,
    },
    pattern::{Variabilizable, ThingStatement},
    query::{writable::validate_non_empty},
    write_joined,
};
use crate::query::{MatchClause};
use crate::query::modifier::Modifiers;

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
                validate_insert_in_scope_of_get(&self.clause_match, &self.statements),
                self.clause_match.as_ref().map(Validatable::validate).unwrap_or_else(|| Ok(()))
            ]
                .into_iter()
                .chain(self.statements.iter().map(Validatable::validate)),
        )
    }
}

fn validate_insert_in_scope_of_get(match_clause: &Option<MatchClause>, statements: &[ThingStatement]) -> Result {
    if let Some(match_) = match_clause {
        let names_in_scope = match_.named_variables();
        if statements.iter().any(|v| {
            v.variable.is_name() && names_in_scope.contains(&v.variable)
                || v.variables_recursive().any(|w| names_in_scope.contains(w))
        }) {
            Ok(())
        } else {
            let stmts_str = statements.iter().map(ThingStatement::to_string).collect::<Vec<String>>().join(", ");
            let bounds_str = names_in_scope.into_iter().map(|r| r.to_string()).collect::<Vec<String>>().join(", ");
            Err(TypeQLError::NoVariableInScopeInsert(stmts_str, bounds_str))?
        }
    } else {
        Ok(())
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
        write!(f, "\n{}", self.modifiers)
    }
}
