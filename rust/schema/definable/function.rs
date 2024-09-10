/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use crate::{
    common::{identifier::Identifier, token, Span, Spanned},
    pretty::{indent, Pretty},
    query::pipeline::stage::{Match, Modifier, Reduce},
    type_::TypeRefAny,
    variable::Variable,
};

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct Function {
    span: Option<Span>,
    pub signature: Signature,
    pub body: Match,
    pub modifiers: Vec<Modifier>,
    pub return_stmt: ReturnStatement,
}

impl Function {
    pub fn new(
        span: Option<Span>,
        signature: Signature,
        body: Match,
        modifiers: Vec<Modifier>,
        return_stmt: ReturnStatement,
    ) -> Self {
        Self { span, signature, body, modifiers, return_stmt }
    }
}

impl Pretty for Function {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        indent(indent_level, f)?;
        write!(f, "{} ", token::Keyword::Fun)?;
        Pretty::fmt(&self.signature, indent_level, f)?;
        f.write_char(':')?;
        Ok(())
    }
}

impl fmt::Display for Function {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if f.alternate() {
            return Pretty::fmt(self, 0, f);
        }

        todo!()
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct Signature {
    span: Option<Span>,
    pub ident: Identifier,
    pub args: Vec<Argument>,
    pub output: Output,
}

impl Signature {
    pub(crate) fn new(span: Option<Span>, ident: Identifier, args: Vec<Argument>, output: Output) -> Self {
        Self { span, ident, args, output }
    }
}

impl Pretty for Signature {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}(", self.ident)?;
        todo!("write args");
        write!(f, ") -> ")?;
        Pretty::fmt(&self.output, indent_level, f)?;
        Ok(())
    }
}

impl fmt::Display for Signature {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if f.alternate() {
            Pretty::fmt(self, 0, f)
        } else {
            write!(f, "{}(", self.ident)?;
            todo!("write args");
            write!(f, ") -> {}", self.output)?;
            Ok(())
        }
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct Argument {
    span: Option<Span>,
    pub var: Variable,
    pub type_: TypeRefAny,
}

impl Argument {
    pub fn new(span: Option<Span>, var: Variable, type_: TypeRefAny) -> Self {
        Self { span, var, type_ }
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum Output {
    Stream(Stream),
    Single(Single),
}

impl Spanned for Output {
    fn span(&self) -> Option<Span> {
        match self {
            Self::Stream(inner) => inner.span(),
            Self::Single(inner) => inner.span(),
        }
    }
}

impl Pretty for Output {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Stream(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Single(inner) => Pretty::fmt(inner, indent_level, f),
        }
    }
}

impl fmt::Display for Output {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Stream(inner) => fmt::Display::fmt(inner, f),
            Self::Single(inner) => fmt::Display::fmt(inner, f),
        }
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct Stream {
    span: Option<Span>,
    pub types: Vec<TypeRefAny>,
}

impl Stream {
    pub fn new(span: Option<Span>, types: Vec<TypeRefAny>) -> Self {
        Self { span, types }
    }
}

impl Spanned for Stream {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Stream {
    fn fmt(&self, _indent_level: usize, _f: &mut fmt::Formatter<'_>) -> fmt::Result {
        todo!()
    }
}

impl fmt::Display for Stream {
    fn fmt(&self, _f: &mut fmt::Formatter<'_>) -> fmt::Result {
        todo!()
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct Single {
    span: Option<Span>,
    pub types: Vec<TypeRefAny>,
}

impl Single {
    pub fn new(span: Option<Span>, types: Vec<TypeRefAny>) -> Self {
        Self { span, types }
    }
}

impl Spanned for Single {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Single {
    fn fmt(&self, _indent_level: usize, _f: &mut fmt::Formatter<'_>) -> fmt::Result {
        todo!()
    }
}

impl fmt::Display for Single {
    fn fmt(&self, _f: &mut fmt::Formatter<'_>) -> fmt::Result {
        todo!()
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ReturnStream {
    span: Option<Span>,
    pub vars: Vec<Variable>,
}

impl ReturnStream {
    pub fn new(span: Option<Span>, vars: Vec<Variable>) -> Self {
        Self { span, vars }
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum SingleOutput {
    Variable(Variable),
    Reduce(Reduce),
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum ReturnStatement {
    Stream(ReturnStream),
    Reduce(Reduce),
}
