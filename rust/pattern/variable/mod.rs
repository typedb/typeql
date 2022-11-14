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

mod reference;

pub use reference::{Reference, Visibility};
use std::collections::HashSet;

mod concept;
pub use concept::ConceptVariable;

mod thing;
pub use thing::ThingVariable;

mod type_;
pub use type_::TypeVariable;

mod unbound;
pub use unbound::UnboundVariable;

mod builder;
pub use builder::{
    ConceptConstrainable, ConceptVariableBuilder, RelationConstrainable, RelationVariableBuilder,
    ThingConstrainable, ThingVariableBuilder, TypeConstrainable, TypeVariableBuilder,
};

use crate::{
    common::{error::MATCH_HAS_UNBOUNDED_NESTED_PATTERN, validatable::Validatable, Result},
    enum_wrapper,
};
use std::fmt;

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Variable {
    Concept(ConceptVariable),
    Thing(ThingVariable),
    Type(TypeVariable),
    Unbound(UnboundVariable),
}

impl Variable {
    pub fn references(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        use Variable::*;
        match self {
            Unbound(unbound) => unbound.references(),
            Concept(concept) => concept.references(),
            Thing(thing) => thing.references(),
            Type(type_) => type_.references(),
        }
    }

    pub fn expect_is_bounded_by(&self, bounds: &HashSet<Reference>) -> Result<()> {
        match self {
            Self::Unbound(_) => unreachable!(),
            _ => {
                if !self.references().any(|r| r.is_name() && bounds.contains(r)) {
                    Err(MATCH_HAS_UNBOUNDED_NESTED_PATTERN
                        .format(&[&self.to_string().replace('\n', " ")]))?
                }
                Ok(())
            }
        }
    }
}

impl Validatable for Variable {
    fn validate(&self) -> Result<()> {
        use Variable::*;
        match self {
            Unbound(unbound) => unbound.validate(),
            Concept(concept) => concept.validate(),
            Thing(thing) => thing.validate(),
            Type(type_) => type_.validate(),
        }
    }
}

enum_wrapper! { Variable
    ConceptVariable => Concept,
    ThingVariable => Thing,
    TypeVariable => Type,
    UnboundVariable => Unbound,
}

impl fmt::Display for Variable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Variable::*;
        match self {
            Unbound(unbound) => write!(f, "{}", unbound),
            Concept(concept) => write!(f, "{}", concept),
            Thing(thing) => write!(f, "{}", thing),
            Type(type_) => write!(f, "{}", type_),
        }
    }
}
