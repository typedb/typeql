/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::identifier::{Identifier, Label, Variable};

#[macro_export]
macro_rules! define {
    ($($def:expr),* $(,)?) => {
        $crate::query::Define::build(vec![$($def.into()),*])
    }
}

#[macro_export]
macro_rules! undefine {
    ($($def:expr),* $(,)?) => {
        $crate::query::Undefine::build(vec![$($def.into()),*])
    }
}

#[macro_export]
macro_rules! match_ {
    ($($pattern:expr),* $(,)?) => {
        $crate::query::data::stage::Match::build(vec![$($pattern.into()),*])
    }
}

pub fn var(name: impl Into<Identifier>) -> Variable {
    Variable::Named(None, name.into())
}

pub fn type_(name: impl Into<Identifier>) -> Label {
    Label::Identifier(name.into())
}
