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
    pattern::{NamedReferences, ThingVariable},
    query::{writable::expect_non_empty, TypeQLMatch, TypeQLUpdate, Writable},
    write_joined,
};

#[derive(Debug, Eq, PartialEq)]
pub struct TypeQLDelete {
    pub match_query: TypeQLMatch,
    pub variables: Vec<ThingVariable>,
}

impl TypeQLDelete {
    pub fn insert(self, vars: impl Writable) -> TypeQLUpdate {
        TypeQLUpdate { delete_query: self, insert_variables: vars.vars() }
    }
}

impl Validatable for TypeQLDelete {
    fn validate(&self) -> Result<()> {
        collect_err(
            &mut ([
                expect_delete_in_scope_of_match(&self.match_query, &self.variables),
                expect_non_empty(&self.variables),
                self.match_query.validate(),
            ]
            .into_iter())
            .chain(self.variables.iter().map(Validatable::validate)),
        )
    }
}

fn expect_delete_in_scope_of_match(match_query: &TypeQLMatch, variables: &[ThingVariable]) -> Result<()> {
    let names_in_scope = match_query.named_references();
    collect_err(&mut variables.iter().flat_map(|v| v.references()).filter(|r| r.is_name()).map(|r| -> Result<()> {
        if names_in_scope.contains(r) {
            Ok(())
        } else {
            Err(TypeQLError::VariableOutOfScopeDelete(r.clone()))?
        }
    }))
}

impl fmt::Display for TypeQLDelete {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        writeln!(f, "{}", self.match_query)?;
        writeln!(f, "{}", token::Command::Delete)?;
        write_joined!(f, ";\n", self.variables)?;
        f.write_str(";")
    }
}
