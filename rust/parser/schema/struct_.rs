/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

#[allow(unused)]
use crate::{
    parser::{IntoChildNodes, Node, Rule, RuleMatcher},
    schema::{
        definable,
        definable::{Function, Struct},
    },
};

pub(super) fn visit_definition_struct(node: Node<'_>) -> Struct {
    todo!()
}
