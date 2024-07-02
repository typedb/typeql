/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use crate::{
    common::{token, Span},
    identifier::Variable,
    pattern::statement::thing::Relation,
    pretty::{indent, Pretty},
};

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

impl Pretty for Delete {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", token::Clause::Delete)?;
        for deletable in &self.deletables {
            writeln!(f)?;
            indent(indent_level, f)?;
            Pretty::fmt(deletable, indent_level, f)?;
            f.write_char(';')?;
        }
        Ok(())
    }
}

impl fmt::Display for Delete {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if f.alternate() {
            Pretty::fmt(self, 0, f)
        } else {
            write!(f, "{}", token::Clause::Delete)?;
            for deletable in &self.deletables {
                write!(f, " {deletable};")?;
            }
            Ok(())
        }
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

impl Pretty for Deletable {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        Pretty::fmt(&self.kind, indent_level, f)
    }
}

impl fmt::Display for Deletable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        fmt::Display::fmt(&self.kind, f)
    }
}

#[derive(Debug, Eq, PartialEq)]
pub enum DeletableKind {
    Has { attribute: Variable, owner: Variable },
    Links { players: Relation, relation: Variable },
    Concept { variable: Variable },
}

impl Pretty for DeletableKind {}

impl fmt::Display for DeletableKind {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Has { attribute, owner } => {
                write!(f, "{} {} {} {}", token::Keyword::Has, attribute, token::Keyword::Of, owner)
            }
            Self::Links { players, relation } => {
                write!(f, "{} {} {} {}", token::Keyword::Links, players, token::Keyword::Of, relation)
            }
            Self::Concept { variable } => write!(f, "{}", variable),
        }
    }
}
