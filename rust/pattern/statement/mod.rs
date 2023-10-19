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

mod builder;
mod concept;
mod reference;
mod thing;
mod type_;
mod value;

use std::{collections::HashSet, fmt};

pub(crate) use builder::LeftOperand;
pub use builder::{
    ConceptConstrainable, ConceptStatementBuilder, ExpressionBuilder, RelationConstrainable, RelationStatementBuilder,
    ThingConstrainable, ThingStatementBuilder, TypeConstrainable, TypeStatementBuilder, ValueConstrainable,
    ValueStatementBuilder,
};
pub use concept::ConceptStatement;
pub use thing::ThingStatement;
pub use type_::TypeStatement;
pub use crate::variable::variable::Variable;
pub use crate::variable::variable_concept::ConceptVariable;
pub use crate::variable::variable_value::ValueVariable;
pub use value::ValueStatement;

use crate::{
    common::{error::TypeQLError, validatable::Validatable, Result},
    enum_wrapper,
    pattern::{Normalisable, Pattern},
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Statement {
    Concept(ConceptStatement),
    Thing(ThingStatement),
    Type(TypeStatement),
    Value(ValueStatement),
}

impl Statement {
    pub fn variable(&self) -> &Variable {
        match self {
            Statement::Concept(concept) => &concept.variable,
            Statement::Thing(thing) => &thing.variable,
            Statement::Type(type_) => &type_.variable,
            Statement::Value(value) => &value.variable,
        }
    }

    pub fn variables(&self) -> Box<dyn Iterator<Item=&Variable> + '_> {
        match self {
            Statement::Concept(concept) => concept.references(),
            Statement::Thing(thing) => thing.variables(),
            Statement::Type(type_) => type_.variables(),
            Statement::Value(value) => value.variables(),
        }
    }

    pub fn variables_recursive(&self) -> Box<dyn Iterator<Item=&Variable> + '_> {
        use Statement::*;
        match self {
            Concept(concept) => concept.references(),
            Thing(thing) => thing.variables(),
            Type(type_) => type_.variables(),
            Value(value) => value.variables_recursive(),
        }
    }

    pub fn validate_is_bounded_by(&self, bounds: &HashSet<Variable>) -> Result {
        if !self.variables_recursive().any(|r| r.is_name() && bounds.contains(r)) {
            Err(TypeQLError::MatchHasUnboundedNestedPattern(self.clone().into()))?
        }
        Ok(())
    }
}

enum_wrapper! { Statement
    ConceptStatement => Concept,
    ThingStatement => Thing,
    TypeStatement => Type,
    ValueStatement => Value,
}

impl Validatable for Statement {
    fn validate(&self) -> Result {
        match self {
            Statement::Concept(concept) => concept.validate(),
            Statement::Thing(thing) => thing.validate(),
            Statement::Type(type_) => type_.validate(),
            Statement::Value(value) => value.validate(),
        }
    }
}

impl Normalisable for Statement {
    fn normalise(&mut self) -> Pattern {
        self.compute_normalised()
    }

    fn compute_normalised(&self) -> Pattern {
        self.clone().into()
    }
}

impl fmt::Display for Statement {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Statement::*;
        match self {
            Concept(concept) => write!(f, "{concept}"),
            Thing(thing) => write!(f, "{thing}"),
            Type(type_) => write!(f, "{type_}"),
            Value(value) => write!(f, "{value}"),
        }
    }
}
