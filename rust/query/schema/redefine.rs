/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use crate::{
    common::{token, Span, Spanned},
    schema::definable::Definable,
    util::write_joined,
};

#[derive(Debug, Eq, PartialEq)]
pub struct Redefine {
    pub span: Option<Span>,
    pub definables: Vec<Definable>,
}

impl Redefine {
    pub(crate) fn new(span: Option<Span>, definables: Vec<Definable>) -> Self {
        Self { span, definables }
    }

    pub fn build(definables: Vec<Definable>) -> Self {
        Self::new(None, definables)
    }
}

impl Spanned for Redefine {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl fmt::Display for Redefine {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", token::Clause::Redefine)?;
        if f.alternate() {
            f.write_char('\n')?;
        } else {
            f.write_char(' ')?;
        }
        let delimiter = if f.alternate() { ";\n" } else { "; " };
        write_joined!(f, delimiter, self.definables)
    }
}
