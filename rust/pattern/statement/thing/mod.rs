/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use self::isa::Isa;
use super::{comparison, Statement};
use crate::{
    common::{token, Span},
    expression::{Expression, Value},
    identifier::Variable,
    pretty::{indent, Pretty},
    type_::{Type, TypeAny},
    util::write_joined,
};

pub mod isa;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ThingStatement {
    span: Option<Span>,
    head: ThingStatementHead,
    constraints: Vec<ThingConstraint>,
}

impl ThingStatement {
    pub fn new(span: Option<Span>, head: ThingStatementHead, constraints: Vec<ThingConstraint>) -> Self {
        Self { span, head, constraints }
    }
}

impl From<ThingStatement> for Statement {
    fn from(val: ThingStatement) -> Self {
        Statement::Thing(val)
    }
}

impl Pretty for ThingStatement {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.head)?;
        if let Some((first, rest)) = self.constraints.split_first() {
            f.write_char(' ')?;
            Pretty::fmt(first, indent_level, f)?;
            for constraint in rest {
                f.write_str(",\n")?;
                indent(indent_level + 1, f)?;
                Pretty::fmt(constraint, indent_level + 1, f)?;
            }
        }
        Ok(())
    }
}

impl fmt::Display for ThingStatement {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.head)?;
        if let Some((first, rest)) = self.constraints.split_first() {
            write!(f, " {}", first)?;
            for constraint in rest {
                write!(f, ", {}", constraint)?;
            }
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum ThingStatementHead {
    Variable(Variable),
    Relation(Relation),
}

impl Pretty for ThingStatementHead {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Variable(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Relation(inner) => Pretty::fmt(inner, indent_level, f),
        }
    }
}

impl fmt::Display for ThingStatementHead {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Variable(inner) => fmt::Display::fmt(inner, f),
            Self::Relation(inner) => fmt::Display::fmt(inner, f),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Relation {
    span: Option<Span>,
    role_players: Vec<RolePlayer>,
}

impl Relation {
    pub fn new(span: Option<Span>, role_players: Vec<RolePlayer>) -> Self {
        Self { span, role_players }
    }
}

impl Pretty for Relation {}

impl fmt::Display for Relation {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_char('(')?;
        write_joined!(f, ", ", self.role_players)?;
        f.write_char(')')?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum RolePlayer {
    Typed(TypeAny, Variable),
    Untyped(Variable),
}

impl Pretty for RolePlayer {}

impl fmt::Display for RolePlayer {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            RolePlayer::Typed(type_, var) => write!(f, "{type_}: {var}"),
            RolePlayer::Untyped(var) => write!(f, "{var}"),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct AttributeValueStatement {
    span: Option<Span>,
    type_: Option<Type>,
    value: Value,
    isa: Isa,
}

impl AttributeValueStatement {
    pub fn new(span: Option<Span>, type_: Option<Type>, value: Value, isa: Isa) -> Self {
        Self { span, type_, value, isa }
    }
}

impl Pretty for AttributeValueStatement {}

impl fmt::Display for AttributeValueStatement {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if let Some(type_) = &self.type_ {
            write!(f, "{} ", type_)?;
        }
        write!(f, "{} {}", self.value, self.isa)?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct AttributeComparisonStatement {
    span: Option<Span>,
    var: Variable,
    comparison: comparison::Comparison,
    isa: Isa,
}

impl AttributeComparisonStatement {
    pub fn new(span: Option<Span>, var: Variable, comparison: comparison::Comparison, isa: Isa) -> Self {
        Self { span, var, comparison, isa }
    }
}

impl Pretty for AttributeComparisonStatement {}

impl fmt::Display for AttributeComparisonStatement {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {} {}", self.var, self.comparison, self.isa)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum ThingConstraint {
    Isa(Isa),
    Iid(Iid),
    Has(Has),
    Links(Links),
}

impl Pretty for ThingConstraint {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Isa(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Iid(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Has(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Links(inner) => Pretty::fmt(inner, indent_level, f),
        }
    }
}

impl fmt::Display for ThingConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Isa(inner) => fmt::Display::fmt(inner, f),
            Self::Iid(inner) => fmt::Display::fmt(inner, f),
            Self::Has(inner) => fmt::Display::fmt(inner, f),
            Self::Links(inner) => fmt::Display::fmt(inner, f),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Iid {
    span: Option<Span>,
    iid: String,
}

impl Iid {
    pub(crate) fn new(span: Option<Span>, iid: String) -> Self {
        Self { span, iid }
    }
}

impl Pretty for Iid {}

impl fmt::Display for Iid {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Keyword::IID, self.iid)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Has {
    span: Option<Span>,
    type_: Option<TypeAny>,
    value: HasValue,
}

impl Has {
    pub fn new(span: Option<Span>, type_: Option<TypeAny>, value: HasValue) -> Self {
        Self { span, type_, value }
    }
}

impl Pretty for Has {}

impl fmt::Display for Has {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", token::Keyword::Has)?;
        if let Some(type_) = &self.type_ {
            write!(f, " {}", type_)?;
        }
        write!(f, " {}", self.value)?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum HasValue {
    Variable(Variable),
    Expression(Expression),
    Comparison(comparison::Comparison),
}

impl Pretty for HasValue {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Variable(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Expression(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Comparison(inner) => Pretty::fmt(inner, indent_level, f),
        }
    }
}

impl fmt::Display for HasValue {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Variable(inner) => fmt::Display::fmt(inner, f),
            Self::Expression(inner) => fmt::Display::fmt(inner, f),
            Self::Comparison(inner) => fmt::Display::fmt(inner, f),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Links {
    span: Option<Span>,
    relation: Relation,
}

impl Links {
    pub fn new(span: Option<Span>, relation: Relation) -> Self {
        Self { span, relation }
    }
}

impl Pretty for Links {}

impl fmt::Display for Links {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Keyword::Links, self.relation)
    }
}
