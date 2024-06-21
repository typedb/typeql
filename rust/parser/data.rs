/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::{statement::visit_statement, IntoChildNodes, Node, Rule};
use crate::{
    common::{
        error::TypeQLError,
        token::{Aggregate, Order},
        Spanned,
    },
    parser::{statement::thing::visit_statement_things, visit_long_value, visit_var, RuleMatcher},
    pattern::{Conjunction, Disjunction, Negation, Pattern, Try},
    query::{
        data::stage::{
            modifier::{Filter, Limit, Offset, OrderedVariable, Sort},
            reduce::{Check, Count, First, ReduceAll, Stat},
            Delete, Insert, Match, Modifier, Put, Reduce, Stage,
        },
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
        Rule::stage_delete => Stage::Delete(visit_stage_delete(child)),
        Rule::stage_put => Stage::Put(visit_stage_put(child)),
        Rule::stage_fetch => visit_stage_fetch(child),
        Rule::stage_reduce => Stage::Reduce(visit_stage_reduce(child)),
        Rule::stage_modifier => Stage::Modifier(visit_stage_modifier(child)),
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

pub(super) fn visit_patterns(node: Node<'_>) -> Vec<Pattern> {
    debug_assert_eq!(node.as_rule(), Rule::patterns);
    node.into_children().map(visit_pattern).collect()
}

pub(super) fn visit_pattern(node: Node<'_>) -> Pattern {
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
    debug_assert_eq!(children.try_consume_any(), None);
    Disjunction::new(span, branches)
}

fn visit_pattern_negation(node: Node<'_>) -> Negation {
    debug_assert_eq!(node.as_rule(), Rule::pattern_negation);
    let span = node.span();
    let mut children = node.into_children();
    let patterns = children.skip_expected(Rule::NOT).consume_expected(Rule::patterns);
    debug_assert_eq!(children.try_consume_any(), None);
    Negation::new(span, visit_patterns(patterns))
}

fn visit_pattern_try(node: Node<'_>) -> Try {
    debug_assert_eq!(node.as_rule(), Rule::pattern_try);
    let span = node.span();
    let mut children = node.into_children();
    let patterns = children.skip_expected(Rule::TRY).consume_expected(Rule::patterns);
    debug_assert_eq!(children.try_consume_any(), None);
    Try::new(span, visit_patterns(patterns))
}

fn visit_stage_insert(node: Node<'_>) -> Insert {
    debug_assert_eq!(node.as_rule(), Rule::stage_insert);
    let span = node.span();
    let mut children = node.into_children();
    let statement_things = children.skip_expected(Rule::INSERT).consume_expected(Rule::statement_things);
    debug_assert_eq!(children.try_consume_any(), None);
    Insert::new(span, visit_statement_things(statement_things))
}

fn visit_stage_delete(node: Node<'_>) -> Delete {
    debug_assert_eq!(node.as_rule(), Rule::stage_delete);
    let span = node.span();
    let mut children = node.into_children();
    let statement_things = children.skip_expected(Rule::DELETE).consume_expected(Rule::statement_things);
    debug_assert_eq!(children.try_consume_any(), None);
    Delete::new(span, visit_statement_things(statement_things))
}

fn visit_stage_put(node: Node<'_>) -> Put {
    debug_assert_eq!(node.as_rule(), Rule::stage_put);
    let span = node.span();
    let mut children = node.into_children();
    let statement_things = children.skip_expected(Rule::PUT).consume_expected(Rule::statement_things);
    debug_assert_eq!(children.try_consume_any(), None);
    Put::new(span, visit_statement_things(statement_things))
}

fn visit_stage_fetch(node: Node<'_>) -> Stage {
    todo!()
}

fn visit_stage_reduce(node: Node<'_>) -> Reduce {
    debug_assert_eq!(node.as_rule(), Rule::stage_reduce);
    visit_reduce(node.into_child())
}

fn visit_reduce(node: Node<'_>) -> Reduce {
    debug_assert_eq!(node.as_rule(), Rule::reduce);
    let mut children = node.into_children();
    match children.peek_rule() {
        Some(Rule::reduce_limited) => {
            let reduce = visit_reduce_limited(children.consume_expected(Rule::reduce_limited));
            debug_assert_eq!(children.try_consume_any(), None);
            reduce
        }
        Some(Rule::reduce_all) => Reduce::All(children.map(visit_reduce_all).collect()),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: children.to_string() }),
    }
}

fn visit_reduce_limited(node: Node<'_>) -> Reduce {
    debug_assert_eq!(node.as_rule(), Rule::reduce_limited);
    let child = node.into_child();
    match child.as_rule() {
        Rule::CHECK => Reduce::Check(Check::new(child.span())),
        Rule::reduce_first => Reduce::First(visit_reduce_first(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_reduce_first(node: Node<'_>) -> First {
    debug_assert_eq!(node.as_rule(), Rule::reduce_first);
    let span = node.span();
    let mut children = node.into_children();
    let variables = children.skip_expected(Rule::FIRST).map(visit_var).collect();
    First::new(span, variables)
}

fn visit_reduce_all(node: Node<'_>) -> ReduceAll {
    debug_assert_eq!(node.as_rule(), Rule::reduce_all);
    let span = node.span();
    let mut children = node.into_children();
    let keyword = children.consume_any();
    match keyword.as_rule() {
        Rule::COUNT => ReduceAll::Count(Count::new(span, children.map(visit_var).collect())),
        Rule::MAX => ReduceAll::Stat(Stat::new(span, Aggregate::Max, visit_var(children.consume_expected(Rule::var)))),
        Rule::MIN => ReduceAll::Stat(Stat::new(span, Aggregate::Min, visit_var(children.consume_expected(Rule::var)))),
        Rule::MEAN => {
            ReduceAll::Stat(Stat::new(span, Aggregate::Mean, visit_var(children.consume_expected(Rule::var))))
        }
        Rule::MEDIAN => {
            ReduceAll::Stat(Stat::new(span, Aggregate::Median, visit_var(children.consume_expected(Rule::var))))
        }
        Rule::STD => ReduceAll::Stat(Stat::new(span, Aggregate::Std, visit_var(children.consume_expected(Rule::var)))),
        Rule::SUM => ReduceAll::Stat(Stat::new(span, Aggregate::Sum, visit_var(children.consume_expected(Rule::var)))),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: keyword.to_string() }),
    }
}

fn visit_stage_modifier(node: Node<'_>) -> Modifier {
    debug_assert_eq!(node.as_rule(), Rule::stage_modifier);
    let child = node.into_child();
    match child.as_rule() {
        Rule::stage_filter => Modifier::Filter(visit_stage_filter(child)),
        Rule::stage_sort => Modifier::Sort(visit_stage_sort(child)),
        Rule::stage_offset => Modifier::Offset(visit_stage_offset(child)),
        Rule::stage_limit => Modifier::Limit(visit_stage_limit(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_stage_filter(node: Node<'_>) -> Filter {
    debug_assert_eq!(node.as_rule(), Rule::stage_filter);
    let span = node.span();
    let variables = node.into_children().skip_expected(Rule::FILTER).map(visit_var).collect();
    Filter::new(span, variables)
}

fn visit_stage_sort(node: Node<'_>) -> Sort {
    debug_assert_eq!(node.as_rule(), Rule::stage_sort);
    let span = node.span();
    let ordered_variables = node.into_children().skip_expected(Rule::SORT).map(visit_var_order).collect();
    Sort::new(span, ordered_variables)
}

fn visit_var_order(node: Node<'_>) -> OrderedVariable {
    debug_assert_eq!(node.as_rule(), Rule::var_order);
    let span = node.span();
    let mut children = node.into_children();
    let variable = visit_var(children.consume_expected(Rule::var));
    let ordering = children.try_consume_expected(Rule::ORDER).map(visit_order);
    debug_assert_eq!(children.try_consume_any(), None);
    OrderedVariable::new(span, variable, ordering)
}

fn visit_order(node: Node<'_>) -> Order {
    debug_assert_eq!(node.as_rule(), Rule::ORDER);
    let child = node.into_child();
    match child.as_rule() {
        Rule::ASC => Order::Asc,
        Rule::DESC => Order::Desc,
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_stage_offset(node: Node<'_>) -> Offset {
    debug_assert_eq!(node.as_rule(), Rule::stage_offset);
    let span = node.span();
    let mut children = node.into_children();
    let offset = visit_long_value(children.skip_expected(Rule::OFFSET).consume_expected(Rule::long_value));
    debug_assert_eq!(children.try_consume_any(), None);
    Offset::new(span, offset)
}

fn visit_stage_limit(node: Node<'_>) -> Limit {
    debug_assert_eq!(node.as_rule(), Rule::stage_limit);
    let span = node.span();
    let mut children = node.into_children();
    let limit = visit_long_value(children.skip_expected(Rule::LIMIT).consume_expected(Rule::long_value));
    debug_assert_eq!(children.try_consume_any(), None);
    Limit::new(span, limit)
}
