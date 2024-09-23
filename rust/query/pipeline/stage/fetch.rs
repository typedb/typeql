/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{fmt, fmt::Formatter};

use crate::{
    common::{token, Span},
    expression::{Expression, FunctionCall},
    pretty::{indent, Pretty},
    query::Pipeline,
    value::StringLiteral,
    TypeRefAny, Variable,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Fetch {
    span: Option<Span>,
    object: FetchObject,
}

impl Fetch {
    pub fn new(span: Option<Span>, object: FetchObject) -> Self {
        Self { span, object }
    }
}

impl Pretty for Fetch {
    fn fmt(&self, indent_level: usize, f: &mut Formatter<'_>) -> fmt::Result {
        indent(indent_level, f)?;
        write!(f, "{} ", token::Clause::Fetch)?;
        Pretty::fmt(&self.object, indent_level, f)
    }
}

impl fmt::Display for Fetch {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", token::Clause::Fetch)?;
        fmt::Display::fmt(&self.object, f)?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum FetchEntry {
    Object(FetchObject),
    List(FetchList),
    Single(FetchSingle),
}

impl Pretty for FetchEntry {
    fn fmt(&self, indent_level: usize, f: &mut Formatter<'_>) -> fmt::Result {
        match self {
            FetchEntry::Object(entry) => {
                write!(f, " ")?;
                Pretty::fmt(entry, indent_level, f)
            }
            FetchEntry::List(entry) => {
                write!(f, " ")?;
                Pretty::fmt(entry, indent_level, f)
            }
            FetchEntry::Single(entry) => Pretty::fmt(entry, indent_level, f),
        }
    }
}

impl fmt::Display for FetchEntry {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            FetchEntry::Object(entry) => fmt::Display::fmt(entry, f),
            FetchEntry::List(entry) => fmt::Display::fmt(entry, f),
            FetchEntry::Single(entry) => fmt::Display::fmt(entry, f),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct FetchObject {
    span: Option<Span>,
    body: FetchObjectBody,
}

impl FetchObject {
    pub fn new(span: Option<Span>, body: FetchObjectBody) -> Self {
        Self { span, body }
    }
}

impl Pretty for FetchObject {
    fn fmt(&self, indent_level: usize, f: &mut Formatter<'_>) -> fmt::Result {
        Pretty::fmt(&self.body, indent_level, f)
    }
}

impl fmt::Display for FetchObject {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        fmt::Display::fmt(&self.body, f)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum FetchObjectBody {
    Entries(Vec<FetchObjectEntry>),
    AttributesAll(Variable),
}

impl Pretty for FetchObjectBody {
    fn fmt(&self, indent_level: usize, f: &mut Formatter<'_>) -> fmt::Result {
        // don't indent before left curly, since it will always be in the same line as the previous element
        write!(f, "{}", token::Char::CurlyLeft)?;
        match self {
            FetchObjectBody::Entries(entries) => {
                if !entries.is_empty() {
                    writeln!(f)?; // entries always go on new line
                    Pretty::fmt(&entries[0], indent_level + 1, f)?;
                    for field in &entries[1..] {
                        writeln!(f, ",")?;
                        Pretty::fmt(field, indent_level + 1, f)?;
                    }
                    writeln!(f)?;
                }
                indent(indent_level, f)?;
                write!(f, "{}", token::Char::CurlyRight)
            }
            FetchObjectBody::AttributesAll(var) => {
                write!(f, " {}{}{} {}", var, token::Char::Dot, token::Char::Star, token::Char::CurlyRight)
            }
        }
    }
}

impl fmt::Display for FetchObjectBody {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", token::Char::CurlyLeft)?;
        match self {
            FetchObjectBody::Entries(entries) => {
                if !entries.is_empty() {
                    write!(f, "{}", entries[0])?;
                    for field in &entries[1..] {
                        write!(f, ", {}", field)?;
                    }
                }
            }
            FetchObjectBody::AttributesAll(var) => {
                write!(f, "{}", var)?;
            }
        }
        write!(f, "{}", token::Char::CurlyRight)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct FetchObjectEntry {
    span: Option<Span>,
    key: StringLiteral,
    value: FetchEntry,
}

impl FetchObjectEntry {
    pub fn new(span: Option<Span>, key: StringLiteral, value: FetchEntry) -> Self {
        Self { span, key, value }
    }
}

impl Pretty for FetchObjectEntry {
    fn fmt(&self, indent_level: usize, f: &mut Formatter<'_>) -> fmt::Result {
        indent(indent_level, f)?;
        write!(f, "{}{}", self.key, token::Char::Colon)?;
        Pretty::fmt(&self.value, indent_level, f)
    }
}

impl fmt::Display for FetchObjectEntry {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "\"{}\"{} {}", self.key, token::Char::Colon, self.value)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct FetchList {
    span: Option<Span>,
    stream: FetchStream,
}

impl FetchList {
    pub fn new(span: Option<Span>, stream: FetchStream) -> Self {
        Self { span, stream }
    }
}

impl Pretty for FetchList {
    fn fmt(&self, indent_level: usize, f: &mut Formatter<'_>) -> fmt::Result {
        Pretty::fmt(&self.stream, indent_level, f)
    }
}

impl fmt::Display for FetchList {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        fmt::Display::fmt(&self.stream, f)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum FetchSingle {
    Attribute(FetchAttribute),
    Expression(Expression),
    Subquery(Pipeline),
}

impl Pretty for FetchSingle {
    fn fmt(&self, indent_level: usize, f: &mut Formatter<'_>) -> fmt::Result {
        match self {
            FetchSingle::Attribute(single) => {
                write!(f, " ")?;
                Pretty::fmt(single, indent_level, f)
            }
            FetchSingle::Expression(single) => {
                write!(f, " ")?;
                Pretty::fmt(single, indent_level, f)
            }
            FetchSingle::Subquery(single) => {
                writeln!(f)?;
                Pretty::fmt(single, indent_level + 1, f)
            }
        }
    }
}

impl fmt::Display for FetchSingle {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            FetchSingle::Attribute(single) => fmt::Display::fmt(single, f),
            FetchSingle::Expression(single) => fmt::Display::fmt(single, f),
            FetchSingle::Subquery(single) => fmt::Display::fmt(single, f),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum FetchStream {
    Attribute(FetchAttribute),
    Function(FunctionCall),
    Subquery(Pipeline),
}

impl Pretty for FetchStream {
    fn fmt(&self, indent_level: usize, f: &mut Formatter<'_>) -> fmt::Result {
        // don't indent before left curly, since it will always be in the same line as the previous element
        match self {
            FetchStream::Attribute(stream) => {
                write!(f, "{} ", token::Char::SquareLeft)?;
                Pretty::fmt(stream, indent_level, f)?;
                write!(f, " {}", token::Char::SquareRight)
            }
            FetchStream::Function(stream) => {
                write!(f, "{} ", token::Char::SquareLeft)?;
                Pretty::fmt(stream, indent_level, f)?;
                write!(f, " {}", token::Char::SquareRight)
            }
            FetchStream::Subquery(stream) => {
                writeln!(f, "{}", token::Char::SquareLeft)?;
                Pretty::fmt(stream, indent_level + 1, f)?;
                writeln!(f)?;
                indent(indent_level, f)?;
                write!(f, "{}", token::Char::SquareRight)
            }
        }
    }
}

impl fmt::Display for FetchStream {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", token::Char::SquareLeft)?;
        match self {
            FetchStream::Attribute(stream) => fmt::Display::fmt(stream, f),
            FetchStream::Function(stream) => fmt::Display::fmt(stream, f),
            FetchStream::Subquery(stream) => fmt::Display::fmt(stream, f),
        }?;
        write!(f, "{}", token::Char::SquareRight)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct FetchAttribute {
    span: Option<Span>,
    owner: Variable,
    attribute: TypeRefAny,
}

impl FetchAttribute {
    pub fn new(span: Option<Span>, owner: Variable, attribute: TypeRefAny) -> Self {
        Self { span, owner, attribute }
    }
}

impl Pretty for FetchAttribute {}

impl fmt::Display for FetchAttribute {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}{}{}", self.owner, token::Char::Dot, self.attribute)
    }
}
