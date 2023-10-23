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

pub use conjunction::Conjunction;
pub use constant::Constant;
pub use constraint::{
    AbstractConstraint, Annotation, AssignConstraint, HasConstraint, IIDConstraint, IsaConstraint, IsConstraint,
    IsExplicit, LabelConstraint, OwnsConstraint, PlaysConstraint, Predicate, RegexConstraint,
    RelatesConstraint, RelationConstraint, RolePlayerConstraint, SubConstraint, Value, ValueTypeConstraint,
};
pub use disjunction::Disjunction;
pub use expression::{Expression, Function, Operation};
pub use label::Label;
pub use negation::Negation;
pub use schema::{RuleLabel, Rule};
pub use statement::{
    ConceptConstrainable, ConceptStatement, ConceptStatementBuilder, ExpressionBuilder,
    RelationConstrainable, RelationStatementBuilder, Statement, ThingConstrainable, ThingStatement,
    ThingStatementBuilder, TypeConstrainable, TypeStatement, TypeStatementBuilder, ValueConstrainable, ValueStatement, ValueStatementBuilder,
};
pub(crate) use statement::LeftOperand;

use crate::{
    common::{Result, validatable::Validatable},
    enum_getter, enum_wrapper,
};
pub use crate::common::variabilizable::Variabilizable;
use crate::variable::variable::VariableRef;

mod conjunction;
mod constant;
mod constraint;
mod disjunction;
mod expression;
mod label;
mod negation;
mod schema;
pub(crate) mod statement;
#[cfg(test)]
mod test;

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Pattern {
    Conjunction(Conjunction),
    Disjunction(Disjunction),
    Negation(Negation),
    Statement(Statement),
}

impl Pattern {
    pub fn variables_recursive(&self) -> Box<dyn Iterator<Item=VariableRef<'_>> + '_> {
        Box::new(match self {
            Pattern::Conjunction(conjunction) => conjunction.variables_recursive(),
            Pattern::Disjunction(disjunction) => disjunction.variables_recursive(),
            Pattern::Negation(negation) => negation.variables_recursive(),
            Pattern::Statement(statement) => statement.variables(),
        })
    }

    pub fn validate_is_bounded_by(&self, bounds: &HashSet<VariableRef<'_>>) -> Result {
        match self {
            Pattern::Conjunction(conjunction) => conjunction.validate_is_bounded_by(bounds),
            Pattern::Disjunction(disjunction) => disjunction.validate_is_bounded_by(bounds),
            Pattern::Negation(negation) => negation.validate_is_bounded_by(bounds),
            Pattern::Statement(statement) => statement.validate_is_bounded_by(bounds),
        }
    }
}

enum_getter! { Pattern
    into_conjunction(Conjunction) => Conjunction,
    into_disjunction(Disjunction) => Disjunction,
    into_negation(Negation) => Negation,
    into_statement(Statement) => Statement,
}

enum_wrapper! { Pattern
    Conjunction => Conjunction,
    Disjunction => Disjunction,
    Negation => Negation,
    Statement => Statement,
}

impl Validatable for Pattern {
    fn validate(&self) -> Result {
        match self {
            Pattern::Conjunction(conjunction) => conjunction.validate(),
            Pattern::Disjunction(disjunction) => disjunction.validate(),
            Pattern::Negation(negation) => negation.validate(),
            Pattern::Statement(statement) => statement.validate(),
        }
    }
}

impl Variabilizable for Pattern {
    fn named_variables(&self) -> Box<dyn Iterator<Item=VariableRef<'_>> + '_> {
        match self {
            Pattern::Conjunction(conjunction) => conjunction.named_variables(),
            Pattern::Disjunction(disjunction) => disjunction.variables_recursive(),
            Pattern::Negation(negation) => negation.variables_recursive(),
            Pattern::Statement(statement) => statement.variables()
        }
    }
}

pub trait Normalisable {
    fn normalise(&mut self) -> Pattern;
    fn compute_normalised(&self) -> Pattern;
}

impl Normalisable for Pattern {
    fn normalise(&mut self) -> Pattern {
        match self {
            Pattern::Conjunction(conjunction) => conjunction.normalise(),
            Pattern::Disjunction(disjunction) => disjunction.normalise(),
            Pattern::Negation(negation) => negation.normalise(),
            Pattern::Statement(statement) => statement.normalise(),
        }
    }

    fn compute_normalised(&self) -> Pattern {
        match self {
            Pattern::Conjunction(conjunction) => conjunction.compute_normalised(),
            Pattern::Disjunction(disjunction) => disjunction.compute_normalised(),
            Pattern::Negation(negation) => negation.compute_normalised(),
            Pattern::Statement(statement) => statement.compute_normalised(),
        }
    }
}

impl From<ConceptStatement> for Pattern {
    fn from(statement: ConceptStatement) -> Self {
        Statement::from(statement).into()
    }
}

impl From<ThingStatement> for Pattern {
    fn from(statement: ThingStatement) -> Self {
        Statement::from(statement).into()
    }
}

impl From<TypeStatement> for Pattern {
    fn from(statement: TypeStatement) -> Self {
        Statement::from(statement).into()
    }
}

impl From<ValueStatement> for Pattern {
    fn from(statement: ValueStatement) -> Self {
        Statement::from(statement).into()
    }
}

impl fmt::Display for Pattern {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Pattern::*;
        match self {
            Conjunction(conjunction) => write!(f, "{conjunction}"),
            Disjunction(disjunction) => write!(f, "{disjunction}"),
            Negation(negation) => write!(f, "{negation}"),
            Statement(statement) => write!(f, "{statement}"),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Definable {
    RuleDeclaration(RuleLabel),
    RuleDefinition(Rule),
    TypeStatement(TypeStatement),
}

enum_getter! { Definable
    into_rule_declaration(RuleDeclaration) => RuleLabel,
    into_rule(RuleDefinition) => Rule,
    into_type_statement(TypeStatement) => TypeStatement,
}

enum_wrapper! { Definable
    RuleLabel => RuleDeclaration,
    Rule => RuleDefinition,
    TypeStatement => TypeStatement,
}

impl Validatable for Definable {
    fn validate(&self) -> Result {
        match self {
            Definable::RuleDeclaration(rule) => rule.validate(),
            Definable::RuleDefinition(rule) => rule.validate(),
            Definable::TypeStatement(statement) => statement.validate(),
        }
    }
}

impl fmt::Display for Definable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Definable::RuleDeclaration(rule_declaration) => write!(f, "{rule_declaration}"),
            Definable::RuleDefinition(rule) => write!(f, "{rule}"),
            Definable::TypeStatement(statement) => write!(f, "{statement}"),
        }
    }
}
