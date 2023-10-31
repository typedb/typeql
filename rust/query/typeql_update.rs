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
