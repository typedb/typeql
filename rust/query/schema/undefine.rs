/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use crate::{
    common::{token, Span, Spanned},
    pretty::{indent, Pretty},
    schema::undefinable::Undefinable,
};

#[derive(Debug, Eq, PartialEq)]
pub struct Undefine {
    span: Option<Span>,
    undefinables: Vec<Undefinable>,
}

impl Undefine {
    pub(crate) fn new(span: Option<Span>, undefinables: Vec<Undefinable>) -> Self {
        Self { span, undefinables }
    }

    pub fn build(definables: Vec<Undefinable>) -> Self {
        Self::new(None, definables)
    }
}

impl Spanned for Undefine {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Undefine {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        indent(indent_level, f)?;
        write!(f, "{}", token::Clause::Undefine)?;
        for undefinable in &self.undefinables {
            writeln!(f);
            Pretty::fmt(undefinable, indent_level + 1, f);
            f.write_char(';')?;
        }
        Ok(())
    }
}

impl fmt::Display for Undefine {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if f.alternate() {
            Pretty::fmt(self, 0, f)
        } else {
            token::Clause::Undefine.fmt(f)?;
            for undefinable in &self.undefinables {
                write!(f, " {};", undefinable)?;
            }
            Ok(())
        }
    }
}
