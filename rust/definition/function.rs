/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::Span,
    identifier::{Identifier, Variable},
    pattern::statement::Type,
    query::data::stage::{Match, Reduce},
};

#[derive(Debug, PartialEq, Eq)]
pub struct Argument {
    span: Option<Span>,
    var: Variable,
    type_: Type,
}

impl Argument {
    pub fn new(span: Option<Span>, var: Variable, type_: Type) -> Self {
        Self { span, var, type_ }
    }
}

#[derive(Debug, PartialEq, Eq)]
pub struct Stream {
    span: Option<Span>,
    types: Vec<Type>,
}

impl Stream {
    pub fn new(span: Option<Span>, types: Vec<Type>) -> Self {
        Self { span, types }
    }
}

#[derive(Debug, PartialEq, Eq)]
pub struct Single {
    span: Option<Span>,
    types: Vec<Type>,
}

impl Single {
    pub fn new(span: Option<Span>, types: Vec<Type>) -> Self {
        Self { span, types }
    }
}

#[derive(Debug, PartialEq, Eq)]
pub enum Return {
    Stream(Stream),
    Single(Single),
}

#[derive(Debug, PartialEq, Eq)]
pub struct Signature {
    span: Option<Span>,
    sigil: Identifier,
    args: Vec<Argument>,
    return_types: Return,
}

impl Signature {
    pub(crate) fn new(span: Option<Span>, sigil: Identifier, args: Vec<Argument>, return_types: Return) -> Self {
        Self { span, sigil, args, return_types }
    }
}

#[derive(Debug, PartialEq, Eq)]
pub struct Function {
    span: Option<Span>,
    signature: Signature,
    body: Match,
    return_stmt: ReturnStatement,
}

impl Function {
    pub fn new(span: Option<Span>, signature: Signature, body: Match, return_stmt: ReturnStatement) -> Self {
        Self { span, signature, body, return_stmt }
    }
}

impl fmt::Display for Function {
    fn fmt(&self, _f: &mut fmt::Formatter<'_>) -> fmt::Result {
        todo!()
    }
}

#[derive(Debug, PartialEq, Eq)]
pub struct ReturnStream {
    span: Option<Span>,
    vars: Vec<Variable>,
}

impl ReturnStream {
    pub fn new(span: Option<Span>, vars: Vec<Variable>) -> Self {
        Self { span, vars }
    }
}

#[derive(Debug, PartialEq, Eq)]
pub enum SingleOutput {
    Variable(Variable),
    Reduce(Reduce),
}

#[derive(Debug, PartialEq, Eq)]
pub struct ReturnSingle {
    span: Option<Span>,
    outputs: Vec<SingleOutput>,
}

impl ReturnSingle {
    pub fn new(span: Option<Span>, outputs: Vec<SingleOutput>) -> Self {
        Self { span, outputs }
    }
}

#[derive(Debug, PartialEq, Eq)]
pub enum ReturnStatement {
    Stream(ReturnStream),
    Single(ReturnSingle),
}
