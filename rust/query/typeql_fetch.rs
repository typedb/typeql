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
use std::fmt;
use std::path::Display;
use itertools::Itertools;

use crate::common::{Result, token};
use crate::common::error::collect_err;
use crate::common::validatable::Validatable;
use crate::pattern::{Label, VariablesRetrieved};
use crate::query::{MatchClause, TypeQLGetAggregate};
use crate::query::modifier::Modifiers;
use crate::variable::Variable;
use crate::variable::variable::VariableRef;
use crate::write_joined;

#[derive(Debug, Eq, PartialEq)]
pub struct TypeQLFetch {
    pub clause_match: MatchClause,
    pub projections: Vec<Projection>,
    pub modifiers: Modifiers,
}

impl TypeQLFetch {
    fn validate_names_are_unique(&self) -> Result {
        // TODO: either do restrict no-reuse across sub-trees or fully allow re-use
        todo!()
    }
}

impl TypeQLFetch {
    pub(crate) fn new(clause_match: MatchClause, projections: Vec<Projection>) -> Self {
        TypeQLFetch { clause_match, projections, modifiers: Modifiers::default() }
    }
}

impl Validatable for TypeQLFetch {
    fn validate(&self) -> Result {
        let match_variables = self.clause_match.retrieved_variables().collect();
        collect_err([
            self.clause_match.validate(),
            self.modifiers.sorting.as_ref().map(|s| s.validate(&match_variables)).unwrap_or(Ok(())),
            self.validate_names_are_unique()
        ])
    }
}

impl VariablesRetrieved for TypeQLFetch {
    fn retrieved_variables(&self) -> Box<dyn Iterator<Item=VariableRef<'_>> + '_> {
        // TODO get projection variable keys
        self.clause_match.retrieved_variables()
    }
}

#[derive(Debug, Eq, PartialEq)]
enum Projection {
    Variable(ProjectionKey),
    Attribute(ProjectionKey, Vec<ProjectionAttribute>),
    Subquery(ProjectionLabel, ProjectionSubquery),
}

#[derive(Debug, Eq, PartialEq)]
struct ProjectionKey {
    variable: Variable,
    label: Option<ProjectionLabel>
}

#[derive(Debug, Eq, PartialEq)]
enum ProjectionLabel {
    Quoted(String),
    Unquoted(String),
}

#[derive(Debug, Eq, PartialEq)]
struct ProjectionAttribute {
    attribute: Label,
    label: Option<ProjectionLabel>,
}

#[derive(Debug, Eq, PartialEq)]
enum ProjectionSubquery {
    GetAggregate(TypeQLGetAggregate),
    Fetch(Box<TypeQLFetch>)
}

impl fmt::Display for TypeQLFetch {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        writeln!(f, "{}", self.clause_match)?;
        writeln!(f, "{}", token::Clause::Fetch)?;
        write_joined!(f, ";\n", self.projections)?;
        writeln!(f, "{}", self.modifiers)
    }
}

impl fmt::Display for Projection {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Projection::Variable(key) => {
                writeln!(f, "{};", key)
            }
            Projection::Attribute(key, attrs) => {
                write!(f, "{}: ", key)?;
                // TODO fix
                writeln!(f, " {};", attrs.iter().map(Display::fmt)).join(", ")
            }
            Projection::Subquery(label, subquery) => {
                writeln!(f, "{}: {{\n {} }};", label, subquery)
            }
        }
    }
}

impl fmt::Display for ProjectionKey {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.variable)?;
        if let Some(label) = &self.label {
            write!(f, " {}", label)
        } else {
            Ok(())
        }
    }
}

impl fmt::Display for ProjectionLabel {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            ProjectionLabel::Quoted(s) => write!(f, "\"{}\"", s),
            ProjectionLabel::Unquoted(s) => write!(f, "{}", s),
        }
    }
}

impl fmt::Display for ProjectionAttribute {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.attribute)?;
        if let Some(label) = &self.label {
            write!(f, " {}", label)
        } else {
            Ok(())
        }
    }
}

impl fmt::Display for ProjectionSubquery {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            ProjectionSubquery::GetAggregate(query) => {
                write!(f, "{}", query)
            }
            ProjectionSubquery::Fetch(query) => {
                write!(f, "{}", query)
            }
        }
    }
}