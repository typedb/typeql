/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::definition::Struct;
#[allow(unused)]
use crate::{
    common::{error::TypeQLError, Spanned},
    definition::{
        function::{
            Argument, Return, ReturnSingle, ReturnStatement, ReturnStream, Signature, Single, SingleOutput, Stream,
        },
        Function,
    },
    parser::{
        data::{visit_reduce, visit_stage_match, visit_stage_modifier},
        visit_identifier, visit_label, visit_list_label, visit_list_value_type_primitive, visit_value_type_primitive,
        visit_var, visit_vars, IntoChildNodes, Node, Rule, RuleMatcher,
    },
    pattern::statement::Type,
};

pub(super) fn visit_definition_struct(node: Node<'_>) -> Struct {
    todo!()
}
