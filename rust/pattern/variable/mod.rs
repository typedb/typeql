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
mod unbound;
mod unbound_concept;
mod unbound_value;
mod value;

use std::{collections::HashSet, fmt};

pub(crate) use builder::LeftOperand;
pub use builder::{
    ConceptConstrainable, ConceptVariableBuilder, ExpressionBuilder, RelationConstrainable, RelationVariableBuilder,
    ThingConstrainable, ThingVariableBuilder, TypeConstrainable, TypeVariableBuilder, ValueConstrainable,
    ValueVariableBuilder,
};
pub use concept::ConceptVariable;
pub use reference::{ConceptReference, Reference, ValueReference, Visibility};
pub use thing::ThingStatement;
pub use type_::TypeStatement;
pub use unbound::UnboundVariable;
pub use unbound_concept::UnboundConceptVariable;
pub use unbound_value::UnboundValueVariable;
pub use value::ValueVariable;

use crate::{
    common::{error::TypeQLError, validatable::Validatable, Result},
    enum_wrapper,
    pattern::{Normalisable, Pattern},
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Statement {
    Concept(ConceptVariable),
    Thing(ThingStatement),
    Type(TypeStatement),
    Value(ValueVariable),
}

impl Statement {
    pub fn reference(&self) -> &Reference {
        use Statement::*;
        match self {
            Concept(concept) => &concept.reference,
            Thing(thing) => &thing.reference,
            Type(type_) => &type_.reference,
            Value(value) => &value.reference,
        }
    }

    pub fn references(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        use Statement::*;
        match self {
            Concept(concept) => concept.references(),
            Thing(thing) => thing.references(),
            Type(type_) => type_.references(),
            Value(value) => value.references(),
        }
    }

    pub fn references_recursive(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        use Statement::*;
        match self {
            Concept(concept) => concept.references(),
            Thing(thing) => thing.references(),
            Type(type_) => type_.references(),
            Value(value) => value.references_recursive(),
        }
    }

    pub fn expect_is_bounded_by(&self, bounds: &HashSet<Reference>) -> Result<()> {
        if !self.references_recursive().any(|r| r.is_name() && bounds.contains(r)) {
            Err(TypeQLError::MatchHasUnboundedNestedPattern(self.clone().into()))?
        }
        Ok(())
    }
}

enum_wrapper! { Statement
    ConceptVariable => Concept,
    ThingStatement => Thing,
    TypeStatement => Type,
    ValueVariable => Value,
}

impl Validatable for Statement {
    fn validate(&self) -> Result<()> {
        use Statement::*;
        match self {
            Concept(concept) => concept.validate(),
            Thing(thing) => thing.validate(),
            Type(type_) => type_.validate(),
            Value(value) => value.validate(),
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
