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
pub struct TypeQLDefine {
    definables: Vec<Definable>,
    span: Option<Span>,
}

impl TypeQLDefine {
    pub fn new(definables: Vec<Definable>, span: Option<Span>) -> Self {
        Self { definables, span }
    }
}

impl Spanned for TypeQLDefine {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl fmt::Display for TypeQLDefine {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        token::Clause::Define.fmt(f)?;
        if f.alternate() {
            f.write_char('\n')?;
            f.write_str("    ")?;
        } else {
            f.write_char(' ')?;
        }
        let delimiter = if f.alternate() { ";\n" } else { "; " };
        write_joined!(f, delimiter, self.definables)?;
        f.write_str(";")
    }
}
