/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{collections::HashSet, fmt};

pub use conjunction::Conjunction;
pub use constant::Constant;
pub use constraint::{
    AbstractConstraint, Annotation, AssignConstraint, HasConstraint, IIDConstraint, IsConstraint, IsExplicit,
    IsaConstraint, LabelConstraint, OwnsConstraint, PlaysConstraint, Comparison, RegexConstraint, RelatesConstraint,
    RelationConstraint, RolePlayerConstraint, SubConstraint, Value, ValueTypeConstraint,
};
pub use disjunction::Disjunction;
pub use expression::{Expression, Function, Operation};
pub use label::Label;
pub use negation::Negation;
pub use schema::{Rule, RuleLabel};
pub(crate) use statement::LeftOperand;
pub use statement::{
    IsStatementBuilder, ConceptStatement, ExpressionBuilder, Statement, ThingStatement,
    ThingStatementBuilder, TypeStatement, TypeStatementBuilder, ValueStatement, ValueStatementBuilder,
};

pub use crate::common::variables_retrieved::VariablesRetrieved;
use crate::{
    common::{validatable::Validatable, Result},
    enum_getter, enum_wrapper,
};
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

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Pattern {
    Conjunction(Conjunction),
    Disjunction(Disjunction),
    Negation(Negation),
    Statement(Statement),
}

impl Pattern {
    pub fn variables_recursive(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
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

impl VariablesRetrieved for Pattern {
    fn retrieved_variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        match self {
            Pattern::Conjunction(conjunction) => conjunction.retrieved_variables(),
            Pattern::Disjunction(disjunction) => disjunction.variables_recursive(),
            Pattern::Negation(negation) => negation.variables_recursive(),
            Pattern::Statement(statement) => statement.variables(),
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
