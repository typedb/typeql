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
 */

use std::{fmt, iter};

use crate::{
    common::{
        error::{collect_err, TypeQLError},
        token,
        validatable::Validatable,
        Error,
    },
    pattern::{Conjunction, Pattern, VariablesRetrieved},
    query::{
        modifier::Modifiers, typeql_get, Projection, TypeQLDelete, TypeQLFetch, TypeQLGet, TypeQLInsert, Writable,
    },
    variable::{variable::VariableRef, Variable},
    Result,
};

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct MatchClause {
    pub conjunction: Conjunction,
}

impl MatchClause {
    pub fn new(conjunction: Conjunction) -> Self {
        Self { conjunction }
    }

    pub fn get(self) -> TypeQLGet {
        TypeQLGet {
            match_clause: self,
            filter: typeql_get::Filter { vars: Vec::default() },
            modifiers: Modifiers::default(),
        }
    }

    pub fn get_vars(self, vars: Vec<Variable>) -> TypeQLGet {
        TypeQLGet { match_clause: self, filter: typeql_get::Filter { vars }, modifiers: Modifiers::default() }
    }

    pub fn get_fixed<const N: usize, T: Into<Variable>>(self, vars: [T; N]) -> TypeQLGet {
        self.get_vars(vars.into_iter().map(|var| var.into()).collect())
    }

    pub fn fetch(self, projections: Vec<Projection>) -> TypeQLFetch {
        TypeQLFetch { match_clause: self, projections, modifiers: Modifiers::default() }
    }

    pub fn fetch_fixed<const N: usize>(self, projections: [Projection; N]) -> TypeQLFetch {
        self.fetch(projections.into())
    }

    pub fn insert(self, writable: impl Writable) -> TypeQLInsert {
        TypeQLInsert { match_clause: Some(self), statements: writable.statements(), modifiers: Modifiers::default() }
    }

    pub fn delete(self, writable: impl Writable) -> TypeQLDelete {
        TypeQLDelete { match_clause: self, statements: writable.statements(), modifiers: Modifiers::default() }
    }

    fn validate_nested_patterns_are_bounded(&self) -> Result {
        let bounds = self.conjunction.retrieved_variables().collect();
        collect_err(self.conjunction.patterns.iter().map(|p| p.validate_is_bounded_by(&bounds)))
    }
}

impl VariablesRetrieved for MatchClause {
    fn retrieved_variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        self.conjunction.retrieved_variables()
    }
}

impl Validatable for MatchClause {
    fn validate(&self) -> Result {
        self.validate_nested_patterns_are_bounded()?;
        validate_statements_have_named_variable(self.conjunction.patterns.iter())?;
        collect_err(self.conjunction.patterns.iter().map(|p| p.validate()))
    }
}

fn validate_statements_have_named_variable<'a>(patterns: impl Iterator<Item = &'a Pattern>) -> Result {
    collect_err(patterns.map(|pattern| {
        match pattern {
            Pattern::Statement(statement) => {
                statement.variables().any(|variable| variable.is_name()).then_some(()).ok_or_else(|| {
                    Error::from(TypeQLError::MatchStatementHasNoNamedVariable { pattern: pattern.clone() })
                })
            }
            Pattern::Conjunction(c) => validate_statements_have_named_variable(c.patterns.iter()),
            Pattern::Disjunction(d) => validate_statements_have_named_variable(d.patterns.iter()),
            Pattern::Negation(n) => validate_statements_have_named_variable(iter::once(n.pattern.as_ref())),
        }
    }))
}

impl fmt::Display for MatchClause {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", token::Clause::Match)?;

        for pattern in &self.conjunction.patterns {
            write!(f, "\n{pattern};")?;
        }

        Ok(())
    }
}
