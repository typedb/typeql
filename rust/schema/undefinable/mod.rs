/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{token, Span},
    identifier::{Identifier, Label, ScopedLabel},
    type_::Type,
};

#[derive(Debug, Eq, PartialEq)]
pub enum Undefinable {
    Type(Label),                                // undefine person;
    AnnotationType(AnnotationType),             // undefine @independent from name;
    AnnotationCapability(AnnotationCapability), // undefine @card from person owns name;
    CapabilityType(CapabilityType),             // undefine owns name from person; OR undefine name from person;

    Function(Function), // undefine fun reachable;
    Struct(Struct),     // undefine struct coords;
}

impl fmt::Display for Undefinable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        todo!()
    }
}

#[derive(Debug, Eq, PartialEq)]
pub struct AnnotationType {
    span: Option<Span>,
    annotation_kind: token::Annotation,
    type_: Label,
}

#[derive(Debug, Eq, PartialEq)]
pub struct AnnotationCapability {
    span: Option<Span>,
    annotation_kind: token::Annotation,
    type_: Label,
    capability: Capability,
}

#[derive(Debug, Eq, PartialEq)]
pub struct CapabilityType {
    span: Option<Span>,
    capability: Capability,
    type_: Label,
}

#[derive(Debug, PartialEq, Eq)]
pub struct Function {
    span: Option<Span>,
    ident: Identifier,
}

#[derive(Debug, PartialEq, Eq)]
pub struct Struct {
    span: Option<Span>,
    ident: Identifier,
}

#[derive(Debug, PartialEq, Eq)]
pub enum Capability {
    ValueType(ValueType),
    Owns(Owns),
    Relates(Relates),
    Plays(Plays),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Alias {
    span: Option<Span>,
    labels: Vec<Label>,
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

impl fmt::Display for Sub {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("sub ")?;
        fmt::Display::fmt(&self.supertype_label, f)?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ValueType {
    pub value_type: Type,
    pub span: Option<Span>,
}

impl ValueType {
    pub fn new(value_type: Type, span: Option<Span>) -> Self {
        Self { value_type, span }
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
    Attribute(Label),
}

impl fmt::Display for Owned {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::List(label) => write!(f, "{label}[]"),
            Self::Attribute(label) => write!(f, "{label}"),
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
    pub related: Related,
    span: Option<Span>,
}

impl Relates {
    pub fn new(related: Related, span: Option<Span>) -> Self {
        Self { related, span }
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
    played: ScopedLabel,
}

impl Plays {
    pub fn new(span: Option<Span>, played: ScopedLabel) -> Self {
        Self { span, played }
    }
}

impl fmt::Display for Plays {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("plays ")?;
        fmt::Display::fmt(&self.played, f)?;
        Ok(())
    }
}
