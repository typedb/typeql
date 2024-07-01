/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{common::Span, identifier::Variable, pattern::statement::thing::Relation};

#[derive(Debug, Eq, PartialEq)]
pub struct Delete {
    span: Option<Span>,
    deletables: Vec<Deletable>,
}

impl Delete {
    pub fn new(span: Option<Span>, deletables: Vec<Deletable>) -> Self {
        Self { span, deletables }
    }
}

#[derive(Debug, Eq, PartialEq)]
pub struct Deletable {
    span: Option<Span>,
    kind: DeletableKind,
}

impl Deletable {
    pub fn new(span: Option<Span>, kind: DeletableKind) -> Self {
        Self { span, kind }
    }
}

#[derive(Debug, Eq, PartialEq)]
pub enum DeletableKind {
    Has { attribute: Variable, owner: Variable },
    Links { players: Relation, relation: Variable },
    Concept { variable: Variable },
}
