/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{
    common::{error::TypeQLError, Spanned},
    definition::type_::{
        declaration::{Owned, Owns, Played, Plays, Related, Relates, Sub, ValueType},
        Type, TypeCapability,
    },
    parser::{
        annotation::{
            visit_annotations_owns, visit_annotations_relates, visit_annotations_sub, visit_annotations_value,
        },
        visit_identifier, visit_label, visit_label_scoped, visit_list_label, visit_value_type_primitive,
        IntoChildNodes, Node, Rule, RuleMatcher,
    },
};

pub(super) fn visit_definition_type(node: Node<'_>) -> Type {
    debug_assert_eq!(node.as_rule(), Rule::definition_type);
    let span = node.span();
    let mut children = node.into_children();
    let ident = visit_identifier(children.consume_expected(Rule::identifier));
    let traits = children.map(visit_type_constraint_declaration).collect();
    Type::new(span, ident, traits)
}

fn visit_type_constraint_declaration(node: Node<'_>) -> TypeCapability {
    debug_assert_eq!(node.as_rule(), Rule::type_capability_declaration);

    let child = node.into_child();
    match child.as_rule() {
        Rule::sub_declaration => TypeCapability::Sub(visit_sub_declaration(child)),
        Rule::value_type_declaration => TypeCapability::ValueType(visit_value_type_declaration(child)),
        Rule::owns_declaration => TypeCapability::Owns(visit_owns_declaration(child)),
        Rule::relates_declaration => TypeCapability::Relates(visit_relates_declaration(child)),
        Rule::plays_declaration => TypeCapability::Plays(visit_plays_declaration(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_plays_declaration(node: Node<'_>) -> Plays {
    debug_assert_eq!(node.as_rule(), Rule::plays_declaration);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::PLAYS);

    let label = visit_label_scoped(children.consume_expected(Rule::label_scoped));
    let played = match children.try_consume_expected(Rule::AS) {
        None => Played::new(label, None),
        Some(_) => Played::new(label, Some(visit_label(children.consume_expected(Rule::label)))),
    };

    debug_assert_eq!(children.try_consume_any(), None);
    Plays::new(played, span)
}

fn visit_owns_declaration(node: Node<'_>) -> Owns {
    debug_assert_eq!(node.as_rule(), Rule::owns_declaration);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::OWNS);

    let owned_label = children.consume_any();
    let owned = match owned_label.as_rule() {
        Rule::list_label => Owned::List(visit_list_label(owned_label)),
        Rule::label => {
            let label = visit_label(owned_label);
            match children.try_consume_expected(Rule::AS) {
                None => Owned::Attribute(label, None),
                Some(_) => Owned::Attribute(label, Some(visit_label(children.consume_expected(Rule::label)))),
            }
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: owned_label.to_string() }),
    };

    let annotations_owns = visit_annotations_owns(children.consume_expected(Rule::annotations_owns));
    debug_assert_eq!(children.try_consume_any(), None);
    Owns::new(span, owned, annotations_owns)
}

fn visit_relates_declaration(node: Node<'_>) -> Relates {
    debug_assert_eq!(node.as_rule(), Rule::relates_declaration);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::RELATES);

    let related_label = children.consume_any();
    let related = match related_label.as_rule() {
        Rule::list_label => Related::List(visit_list_label(related_label)),
        Rule::label => {
            let label = visit_label(related_label);
            match children.try_consume_expected(Rule::AS) {
                None => Related::Role(label, None),
                Some(_) => Related::Role(label, Some(visit_label(children.consume_expected(Rule::label)))),
            }
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: related_label.to_string() }),
    };

    let annotations_relates = visit_annotations_relates(children.consume_expected(Rule::annotations_relates));
    debug_assert_eq!(children.try_consume_any(), None);
    Relates::new(related, annotations_relates, span)
}

fn visit_value_type_declaration(node: Node<'_>) -> ValueType {
    debug_assert_eq!(node.as_rule(), Rule::value_type_declaration);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::VALUE);
    let value_type_node = children.consume_any();
    let (value_type, annotations_value_type) = match value_type_node.as_rule() {
        Rule::label => (crate::pattern::statement::Type::Label(visit_label(value_type_node)), Vec::new()),
        Rule::value_type_primitive => (
            crate::pattern::statement::Type::BuiltinValue(visit_value_type_primitive(value_type_node)),
            visit_annotations_value(children.consume_expected(Rule::annotations_value)),
        ),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: value_type_node.to_string() }),
    };
    debug_assert_eq!(children.try_consume_any(), None);
    ValueType::new(value_type, annotations_value_type, span)
}

fn visit_sub_declaration(node: Node<'_>) -> Sub {
    debug_assert_eq!(node.as_rule(), Rule::sub_declaration);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::SUB);
    let supertype_label = visit_label(children.consume_expected(Rule::label));
    let annotations_sub = visit_annotations_sub(children.consume_expected(Rule::annotations_sub));
    debug_assert_eq!(children.try_consume_any(), None);
    Sub::new(supertype_label, annotations_sub, span)
}
