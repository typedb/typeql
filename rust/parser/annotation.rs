/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::{IntoChildNodes, Node, Rule, RuleMatcher};
use crate::{
    annotation::{Abstract, Annotation, Cardinality, Cascade, Distinct, Independent, Key, Regex, Unique, Values},
    common::{error::TypeQLError, Spanned},
};

pub(super) fn visit_annotations_relates(node: Node<'_>) -> Vec<Annotation> {
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
                    Annotation::Cardinality(Cardinality::new(span, lower, upper))
                }
                Rule::ANNOTATION_CASCADE => Annotation::Cascade(Cascade::new(span)),
                Rule::ANNOTATION_DISTINCT => Annotation::Distinct(Distinct::new(span)),
                _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: anno.to_string() }),
            }
        })
        .collect()
}

pub(super) fn visit_annotations_owns(node: Node<'_>) -> Vec<Annotation> {
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
                    Annotation::Cardinality(Cardinality::new(span, lower, upper))
                }
                Rule::ANNOTATION_DISTINCT => Annotation::Distinct(Distinct::new(span)),
                Rule::ANNOTATION_KEY => Annotation::Key(Key::new(span)),
                Rule::ANNOTATION_UNIQUE => Annotation::Unique(Unique::new(span)),
                _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: anno.to_string() }),
            }
        })
        .collect()
}

pub(super) fn visit_annotations_value(node: Node<'_>) -> Vec<Annotation> {
    node.into_children()
        .map(|anno| match anno.as_rule() {
            Rule::annotation_regex => visit_annotation_regex(anno),
            Rule::annotation_values => visit_annotation_values(anno),
            _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: anno.to_string() }),
        })
        .collect()
}

pub(super) fn visit_annotation_values(node: Node<'_>) -> Annotation {
    let span = node.span();
    let values = node.into_children().skip_expected(Rule::ANNOTATION_VALUES).map(|v| v.as_str().to_owned()).collect();
    Annotation::Values(Values::new(span, values))
}

pub(super) fn visit_annotation_regex(node: Node<'_>) -> Annotation {
    let span = node.span();
    let mut children = node.into_children();
    children.consume_expected(Rule::ANNOTATION_REGEX);
    let regex = children.consume_expected(Rule::quoted_string_literal).as_str().to_owned(); // FIXME
    Annotation::Regex(Regex::new(span, regex))
}

pub(super) fn visit_annotations_sub(node: Node<'_>) -> Vec<Annotation> {
    node.into_children()
        .map(|anno| match anno.as_rule() {
            Rule::ANNOTATION_ABSTRACT => Annotation::Abstract(Abstract::new(anno.span())),
            Rule::ANNOTATION_CASCADE => Annotation::Cascade(Cascade::new(anno.span())),
            Rule::ANNOTATION_INDEPENDENT => Annotation::Independent(Independent::new(anno.span())),
            _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: anno.to_string() }),
        })
        .collect()
}
