/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{identifier::Identifier, token, Span, Spanned},
    pretty::Pretty,
    type_::{Label, NamedType, ScopedLabel, TypeRefAny},
    util::write_joined,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Alias {
    span: Option<Span>,
    pub aliases: Vec<Label>,
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
    pub supertype_label: Label,
}

impl Sub {
    pub fn new(span: Option<Span>, supertype_label: Label) -> Self {
        Self { span, supertype_label }
    }

    pub fn build(supertype_label: impl Into<Identifier>) -> Self {
        Self::new(None, Label::new(None, supertype_label.into()))
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

impl fmt::Display for ValueType {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("value ")?;
        fmt::Display::fmt(&self.value_type, f)?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Owns {
    span: Option<Span>,
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
pub struct Relates {
    span: Option<Span>,
    pub related: TypeRefAny,
    pub specialised: Option<Label>,
}

impl Relates {
    pub fn new(span: Option<Span>, related: TypeRefAny, specialised: Option<Label>) -> Self {
        Self { span, related, specialised }
    }
}

impl Spanned for Relates {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

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
pub struct Plays {
    span: Option<Span>,
    pub role: ScopedLabel,
}

impl Plays {
    pub fn new(span: Option<Span>, role: ScopedLabel) -> Self {
        Self { span, role }
    }
}

impl Spanned for Plays {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl fmt::Display for Plays {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Keyword::Plays, self.role)?;
        Ok(())
    }
}
