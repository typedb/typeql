/*
 * Copyright (C) 2022 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

use chrono::{NaiveDate, NaiveDateTime};
use pest::{
    Parser,
    pratt_parser::{Assoc, Op, PrattParser},
};
use pest_derive::Parser;

use crate::{
    common::{
        date_time,
        error::TypeQLError,
        Result,
        string::{unescape_regex, unquote},
        token,
        validatable::Validatable,
    },
    pattern::{
        Annotation, AssignConstraint, ConceptVariable, ConceptVariableBuilder, Conjunction, Constant, Definable,
        Disjunction, Expression, Function, HasConstraint, IsaConstraint, Label, Negation, Operation, OwnsConstraint,
        Pattern, PlaysConstraint, PredicateConstraint, RelatesConstraint, RelationConstraint, RolePlayerConstraint,
        RuleDeclaration, RuleDefinition, Statement, SubConstraint, ThingConstrainable, ThingStatement,
        ThingVariableBuilder, TypeConstrainable, TypeStatement, TypeVariableBuilder, UnboundConceptVariable,
        UnboundValueVariable, UnboundVariable, Value, ValueConstrainable, ValueVariable,
    },
    query::{
        AggregateQueryBuilder, MatchClause, Query, TypeQLDefine, TypeQLDelete, TypeQLGet,
        TypeQLGetAggregate, TypeQLGetGroup, TypeQLGetGroupAggregate, TypeQLInsert, TypeQLUndefine,
        TypeQLUpdate,
    },
};
use crate::query::{Limit, Offset};
use crate::query::modifier::{Modifiers, sorting, Sorting};

#[cfg(test)]
mod test;

#[derive(Parser)]
#[grammar = "parser/typeql.pest"]
pub(crate) struct TypeQLParser;

type Node<'a> = pest::iterators::Pair<'a, Rule>;
type ChildNodes<'a> = pest::iterators::Pairs<'a, Rule>;

trait IntoChildNodes<'a> {
    fn into_first_child(self) -> Self;
    fn into_children(self) -> ChildNodes<'a>;
}

impl<'a> IntoChildNodes<'a> for Node<'a> {
    fn into_first_child(self) -> Self {
        self.into_children().consume_any()
    }

    fn into_children(self) -> ChildNodes<'a> {
        self.into_inner()
    }
}

trait RuleMatcher<'a> {
    fn skip_expected(&mut self, rule: Rule) -> &mut Self;
    fn consume_expected(&mut self, rule: Rule) -> Node<'a>;
    fn peek_rule(&mut self) -> Option<Rule>;
    fn consume_if_matches(&mut self, rule: Rule) -> Option<Node<'a>>;
    fn consume_any(&mut self) -> Node<'a>;
    fn try_consume_any(&mut self) -> Option<Node<'a>>;
}

impl<'a, T: Iterator<Item=Node<'a>> + Clone> RuleMatcher<'a> for T {
    fn skip_expected(&mut self, rule: Rule) -> &mut Self {
        self.consume_expected(rule);
        self
    }

    fn consume_expected(&mut self, _rule: Rule) -> Node<'a> {
        let next = self.consume_any();
        assert_eq!(next.as_rule(), _rule);
        next
    }

    fn peek_rule(&mut self) -> Option<Rule> {
        self.clone().peekable().peek().map(|node| node.as_rule())
    }

    fn consume_if_matches(&mut self, rule: Rule) -> Option<Node<'a>> {
        (Some(rule) == self.peek_rule()).then(|| self.consume_any())
    }

    fn consume_any(&mut self) -> Node<'a> {
        self.next().expect("attempting to consume from an empty iterator")
    }

    fn try_consume_any(&mut self) -> Option<Node<'a>> {
        self.next()
    }
}

#[derive(Debug)]
enum Type {
    Label(Label),
    Variable(UnboundConceptVariable),
}

impl Type {
    pub fn into_type_statement(self) -> TypeStatement {
        match self {
            Self::Label(label) => UnboundConceptVariable::hidden().type_(label),
            Self::Variable(var) => var.into_type(),
        }
    }
}

fn parse_single(rule: Rule, string: &str) -> Result<Node> {
    Ok(TypeQLParser::parse(rule, string)?.consume_any())
}

pub(crate) fn visit_eof_query(query: &str) -> Result<Query> {
    visit_query(parse_single(Rule::eof_query, query)?.into_first_child()).validated()
}

pub(crate) fn visit_eof_queries(queries: &str) -> Result<impl Iterator<Item=Result<Query>> + '_> {
    Ok(TypeQLParser::parse(Rule::eof_queries, queries)?
        .consume_expected(Rule::eof_queries)
        .into_children()
        .filter(|child| matches!(child.as_rule(), Rule::query))
        .map(|query| visit_query(query).validated()))
}

pub(crate) fn visit_eof_pattern(pattern: &str) -> Result<Pattern> {
    visit_pattern(parse_single(Rule::eof_pattern, pattern)?.into_children().consume_expected(Rule::pattern)).validated()
}

pub(crate) fn visit_eof_patterns(patterns: &str) -> Result<Vec<Pattern>> {
    visit_patterns(parse_single(Rule::eof_patterns, patterns)?.into_children().consume_expected(Rule::patterns))
        .into_iter()
        .map(Validatable::validated)
        .collect()
}

pub(crate) fn visit_eof_definables(definables: &str) -> Result<Vec<Definable>> {
    visit_definables(parse_single(Rule::eof_definables, definables)?.into_children().consume_expected(Rule::definables))
        .into_iter()
        .map(Validatable::validated)
        .collect()
}

pub(crate) fn visit_eof_statement(statement: &str) -> Result<Statement> {
    visit_statement(
        parse_single(Rule::eof_statement, statement)?.into_children().consume_expected(Rule::statement),
    )
        .validated()
}

pub(crate) fn visit_eof_label(label: &str) -> Result<Label> {
    let parsed = parse_single(Rule::eof_label, label)?.into_first_child().as_str();
    if parsed != label {
        Err(TypeQLError::IllegalCharInLabel(label.to_string()))?;
    }
    Ok(parsed.into())
}

pub(crate) fn visit_eof_schema_rule(rule: &str) -> Result<RuleDefinition> {
    visit_schema_rule(parse_single(Rule::eof_schema_rule, rule)?).validated()
}

fn get_string_from_quoted(string: Node) -> String {
    unquote(string.as_str())
}

fn get_regex(string: Node) -> String {
    unescape_regex(&unquote(string.as_str()))
}

fn get_long(long: Node) -> i64 {
    long.as_str().parse().unwrap_or_else(|_| panic!("{}", TypeQLError::IllegalGrammar(long.to_string())))
}

fn get_double(double: Node) -> f64 {
    double.as_str().parse().unwrap_or_else(|_| panic!("{}", TypeQLError::IllegalGrammar(double.to_string())))
}

fn get_boolean(boolean: Node) -> bool {
    boolean.as_str().parse().unwrap_or_else(|_| panic!("{}", TypeQLError::IllegalGrammar(boolean.to_string())))
}

fn get_date(date: Node) -> NaiveDate {
    NaiveDate::parse_from_str(date.as_str(), "%Y-%m-%d")
        .unwrap_or_else(|_| panic!("{}", TypeQLError::IllegalGrammar(date.to_string())))
}

fn get_date_time(date_time: Node) -> NaiveDateTime {
    date_time::parse(date_time.as_str())
        .unwrap_or_else(|| panic!("{}", TypeQLError::IllegalGrammar(date_time.to_string())))
}

fn get_var(nodes: &mut ChildNodes) -> UnboundVariable {
    let var = nodes.consume_expected(Rule::VAR_);
    let name = var.as_str();

    let (prefix, _name) = name.split_at(1);
    match prefix {
        "$" => UnboundVariable::Concept(get_var_concept(var)),
        "?" => UnboundVariable::Value(get_var_value(var)),
        _ => unreachable!(),
    }
}

fn get_var_concept(var: Node) -> UnboundConceptVariable {
    let name = var.as_str();

    assert!(name.len() > 1);
    assert!(name.starts_with('$'));
    let name = &name[1..];

    if name == "_" {
        UnboundConceptVariable::anonymous()
    } else {
        UnboundConceptVariable::named(String::from(name))
    }
}

fn get_var_value(var: Node) -> UnboundValueVariable {
    let name = var.as_str();
    assert!(name.len() > 1);
    assert!(name.starts_with('?'));
    UnboundValueVariable::named(String::from(&name[1..]))
}

fn get_isa_constraint(isa: Node, node: Node) -> IsaConstraint {
    let is_explicit = matches!(isa.into_first_child().as_rule(), Rule::ISAX).into();
    match visit_type(node) {
        Type::Label(label) => IsaConstraint::from((label, is_explicit)),
        Type::Variable(var) => IsaConstraint::from((var, is_explicit)),
    }
}

fn get_role_player_constraint(node: Node) -> RolePlayerConstraint {
    let mut node = node.into_children().rev();
    let player = get_var_concept(node.consume_expected(Rule::player));
    if let Some(type_) = node.consume_if_matches(Rule::type_) {
        match visit_type(type_) {
            Type::Label(label) => RolePlayerConstraint::from((label, player)),
            Type::Variable(var) => RolePlayerConstraint::from((var, player)),
        }
    } else {
        RolePlayerConstraint::from(player)
    }
}

fn get_role_players(node: Node) -> Vec<RolePlayerConstraint> {
    node.into_children().map(get_role_player_constraint).collect()
}

fn visit_query(node: Node) -> Query {
    let mut children = node.into_children();
    match children.peek_rule().unwrap() {
        Rule::query_define => visit_query_define(&mut children).into(),
        Rule::query_undefine => visit_query_undefine(&mut children).into(),
        Rule::query_insert => visit_query_insert(&mut children).into(),
        Rule::query_delete => visit_query_delete(&mut children).into(),
        Rule::query_update => visit_query_update(&mut children).into(),
        Rule::query_get => visit_query_get(&mut children).into(),
        // Rule::query_fetch => visit_query_fetch(&mut children).into(),
        Rule::query_get_aggregate => visit_query_get_aggregate(&mut children).into(),
        Rule::query_get_group => visit_query_get_group(&mut children).into(),
        Rule::query_get_group_agg => visit_query_get_group_agg(&mut children).into(),
        _ => unreachable!("{:?}", TypeQLError::IllegalGrammar(node.to_string())),
    }
}

fn visit_query_define(nodes: &mut ChildNodes) -> TypeQLDefine {
    let node = nodes.consume_expected(Rule::query_define);
    let mut children = node.into_children().skip_expected(Rule::DEFINE);
    TypeQLDefine::new(visit_definables(&mut children))
}

fn visit_query_undefine(nodes: &mut ChildNodes) -> TypeQLUndefine {
    let node = nodes.consume_expected(Rule::query_undefine);
    let mut children = node.into_children().skip_expected(Rule::UNDEFINE);
    TypeQLUndefine::new(visit_definables(&mut children))
}

fn visit_query_insert(nodes: &mut ChildNodes) -> TypeQLInsert {
    let node = nodes.consume_expected(Rule::query_insert);
    let mut children = node.into_children();
    match children.peek_rule().unwrap() {
        Rule::clause_match => {
            let clause_match = visit_clause_match(&mut children);
            let clause_insert = visit_clause_insert(&mut children);
            let modifiers = visit_modifiers(&mut children);
            debug_assert!(children.try_consume_any().is_none());
            TypeQLInsert { clause_match: Some(clause_match), statements: clause_insert, modifiers }
        }
        Rule::clause_insert => {
            debug_assert!(children.try_consume_any().is_none());
            TypeQLInsert::new(visit_clause_insert(&mut children))
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(children.to_string())),
    }
}

fn visit_query_delete(nodes: &mut ChildNodes) -> TypeQLDelete {
    let node = nodes.consume_expected(Rule::query_delete);
    let mut children = node.into_children();
    let clause_match = visit_clause_match(&mut children);
    let clause_delete = visit_clause_delete(&mut children);
    let modifiers = visit_modifiers(&mut children);
    debug_assert!(children.try_consume_any().is_none());
    TypeQLDelete { clause_match, statements: clause_delete, modifiers }
}

fn visit_query_update(nodes: &mut ChildNodes) -> TypeQLUpdate {
    let node = nodes.consume_expected(Rule::query_update);
    let mut children = node.into_children();
    let query_delete = visit_query_delete(&mut children);
    let clause_insert = visit_clause_insert(&mut children);
    let modifiers = visit_modifiers(&mut children);
    debug_assert!(children.try_consume_any().is_none());
    TypeQLUpdate { query_delete, insert_statements: clause_insert, modifiers }
}

fn visit_query_get(nodes: &mut ChildNodes) -> TypeQLGet {
    let node = nodes.consume_expected(Rule::query_get);
    let mut children = node.into_children();
    let clause_match = visit_clause_match(&mut children);
    let clause_get = visit_clause_get(&mut children);
    let modifiers = visit_modifiers(&mut children);
    debug_assert!(children.try_consume_any().is_none());
    TypeQLGet { match_: clause_match, filter: clause_get, modifiers }
}

fn visit_clause_insert(nodes: &mut ChildNodes) -> Vec<ThingStatement> {
    let node = nodes.consume_expected(Rule::clause_insert);
    let mut children = node.into_children().skip_expected(Rule::INSERT);
    visit_statement_things(&mut children)
}

fn visit_clause_delete(nodes: &mut ChildNodes) -> Vec<ThingStatement> {
    let node = nodes.consume_expected(Rule::clause_delete);
    let mut children = node.into_children().skip_expected(Rule::DELETE);
    visit_statement_things(&mut children)
}

fn visit_clause_match(nodes: &mut ChildNodes) -> MatchClause {
    let node = nodes.consume_expected(Rule::clause_match);
    MatchClause::from_patterns(visit_patterns(
        node.into_children().skip_expected(Rule::MATCH).consume_expected(Rule::patterns)
    ))
}

fn visit_clause_get(nodes: &mut ChildNodes) -> Vec<UnboundVariable> {
    let node = nodes.consume_expected(Rule::clause_get);
    node.into_children().skip_expected(Rule::GET).map(get_var).collect()
}

fn visit_modifiers(nodes: &mut ChildNodes) -> Modifiers {
    let node = nodes.consume_expected(Rule::modifiers);
    let mut modifiers = Modifiers::default();
    for modifier in node.into_children() {
        match node.as_rule() {
            Rule::sort => modifiers.sorting = Some(visit_sort(modifier)),
            Rule::offset => modifiers.offset = Some(Offset {
                offset: get_long(
                    modifier.into_children().skip_expected(Rule::OFFSET).consume_expected(Rule::LONG_),
                ) as usize
            }),
            Rule::limit => modifiers.limit = Some(Limit {
                limit: get_long(modifier.into_children().skip_expected(Rule::LIMIT).consume_expected(Rule::LONG_)) as usize,
            }),
            _ => unreachable!("{}", TypeQLError::IllegalGrammar(modifier.to_string())),
        };
    }
    modifiers
}

fn visit_query_get_aggregate(nodes: &mut ChildNodes) -> TypeQLGetAggregate {
    let node = nodes.consume_expected(Rule::query_get_aggregate);
    let mut children = node.into_children();
    let match_query = visit_query_get(&mut children);
    let mut function = children.consume_expected(Rule::clause_aggregate).into_children();
    match visit_aggregate_method(function.consume_expected(Rule::aggregate_method)) {
        token::Aggregate::Count => match_query.count(),
        method => match_query.aggregate(method, get_var(&mut function)),
    }
}

fn visit_query_get_group(nodes: &mut ChildNodes) -> TypeQLGetGroup {
    let node = nodes.consume_expected(Rule::query_get_group);
    let mut children = node.into_children();
    visit_query_get(&mut children).group(get_var(
        &mut children
            .consume_expected(Rule::clause_group)
            .into_children()
            .skip_expected(Rule::GROUP),
    ))
}

fn visit_query_get_group_agg(nodes: &mut ChildNodes) -> TypeQLGetGroupAggregate {
    let node = nodes.consume_expected(Rule::query_get_group_agg);
    let mut children = node.into_children();
    let group = visit_query_get(&mut children).group(get_var(
        &mut children
            .consume_expected(Rule::clause_group)
            .into_children()
            .skip_expected(Rule::GROUP)
        ,
    ));
    let mut function = children.consume_expected(Rule::clause_aggregate).into_children();
    match visit_aggregate_method(function.consume_expected(Rule::aggregate_method)) {
        token::Aggregate::Count => group.count(),
        method => group.aggregate(method, get_var(&mut function)),
    }
}

fn visit_sort(node: Node) -> Sorting {
    Sorting::new(node.into_children().skip_expected(Rule::SORT).map(visit_var_order).collect())
}

fn visit_var_order(node: Node) -> sorting::OrderedVariable {
    let mut children = node.into_children();
    sorting::OrderedVariable {
        var: get_var(&mut children),
        order: children.consume_if_matches(Rule::ORDER_).map(|child| token::Order::from(child.as_str())),
    }
}

fn visit_aggregate_method(node: Node) -> token::Aggregate {
    token::Aggregate::from(node.as_str())
}

fn visit_definables(nodes: &mut ChildNodes) -> Vec<Definable> {
    let node = nodes.consume_expected(Rule::definables);
    node.into_children().map(visit_definable).collect()
}

fn visit_definable(node: Node) -> Definable {
    let child = node.into_first_child();
    match child.as_rule() {
        Rule::statement_type => visit_statement_type(child).into(),
        Rule::schema_rule => visit_schema_rule(child).into(),
        Rule::schema_rule_declaration => visit_schema_rule_declaration(child).into(),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(child.to_string())),
    }
}

fn visit_patterns(nodes: &mut ChildNodes) -> Vec<Pattern> {
    let node = nodes.consume_expected(Rule::patterns);
    let mut children = node.into_children();
    let mut patterns = vec![];
    // TODO: this is where a 'consume' based pattern becomes ugly
    while children.peek().is_some() {
        patterns.push(visit_pattern(&mut children));
    }
    patterns
}

fn visit_pattern(nodes: &mut ChildNodes) -> Pattern {
    let node = nodes.consume_expected(Rule::pattern);
    let mut children = node.into_children();
    let query = match children.peek_rule().unwrap() {
        Rule::statement => visit_statement(&mut children).into(),
        Rule::pattern_disjunction => visit_pattern_disjunction(&mut children).into(),
        Rule::pattern_conjunction => visit_pattern_conjunction(&mut children).into(),
        Rule::pattern_negation => visit_pattern_negation(&mut children).into(),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(node.to_string())),
    };
    debug_assert!(children.try_consume_any().is_none());
    query
}

fn visit_pattern_conjunction(nodes: &mut ChildNodes) -> Conjunction {
    let node = nodes.consume_expected(Rule::pattern_conjunction);
    let mut children = node.into_children();
    let conjunction = Conjunction::new(visit_patterns(&mut children));
    debug_assert!(children.try_consume_any().is_none());
    conjunction
}

fn visit_pattern_disjunction(nodes: &mut ChildNodes) -> Disjunction {
    let node = nodes.consume_expected(Rule::pattern_disjunction);
    let mut children = node.into_children();
    let patterns = vec![];
    while children.peek().is_some() {

    }
    let patterns = children
        .filter(|child| matches!(child.as_rule(), Rule::patterns))
        .map(visit_patterns)
        .map(|mut nested| match nested.len() {
            1 => nested.pop().unwrap(),
            _ => Conjunction::new(nested).into(),
        })
        .collect::<Vec<Pattern>>();
    Disjunction::new(patterns)
}

fn visit_pattern_negation(node: Node) -> Negation {
    let mut patterns = visit_patterns(node.into_children().skip_expected(Rule::NOT).consume_expected(Rule::patterns));
    match patterns.len() {
        1 => Negation::new(patterns.pop().unwrap()),
        _ => Negation::new(Conjunction::new(patterns).into()),
    }
}

fn visit_statement(node: Node) -> Statement {
    let child = node.into_first_child();
    match child.as_rule() {
        Rule::statement_thing_any => visit_statement_thing_any(child).into(),
        Rule::statement_type => visit_statement_type(child).into(),
        Rule::statement_concept => visit_statement_concept(child).into(),
        Rule::statement_value => visit_statement_value(child).into(),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(child.to_string())),
    }
}

fn visit_statement_concept(node: Node) -> ConceptVariable {
    let mut children = node.into_children();
    get_var_concept(children.consume_expected(Rule::VAR_CONCEPT_))
        .is(get_var_concept(children.skip_expected(Rule::IS).consume_expected(Rule::VAR_CONCEPT_)))
}

fn visit_statement_value(node: Node) -> ValueVariable {
    let mut children = node.into_children();
    let var_value = get_var_value(children.consume_expected(Rule::VAR_VALUE_));
    match children.peek_rule() {
        Some(Rule::ASSIGN) => {
            let expression = visit_expression(children.skip_expected(Rule::ASSIGN).consume_any());
            var_value.constrain_assign(AssignConstraint::from(expression))
        }
        Some(Rule::predicate) => {
            let predicate = visit_predicate(children.consume_any());
            var_value.constrain_predicate(predicate)
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(children.to_string())),
    }
}

fn visit_statement_type(node: Node) -> TypeStatement {
    let mut children = node.into_children();
    let mut var_type = visit_type_any(children.consume_expected(Rule::type_any)).into_type_statement();
    var_type = children.map(Node::into_children).fold(var_type, |var_type, mut constraint| {
        let keyword = constraint.consume_any();
        match keyword.as_rule() {
            Rule::ABSTRACT => var_type.abstract_(),
            Rule::OWNS => {
                let type_ = visit_type(constraint.consume_expected(Rule::type_)).into_type_statement();
                let overridden = constraint
                    .consume_if_matches(Rule::AS)
                    .map(|_| visit_type(constraint.consume_expected(Rule::type_)).into_type_statement());
                let annotations = visit_annotations_owns(constraint.consume_expected(Rule::annotations_owns));
                var_type.constrain_owns(OwnsConstraint::new(type_, overridden, annotations))
            }
            Rule::PLAYS => {
                let type_ = visit_type_scoped(constraint.consume_expected(Rule::type_scoped)).into_type_statement();
                let overridden = constraint
                    .consume_if_matches(Rule::AS)
                    .map(|_| visit_type(constraint.consume_expected(Rule::type_)).into_type_statement());
                var_type.constrain_plays(PlaysConstraint::new(type_, overridden))
            }
            Rule::REGEX => var_type.regex(get_regex(constraint.consume_expected(Rule::QUOTED_STRING))),
            Rule::RELATES => {
                let type_ = visit_type(constraint.consume_expected(Rule::type_)).into_type_statement();
                let overridden = constraint
                    .consume_if_matches(Rule::AS)
                    .map(|_| visit_type(constraint.consume_expected(Rule::type_)).into_type_statement());
                var_type.constrain_relates(RelatesConstraint::from((type_, overridden)))
            }
            Rule::SUB_ => var_type.constrain_sub(SubConstraint::from((
                visit_type_any(constraint.consume_expected(Rule::type_any)).into_type_statement(),
                matches!(keyword.into_first_child().as_rule(), Rule::SUBX).into(),
            ))),
            Rule::TYPE => var_type.type_(visit_label_any(constraint.consume_expected(Rule::label_any))),
            Rule::VALUE => {
                var_type.value(token::ValueType::from(constraint.consume_expected(Rule::value_type).as_str()))
            }
            _ => unreachable!("{}", TypeQLError::IllegalGrammar(constraint.to_string())),
        }
    });

    var_type
}

fn visit_annotations_owns(node: Node) -> Vec<Annotation> {
    node.into_children()
        .map(|annotation| match annotation.as_rule() {
            Rule::ANNOTATION_KEY => Annotation::Key,
            Rule::ANNOTATION_UNIQUE => Annotation::Unique,
            _ => unreachable!("{}", TypeQLError::IllegalGrammar(annotation.to_string())),
        })
        .collect()
}

fn visit_statement_things(nodes: &mut ChildNodes) -> Vec<ThingStatement> {
    let node = nodes.consume_expected(Rule::statement_things);
    node.into_children().map(visit_statement_thing_any).collect()
}

fn visit_statement_thing_any(node: Node) -> ThingStatement {
    let child = node.into_first_child();
    match child.as_rule() {
        Rule::statement_thing => visit_statement_thing(child),
        Rule::statement_relation => visit_statement_relation(child),
        Rule::statement_attribute => visit_statement_attribute(child),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(child.to_string())),
    }
}

fn visit_statement_thing(node: Node) -> ThingStatement {
    let mut children = node.into_children();
    let mut stmt_thing = get_var_concept(children.consume_expected(Rule::VAR_CONCEPT_)).into_thing();
    if children.peek_rule() != Some(Rule::attributes) {
        let keyword = children.consume_any();
        stmt_thing = match keyword.as_rule() {
            Rule::IID => stmt_thing.iid(children.consume_expected(Rule::IID_).as_str()),
            Rule::ISA_ => stmt_thing.constrain_isa(get_isa_constraint(keyword, children.consume_expected(Rule::type_))),
            _ => unreachable!("{}", TypeQLError::IllegalGrammar(children.to_string())),
        }
    }
    if let Some(attributes) = children.consume_if_matches(Rule::attributes) {
        stmt_thing = visit_attributes(attributes).into_iter()
            .fold(stmt_thing, |var_thing, has| var_thing.constrain_has(has));
    }
    stmt_thing
}

fn visit_statement_relation(node: Node) -> ThingStatement {
    let mut children = node.into_children();
    let mut relation = children
        .consume_if_matches(Rule::VAR_CONCEPT_)
        .map(get_var_concept)
        .unwrap_or_else(UnboundConceptVariable::hidden)
        .constrain_relation(visit_relation(children.consume_expected(Rule::relation)));

    if let Some(isa) = children.consume_if_matches(Rule::ISA_) {
        let type_ = children.consume_expected(Rule::type_);
        relation = relation.constrain_isa(get_isa_constraint(isa, type_));
    }
    if let Some(attributes) = children.consume_if_matches(Rule::attributes) {
        relation = visit_attributes(attributes).into_iter().fold(relation, |relation, has| relation.constrain_has(has));
    }

    relation
}

fn visit_statement_attribute(node: Node) -> ThingStatement {
    let mut children = node.into_children();
    let mut attribute = children
        .consume_if_matches(Rule::VAR_CONCEPT_)
        .map(get_var_concept)
        .unwrap_or_else(UnboundConceptVariable::hidden)
        .constrain_predicate(visit_predicate(children.consume_expected(Rule::predicate)));

    if let Some(isa) = children.consume_if_matches(Rule::ISA_) {
        let type_ = children.consume_expected(Rule::type_);
        attribute = attribute.constrain_isa(get_isa_constraint(isa, type_));
    }
    if let Some(attributes) = children.consume_if_matches(Rule::attributes) {
        attribute =
            visit_attributes(attributes).into_iter().fold(attribute, |attribute, has| attribute.constrain_has(has));
    }

    attribute
}

fn visit_relation(node: Node) -> RelationConstraint {
    RelationConstraint::new(get_role_players(node))
}

fn visit_attributes(node: Node) -> Vec<HasConstraint> {
    node.into_children().map(visit_attribute).collect()
}

fn visit_attribute(node: Node) -> HasConstraint {
    let mut children = node.into_children();

    match children.skip_expected(Rule::HAS).peek_rule() {
        Some(Rule::label) => {
            let label = children.consume_expected(Rule::label).as_str().to_owned();
            match children.peek_rule() {
                Some(Rule::VAR_) => HasConstraint::from((
                    label,
                    get_var(children.consume_expected(Rule::VAR_).into_children().consume_any()),
                )),
                Some(Rule::predicate) => {
                    HasConstraint::new((label, visit_predicate(children.consume_expected(Rule::predicate))))
                }
                _ => unreachable!("{}", TypeQLError::IllegalGrammar(children.to_string())),
            }
        }
        Some(Rule::VAR_CONCEPT_) => HasConstraint::from(get_var_concept(children.consume_expected(Rule::VAR_CONCEPT_))),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(children.to_string())),
    }
}

fn visit_predicate(node: Node) -> PredicateConstraint {
    let mut children = node.into_children();
    match children.peek_rule() {
        Some(Rule::constant) => PredicateConstraint::new(
            token::Predicate::Eq,
            visit_constant(children.consume_expected(Rule::constant)).into(),
        ),
        Some(Rule::predicate_equality) => PredicateConstraint::new(
            token::Predicate::from(children.consume_expected(Rule::predicate_equality).as_str()),
            {
                let predicate_value = children.consume_expected(Rule::value).into_first_child();
                match predicate_value.as_rule() {
                    Rule::constant => visit_constant(predicate_value).into(),
                    Rule::VAR_ => Value::from(get_var(&mut predicate_value.into_children())),
                    _ => unreachable!("{}", TypeQLError::IllegalGrammar(predicate_value.to_string())),
                }
            },
        ),
        Some(Rule::predicate_substring) => {
            let predicate = token::Predicate::from(children.consume_expected(Rule::predicate_substring).as_str());
            PredicateConstraint::new(
                predicate,
                {
                    match predicate {
                        token::Predicate::Like => get_regex(children.consume_expected(Rule::QUOTED_STRING)),
                        token::Predicate::Contains => get_string_from_quoted(children.consume_expected(Rule::QUOTED_STRING)),
                        _ => unreachable!("{}", TypeQLError::IllegalGrammar(children.to_string())),
                    }
                }
                    .into(),
            )
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(children.to_string())),
    }
}

fn visit_expression(node: Node) -> Expression {
    let pratt_parser: PrattParser<Rule> = PrattParser::new()
        .op(Op::infix(Rule::ADD, Assoc::Left) | Op::infix(Rule::SUBTRACT, Assoc::Left))
        .op(Op::infix(Rule::MULTIPLY, Assoc::Left)
            | Op::infix(Rule::DIVIDE, Assoc::Left)
            | Op::infix(Rule::MODULO, Assoc::Left))
        .op(Op::infix(Rule::POWER, Assoc::Right));

    pratt_parser
        .map_primary(|primary| match primary.as_rule() {
            Rule::VAR_ => Expression::Variable(get_var(primary)),
            Rule::constant => Expression::Constant(visit_constant(primary)),
            Rule::expression_function => Expression::Function(visit_function(primary)),
            Rule::expression_parenthesis => visit_expression(primary.into_children().consume_any()),
            _ => unreachable!("{}", TypeQLError::IllegalGrammar(primary.to_string())),
        })
        .map_infix(|left, op, right| {
            let op = match op.as_rule() {
                Rule::ADD => token::ArithmeticOperator::Add,
                Rule::SUBTRACT => token::ArithmeticOperator::Subtract,
                Rule::MULTIPLY => token::ArithmeticOperator::Multiply,
                Rule::DIVIDE => token::ArithmeticOperator::Divide,
                Rule::MODULO => token::ArithmeticOperator::Modulo,
                Rule::POWER => token::ArithmeticOperator::Power,
                _ => unreachable!("{}", TypeQLError::IllegalGrammar(op.to_string())),
            };
            Expression::Operation(Operation::new(op, left, right))
        })
        .parse(node.into_children())
}

fn visit_function(node: Node) -> Function {
    let mut children = node.into_children();
    let function_name = visit_function_name(children.consume_expected(Rule::expression_function_name));
    let args = children.map(|arg| Box::new(visit_expression(arg))).collect();
    Function { function_name, args }
}

fn visit_function_name(node: Node) -> token::Function {
    let child = node.into_first_child();
    match child.as_rule() {
        Rule::ABS => token::Function::Abs,
        Rule::CEIL => token::Function::Ceil,
        Rule::FLOOR => token::Function::Floor,
        Rule::MAX => token::Function::Max,
        Rule::MIN => token::Function::Min,
        Rule::ROUND => token::Function::Round,
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(child.to_string())),
    }
}

fn visit_schema_rule(node: Node) -> RuleDefinition {
    let mut children = node.into_children();
    RuleDeclaration::new(Label::from(children.skip_expected(Rule::RULE).consume_expected(Rule::label).as_str()))
        .when(Conjunction::new(visit_patterns(children.skip_expected(Rule::WHEN).consume_expected(Rule::patterns))))
        .then(visit_statement_thing_any(children.skip_expected(Rule::THEN).consume_expected(Rule::statement_thing_any)))
}

fn visit_schema_rule_declaration(node: Node) -> RuleDeclaration {
    RuleDeclaration::new(Label::from(
        node.into_children().skip_expected(Rule::RULE).consume_expected(Rule::label).as_str(),
    ))
}

fn visit_type_any(node: Node) -> Type {
    let child = node.into_first_child();
    match child.as_rule() {
        Rule::VAR_CONCEPT_ => Type::Variable(get_var_concept(child)),
        Rule::type_ => visit_type(child),
        Rule::type_scoped => visit_type_scoped(child),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(child.to_string())),
    }
}

fn visit_type_scoped(node: Node) -> Type {
    let child = node.into_first_child();
    match child.as_rule() {
        Rule::label_scoped => Type::Label(visit_label_scoped(child)),
        Rule::VAR_CONCEPT_ => Type::Variable(get_var_concept(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(child.to_string())),
    }
}

fn visit_type(node: Node) -> Type {
    let child = node.into_first_child();
    match child.as_rule() {
        Rule::label => Type::Label(child.as_str().into()),
        Rule::VAR_CONCEPT_ => Type::Variable(get_var_concept(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(child.to_string())),
    }
}

fn visit_label_any(node: Node) -> Label {
    let child = node.into_first_child();
    match child.as_rule() {
        Rule::label => Label::from(child.as_str()),
        Rule::label_scoped => visit_label_scoped(child),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(child.to_string())),
    }
}

fn visit_label_scoped(node: Node) -> Label {
    let parts: Vec<String> = node.as_str().split(':').map(String::from).collect();
    assert_eq!(parts.len(), 2);
    Label::from((parts[0].clone(), parts[1].clone()))
}

fn visit_constant(node: Node) -> Constant {
    let child = node.into_first_child();
    match child.as_rule() {
        Rule::QUOTED_STRING => Constant::from(get_string_from_quoted(child)),
        Rule::signed_long => Constant::from(get_long(child)),
        Rule::signed_double => Constant::from(get_double(child)),
        Rule::BOOLEAN_ => Constant::from(get_boolean(child)),
        Rule::DATE_ => Constant::from(get_date(child).and_hms_opt(0, 0, 0).unwrap()),
        Rule::DATETIME_ => Constant::from(get_date_time(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(child.to_string())),
    }
}
