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
    pattern::VariablesRetrieved,
    query::{TypeQLGet, TypeQLGetGroup},
    variable::{variable::VariableRef, Variable},
};

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct AggregateQuery<T>
where
    T: AggregateQueryBuilder,
{
    pub query: T,
    pub method: token::Aggregate,
    pub var: Option<Variable>,
}

pub type TypeQLGetAggregate = AggregateQuery<TypeQLGet>;
pub type TypeQLGetGroupAggregate = AggregateQuery<TypeQLGetGroup>;

impl<T: AggregateQueryBuilder> AggregateQuery<T> {
    fn new_count(query: T) -> Self {
        Self { query, method: token::Aggregate::Count, var: None }
    }

    fn new(query: T, method: token::Aggregate, var: impl Into<Variable>) -> Self {
        Self { query, method, var: Some(var.into()) }
    }
}

impl<T: AggregateQueryBuilder> Validatable for AggregateQuery<T> {
    fn validate(&self) -> Result {
        let retrieved_variables = self.query.retrieved_variables().collect();
        collect_err(
            [self.query.validate(), validate_method_variable_compatible(&self.method, &self.var)]
                .into_iter()
                .chain(self.var.iter().map(|v| validate_variable_in_scope(v, &retrieved_variables))),
        )
    }
}

fn validate_method_variable_compatible(method: &token::Aggregate, var: &Option<Variable>) -> Result {
    if *method == token::Aggregate::Count && var.is_some() {
        Err(TypeQLError::InvalidCountVariableArgument)?
    }
    Ok(())
}

fn validate_variable_in_scope(var: &Variable, scope_variables: &HashSet<VariableRef<'_>>) -> Result {
    if !scope_variables.contains(&var.as_ref()) {
        Err(TypeQLError::AggregateVarNotBound { variable: var.clone() })?;
    }
    Ok(())
}

impl fmt::Display for TypeQLGetAggregate {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}\n{}", self.query, self.method)?;
        if let Some(var) = &self.var {
            write!(f, " {var}")?;
        }
        f.write_str(";")
    }
}

impl fmt::Display for TypeQLGetGroupAggregate {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", self.query, self.method)?;
        if let Some(var) = &self.var {
            write!(f, " {var}")?;
        }
        f.write_str(";")
    }
}

pub trait AggregateQueryBuilder:
    Sized + Clone + fmt::Display + fmt::Debug + Eq + PartialEq + VariablesRetrieved + Validatable
{
    fn count(self) -> AggregateQuery<Self> {
        AggregateQuery::<Self>::new_count(self)
    }

    fn aggregate(self, method: token::Aggregate, var: impl Into<Variable>) -> AggregateQuery<Self> {
        AggregateQuery::<Self>::new(self, method, var)
    }

    fn max(self, var: impl Into<Variable>) -> AggregateQuery<Self> {
        self.aggregate(token::Aggregate::Max, var.into())
    }

    fn min(self, var: impl Into<Variable>) -> AggregateQuery<Self> {
        self.aggregate(token::Aggregate::Min, var.into())
    }

    fn mean(self, var: impl Into<Variable>) -> AggregateQuery<Self> {
        self.aggregate(token::Aggregate::Mean, var.into())
    }

    fn median(self, var: impl Into<Variable>) -> AggregateQuery<Self> {
        self.aggregate(token::Aggregate::Median, var.into())
    }

    fn std(self, var: impl Into<Variable>) -> AggregateQuery<Self> {
        self.aggregate(token::Aggregate::Std, var.into())
    }

    fn sum(self, var: impl Into<Variable>) -> AggregateQuery<Self> {
        self.aggregate(token::Aggregate::Sum, var.into())
    }
}
