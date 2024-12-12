/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{token, Span},
    pretty::Pretty,
    statement::{comparison::Comparison, thing::Relation},
    type_::TypeRef,
    value::ValueLiteral,
    Expression, Literal,
};

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct Isa {
    span: Option<Span>,
    pub kind: IsaKind,
    pub type_: TypeRef,
    pub constraint: Option<IsaInstanceConstraint>,
}

impl Isa {
    pub fn new(span: Option<Span>, kind: IsaKind, type_: TypeRef, constraint: Option<IsaInstanceConstraint>) -> Self {
        Self { span, kind, type_, constraint }
    }
}

impl Pretty for Isa {}

impl fmt::Display for Isa {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if let Some(constraint) = &self.constraint {
            write!(f, "{} {} {}", self.kind, self.type_, constraint)
        } else {
            write!(f, "{} {}", self.kind, self.type_)
        }
    }
}

#[derive(Clone, Copy, Debug, Eq, PartialEq)]
pub enum IsaKind {
    Exact,
    Subtype,
}

impl Pretty for IsaKind {}

impl fmt::Display for IsaKind {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        let token = match self {
            Self::Exact => token::Keyword::IsaX,
            Self::Subtype => token::Keyword::Isa,
        };
        write!(f, "{}", token)
    }
}

#[derive(Clone, Debug, Eq, PartialEq)]
pub enum IsaInstanceConstraint {
    Relation(Relation),
    Value(Literal),
    Expression(Expression),
    Comparison(Comparison),
    Struct(Literal),
}

impl Pretty for IsaInstanceConstraint {}

impl fmt::Display for IsaInstanceConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Relation(relation) => write!(f, "{}", relation),
            Self::Value(value) => write!(f, "{}", value),
            Self::Expression(expr) => write!(f, "{}", expr),
            Self::Comparison(cmp) => write!(f, "{}", cmp),
            Self::Struct(value) => write!(f, "{}", value),
        }
    }
}
