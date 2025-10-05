/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::{IntoChildNodes, Node, Rule, RuleMatcher};
use crate::{
    common::{error::TypeQLError, token, Spanned},
    parser::{
        define::type_::{visit_relates_declaration, visit_type_capability_base},
        type_::visit_label,
        visit_identifier,
    },
    query::schema::Undefine,
    schema::undefinable::{
        AnnotationCapability, AnnotationType, CapabilityType, Function, Specialise, Struct, Undefinable,
    },
};

pub(super) fn visit_query_undefine(node: Node<'_>) -> Undefine {
    debug_assert_eq!(node.as_rule(), Rule::query_undefine);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::UNDEFINE);
    let undefinables = visit_undefinables(children.consume_expected(Rule::undefinables));
    debug_assert_eq!(children.try_consume_any(), None);
    Undefine::new(span, undefinables)
}

fn visit_undefinables(node: Node<'_>) -> Vec<Undefinable> {
    debug_assert_eq!(node.as_rule(), Rule::undefinables);
    node.into_children().map(visit_undefinable).collect()
}

fn visit_undefinable(node: Node<'_>) -> Undefinable {
    debug_assert_eq!(node.as_rule(), Rule::undefinable);
    let child = node.into_child();
    match child.as_rule() {
        Rule::undefine_from => visit_undefine_from(child),
        Rule::undefine_struct => Undefinable::Struct(visit_undefine_struct(child)),
        Rule::undefine_function => Undefinable::Function(visit_undefine_function(child)),
        Rule::label => Undefinable::Type(visit_label(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.as_str().to_owned() }),
    }
}

fn visit_undefine_from(node: Node<'_>) -> Undefinable {
    debug_assert_eq!(node.as_rule(), Rule::undefine_from);
    let child = node.into_child();
    match child.as_rule() {
        Rule::undefine_annotation_from_capability => {
            Undefinable::AnnotationCapability(visit_undefine_annotation_from_capability(child))
        }
        Rule::undefine_annotation_from_type => Undefinable::AnnotationType(visit_undefine_annotation_from_type(child)),
        Rule::undefine_capability => Undefinable::CapabilityType(visit_undefine_capability(child)),
        Rule::undefine_specialise => Undefinable::Specialise(visit_undefine_specialise(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.as_str().to_owned() }),
    }
}

fn visit_undefine_annotation_from_capability(node: Node<'_>) -> AnnotationCapability {
    debug_assert_eq!(node.as_rule(), Rule::undefine_annotation_from_capability);
    let span = node.span();
    let mut children = node.into_children();
    let annotation_category = visit_annotation_category(children.consume_expected(Rule::annotation_category));
    children.skip_expected(Rule::FROM);
    let type_ = visit_label(children.consume_expected(Rule::label));
    let capability = visit_type_capability_base(children.consume_expected(Rule::type_capability_base));
    debug_assert_eq!(children.try_consume_any(), None);
    AnnotationCapability::new(span, annotation_category, type_, capability)
}

fn visit_undefine_annotation_from_type(node: Node<'_>) -> AnnotationType {
    debug_assert_eq!(node.as_rule(), Rule::undefine_annotation_from_type);
    let span = node.span();
    let mut children = node.into_children();
    let annotation_category = visit_annotation_category(children.consume_expected(Rule::annotation_category));
    children.skip_expected(Rule::FROM);
    let type_ = visit_label(children.consume_expected(Rule::label));
    debug_assert_eq!(children.try_consume_any(), None);
    AnnotationType::new(span, annotation_category, type_)
}

fn visit_annotation_category(node: Node<'_>) -> token::Annotation {
    debug_assert_eq!(node.as_rule(), Rule::annotation_category);
    let child = node.into_child();
    match child.as_rule() {
        Rule::ANNOTATION_ABSTRACT => token::Annotation::Abstract,
        Rule::ANNOTATION_CARD => token::Annotation::Cardinality,
        Rule::ANNOTATION_CASCADE => token::Annotation::Cascade,
        Rule::ANNOTATION_DISTINCT => token::Annotation::Distinct,
        Rule::ANNOTATION_INDEPENDENT => token::Annotation::Independent,
        Rule::ANNOTATION_KEY => token::Annotation::Key,
        Rule::ANNOTATION_RANGE => token::Annotation::Range,
        Rule::ANNOTATION_REGEX => token::Annotation::Regex,
        Rule::ANNOTATION_SUBKEY => token::Annotation::Subkey,
        Rule::ANNOTATION_UNIQUE => token::Annotation::Unique,
        Rule::ANNOTATION_VALUES => token::Annotation::Values,
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.as_str().to_owned() }),
    }
}

fn visit_undefine_capability(node: Node<'_>) -> CapabilityType {
    debug_assert_eq!(node.as_rule(), Rule::undefine_capability);
    let span = node.span();
    let mut children = node.into_children();
    let capability = visit_type_capability_base(children.consume_expected(Rule::type_capability_base));
    children.skip_expected(Rule::FROM);
    let type_ = visit_label(children.consume_expected(Rule::label));
    debug_assert_eq!(children.try_consume_any(), None);
    CapabilityType::new(span, capability, type_)
}

fn visit_undefine_specialise(node: Node<'_>) -> Specialise {
    debug_assert_eq!(node.as_rule(), Rule::undefine_specialise);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::AS);
    let specialised = visit_label(children.consume_expected(Rule::label));
    children.skip_expected(Rule::FROM);
    let type_ = visit_label(children.consume_expected(Rule::label));
    let relates = visit_relates_declaration(children.consume_expected(Rule::relates_declaration));
    debug_assert_eq!(children.try_consume_any(), None);
    Specialise::new(span, specialised, type_, relates)
}

fn visit_undefine_struct(node: Node<'_>) -> Struct {
    debug_assert_eq!(node.as_rule(), Rule::undefine_struct);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::STRUCT);
    let ident = visit_identifier(children.consume_expected(Rule::identifier));
    debug_assert_eq!(children.try_consume_any(), None);
    Struct::new(span, ident)
}

fn visit_undefine_function(node: Node<'_>) -> Function {
    debug_assert_eq!(node.as_rule(), Rule::undefine_function);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::FUN);
    let ident = visit_identifier(children.consume_expected(Rule::identifier));
    debug_assert_eq!(children.try_consume_any(), None);
    Function::new(span, ident)
}
