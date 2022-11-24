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

#[cfg(test)]
mod test;

use crate::{
    common::{
        date_time,
        error::ILLEGAL_GRAMMAR,
        string::{unescape_regex, unquote},
        token,
        validatable::Validatable,
        Result,
    },
    pattern::{
        ConceptVariable, ConceptVariableBuilder, Conjunction, Definable, Disjunction,
        HasConstraint, IsKeyAttribute, IsaConstraint, Label, Negation, OwnsConstraint, Pattern,
        PlaysConstraint, RelatesConstraint, RelationConstraint, RolePlayerConstraint,
        RuleDeclaration, RuleDefinition, SubConstraint, ThingConstrainable, ThingVariable,
        ThingVariableBuilder, Type, TypeConstrainable, TypeVariable, TypeVariableBuilder,
        UnboundVariable, Value, ValueConstraint, Variable,
    },
    query::{
        sorting, AggregateQueryBuilder, Query, Sorting, TypeQLDefine, TypeQLDelete, TypeQLInsert,
        TypeQLMatch, TypeQLMatchAggregate, TypeQLMatchGroup, TypeQLMatchGroupAggregate,
        TypeQLUndefine, TypeQLUpdate,
    },
};
use chrono::{NaiveDate, NaiveDateTime};
use pest::Parser;
use pest_derive::Parser;

#[derive(Parser)]
#[grammar = "parser/typeql.pest"]
struct TypeQLParser;

type SyntaxTree<'a> = pest::iterators::Pair<'a, Rule>;
type Children<'a> = pest::iterators::Pairs<'a, Rule>;

trait TreeChildren {
    type Items;
    fn children(self) -> Self::Items;
}

impl<'a> TreeChildren for SyntaxTree<'a> {
    type Items = Children<'a>;
    fn children(self) -> Self::Items {
        self.into_inner()
    }
}

trait RuleIterator {
    type Item;

    fn omit(&mut self, rule: Rule) -> &mut Self;

    fn consume(&mut self, rule: Rule) -> Self::Item;
    fn try_consume(&mut self, rule: Rule) -> Option<Self::Item>;

    fn consume_any(&mut self) -> Self::Item;
    fn try_consume_any(&mut self) -> Option<Self::Item>;
}

impl<'a, T: Iterator<Item = SyntaxTree<'a>>> RuleIterator for T {
    type Item = SyntaxTree<'a>;

    fn omit(&mut self, rule: Rule) -> &mut Self {
        self.consume(rule);
        self
    }

    fn consume(&mut self, _rule: Rule) -> Self::Item {
        let next = self.consume_any();
        assert_eq!(next.as_rule(), _rule);
        next
    }

    fn try_consume(&mut self, _rule: Rule) -> Option<Self::Item> {
        let next = self.try_consume_any();
        if next.is_some() {
            assert_eq!(next.as_ref().unwrap().as_rule(), _rule);
        }
        next
    }

    fn consume_any(&mut self) -> Self::Item {
        self.next().expect("attempting to consume from an empty iterator")
    }

    fn try_consume_any(&mut self) -> Option<Self::Item> {
        self.next()
    }
}

fn parse_single(rule: Rule, string: &str) -> Result<SyntaxTree> {
    Ok(TypeQLParser::parse(rule, string)?.consume_any())
}

fn unwrap_single(outer: SyntaxTree) -> SyntaxTree {
    outer.children().consume_any()
}

pub(crate) fn visit_eof_query(query: &str) -> Result<Query> {
    visit_query(unwrap_single(parse_single(Rule::eof_query, query)?)).validated()
}

pub(crate) fn visit_eof_queries(queries: &str) -> Result<impl Iterator<Item = Result<Query>> + '_> {
    let mut qs = Vec::new();
    for p in TypeQLParser::parse(Rule::eof_queries, queries)?
        .consume(Rule::eof_queries)
        .children()
        .filter(|r| matches!(r.as_rule(), Rule::query))
    {
        qs.push(visit_query(p).validated());
    }
    Ok(qs.into_iter())
}

pub(crate) fn visit_eof_pattern(pattern: &str) -> Result<Pattern> {
    visit_pattern(parse_single(Rule::eof_pattern, pattern)?.children().consume(Rule::pattern))
        .validated()
}

pub(crate) fn visit_eof_patterns(patterns: &str) -> Result<Vec<Pattern>> {
    visit_patterns(
        parse_single(Rule::eof_patterns, patterns)?.children().consume(Rule::eof_patterns),
    )
    .into_iter()
    .map(Validatable::validated)
    .collect()
}

pub(crate) fn visit_eof_definables(definables: &str) -> Result<Vec<Definable>> {
    visit_definables(parse_single(Rule::eof_definables, definables)?)
        .into_iter()
        .map(Validatable::validated)
        .collect()
}

pub(crate) fn visit_eof_variable(variable: &str) -> Result<Variable> {
    visit_pattern_variable(parse_single(Rule::eof_variable, variable)?).validated()
}

pub(crate) fn visit_eof_label(label: &str) -> Result<Label> {
    // TODO validation
    Ok(parse_single(Rule::label, label)?.as_str().into())
}

pub(crate) fn visit_eof_schema_rule(rule: &str) -> Result<RuleDefinition> {
    visit_schema_rule(parse_single(Rule::eof_schema_rule, rule)?).validated()
}

fn get_string(string: SyntaxTree) -> String {
    unquote(string.as_str())
}

fn get_regex(string: SyntaxTree) -> String {
    unescape_regex(&unquote(string.as_str()))
}

fn get_long(long: SyntaxTree) -> i64 {
    long.as_str().parse().expect(&ILLEGAL_GRAMMAR.format(&[long.as_str()]).message)
}

fn get_double(double: SyntaxTree) -> f64 {
    double.as_str().parse().expect(&ILLEGAL_GRAMMAR.format(&[double.as_str()]).message)
}

fn get_boolean(boolean: SyntaxTree) -> bool {
    boolean.as_str().parse().expect(&ILLEGAL_GRAMMAR.format(&[boolean.as_str()]).message)
}

fn get_date(date: SyntaxTree) -> NaiveDate {
    NaiveDate::parse_from_str(&date.as_str(), "%Y-%m-%d")
        .expect(&ILLEGAL_GRAMMAR.format(&[date.as_str()]).message)
}

fn get_date_time(date_time: SyntaxTree) -> NaiveDateTime {
    date_time::parse(&date_time.as_str())
        .expect(&ILLEGAL_GRAMMAR.format(&[date_time.as_str()]).message)
}

fn get_var(var: SyntaxTree) -> UnboundVariable {
    let name = var.as_str();

    assert!(name.len() > 1);
    assert!(name.starts_with('$'));
    let name = &name[1..];

    if name == "_" {
        UnboundVariable::anonymous()
    } else {
        UnboundVariable::named(String::from(name))
    }
}

fn get_isa_constraint(_isa: SyntaxTree, tree: SyntaxTree) -> IsaConstraint {
    match visit_type(tree) {
        Type::Label(label) => IsaConstraint::from(label),
        Type::Variable(var) => IsaConstraint::from(var),
    }
}

fn get_role_player_constraint(tree: SyntaxTree) -> RolePlayerConstraint {
    let mut tree = tree.children().rev();
    let player = get_var(tree.consume(Rule::player));
    if let Some(type_) = tree.try_consume(Rule::type_) {
        match visit_type(type_) {
            Type::Label(label) => RolePlayerConstraint::from((label, player)),
            Type::Variable(var) => RolePlayerConstraint::from((var, player)),
        }
    } else {
        RolePlayerConstraint::from(player)
    }
}

fn get_role_players(tree: SyntaxTree) -> Vec<RolePlayerConstraint> {
    tree.children().map(get_role_player_constraint).collect()
}

fn visit_query(tree: SyntaxTree) -> Query {
    let inner = unwrap_single(tree);
    match inner.as_rule() {
        Rule::query_match => visit_query_match(inner).into(),
        Rule::query_insert => visit_query_insert(inner).into(),
        Rule::query_delete => visit_query_delete(inner).into(),
        Rule::query_update => visit_query_update(inner).into(),
        Rule::query_define => visit_query_define(inner).into(),
        Rule::query_undefine => visit_query_undefine(inner).into(),
        Rule::query_match_aggregate => visit_query_match_aggregate(inner).into(),
        Rule::query_match_group => visit_query_match_group(inner).into(),
        Rule::query_match_group_agg => visit_query_match_group_agg(inner).into(),
        _ => unreachable!("{:?}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
    }
}

fn visit_query_define(tree: SyntaxTree) -> TypeQLDefine {
    TypeQLDefine::new(visit_definables(
        tree.children().omit(Rule::DEFINE).consume(Rule::definables),
    ))
}

fn visit_query_undefine(tree: SyntaxTree) -> TypeQLUndefine {
    TypeQLUndefine::new(visit_definables(
        tree.children().omit(Rule::UNDEFINE).consume(Rule::definables),
    ))
}

fn visit_query_insert(tree: SyntaxTree) -> TypeQLInsert {
    let mut children = tree.children();
    match children.consume_any().as_rule() {
        Rule::MATCH => TypeQLMatch::from_patterns(visit_patterns(children.consume(Rule::patterns)))
            .insert(visit_variable_things(
                children.omit(Rule::INSERT).consume(Rule::variable_things),
            )),
        Rule::INSERT => {
            TypeQLInsert::new(visit_variable_things(children.consume(Rule::variable_things)))
        }
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&children.as_str()])),
    }
}

fn visit_query_delete(tree: SyntaxTree) -> TypeQLDelete {
    let mut children = tree.children();
    TypeQLMatch::from_patterns(visit_patterns(children.omit(Rule::MATCH).consume(Rule::patterns)))
        .delete(visit_variable_things(children.omit(Rule::DELETE).consume(Rule::variable_things)))
}

fn visit_query_update(tree: SyntaxTree) -> TypeQLUpdate {
    let mut children = tree.children();
    visit_query_delete(children.consume(Rule::query_delete))
        .insert(visit_variable_things(children.omit(Rule::INSERT).consume(Rule::variable_things)))
}

fn visit_query_match(tree: SyntaxTree) -> TypeQLMatch {
    let mut children = tree.children();
    let mut match_query = TypeQLMatch::from_patterns(visit_patterns(
        children.omit(Rule::MATCH).consume(Rule::patterns),
    ));
    if let Some(modifiers) = children.try_consume(Rule::modifiers) {
        for modifier in modifiers.children() {
            match_query = match modifier.as_rule() {
                Rule::filter => match_query.filter(visit_filter(modifier)),
                Rule::sort => match_query.sort(visit_sort(modifier)),
                Rule::offset => match_query
                    .offset(get_long(modifier.children().omit(Rule::OFFSET).consume(Rule::LONG_))
                        as usize),
                Rule::limit => match_query
                    .limit(get_long(modifier.children().omit(Rule::LIMIT).consume(Rule::LONG_))
                        as usize),
                _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&modifier.as_str()])),
            };
        }
    }
    match_query
}

fn visit_query_match_aggregate(tree: SyntaxTree) -> TypeQLMatchAggregate {
    let mut children = tree.children();
    let match_query = visit_query_match(children.consume(Rule::query_match));
    let mut function = children.consume(Rule::match_aggregate).children();
    match visit_aggregate_method(function.consume(Rule::aggregate_method)) {
        token::Aggregate::Count => match_query.count(),
        method => match_query.aggregate(method, get_var(function.consume(Rule::VAR_))),
    }
}

fn visit_query_match_group(tree: SyntaxTree) -> TypeQLMatchGroup {
    let mut children = tree.children();
    visit_query_match(children.consume(Rule::query_match)).group(get_var(
        children.consume(Rule::match_group).children().omit(Rule::GROUP).consume(Rule::VAR_),
    ))
}

fn visit_query_match_group_agg(tree: SyntaxTree) -> TypeQLMatchGroupAggregate {
    let mut children = tree.children();
    let group = visit_query_match(children.consume(Rule::query_match)).group(get_var(
        children.consume(Rule::match_group).children().omit(Rule::GROUP).consume(Rule::VAR_),
    ));
    let mut function = children.consume(Rule::match_aggregate).children();
    match visit_aggregate_method(function.consume(Rule::aggregate_method)) {
        token::Aggregate::Count => group.count(),
        method => group.aggregate(method, get_var(function.consume(Rule::VAR_))),
    }
}

fn visit_filter(tree: SyntaxTree) -> Vec<UnboundVariable> {
    tree.children().omit(Rule::GET).map(get_var).collect()
}

fn visit_sort(tree: SyntaxTree) -> Sorting {
    Sorting::new(tree.children().omit(Rule::SORT).map(visit_var_order).collect())
}

fn visit_var_order(tree: SyntaxTree) -> sorting::OrderedVariable {
    let mut children = tree.children();
    sorting::OrderedVariable {
        var: get_var(children.consume(Rule::VAR_)),
        order: children.try_consume(Rule::ORDER_).map(|p| p.as_str().to_owned()),
    }
}

fn visit_aggregate_method(tree: SyntaxTree) -> token::Aggregate {
    token::Aggregate::from(tree.as_str())
}

fn visit_definables(tree: SyntaxTree) -> Vec<Definable> {
    tree.children().map(visit_definable).collect()
}

fn visit_definable(tree: SyntaxTree) -> Definable {
    let inner = unwrap_single(tree);
    match inner.as_rule() {
        Rule::variable_type => visit_variable_type(inner).into(),
        Rule::schema_rule => visit_schema_rule(inner).into(),
        Rule::schema_rule_declaration => visit_schema_rule_declaration(inner).into(),
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
    }
}

fn visit_patterns(tree: SyntaxTree) -> Vec<Pattern> {
    tree.children().map(visit_pattern).collect()
}

fn visit_pattern(tree: SyntaxTree) -> Pattern {
    let inner = unwrap_single(tree);
    match inner.as_rule() {
        Rule::pattern_variable => visit_pattern_variable(inner).into(),
        Rule::pattern_disjunction => visit_pattern_disjunction(inner).into(),
        Rule::pattern_conjunction => visit_pattern_conjunction(inner).into(),
        Rule::pattern_negation => visit_pattern_negation(inner).into(),
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
    }
}

fn visit_pattern_conjunction(tree: SyntaxTree) -> Conjunction {
    Conjunction::new(visit_patterns(unwrap_single(tree)))
}

fn visit_pattern_disjunction(tree: SyntaxTree) -> Disjunction {
    Disjunction::new(
        tree.children()
            .filter(|child| matches!(child.as_rule(), Rule::patterns))
            .map(visit_patterns)
            .map(|mut nested| match nested.len() {
                1 => nested.pop().unwrap(),
                _ => Conjunction::new(nested).into(),
            })
            .collect::<Vec<Pattern>>(),
    )
}

fn visit_pattern_negation(tree: SyntaxTree) -> Negation {
    let mut patterns = visit_patterns(tree.children().omit(Rule::NOT).consume(Rule::patterns));
    match patterns.len() {
        1 => Negation::new(patterns.pop().unwrap()),
        _ => Negation::new(Conjunction::new(patterns).into()),
    }
}

fn visit_pattern_variable(tree: SyntaxTree) -> Variable {
    let inner = unwrap_single(tree);
    match inner.as_rule() {
        Rule::variable_thing_any => visit_variable_thing_any(inner).into(),
        Rule::variable_type => visit_variable_type(inner).into(),
        Rule::variable_concept => visit_variable_concept(inner).into(),
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
    }
}

fn visit_variable_concept(tree: SyntaxTree) -> ConceptVariable {
    let mut children = tree.children();
    get_var(children.consume(Rule::VAR_)).is(get_var(children.omit(Rule::IS).consume(Rule::VAR_)))
}

fn visit_variable_type(tree: SyntaxTree) -> TypeVariable {
    let mut children = tree.children();
    let mut var_type = visit_type_any(children.consume(Rule::type_any)).into_type_variable();
    var_type = children.map(SyntaxTree::children).fold(var_type, |var_type, mut constraint| {
        match constraint.consume_any().as_rule() {
            Rule::ABSTRACT => var_type.abstract_(),
            Rule::OWNS => {
                let type_ = visit_type(constraint.consume(Rule::type_));
                let overridden = match constraint.peek().map(|child| child.as_rule()) {
                    Some(Rule::AS) => {
                        Some(visit_type(constraint.omit(Rule::AS).consume(Rule::type_)))
                    }
                    _ => None,
                };
                let is_key = IsKeyAttribute::from(constraint.try_consume(Rule::IS_KEY).is_some());
                var_type.constrain_owns(OwnsConstraint::from((type_, overridden, is_key)))
            }
            Rule::PLAYS => {
                let type_ = visit_type_scoped(constraint.consume(Rule::type_scoped));
                let overridden = match constraint.peek().map(|child| child.as_rule()) {
                    Some(Rule::AS) => {
                        Some(visit_type(constraint.omit(Rule::AS).consume(Rule::type_)))
                    }
                    _ => None,
                };
                var_type.constrain_plays(PlaysConstraint::from((type_, overridden)))
            }
            Rule::REGEX => var_type.regex(get_regex(constraint.consume(Rule::STRING_))),
            Rule::RELATES => {
                let type_ = visit_type(constraint.consume(Rule::type_));
                let overridden = match constraint.peek().map(|child| child.as_rule()) {
                    Some(Rule::AS) => {
                        Some(visit_type(constraint.omit(Rule::AS).consume(Rule::type_)))
                    }
                    _ => None,
                };
                var_type.constrain_relates(RelatesConstraint::from((type_, overridden)))
            }
            Rule::SUB_ => var_type.constrain_sub(SubConstraint::from(visit_type_any(
                constraint.consume(Rule::type_any),
            ))),
            Rule::TYPE => var_type.type_(visit_label_any(constraint.consume(Rule::label_any))),
            Rule::VALUE => var_type
                .value(token::ValueType::from(constraint.consume(Rule::value_type).as_str())),
            _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&constraint.as_str()])),
        }
    });

    var_type
}

fn visit_variable_things(tree: SyntaxTree) -> Vec<ThingVariable> {
    tree.children().map(visit_variable_thing_any).collect()
}

fn visit_variable_thing_any(tree: SyntaxTree) -> ThingVariable {
    let inner = unwrap_single(tree);
    match inner.as_rule() {
        Rule::variable_thing => visit_variable_thing(inner),
        Rule::variable_relation => visit_variable_relation(inner),
        Rule::variable_attribute => visit_variable_attribute(inner),
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
    }
}

fn visit_variable_thing(tree: SyntaxTree) -> ThingVariable {
    let mut children = tree.children();
    let mut var_thing = get_var(children.consume(Rule::VAR_)).into_thing();
    if children.peek().unwrap().as_rule() != Rule::attributes {
        let keyword = children.consume_any();
        var_thing = match keyword.as_rule() {
            Rule::IID => var_thing.iid(children.consume(Rule::IID_).as_str()),
            Rule::ISA_ => {
                var_thing.constrain_isa(get_isa_constraint(keyword, children.consume(Rule::type_)))
            }
            _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&children.as_str()])),
        }
    }
    if let Some(attributes) = children.try_consume_any() {
        var_thing = visit_attributes(attributes)
            .into_iter()
            .fold(var_thing, |var_thing, has| var_thing.constrain_has(has));
    }
    var_thing
}

fn visit_variable_relation(tree: SyntaxTree) -> ThingVariable {
    let mut children = tree.children();
    let mut relation = match children.peek().unwrap().as_rule() {
        Rule::VAR_ => get_var(children.consume(Rule::VAR_)),
        _ => UnboundVariable::hidden(),
    }
    .constrain_relation(visit_relation(children.consume(Rule::relation)));

    if matches!(children.peek().map(|child| child.as_rule()), Some(Rule::ISA_)) {
        let isa = children.consume(Rule::ISA_);
        let type_ = children.consume(Rule::type_);
        relation = relation.constrain_isa(get_isa_constraint(isa, type_));
    }
    if let Some(attributes) = children.try_consume_any() {
        relation = visit_attributes(attributes)
            .into_iter()
            .fold(relation, |relation, has| relation.constrain_has(has));
    }

    relation
}

fn visit_variable_attribute(tree: SyntaxTree) -> ThingVariable {
    let mut children = tree.children();
    let mut attribute = match children.peek().unwrap().as_rule() {
        Rule::VAR_ => get_var(children.consume(Rule::VAR_)),
        _ => UnboundVariable::hidden(),
    }
    .constrain_value(visit_predicate(children.consume(Rule::predicate)));

    if matches!(children.peek().map(|child| child.as_rule()), Some(Rule::ISA_)) {
        let isa = children.consume(Rule::ISA_);
        let type_ = children.consume(Rule::type_);
        attribute = attribute.constrain_isa(get_isa_constraint(isa, type_));
    }
    if let Some(attributes) = children.try_consume_any() {
        attribute = visit_attributes(attributes)
            .into_iter()
            .fold(attribute, |attribute, has| attribute.constrain_has(has));
    }

    attribute
}

fn visit_relation(tree: SyntaxTree) -> RelationConstraint {
    RelationConstraint::new(get_role_players(tree))
}

fn visit_attributes(tree: SyntaxTree) -> Vec<HasConstraint> {
    tree.children().map(visit_attribute).collect()
}

fn visit_attribute(tree: SyntaxTree) -> HasConstraint {
    let mut children = tree.children();

    match children.omit(Rule::HAS).peek().unwrap().as_rule() {
        Rule::label => {
            let label = children.consume(Rule::label).as_str().to_owned();
            match children.peek().unwrap().as_rule() {
                Rule::VAR_ => HasConstraint::from((label, get_var(children.consume(Rule::VAR_)))),
                Rule::predicate => {
                    HasConstraint::new((label, visit_predicate(children.consume(Rule::predicate))))
                }
                _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&children.as_str()])),
            }
        }
        Rule::VAR_ => HasConstraint::from(get_var(children.consume(Rule::VAR_))),
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&children.as_str()])),
    }
}

fn visit_predicate(tree: SyntaxTree) -> ValueConstraint {
    let mut children = tree.children();
    match children.peek().unwrap().as_rule() {
        Rule::value => {
            ValueConstraint::new(token::Predicate::Eq, visit_value(children.consume(Rule::value)))
        }
        Rule::predicate_equality => {
            ValueConstraint::new(token::Predicate::from(children.consume_any().as_str()), {
                let predicate_value = unwrap_single(children.consume(Rule::predicate_value));
                match predicate_value.as_rule() {
                    Rule::value => visit_value(predicate_value),
                    Rule::VAR_ => Value::from(get_var(predicate_value)),
                    _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&children.as_str()])),
                }
            })
        }
        Rule::predicate_substring => {
            let predicate = token::Predicate::from(children.consume_any().as_str());
            ValueConstraint::new(
                predicate,
                {
                    match predicate {
                        token::Predicate::Like => get_regex(children.consume(Rule::STRING_)),
                        token::Predicate::Contains => get_string(children.consume(Rule::STRING_)),
                        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&children.as_str()])),
                    }
                }
                .into(),
            )
        }
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&children.as_str()])),
    }
}

fn visit_schema_rule(tree: SyntaxTree) -> RuleDefinition {
    let mut children = tree.children();
    RuleDeclaration::new(Label::from(children.omit(Rule::RULE).consume(Rule::label).as_str()))
        .when(Conjunction::new(visit_patterns(children.omit(Rule::WHEN).consume(Rule::patterns))))
        .then(visit_variable_thing_any(children.omit(Rule::THEN).consume(Rule::variable_thing_any)))
}

fn visit_schema_rule_declaration(tree: SyntaxTree) -> RuleDeclaration {
    RuleDeclaration::new(Label::from(
        tree.children().omit(Rule::RULE).consume(Rule::label).as_str(),
    ))
}

fn visit_type_any(tree: SyntaxTree) -> Type {
    let inner = unwrap_single(tree);
    match inner.as_rule() {
        Rule::VAR_ => Type::Variable(get_var(inner)),
        Rule::type_ => visit_type(inner),
        Rule::type_scoped => visit_type_scoped(inner),
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
    }
}

fn visit_type_scoped(tree: SyntaxTree) -> Type {
    let inner = unwrap_single(tree);
    match inner.as_rule() {
        Rule::label_scoped => Type::Label(visit_label_scoped(inner)),
        Rule::VAR_ => Type::Variable(get_var(inner)),
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
    }
}

fn visit_type(tree: SyntaxTree) -> Type {
    let inner = unwrap_single(tree);
    match inner.as_rule() {
        Rule::label => Type::Label(inner.as_str().into()),
        Rule::VAR_ => Type::Variable(get_var(inner)),
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
    }
}

fn visit_label_any(tree: SyntaxTree) -> Label {
    let inner = unwrap_single(tree);
    match inner.as_rule() {
        Rule::label => Label::from(inner.as_str()),
        Rule::label_scoped => visit_label_scoped(inner),
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
    }
}

fn visit_label_scoped(tree: SyntaxTree) -> Label {
    let parts: Vec<String> = tree.as_str().split(':').map(String::from).collect();
    assert_eq!(parts.len(), 2);
    Label::from((parts[0].clone(), parts[1].clone()))
}

fn visit_value(tree: SyntaxTree) -> Value {
    let inner = unwrap_single(tree);
    match inner.as_rule() {
        Rule::STRING_ => Value::from(get_string(inner)),
        Rule::LONG_ => Value::from(get_long(inner)),
        Rule::DOUBLE_ => Value::from(get_double(inner)),
        Rule::BOOLEAN_ => Value::from(get_boolean(inner)),
        Rule::DATE_ => Value::from(get_date(inner).and_hms_opt(0, 0, 0).unwrap()),
        Rule::DATETIME_ => Value::from(get_date_time(inner)),
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
    }
}
