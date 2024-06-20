/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{
    common::{error::TypeQLError, Spanned},
    parser::{
        schema::{visit_annotations_owns, visit_annotations_relates, visit_annotations_sub, visit_annotations_value},
        statement::{visit_type_ref, visit_type_ref_any, visit_type_ref_list, visit_type_ref_scoped},
        visit_label_any, visit_value_type_primitive, IntoChildNodes, Node, Rule, RuleMatcher,
    },
    pattern::{
        statement::{
            type_::{Owned, Owns, Played, Plays, Related, Relates, Sub, SubKind, ValueType},
            Statement, TypeConstraint, TypeStatement,
        },
        Label,
    },
};

pub(super) fn visit_statement_type(node: Node<'_>) -> Statement {
    debug_assert_eq!(node.as_rule(), Rule::statement_type);
    let span = node.span();
    let mut children = node.into_children();
    let type_ = visit_type_ref_any(children.consume_expected(Rule::type_ref_any));
    let constraints = children.map(visit_type_constraint).collect();
    Statement::Type(TypeStatement::new(span, type_, constraints))
}

fn visit_type_constraint(node: Node<'_>) -> TypeConstraint {
    debug_assert_eq!(node.as_rule(), Rule::type_constraint);
    let child = node.into_child();
    match child.as_rule() {
        Rule::sub_constraint => TypeConstraint::Sub(visit_sub_constraint(child)),
        Rule::value_type_constraint => TypeConstraint::ValueType(visit_value_type_constraint(child)),
        Rule::label_constraint => TypeConstraint::Label(visit_label_constraint(child)),
        Rule::owns_constraint => TypeConstraint::Owns(visit_owns_constraint(child)),
        Rule::relates_constraint => TypeConstraint::Relates(visit_relates_constraint(child)),
        Rule::plays_constraint => TypeConstraint::Plays(visit_plays_constraint(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_sub_constraint(node: Node<'_>) -> Sub {
    debug_assert_eq!(node.as_rule(), Rule::sub_constraint);
    let span = node.span();
    let mut children = node.into_children();
    let kind = visit_sub_token(children.consume_expected(Rule::SUB_));
    let supertype = visit_type_ref_any(children.consume_expected(Rule::type_ref_any));
    let annotations = visit_annotations_sub(children.consume_expected(Rule::annotations_sub));
    Sub::new(kind, supertype, annotations, span)
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
    let value_type = visit_value_type_primitive(children.consume_expected(Rule::value_type_primitive));
    let annotations = visit_annotations_value(children.consume_expected(Rule::annotations_value));
    debug_assert!(children.try_consume_any().is_none());
    ValueType::new(value_type, annotations, span)
}

fn visit_label_constraint(node: Node<'_>) -> Label {
    debug_assert_eq!(node.as_rule(), Rule::label_constraint);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::TYPE);
    let label = visit_label_any(children.consume_expected(Rule::label_any));
    debug_assert!(children.try_consume_any().is_none());
    label
}

fn visit_owns_constraint(node: Node<'_>) -> Owns {
    debug_assert_eq!(node.as_rule(), Rule::owns_constraint);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::OWNS);

    let owned_type = children.consume_any();
    let owned = match owned_type.as_rule() {
        Rule::type_ref_list => Owned::List(visit_type_ref_list(owned_type)),
        Rule::type_ref => {
            let type_ = visit_type_ref(owned_type);
            match children.try_consume_expected(Rule::AS) {
                None => Owned::Attribute(type_, None),
                Some(_) => Owned::Attribute(type_, Some(visit_type_ref(children.consume_expected(Rule::type_ref)))),
            }
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: owned_type.to_string() }),
    };

    let annotations_owns = visit_annotations_owns(children.consume_expected(Rule::annotations_owns));
    debug_assert!(children.try_consume_any().is_none());
    Owns::new(owned, annotations_owns, span)
}

fn visit_relates_constraint(node: Node<'_>) -> Relates {
    debug_assert_eq!(node.as_rule(), Rule::relates_constraint);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::RELATES);

    let related_role = children.consume_any();
    let related = match related_role.as_rule() {
        Rule::type_ref_list => Related::List(visit_type_ref_list(related_role)),
        Rule::type_ref => {
            let type_ = visit_type_ref(related_role);
            match children.try_consume_expected(Rule::AS) {
                None => Related::Role(type_, None),
                Some(_) => Related::Role(type_, Some(visit_type_ref(children.consume_expected(Rule::type_ref)))),
            }
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: related_role.to_string() }),
    };

    let annotations_relates = visit_annotations_relates(children.consume_expected(Rule::annotations_relates));
    debug_assert!(children.try_consume_any().is_none());
    Relates::new(related, annotations_relates, span)
}

fn visit_plays_constraint(node: Node<'_>) -> Plays {
    debug_assert_eq!(node.as_rule(), Rule::plays_constraint);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::PLAYS);

    let role_type = visit_type_ref_scoped(children.consume_expected(Rule::type_ref_scoped));
    let played = match children.try_consume_expected(Rule::AS) {
        None => Played::new(role_type, None),
        Some(_) => Played::new(role_type, Some(visit_type_ref(children.consume_expected(Rule::type_ref)))),
    };

    debug_assert!(children.try_consume_any().is_none());
    Plays::new(played, span)
}

