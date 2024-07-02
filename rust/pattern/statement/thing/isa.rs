/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{common::{token, Span}, pretty::Pretty, type_::Type};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Isa {
    span: Option<Span>,
    kind: IsaKind,
    type_: Type,
}

impl Isa {
    pub fn new(span: Option<Span>, kind: IsaKind, type_: Type) -> Self {
        Self { span, kind, type_ }
    }
}

impl Pretty for Isa {}

impl fmt::Display for Isa {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", self.kind, self.type_)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum IsaKind {
    Exact,
    Subtype,
}

impl Pretty for IsaKind {}

impl fmt::Display for IsaKind {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        let token = match self {
            Self::Exact => token::Keyword::IsaX,
            Self::Subtype => token::Keyword::Isa,
        };
        write!(f, "{}", token)
    }
}

