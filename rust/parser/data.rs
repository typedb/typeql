/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use itertools::Itertools;

use super::{
    define::function::visit_definition_function,
    statement::{
        thing::{visit_relation, visit_statement_thing},
        visit_statement,
    },
    visit_integer_literal, visit_label, visit_var, visit_vars, IntoChildNodes, Node, Rule, RuleMatcher,
};
use crate::{
    common::{
        error::TypeQLError,
        token::{Aggregate, Order},
        Spanned,
    },
    pattern::{Conjunction, Disjunction, Negation, Pattern, Try},
    query::{
        data::{
            stage::{
                delete::{Deletable, DeletableKind},
                fetch::{Projection, ProjectionAttribute, ProjectionKeyLabel, ProjectionKeyVar},
                modifier::{Filter, Limit, Offset, OrderedVariable, Sort},
                reduce::{Check, Count, First, ReduceValue, Stat},
                Delete, Fetch, Insert, Match, Modifier, Put, Reduce, Stage, Update,
            },
            Preamble,
        },
        DataQuery,
    },
    type_::Type,
};

pub(super) fn visit_query_data(node: Node<'_>) -> DataQuery {
    debug_assert_eq!(node.as_rule(), Rule::query_data);
    let span = node.span();
    let mut children = node.into_children();

    let preambles = children.take_while_ref(|child| child.as_rule() == Rule::preamble).map(visit_preamble).collect();
    let mut stages =
        children.take_while_ref(|child| child.as_rule() == Rule::query_stage).map(visit_query_stage).collect_vec();
    stages.extend(children.try_consume_expected(Rule::query_stage_terminal).map(visit_query_stage_terminal));

    debug_assert_eq!(children.try_consume_any(), None);

    DataQuery::new(span, preambles, stages)
}

fn visit_preamble(node: Node<'_>) -> Preamble {
    debug_assert_eq!(node.as_rule(), Rule::preamble);
    let span = node.span();
    let mut children = node.into_children();
    let patterns =
        visit_definition_function(children.skip_expected(Rule::WITH).consume_expected(Rule::definition_function));
    debug_assert_eq!(children.try_consume_any(), None);
    Preamble::new(span, patterns)
}

fn visit_query_stage(node: Node<'_>) -> Stage {
    debug_assert_eq!(node.as_rule(), Rule::query_stage);
    let child = node.into_child();
    match child.as_rule() {
        Rule::stage_match => Stage::Match(visit_stage_match(child)),
        Rule::stage_insert => Stage::Insert(visit_stage_insert(child)),
        Rule::stage_put => Stage::Put(visit_stage_put(child)),
        Rule::stage_update => Stage::Update(visit_stage_update(child)),
        Rule::stage_delete => Stage::Delete(visit_stage_delete(child)),
        Rule::stage_modifier => Stage::Modifier(visit_stage_modifier(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_query_stage_terminal(node: Node<'_>) -> Stage {
    debug_assert_eq!(node.as_rule(), Rule::query_stage_terminal);
    let child = node.into_child();
    match child.as_rule() {
        Rule::stage_fetch => Stage::Fetch(visit_stage_fetch(child)),
        Rule::stage_reduce => Stage::Reduce(visit_stage_reduce(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

pub(super) fn visit_stage_match(node: Node<'_>) -> Match {
    debug_assert_eq!(node.as_rule(), Rule::stage_match);
    let span = node.span();
    let mut children = node.into_children();
    let patterns = visit_patterns(children.skip_expected(Rule::MATCH).consume_expected(Rule::patterns));
    debug_assert_eq!(children.try_consume_any(), None);
    Match::new(span, patterns)
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
    let statement_things = node.into_children().skip_expected(Rule::INSERT).map(visit_statement_thing).collect();
    Insert::new(span, statement_things)
}

fn visit_stage_put(node: Node<'_>) -> Put {
    debug_assert_eq!(node.as_rule(), Rule::stage_put);
    let span = node.span();
    let statement_things = node.into_children().skip_expected(Rule::PUT).map(visit_statement_thing).collect();
    Put::new(span, statement_things)
}

fn visit_stage_update(node: Node<'_>) -> Update {
    debug_assert_eq!(node.as_rule(), Rule::stage_update);
    let span = node.span();
    let statement_things = node.into_children().skip_expected(Rule::UPDATE).map(visit_statement_thing).collect();
    Update::new(span, statement_things)
}

fn visit_stage_delete(node: Node<'_>) -> Delete {
    debug_assert_eq!(node.as_rule(), Rule::stage_delete);
    let span = node.span();
    let deletables = node.into_children().skip_expected(Rule::DELETE).map(visit_statement_deletable).collect();
    Delete::new(span, deletables)
}

fn visit_statement_deletable(node: Node<'_>) -> Deletable {
    debug_assert_eq!(node.as_rule(), Rule::statement_deletable);
    let span = node.span();
    let mut children = node.into_children();
    let kind = match children.peek_rule().unwrap() {
        Rule::var if children.len() == 1 => {
            DeletableKind::Concept { variable: visit_var(children.consume_expected(Rule::var)) }
        }
        Rule::HAS | Rule::var => {
            children.try_consume_expected(Rule::HAS);
            let attribute = visit_var(children.consume_expected(Rule::var));
            children.skip_expected(Rule::OF);
            let owner = visit_var(children.consume_expected(Rule::var));
            DeletableKind::Has { attribute, owner }
        }
        Rule::LINKS | Rule::relation => {
            children.try_consume_expected(Rule::LINKS);
            let players = visit_relation(children.consume_expected(Rule::relation));
            children.skip_expected(Rule::OF);
            let relation = visit_var(children.consume_expected(Rule::var));
            DeletableKind::Links { players, relation }
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: children.to_string() }),
    };
    debug_assert_eq!(children.try_consume_any(), None);
    Deletable::new(span, kind)
}

fn visit_stage_fetch(node: Node<'_>) -> Fetch {
    debug_assert_eq!(node.as_rule(), Rule::stage_fetch);
    let span = node.span();
    let mut children = node.into_children();
    let projections = visit_projections(children.skip_expected(Rule::FETCH).consume_expected(Rule::projections));
    debug_assert_eq!(children.try_consume_any(), None);
    Fetch::new(span, projections)
}

fn visit_projections(node: Node<'_>) -> Vec<Projection> {
    debug_assert_eq!(node.as_rule(), Rule::projections);
    node.into_children().map(visit_projection).collect()
}

fn visit_projection(node: Node<'_>) -> Projection {
    debug_assert_eq!(node.as_rule(), Rule::projection);
    let mut children = node.into_children();
    let child = children.consume_any();
    match child.as_rule() {
        Rule::projection_key_var => {
            let key_var = visit_projection_key_var(child);
            let node = children.try_consume_any();
            if let Some(n) = node {
                let attrs = visit_projection_attributes(n);
                Projection::Attribute(key_var, attrs)
            } else {
                Projection::Variable(key_var)
            }
        }
        Rule::projection_key_label => {
            let key_label = visit_projection_key_label(child);
            let subquery = visit_projection_subquery(children.consume_expected(Rule::projection_subquery));
            Projection::Subquery(key_label, subquery)
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_projection_key_label(node: Node<'_>) -> ProjectionKeyLabel {
    debug_assert_eq!(node.as_rule(), Rule::projection_key_label);
    let mut children = node.into_children();
    let child = children.consume_any();
    match child.as_rule() {
        Rule::quoted_string_literal => ProjectionKeyLabel { label: child.as_str().to_owned() },
        Rule::label => ProjectionKeyLabel { label: child.as_str().to_owned() },
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_projection_subquery(node: Node<'_>) -> DataQuery {
    debug_assert_eq!(node.as_rule(), Rule::projection_subquery);
    visit_query_data(node.into_child())
}

fn visit_projection_attributes(node: Node<'_>) -> Vec<ProjectionAttribute> {
    debug_assert_eq!(node.as_rule(), Rule::projection_attributes);
    node.into_children().map(visit_projection_attribute).collect()
}

fn visit_projection_attribute(node: Node<'_>) -> ProjectionAttribute {
    debug_assert_eq!(node.as_rule(), Rule::projection_attribute);
    let mut children = node.into_children();
    let attribute_label = Type::Label(visit_label(children.consume_expected(Rule::label)));
    let label = children.try_consume_expected(Rule::projection_key_as_label).map(visit_projection_key_as_label);
    debug_assert!(children.try_consume_any().is_none());
    ProjectionAttribute::new(attribute_label, label)
}

fn visit_projection_key_var(node: Node<'_>) -> ProjectionKeyVar {
    debug_assert_eq!(node.as_rule(), Rule::projection_key_var);
    let mut children = node.into_children();
    let variable = visit_var(children.consume_expected(Rule::var));
    let label = children.try_consume_expected(Rule::projection_key_as_label).map(visit_projection_key_as_label);
    debug_assert!(children.try_consume_any().is_none());
    ProjectionKeyVar::new(variable, label)
}

fn visit_projection_key_as_label(node: Node<'_>) -> ProjectionKeyLabel {
    debug_assert_eq!(node.as_rule(), Rule::projection_key_as_label);
    let mut children = node.into_children();
    children.skip_expected(Rule::AS);
    let label = visit_projection_key_label(children.consume_expected(Rule::projection_key_label));
    debug_assert!(children.try_consume_any().is_none());
    label
}
fn visit_stage_reduce(node: Node<'_>) -> Reduce {
    debug_assert_eq!(node.as_rule(), Rule::stage_reduce);
    visit_reduce(node.into_child())
}

pub(super) fn visit_reduce(node: Node<'_>) -> Reduce {
    debug_assert_eq!(node.as_rule(), Rule::reduce);
    let mut children = node.into_children();
    let reduce = match children.peek_rule().unwrap() {
        Rule::CHECK => Reduce::Check(Check::new(children.consume_expected(Rule::CHECK).span())),
        Rule::reduce_first => Reduce::First(visit_reduce_first(children.consume_expected(Rule::reduce_first))),
        Rule::reduce_value => Reduce::Value(children.by_ref().map(visit_reduce_value).collect()),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: children.to_string() }),
    };
    debug_assert!(children.try_consume_any().is_none());
    reduce
}

fn visit_reduce_first(node: Node<'_>) -> First {
    debug_assert_eq!(node.as_rule(), Rule::reduce_first);
    let span = node.span();
    let mut children = node.into_children();
    let variables = visit_vars(children.skip_expected(Rule::FIRST).consume_expected(Rule::vars));
    First::new(span, variables)
}

fn visit_reduce_value(node: Node<'_>) -> ReduceValue {
    debug_assert_eq!(node.as_rule(), Rule::reduce_value);
    let span = node.span();
    let mut children = node.into_children();
    let keyword = children.consume_any();
    match keyword.as_rule() {
        Rule::COUNT => ReduceValue::Count(Count::new(
            span,
            children.try_consume_expected(Rule::vars).map(visit_vars).unwrap_or_default(),
        )),
        Rule::MAX => {
            ReduceValue::Stat(Stat::new(span, Aggregate::Max, visit_var(children.consume_expected(Rule::var))))
        }
        Rule::MIN => {
            ReduceValue::Stat(Stat::new(span, Aggregate::Min, visit_var(children.consume_expected(Rule::var))))
        }
        Rule::MEAN => {
            ReduceValue::Stat(Stat::new(span, Aggregate::Mean, visit_var(children.consume_expected(Rule::var))))
        }
        Rule::MEDIAN => {
            ReduceValue::Stat(Stat::new(span, Aggregate::Median, visit_var(children.consume_expected(Rule::var))))
        }
        Rule::STD => {
            ReduceValue::Stat(Stat::new(span, Aggregate::Std, visit_var(children.consume_expected(Rule::var))))
        }
        Rule::SUM => {
            ReduceValue::Stat(Stat::new(span, Aggregate::Sum, visit_var(children.consume_expected(Rule::var))))
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: keyword.to_string() }),
    }
}

pub(super) fn visit_stage_modifier(node: Node<'_>) -> Modifier {
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
    let mut children = node.into_children();
    let variables = visit_vars(children.skip_expected(Rule::FILTER).consume_expected(Rule::vars));
    debug_assert_eq!(children.try_consume_any(), None);
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
    let offset = visit_integer_literal(children.skip_expected(Rule::OFFSET).consume_expected(Rule::integer_literal));
    debug_assert_eq!(children.try_consume_any(), None);
    Offset::new(span, offset)
}

fn visit_stage_limit(node: Node<'_>) -> Limit {
    debug_assert_eq!(node.as_rule(), Rule::stage_limit);
    let span = node.span();
    let mut children = node.into_children();
    let limit = visit_integer_literal(children.skip_expected(Rule::LIMIT).consume_expected(Rule::integer_literal));
    debug_assert_eq!(children.try_consume_any(), None);
    Limit::new(span, limit)
}
