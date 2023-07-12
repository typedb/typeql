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
    pattern::{NamedReferences, Reference, UnboundVariable},
    query::{TypeQLMatch, TypeQLMatchGroup},
};

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct AggregateQuery<T>
where
    T: AggregateQueryBuilder,
{
    pub query: T,
    pub method: token::Aggregate,
    pub var: Option<UnboundVariable>,
}

pub type TypeQLMatchAggregate = AggregateQuery<TypeQLMatch>;
pub type TypeQLMatchGroupAggregate = AggregateQuery<TypeQLMatchGroup>;

impl<T: AggregateQueryBuilder> AggregateQuery<T> {
    fn new_count(base: T) -> Self {
        Self { query: base, method: token::Aggregate::Count, var: None }
    }

    fn new(base: T, method: token::Aggregate, var: UnboundVariable) -> Self {
        Self { query: base, method, var: Some(var) }
    }
}

impl<T: AggregateQueryBuilder> Validatable for AggregateQuery<T> {
    fn validate(&self) -> Result<()> {
        collect_err(
            &mut [expect_method_variable_compatible(self.method, &self.var), self.query.validate()]
                .into_iter()
                .chain(self.var.iter().map(|v| expect_variable_in_scope(v, self.query.named_references()))),
        )
    }
}

fn expect_method_variable_compatible(method: token::Aggregate, var: &Option<UnboundVariable>) -> Result<()> {
    if method == token::Aggregate::Count && var.is_some() {
        Err(TypeQLError::InvalidCountVariableArgument())?
    }
    Ok(())
}

fn expect_variable_in_scope(var: &UnboundVariable, names_in_scope: HashSet<Reference>) -> Result<()> {
    if !names_in_scope.contains(var.reference()) {
        Err(TypeQLError::VariableOutOfScopeMatch(var.reference().clone()))?;
    }
    Ok(())
}

impl fmt::Display for TypeQLMatchAggregate {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}\n{}", self.query, self.method)?;
        if let Some(var) = &self.var {
            write!(f, " {var}")?;
        }
        f.write_str(";")
    }
}

impl fmt::Display for TypeQLMatchGroupAggregate {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", self.query, self.method)?;
        if let Some(var) = &self.var {
            write!(f, " {var}")?;
        }
        f.write_str(";")
    }
}

pub trait AggregateQueryBuilder:
    Sized + Clone + fmt::Display + fmt::Debug + Eq + PartialEq + NamedReferences + Validatable
{
    fn count(self) -> AggregateQuery<Self> {
        AggregateQuery::<Self>::new_count(self)
    }

    fn aggregate(self, method: token::Aggregate, var: UnboundVariable) -> AggregateQuery<Self> {
        AggregateQuery::<Self>::new(self, method, var)
    }

    fn max(self, var: impl Into<UnboundVariable>) -> AggregateQuery<Self> {
        self.aggregate(token::Aggregate::Max, var.into())
    }

    fn min(self, var: impl Into<UnboundVariable>) -> AggregateQuery<Self> {
        self.aggregate(token::Aggregate::Min, var.into())
    }

    fn mean(self, var: impl Into<UnboundVariable>) -> AggregateQuery<Self> {
        self.aggregate(token::Aggregate::Mean, var.into())
    }

    fn median(self, var: impl Into<UnboundVariable>) -> AggregateQuery<Self> {
        self.aggregate(token::Aggregate::Median, var.into())
    }

    fn std(self, var: impl Into<UnboundVariable>) -> AggregateQuery<Self> {
        self.aggregate(token::Aggregate::Std, var.into())
    }

    fn sum(self, var: impl Into<UnboundVariable>) -> AggregateQuery<Self> {
        self.aggregate(token::Aggregate::Sum, var.into())
    }
}
