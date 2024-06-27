/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use super::TypeCapability;
use crate::{
    annotation::Annotation,
    common::Span,
    identifier::{Identifier, Label, ScopedLabel},
    write_joined,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Sub {
    span: Option<Span>,
    supertype_label: Label,
    annotations: Vec<Annotation>,
}

impl Sub {
    pub fn new(supertype_label: Label, annotations: Vec<Annotation>, span: Option<Span>) -> Self {
        Self { supertype_label, annotations, span }
    }

    pub fn build(supertype_label: impl Into<Identifier>) -> Self {
        Self::new(Label::Identifier(supertype_label.into()), Vec::new(), None)
    }
}

impl super::Type {
    #[allow(clippy::should_implement_trait)]
    pub fn sub(mut self, supertype: impl Into<Identifier>) -> Self {
        self.traits.push(TypeCapability::Sub(Sub::build(supertype)));
        self
    }
}

impl Label {
    #[allow(clippy::should_implement_trait)]
    pub fn sub(self, supertype: impl Into<Identifier>) -> super::Type {
        super::Type::from(self).sub(supertype)
    }
}

impl fmt::Display for Sub {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("sub ")?;
        fmt::Display::fmt(&self.supertype_label, f)?;
        if !self.annotations.is_empty() {
            f.write_char(' ')?;
            write_joined!(f, ' ', &self.annotations)?;
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ValueType {
    pub value_type: crate::pattern::statement::Type,
    pub annotations: Vec<Annotation>,
    pub span: Option<Span>,
}

impl ValueType {
    pub fn new(value_type: crate::pattern::statement::Type, annotations: Vec<Annotation>, span: Option<Span>) -> Self {
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
    List(Label),
    Attribute(Label, Option<Label>),
}

impl fmt::Display for Owned {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::List(label) => write!(f, "{label}[]"),
            Self::Attribute(label, None) => write!(f, "{label}"),
            Self::Attribute(label, Some(overridden)) => write!(f, "{label} as {overridden}"),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Owns {
    span: Option<Span>,
    owned: Owned,
    annotations: Vec<Annotation>,
}

impl Owns {
    pub fn new(span: Option<Span>, owned: Owned, annotations: Vec<Annotation>) -> Self {
        Self { span, owned, annotations }
    }
}

impl super::Type {
    pub fn owns(mut self, attribute_type: impl Into<Identifier>) -> Self {
        self.traits.push(TypeCapability::Sub(Sub::build(attribute_type)));
        self
    }
}

impl Label {
    pub fn owns(self, attribute_type: impl Into<Identifier>) -> super::Type {
        super::Type::from(self).owns(attribute_type)
    }
}

impl fmt::Display for Owns {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("owns ")?;
        fmt::Display::fmt(&self.owned, f)?;
        if !self.annotations.is_empty() {
            f.write_char(' ')?;
            write_joined!(f, ' ', &self.annotations)?;
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Related {
    List(Label),
    Role(Label, Option<Label>),
}

impl fmt::Display for Related {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::List(label) => write!(f, "{label}[]"),
            Self::Role(label, None) => write!(f, "{label}"),
            Self::Role(label, Some(overridden)) => write!(f, "{label} as {overridden}"),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Relates {
    pub related: Related,
    pub annotations: Vec<Annotation>,
    span: Option<Span>,
}

impl Relates {
    pub fn new(related: Related, annotations: Vec<Annotation>, span: Option<Span>) -> Self {
        Self { related, annotations, span }
    }
}

impl fmt::Display for Relates {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("relates ")?;
        fmt::Display::fmt(&self.related, f)?;
        if !self.annotations.is_empty() {
            f.write_char(' ')?;
            write_joined!(f, ' ', &self.annotations)?;
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Played {
    role: ScopedLabel,
    overridden: Option<Label>,
}

impl Played {
    pub fn new(role: ScopedLabel, overridden: Option<Label>) -> Self {
        Self { role, overridden }
    }
}

impl fmt::Display for Played {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.role)?;
        if let Some(overridden) = &self.overridden {
            write!(f, " as {overridden}")?;
        }
        Ok(())
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

impl fmt::Display for Plays {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("plays ")?;
        fmt::Display::fmt(&self.played, f)?;
        Ok(())
    }
}
