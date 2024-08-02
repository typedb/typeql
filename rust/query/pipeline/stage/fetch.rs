/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{token, Span},
    expression::{Expression, FunctionCall},
    pretty::Pretty,
    query::Pipeline,
    value::StringLiteral,
    TypeRefAny, Variable,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Fetch {
    span: Option<Span>,
    object: ProjectionObject,
}

impl Fetch {
    pub fn new(span: Option<Span>, object: ProjectionObject) -> Self {
        Self { span, object }
    }
}

impl Pretty for Fetch {}

impl fmt::Display for Fetch {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", token::Clause::Fetch)?;
        fmt::Display::fmt(&self.object, f)?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Projection {
    Object(ProjectionObject),
    List(ProjectionList),
    Single(ProjectionSingle),
}

impl fmt::Display for Projection {
    fn fmt(&self, _f: &mut fmt::Formatter<'_>) -> fmt::Result {
        todo!()
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ProjectionObject {
    span: Option<Span>,
    fields: Vec<ProjectionObjectField>,
}

impl fmt::Display for ProjectionObject {
    fn fmt(&self, _f: &mut fmt::Formatter<'_>) -> fmt::Result {
        todo!()
    }
}

impl ProjectionObject {
    pub fn new(span: Option<Span>, fields: Vec<ProjectionObjectField>) -> Self {
        Self { span, fields }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ProjectionObjectField {
    span: Option<Span>,
    key: StringLiteral,
    value: Projection,
}

impl ProjectionObjectField {
    pub fn new(span: Option<Span>, key: StringLiteral, value: Projection) -> Self {
        Self { span, key, value }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ProjectionList {
    span: Option<Span>,
    stream: ProjectionStream,
}

impl ProjectionList {
    pub fn new(span: Option<Span>, stream: ProjectionStream) -> Self {
        Self { span, stream }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum ProjectionSingle {
    Attribute(ProjectionAttribute),
    Expression(Expression),
    Subquery(Pipeline),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum ProjectionStream {
    Attribute(ProjectionAttribute),
    Function(FunctionCall),
    Subquery(Pipeline),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ProjectionAttribute {
    span: Option<Span>,
    owner: Variable,
    attribute: TypeRefAny,
}

impl ProjectionAttribute {
    pub fn new(span: Option<Span>, owner: Variable, attribute: TypeRefAny) -> Self {
        Self { span, owner, attribute }
    }
}
