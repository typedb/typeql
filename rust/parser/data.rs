/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::{statement::visit_statement, IntoChildNodes, Node, Rule};
use crate::{
    common::{error::TypeQLError, Spanned},
    parser::RuleMatcher,
    pattern::{Conjunction, Disjunction, Negation, Pattern, Try},
    query::{
        data::stage::{Insert, Match, Stage},
        DataQuery,
    },
};

pub(super) fn visit_query_data(node: Node<'_>) -> DataQuery {
    debug_assert_eq!(node.as_rule(), Rule::query_data);
    let span = node.span();
    node.into_children().fold(DataQuery::new(span), |query, stage| query.then(visit_query_stage(stage)))
}

fn visit_query_stage(node: Node<'_>) -> Stage {
    debug_assert_eq!(node.as_rule(), Rule::query_stage);
    let span = node.span();
    let child = node.into_child();
    match child.as_rule() {
        Rule::stage_match => Stage::Match(visit_stage_match(child)),
        Rule::stage_insert => Stage::Insert(visit_stage_insert(child)),
        Rule::stage_delete => visit_stage_delete(child),
        Rule::stage_put => visit_stage_put(child),
        Rule::stage_fetch => visit_stage_fetch(child),
        Rule::stage_reduce => visit_stage_reduce(child),
        Rule::stage_modifier => visit_stage_modifier(child),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_stage_match(node: Node<'_>) -> Match {
    debug_assert_eq!(node.as_rule(), Rule::stage_match);
    let span = node.span();
    let mut children = node.into_children();
    let patterns = children.skip_expected(Rule::MATCH).consume_expected(Rule::patterns);
    Match::new(span, visit_patterns(patterns))
}

fn visit_patterns(node: Node<'_>) -> Vec<Pattern> {
    debug_assert_eq!(node.as_rule(), Rule::patterns);
    node.into_children().map(visit_pattern).collect()
}

fn visit_pattern(node: Node<'_>) -> Pattern {
    debug_assert_eq!(node.as_rule(), Rule::pattern);
    let child = node.into_child();
    match child.as_rule() {
        Rule::pattern_conjunction => Pattern::Conjunction(visit_pattern_conjunction(child)),
        Rule::pattern_disjunction => Pattern::Disjunction(visit_pattern_disjunction(child)),
        Rule::pattern_negation => Pattern::Negation(visit_pattern_negation(child)),
        Rule::pattern_try => Pattern::Try(visit_pattern_try(child)),
        Rule::statement => Pattern::Statement(visit_statement(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_pattern_conjunction(node: Node<'_>) -> Conjunction {
    debug_assert_eq!(node.as_rule(), Rule::pattern_conjunction);
    let span = node.span();
    Conjunction::new(span, visit_patterns(node.into_child()))
}

fn visit_pattern_disjunction(node: Node<'_>) -> Disjunction {
    debug_assert_eq!(node.as_rule(), Rule::pattern_disjunction);
    let span = node.span();
    let mut branches = Vec::new();
    let mut children = node.into_children();
    while let Some(branch) = children.try_consume_expected(Rule::patterns) {
        branches.push(visit_patterns(branch));
        children.try_consume_expected(Rule::OR);
    }
    debug_assert!(children.try_consume_any().is_none());
    Disjunction::new(span, branches)
}

fn visit_pattern_negation(node: Node<'_>) -> Negation {
    debug_assert_eq!(node.as_rule(), Rule::pattern_negation);
    let span = node.span();
    let mut children = node.into_children();
    let patterns = children.skip_expected(Rule::NOT).consume_expected(Rule::patterns);
    debug_assert!(children.try_consume_any().is_none());
    Negation::new(span, visit_patterns(patterns))
}

fn visit_pattern_try(node: Node<'_>) -> Try {
    debug_assert_eq!(node.as_rule(), Rule::pattern_try);
    let span = node.span();
    let mut children = node.into_children();
    let patterns = children.skip_expected(Rule::TRY).consume_expected(Rule::patterns);
    debug_assert!(children.try_consume_any().is_none());
    Try::new(span, visit_patterns(patterns))
}

fn visit_stage_insert(node: Node<'_>) -> Insert {
    debug_assert_eq!(node.as_rule(), Rule::stage_insert);
    let span = node.span();
    let mut children = node.into_children();
    let statement_things = children.skip_expected(Rule::INSERT).consume_expected(Rule::statement_things);
    // StageInsert::new(span, visit_statement_things(statements))
    todo!()
}

fn visit_stage_delete(node: Node<'_>) -> Stage {
    todo!()
}

fn visit_stage_put(node: Node<'_>) -> Stage {
    todo!()
}

fn visit_stage_fetch(node: Node<'_>) -> Stage {
    todo!()
}

fn visit_stage_reduce(node: Node<'_>) -> Stage {
    todo!()
}

fn visit_stage_modifier(node: Node<'_>) -> Stage {
    todo!()
}
