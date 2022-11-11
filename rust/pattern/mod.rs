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

pub use conjunction::Conjunction;
use std::collections::HashSet;

mod constraint;
pub use constraint::{
    AbstractConstraint, HasConstraint, IIDConstraint, IsConstraint, IsExplicit, IsKeyAttribute,
    IsaConstraint, LabelConstraint, OwnsConstraint, PlaysConstraint, RegexConstraint,
    RelatesConstraint, RelationConstraint, RolePlayerConstraint, SubConstraint, Value,
    ValueConstraint, ValueTypeConstraint, KEY,
};

mod disjunction;
pub use disjunction::Disjunction;

mod label;
pub use label::{Label, Type};

mod schema;
pub use schema::{RuleDeclaration, RuleDefinition};

mod negation;
pub use negation::Negation;

mod variable;
pub use variable::{
    ConceptConstrainable, ConceptVariable, ConceptVariableBuilder, Reference,
    RelationConstrainable, RelationVariableBuilder, ThingConstrainable, ThingVariable,
    ThingVariableBuilder, TypeConstrainable, TypeVariable, TypeVariableBuilder, UnboundVariable,
    Variable, Visibility,
};

#[cfg(test)]
mod test;

use crate::{
    common::{error::ErrorReport, validatable::Validatable},
    enum_getter, enum_wrapper,
};
use std::fmt;

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Pattern {
    Conjunction(Conjunction),
    Disjunction(Disjunction),
    Negation(Negation),
    Variable(Variable),
}

impl Pattern {
    enum_getter!(into_conjunction, Conjunction, Conjunction);
    enum_getter!(into_disjunction, Disjunction, Disjunction);
    enum_getter!(into_negation, Negation, Negation);
    enum_getter!(into_variable, Variable, Variable);

    pub fn expect_is_bounded_by(&self, bounds: &HashSet<Reference>) -> Result<(), ErrorReport> {
        use Pattern::*;
        match self {
            Conjunction(conjunction) => conjunction.expect_is_bounded_by(bounds),
            Disjunction(disjunction) => disjunction.expect_is_bounded_by(bounds),
            Negation(negation) => negation.expect_is_bounded_by(bounds),
            Variable(variable) => variable.expect_is_bounded_by(bounds),
        }
    }
}

impl Validatable for Pattern {
    fn validate(&self) -> Result<(), ErrorReport> {
        use Pattern::*;
        match self {
            Conjunction(conjunction) => conjunction.validate(),
            Disjunction(disjunction) => disjunction.validate(),
            Negation(negation) => negation.validate(),
            Variable(variable) => variable.validate(),
        }
    }
}

enum_wrapper! { Pattern
    Conjunction => Conjunction,
    Disjunction => Disjunction,
    Negation => Negation,
    Variable => Variable,
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
            Conjunction(conjunction) => write!(f, "{}", conjunction),
            Disjunction(disjunction) => write!(f, "{}", disjunction),
            Negation(negation) => write!(f, "{}", negation),
            Variable(variable) => write!(f, "{}", variable),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Definable {
    RuleDefinition(RuleDefinition),
    RuleDeclaration(RuleDeclaration),
    TypeVariable(TypeVariable),
}

impl Definable {
    enum_getter!(into_rule, RuleDefinition, RuleDefinition);
    enum_getter!(into_rule_declaration, RuleDeclaration, RuleDeclaration);
    enum_getter!(into_type_variable, TypeVariable, TypeVariable);
}

impl Validatable for Definable {
    fn validate(&self) -> Result<(), ErrorReport> {
        use Definable::*;
        match self {
            RuleDefinition(rule) => rule.validate(),
            RuleDeclaration(rule) => rule.validate(),
            TypeVariable(variable) => variable.validate(),
        }
    }
}

enum_wrapper! { Definable
    RuleDefinition => RuleDefinition,
    RuleDeclaration => RuleDeclaration,
    TypeVariable => TypeVariable,
}

impl fmt::Display for Definable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Definable::*;
        match self {
            RuleDefinition(rule) => write!(f, "{}", rule),
            RuleDeclaration(rule_declaration) => write!(f, "{}", rule_declaration),
            TypeVariable(variable) => write!(f, "{}", variable),
        }
    }
}
