/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{collections::HashSet, fmt};

pub use builder::{
    IsStatementBuilder, ExpressionBuilder, ThingStatementBuilder, TypeStatementBuilder,
    ValueStatementBuilder,
};
pub(crate) use builder::LeftOperand;
pub use concept::ConceptStatement;
pub use thing::ThingStatement;
pub use type_::TypeStatement;
pub use value::ValueStatement;


use crate::{
    common::{error::TypeQLError, validatable::Validatable, Result},
    enum_wrapper,
};
use crate::variable::variable::VariableRef;

mod builder;
mod concept;
mod thing;
mod type_;
mod value;
mod statement;

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Statement {
    Concept(ConceptStatement),
    Thing(ThingStatement),
    Type(TypeStatement),
    Value(ValueStatement),
}

impl Statement {
    pub fn owner(&self) -> VariableRef<'_> {
        match self {
            Statement::Concept(concept) => concept.owner(),
            Statement::Thing(thing) => thing.owner(),
            Statement::Type(type_) => type_.owner(),
            Statement::Value(value) => value.owner(),
        }
    }

    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        match self {
            Statement::Concept(concept) => concept.variables(),
            Statement::Thing(thing) => thing.variables(),
            Statement::Type(type_) => type_.variables(),
            Statement::Value(value) => value.variables(),
        }
    }

    pub fn validate_is_bounded_by(&self, bounds: &HashSet<VariableRef<'_>>) -> Result {
        if !self.variables().any(|r| r.is_name() && bounds.contains(&r)) {
            Err(TypeQLError::MatchHasUnboundedNestedPattern { pattern: self.clone().into() })?
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
