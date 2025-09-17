/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

#[cfg(feature = "quine")]
use {polyquine::Quine, proc_macro2::TokenStream};

use crate::{
    annotation::Annotation,
    common::{token, Span, Spanned},
    pretty::{indent, Pretty},
    type_::{Label, NamedType, ScopedLabel, TypeRef, TypeRefAny},
};

#[derive(Debug, Clone, Eq, PartialEq)]
#[cfg_attr(feature = "quine", derive(Quine))]
pub struct Type {
    pub span: Option<Span>,
    pub kind: Option<token::Kind>,
    pub type_: TypeRef,
    pub constraints: Vec<Constraint>,
}

impl Type {
    pub fn new(span: Option<Span>, kind: Option<token::Kind>, type_: TypeRef, constraints: Vec<Constraint>) -> Self {
        Self { span, kind, type_, constraints }
    }
}

impl Spanned for Type {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Type {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.type_)?;
        if let Some((first, rest)) = self.constraints.split_first() {
            f.write_char(' ')?;
            Pretty::fmt(first, indent_level, f)?;
            for constraint in rest {
                f.write_str(",\n")?;
                indent(indent_level + 1, f)?;
                Pretty::fmt(constraint, indent_level, f)?;
            }
        }
        Ok(())
    }
}

impl fmt::Display for Type {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.type_)?;
        if let Some((first, rest)) = self.constraints.split_first() {
            write!(f, " {}", first)?;
            for constraint in rest {
                write!(f, ", {}", constraint)?;
            }
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
#[cfg_attr(feature = "quine", derive(Quine))]
pub struct Constraint {
    pub span: Option<Span>,
    pub base: ConstraintBase,
    pub annotations: Vec<Annotation>,
}

impl Constraint {
    pub fn new(span: Option<Span>, base: ConstraintBase, annotations: Vec<Annotation>) -> Self {
        Self { span, base, annotations }
    }
}

impl Spanned for Constraint {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Constraint {}

impl fmt::Display for Constraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.base)?;
        for annotation in &self.annotations {
            write!(f, " {}", annotation)?;
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
#[cfg_attr(feature = "quine", derive(Quine))]
pub enum ConstraintBase {
    Sub(Sub),
    Label(LabelConstraint),
    ValueType(ValueType),
    Owns(Owns),
    Relates(Relates),
    Plays(Plays),
}

impl Pretty for ConstraintBase {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Sub(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Label(inner) => Pretty::fmt(inner, indent_level, f),
            Self::ValueType(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Owns(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Relates(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Plays(inner) => Pretty::fmt(inner, indent_level, f),
        }
    }
}

impl fmt::Display for ConstraintBase {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Sub(inner) => fmt::Display::fmt(inner, f),
            Self::Label(inner) => fmt::Display::fmt(inner, f),
            Self::ValueType(inner) => fmt::Display::fmt(inner, f),
            Self::Owns(inner) => fmt::Display::fmt(inner, f),
            Self::Relates(inner) => fmt::Display::fmt(inner, f),
            Self::Plays(inner) => fmt::Display::fmt(inner, f),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
#[cfg_attr(feature = "quine", derive(Quine))]
pub enum LabelConstraint {
    Name(Label),
    Scoped(ScopedLabel),
}

impl Pretty for LabelConstraint {}

impl fmt::Display for LabelConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Name(inner) => write!(f, "{} {}", token::Keyword::Label, inner),
            Self::Scoped(inner) => write!(f, "{} {}", token::Keyword::Label, inner),
        }
    }
}

#[derive(Debug, Clone, Copy, Eq, PartialEq)]
#[cfg_attr(feature = "quine", derive(Quine))]
pub enum SubKind {
    Direct,
    Transitive,
}

impl Pretty for SubKind {}

impl fmt::Display for SubKind {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        let token = match self {
            Self::Transitive => token::Keyword::Sub,
            Self::Direct => token::Keyword::SubX,
        };
        write!(f, "{}", token)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
#[cfg_attr(feature = "quine", derive(Quine))]
pub struct Sub {
    pub span: Option<Span>,
    pub kind: SubKind,
    pub supertype: TypeRef,
}

impl Sub {
    pub fn new(span: Option<Span>, kind: SubKind, supertype: TypeRef) -> Self {
        Self { span, kind, supertype }
    }
}

impl Spanned for Sub {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Sub {}

impl fmt::Display for Sub {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", self.kind, self.supertype)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
#[cfg_attr(feature = "quine", derive(Quine))]
pub struct ValueType {
    pub span: Option<Span>,
    pub value_type: NamedType,
}

impl ValueType {
    pub fn new(span: Option<Span>, value_type: NamedType) -> Self {
        Self { span, value_type }
    }
}

impl Spanned for ValueType {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for ValueType {}

impl fmt::Display for ValueType {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Keyword::Value, self.value_type)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
#[cfg_attr(feature = "quine", derive(Quine))]
pub struct Owns {
    pub span: Option<Span>,
    pub owned: TypeRefAny,
}

impl Owns {
    pub fn new(span: Option<Span>, owned: TypeRefAny) -> Self {
        Self { span, owned }
    }
}

impl Spanned for Owns {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Owns {}

impl fmt::Display for Owns {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Keyword::Owns, self.owned)?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
#[cfg_attr(feature = "quine", derive(Quine))]
pub struct Relates {
    pub span: Option<Span>,
    pub related: TypeRefAny,
    pub specialised: Option<TypeRefAny>,
}

impl Relates {
    pub fn new(span: Option<Span>, related: TypeRefAny, specialised: Option<TypeRefAny>) -> Self {
        Self { span, related, specialised }
    }
}

impl Spanned for Relates {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Relates {}

impl fmt::Display for Relates {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Keyword::Relates, self.related)?;
        if let Some(specialised) = &self.specialised {
            write!(f, " {} {}", token::Keyword::As, specialised)?;
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
#[cfg_attr(feature = "quine", derive(Quine))]
pub struct Plays {
    pub span: Option<Span>,
    pub role: TypeRef,
}

impl Plays {
    pub fn new(span: Option<Span>, role: TypeRef) -> Self {
        Self { span, role }
    }
}

impl Spanned for Plays {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Plays {}

impl fmt::Display for Plays {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Keyword::Plays, self.role)?;
        Ok(())
    }
}
