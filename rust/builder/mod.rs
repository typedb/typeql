/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{common::identifier::Identifier, type_::Label, variable::Variable};

#[macro_export]
macro_rules! define {
    ($($def:expr),* $(,)?) => {
        $crate::query::schema::Define::build(vec![$($def.into()),*])
    }
}

#[macro_export]
macro_rules! undefine {
    ($($def:expr),* $(,)?) => {
        $crate::query::schema::Undefine::build(vec![$($def.into()),*])
    }
}

#[macro_export]
macro_rules! match_ {
    ($($pattern:expr),* $(,)?) => {
        $crate::query::pipeline::stage::Match::build(vec![$($pattern.into()),*])
    }
}

pub fn var(name: impl Into<Identifier>) -> Variable {
    Variable::Named { span: None, ident: name.into(), optional: None }
}

pub fn type_(name: impl Into<Identifier>) -> Label {
    Label::new(None, name.into())
}
