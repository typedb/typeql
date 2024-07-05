/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use crate::{
    common::{token, Span},
    pretty::{indent, Pretty},
    statement::Statement,
};

#[derive(Debug, Eq, PartialEq)]
pub struct Update {
    span: Option<Span>,
    statements: Vec<Statement>,
}

impl Update {
    pub(crate) fn new(span: Option<Span>, statements: Vec<Statement>) -> Self {
        Self { span, statements }
    }
}

impl Pretty for Update {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", token::Clause::Update)?;
        for statement in &self.statements {
            writeln!(f)?;
            indent(indent_level, f)?;
            Pretty::fmt(statement, indent_level, f)?;
            f.write_char(';')?;
        }
        Ok(())
    }
}

impl fmt::Display for Update {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if f.alternate() {
            Pretty::fmt(self, 0, f)
        } else {
            write!(f, "{}", token::Clause::Update)?;
            for statement in &self.statements {
                write!(f, " {statement};")?;
            }
            Ok(())
        }
    }
}
