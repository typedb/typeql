/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::{IntoChildNodes, Node, Rule, RuleMatcher};
use crate::{
    annotation::{
        Abstract, Annotation, Cardinality, Cascade, Distinct, Independent, Key, Range, Regex, Subkey, Unique, Values,
    },
    common::{error::TypeQLError, Spanned},
    expression::Value,
    parser::{expression::visit_value_literal, visit_identifier},
};

pub(super) fn visit_annotations(node: Node<'_>) -> Vec<Annotation> {
    debug_assert_eq!(node.as_rule(), Rule::annotations);
    node.into_children().map(visit_annotation).collect()
}

fn visit_annotation(node: Node<'_>) -> Annotation {
    debug_assert_eq!(node.as_rule(), Rule::annotation);
    let span = node.span();
    let child = node.into_child();
    match child.as_rule() {
        Rule::ANNOTATION_ABSTRACT => Annotation::Abstract(Abstract::new(span)),
        Rule::ANNOTATION_CASCADE => Annotation::Cascade(Cascade::new(span)),
        Rule::ANNOTATION_DISTINCT => Annotation::Distinct(Distinct::new(span)),
        Rule::ANNOTATION_INDEPENDENT => Annotation::Independent(Independent::new(span)),
        Rule::ANNOTATION_KEY => Annotation::Key(Key::new(span)),
        Rule::ANNOTATION_UNIQUE => Annotation::Unique(Unique::new(span)),
        Rule::annotation_card => Annotation::Cardinality(visit_annotation_card(child)),
        Rule::annotation_range => Annotation::Range(visit_annotation_range(child)),
        Rule::annotation_regex => Annotation::Regex(visit_annotation_regex(child)),
        Rule::annotation_subkey => Annotation::Subkey(visit_annotation_subkey(child)),
        Rule::annotation_values => Annotation::Values(visit_annotation_values(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_annotation_card(node: Node<'_>) -> Cardinality {
    debug_assert_eq!(node.as_rule(), Rule::annotation_card);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::ANNOTATION_CARD);
    let lower = children.consume_expected(Rule::integer_literal).as_str().parse().unwrap();
    let upper = children.try_consume_expected(Rule::integer_literal).map(|child| child.as_str().parse().unwrap());
    debug_assert_eq!(children.try_consume_any(), None);
    Cardinality::new(span, lower, upper)
}

fn visit_annotation_range(node: Node<'_>) -> Range {
    debug_assert_eq!(node.as_rule(), Rule::annotation_range);
    let span = node.span();
    let mut children = node.into_children();
    let (lower, upper) = visit_range(children.skip_expected(Rule::ANNOTATION_RANGE).consume_expected(Rule::range));
    debug_assert_eq!(children.try_consume_any(), None);
    Range::new(span, lower, upper)
}

fn visit_range(node: Node<'_>) -> (Option<Value>, Option<Value>) {
    debug_assert_eq!(node.as_rule(), Rule::range);
    let child = node.into_child();
    match child.as_rule() {
        Rule::range_full => visit_range_full(child),
        Rule::range_from => visit_range_from(child),
        Rule::range_to => visit_range_to(child),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_range_full(node: Node<'_>) -> (Option<Value>, Option<Value>) {
    debug_assert_eq!(node.as_rule(), Rule::range_full);
    let mut children = node.into_children();
    let lower = visit_value_literal(children.consume_expected(Rule::value_literal));
    let upper = visit_value_literal(children.consume_expected(Rule::value_literal));
    debug_assert_eq!(children.try_consume_any(), None);
    (Some(lower), Some(upper))
}

fn visit_range_from(node: Node<'_>) -> (Option<Value>, Option<Value>) {
    debug_assert_eq!(node.as_rule(), Rule::range_from);
    let mut children = node.into_children();
    let lower = visit_value_literal(children.consume_expected(Rule::value_literal));
    debug_assert_eq!(children.try_consume_any(), None);
    (Some(lower), None)
}

fn visit_range_to(node: Node<'_>) -> (Option<Value>, Option<Value>) {
    debug_assert_eq!(node.as_rule(), Rule::range_to);
    let mut children = node.into_children();
    let upper = visit_value_literal(children.consume_expected(Rule::value_literal));
    debug_assert_eq!(children.try_consume_any(), None);
    (None, Some(upper))
}

fn visit_annotation_subkey(node: Node<'_>) -> Subkey {
    debug_assert_eq!(node.as_rule(), Rule::annotation_subkey);
    let span = node.span();
    let mut children = node.into_children();
    children.consume_expected(Rule::ANNOTATION_SUBKEY);
    let ident = visit_identifier(children.consume_expected(Rule::identifier));
    debug_assert_eq!(children.try_consume_any(), None);
    Subkey::new(span, ident)
}

fn visit_annotation_regex(node: Node<'_>) -> Regex {
    debug_assert_eq!(node.as_rule(), Rule::annotation_regex);
    let span = node.span();
    let mut children = node.into_children();
    children.consume_expected(Rule::ANNOTATION_REGEX);
    let regex = children.consume_expected(Rule::quoted_string_literal).as_str().to_owned(); // FIXME unquote
    debug_assert_eq!(children.try_consume_any(), None);
    Regex::new(span, regex)
}

fn visit_annotation_values(node: Node<'_>) -> Values {
    debug_assert_eq!(node.as_rule(), Rule::annotation_values);
    let span = node.span();
    let values = node.into_children().skip_expected(Rule::ANNOTATION_VALUES).map(|v| v.as_str().to_owned()).collect();
    Values::new(span, values)
}
