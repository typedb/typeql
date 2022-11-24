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
use pest::{iterators::Pair, Parser};
use pest_derive::Parser;

#[derive(Parser)]
#[grammar = "parser/typeql.pest"]
struct TypeQLParser;

fn parse_single(rule: Rule, string: &str) -> Result<Pair<Rule>> {
    Ok(TypeQLParser::parse(rule, string)?.next().unwrap())
}

fn unwrap_single(outer: Pair<Rule>) -> Pair<Rule> {
    outer.into_inner().next().unwrap()
}

pub(crate) fn visit_eof_query(query: &str) -> Result<Query> {
    visit_query(unwrap_single(parse_single(Rule::eof_query, query)?)).validated()
}

pub(crate) fn visit_eof_queries(queries: &str) -> Result<impl Iterator<Item = Result<Query>> + '_> {
    let mut qs = Vec::new();
    for p in TypeQLParser::parse(Rule::eof_queries, queries)?
        .next()
        .unwrap()
        .into_inner()
        .filter(|r| matches!(r.as_rule(), Rule::query))
    {
        qs.push(visit_query(p).validated());
    }
    Ok(qs.into_iter())
}

pub(crate) fn visit_eof_pattern(pattern: &str) -> Result<Pattern> {
    visit_pattern(parse_single(Rule::eof_pattern, pattern)?.into_inner().next().unwrap())
        .validated()
}

pub(crate) fn visit_eof_patterns(patterns: &str) -> Result<Vec<Pattern>> {
    visit_patterns(parse_single(Rule::eof_patterns, patterns)?.into_inner().next().unwrap())
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

fn get_string(string: Pair<Rule>) -> String {
    unquote(string.as_str())
}

fn get_regex(string: Pair<Rule>) -> String {
    unescape_regex(&unquote(string.as_str()))
}

fn get_long(long: Pair<Rule>) -> i64 {
    long.as_str().parse().expect(&ILLEGAL_GRAMMAR.format(&[long.as_str()]).message)
}

fn get_double(double: Pair<Rule>) -> f64 {
    double.as_str().parse().expect(&ILLEGAL_GRAMMAR.format(&[double.as_str()]).message)
}

fn get_boolean(boolean: Pair<Rule>) -> bool {
    boolean.as_str().parse().expect(&ILLEGAL_GRAMMAR.format(&[boolean.as_str()]).message)
}

fn get_date(date: Pair<Rule>) -> NaiveDate {
    NaiveDate::parse_from_str(&date.as_str(), "%Y-%m-%d")
        .expect(&ILLEGAL_GRAMMAR.format(&[date.as_str()]).message)
}

fn get_date_time(date_time: Pair<Rule>) -> NaiveDateTime {
    date_time::parse(&date_time.as_str())
        .expect(&ILLEGAL_GRAMMAR.format(&[date_time.as_str()]).message)
}

fn get_var(var: Pair<Rule>) -> UnboundVariable {
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

fn get_isa_constraint(_isa: Pair<Rule>, tree: Pair<Rule>) -> IsaConstraint {
    match visit_type(tree) {
        Type::Label(label) => IsaConstraint::from(label),
        Type::Variable(var) => IsaConstraint::from(var),
    }
}

fn get_role_player_constraint(tree: Pair<Rule>) -> RolePlayerConstraint {
    let mut tree = tree.into_inner().rev();
    let player = get_var(tree.next().unwrap());
    if let Some(type_) = tree.next() {
        match visit_type(type_) {
            Type::Label(label) => RolePlayerConstraint::from((label, player)),
            Type::Variable(var) => RolePlayerConstraint::from((var, player)),
        }
    } else {
        RolePlayerConstraint::from(player)
    }
}

fn get_role_players(tree: Pair<Rule>) -> Vec<RolePlayerConstraint> {
    tree.into_inner().map(get_role_player_constraint).collect()
}

fn visit_query(tree: Pair<Rule>) -> Query {
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

fn visit_query_define(tree: Pair<Rule>) -> TypeQLDefine {
    TypeQLDefine::new(visit_definables(tree.into_inner().skip(1).next().unwrap()))
}

fn visit_query_undefine(tree: Pair<Rule>) -> TypeQLUndefine {
    TypeQLUndefine::new(visit_definables(tree.into_inner().skip(1).next().unwrap()))
}

fn visit_query_insert(tree: Pair<Rule>) -> TypeQLInsert {
    let mut inner = tree.into_inner();
    match inner.next().unwrap().as_rule() {
        Rule::MATCH => TypeQLMatch::from_patterns(visit_patterns(inner.next().unwrap()))
            .insert(visit_variable_things(inner.skip(1).next().unwrap())),
        Rule::INSERT => TypeQLInsert::new(visit_variable_things(inner.next().unwrap())),
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
    }
}

fn visit_query_delete(tree: Pair<Rule>) -> TypeQLDelete {
    let inner = tree.into_inner().collect::<Vec<_>>();
    TypeQLMatch::from_patterns(visit_patterns(inner.get(1).unwrap().clone()))
        .delete(visit_variable_things(inner.get(3).unwrap().clone()))
}

fn visit_query_update(tree: Pair<Rule>) -> TypeQLUpdate {
    let mut inner = tree.into_inner();
    visit_query_delete(inner.next().unwrap())
        .insert(visit_variable_things(inner.skip(1).next().unwrap()))
}

fn visit_query_match(tree: Pair<Rule>) -> TypeQLMatch {
    let mut inner = tree.into_inner().skip(1);
    let mut match_query = TypeQLMatch::from_patterns(visit_patterns(inner.next().unwrap()));
    if let Some(modifiers) = inner.next() {
        for modifier in modifiers.into_inner() {
            match_query = match modifier.as_rule() {
                Rule::filter => match_query.filter(visit_filter(modifier)),
                Rule::sort => match_query.sort(visit_sort(modifier)),
                Rule::offset => match_query
                    .offset(get_long(modifier.into_inner().skip(1).next().unwrap()) as usize),
                Rule::limit => match_query
                    .limit(get_long(modifier.into_inner().skip(1).next().unwrap()) as usize),
                _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&modifier.as_str()])),
            };
        }
    }
    match_query
}

fn visit_query_match_aggregate(tree: Pair<Rule>) -> TypeQLMatchAggregate {
    let mut inner = tree.into_inner();
    let match_query = visit_query_match(inner.next().unwrap());
    let mut function = inner.next().unwrap().into_inner();
    match visit_aggregate_method(function.next().unwrap()) {
        token::Aggregate::Count => match_query.count(),
        method => match_query.aggregate(method, get_var(function.next().unwrap())),
    }
}

fn visit_query_match_group(tree: Pair<Rule>) -> TypeQLMatchGroup {
    let mut inner = tree.into_inner();
    visit_query_match(inner.next().unwrap())
        .group(get_var(inner.next().unwrap().into_inner().skip(1).next().unwrap()))
}

fn visit_query_match_group_agg(tree: Pair<Rule>) -> TypeQLMatchGroupAggregate {
    let mut inner = tree.into_inner();
    let group = visit_query_match(inner.next().unwrap())
        .group(get_var(inner.next().unwrap().into_inner().skip(1).next().unwrap()));
    let mut function = inner.next().unwrap().into_inner();
    match visit_aggregate_method(function.next().unwrap()) {
        token::Aggregate::Count => group.count(),
        method => group.aggregate(method, get_var(function.next().unwrap())),
    }
}

fn visit_filter(tree: Pair<Rule>) -> Vec<UnboundVariable> {
    tree.into_inner().skip(1).map(get_var).collect()
}

fn visit_sort(tree: Pair<Rule>) -> Sorting {
    Sorting::new(tree.into_inner().skip(1).map(visit_var_order).collect())
}

fn visit_var_order(tree: Pair<Rule>) -> sorting::OrderedVariable {
    let mut inner = tree.into_inner();
    sorting::OrderedVariable {
        var: get_var(inner.next().unwrap()),
        order: inner.next().map(|p| p.as_str().to_owned()),
    }
}

fn visit_aggregate_method(tree: Pair<Rule>) -> token::Aggregate {
    token::Aggregate::from(tree.as_str())
}

fn visit_definables(tree: Pair<Rule>) -> Vec<Definable> {
    tree.into_inner().map(visit_definable).collect()
}

fn visit_definable(tree: Pair<Rule>) -> Definable {
    let inner = unwrap_single(tree);
    match inner.as_rule() {
        Rule::variable_type => visit_variable_type(inner).into(),
        Rule::schema_rule => visit_schema_rule(inner).into(),
        Rule::schema_rule_declaration => visit_schema_rule_declaration(inner).into(),
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
    }
}

fn visit_patterns(tree: Pair<Rule>) -> Vec<Pattern> {
    tree.into_inner().map(visit_pattern).collect()
}

fn visit_pattern(tree: Pair<Rule>) -> Pattern {
    let inner = unwrap_single(tree);
    match inner.as_rule() {
        Rule::pattern_variable => visit_pattern_variable(inner).into(),
        Rule::pattern_disjunction => visit_pattern_disjunction(inner).into(),
        Rule::pattern_conjunction => visit_pattern_conjunction(inner).into(),
        Rule::pattern_negation => visit_pattern_negation(inner).into(),
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
    }
}

fn visit_pattern_conjunction(tree: Pair<Rule>) -> Conjunction {
    Conjunction::new(visit_patterns(unwrap_single(tree)))
}

fn visit_pattern_disjunction(tree: Pair<Rule>) -> Disjunction {
    Disjunction::new(
        tree.into_inner()
            .filter(|p| matches!(p.as_rule(), Rule::patterns))
            .map(visit_patterns)
            .map(|mut nested| match nested.len() {
                1 => nested.pop().unwrap(),
                _ => Conjunction::new(nested).into(),
            })
            .collect::<Vec<Pattern>>(),
    )
}

fn visit_pattern_negation(tree: Pair<Rule>) -> Negation {
    let mut patterns = visit_patterns(tree.into_inner().skip(1).next().unwrap());
    match patterns.len() {
        1 => Negation::new(patterns.pop().unwrap()),
        _ => Negation::new(Conjunction::new(patterns).into()),
    }
}

fn visit_pattern_variable(tree: Pair<Rule>) -> Variable {
    let inner = unwrap_single(tree);
    match inner.as_rule() {
        Rule::variable_thing_any => visit_variable_thing_any(inner).into(),
        Rule::variable_type => visit_variable_type(inner).into(),
        Rule::variable_concept => visit_variable_concept(inner).into(),
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
    }
}

fn visit_variable_concept(tree: Pair<Rule>) -> ConceptVariable {
    let mut inner = tree.into_inner();
    get_var(inner.next().unwrap()).is(get_var(inner.skip(1).next().unwrap()))
}

fn visit_variable_type(tree: Pair<Rule>) -> TypeVariable {
    let mut inner = tree.into_inner();
    let mut var_type = visit_type_any(inner.next().unwrap()).into_type_variable();
    var_type = inner.map(Pair::into_inner).fold(var_type, |var_type, mut constraint| {
        match constraint.next().unwrap().as_rule() {
            Rule::ABSTRACT => var_type.abstract_(),
            Rule::OWNS => {
                let mut constraint = constraint.collect::<Vec<_>>();
                let type_ = visit_type(constraint.get(0).unwrap().clone());
                let overridden = match constraint.get(1).map(|x| x.as_rule()) {
                    Some(Rule::AS) => Some(visit_type(constraint.get(2).unwrap().clone())),
                    _ => None,
                };
                let is_key = IsKeyAttribute::from(
                    constraint.pop().map(|x| x.as_rule() == Rule::IS_KEY).unwrap_or(false),
                );
                var_type.constrain_owns(OwnsConstraint::from((type_, overridden, is_key)))
            }
            Rule::PLAYS => {
                let type_ = visit_type_scoped(constraint.next().unwrap());
                let overridden = match constraint.peek().map(|x| x.as_rule()) {
                    Some(Rule::AS) => Some(visit_type(constraint.skip(1).next().unwrap())),
                    _ => None,
                };
                var_type.constrain_plays(PlaysConstraint::from((type_, overridden)))
            }
            Rule::REGEX => var_type.regex(get_regex(constraint.next().unwrap())),
            Rule::RELATES => {
                let type_ = visit_type(constraint.next().unwrap());
                let overridden = match constraint.peek().map(|x| x.as_rule()) {
                    Some(Rule::AS) => Some(visit_type(constraint.skip(1).next().unwrap())),
                    _ => None,
                };
                var_type.constrain_relates(RelatesConstraint::from((type_, overridden)))
            }
            Rule::SUB_ => var_type
                .constrain_sub(SubConstraint::from(visit_type_any(constraint.next().unwrap()))),
            Rule::TYPE => var_type.type_(visit_label_any(constraint.next().unwrap())),
            Rule::VALUE => {
                var_type.value(token::ValueType::from(constraint.next().unwrap().as_str()))
            }
            _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&constraint.as_str()])),
        }
    });

    var_type
}

fn visit_variable_things(tree: Pair<Rule>) -> Vec<ThingVariable> {
    tree.into_inner().map(visit_variable_thing_any).collect()
}

fn visit_variable_thing_any(tree: Pair<Rule>) -> ThingVariable {
    let inner = unwrap_single(tree);
    match inner.as_rule() {
        Rule::variable_thing => visit_variable_thing(inner),
        Rule::variable_relation => visit_variable_relation(inner),
        Rule::variable_attribute => visit_variable_attribute(inner),
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
    }
}

fn visit_variable_thing(tree: Pair<Rule>) -> ThingVariable {
    let mut inner = tree.into_inner();
    let mut var_thing = get_var(inner.next().unwrap()).into_thing();
    if inner.peek().unwrap().as_rule() != Rule::attributes {
        let keyword = inner.next().unwrap();
        let argument = inner.next().unwrap();
        match keyword.as_rule() {
            Rule::IID => var_thing = var_thing.iid(argument.as_str()),
            Rule::ISA_ => {
                var_thing = var_thing.constrain_isa(get_isa_constraint(keyword, argument))
            }
            _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
        }
    }
    if let Some(attributes) = inner.next() {
        var_thing = visit_attributes(attributes)
            .into_iter()
            .fold(var_thing, |var_thing, has| var_thing.constrain_has(has));
    }
    var_thing
}

fn visit_variable_relation(tree: Pair<Rule>) -> ThingVariable {
    let mut inner = tree.into_inner();
    let mut relation = match inner.peek().unwrap().as_rule() {
        Rule::VAR_ => get_var(inner.next().unwrap()),
        _ => UnboundVariable::hidden(),
    }
    .constrain_relation(visit_relation(inner.next().unwrap()));

    if matches!(inner.peek().map(|p| p.as_rule()), Some(Rule::ISA_)) {
        let isa = inner.next().unwrap();
        let type_ = inner.next().unwrap();
        relation = relation.constrain_isa(get_isa_constraint(isa, type_));
    }
    if let Some(attributes) = inner.next() {
        relation = visit_attributes(attributes)
            .into_iter()
            .fold(relation, |relation, has| relation.constrain_has(has));
    }

    relation
}

fn visit_variable_attribute(tree: Pair<Rule>) -> ThingVariable {
    let mut inner = tree.into_inner();
    let mut attribute = match inner.peek().unwrap().as_rule() {
        Rule::VAR_ => get_var(inner.next().unwrap()),
        _ => UnboundVariable::hidden(),
    }
    .constrain_value(visit_predicate(inner.next().unwrap()));

    if matches!(inner.peek().map(|p| p.as_rule()), Some(Rule::ISA_)) {
        let isa = inner.next().unwrap();
        let type_ = inner.next().unwrap();
        attribute = attribute.constrain_isa(get_isa_constraint(isa, type_));
    }
    if let Some(attributes) = inner.next() {
        attribute = visit_attributes(attributes)
            .into_iter()
            .fold(attribute, |attribute, has| attribute.constrain_has(has));
    }

    attribute
}

fn visit_relation(tree: Pair<Rule>) -> RelationConstraint {
    RelationConstraint::new(get_role_players(tree))
}

fn visit_attributes(tree: Pair<Rule>) -> Vec<HasConstraint> {
    tree.into_inner().map(visit_attribute).collect()
}

fn visit_attribute(tree: Pair<Rule>) -> HasConstraint {
    let mut inner = tree.into_inner().skip(1);

    let next = inner.next().unwrap();
    match next.as_rule() {
        Rule::label => {
            let label = next.as_str().to_owned();
            let next = inner.next().unwrap();
            match next.as_rule() {
                Rule::VAR_ => HasConstraint::from((label, get_var(next))),
                Rule::predicate => HasConstraint::new((label, visit_predicate(next))),
                _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&next.as_str()])),
            }
        }
        Rule::VAR_ => HasConstraint::from(get_var(next)),
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&next.as_str()])),
    }
}

fn visit_predicate(tree: Pair<Rule>) -> ValueConstraint {
    let mut inner = tree.into_inner();
    match inner.peek().unwrap().as_rule() {
        Rule::value => {
            ValueConstraint::new(token::Predicate::Eq, visit_value(inner.next().unwrap()))
        }
        Rule::predicate_equality => {
            ValueConstraint::new(token::Predicate::from(inner.next().unwrap().as_str()), {
                let predicate_value = unwrap_single(inner.next().unwrap());
                match predicate_value.as_rule() {
                    Rule::value => visit_value(predicate_value),
                    Rule::VAR_ => Value::from(get_var(predicate_value)),
                    _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
                }
            })
        }
        Rule::predicate_substring => {
            let predicate = token::Predicate::from(inner.next().unwrap().as_str());
            ValueConstraint::new(
                predicate,
                {
                    match predicate {
                        token::Predicate::Like => get_regex(inner.next().unwrap()),
                        token::Predicate::Contains => get_string(inner.next().unwrap()),
                        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
                    }
                }
                .into(),
            )
        }
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
    }
}

fn visit_schema_rule(tree: Pair<Rule>) -> RuleDefinition {
    let inner = tree.into_inner().collect::<Vec<_>>();
    RuleDeclaration::new(Label::from(inner.get(1).unwrap().as_str()))
        .when(Conjunction::new(visit_patterns(inner.get(3).unwrap().clone())))
        .then(visit_variable_thing_any(inner.get(5).unwrap().clone()))
}

fn visit_schema_rule_declaration(tree: Pair<Rule>) -> RuleDeclaration {
    RuleDeclaration::new(Label::from(tree.into_inner().skip(1).next().unwrap().as_str()))
}

fn visit_type_any(tree: Pair<Rule>) -> Type {
    let inner = unwrap_single(tree);
    match inner.as_rule() {
        Rule::VAR_ => Type::Variable(get_var(inner)),
        Rule::type_ => visit_type(inner),
        Rule::type_scoped => visit_type_scoped(inner),
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
    }
}

fn visit_type_scoped(tree: Pair<Rule>) -> Type {
    let inner = unwrap_single(tree);
    match inner.as_rule() {
        Rule::label_scoped => Type::Label(visit_label_scoped(inner)),
        Rule::VAR_ => Type::Variable(get_var(inner)),
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
    }
}

fn visit_type(tree: Pair<Rule>) -> Type {
    let inner = unwrap_single(tree);
    match inner.as_rule() {
        Rule::label => Type::Label(inner.as_str().into()),
        Rule::VAR_ => Type::Variable(get_var(inner)),
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
    }
}

fn visit_label_any(tree: Pair<Rule>) -> Label {
    let inner = unwrap_single(tree);
    match inner.as_rule() {
        Rule::label => Label::from(inner.as_str()),
        Rule::label_scoped => visit_label_scoped(inner),
        _ => unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&inner.as_str()])),
    }
}

fn visit_label_scoped(tree: Pair<Rule>) -> Label {
    let parts: Vec<String> = tree.as_str().split(':').map(String::from).collect();
    assert_eq!(parts.len(), 2);
    Label::from((parts[0].clone(), parts[1].clone()))
}

fn visit_value(tree: Pair<Rule>) -> Value {
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
