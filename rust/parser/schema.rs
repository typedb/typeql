/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::{visit_label, IntoChildNodes, Node, Rule, RuleMatcher};
use crate::{
    common::{error::TypeQLError, Spanned},
    definition::type_::{
        declaration::{
            AnnotationOwns, AnnotationRelates, AnnotationSub, AnnotationValueType, Owned, Owns, Played, Plays, Related,
            Relates, Sub, ValueType,
        },
        Type,
    },
    parser::{visit_label_scoped, visit_value_type_primitive},
    pattern::{Definable, Label},
    query::{SchemaQuery, TypeQLDefine, TypeQLUndefine},
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
    debug_assert!(children.try_consume_any().is_none());
    query
}

fn visit_query_define(node: Node<'_>) -> TypeQLDefine {
    debug_assert_eq!(node.as_rule(), Rule::query_define);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::DEFINE);
    let query = TypeQLDefine::new(visit_definables(children.consume_expected(Rule::definables)), span);
    debug_assert!(children.try_consume_any().is_none());
    query
}

fn visit_query_undefine(node: Node<'_>) -> TypeQLUndefine {
    debug_assert_eq!(node.as_rule(), Rule::query_undefine);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::UNDEFINE);
    let query = TypeQLUndefine::new(visit_definables(children.consume_expected(Rule::definables)), span);
    debug_assert!(children.try_consume_any().is_none());
    query
}

fn visit_definables(node: Node<'_>) -> Vec<Definable> {
    debug_assert_eq!(node.as_rule(), Rule::definables);
    node.into_children().map(visit_definable).collect()
}

fn visit_definable(node: Node<'_>) -> Definable {
    debug_assert_eq!(node.as_rule(), Rule::definable);
    let child = node.into_child();
    match child.as_rule() {
        Rule::definition_type => Definable::TypeDeclaration(visit_definition_type(child)),
        Rule::definition_function => todo!(),
        Rule::definition_struct => todo!(),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_definition_type(node: Node<'_>) -> Type {
    debug_assert_eq!(node.as_rule(), Rule::definition_type);
    let span = node.span();
    let mut children = node.into_children();
    let label = visit_label(children.consume_expected(Rule::LABEL));
    children.fold(Type::new(label, span), |type_declaration, constraint| {
        let constraint = constraint.into_child();
        match constraint.as_rule() {
            Rule::sub_declaration => type_declaration.set_sub(visit_sub_declaration(constraint)),
            Rule::value_type_declaration => type_declaration.set_value_type(visit_value_type_declaration(constraint)),
            Rule::owns_declaration => type_declaration.add_owns(visit_owns_declaration(constraint)),
            Rule::relates_declaration => type_declaration.add_relates(visit_relates_declaration(constraint)),
            Rule::plays_declaration => type_declaration.add_plays(visit_plays_declaration(constraint)),
            _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: constraint.to_string() }),
        }
    })
}

fn visit_plays_declaration(node: Node<'_>) -> Plays {
    debug_assert_eq!(node.as_rule(), Rule::plays_declaration);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::PLAYS);

    let label = visit_label_scoped(children.consume_expected(Rule::LABEL_SCOPED));
    let played = match children.try_consume_expected(Rule::AS) {
        None => Played::new(label, None),
        Some(_) => Played::new(label, Some(visit_label(children.consume_expected(Rule::LABEL)))),
    };

    debug_assert!(children.try_consume_any().is_none());
    Plays::new(played, span)
}

fn visit_relates_declaration(node: Node<'_>) -> Relates {
    debug_assert_eq!(node.as_rule(), Rule::relates_declaration);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::RELATES);

    let related_label = children.consume_any();
    let related = match related_label.as_rule() {
        Rule::list_label => Related::List(visit_list_label(related_label)),
        Rule::LABEL => {
            let label = visit_label(related_label);
            match children.try_consume_expected(Rule::AS) {
                None => Related::Role(label, None),
                Some(_) => Related::Role(label, Some(visit_label(children.consume_expected(Rule::LABEL)))),
            }
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: related_label.to_string() }),
    };

    let annotations_relates = visit_annotations_relates(children.consume_expected(Rule::annotations_relates));
    debug_assert!(children.try_consume_any().is_none());
    Relates::new(related, annotations_relates, span)
}

pub(super) fn visit_annotations_relates(node: Node<'_>) -> Vec<AnnotationRelates> {
    debug_assert_eq!(node.as_rule(), Rule::annotations_relates);
    let children = node.into_children();
    children
        .map(|anno| {
            let span = anno.span();
            match anno.as_rule() {
                Rule::annotation_card => {
                    let mut children = anno.into_children();
                    children.skip_expected(Rule::ANNOTATION_CARD);
                    let lower = children.consume_expected(Rule::ANNOTATION_CARD_LOWER).as_str().parse().unwrap();
                    let upper = children.consume_expected(Rule::ANNOTATION_CARD_UPPER).as_str().parse().ok();
                    AnnotationRelates::Cardinality(lower, upper, span)
                }
                Rule::ANNOTATION_DISTINCT => AnnotationRelates::Distinct(span),
                Rule::ANNOTATION_CASCADE => AnnotationRelates::Cascade(span),
                _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: anno.to_string() }),
            }
        })
        .collect()
}

fn visit_owns_declaration(node: Node<'_>) -> Owns {
    debug_assert_eq!(node.as_rule(), Rule::owns_declaration);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::OWNS);

    let owned_label = children.consume_any();
    let owned = match owned_label.as_rule() {
        Rule::list_label => Owned::List(visit_list_label(owned_label)),
        Rule::LABEL => {
            let label = visit_label(owned_label);
            match children.try_consume_expected(Rule::AS) {
                None => Owned::Attribute(label, None),
                Some(_) => Owned::Attribute(label, Some(visit_label(children.consume_expected(Rule::LABEL)))),
            }
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: owned_label.to_string() }),
    };

    let annotations_owns = visit_annotations_owns(children.consume_expected(Rule::annotations_owns));
    debug_assert!(children.try_consume_any().is_none());
    Owns::new(owned, annotations_owns, span)
}

fn visit_list_label(node: Node<'_>) -> Label {
    debug_assert_eq!(node.as_rule(), Rule::list_label);
    let mut children = node.into_children();
    let label = children.consume_expected(Rule::LABEL);
    debug_assert!(children.try_consume_any().is_none());
    Label::new_unscoped(label.as_str(), label.span())
}

pub(super) fn visit_annotations_owns(node: Node<'_>) -> Vec<AnnotationOwns> {
    debug_assert_eq!(node.as_rule(), Rule::annotations_owns);
    let children = node.into_children();
    children
        .map(|anno| {
            let span = anno.span();
            match anno.as_rule() {
                Rule::annotation_card => {
                    let mut children = anno.into_children();
                    children.skip_expected(Rule::ANNOTATION_CARD);
                    let lower = children.consume_expected(Rule::ANNOTATION_CARD_LOWER).as_str().parse().unwrap();
                    let upper = children.consume_expected(Rule::ANNOTATION_CARD_UPPER).as_str().parse().ok();
                    AnnotationOwns::Cardinality(lower, upper, span)
                }
                Rule::ANNOTATION_DISTINCT => AnnotationOwns::Distinct(span),
                Rule::ANNOTATION_KEY => AnnotationOwns::Key(span),
                Rule::ANNOTATION_UNIQUE => AnnotationOwns::Unique(span),
                _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: anno.to_string() }),
            }
        })
        .collect()
}

fn visit_value_type_declaration(node: Node<'_>) -> ValueType {
    debug_assert_eq!(node.as_rule(), Rule::value_type_declaration);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::VALUE);
    let value_type_node = children.consume_any();
    let (value_type, annotations_value_type) = match value_type_node.as_rule() {
        Rule::LABEL => (visit_label(value_type_node).to_string(), Vec::new()),
        Rule::value_type_primitive => (
            visit_value_type_primitive(value_type_node),
            visit_annotations_value(children.consume_expected(Rule::annotations_value)),
        ),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: value_type_node.to_string() }),
    };
    debug_assert!(children.try_consume_any().is_none());
    ValueType::new(value_type, annotations_value_type, span)
}

pub(super) fn visit_annotations_value(node: Node<'_>) -> Vec<AnnotationValueType> {
    node.into_children()
        .map(|anno| match anno.as_rule() {
            Rule::annotation_regex => visit_annotation_regex(anno),
            Rule::annotation_values => visit_annotation_values(anno),
            _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: anno.to_string() }),
        })
        .collect()
}

pub(super) fn visit_annotation_values(node: Node<'_>) -> AnnotationValueType {
    let span = node.span();
    let values = node.into_children().skip_expected(Rule::ANNOTATION_VALUES).map(|v| v.as_str().to_owned()).collect();
    AnnotationValueType::Values(values, span)
}

pub(super) fn visit_annotation_regex(node: Node<'_>) -> AnnotationValueType {
    let span = node.span();
    let mut children = node.into_children();
    children.consume_expected(Rule::ANNOTATION_REGEX);
    let regex = children.consume_expected(Rule::QUOTED_STRING).as_str().to_owned();
    AnnotationValueType::Regex(regex, span)
}

fn visit_sub_declaration(node: Node<'_>) -> Sub {
    debug_assert_eq!(node.as_rule(), Rule::sub_declaration);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::SUB);
    let supertype_label = visit_label(children.consume_expected(Rule::LABEL));
    let annotations_sub = visit_annotations_sub(children.consume_expected(Rule::annotations_sub));
    debug_assert!(children.try_consume_any().is_none());
    Sub::new(supertype_label, annotations_sub, span)
}

pub(super) fn visit_annotations_sub(node: Node<'_>) -> Vec<AnnotationSub> {
    node.into_children()
        .map(|anno| match anno.as_rule() {
            Rule::ANNOTATION_ABSTRACT => AnnotationSub::Abstract(anno.span()),
            Rule::ANNOTATION_CASCADE => AnnotationSub::Cascade(anno.span()),
            Rule::ANNOTATION_INDEPENDENT => AnnotationSub::Independent(anno.span()),
            _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: anno.to_string() }),
        })
        .collect()
}
