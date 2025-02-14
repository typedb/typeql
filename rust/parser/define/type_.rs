/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{
    common::{error::TypeQLError, Spanned},
    parser::{
        annotation::visit_annotations,
        type_::{visit_label, visit_label_list, visit_label_scoped, visit_value_type},
        visit_kind, IntoChildNodes, Node, Rule, RuleMatcher,
    },
    schema::definable::type_::{
        capability::{Alias, Owns, Plays, Relates, Sub, ValueType},
        Capability, CapabilityBase, Type,
    },
    type_::{NamedType, TypeRef, TypeRefAny},
};

pub(super) fn visit_definition_type(node: Node<'_>) -> Type {
    debug_assert_eq!(node.as_rule(), Rule::definition_type);
    let span = node.span();
    let mut children = node.into_children();
    let kind = children.try_consume_expected(Rule::kind).map(visit_kind);
    let label = visit_label(children.consume_expected(Rule::label));
    let annotations = children.try_consume_expected(Rule::annotations).map(visit_annotations).unwrap_or_default();
    let traits = children.map(visit_type_capability).collect();
    Type::new(span, kind, label, annotations, traits)
}

pub(in crate::parser) fn visit_type_capability(node: Node<'_>) -> Capability {
    debug_assert_eq!(node.as_rule(), Rule::type_capability);
    let span = node.span();
    let mut children = node.into_children();
    let base = visit_type_capability_base(children.consume_expected(Rule::type_capability_base));
    let annotations = children.try_consume_expected(Rule::annotations).map(visit_annotations).unwrap_or_default();
    debug_assert_eq!(children.try_consume_any(), None);
    Capability::new(span, base, annotations)
}

pub(in crate::parser) fn visit_type_capability_base(node: Node<'_>) -> CapabilityBase {
    debug_assert_eq!(node.as_rule(), Rule::type_capability_base);
    let child = node.into_child();
    match child.as_rule() {
        Rule::sub_declaration => CapabilityBase::Sub(visit_sub_declaration(child)),
        Rule::alias_declaration => CapabilityBase::Alias(visit_alias_declaration(child)),
        Rule::value_type_declaration => CapabilityBase::ValueType(visit_value_type_declaration(child)),
        Rule::owns_declaration => CapabilityBase::Owns(visit_owns_declaration(child)),
        Rule::relates_declaration => CapabilityBase::Relates(visit_relates_declaration(child)),
        Rule::plays_declaration => CapabilityBase::Plays(visit_plays_declaration(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_sub_declaration(node: Node<'_>) -> Sub {
    debug_assert_eq!(node.as_rule(), Rule::sub_declaration);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::SUB);
    let supertype_label = visit_label(children.consume_expected(Rule::label));
    debug_assert_eq!(children.try_consume_any(), None);
    Sub::new(span, supertype_label)
}

fn visit_alias_declaration(node: Node<'_>) -> Alias {
    debug_assert_eq!(node.as_rule(), Rule::alias_declaration);
    let span = node.span();
    let aliases = node.into_children().skip_expected(Rule::ALIAS).map(visit_label).collect();
    Alias::new(span, aliases)
}

fn visit_value_type_declaration(node: Node<'_>) -> ValueType {
    debug_assert_eq!(node.as_rule(), Rule::value_type_declaration);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::VALUE);
    let value_type = visit_value_type(children.consume_expected(Rule::value_type));
    debug_assert_eq!(children.try_consume_any(), None);
    ValueType::new(span, value_type)
}

fn visit_owns_declaration(node: Node<'_>) -> Owns {
    debug_assert_eq!(node.as_rule(), Rule::owns_declaration);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::OWNS);

    let owned_label = children.consume_any();
    let owned = match owned_label.as_rule() {
        Rule::label_list => TypeRefAny::List(visit_label_list(owned_label)),
        Rule::label => TypeRefAny::Type(TypeRef::Label(visit_label(owned_label))),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: owned_label.to_string() }),
    };

    debug_assert_eq!(children.try_consume_any(), None);
    Owns::new(span, owned)
}

pub(in crate::parser) fn visit_relates_declaration(node: Node<'_>) -> Relates {
    debug_assert_eq!(node.as_rule(), Rule::relates_declaration);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::RELATES);

    let related_label = children.consume_any();
    let related = match related_label.as_rule() {
        Rule::label_list => TypeRefAny::List(visit_label_list(related_label)),
        Rule::label => TypeRefAny::Type(TypeRef::Label(visit_label(related_label))),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: related_label.to_string() }),
    };
    let specialised = if children.try_consume_expected(Rule::AS).is_some() {
        Some(visit_label(children.consume_expected(Rule::label)))
    } else {
        None
    };
    debug_assert_eq!(children.try_consume_any(), None);
    Relates::new(span, related, specialised)
}

fn visit_plays_declaration(node: Node<'_>) -> Plays {
    debug_assert_eq!(node.as_rule(), Rule::plays_declaration);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::PLAYS);

    let role = visit_label_scoped(children.consume_expected(Rule::label_scoped));

    debug_assert_eq!(children.try_consume_any(), None);
    Plays::new(span, role)
}
