/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

pub(super) mod function;
mod struct_;
mod type_;

use super::{IntoChildNodes, Node, Rule, RuleMatcher};
use crate::{
    common::{error::TypeQLError, Spanned},
    definition::Definable,
    parser::schema::{
        function::visit_definition_function, struct_::visit_definition_struct, type_::visit_definition_type,
    },
    query::schema::{Define, SchemaQuery, Undefine},
};

pub(super) fn visit_query_schema(node: Node<'_>) -> SchemaQuery {
    debug_assert_eq!(node.as_rule(), Rule::query_schema);
    let mut children = node.into_children();
    let child = children.consume_any();
    let query = match child.as_rule() {
        Rule::query_define => SchemaQuery::Define(visit_query_define(child)),
        Rule::query_undefine => SchemaQuery::Undefine(visit_query_undefine(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };
    debug_assert_eq!(children.try_consume_any(), None);
    query
}

fn visit_query_define(node: Node<'_>) -> Define {
    debug_assert_eq!(node.as_rule(), Rule::query_define);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::DEFINE);
    let query = Define::new(visit_definables(children.consume_expected(Rule::definables)), span);
    debug_assert_eq!(children.try_consume_any(), None);
    query
}

fn visit_query_undefine(node: Node<'_>) -> Undefine {
    debug_assert_eq!(node.as_rule(), Rule::query_undefine);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::UNDEFINE);
    let query = Undefine::new(visit_definables(children.consume_expected(Rule::definables)), span);
    debug_assert_eq!(children.try_consume_any(), None);
    query
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
