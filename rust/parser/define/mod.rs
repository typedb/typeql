/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use self::{function::visit_definition_function, struct_::visit_definition_struct, type_::visit_definition_type};
use super::{IntoChildNodes, Node, Rule, RuleMatcher};
use crate::{
    common::{error::TypeQLError, Spanned},
    query::schema::Define,
    schema::definable::Definable,
};

pub(super) mod function;
pub(super) mod struct_;
pub(super) mod type_;

pub(super) fn visit_query_define(node: Node<'_>) -> Define {
    debug_assert_eq!(node.as_rule(), Rule::query_define);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::DEFINE);
    let definables = visit_definables(children.consume_expected(Rule::definables));
    debug_assert_eq!(children.try_consume_any(), None);
    Define::new(span, definables)
}

pub(super) fn visit_definables(node: Node<'_>) -> Vec<Definable> {
    debug_assert_eq!(node.as_rule(), Rule::definables);
    node.into_children().map(visit_definable).collect()
}

pub(super) fn visit_definable(node: Node<'_>) -> Definable {
    debug_assert_eq!(node.as_rule(), Rule::definable);
    let child = node.into_child();
    match child.as_rule() {
        Rule::definition_type => Definable::TypeDeclaration(visit_definition_type(child)),
        Rule::definition_function => Definable::Function(visit_definition_function(child)),
        Rule::definition_struct => Definable::Struct(visit_definition_struct(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}
