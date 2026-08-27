/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{Span, Spanned},
    pretty::Pretty,
    schema::definable::function::Argument,
    token::Keyword,
    util::write_joined,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Given {
    pub span: Option<Span>,
    pub variables: Vec<Argument>,
}

impl Given {
    pub fn new(span: Option<Span>, variables: Vec<Argument>) -> Self {
        Self { span, variables }
    }
}

impl Spanned for Given {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Given {}

impl fmt::Display for Given {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", Keyword::Given)?;
        write_joined!(f, ", ", self.variables)?;
        write!(f, ";")?;
        Ok(())
    }
}
