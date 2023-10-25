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
    query::{TypeQLUpdate, Writable, writable::validate_non_empty},
    write_joined,
};
use crate::query::{MatchClause, Sorting};
use crate::query::modifier::Modifiers;
use crate::variable::variable::VariableRef;

#[derive(Debug, Eq, PartialEq)]
pub struct TypeQLDelete {
    pub clause_match: MatchClause,
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
        let match_variables = self.clause_match.retrieved_variables().collect();
        collect_err(
            ([
                self.clause_match.validate(),
                validate_delete_in_scope(&match_variables, &self.statements),
                validate_non_empty(&self.statements),
                self.modifiers.sorting.as_ref().map(|s| s.validate(&match_variables)).unwrap_or(Ok(())),
            ].into_iter())
                .chain(self.statements.iter().map(Validatable::validate)),
        )
    }
}

fn validate_delete_in_scope(scope_variables: &HashSet<VariableRef>, statements: &Vec<ThingStatement>) -> Result {
    collect_err(statements.iter().flat_map(|v| v.variables()).filter(|r| r.is_name()).map(|r| -> Result {
        if scope_variables.contains(&r) {
            Ok(())
        } else {
            Err(TypeQLError::DeleteVarNotBound(r.to_owned()))?
        }
    }))
}

impl fmt::Display for TypeQLDelete {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        writeln!(f, "{}", self.clause_match)?;
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
