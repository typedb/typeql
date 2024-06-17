/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::{IntoChildNodes, Node, Rule};
use crate::{
    common::{error::TypeQLError, Spanned},
    parser::RuleMatcher,
    pattern::{Conjunction, Disjunction, Negation, Pattern, Statement, Try},
    query::DataQuery,
};

pub(super) fn visit_statement(node: Node<'_>) -> Statement {
    Statement
}

pub(super) fn visit_statement_things(_node: Node<'_>) {
    todo!()
}
