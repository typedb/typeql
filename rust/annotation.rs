/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use crate::{
    common::{identifier::Identifier, token, Span, Spanned},
    util::write_joined,
    value::{IntegerLiteral, Literal, StringLiteral},
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Annotation {
    Abstract(Abstract),
    Cardinality(Cardinality),
    Cascade(Cascade),
    Distinct(Distinct),
    Independent(Independent),
    Key(Key),
    Range(Range),
    Regex(Regex),
    Subkey(Subkey),
    Unique(Unique),
    Values(Values),
}

impl Spanned for Annotation {
    fn span(&self) -> Option<Span> {
        match self {
            Annotation::Abstract(annotation) => annotation.span(),
            Annotation::Cardinality(annotation) => annotation.span(),
            Annotation::Cascade(annotation) => annotation.span(),
            Annotation::Distinct(annotation) => annotation.span(),
            Annotation::Independent(annotation) => annotation.span(),
            Annotation::Key(annotation) => annotation.span(),
            Annotation::Range(annotation) => annotation.span(),
            Annotation::Regex(annotation) => annotation.span(),
            Annotation::Subkey(annotation) => annotation.span(),
            Annotation::Unique(annotation) => annotation.span(),
            Annotation::Values(annotation) => annotation.span(),
        }
    }
}

impl fmt::Display for Annotation {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Abstract(inner) => fmt::Display::fmt(inner, f),
            Self::Cardinality(inner) => fmt::Display::fmt(inner, f),
            Self::Cascade(inner) => fmt::Display::fmt(inner, f),
            Self::Distinct(inner) => fmt::Display::fmt(inner, f),
            Self::Independent(inner) => fmt::Display::fmt(inner, f),
            Self::Key(inner) => fmt::Display::fmt(inner, f),
            Self::Range(inner) => fmt::Display::fmt(inner, f),
            Self::Regex(inner) => fmt::Display::fmt(inner, f),
            Self::Subkey(inner) => fmt::Display::fmt(inner, f),
            Self::Unique(inner) => fmt::Display::fmt(inner, f),
            Self::Values(inner) => fmt::Display::fmt(inner, f),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Abstract {
    pub span: Option<Span>,
}

impl Abstract {
    pub fn new(span: Option<Span>) -> Self {
        Self { span }
    }
}

impl Spanned for Abstract {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl fmt::Display for Abstract {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@{}", token::Annotation::Abstract)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Cardinality {
    pub span: Option<Span>,
    pub range: CardinalityRange,
}

impl Cardinality {
    pub fn new(span: Option<Span>, range: CardinalityRange) -> Self {
        Self { span, range }
    }
}

impl Spanned for Cardinality {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl fmt::Display for Cardinality {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@{}({})", token::Annotation::Cardinality, self.range)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum CardinalityRange {
    Exact(IntegerLiteral),
    Range(IntegerLiteral, Option<IntegerLiteral>),
}

impl fmt::Display for CardinalityRange {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Exact(exact) => write!(f, "{}", exact),
            Self::Range(min, None) => write!(f, "{}..", min),
            Self::Range(min, Some(max)) => write!(f, "{}..{}", min, max),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Cascade {
    pub span: Option<Span>,
}

impl Cascade {
    pub fn new(span: Option<Span>) -> Self {
        Self { span }
    }
}

impl Spanned for Cascade {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl fmt::Display for Cascade {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@{}", token::Annotation::Cascade)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Distinct {
    pub span: Option<Span>,
}

impl Distinct {
    pub fn new(span: Option<Span>) -> Self {
        Self { span }
    }
}

impl Spanned for Distinct {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl fmt::Display for Distinct {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@{}", token::Annotation::Distinct)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Independent {
    pub span: Option<Span>,
}

impl Independent {
    pub fn new(span: Option<Span>) -> Self {
        Self { span }
    }
}

impl Spanned for Independent {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl fmt::Display for Independent {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@{}", token::Annotation::Independent)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Key {
    pub span: Option<Span>,
}

impl Key {
    pub fn new(span: Option<Span>) -> Self {
        Self { span }
    }
}

impl Spanned for Key {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl fmt::Display for Key {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@{}", token::Annotation::Key)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Range {
    pub span: Option<Span>,
    pub min: Option<Literal>,
    pub max: Option<Literal>,
}

impl Range {
    pub fn new(span: Option<Span>, min: Option<Literal>, max: Option<Literal>) -> Self {
        Self { span, min, max }
    }
}

impl Spanned for Range {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl fmt::Display for Range {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@{}(", token::Annotation::Range)?;
        if let Some(min) = &self.min {
            write!(f, "{}", min)?;
        }
        f.write_str("..")?;
        if let Some(max) = &self.max {
            write!(f, "{}", max)?;
        }
        f.write_char(')')?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Regex {
    pub span: Option<Span>,
    pub regex: StringLiteral,
}

impl Regex {
    pub fn new(span: Option<Span>, regex: StringLiteral) -> Self {
        Self { span, regex }
    }
}

impl Spanned for Regex {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl fmt::Display for Regex {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@{}({})", token::Annotation::Regex, self.regex.value)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Subkey {
    pub span: Option<Span>,
    pub ident: Identifier,
}

impl Subkey {
    pub fn new(span: Option<Span>, ident: Identifier) -> Self {
        Self { span, ident }
    }
}

impl Spanned for Subkey {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl fmt::Display for Subkey {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@{}({})", token::Annotation::Subkey, self.ident)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Unique {
    pub span: Option<Span>,
}

impl Unique {
    pub fn new(span: Option<Span>) -> Self {
        Self { span }
    }
}

impl Spanned for Unique {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl fmt::Display for Unique {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@{}", token::Annotation::Unique)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Values {
    pub span: Option<Span>,
    pub values: Vec<Literal>,
}

impl Values {
    pub fn new(span: Option<Span>, values: Vec<Literal>) -> Self {
        Self { span, values }
    }
}

impl Spanned for Values {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl fmt::Display for Values {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@{}(", token::Annotation::Values)?;
        write_joined!(f, ", ", &self.values)?;
        f.write_char(')')?;
        Ok(())
    }
}
