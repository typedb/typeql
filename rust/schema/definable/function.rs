/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Debug, Formatter, Write};

use crate::{
    common::{identifier::Identifier, token, Span, Spanned},
    pretty::{indent, Pretty},
    query::stage::{reduce::Reducer, Stage},
    type_::NamedTypeAny,
    util::write_joined,
    variable::Variable,
};

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct Function {
    pub span: Option<Span>,
    pub signature: Signature,
    pub block: FunctionBlock,
    pub unparsed: String,
}

impl Function {
    pub fn new(span: Option<Span>, signature: Signature, block: FunctionBlock, unparsed: String) -> Self {
        Self { span, signature, block, unparsed }
    }
}

impl Spanned for Function {
    fn span(&self) -> Option<Span> {
        self.span
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
        if f.alternate() {
            return Pretty::fmt(self, 0, f);
        } else {
            write!(f, "{} ", token::Keyword::Fun)?;
            std::fmt::Display::fmt(&self.signature, f)?;
            write!(f, "{} ", token::Char::Colon)?;
            std::fmt::Display::fmt(&self.block, f)
        }
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct Signature {
    pub span: Option<Span>,
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
        if self.args.len() > 0 {
            Pretty::fmt(&self.args[0], indent_level, f)?;
            self.args[1..self.args.len()].iter().try_for_each(|arg| {
                f.write_str(", ")?;
                Pretty::fmt(arg, indent_level, f)
            })?;
        }
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
            if self.args.len() > 0 {
                write!(f, "{}", self.args[0])?;
                self.args[1..self.args.len()].iter().try_for_each(|arg| write!(f, ", {arg}"))?;
            }
            write!(f, ") -> {}", self.output)?;
            Ok(())
        }
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct Argument {
    pub span: Option<Span>,
    pub var: Variable,
    pub type_: NamedTypeAny,
}

impl Argument {
    pub fn new(span: Option<Span>, var: Variable, type_: NamedTypeAny) -> Self {
        Self { span, var, type_ }
    }
}

impl Pretty for Argument {}
impl fmt::Display for Argument {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        write!(f, "{}: {}", self.var, self.type_)
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
    pub span: Option<Span>,
    pub types: Vec<NamedTypeAny>,
}

impl Stream {
    pub fn new(span: Option<Span>, types: Vec<NamedTypeAny>) -> Self {
        Self { span, types }
    }
}

impl Spanned for Stream {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Stream {}

impl fmt::Display for Stream {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{{ ")?;
        write_joined!(f, ", ", self.types)?;
        write!(f, " }}")
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct Single {
    pub span: Option<Span>,
    pub types: Vec<NamedTypeAny>,
}

impl Single {
    pub fn new(span: Option<Span>, types: Vec<NamedTypeAny>) -> Self {
        Self { span, types }
    }
}

impl Spanned for Single {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Single {}

impl fmt::Display for Single {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write_joined!(f, ", ", self.types)
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct FunctionBlock {
    pub span: Option<Span>,
    pub stages: Vec<Stage>,
    pub return_stmt: ReturnStatement,
}

impl FunctionBlock {
    pub fn new(span: Option<Span>, stages: Vec<Stage>, return_stmt: ReturnStatement) -> Self {
        Self { span, stages, return_stmt }
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
        write!(f, " ")?;
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
    pub span: Option<Span>,
    pub vars: Vec<Variable>,
}

impl ReturnStream {
    pub fn new(span: Option<Span>, vars: Vec<Variable>) -> Self {
        Self { span, vars }
    }
}

impl Spanned for ReturnStream {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for ReturnStream {}

impl fmt::Display for ReturnStream {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        assert!(!self.vars.is_empty());
        write!(f, "{} {}", token::Char::CurlyLeft, self.vars[0])?;
        for var in &self.vars[1..] {
            write!(f, ", {}", var)?;
        }
        write!(f, " {}", token::Char::CurlyRight)
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ReturnSingle {
    pub span: Option<Span>,
    pub selector: SingleSelector,
    pub vars: Vec<Variable>,
}

impl ReturnSingle {
    pub fn new(span: Option<Span>, selector: SingleSelector, vars: Vec<Variable>) -> Self {
        Self { span, selector, vars }
    }
}

impl Spanned for ReturnSingle {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for ReturnSingle {}

impl fmt::Display for ReturnSingle {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        assert!(!self.vars.is_empty());
        write!(f, "{} ", self.selector)?;
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
    Value(Vec<Reducer>, Option<Span>),
}

impl Spanned for ReturnReduction {
    fn span(&self) -> Option<Span> {
        match self {
            ReturnReduction::Check(check) => check.span(),
            ReturnReduction::Value(_, span) => *span,
        }
    }
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
            Self::Value(inner, _) => {
                write_joined!(f, ", ", inner)?;
                Ok(())
            }
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Check {
    pub span: Option<Span>,
}

impl Check {
    pub fn new(span: Option<Span>) -> Self {
        Self { span }
    }
}

impl Spanned for Check {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Check {}

impl fmt::Display for Check {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", token::Keyword::Check)
    }
}
