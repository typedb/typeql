/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use crate::{
    annotation::Annotation,
    common::{token, Span},
    pretty::{indent, Pretty},
    type_::{Label, NamedType, ScopedLabel, TypeRef, TypeRefAny},
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Type {
    span: Option<Span>,
    pub kind: Option<token::Kind>,
    pub type_: TypeRefAny,
    pub constraints: Vec<Constraint>,
}

impl Type {
    pub fn new(span: Option<Span>, kind: Option<token::Kind>, type_: TypeRefAny, constraints: Vec<Constraint>) -> Self {
        Self { span, kind, type_, constraints }
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
pub struct Constraint {
    span: Option<Span>,
    pub base: ConstraintBase,
    pub annotations: Vec<Annotation>,
}

impl Constraint {
    pub fn new(span: Option<Span>, base: ConstraintBase, annotations: Vec<Annotation>) -> Self {
        Self { span, base, annotations }
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

#[derive(Debug, Clone, Eq, PartialEq)]
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
pub struct Sub {
    span: Option<Span>,
    pub kind: SubKind,
    pub supertype: TypeRefAny,
}

impl Sub {
    pub fn new(span: Option<Span>, kind: SubKind, supertype: TypeRefAny) -> Self {
        Self { span, kind, supertype }
    }
}

impl Pretty for Sub {}

impl fmt::Display for Sub {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", self.kind, self.supertype)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ValueType {
    span: Option<Span>,
    pub value_type: NamedType,
}

impl ValueType {
    pub fn new(span: Option<Span>, value_type: NamedType) -> Self {
        Self { span, value_type }
    }
}

impl Pretty for ValueType {}

impl fmt::Display for ValueType {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Keyword::ValueType, self.value_type)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Owns {
    span: Option<Span>,
    pub owned: TypeRefAny,
    pub overridden: Option<TypeRef>,
}

impl Owns {
    pub fn new(span: Option<Span>, owned: TypeRefAny, overridden: Option<TypeRef>) -> Self {
        Self { span, owned, overridden }
    }
}

impl Pretty for Owns {}

impl fmt::Display for Owns {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Keyword::Owns, self.owned)?;
        if let Some(overridden) = &self.overridden {
            write!(f, " {} {}", token::Keyword::As, overridden)?;
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Relates {
    span: Option<Span>,
    pub related: TypeRefAny,
    pub overridden: Option<TypeRef>,
}

impl Relates {
    pub fn new(span: Option<Span>, related: TypeRefAny, overridden: Option<TypeRef>) -> Self {
        Self { span, related, overridden }
    }
}

impl Pretty for Relates {}

impl fmt::Display for Relates {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Keyword::Relates, self.related)?;
        if let Some(overridden) = &self.overridden {
            write!(f, " {} {}", token::Keyword::As, overridden)?;
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Plays {
    span: Option<Span>,
    pub role: TypeRef,
    pub overridden: Option<TypeRef>,
}

impl Plays {
    pub fn new(span: Option<Span>, role: TypeRef, overridden: Option<TypeRef>) -> Self {
        Self { span, role, overridden }
    }
}

impl Pretty for Plays {}

impl fmt::Display for Plays {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Keyword::Plays, self.role)?;
        if let Some(overridden) = &self.overridden {
            write!(f, " {} {}", token::Keyword::As, overridden)?;
        }
        Ok(())
    }
}
