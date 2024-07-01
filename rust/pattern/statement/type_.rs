/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use crate::{
    annotation::Annotation,
    common::Span,
    identifier::{Label, ScopedLabel},
    type_::{Type, TypeAny},
    util::write_joined,
};

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
    kind: SubKind,
    supertype: Type,
    annotations: Vec<Annotation>,
    span: Option<Span>,
}

impl Sub {
    pub fn new(kind: SubKind, supertype: Type, annotations: Vec<Annotation>, span: Option<Span>) -> Self {
        Self { kind, supertype, annotations, span }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ValueType {
    pub value_type: Type,
    pub annotations: Vec<Annotation>,
    pub span: Option<Span>,
}

impl ValueType {
    pub fn new(value_type: Type, annotations: Vec<Annotation>, span: Option<Span>) -> Self {
        Self { value_type, annotations, span }
    }
}

impl fmt::Display for ValueType {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("value ")?;
        fmt::Display::fmt(&self.value_type, f)?;
        if !self.annotations.is_empty() {
            f.write_char(' ')?;
            write_joined!(f, ' ', &self.annotations)?;
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Owns {
    span: Option<Span>,
    owned: TypeAny,
    overridden: Option<Type>,
    annotations: Vec<Annotation>,
}

impl Owns {
    pub fn new(span: Option<Span>, owned: TypeAny, overridden: Option<Type>, annotations: Vec<Annotation>) -> Self {
        Self { span, owned, overridden, annotations }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Relates {
    span: Option<Span>,
    related: TypeAny,
    overridden: Option<Type>,
    annotations: Vec<Annotation>,
}

impl Relates {
    pub fn new(span: Option<Span>, related: TypeAny, overridden: Option<Type>, annotations: Vec<Annotation>) -> Self {
        Self { span, related, overridden, annotations }
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
