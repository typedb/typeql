/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::super::Type;
use crate::common::Span;

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum IsaKind {
    Exact,
    Subtype,
}

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
