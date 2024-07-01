/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::{IntoChildNodes, Node, Rule, RuleMatcher};
use crate::{common::Spanned, query::schema::Undefine};

pub(super) fn visit_query_undefine(node: Node<'_>) -> Undefine {
    debug_assert_eq!(node.as_rule(), Rule::query_undefine);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::UNDEFINE);
    todo!()
}
