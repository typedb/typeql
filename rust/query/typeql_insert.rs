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
    query::{writable::expect_non_empty, TypeQLMatch},
    write_joined,
};

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct TypeQLInsert {
    pub match_query: Option<TypeQLMatch>,
    pub variables: Vec<ThingVariable>,
}

impl TypeQLInsert {
    pub fn new(variables: Vec<ThingVariable>) -> Self {
        TypeQLInsert { match_query: None, variables }
    }
}

impl Validatable for TypeQLInsert {
    fn validate(&self) -> Result<()> {
        collect_err(
            &mut [
                expect_non_empty(&self.variables),
                expect_insert_in_scope_of_match(&self.match_query, &self.variables),
            ]
            .into_iter()
            .chain(self.match_query.iter().map(Validatable::validate))
            .chain(self.variables.iter().map(Validatable::validate)),
        )
    }
}

fn expect_insert_in_scope_of_match(match_query: &Option<TypeQLMatch>, variables: &[ThingVariable]) -> Result<()> {
    if let Some(match_query) = match_query {
        let names_in_scope = match_query.named_references();
        if variables.iter().any(|v| {
            v.reference.is_name() && names_in_scope.contains(&v.reference)
                || v.references_recursive().any(|w| names_in_scope.contains(w))
        }) {
            Ok(())
        } else {
            let variables_str = variables.iter().map(ThingVariable::to_string).collect::<Vec<String>>().join(", ");
            let bounds_str = names_in_scope.into_iter().map(|r| r.to_string()).collect::<Vec<String>>().join(", ");
            Err(TypeQLError::NoVariableInScopeInsert(variables_str, bounds_str))?
        }
    } else {
        Ok(())
    }
}

impl fmt::Display for TypeQLInsert {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if let Some(match_query) = &self.match_query {
            writeln!(f, "{match_query}")?;
        }

        writeln!(f, "{}", token::Command::Insert)?;
        write_joined!(f, ";\n", self.variables)?;
        f.write_str(";")
    }
}
