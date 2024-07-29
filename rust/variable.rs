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
pub struct Optional;

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Variable {
    Anonymous { span: Option<Span>, optional: Option<Optional> },
    Named { span: Option<Span>, ident: Identifier, optional: Option<Optional> },
}

impl Variable {
    pub fn name(&self) -> Option<&str> {
        match self {
            Self::Anonymous { .. } => None,
            Self::Named { ident, .. } => Some(ident.as_str()),
        }
    }
}

impl Pretty for Variable {}

impl fmt::Display for Variable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Variable::Anonymous { optional: None, .. } => write!(f, "$_"),
            Variable::Anonymous { optional: Some(Optional), .. } => write!(f, "$_?"),
            Variable::Named { ident, optional: None, .. } => write!(f, "${ident}"),
            Variable::Named { ident, optional: Some(Optional), .. } => write!(f, "${ident}?"),
        }
    }
}

impl Spanned for Variable {
    fn span(&self) -> Option<Span> {
        match *self {
            Self::Anonymous { span, .. } | Self::Named { span, .. } => span,
        }
    }
}
