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
    query::{modifier::Modifiers, writable::validate_non_empty, MatchClause, Sorting, TypeQLUpdate, Writable},
    variable::variable::VariableRef,
    write_joined,
};

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
