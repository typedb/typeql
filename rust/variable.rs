/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{identifier::Identifier, Span, Spanned},
    pretty::Pretty,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Variable {
    Anonymous(Option<Span>),
    Named(Option<Span>, Identifier),
}

impl Variable {
    pub fn name(&self) -> Option<&str> {
        match self {
            Self::Anonymous(_) => None,
            Self::Named(_, ident) => Some(ident.as_str()),
        }
    }
}

impl Pretty for Variable {}

impl fmt::Display for Variable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Variable::Anonymous(_) => write!(f, "$_"),
            Variable::Named(_, ident) => write!(f, "${ident}"),
        }
    }
}

impl Spanned for Variable {
    fn span(&self) -> Option<Span> {
        match self {
            Variable::Anonymous(span) | Variable::Named(span, _) => *span,
        }
    }
}
