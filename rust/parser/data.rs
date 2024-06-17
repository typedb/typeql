/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::{Node, Rule};
use crate::{parser::IntoChildNodes, query::DataQuery};

pub(super) fn visit_query_data(node: Node<'_>) -> DataQuery {
    debug_assert_eq!(node.as_rule(), Rule::query_data);
    node.into_children().fold(DataQuery::new(), |_query, stage| {
        debug_assert_eq!(stage.as_rule(), Rule::query_stage);
        todo!()
    })
}
