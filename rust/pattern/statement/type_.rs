/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use super::Type;
use crate::{
    common::Span,
    definition::type_::declaration::{AnnotationOwns, AnnotationRelates, AnnotationSub, AnnotationValueType},
    write_joined,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum SubKind {
    Direct,
    Transitive,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Sub {
    kind: SubKind,
    supertype: Type,
    annotations: Vec<AnnotationSub>,
    span: Option<Span>,
}

impl Sub {
    pub fn new(kind: SubKind, supertype: Type, annotations: Vec<AnnotationSub>, span: Option<Span>) -> Self {
        Self { kind, supertype, annotations, span }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ValueType {
    pub value_type: String, // TODO enum with optional user type?
    pub annotations: Vec<AnnotationValueType>,
    pub span: Option<Span>,
}

impl ValueType {
    pub fn new(value_type: String, annotations: Vec<AnnotationValueType>, span: Option<Span>) -> Self {
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
pub enum Owned {
    List(Type),
    Attribute(Type, Option<Type>),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Owns {
    pub owned: Owned,
    pub annotations: Vec<AnnotationOwns>,
    span: Option<Span>,
}

impl Owns {
    pub fn new(owned: Owned, annotations: Vec<AnnotationOwns>, span: Option<Span>) -> Self {
        Self { owned, annotations, span }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Related {
    List(Type),
    Role(Type, Option<Type>),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Relates {
    pub related: Related,
    pub annotations: Vec<AnnotationRelates>,
    span: Option<Span>,
}

impl Relates {
    pub fn new(related: Related, annotations: Vec<AnnotationRelates>, span: Option<Span>) -> Self {
        Self { related, annotations, span }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Played {
    role: Type,
    overridden: Option<Type>,
}

impl Played {
    pub fn new(role: Type, overridden: Option<Type>) -> Self {
        Self { role, overridden }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Plays {
    played: Played,
    span: Option<Span>,
}

impl Plays {
    pub fn new(played: Played, span: Option<Span>) -> Self {
        Self { played, span }
    }
}
