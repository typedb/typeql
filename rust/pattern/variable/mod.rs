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
mod unbound_concept;
mod unbound;
mod unbound_value;

use std::{collections::HashSet, fmt};

pub use builder::{
    ConceptConstrainable, ConceptVariableBuilder, Constant, Expression, Function, Operation, Parenthesis, RelationConstrainable, RelationVariableBuilder, ThingConstrainable,
    ThingVariableBuilder, TypeConstrainable, TypeVariableBuilder, ValueConstrainable, ValueVariableBuilder
};
pub use concept::ConceptVariable;
pub use reference::{Reference, Visibility};
pub use thing::ThingVariable;
pub use type_::TypeVariable;
pub use value::ValueVariable;
pub use unbound::UnboundVariable;
pub use unbound_concept::UnboundConceptVariable;
pub use unbound_value::UnboundValueVariable;

use crate::{
    common::{error::TypeQLError, validatable::Validatable, Result},
    enum_wrapper,
    pattern::{Normalisable, Pattern},
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Variable {
    Concept(ConceptVariable),
    Thing(ThingVariable),
    Type(TypeVariable),
    Value(ValueVariable),
    Unbound(UnboundConceptVariable),
}

impl Variable {
    pub fn references(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        use Variable::*;
        match self {
            Unbound(unbound) => unbound.references(),
            Concept(concept) => concept.references(),
            Thing(thing) => thing.references(),
            Type(type_) => type_.references(),
            Value(value) => value.references(),
        }
    }

    pub fn expect_is_bounded_by(&self, bounds: &HashSet<Reference>) -> Result<()> {
        match self {
            Self::Unbound(_) => unreachable!(),
            _ => {
                if !self.references().any(|r| r.is_name() && bounds.contains(r)) {
                    Err(TypeQLError::MatchHasUnboundedNestedPattern(self.clone().into()))?
                }
                Ok(())
            }
        }
    }
}

enum_wrapper! { Variable
    ConceptVariable => Concept,
    ThingVariable => Thing,
    TypeVariable => Type,
    ValueVariable => Value,
    UnboundConceptVariable => Unbound,
}

impl Validatable for Variable {
    fn validate(&self) -> Result<()> {
        use Variable::*;
        match self {
            Unbound(unbound) => unbound.validate(),
            Concept(concept) => concept.validate(),
            Thing(thing) => thing.validate(),
            Type(type_) => type_.validate(),
            Value(value) => value.validate(),
        }
    }
}

impl Normalisable for Variable {
    fn normalise(&mut self) -> Pattern {
        self.compute_normalised()
    }

    fn compute_normalised(&self) -> Pattern {
        self.clone().into()
    }
}

impl fmt::Display for Variable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Variable::*;
        match self {
            Unbound(unbound) => write!(f, "{unbound}"),
            Concept(concept) => write!(f, "{concept}"),
            Thing(thing) => write!(f, "{thing}"),
            Type(type_) => write!(f, "{type_}"),
            Value(value) => write!(f, "{value}"),
        }
    }
}
