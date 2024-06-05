/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::variable::Variable;

pub enum Statement {
    Assignment,
    Comparison,
    Is,
    MultiThing,
    MultiType,
}

pub struct Assignment {

}

pub struct Comparison {

}

pub struct Is {
    left: Variable,
    right: Variable,
}

impl Is {
    pub(crate) fn new(left: Variable, right: Variable) -> Is {
        Self { left, right }
    }
}

pub struct MultiThing {

}

pub struct MultiType {

}