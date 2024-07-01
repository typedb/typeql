/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use crate::{
    common::{token, Span, Spanned},
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

impl fmt::Display for Undefine {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        token::Clause::Undefine.fmt(f)?;
        if f.alternate() {
            f.write_char('\n')?;
        } else {
            f.write_char(' ')?;
        }
        let delimiter = if f.alternate() { ";\n" } else { "; " };
        // write_joined!(f, delimiter, self.undefinables)?;
        f.write_str(";")
    }
}
