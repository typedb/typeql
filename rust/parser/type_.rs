/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::{visit_identifier, visit_var, IntoChildNodes, Node, Rule, RuleMatcher};
use crate::{
    common::{error::TypeQLError, token, Spanned},
    type_::{BuiltinValueType, Label, List, NamedType, Optional, TypeRef},
    ScopedLabel, TypeRefAny,
};

pub(super) fn visit_type_ref_any(node: Node<'_>) -> TypeRefAny {
    debug_assert_eq!(node.as_rule(), Rule::type_ref_any);
    let child = node.into_child();
    match child.as_rule() {
        Rule::type_ref => TypeRefAny::Type(visit_type_ref(child)),
        Rule::type_ref_optional => TypeRefAny::Optional(visit_type_ref_optional(child)),
        Rule::type_ref_list => TypeRefAny::List(visit_type_ref_list(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

pub(super) fn visit_type_ref(node: Node<'_>) -> TypeRef {
    debug_assert_eq!(node.as_rule(), Rule::type_ref);
    let child = node.into_child();
    match child.as_rule() {
        Rule::var => TypeRef::Variable(visit_var(child)),
        Rule::named_type => TypeRef::Named(visit_named_type(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

pub(super) fn visit_type_ref_optional(node: Node<'_>) -> Optional {
    debug_assert_eq!(node.as_rule(), Rule::type_ref_optional);
    let span = node.span();
    let inner = visit_type_ref(node.into_child());
    Optional::new(span, inner)
}

pub(super) fn visit_type_ref_list(node: Node<'_>) -> List {
    debug_assert_eq!(node.as_rule(), Rule::type_ref_list);
    let span = node.span();
    let inner = visit_type_ref(node.into_child());
    List::new(span, inner)
}

pub(super) fn visit_named_type_any(node: Node<'_>) -> TypeRefAny {
    debug_assert_eq!(node.as_rule(), Rule::named_type_any);
    let child = node.into_child();
    match child.as_rule() {
        Rule::named_type => TypeRefAny::Type(TypeRef::Named(visit_named_type(child))),
        Rule::named_type_optional => TypeRefAny::Optional(visit_named_type_optional(child)),
        Rule::named_type_list => TypeRefAny::List(visit_named_type_list(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

pub(super) fn visit_named_type(node: Node<'_>) -> NamedType {
    debug_assert_eq!(node.as_rule(), Rule::named_type);
    let child = node.into_child();
    match child.as_rule() {
        Rule::label => NamedType::Label(visit_label(child)),
        Rule::label_scoped => NamedType::Role(visit_label_scoped(child)),
        Rule::value_type_primitive => NamedType::BuiltinValueType(visit_value_type_primitive(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

pub(super) fn visit_named_type_optional(node: Node<'_>) -> Optional {
    debug_assert_eq!(node.as_rule(), Rule::named_type_optional);
    let span = node.span();
    let inner = TypeRef::Named(visit_named_type(node.into_child()));
    Optional::new(span, inner)
}

pub(super) fn visit_named_type_list(node: Node<'_>) -> List {
    debug_assert_eq!(node.as_rule(), Rule::named_type_list);
    let span = node.span();
    let inner = TypeRef::Named(visit_named_type(node.into_child()));
    List::new(span, inner)
}

pub(super) fn visit_label(node: Node<'_>) -> Label {
    debug_assert_eq!(node.as_rule(), Rule::label);
    let span = node.span();
    let ident = visit_identifier(node.into_child());
    Label::new(span, ident)
}

pub(super) fn visit_label_list(node: Node<'_>) -> List {
    debug_assert_eq!(node.as_rule(), Rule::label_list);
    let span = node.span();
    let inner = TypeRef::Named(NamedType::Label(visit_label(node.into_child())));
    List::new(span, inner)
}

pub(super) fn visit_label_scoped(node: Node<'_>) -> ScopedLabel {
    debug_assert_eq!(node.as_rule(), Rule::label_scoped);
    let span = node.span();
    let mut children = node.into_children();
    let scope = visit_label(children.consume_expected(Rule::label));
    let name = visit_label(children.consume_expected(Rule::label));
    debug_assert_eq!(children.try_consume_any(), None);
    ScopedLabel::new(span, scope, name)
}

pub(super) fn visit_value_type(node: Node<'_>) -> NamedType {
    debug_assert_eq!(node.as_rule(), Rule::value_type);
    let child = node.into_child();
    match child.as_rule() {
        Rule::value_type_primitive => NamedType::BuiltinValueType(visit_value_type_primitive(child)),
        Rule::label => NamedType::Label(visit_label(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

pub(super) fn visit_value_type_optional(node: Node<'_>) -> Optional {
    debug_assert_eq!(node.as_rule(), Rule::value_type_optional);
    let span = node.span();
    let inner = visit_value_type(node.into_child());
    Optional::new(span, TypeRef::Named(inner))
}

pub(super) fn visit_value_type_primitive(node: Node<'_>) -> BuiltinValueType {
    debug_assert_eq!(node.as_rule(), Rule::value_type_primitive);
    let span = node.span();
    let child = node.into_child();
    let token = match child.as_rule() {
        Rule::BOOLEAN => token::ValueType::Boolean,
        Rule::DATE => token::ValueType::Date,
        Rule::DATETIME => token::ValueType::DateTime,
        Rule::DATETIME_TZ => token::ValueType::DateTimeTZ,
        Rule::DECIMAL => token::ValueType::Decimal,
        Rule::DOUBLE => token::ValueType::Double,
        Rule::DURATION => token::ValueType::Duration,
        Rule::INTEGER => token::ValueType::Integer,
        Rule::STRING => token::ValueType::String,
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };
    BuiltinValueType::new(span, token)
}
