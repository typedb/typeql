/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use itertools::Itertools;

use super::{define::function::visit_definition_function, expression::{visit_expression, visit_expression_function}, literal::{visit_integer_literal, visit_quoted_string_literal}, statement::{
    thing::{visit_relation, visit_statement_thing},
    visit_statement,
}, type_::{visit_label, visit_label_list}, visit_var, visit_vars, IntoChildNodes, Node, Rule, RuleMatcher, visit_var_named};
use crate::{
    common::{
        error::TypeQLError,
        token::{Aggregate, Order},
        Spanned,
    },
    pattern::{Conjunction, Disjunction, Negation, Optional, Pattern},
    query::{
        pipeline::{
            stage::{
                delete::{Deletable, DeletableKind},
                fetch::FetchEntry,
                modifier::{Limit, Offset, OrderedVariable, Select, Sort},
                reduce::{Check, Count, First, ReduceValue, Stat},
                Delete, Fetch, Insert, Match, Modifier, Put, Reduce, Stage, Update,
            },
            Preamble,
        },
        stage::fetch::{FetchAttribute, FetchObjectBody, FetchList, FetchObject, FetchObjectEntry, FetchSingle, FetchStream},
        Pipeline,
    },
    type_::NamedType,
    value::StringLiteral,
    TypeRef, TypeRefAny,
};

pub(super) fn visit_query_pipeline(node: Node<'_>) -> Pipeline {
    debug_assert_eq!(node.as_rule(), Rule::query_pipeline);
    let span = node.span();
    let mut children = node.into_children();

    let preambles = children.take_while_ref(|child| child.as_rule() == Rule::preamble).map(visit_preamble).collect();
    let mut stages =
        children.take_while_ref(|child| child.as_rule() == Rule::query_stage).map(visit_query_stage).collect_vec();
    stages.extend(children.try_consume_expected(Rule::query_stage_terminal).map(visit_query_stage_terminal));

    debug_assert_eq!(children.try_consume_any(), None);

    Pipeline::new(span, preambles, stages)
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
        Rule::clause_match => Stage::Match(visit_clause_match(child)),
        Rule::clause_insert => Stage::Insert(visit_clause_insert(child)),
        Rule::clause_put => Stage::Put(visit_clause_put(child)),
        Rule::clause_update => Stage::Update(visit_clause_update(child)),
        Rule::clause_delete => Stage::Delete(visit_clause_delete(child)),
        Rule::operator_stream => Stage::Modifier(visit_operator_stream(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_query_stage_terminal(node: Node<'_>) -> Stage {
    debug_assert_eq!(node.as_rule(), Rule::query_stage_terminal);
    let child = node.into_child();
    match child.as_rule() {
        Rule::clause_fetch => Stage::Fetch(visit_clause_fetch(child)),
        Rule::operator_reduce => Stage::Reduce(visit_operator_reduce(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

pub(super) fn visit_clause_match(node: Node<'_>) -> Match {
    debug_assert_eq!(node.as_rule(), Rule::clause_match);
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
        Rule::pattern_try => Pattern::Optional(visit_pattern_try(child)),
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

fn visit_pattern_try(node: Node<'_>) -> Optional {
    debug_assert_eq!(node.as_rule(), Rule::pattern_try);
    let span = node.span();
    let mut children = node.into_children();
    let patterns = children.skip_expected(Rule::TRY).consume_expected(Rule::patterns);
    debug_assert_eq!(children.try_consume_any(), None);
    Optional::new(span, visit_patterns(patterns))
}

fn visit_clause_insert(node: Node<'_>) -> Insert {
    debug_assert_eq!(node.as_rule(), Rule::clause_insert);
    let span = node.span();
    let statement_things = node.into_children().skip_expected(Rule::INSERT).map(visit_statement_thing).collect();
    Insert::new(span, statement_things)
}

fn visit_clause_put(node: Node<'_>) -> Put {
    debug_assert_eq!(node.as_rule(), Rule::clause_put);
    let span = node.span();
    let statement_things = node.into_children().skip_expected(Rule::PUT).map(visit_statement_thing).collect();
    Put::new(span, statement_things)
}

fn visit_clause_update(node: Node<'_>) -> Update {
    debug_assert_eq!(node.as_rule(), Rule::clause_update);
    let span = node.span();
    let statement_things = node.into_children().skip_expected(Rule::UPDATE).map(visit_statement_thing).collect();
    Update::new(span, statement_things)
}

fn visit_clause_delete(node: Node<'_>) -> Delete {
    debug_assert_eq!(node.as_rule(), Rule::clause_delete);
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

fn visit_clause_fetch(node: Node<'_>) -> Fetch {
    debug_assert_eq!(node.as_rule(), Rule::clause_fetch);
    let span = node.span();
    let mut children = node.into_children();
    let fetch_object = visit_fetch_object(children.skip_expected(Rule::FETCH).consume_expected(Rule::fetch_object));
    debug_assert_eq!(children.try_consume_any(), None);
    Fetch::new(span, fetch_object)
}

fn visit_fetch_some(node: Node<'_>) -> FetchEntry {
    debug_assert_eq!(node.as_rule(), Rule::fetch_some);
    let child = node.into_child();
    match child.as_rule() {
        Rule::fetch_object => FetchEntry::Object(visit_fetch_object(child)),
        Rule::fetch_list => FetchEntry::List(visit_fetch_list(child)),
        Rule::fetch_single => FetchEntry::Single(visit_fetch_single(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_fetch_single(node: Node<'_>) -> FetchSingle {
    debug_assert_eq!(node.as_rule(), Rule::fetch_single);
    let child = node.into_child();
    match child.as_rule() {
        Rule::fetch_attribute => FetchSingle::Attribute(visit_fetch_attribute(child)),
        Rule::query_pipeline => FetchSingle::Subquery(visit_query_pipeline(child)),
        Rule::expression => FetchSingle::Expression(visit_expression(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_fetch_attribute(node: Node<'_>) -> FetchAttribute {
    debug_assert_eq!(node.as_rule(), Rule::fetch_attribute);
    let span = node.span();
    let mut children = node.into_children();
    let owner = visit_var_named(children.consume_expected(Rule::var_named));
    let child = children.consume_any();
    let attribute = match child.as_rule() {
        Rule::label_list => TypeRefAny::List(visit_label_list(child)),
        Rule::label => TypeRefAny::Type(TypeRef::Named(NamedType::Label(visit_label(child)))),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };
    debug_assert_eq!(children.try_consume_any(), None);
    FetchAttribute::new(span, owner, attribute)
}

fn visit_fetch_object(node: Node<'_>) -> FetchObject {
    debug_assert_eq!(node.as_rule(), Rule::fetch_object);
    let span = node.span();
    let mut children = node.into_children();
    let body = visit_fetch_object_body(children.consume_expected(Rule::fetch_body));
    FetchObject::new(span, body)
}

fn visit_fetch_object_body(node: Node<'_>) -> FetchObjectBody {
    debug_assert_eq!(node.as_rule(), Rule::fetch_body);
    let child = node.into_child();
    match child.as_rule() {
        Rule::fetch_object_entries => {
            let entries = child.into_children().map(visit_fetch_object_entry).collect();
            FetchObjectBody::Entries(entries)
        }
        Rule::fetch_attributes_all => {
            let var = visit_var_named(child.into_children().consume_expected(Rule::var_named));
            FetchObjectBody::AttributesAll(var)
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_fetch_object_entry(node: Node<'_>) -> FetchObjectEntry {
    debug_assert_eq!(node.as_rule(), Rule::fetch_object_entry);
    let span = node.span();
    let mut children = node.into_children();
    let key = visit_fetch_key(children.consume_expected(Rule::fetch_key));
    let value = visit_fetch_some(children.consume_expected(Rule::fetch_some));
    debug_assert_eq!(children.try_consume_any(), None);
    FetchObjectEntry::new(span, key, value)
}

fn visit_fetch_key(node: Node<'_>) -> StringLiteral {
    debug_assert_eq!(node.as_rule(), Rule::fetch_key);
    visit_quoted_string_literal(node.into_child())
}

fn visit_fetch_list(node: Node<'_>) -> FetchList {
    debug_assert_eq!(node.as_rule(), Rule::fetch_list);
    let span = node.span();
    let stream = visit_fetch_stream(node.into_child());
    FetchList::new(span, stream)
}

fn visit_fetch_stream(node: Node<'_>) -> FetchStream {
    debug_assert_eq!(node.as_rule(), Rule::fetch_stream);
    let child = node.into_child();
    match child.as_rule() {
        Rule::fetch_attribute => FetchStream::Attribute(visit_fetch_attribute(child)),
        Rule::query_pipeline => FetchStream::Subquery(visit_query_pipeline(child)),
        Rule::expression_function => FetchStream::Function(visit_expression_function(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_operator_reduce(node: Node<'_>) -> Reduce {
    debug_assert_eq!(node.as_rule(), Rule::operator_reduce);
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
        Rule::LIST => {
            // TODO      vvvv rename
            ReduceValue::Stat(Stat::new(span, Aggregate::List, visit_var(children.consume_expected(Rule::var))))
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: keyword.to_string() }),
    }
}

pub(super) fn visit_operator_stream(node: Node<'_>) -> Modifier {
    debug_assert_eq!(node.as_rule(), Rule::operator_stream);
    let child = node.into_child();
    match child.as_rule() {
        Rule::operator_select => Modifier::Select(visit_operator_select(child)),
        Rule::operator_sort => Modifier::Sort(visit_operator_sort(child)),
        Rule::operator_offset => Modifier::Offset(visit_operator_offset(child)),
        Rule::operator_limit => Modifier::Limit(visit_operator_limit(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_operator_select(node: Node<'_>) -> Select {
    debug_assert_eq!(node.as_rule(), Rule::operator_select);
    let span = node.span();
    let mut children = node.into_children();
    let variables = visit_vars(children.skip_expected(Rule::SELECT).consume_expected(Rule::vars));
    debug_assert_eq!(children.try_consume_any(), None);
    Select::new(span, variables)
}

fn visit_operator_sort(node: Node<'_>) -> Sort {
    debug_assert_eq!(node.as_rule(), Rule::operator_sort);
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

fn visit_operator_offset(node: Node<'_>) -> Offset {
    debug_assert_eq!(node.as_rule(), Rule::operator_offset);
    let span = node.span();
    let mut children = node.into_children();
    let offset = visit_integer_literal(children.skip_expected(Rule::OFFSET).consume_expected(Rule::integer_literal));
    debug_assert_eq!(children.try_consume_any(), None);
    Offset::new(span, offset)
}

fn visit_operator_limit(node: Node<'_>) -> Limit {
    debug_assert_eq!(node.as_rule(), Rule::operator_limit);
    let span = node.span();
    let mut children = node.into_children();
    let limit = visit_integer_literal(children.skip_expected(Rule::LIMIT).consume_expected(Rule::integer_literal));
    debug_assert_eq!(children.try_consume_any(), None);
    Limit::new(span, limit)
}
