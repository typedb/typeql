/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use self::declaration::{Owns, Plays, Relates, Sub, ValueType};
use crate::{
    common::{Span, Spanned},
    pattern::Label,
    write_joined,
};

pub mod declaration;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Type {
    span: Option<Span>,
    pub label: Label,
    pub sub: Option<Sub>,
    pub value_type: Option<ValueType>,
    pub owns: Vec<Owns>,
    pub relates: Vec<Relates>,
    pub plays: Vec<Plays>,
}

impl Type {
    pub(crate) fn new(label: Label, span: Option<Span>) -> Self {
        Self { span, label, sub: None, value_type: None, owns: Vec::new(), relates: Vec::new(), plays: Vec::new() }
    }

    pub fn set_sub(self, sub: Sub) -> Self {
        Self { sub: Some(sub), ..self }
    }

    pub fn set_value_type(self, value_type: ValueType) -> Self {
        Self { value_type: Some(value_type), ..self }
    }

    pub fn set_owns(self, owns: Vec<Owns>) -> Self {
        Self { owns, ..self }
    }

    pub fn add_owns(mut self, owns: Owns) -> Self {
        self.owns.push(owns);
        self
    }

    pub fn set_relates(self, relates: Vec<Relates>) -> Self {
        Self { relates, ..self }
    }

    pub fn add_relates(mut self, relates: Relates) -> Self {
        self.relates.push(relates);
        self
    }

    pub fn set_plays(self, plays: Vec<Plays>) -> Self {
        Self { plays, ..self }
    }

    pub fn add_plays(mut self, plays: Plays) -> Self {
        self.plays.push(plays);
        self
    }
}

impl fmt::Display for Type {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        fmt::Display::fmt(&self.label, f)?;
        f.write_char(' ')?;
        let joiner = if f.alternate() { ",\n    " } else { ", " };
        write_joined!(f, joiner, &self.sub, &self.value_type, &self.relates, &self.plays, &self.owns)?;
        Ok(())
    }
}

impl Spanned for Type {
    fn span(&self) -> Option<Span> {
        self.span
    }
}
