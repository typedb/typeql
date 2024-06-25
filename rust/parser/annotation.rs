/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::{IntoChildNodes, Node, Rule, RuleMatcher};
use crate::{
    common::{error::TypeQLError, Spanned},
    annotation::{AnnotationOwns, AnnotationRelates, AnnotationSub, AnnotationValueType},
};

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
                    let lower = children.consume_expected(Rule::annotation_card_lower).as_str().parse().unwrap();
                    let upper = children.consume_expected(Rule::annotation_card_upper).as_str().parse().ok();
                    AnnotationRelates::Cardinality(lower, upper, span)
                }
                Rule::ANNOTATION_DISTINCT => AnnotationRelates::Distinct(span),
                Rule::ANNOTATION_CASCADE => AnnotationRelates::Cascade(span),
                _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: anno.to_string() }),
            }
        })
        .collect()
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
                    let lower = children.consume_expected(Rule::annotation_card_lower).as_str().parse().unwrap();
                    let upper = children.consume_expected(Rule::annotation_card_upper).as_str().parse().ok();
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
    let regex = children.consume_expected(Rule::quoted_string_literal).as_str().to_owned();
    AnnotationValueType::Regex(regex, span)
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
