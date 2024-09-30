/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Formatter, Write};

use crate::{
    common::{identifier::Identifier, token, Span, Spanned},
    pretty::{indent, Pretty},
    query::{
        pipeline::stage::{Match, Operator},
        stage::{Stage},
        Pipeline,
    },
    type_::TypeRefAny,
    variable::Variable,
};
use crate::query::stage::reduce::Reducer;
use crate::util::write_joined;

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct Function {
    span: Option<Span>,
    pub signature: Signature,
    pub block: FunctionBlock,
}

impl Function {
    pub fn new(span: Option<Span>, signature: Signature, block: FunctionBlock) -> Self {
        Self { span, signature, block }
    }
}

impl Pretty for Function {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        indent(indent_level, f)?;
        write!(f, "{} ", token::Keyword::Fun)?;
        Pretty::fmt(&self.signature, indent_level, f)?;
        f.write_char(':')?;
        f.write_str("\n")?;
        Pretty::fmt(&self.block, indent_level + 1, f)?;
        Ok(())
    }
}

impl fmt::Display for Function {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        // if f.alternate() {
        //     return Pretty::fmt(self, 0, f);
        // }
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
pub struct FunctionBlock {
    pub stages: Vec<Stage>,
    pub return_stmt: ReturnStatement,
}

impl FunctionBlock {
    pub fn new(stages: Vec<Stage>, return_stmt: ReturnStatement) -> Self {
        Self { stages, return_stmt }
    }
}

impl Pretty for FunctionBlock {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        for stage in &self.stages {
            Pretty::fmt(stage, indent_level, f)?;
        }
        write!(f, "\n")?;
        Pretty::fmt(&self.return_stmt, indent_level, f)
    }
}

impl fmt::Display for FunctionBlock {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        for stage in &self.stages {
            fmt::Display::fmt(stage, f)?;
        }
        fmt::Display::fmt(&self.return_stmt, f)
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum ReturnStatement {
    Stream(ReturnStream),
    Single(ReturnSingle),
    Reduce(ReturnReduction),
}

impl Pretty for ReturnStatement {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        indent(indent_level, f)?;
        fmt::Display::fmt(self, f)
    }
}

impl fmt::Display for ReturnStatement {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            ReturnStatement::Stream(return_stream) => {
                write!(f, "{} {};", token::Keyword::Return, return_stream)
            }
            ReturnStatement::Single(return_single) => {
                write!(f, "{} {};", token::Keyword::Return, return_single)
            }
            ReturnStatement::Reduce(reduction) => {
                write!(f, "{} {};", token::Keyword::Return, reduction)
            }
        }
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

impl fmt::Display for ReturnStream {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        assert!(!self.vars.is_empty());
        write!(f, "{}", self.vars[0])?;
        for var in &self.vars[1..] {
            write!(f, ", {}", var)?;
        }
        Ok(())
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ReturnSingle {
    span: Option<Span>,
    pub selector: SingleSelector,
    pub vars: Vec<Variable>,
}

impl ReturnSingle {
    pub fn new(span: Option<Span>, selector: SingleSelector, vars: Vec<Variable>) -> Self {
        Self { span, selector, vars }
    }
}

impl fmt::Display for ReturnSingle {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        assert!(!self.vars.is_empty());
        write!(f, "{}", self.selector)?;
        write!(f, "{}", self.vars[0])?;
        for var in &self.vars[1..] {
            write!(f, ", {}", var)?;
        }
        Ok(())
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum SingleSelector {
    First,
    Last,
}

impl fmt::Display for SingleSelector {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        match self {
            SingleSelector::First => write!(f, "{}", token::Keyword::First),
            SingleSelector::Last => write!(f, "{}", token::Keyword::Last),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum ReturnReduction {
    Check(Check),
    Value(Vec<Reducer>),
}

impl Pretty for ReturnReduction {
    fn fmt(&self, _indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        fmt::Display::fmt(self, f)
    }
}

impl fmt::Display for ReturnReduction {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Check(inner) => fmt::Display::fmt(inner, f),
            Self::Value(inner) => {
                write_joined!(f, ", ", inner)?;
                Ok(())
            }
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Check {
    span: Option<Span>,
}

impl Check {
    pub fn new(span: Option<Span>) -> Self {
        Self { span }
    }
}

impl Pretty for Check {}

impl fmt::Display for Check {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{};", token::Keyword::Check)
    }
}

