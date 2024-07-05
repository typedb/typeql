/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{
    common::{error::TypeQLError, Spanned},
    parser::{
        annotation::visit_annotations,
        statement::{visit_type_ref, visit_type_ref_any, visit_type_ref_list, visit_type_ref_scoped},
        visit_label, visit_label_scoped, visit_value_type, IntoChildNodes, Node, Rule, RuleMatcher,
    },
    statement::{
        type_::{Constraint, LabelConstraint, Owns, Plays, Relates, Sub, SubKind, TypeConstraintBase, ValueType},
        Statement, Type,
    },
    type_::TypeAny,
};

pub(super) fn visit_statement_type(node: Node<'_>) -> Statement {
    debug_assert_eq!(node.as_rule(), Rule::statement_type);
    let span = node.span();
    let mut children = node.into_children();
    let type_ = visit_type_ref_any(children.consume_expected(Rule::type_ref_any));
    let constraints = children.map(visit_type_constraint).collect();
    Statement::Type(Type::new(span, type_, constraints))
}

fn visit_type_constraint(node: Node<'_>) -> Constraint {
    debug_assert_eq!(node.as_rule(), Rule::type_constraint);
    let span = node.span();
    let mut children = node.into_children();
    let base = visit_type_constraint_base(children.consume_expected(Rule::type_constraint_base));
    let annotations = children.try_consume_expected(Rule::annotations).map(visit_annotations).unwrap_or_default();
    debug_assert_eq!(children.try_consume_any(), None);
    Constraint::new(span, base, annotations)
}

fn visit_type_constraint_base(node: Node<'_>) -> TypeConstraintBase {
    debug_assert_eq!(node.as_rule(), Rule::type_constraint_base);
    let child = node.into_child();
    match child.as_rule() {
        Rule::sub_constraint => TypeConstraintBase::Sub(visit_sub_constraint(child)),
        Rule::value_type_constraint => TypeConstraintBase::ValueType(visit_value_type_constraint(child)),
        Rule::label_constraint => TypeConstraintBase::Label(visit_label_constraint(child)),
        Rule::owns_constraint => TypeConstraintBase::Owns(visit_owns_constraint(child)),
        Rule::relates_constraint => TypeConstraintBase::Relates(visit_relates_constraint(child)),
        Rule::plays_constraint => TypeConstraintBase::Plays(visit_plays_constraint(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_sub_constraint(node: Node<'_>) -> Sub {
    debug_assert_eq!(node.as_rule(), Rule::sub_constraint);
    let span = node.span();
    let mut children = node.into_children();
    let kind = visit_sub_token(children.consume_expected(Rule::SUB_));
    let supertype = visit_type_ref_any(children.consume_expected(Rule::type_ref_any));
    debug_assert_eq!(children.try_consume_any(), None);
    Sub::new(span, kind, supertype)
}

fn visit_sub_token(node: Node<'_>) -> SubKind {
    debug_assert_eq!(node.as_rule(), Rule::SUB_);
    let child = node.into_child();
    match child.as_rule() {
        Rule::SUB => SubKind::Transitive,
        Rule::SUBX => SubKind::Direct,
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_value_type_constraint(node: Node<'_>) -> ValueType {
    debug_assert_eq!(node.as_rule(), Rule::value_type_constraint);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::VALUE);
    let value_type = visit_value_type(children.consume_expected(Rule::value_type));
    debug_assert_eq!(children.try_consume_any(), None);
    ValueType::new(span, value_type)
}

fn visit_label_constraint(node: Node<'_>) -> LabelConstraint {
    debug_assert_eq!(node.as_rule(), Rule::label_constraint);
    let mut children = node.into_children();
    children.skip_expected(Rule::LABEL);
    let label = children.consume_any();
    let label = match label.as_rule() {
        Rule::label => LabelConstraint::Name(visit_label(label)),
        Rule::label_scoped => LabelConstraint::Scoped(visit_label_scoped(label)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: label.to_string() }),
    };
    debug_assert_eq!(children.try_consume_any(), None);
    label
}

fn visit_owns_constraint(node: Node<'_>) -> Owns {
    debug_assert_eq!(node.as_rule(), Rule::owns_constraint);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::OWNS);

    let child = children.consume_any();
    let owned = match child.as_rule() {
        Rule::type_ref => TypeAny::Type(visit_type_ref(child)),
        Rule::type_ref_list => visit_type_ref_list(child),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };

    let overridden = if children.try_consume_expected(Rule::AS).is_some() {
        Some(visit_type_ref(children.consume_expected(Rule::type_ref)))
    } else {
        None
    };

    debug_assert_eq!(children.try_consume_any(), None);
    Owns::new(span, owned, overridden)
}

fn visit_relates_constraint(node: Node<'_>) -> Relates {
    debug_assert_eq!(node.as_rule(), Rule::relates_constraint);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::RELATES);

    let child = children.consume_any();
    let related = match child.as_rule() {
        Rule::type_ref => TypeAny::Type(visit_type_ref(child)),
        Rule::type_ref_list => visit_type_ref_list(child),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };

    let overridden = if children.try_consume_expected(Rule::AS).is_some() {
        Some(visit_type_ref(children.consume_expected(Rule::type_ref)))
    } else {
        None
    };

    debug_assert_eq!(children.try_consume_any(), None);
    Relates::new(span, related, overridden)
}

fn visit_plays_constraint(node: Node<'_>) -> Plays {
    debug_assert_eq!(node.as_rule(), Rule::plays_constraint);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::PLAYS);

    let role = visit_type_ref_scoped(children.consume_expected(Rule::type_ref_scoped));
    let overridden = if children.try_consume_expected(Rule::AS).is_some() {
        Some(visit_type_ref(children.consume_expected(Rule::type_ref)))
    } else {
        None
    };

    debug_assert_eq!(children.try_consume_any(), None);
    Plays::new(span, role, overridden)
}
