/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;
use std::fmt::{Formatter};

use crate::{
    common::{token, Span},
    expression::{Expression, FunctionCall},
    pretty::Pretty,
    query::Pipeline,
    value::StringLiteral,
    TypeRefAny, Variable,
};
use crate::pretty::indent;

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
        Pretty::fmt(&self.object, indent_level + 1, f)
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
            FetchEntry::Object(projection) => Pretty::fmt(projection, indent_level, f),
            FetchEntry::List(projection) => Pretty::fmt(projection, indent_level, f),
            FetchEntry::Single(projection) => Pretty::fmt(projection, indent_level, f),
        }
    }
}

impl fmt::Display for FetchEntry {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            FetchEntry::Object(projection) => fmt::Display::fmt(projection, f),
            FetchEntry::List(projection) => fmt::Display::fmt(projection, f),
            FetchEntry::Single(projection) => fmt::Display::fmt(projection, f),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct FetchObject {
    span: Option<Span>,
    body: FetchBody,
}

impl FetchObject {
    pub fn new(span: Option<Span>, body: FetchBody) -> Self {
        Self { span, body }
    }
}

impl Pretty for FetchObject {
    fn fmt(&self, indent_level: usize, f: &mut Formatter<'_>) -> fmt::Result {
        indent(indent_level, f)?;
        writeln!(f, "{}", token::Char::CurlyLeft)?;
        Pretty::fmt(&self.body, indent_level + 1, f)?;
        indent(indent_level, f)?;
        write!(f, "{}", token::Char::CurlyRight)
    }
}

impl fmt::Display for FetchObject {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {} {}", token::Char::CurlyLeft, self.body, token::Char::CurlyRight)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum FetchBody {
    Entries(Vec<FetchObjectEntry>),
    AttributesAll(Variable),
}

impl Pretty for FetchBody {
    fn fmt(&self, indent_level: usize, f: &mut Formatter<'_>) -> fmt::Result {
        match self {
            FetchBody::Entries(entries) => {
                if !entries.is_empty() {
                    Pretty::fmt(&entries[0], indent_level + 1, f)?;
                    for field in &entries[1..] {
                        writeln!(f, ",")?;
                        Pretty::fmt(field, indent_level + 1, f)?;
                    }
                    writeln!(f, "")?;
                }
                Ok(())
            }
            FetchBody::AttributesAll(var) => {
                indent(indent_level, f)?;
                write!(f, "{}", var)
            }
        }
    }
}

impl fmt::Display for FetchBody {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            FetchBody::Entries(entries) => {
                if !entries.is_empty() {
                    write!(f, "{}", entries[0])?;
                    for field in &entries[1..] {
                        write!(f, ", {}", field)?;
                    }
                }
                Ok(())
            }
            FetchBody::AttributesAll(var) => {
                write!(f, "{}", var)
            }
        }

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
        write!(f, "\"{}\"{} ", self.key, token::Char::Colon)?;
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
        indent(indent_level, f)?;
        writeln!(f, "{}", token::Char::SquareLeft)?;
        Pretty::fmt(&self.stream, indent_level + 1, f)?;
        writeln!(f, "")?;
        indent(indent_level, f)?;
        write!(f, "{}", token::Char::SquareRight)
    }
}

impl fmt::Display for FetchList {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {} {}", token::Char::SquareLeft, self.stream, token::Char::SquareRight)
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
            FetchSingle::Attribute(single) => Pretty::fmt(single, indent_level, f),
            FetchSingle::Expression(single) => Pretty::fmt(single, indent_level, f),
            FetchSingle::Subquery(single) => Pretty::fmt(single, indent_level, f),
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
        match self {
            FetchStream::Attribute(stream) => Pretty::fmt(stream, indent_level, f),
            FetchStream::Function(stream) => Pretty::fmt(stream, indent_level, f),
            FetchStream::Subquery(stream) => Pretty::fmt(stream, indent_level, f),
        }
    }
}

impl fmt::Display for FetchStream {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            FetchStream::Attribute(stream) => fmt::Display::fmt(stream, f),
            FetchStream::Function(stream) => fmt::Display::fmt(stream, f),
            FetchStream::Subquery(stream) => fmt::Display::fmt(stream, f),
        }    }
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

impl Pretty for FetchAttribute {
    fn fmt(&self, indent_level: usize, f: &mut Formatter<'_>) -> fmt::Result {
        indent(indent_level, f)?;
        fmt::Display::fmt(self, f)
    }
}

impl fmt::Display for FetchAttribute {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}{}{}", self.owner, token::Char::Dot, self.attribute)
    }
}
