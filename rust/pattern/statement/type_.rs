/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::Span,
    identifier::{Label, ScopedLabel},
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

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum LabelConstraint {
    Name(Label),
    Scoped(ScopedLabel),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum SubKind {
    Direct,
    Transitive,
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

impl fmt::Display for ValueType {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("value ")?;
        fmt::Display::fmt(&self.value_type, f)?;
        Ok(())
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
