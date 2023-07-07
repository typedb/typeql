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

use chrono::{NaiveDate, NaiveDateTime};
use pest::Parser;
use pest::pratt_parser::PrattParser;
use pest::pratt_parser::{Assoc::{Left, Right}, Op};
use pest_derive::Parser;

use crate::{
    common::{
        date_time,
        error::TypeQLError,
        string::{unescape_regex, unquote},
        token,
        validatable::Validatable,
        Result,
    },
    pattern::{
        Annotation, ConceptVariable, ConceptVariableBuilder, Conjunction, Definable, Disjunction, HasConstraint,
        IsaConstraint, Label, Negation, OwnsConstraint, Pattern, PlaysConstraint, RelatesConstraint,
        RelationConstraint, RolePlayerConstraint, RuleDeclaration, RuleDefinition, SubConstraint, ThingConstrainable,
        ThingVariable, ThingVariableBuilder, TypeConstrainable, TypeVariable, TypeVariableBuilder, UnboundVariable, UnboundConceptVariable, UnboundValueVariable,
        Value, ValueConstraint, ValueVariable, Variable,
    },
    query::{
        sorting, AggregateQueryBuilder, Query, Sorting, TypeQLDefine, TypeQLDelete, TypeQLInsert, TypeQLMatch,
        TypeQLMatchAggregate, TypeQLMatchGroup, TypeQLMatchGroupAggregate, TypeQLUndefine, TypeQLUpdate,
    },
};
use crate::pattern::{Constant, Expression, Function, Operation, Parenthesis};

#[derive(Parser)]
#[grammar = "parser/typeql.pest"]
struct TypeQLParser;

type SyntaxTree<'a> = pest::iterators::Pair<'a, Rule>;
type Children<'a> = pest::iterators::Pairs<'a, Rule>;

trait TreeChildren {
    type Items;
    fn into_child(self) -> Self;
    fn into_children(self) -> Self::Items;
}

impl<'a> TreeChildren for SyntaxTree<'a> {
    type Items = Children<'a>;

    fn into_child(self) -> Self {
        self.into_children().consume_any()
    }

    fn into_children(self) -> Self::Items {
        self.into_inner()
    }
}

trait RuleIterator {
    type Item;

    fn skip_expected(&mut self, rule: Rule) -> &mut Self;
    fn consume_expected(&mut self, rule: Rule) -> Self::Item;
    fn peek_rule(&mut self) -> Option<Rule>;
    fn consume_if_matches(&mut self, rule: Rule) -> Option<Self::Item>;
    fn consume_any(&mut self) -> Self::Item;
    fn try_consume_any(&mut self) -> Option<Self::Item>;
}

impl<'a, T: Iterator<Item = SyntaxTree<'a>> + Clone> RuleIterator for T {
    type Item = SyntaxTree<'a>;

    fn skip_expected(&mut self, rule: Rule) -> &mut Self {
        self.consume_expected(rule);
        self
    }

    fn consume_expected(&mut self, _rule: Rule) -> Self::Item {
        let next = self.consume_any();
        assert_eq!(next.as_rule(), _rule);
        next
    }

    fn peek_rule(&mut self) -> Option<Rule> {
        self.clone().peekable().peek().map(|tree| tree.as_rule())
    }

    fn consume_if_matches(&mut self, rule: Rule) -> Option<Self::Item> {
        (Some(rule) == self.peek_rule()).then(|| self.consume_any())
    }

    fn consume_any(&mut self) -> Self::Item {
        self.next().expect("attempting to consume from an empty iterator")
    }

    fn try_consume_any(&mut self) -> Option<Self::Item> {
        self.next()
    }
}

#[derive(Debug)]
enum Type {
    Label(Label),
    Variable(UnboundConceptVariable),
}

impl Type {
    pub fn into_type_variable(self) -> TypeVariable {
        match self {
            Self::Label(label) => UnboundConceptVariable::hidden().type_(label),
            Self::Variable(var) => var.into_type(),
        }
    }
}

fn parse_single(rule: Rule, string: &str) -> Result<SyntaxTree> {
    Ok(TypeQLParser::parse(rule, string)?.consume_any())
}

pub(crate) fn visit_eof_query(query: &str) -> Result<Query> {
    visit_query(parse_single(Rule::eof_query, query)?.into_child()).validated()
}

pub(crate) fn visit_eof_queries(queries: &str) -> Result<impl Iterator<Item = Result<Query>> + '_> {
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

pub(crate) fn visit_eof_variable(variable: &str) -> Result<Variable> {
    visit_pattern_variable(
        parse_single(Rule::eof_variable, variable)?.into_children().consume_expected(Rule::pattern_variable),
    )
    .validated()
}

pub(crate) fn visit_eof_label(label: &str) -> Result<Label> {
    let parsed = parse_single(Rule::eof_label, label)?.into_child().as_str();
    if parsed != label {
        Err(TypeQLError::IllegalCharInLabel(label.to_string()))?;
    }
    Ok(parsed.into())
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
    long.as_str().parse().unwrap_or_else(|_| panic!("{}", TypeQLError::IllegalGrammar(long.to_string())))
}

fn get_double(double: SyntaxTree) -> f64 {
    double.as_str().parse().unwrap_or_else(|_| panic!("{}", TypeQLError::IllegalGrammar(double.to_string())))
}

fn get_boolean(boolean: SyntaxTree) -> bool {
    boolean.as_str().parse().unwrap_or_else(|_| panic!("{}", TypeQLError::IllegalGrammar(boolean.to_string())))
}

fn get_date(date: SyntaxTree) -> NaiveDate {
    NaiveDate::parse_from_str(date.as_str(), "%Y-%m-%d")
        .unwrap_or_else(|_| panic!("{}", TypeQLError::IllegalGrammar(date.to_string())))
}

fn get_date_time(date_time: SyntaxTree) -> NaiveDateTime {
    date_time::parse(date_time.as_str())
        .unwrap_or_else(|| panic!("{}", TypeQLError::IllegalGrammar(date_time.to_string())))
}

fn get_var(var: SyntaxTree) -> UnboundVariable {
    let name = var.as_str();

    assert!(name.len() > 1);
    assert!(name.starts_with('$') || name.starts_with('?'));
    if name.starts_with('$') {
        let name = &name[1..];
        if name == "_" {
            UnboundVariable::Concept(UnboundConceptVariable::anonymous())
        } else {
            UnboundVariable::Concept(UnboundConceptVariable::named(String::from(name)))
        }
    } else {
        let name = &name[1..];
        UnboundVariable::Value(UnboundValueVariable::named(String::from(name)))
    }
}

fn get_var_concept(var: SyntaxTree) -> UnboundConceptVariable {
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

fn get_var_value(var: SyntaxTree) -> UnboundValueVariable {
    let name = var.as_str();

    assert!(name.len() > 1);
    assert!(name.starts_with('?'));
    let name = &name[1..];
    UnboundValueVariable::named(String::from(name))
}

fn get_isa_constraint(isa: SyntaxTree, tree: SyntaxTree) -> IsaConstraint {
    let is_explicit = matches!(isa.into_child().as_rule(), Rule::ISAX).into();
    match visit_type(tree) {
        Type::Label(label) => IsaConstraint::from((label, is_explicit)),
        Type::Variable(var) => IsaConstraint::from((var, is_explicit)),
    }
}

fn get_role_player_constraint(tree: SyntaxTree) -> RolePlayerConstraint {
    let mut tree = tree.into_children().rev();
    let player = get_var_concept(tree.consume_expected(Rule::player));
    if let Some(type_) = tree.consume_if_matches(Rule::type_) {
        match visit_type(type_) {
            Type::Label(label) => RolePlayerConstraint::from((label, player)),
            Type::Variable(var) => RolePlayerConstraint::from((var, player)),
        }
    } else {
        RolePlayerConstraint::from(player)
    }
}

fn get_role_players(tree: SyntaxTree) -> Vec<RolePlayerConstraint> {
    tree.into_children().map(get_role_player_constraint).collect()
}

fn visit_query(tree: SyntaxTree) -> Query {
    let child = tree.into_child();
    match child.as_rule() {
        Rule::query_match => visit_query_match(child).into(),
        Rule::query_insert => visit_query_insert(child).into(),
        Rule::query_delete => visit_query_delete(child).into(),
        Rule::query_update => visit_query_update(child).into(),
        Rule::query_define => visit_query_define(child).into(),
        Rule::query_undefine => visit_query_undefine(child).into(),
        Rule::query_match_aggregate => visit_query_match_aggregate(child).into(),
        Rule::query_match_group => visit_query_match_group(child).into(),
        Rule::query_match_group_agg => visit_query_match_group_agg(child).into(),
        _ => unreachable!("{:?}", TypeQLError::IllegalGrammar(child.to_string())),
    }
}

fn visit_query_define(tree: SyntaxTree) -> TypeQLDefine {
    TypeQLDefine::new(visit_definables(
        tree.into_children().skip_expected(Rule::DEFINE).consume_expected(Rule::definables),
    ))
}

fn visit_query_undefine(tree: SyntaxTree) -> TypeQLUndefine {
    TypeQLUndefine::new(visit_definables(
        tree.into_children().skip_expected(Rule::UNDEFINE).consume_expected(Rule::definables),
    ))
}

fn visit_query_insert(tree: SyntaxTree) -> TypeQLInsert {
    let mut children = tree.into_children();
    match children.consume_any().as_rule() {
        Rule::MATCH => TypeQLMatch::from_patterns(visit_patterns(children.consume_expected(Rule::patterns))).insert(
            visit_variable_things(children.skip_expected(Rule::INSERT).consume_expected(Rule::variable_things)),
        ),
        Rule::INSERT => TypeQLInsert::new(visit_variable_things(children.consume_expected(Rule::variable_things))),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(children.to_string())),
    }
}

fn visit_query_delete(tree: SyntaxTree) -> TypeQLDelete {
    let mut children = tree.into_children();
    TypeQLMatch::from_patterns(visit_patterns(children.skip_expected(Rule::MATCH).consume_expected(Rule::patterns)))
        .delete(visit_variable_things(children.skip_expected(Rule::DELETE).consume_expected(Rule::variable_things)))
}

fn visit_query_update(tree: SyntaxTree) -> TypeQLUpdate {
    let mut children = tree.into_children();
    visit_query_delete(children.consume_expected(Rule::query_delete))
        .insert(visit_variable_things(children.skip_expected(Rule::INSERT).consume_expected(Rule::variable_things)))
}

fn visit_query_match(tree: SyntaxTree) -> TypeQLMatch {
    let mut children = tree.into_children();
    let mut match_query = TypeQLMatch::from_patterns(visit_patterns(
        children.skip_expected(Rule::MATCH).consume_expected(Rule::patterns),
    ));
    if let Some(modifiers) = children.consume_if_matches(Rule::modifiers) {
        for modifier in modifiers.into_children() {
            match_query = match modifier.as_rule() {
                Rule::filter => match_query.filter(visit_filter(modifier)),
                Rule::sort => match_query.sort(visit_sort(modifier)),
                Rule::offset => match_query.offset(get_long(
                    modifier.into_children().skip_expected(Rule::OFFSET).consume_expected(Rule::LONG_),
                ) as usize),
                Rule::limit => match_query
                    .limit(get_long(modifier.into_children().skip_expected(Rule::LIMIT).consume_expected(Rule::LONG_))
                        as usize),
                _ => unreachable!("{}", TypeQLError::IllegalGrammar(modifier.to_string())),
            };
        }
    }
    match_query
}

fn visit_query_match_aggregate(tree: SyntaxTree) -> TypeQLMatchAggregate {
    let mut children = tree.into_children();
    let match_query = visit_query_match(children.consume_expected(Rule::query_match));
    let mut function = children.consume_expected(Rule::match_aggregate).into_children();
    match visit_aggregate_method(function.consume_expected(Rule::aggregate_method)) {
        token::Aggregate::Count => match_query.count(),
        method => match_query.aggregate(method, get_var(function.consume_expected(Rule::VAR_))),
    }
}

fn visit_query_match_group(tree: SyntaxTree) -> TypeQLMatchGroup {
    let mut children = tree.into_children();
    visit_query_match(children.consume_expected(Rule::query_match)).group(get_var(
        children
            .consume_expected(Rule::match_group)
            .into_children()
            .skip_expected(Rule::GROUP)
            .consume_expected(Rule::VAR_),
    ))
}

fn visit_query_match_group_agg(tree: SyntaxTree) -> TypeQLMatchGroupAggregate {
    let mut children = tree.into_children();
    let group = visit_query_match(children.consume_expected(Rule::query_match)).group(get_var(
        children
            .consume_expected(Rule::match_group)
            .into_children()
            .skip_expected(Rule::GROUP)
            .consume_expected(Rule::VAR_),
    ));
    let mut function = children.consume_expected(Rule::match_aggregate).into_children();
    match visit_aggregate_method(function.consume_expected(Rule::aggregate_method)) {
        token::Aggregate::Count => group.count(),
        method => group.aggregate(method, get_var(function.consume_expected(Rule::VAR_))),
    }
}

fn visit_filter(tree: SyntaxTree) -> Vec<UnboundVariable> {
    tree.into_children().skip_expected(Rule::GET).map(get_var).collect()
}

fn visit_sort(tree: SyntaxTree) -> Sorting {
    Sorting::new(tree.into_children().skip_expected(Rule::SORT).map(visit_var_order).collect())
}

fn visit_var_order(tree: SyntaxTree) -> sorting::OrderedVariable {
    let mut children = tree.into_children();
    sorting::OrderedVariable {
        var: get_var(children.consume_expected(Rule::VAR_)),
        order: children.consume_if_matches(Rule::ORDER_).map(|child| token::Order::from(child.as_str())),
    }
}

fn visit_aggregate_method(tree: SyntaxTree) -> token::Aggregate {
    token::Aggregate::from(tree.as_str())
}

fn visit_definables(tree: SyntaxTree) -> Vec<Definable> {
    tree.into_children().map(visit_definable).collect()
}

fn visit_definable(tree: SyntaxTree) -> Definable {
    let child = tree.into_child();
    match child.as_rule() {
        Rule::variable_type => visit_variable_type(child).into(),
        Rule::schema_rule => visit_schema_rule(child).into(),
        Rule::schema_rule_declaration => visit_schema_rule_declaration(child).into(),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(child.to_string())),
    }
}

fn visit_patterns(tree: SyntaxTree) -> Vec<Pattern> {
    tree.into_children().map(visit_pattern).collect()
}

fn visit_pattern(tree: SyntaxTree) -> Pattern {
    let child = tree.into_child();
    match child.as_rule() {
        Rule::pattern_variable => visit_pattern_variable(child).into(),
        Rule::pattern_disjunction => visit_pattern_disjunction(child).into(),
        Rule::pattern_conjunction => visit_pattern_conjunction(child).into(),
        Rule::pattern_negation => visit_pattern_negation(child).into(),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(child.to_string())),
    }
}

fn visit_pattern_conjunction(tree: SyntaxTree) -> Conjunction {
    Conjunction::new(visit_patterns(tree.into_child()))
}

fn visit_pattern_disjunction(tree: SyntaxTree) -> Disjunction {
    Disjunction::new(
        tree.into_children()
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
    let mut patterns = visit_patterns(tree.into_children().skip_expected(Rule::NOT).consume_expected(Rule::patterns));
    match patterns.len() {
        1 => Negation::new(patterns.pop().unwrap()),
        _ => Negation::new(Conjunction::new(patterns).into()),
    }
}

fn visit_pattern_variable(tree: SyntaxTree) -> Variable {
    let child = tree.into_child();
    match child.as_rule() {
        Rule::variable_thing_any => visit_variable_thing_any(child).into(),
        Rule::variable_type => visit_variable_type(child).into(),
        Rule::variable_concept => visit_variable_concept(child).into(),
        Rule::variable_value => visit_variable_value(child).into(),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(child.to_string())),
    }
}

fn visit_variable(tree: SyntaxTree) -> Variable {
    let child = tree.into_child();
    match child.as_rule() {
        Rule::variable_concept => visit_variable_concept(child).into(),
        Rule::variable_value => visit_variable_value(child).into(),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(child.to_string())),
    }
}

fn visit_variable_concept(tree: SyntaxTree) -> ConceptVariable {
    let mut children = tree.into_children();
    get_var_concept(children.consume_expected(Rule::VAR_CONCEPT_))
        .is(get_var_concept(children.skip_expected(Rule::IS).consume_expected(Rule::VAR_CONCEPT_)))
}

//FIXME: It's still just a copy of visit_variable_concept
fn visit_variable_value(tree: SyntaxTree) -> ValueVariable {
    let mut children = tree.into_children();
    get_var_value(children.consume_expected(Rule::VAR_VALUE_)).into_value()
        // .is(get_var(children.skip_expected(Rule::IS).consume_expected(Rule::VAR_CONCEPT_)))
}
fn visit_variable_type(tree: SyntaxTree) -> TypeVariable {
    let mut children = tree.into_children();
    let mut var_type = visit_type_any(children.consume_expected(Rule::type_any)).into_type_variable();
    var_type = children.map(SyntaxTree::into_children).fold(var_type, |var_type, mut constraint| {
        let keyword = constraint.consume_any();
        match keyword.as_rule() {
            Rule::ABSTRACT => var_type.abstract_(),
            Rule::OWNS => {
                let type_ = visit_type(constraint.consume_expected(Rule::type_)).into_type_variable();
                let overridden = constraint
                    .consume_if_matches(Rule::AS)
                    .map(|_| visit_type(constraint.consume_expected(Rule::type_)).into_type_variable());
                let annotations = visit_annotations_owns(constraint.consume_expected(Rule::annotations_owns));
                var_type.constrain_owns(OwnsConstraint::new(type_, overridden, annotations))
            }
            Rule::PLAYS => {
                let type_ = visit_type_scoped(constraint.consume_expected(Rule::type_scoped)).into_type_variable();
                let overridden = constraint
                    .consume_if_matches(Rule::AS)
                    .map(|_| visit_type(constraint.consume_expected(Rule::type_)).into_type_variable());
                var_type.constrain_plays(PlaysConstraint::new(type_, overridden))
            }
            Rule::REGEX => var_type.regex(get_regex(constraint.consume_expected(Rule::STRING_))),
            Rule::RELATES => {
                let type_ = visit_type(constraint.consume_expected(Rule::type_)).into_type_variable();
                let overridden = constraint
                    .consume_if_matches(Rule::AS)
                    .map(|_| visit_type(constraint.consume_expected(Rule::type_)).into_type_variable());
                var_type.constrain_relates(RelatesConstraint::from((type_, overridden)))
            }
            Rule::SUB_ => var_type.constrain_sub(SubConstraint::from((
                visit_type_any(constraint.consume_expected(Rule::type_any)).into_type_variable(),
                matches!(keyword.into_child().as_rule(), Rule::SUBX).into(),
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

fn visit_annotations_owns(tree: SyntaxTree) -> Vec<Annotation> {
    tree.into_children()
        .map(|annotation| match annotation.as_rule() {
            Rule::ANNOTATION_KEY => Annotation::Key,
            Rule::ANNOTATION_UNIQUE => Annotation::Unique,
            _ => unreachable!("{}", TypeQLError::IllegalGrammar(annotation.to_string())),
        })
        .collect()
}

fn visit_variable_things(tree: SyntaxTree) -> Vec<ThingVariable> {
    tree.into_children().map(visit_variable_thing_any).collect()
}

fn visit_variable_thing_any(tree: SyntaxTree) -> ThingVariable {
    let child = tree.into_child();
    match child.as_rule() {
        Rule::variable_thing => visit_variable_thing(child),
        Rule::variable_relation => visit_variable_relation(child),
        Rule::variable_attribute => visit_variable_attribute(child),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(child.to_string())),
    }
}

fn visit_variable_thing(tree: SyntaxTree) -> ThingVariable {
    let mut children = tree.into_children();
    let mut var_thing = get_var_concept(children.consume_expected(Rule::VAR_CONCEPT_)).into_thing();
    if children.peek_rule() != Some(Rule::attributes) {
        let keyword = children.consume_any();
        var_thing = match keyword.as_rule() {
            Rule::IID => var_thing.iid(children.consume_expected(Rule::IID_).as_str()),
            Rule::ISA_ => var_thing.constrain_isa(get_isa_constraint(keyword, children.consume_expected(Rule::type_))),
            _ => unreachable!("{}", TypeQLError::IllegalGrammar(children.to_string())),
        }
    }
    if let Some(attributes) = children.consume_if_matches(Rule::attributes) {
        var_thing =
            visit_attributes(attributes).into_iter().fold(var_thing, |var_thing, has| var_thing.constrain_has(has));
    }
    var_thing
}

fn visit_variable_relation(tree: SyntaxTree) -> ThingVariable {
    let mut children = tree.into_children();
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

fn visit_variable_attribute(tree: SyntaxTree) -> ThingVariable {
    let mut children = tree.into_children();
    let mut attribute = children
        .consume_if_matches(Rule::VAR_CONCEPT_)
        .map(get_var_concept)
        .unwrap_or_else(UnboundConceptVariable::hidden)
        .constrain_value(visit_predicate(children.consume_expected(Rule::predicate)));

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

fn visit_relation(tree: SyntaxTree) -> RelationConstraint {
    RelationConstraint::new(get_role_players(tree))
}

fn visit_attributes(tree: SyntaxTree) -> Vec<HasConstraint> {
    tree.into_children().map(visit_attribute).collect()
}

fn visit_attribute(tree: SyntaxTree) -> HasConstraint {
    let mut children = tree.into_children();

    match children.skip_expected(Rule::HAS).peek_rule() {
        Some(Rule::label) => {
            let label = children.consume_expected(Rule::label).as_str().to_owned();
            match children.peek_rule() {
                Some(Rule::VAR_) => {
                    HasConstraint::from((label, get_var_concept(children.consume_expected(Rule::VAR_).into_children().consume_expected(Rule::VAR_CONCEPT_))))
                },
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

fn visit_predicate(tree: SyntaxTree) -> ValueConstraint {
    let mut children = tree.into_children();
    match children.peek_rule() {
        Some(Rule::value) => {
            ValueConstraint::new(token::Predicate::Eq, visit_value(children.consume_expected(Rule::value)))
        }
        Some(Rule::predicate_equality) => ValueConstraint::new(
            token::Predicate::from(children.consume_expected(Rule::predicate_equality).as_str()),
            {
                let predicate_value = children.consume_expected(Rule::predicate_value).into_child();
                match predicate_value.as_rule() {
                    Rule::value => visit_value(predicate_value),
                    Rule::VAR_ => Value::from(get_var_concept(predicate_value.into_children().consume_expected(Rule::VAR_CONCEPT_))),
                    _ => unreachable!("{}", TypeQLError::IllegalGrammar(predicate_value.to_string())),
                }
            },
        ),
        Some(Rule::predicate_substring) => {
            let predicate = token::Predicate::from(children.consume_expected(Rule::predicate_substring).as_str());
            ValueConstraint::new(
                predicate,
                {
                    match predicate {
                        token::Predicate::Like => get_regex(children.consume_expected(Rule::STRING_)),
                        token::Predicate::Contains => get_string(children.consume_expected(Rule::STRING_)),
                        _ => unreachable!("{}", TypeQLError::IllegalGrammar(children.to_string())),
                    }
                }
                .into(),
            )
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(children.to_string())),
    }
}

fn visit_expression(tree: SyntaxTree) -> Expression {
    let pratt_parser: PrattParser<Rule> = PrattParser::new()
        .op(Op::infix(Rule::ADD, Left) | Op::infix(Rule::SUBTRACT, Left))
        .op(Op::infix(Rule::MULTIPLY, Left) | Op::infix(Rule::DIVIDE, Left) | Op::infix(Rule::MODULO, Left))
        .op(Op::infix(Rule::POWER, Right) );

    pratt_parser
        .map_primary(|primary| match primary.as_rule() {
            Rule::value => Expression::Constant(Constant { value: visit_value(primary) }),
            _ => unreachable!("{}", TypeQLError::IllegalGrammar(primary.to_string())),
        })
        .map_infix(|left, op, right| {
            let op = match op.as_rule() {
                Rule::ADD => token::Operation::Add,
                Rule::SUBTRACT => token::Operation::Subtract,
                Rule::MULTIPLY => token::Operation::Multiply,
                Rule::DIVIDE => token::Operation::Divide,
                Rule::MODULO => token::Operation::Modulo,
                Rule::POWER => token::Operation::Power,
                _ => unreachable!("{}", TypeQLError::IllegalGrammar(op.to_string())),
            };
            Expression::Operation(Operation {
                op,
                left: Box::new(left),
                right: Box::new(right),
            })
        })
        .parse(tree.into_children())

    // todo!();
}

fn visit_schema_rule(tree: SyntaxTree) -> RuleDefinition {
    let mut children = tree.into_children();
    RuleDeclaration::new(Label::from(children.skip_expected(Rule::RULE).consume_expected(Rule::label).as_str()))
        .when(Conjunction::new(visit_patterns(children.skip_expected(Rule::WHEN).consume_expected(Rule::patterns))))
        .then(visit_variable_thing_any(children.skip_expected(Rule::THEN).consume_expected(Rule::variable_thing_any)))
}

fn visit_schema_rule_declaration(tree: SyntaxTree) -> RuleDeclaration {
    RuleDeclaration::new(Label::from(
        tree.into_children().skip_expected(Rule::RULE).consume_expected(Rule::label).as_str(),
    ))
}

fn visit_type_any(tree: SyntaxTree) -> Type {
    let child = tree.into_child();
    match child.as_rule() {
        Rule::VAR_CONCEPT_ => Type::Variable(get_var_concept(child)),
        Rule::type_ => visit_type(child),
        Rule::type_scoped => visit_type_scoped(child),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(child.to_string())),
    }
}

fn visit_type_scoped(tree: SyntaxTree) -> Type {
    let child = tree.into_child();
    match child.as_rule() {
        Rule::label_scoped => Type::Label(visit_label_scoped(child)),
        Rule::VAR_CONCEPT_ => Type::Variable(get_var_concept(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(child.to_string())),
    }
}

fn visit_type(tree: SyntaxTree) -> Type {
    let child = tree.into_child();
    match child.as_rule() {
        Rule::label => Type::Label(child.as_str().into()),
        Rule::VAR_CONCEPT_ => Type::Variable(get_var_concept(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(child.to_string())),
    }
}

fn visit_label_any(tree: SyntaxTree) -> Label {
    let child = tree.into_child();
    match child.as_rule() {
        Rule::label => Label::from(child.as_str()),
        Rule::label_scoped => visit_label_scoped(child),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(child.to_string())),
    }
}

fn visit_label_scoped(tree: SyntaxTree) -> Label {
    let parts: Vec<String> = tree.as_str().split(':').map(String::from).collect();
    assert_eq!(parts.len(), 2);
    Label::from((parts[0].clone(), parts[1].clone()))
}

fn visit_value(tree: SyntaxTree) -> Value {
    let child = tree.into_child();
    match child.as_rule() {
        Rule::STRING_ => Value::from(get_string(child)),
        Rule::signed_long => Value::from(get_long(child)),
        Rule::signed_double => Value::from(get_double(child)),
        Rule::BOOLEAN_ => Value::from(get_boolean(child)),
        Rule::DATE_ => Value::from(get_date(child).and_hms_opt(0, 0, 0).unwrap()),
        Rule::DATETIME_ => Value::from(get_date_time(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar(child.to_string())),
    }
}
