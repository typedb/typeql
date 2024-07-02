/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use super::definable::type_::CapabilityBase;
use crate::{
    common::{token, Span},
    identifier::{Identifier, Label},
    pretty::Pretty,
};

#[derive(Debug, Eq, PartialEq)]
pub enum Undefinable {
    Type(Label),                                // undefine person;
    AnnotationType(AnnotationType),             // undefine @independent from name;
    AnnotationCapability(AnnotationCapability), // undefine @card from person owns name;
    CapabilityType(CapabilityType),             // undefine owns name from person;
    Override(Override),                         // undefine as name from person owns first-name;

    Function(Function), // undefine fun reachable;
    Struct(Struct),     // undefine struct coords;
}

impl Pretty for Undefinable {}

impl fmt::Display for Undefinable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Type(inner) => fmt::Display::fmt(inner, f),
            Self::AnnotationType(inner) => fmt::Display::fmt(inner, f),
            Self::AnnotationCapability(inner) => fmt::Display::fmt(inner, f),
            Self::CapabilityType(inner) => fmt::Display::fmt(inner, f),
            Self::Override(inner) => fmt::Display::fmt(inner, f),
            Self::Function(inner) => fmt::Display::fmt(inner, f),
            Self::Struct(inner) => fmt::Display::fmt(inner, f),
        }
    }
}

#[derive(Debug, Eq, PartialEq)]
pub struct AnnotationType {
    span: Option<Span>,
    annotation_kind: token::Annotation,
    type_: Label,
}

impl AnnotationType {
    pub fn new(span: Option<Span>, annotation_kind: token::Annotation, type_: Label) -> Self {
        Self { span, annotation_kind, type_ }
    }
}

impl Pretty for AnnotationType {}

impl fmt::Display for AnnotationType {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        todo!()
    }
}

#[derive(Debug, Eq, PartialEq)]
pub struct AnnotationCapability {
    span: Option<Span>,
    annotation_kind: token::Annotation,
    type_: Label,
    capability: CapabilityBase,
}

impl AnnotationCapability {
    pub fn new(
        span: Option<Span>,
        annotation_kind: token::Annotation,
        type_: Label,
        capability: CapabilityBase,
    ) -> Self {
        Self { span, annotation_kind, type_, capability }
    }
}

impl Pretty for AnnotationCapability {}

impl fmt::Display for AnnotationCapability {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@{} {} {} {}", self.annotation_kind, token::Keyword::From, self.type_, self.capability)
    }
}

#[derive(Debug, Eq, PartialEq)]
pub struct CapabilityType {
    span: Option<Span>,
    capability: CapabilityBase,
    type_: Label,
}

impl CapabilityType {
    pub fn new(span: Option<Span>, capability: CapabilityBase, type_: Label) -> Self {
        Self { span, capability, type_ }
    }
}

impl Pretty for CapabilityType {}

impl fmt::Display for CapabilityType {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {} {}", self.capability, token::Keyword::From, self.type_)
    }
}

#[derive(Debug, Eq, PartialEq)]
pub struct Override {
    span: Option<Span>,
    overridden: Label,
    type_: Label,
    capability: CapabilityBase,
}

impl Override {
    pub fn new(span: Option<Span>, overridden: Label, type_: Label, capability: CapabilityBase) -> Self {
        Self { span, overridden, type_, capability }
    }
}

impl Pretty for Override {}

impl fmt::Display for Override {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        todo!()
    }
}

#[derive(Debug, PartialEq, Eq)]
pub struct Function {
    span: Option<Span>,
    ident: Identifier,
}

impl Function {
    pub fn new(span: Option<Span>, ident: Identifier) -> Self {
        Self { span, ident }
    }
}

impl Pretty for Function {}

impl fmt::Display for Function {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Keyword::Fun, self.ident)
    }
}

#[derive(Debug, PartialEq, Eq)]
pub struct Struct {
    span: Option<Span>,
    ident: Identifier,
}

impl Struct {
    pub fn new(span: Option<Span>, ident: Identifier) -> Self {
        Self { span, ident }
    }
}

impl Pretty for Struct {}

impl fmt::Display for Struct {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Keyword::Struct, self.ident)
    }
}
