/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

pub use self::{label::Label, statement::Statement};
use crate::{common::Span, definition::Type, enum_getter, enum_wrapper};

pub mod label;
pub mod statement;

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Definable {
    TypeDeclaration(Type),
}

enum_getter! { Definable
    into_type_statement(TypeDeclaration) => Type,
}

enum_wrapper! { Definable
    Type => TypeDeclaration,
}

impl fmt::Display for Definable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::TypeDeclaration(declaration) => fmt::Display::fmt(declaration, f),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Conjunction {
    span: Option<Span>,
    patterns: Vec<Pattern>,
}

impl Conjunction {
    pub(crate) fn new(span: Option<Span>, patterns: Vec<Pattern>) -> Self {
        Self { span, patterns }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Negation {
    span: Option<Span>,
    patterns: Vec<Pattern>,
}

impl Negation {
    pub(crate) fn new(span: Option<Span>, patterns: Vec<Pattern>) -> Self {
        Self { span, patterns }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Try {
    span: Option<Span>,
    patterns: Vec<Pattern>,
}

impl Try {
    pub(crate) fn new(span: Option<Span>, patterns: Vec<Pattern>) -> Self {
        Self { span, patterns }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Disjunction {
    span: Option<Span>,
    branches: Vec<Vec<Pattern>>,
}

impl Disjunction {
    pub(crate) fn new(span: Option<Span>, branches: Vec<Vec<Pattern>>) -> Self {
        Self { span, branches }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Pattern {
    Conjunction(Conjunction),
    Disjunction(Disjunction),
    Negation(Negation),
    Try(Try),
    Statement(Statement),
}
