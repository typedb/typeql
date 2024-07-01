/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{token, Span, Spanned},
    pretty::{indent, Pretty},
    schema::definable::Definable,
};

#[derive(Debug, Eq, PartialEq)]
pub struct Define {
    span: Option<Span>,
    definables: Vec<Definable>,
}

impl Define {
    pub(crate) fn new(span: Option<Span>, definables: Vec<Definable>) -> Self {
        Self { span, definables }
    }

    pub fn build(definables: Vec<Definable>) -> Self {
        Self::new(None, definables)
    }
}

impl Spanned for Define {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Define {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        indent(indent_level, f)?;
        write!(f, "{}\n", token::Clause::Define)?;
        for definable in &self.definables {
            f.write_str("\n")?;
            Pretty::fmt(definable, indent_level + 1, f);
            f.write_str(";")?;
        }
        Ok(())
    }
}

impl fmt::Display for Define {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if f.alternate() {
            return Pretty::fmt(self, 0, f);
        }

        write!(f, "{}", token::Clause::Define)?;
        for definable in &self.definables {
            write!(f, " {};", definable)?;
        }
        Ok(())
    }
}
