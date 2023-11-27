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

use std::{collections::HashSet, fmt, iter};

use crate::{
    common::{
        error::{collect_err, TypeQLError},
        token,
        validatable::Validatable,
        Result,
    },
    pattern::VariablesRetrieved,
    query::{AggregateQueryBuilder, TypeQLGet},
    variable::{variable::VariableRef, Variable},
};

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct TypeQLGetGroup {
    pub query: TypeQLGet,
    pub group_var: Variable,
}

impl AggregateQueryBuilder for TypeQLGetGroup {}

impl Validatable for TypeQLGetGroup {
    fn validate(&self) -> Result {
        let retrieved_variables = self.query.retrieved_variables().collect();
        collect_err(
            [self.query.validate(), self.group_var.validate()]
                .into_iter()
                .chain(iter::once(&self.group_var).map(|v| validate_variable_in_scope(v, &retrieved_variables))),
        )
    }
}

impl VariablesRetrieved for TypeQLGetGroup {
    fn retrieved_variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        self.query.retrieved_variables()
    }
}

fn validate_variable_in_scope(var: &Variable, scope_variables: &HashSet<VariableRef<'_>>) -> Result {
    if !scope_variables.contains(&var.as_ref()) {
        Err(TypeQLError::GroupVarNotBound { variable: var.clone() })?;
    }
    Ok(())
}

impl fmt::Display for TypeQLGetGroup {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}\n{} {};", self.query, token::Clause::Group, self.group_var)
    }
}
