/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{token, Span, Spanned},
    identifier::{Identifier, Label, ScopedLabel},
    util::write_joined,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Alias {
    span: Option<Span>,
    aliases: Vec<Label>,
}

impl Alias {
    pub fn new(span: Option<Span>, aliases: Vec<Label>) -> Self {
        Self { span, aliases }
    }
}

impl Spanned for Alias {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl fmt::Display for Alias {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", token::Keyword::Alias)?;
        write_joined!(f, ", ", &self.aliases)?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Sub {
    span: Option<Span>,
    supertype_label: Label,
}

impl Sub {
    pub fn new(span: Option<Span>, supertype_label: Label) -> Self {
        Self { span, supertype_label }
    }

    pub fn build(supertype_label: impl Into<Identifier>) -> Self {
        Self::new(None, Label::Identifier(supertype_label.into()))
    }
}

impl Spanned for Sub {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl fmt::Display for Sub {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("sub ")?;
        fmt::Display::fmt(&self.supertype_label, f)?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ValueType {
    span: Option<Span>,
    value_type: crate::pattern::statement::Type,
}

impl ValueType {
    pub fn new(span: Option<Span>, value_type: crate::pattern::statement::Type) -> Self {
        Self { span, value_type }
    }
}

impl Spanned for ValueType {
    fn span(&self) -> Option<Span> {
        self.span
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
}

impl Owns {
    pub fn new(span: Option<Span>, owned: Owned) -> Self {
        Self { span, owned }
    }
}

impl Spanned for Owns {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl fmt::Display for Owns {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("owns ")?;
        fmt::Display::fmt(&self.owned, f)?;
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
    span: Option<Span>,
    related: Related,
}

impl Relates {
    pub fn new(span: Option<Span>, related: Related) -> Self {
        Self { span, related }
    }
}

impl Spanned for Relates {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl fmt::Display for Relates {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("relates ")?;
        fmt::Display::fmt(&self.related, f)?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Plays {
    span: Option<Span>,
    role: ScopedLabel,
    overridden: Option<Label>,
}

impl Plays {
    pub fn new(span: Option<Span>, role: ScopedLabel, overridden: Option<Label>) -> Self {
        Self { span, role, overridden }
    }
}

impl Spanned for Plays {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl fmt::Display for Plays {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("plays ")?;
        todo!();
        Ok(())
    }
}
