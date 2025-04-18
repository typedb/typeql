/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{
    common::{error::TypeQLError, Spanned},
    parser::{
        type_::{visit_value_type, visit_value_type_optional},
        visit_identifier, IntoChildNodes, Node, Rule, RuleMatcher,
    },
    schema::definable::{struct_::Field, Struct},
    type_::NamedTypeAny,
};

pub(in crate::parser) fn visit_definition_struct(node: Node<'_>) -> Struct {
    debug_assert_eq!(node.as_rule(), Rule::definition_struct);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::STRUCT);
    let ident = visit_identifier(children.consume_expected(Rule::identifier));
    let fields = visit_definition_struct_fields(children.consume_expected(Rule::definition_struct_fields));
    debug_assert_eq!(children.try_consume_any(), None);
    Struct::new(span, ident, fields)
}

fn visit_definition_struct_fields(node: Node<'_>) -> Vec<Field> {
    debug_assert_eq!(node.as_rule(), Rule::definition_struct_fields);
    node.into_children().map(visit_definition_struct_field).collect()
}

fn visit_definition_struct_field(node: Node<'_>) -> Field {
    debug_assert_eq!(node.as_rule(), Rule::definition_struct_field);
    let span = node.span();
    let mut children = node.into_children();
    let key = visit_identifier(children.consume_expected(Rule::identifier));
    children.skip_expected(Rule::VALUE);
    let type_ = visit_struct_field_value_type(children.consume_expected(Rule::struct_field_value_type));
    debug_assert_eq!(children.try_consume_any(), None);
    Field::new(span, key, type_)
}

fn visit_struct_field_value_type(node: Node<'_>) -> NamedTypeAny {
    debug_assert_eq!(node.as_rule(), Rule::struct_field_value_type);
    let child = node.into_child();
    match child.as_rule() {
        Rule::value_type => NamedTypeAny::Simple(visit_value_type(child)),
        Rule::value_type_optional => NamedTypeAny::Optional(visit_value_type_optional(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}
