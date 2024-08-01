/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::{
    define::type_::visit_type_capability, type_::visit_label, visit_kind, IntoChildNodes, Node, Rule, RuleMatcher,
};
use crate::{
    common::Spanned,
    query::schema::Redefine,
    schema::definable::{Definable, Type},
};
use crate::parser::annotation::visit_annotations;

pub(super) fn visit_query_redefine(node: Node<'_>) -> Redefine {
    debug_assert_eq!(node.as_rule(), Rule::query_redefine);
    let span = node.span();
    let redefinables = node.into_children().skip_expected(Rule::REDEFINE).map(visit_redefinable).collect();
    Redefine::new(span, redefinables)
}

fn visit_redefinable(node: Node<'_>) -> Definable {
    debug_assert_eq!(node.as_rule(), Rule::redefinable);
    let span = node.span();
    let mut children = node.into_children();
    let kind = children.try_consume_expected(Rule::kind).map(visit_kind);
    let label = visit_label(children.consume_expected(Rule::label));
    let annotations = children.try_consume_expected(Rule::annotations).map(visit_annotations).unwrap_or_default();
    let capabilities = children.try_consume_expected(Rule::type_capability)
        .map(|node| vec![visit_type_capability(node)]).unwrap_or_default();
    debug_assert_eq!(children.try_consume_any(), None);
    Definable::TypeDeclaration(Type::new(span, kind, label, annotations, capabilities))
}
