/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use crate::{
    common::{token, Span, Spanned},
    pattern::Definable,
    write_joined,
};

#[derive(Debug, Eq, PartialEq)]
pub struct TypeQLUndefine {
    definables: Vec<Definable>,
    span: Option<Span>,
}

impl TypeQLUndefine {
    pub(crate) fn new(definables: Vec<Definable>, span: Option<Span>) -> Self {
        Self { definables, span }
    }

    pub fn build(definables: Vec<Definable>) -> Self {
        Self::new(definables, None)
    }
}

impl Spanned for TypeQLUndefine {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl fmt::Display for TypeQLUndefine {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        token::Clause::Undefine.fmt(f)?;
        if f.alternate() {
            f.write_char('\n')?;
        } else {
            f.write_char(' ')?;
        }
        let delimiter = if f.alternate() { ";\n" } else { "; " };
        write_joined!(f, delimiter, self.definables)?;
        f.write_str(";")
    }
}
