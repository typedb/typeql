/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use crate::{
    common::{Span, Spanned},
    pattern::Label,
    write_joined,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum AnnotationSub {
    Abstract(Option<Span>),    // FIXME
    Cascade(Option<Span>),     // FIXME
    Independent(Option<Span>), // FIXME
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct SubDeclaration {
    supertype_label: Label,
    annotations: Vec<AnnotationSub>,
    span: Option<Span>,
}

impl SubDeclaration {
    pub fn new(supertype_label: Label, annotations: Vec<AnnotationSub>, span: Option<Span>) -> Self {
        Self { supertype_label, annotations, span }
    }
}

impl fmt::Display for SubDeclaration {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("sub ")?;
        fmt::Display::fmt(&self.supertype_label, f)?;
        // TODO annotations
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum AnnotationValueType {
    Regex(String, Option<Span>),       // FIXME
    Values(Vec<String>, Option<Span>), // FIXME
}

impl fmt::Display for AnnotationValueType {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Regex(regex, _) => write!(f, "@regex({regex})"),
            Self::Values(values, _) => {
                f.write_str("@values(")?;
                write_joined!(f, ", ", values)?;
                f.write_char(')')?;
                Ok(())
            }
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ValueTypeDeclaration {
    value_type: String, // TODO enum with optional user type?
    annotations: Vec<AnnotationValueType>,
    span: Option<Span>,
}

impl ValueTypeDeclaration {
    pub fn new(value_type: String, annotations: Vec<AnnotationValueType>, span: Option<Span>) -> Self {
        Self { value_type, annotations, span }
    }
}

impl fmt::Display for ValueTypeDeclaration {
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
pub struct TypeDeclaration {
    pub label: Label,
    pub sub: Option<SubDeclaration>,
    pub value_type: Option<ValueTypeDeclaration>,
    // pub owns: Vec<OwnsConstraint>,
    // pub plays: Vec<PlaysConstraint>,
    // pub relates: Vec<RelatesConstraint>,
    span: Option<Span>,
}

impl TypeDeclaration {
    pub(crate) fn new(label: Label, span: Option<Span>) -> Self {
        Self { label, sub: None, value_type: None, span }
    }

    pub fn sub_decl(self, sub_decl: SubDeclaration) -> Self {
        Self { sub: Some(sub_decl), ..self }
    }

    pub fn value_type(self, value_type_decl: ValueTypeDeclaration) -> Self {
        Self { value_type: Some(value_type_decl), ..self }
    }
}

impl fmt::Display for TypeDeclaration {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        fmt::Display::fmt(&self.label, f)?;
        f.write_char(' ')?;
        write_joined!(f, ", ", &self.sub, &self.value_type)?;
        Ok(())
    }
}

impl Spanned for TypeDeclaration {
    fn span(&self) -> Option<Span> {
        self.span
    }
}
