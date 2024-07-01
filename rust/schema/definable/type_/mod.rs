/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use self::capability::{Alias, Owns, Plays, Relates, Sub, ValueType};
use crate::{
    annotation::Annotation,
    common::{Span, Spanned},
    identifier::{Label, ReservedLabel},
    pretty::Pretty,
    util::write_joined,
};

pub mod capability;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Type {
    span: Option<Span>,
    kind: Option<ReservedLabel>,
    label: Label,
    capabilities: Vec<Capability>,
}

impl Type {
    pub(crate) fn new(
        span: Option<Span>,
        kind: Option<ReservedLabel>,
        label: Label,
        capabilities: Vec<Capability>,
    ) -> Self {
        Self { span, kind, label, capabilities }
    }

    pub fn build(label: Label) -> Self {
        Self::new(None, None, label, Vec::new())
    }
}

impl Spanned for Type {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Type {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        fmt::Display::fmt(&self.label, f)?;
        f.write_char(' ')?;
        let joiner = if f.alternate() { ",\n    " } else { ", " };
        write_joined!(f, joiner, &self.capabilities)?;
        Ok(())
    }
}

impl fmt::Display for Type {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        fmt::Display::fmt(&self.label, f)?;
        f.write_char(' ')?;
        write_joined!(f, ", ", &self.capabilities)?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Capability {
    span: Option<Span>,
    base: CapabilityBase,
    annotations: Vec<Annotation>,
}

impl Capability {
    pub fn new(span: Option<Span>, base: CapabilityBase, annotations: Vec<Annotation>) -> Self {
        Self { span, base, annotations }
    }
}

impl Spanned for Capability {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl fmt::Display for Capability {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        todo!()
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum CapabilityBase {
    Sub(Sub),
    Alias(Alias),
    Owns(Owns),
    Plays(Plays),
    Relates(Relates),
    ValueType(ValueType),
}

impl Spanned for CapabilityBase {
    fn span(&self) -> Option<Span> {
        match self {
            Self::Sub(inner) => inner.span(),
            Self::Alias(inner) => inner.span(),
            Self::Owns(inner) => inner.span(),
            Self::Plays(inner) => inner.span(),
            Self::Relates(inner) => inner.span(),
            Self::ValueType(inner) => inner.span(),
        }
    }
}

impl fmt::Display for CapabilityBase {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Sub(inner) => fmt::Display::fmt(inner, f),
            Self::Alias(inner) => fmt::Display::fmt(inner, f),
            Self::Owns(inner) => fmt::Display::fmt(inner, f),
            Self::Plays(inner) => fmt::Display::fmt(inner, f),
            Self::Relates(inner) => fmt::Display::fmt(inner, f),
            Self::ValueType(inner) => fmt::Display::fmt(inner, f),
        }
    }
}
