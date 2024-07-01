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

impl AnnotationType {
    pub fn new(span: Option<Span>, annotation_kind: token::Annotation, type_: Label) -> Self {
        Self { span, annotation_kind, type_ }
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
