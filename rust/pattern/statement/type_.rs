/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{token, Span},
    identifier::{Label, ScopedLabel},
    pretty::Pretty,
    type_::{Type, TypeAny},
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum TypeConstraintBase {
    Sub(Sub),
    Label(LabelConstraint),
    ValueType(ValueType),
    Owns(Owns),
    Relates(Relates),
    Plays(Plays),
}

impl Pretty for TypeConstraintBase {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Sub(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Label(inner) => Pretty::fmt(inner, indent_level, f),
            Self::ValueType(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Owns(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Relates(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Plays(inner) => Pretty::fmt(inner, indent_level, f),
        }
    }
}

impl fmt::Display for TypeConstraintBase {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Sub(inner) => fmt::Display::fmt(inner, f),
            Self::Label(inner) => fmt::Display::fmt(inner, f),
            Self::ValueType(inner) => fmt::Display::fmt(inner, f),
            Self::Owns(inner) => fmt::Display::fmt(inner, f),
            Self::Relates(inner) => fmt::Display::fmt(inner, f),
            Self::Plays(inner) => fmt::Display::fmt(inner, f),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum LabelConstraint {
    Name(Label),
    Scoped(ScopedLabel),
}

impl Pretty for LabelConstraint {}

impl fmt::Display for LabelConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Name(inner) => write!(f, "{} {}", token::Keyword::Label, inner),
            Self::Scoped(inner) => write!(f, "{} {}", token::Keyword::Label, inner),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum SubKind {
    Direct,
    Transitive,
}

impl Pretty for SubKind {}

impl fmt::Display for SubKind {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        let token = match self {
            Self::Transitive => token::Keyword::Sub,
            Self::Direct => token::Keyword::SubX,
        };
        write!(f, "{}", token)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Sub {
    span: Option<Span>,
    kind: SubKind,
    supertype: Type,
}

impl Sub {
    pub fn new(span: Option<Span>, kind: SubKind, supertype: Type) -> Self {
        Self { span, kind, supertype }
    }
}

impl Pretty for Sub {}

impl fmt::Display for Sub {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", self.kind, self.supertype)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ValueType {
    span: Option<Span>,
    value_type: Type,
}

impl ValueType {
    pub fn new(span: Option<Span>, value_type: Type) -> Self {
        Self { span, value_type }
    }
}

impl Pretty for ValueType {}

impl fmt::Display for ValueType {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Keyword::ValueType, self.value_type)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Owns {
    span: Option<Span>,
    owned: TypeAny,
    overridden: Option<Type>,
}

impl Owns {
    pub fn new(span: Option<Span>, owned: TypeAny, overridden: Option<Type>) -> Self {
        Self { span, owned, overridden }
    }
}

impl Pretty for Owns {}

impl fmt::Display for Owns {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Keyword::Owns, self.owned)?;
        if let Some(overridden) = &self.overridden {
            write!(f, " {} {}", token::Keyword::As, overridden)?;
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Relates {
    span: Option<Span>,
    related: TypeAny,
    overridden: Option<Type>,
}

impl Relates {
    pub fn new(span: Option<Span>, related: TypeAny, overridden: Option<Type>) -> Self {
        Self { span, related, overridden }
    }
}

impl Pretty for Relates {}

impl fmt::Display for Relates {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Keyword::Relates, self.related)?;
        if let Some(overridden) = &self.overridden {
            write!(f, " {} {}", token::Keyword::As, overridden)?;
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Plays {
    span: Option<Span>,
    role: Type,
    overridden: Option<Type>,
}

impl Plays {
    pub fn new(span: Option<Span>, role: Type, overridden: Option<Type>) -> Self {
        Self { span, role, overridden }
    }
}

impl Pretty for Plays {}

impl fmt::Display for Plays {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Keyword::Plays, self.role)?;
        if let Some(overridden) = &self.overridden {
            write!(f, " {} {}", token::Keyword::As, overridden)?;
        }
        Ok(())
    }
}
