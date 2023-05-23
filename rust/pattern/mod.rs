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

mod conjunction;
mod constraint;
mod disjunction;
mod label;
mod named_references;
mod negation;
mod schema;
#[cfg(test)]
mod test;
mod variable;

use std::{collections::HashSet, fmt};

pub use conjunction::Conjunction;
pub use constraint::{
    AbstractConstraint, Annotation, HasConstraint, IIDConstraint, IsConstraint, IsExplicit, IsaConstraint,
    LabelConstraint, OwnsConstraint, PlaysConstraint, RegexConstraint, RelatesConstraint, RelationConstraint,
    RolePlayerConstraint, SubConstraint, Value, ValueConstraint, ValueTypeConstraint,
};
pub use disjunction::Disjunction;
pub use label::Label;
pub use named_references::NamedReferences;
pub use negation::Negation;
pub use schema::{RuleDeclaration, RuleDefinition};
pub use variable::{
    ConceptConstrainable, ConceptVariable, ConceptVariableBuilder, Reference, RelationConstrainable,
    RelationVariableBuilder, ThingConstrainable, ThingVariable, ThingVariableBuilder, TypeConstrainable, TypeVariable,
    TypeVariableBuilder, UnboundVariable, Variable, Visibility,
};

use crate::{
    common::{error::TypeQLError, validatable::Validatable, Result},
    enum_getter, enum_wrapper,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Pattern {
    Conjunction(Conjunction),
    Disjunction(Disjunction),
    Negation(Negation),
    Variable(Variable),
}

impl Pattern {
    pub fn expect_is_bounded_by(&self, bounds: &HashSet<Reference>) -> Result<()> {
        use Pattern::*;
        match self {
            Conjunction(conjunction) => conjunction.expect_is_bounded_by(bounds),
            Disjunction(disjunction) => disjunction.expect_is_bounded_by(bounds),
            Negation(negation) => negation.expect_is_bounded_by(bounds),
            Variable(variable) => variable.expect_is_bounded_by(bounds),
        }
    }
}

enum_getter! { Pattern
    into_conjunction(Conjunction) => Conjunction,
    into_disjunction(Disjunction) => Disjunction,
    into_negation(Negation) => Negation,
    into_variable(Variable) => Variable,
}

enum_wrapper! { Pattern
    Conjunction => Conjunction,
    Disjunction => Disjunction,
    Negation => Negation,
    Variable => Variable,
}

impl Validatable for Pattern {
    fn validate(&self) -> Result<()> {
        use Pattern::*;
        match self {
            Conjunction(conjunction) => conjunction.validate(),
            Disjunction(disjunction) => disjunction.validate(),
            Negation(negation) => negation.validate(),
            Variable(variable) => variable.validate(),
        }
    }
}

pub trait Normalisable {
    fn normalise(&mut self) -> Pattern;
    fn compute_normalised(&self) -> Pattern;
}

impl Normalisable for Pattern {
    fn normalise(&mut self) -> Pattern {
        use Pattern::*;
        match self {
            Conjunction(conjunction) => conjunction.normalise(),
            Disjunction(disjunction) => disjunction.normalise(),
            Negation(negation) => negation.normalise(),
            Variable(variable) => variable.normalise(),
        }
    }

    fn compute_normalised(&self) -> Pattern {
        use Pattern::*;
        match self {
            Conjunction(conjunction) => conjunction.compute_normalised(),
            Disjunction(disjunction) => disjunction.compute_normalised(),
            Negation(negation) => negation.compute_normalised(),
            Variable(variable) => variable.compute_normalised(),
        }
    }
}

impl From<ConceptVariable> for Pattern {
    fn from(variable: ConceptVariable) -> Self {
        Variable::from(variable).into()
    }
}

impl From<ThingVariable> for Pattern {
    fn from(variable: ThingVariable) -> Self {
        Variable::from(variable).into()
    }
}

impl From<TypeVariable> for Pattern {
    fn from(variable: TypeVariable) -> Self {
        Variable::from(variable).into()
    }
}

impl From<UnboundVariable> for Pattern {
    fn from(variable: UnboundVariable) -> Self {
        Variable::from(variable).into()
    }
}

impl fmt::Display for Pattern {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Pattern::*;
        match self {
            Conjunction(conjunction) => write!(f, "{conjunction}"),
            Disjunction(disjunction) => write!(f, "{disjunction}"),
            Negation(negation) => write!(f, "{negation}"),
            Variable(variable) => write!(f, "{variable}"),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Definable {
    RuleDeclaration(RuleDeclaration),
    RuleDefinition(RuleDefinition),
    TypeVariable(TypeVariable),
}

enum_getter! { Definable
    into_rule_declaration(RuleDeclaration) => RuleDeclaration,
    into_rule(RuleDefinition) => RuleDefinition,
    into_type_variable(TypeVariable) => TypeVariable,
}

enum_wrapper! { Definable
    RuleDeclaration => RuleDeclaration,
    RuleDefinition => RuleDefinition,
    TypeVariable => TypeVariable,
}

impl Validatable for Definable {
    fn validate(&self) -> Result<()> {
        use Definable::*;
        match self {
            RuleDeclaration(rule) => rule.validate(),
            RuleDefinition(rule) => rule.validate(),
            TypeVariable(variable) => variable.validate(),
        }
    }
}

impl fmt::Display for Definable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Definable::*;
        match self {
            RuleDeclaration(rule_declaration) => write!(f, "{rule_declaration}"),
            RuleDefinition(rule) => write!(f, "{rule}"),
            TypeVariable(variable) => write!(f, "{variable}"),
        }
    }
}
