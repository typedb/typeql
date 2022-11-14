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

pub mod error_listener;
pub mod syntax_error;

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
use antlr_rust::{
    token::Token,
    tree::{ParseTree, TerminalNode as ANTLRTerminalNode},
};
use chrono::{NaiveDate, NaiveDateTime};
use std::rc::Rc;

// keep star import to not expose generated code
use typeql_grammar::typeqlrustparser::*;

type TerminalNode<'a> = ANTLRTerminalNode<'a, TypeQLRustParserContextType>;

fn get_string(string: Rc<TerminalNode>) -> String {
    unquote(&string.get_text())
}

fn get_regex(string: Rc<TerminalNode>) -> String {
    unescape_regex(&unquote(&string.get_text()))
}

fn get_long(long: Rc<TerminalNode>) -> i64 {
    long.get_text().parse().expect(&ILLEGAL_GRAMMAR.format(&[long.get_text().as_str()]).message)
}

fn get_double(double: Rc<TerminalNode>) -> f64 {
    double.get_text().parse().expect(&ILLEGAL_GRAMMAR.format(&[double.get_text().as_str()]).message)
}

fn get_boolean(boolean: Rc<TerminalNode>) -> bool {
    boolean
        .get_text()
        .parse()
        .expect(&ILLEGAL_GRAMMAR.format(&[boolean.get_text().as_str()]).message)
}

fn get_date(date: Rc<TerminalNode>) -> NaiveDate {
    NaiveDate::parse_from_str(&date.get_text(), "%Y-%m-%d")
        .expect(&ILLEGAL_GRAMMAR.format(&[date.get_text().as_str()]).message)
}

fn get_date_time(date_time: Rc<TerminalNode>) -> NaiveDateTime {
    date_time::parse(&date_time.get_text())
        .expect(&ILLEGAL_GRAMMAR.format(&[date_time.get_text().as_str()]).message)
}

fn get_var(var: Rc<TerminalNode>) -> UnboundVariable {
    let name = &var.symbol.get_text();

    assert!(name.len() > 1);
    assert!(name.starts_with('$'));
    let name = &name[1..];

    if name == "_" {
        UnboundVariable::anonymous()
    } else {
        UnboundVariable::named(String::from(name))
    }
}

fn get_isa_constraint(_isa: Rc<TerminalNode>, ctx: Rc<Type_ContextAll>) -> IsaConstraint {
    match visit_type(ctx) {
        Type::Label(label) => IsaConstraint::from(label),
        Type::Variable(var) => IsaConstraint::from(var),
    }
}

fn get_role_player_constraint(ctx: Rc<Role_playerContext>) -> RolePlayerConstraint {
    let player = get_var(ctx.player().unwrap().VAR_().unwrap());
    if let Some(type_) = ctx.type_() {
        match visit_type(type_) {
            Type::Label(label) => RolePlayerConstraint::from((label, player)),
            Type::Variable(var) => RolePlayerConstraint::from((var, player)),
        }
    } else {
        RolePlayerConstraint::from(player)
    }
}

fn get_role_players(ctx: Rc<RelationContext>) -> Vec<RolePlayerConstraint> {
    (0..).map_while(|i| ctx.role_player(i)).map(get_role_player_constraint).collect()
}

pub(crate) fn visit_eof_query(ctx: Rc<Eof_queryContext>) -> Result<Query> {
    visit_query(ctx.query().unwrap()).validated()
}

pub(crate) fn visit_eof_queries(
    ctx: Rc<Eof_queriesContext>,
) -> Result<impl Iterator<Item = Result<Query>> + '_> {
    Ok((0..).map_while(move |i| ctx.query(i)).map(visit_query).map(Validatable::validated))
}

pub(crate) fn visit_eof_pattern(ctx: Rc<Eof_patternContext>) -> Result<Pattern> {
    visit_pattern(ctx.pattern().unwrap()).validated()
}

pub(crate) fn visit_eof_patterns(ctx: Rc<Eof_patternsContext>) -> Result<Vec<Pattern>> {
    visit_patterns(ctx.patterns().unwrap()).into_iter().map(Validatable::validated).collect()
}

pub(crate) fn visit_eof_definables(ctx: Rc<Eof_definablesContext>) -> Result<Vec<Definable>> {
    visit_definables(ctx.definables().unwrap()).into_iter().map(Validatable::validated).collect()
}

pub(crate) fn visit_eof_variable(ctx: Rc<Eof_variableContext>) -> Result<Variable> {
    visit_pattern_variable(ctx.pattern_variable().unwrap()).validated()
}

pub(crate) fn visit_eof_label(ctx: Rc<Eof_labelContext>) -> Result<Label> {
    // TODO validation
    Ok(ctx.label().unwrap().get_text().into())
}

pub(crate) fn visit_eof_schema_rule(ctx: Rc<Eof_schema_ruleContext>) -> Result<RuleDefinition> {
    visit_schema_rule(ctx.schema_rule().unwrap()).validated()
}

fn visit_query(ctx: Rc<QueryContext>) -> Query {
    if let Some(query_match) = ctx.query_match() {
        visit_query_match(query_match).into()
    } else if let Some(query_insert) = ctx.query_insert() {
        visit_query_insert(query_insert).into()
    } else if let Some(query_delete) = ctx.query_delete() {
        visit_query_delete(query_delete).into()
    } else if let Some(query_update) = ctx.query_update() {
        visit_query_update(query_update).into()
    } else if let Some(query_define) = ctx.query_define() {
        visit_query_define(query_define).into()
    } else if let Some(query_undefine) = ctx.query_undefine() {
        visit_query_undefine(query_undefine).into()
    } else if let Some(query_aggregate) = ctx.query_match_aggregate() {
        visit_query_match_aggregate(query_aggregate).into()
    } else if let Some(query_group) = ctx.query_match_group() {
        visit_query_match_group(query_group).into()
    } else if let Some(query_group_aggregate) = ctx.query_match_group_agg() {
        visit_query_match_group_agg(query_group_aggregate).into()
    } else {
        unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]))
    }
}

fn visit_query_define(ctx: Rc<Query_defineContext>) -> TypeQLDefine {
    TypeQLDefine::new(visit_definables(ctx.definables().unwrap()))
}

fn visit_query_undefine(ctx: Rc<Query_undefineContext>) -> TypeQLUndefine {
    TypeQLUndefine::new(visit_definables(ctx.definables().unwrap()))
}

fn visit_query_insert(ctx: Rc<Query_insertContext>) -> TypeQLInsert {
    let variable_things = visit_variable_things(ctx.variable_things().unwrap());
    if let Some(patterns) = ctx.patterns() {
        TypeQLMatch::from_patterns(visit_patterns(patterns)).insert(variable_things)
    } else {
        TypeQLInsert::new(variable_things)
    }
}

fn visit_query_delete(ctx: Rc<Query_deleteContext>) -> TypeQLDelete {
    TypeQLMatch::from_patterns(visit_patterns(ctx.patterns().unwrap()))
        .delete(visit_variable_things(ctx.variable_things().unwrap()))
}

fn visit_query_update(ctx: Rc<Query_updateContext>) -> TypeQLUpdate {
    visit_query_delete(ctx.query_delete().unwrap())
        .insert(visit_variable_things(ctx.variable_things().unwrap()))
}

fn visit_query_match(ctx: Rc<Query_matchContext>) -> TypeQLMatch {
    let mut match_query = TypeQLMatch::from_patterns(visit_patterns(ctx.patterns().unwrap()));
    if let Some(modifiers) = ctx.modifiers() {
        if let Some(filter) = modifiers.filter() {
            match_query = match_query.filter(visit_filter(filter));
        }
        if let Some(sort) = modifiers.sort() {
            match_query = match_query.sort(visit_sort(sort));
        }
        if let Some(limit) = modifiers.limit() {
            match_query = match_query.limit(get_long(limit.LONG_().unwrap()) as usize);
        }
        if let Some(offset) = modifiers.offset() {
            match_query = match_query.offset(get_long(offset.LONG_().unwrap()) as usize);
        }
    }
    match_query
}

fn visit_query_match_aggregate(ctx: Rc<Query_match_aggregateContext>) -> TypeQLMatchAggregate {
    let function = ctx.match_aggregate().unwrap();
    let match_query = visit_query_match(ctx.query_match().unwrap());
    match visit_aggregate_method(function.aggregate_method().unwrap()) {
        token::Aggregate::Count => match_query.count(),
        method => match_query.aggregate(method, get_var(function.VAR_().unwrap())),
    }
}

fn visit_query_match_group(ctx: Rc<Query_match_groupContext>) -> TypeQLMatchGroup {
    visit_query_match(ctx.query_match().unwrap())
        .group(get_var(ctx.match_group().unwrap().VAR_().unwrap()))
}

fn visit_query_match_group_agg(ctx: Rc<Query_match_group_aggContext>) -> TypeQLMatchGroupAggregate {
    let function = ctx.match_aggregate().unwrap();
    let group = visit_query_match(ctx.query_match().unwrap())
        .group(get_var(ctx.match_group().unwrap().VAR_().unwrap()));
    match visit_aggregate_method(function.aggregate_method().unwrap()) {
        token::Aggregate::Count => group.count(),
        method => group.aggregate(method, get_var(function.VAR_().unwrap())),
    }
}

fn visit_filter(ctx: Rc<FilterContext>) -> Vec<UnboundVariable> {
    (0..).map_while(|i| ctx.VAR_(i)).map(get_var).collect()
}

fn visit_sort(ctx: Rc<SortContext>) -> Sorting {
    Sorting::new((0..).map_while(|i| ctx.var_order(i)).map(visit_var_order).collect())
}

fn visit_var_order(ctx: Rc<Var_orderContext>) -> sorting::OrderedVariable {
    sorting::OrderedVariable {
        var: get_var(ctx.VAR_().unwrap()),
        order: ctx.ORDER_().map(|order| order.get_text()),
    }
}

fn visit_aggregate_method(ctx: Rc<Aggregate_methodContext>) -> token::Aggregate {
    token::Aggregate::from(ctx.get_text())
}

fn visit_definables(ctx: Rc<DefinablesContext>) -> Vec<Definable> {
    (0..).map_while(|i| ctx.definable(i)).map(visit_definable).collect()
}

fn visit_definable(ctx: Rc<DefinableContext>) -> Definable {
    if let Some(variable_type) = ctx.variable_type() {
        visit_variable_type(variable_type).into()
    } else {
        let rule_ctx = ctx.schema_rule().unwrap();
        if rule_ctx.patterns().is_some() {
            visit_schema_rule(rule_ctx).into()
        } else {
            visit_schema_rule_declaration(rule_ctx).into()
        }
    }
}

fn visit_patterns(ctx: Rc<PatternsContext>) -> Vec<Pattern> {
    (0..).map_while(|i| ctx.pattern(i)).map(visit_pattern).collect()
}

fn visit_pattern(ctx: Rc<PatternContext>) -> Pattern {
    if let Some(var) = ctx.pattern_variable() {
        visit_pattern_variable(var).into()
    } else if let Some(disjunction) = ctx.pattern_disjunction() {
        visit_pattern_disjunction(disjunction).into()
    } else if let Some(conjunction) = ctx.pattern_conjunction() {
        visit_pattern_conjunction(conjunction).into()
    } else if let Some(negation) = ctx.pattern_negation() {
        visit_pattern_negation(negation).into()
    } else {
        unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]))
    }
}

fn visit_pattern_conjunction(ctx: Rc<Pattern_conjunctionContext>) -> Conjunction {
    Conjunction::new(visit_patterns(ctx.patterns().unwrap()))
}

fn visit_pattern_disjunction(ctx: Rc<Pattern_disjunctionContext>) -> Disjunction {
    Disjunction::new(
        (0..)
            .map_while(|i| ctx.patterns(i))
            .map(visit_patterns)
            .map(|mut nested| match nested.len() {
                1 => nested.pop().unwrap(),
                _ => Conjunction::new(nested).into(),
            })
            .collect::<Vec<Pattern>>(),
    )
}

fn visit_pattern_negation(ctx: Rc<Pattern_negationContext>) -> Negation {
    let mut patterns = visit_patterns(ctx.patterns().unwrap());
    match patterns.len() {
        1 => Negation::new(patterns.pop().unwrap()),
        _ => Negation::new(Conjunction::new(patterns).into()),
    }
}

fn visit_pattern_variable(ctx: Rc<Pattern_variableContext>) -> Variable {
    if let Some(var_thing_any) = ctx.variable_thing_any() {
        visit_variable_thing_any(var_thing_any).into()
    } else if let Some(var_type) = ctx.variable_type() {
        visit_variable_type(var_type).into()
    } else if let Some(var_concept) = ctx.variable_concept() {
        visit_variable_concept(var_concept).into()
    } else {
        unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]))
    }
}

fn visit_variable_concept(ctx: Rc<Variable_conceptContext>) -> ConceptVariable {
    get_var(ctx.VAR_(0).unwrap()).is(get_var(ctx.VAR_(1).unwrap()))
}

fn visit_variable_type(ctx: Rc<Variable_typeContext>) -> TypeVariable {
    let mut var_type = visit_type_any(ctx.type_any().unwrap()).into_type_variable();
    for constraint in (0..).map_while(|i| ctx.type_constraint(i)) {
        if constraint.ABSTRACT().is_some() {
            var_type = var_type.abstract_();
        } else if constraint.OWNS().is_some() {
            let overridden = constraint.AS().map(|_| visit_type(constraint.type_(1).unwrap()));
            let is_key = IsKeyAttribute::from(constraint.IS_KEY().is_some());
            var_type = var_type.constrain_owns(OwnsConstraint::from((
                visit_type(constraint.type_(0).unwrap()),
                overridden,
                is_key,
            )));
        } else if constraint.PLAYS().is_some() {
            let overridden = constraint.AS().map(|_| visit_type(constraint.type_(0).unwrap()));
            var_type = var_type.constrain_plays(PlaysConstraint::from((
                visit_type_scoped(constraint.type_scoped().unwrap()),
                overridden,
            )));
        } else if constraint.REGEX().is_some() {
            var_type = var_type.regex(get_regex(constraint.STRING_().unwrap()));
        } else if constraint.RELATES().is_some() {
            let overridden = constraint.AS().map(|_| visit_type(constraint.type_(1).unwrap()));
            var_type = var_type.constrain_relates(RelatesConstraint::from((
                visit_type(constraint.type_(0).unwrap()),
                overridden,
            )));
        } else if constraint.SUB_().is_some() {
            var_type = var_type
                .constrain_sub(SubConstraint::from(visit_type_any(constraint.type_any().unwrap())));
        } else if constraint.TYPE().is_some() {
            let scoped_label = visit_label_any(constraint.label_any().unwrap());
            var_type = var_type.type_(scoped_label);
        } else if constraint.VALUE().is_some() {
            var_type =
                var_type.value(token::ValueType::from(constraint.value_type().unwrap().get_text()));
        } else {
            panic!("visit_variable_type: not implemented")
        }
    }

    var_type
}

fn visit_variable_things(ctx: Rc<Variable_thingsContext>) -> Vec<ThingVariable> {
    (0..).map_while(|i| ctx.variable_thing_any(i)).map(visit_variable_thing_any).collect()
}

fn visit_variable_thing_any(ctx: Rc<Variable_thing_anyContext>) -> ThingVariable {
    if let Some(var_thing) = ctx.variable_thing() {
        visit_variable_thing(var_thing)
    } else if let Some(var_relation) = ctx.variable_relation() {
        visit_variable_relation(var_relation)
    } else if let Some(var_attribute) = ctx.variable_attribute() {
        visit_variable_attribute(var_attribute)
    } else {
        unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]))
    }
}

fn visit_variable_thing(ctx: Rc<Variable_thingContext>) -> ThingVariable {
    let mut var_thing = get_var(ctx.VAR_().unwrap()).into_thing();
    if let Some(iid) = ctx.IID_() {
        var_thing = var_thing.iid(iid.get_text());
    }
    if let Some(isa) = ctx.ISA_() {
        var_thing = var_thing.constrain_isa(get_isa_constraint(isa, ctx.type_().unwrap()));
    }
    if let Some(attributes) = ctx.attributes() {
        var_thing = visit_attributes(attributes)
            .into_iter()
            .fold(var_thing, |var_thing, has| var_thing.constrain_has(has));
    }
    var_thing
}

fn visit_variable_relation(ctx: Rc<Variable_relationContext>) -> ThingVariable {
    let mut relation = match ctx.VAR_() {
        Some(var) => get_var(var),
        None => UnboundVariable::hidden(),
    }
    .constrain_relation(visit_relation(ctx.relation().unwrap()));

    if let Some(isa) = ctx.ISA_() {
        relation = relation.constrain_isa(get_isa_constraint(isa, ctx.type_().unwrap()));
    }

    if let Some(attributes) = ctx.attributes() {
        relation = visit_attributes(attributes)
            .into_iter()
            .fold(relation, |relation, has| relation.constrain_has(has));
    }

    relation
}

fn visit_variable_attribute(ctx: Rc<Variable_attributeContext>) -> ThingVariable {
    let mut attribute = match ctx.VAR_() {
        Some(var) => get_var(var),
        None => UnboundVariable::hidden(),
    }
    .constrain_value(visit_predicate(ctx.predicate().unwrap()));

    if let Some(isa) = ctx.ISA_() {
        attribute = attribute.constrain_isa(get_isa_constraint(isa, ctx.type_().unwrap()));
    }

    if let Some(attributes) = ctx.attributes() {
        attribute = visit_attributes(attributes)
            .into_iter()
            .fold(attribute, |attribute, has| attribute.constrain_has(has));
    }

    attribute
}

fn visit_relation(ctx: Rc<RelationContext>) -> RelationConstraint {
    RelationConstraint::new(get_role_players(ctx))
}

fn visit_attributes(ctx: Rc<AttributesContext>) -> Vec<HasConstraint> {
    (0..).map_while(|i| ctx.attribute(i)).map(visit_attribute).collect()
}

fn visit_attribute(ctx: Rc<AttributeContext>) -> HasConstraint {
    if let Some(label) = ctx.label() {
        if let Some(var) = ctx.VAR_() {
            HasConstraint::from((label.get_text(), get_var(var)))
        } else if let Some(predicate) = ctx.predicate() {
            HasConstraint::new((label.get_text(), visit_predicate(predicate)))
        } else {
            unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]))
        }
    } else if let Some(var) = ctx.VAR_() {
        HasConstraint::from(get_var(var))
    } else {
        unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]))
    }
}

fn visit_predicate(ctx: Rc<PredicateContext>) -> ValueConstraint {
    if let Some(value) = ctx.value() {
        ValueConstraint::new(token::Predicate::Eq, visit_value(value))
    } else if let Some(equality) = ctx.predicate_equality() {
        ValueConstraint::new(token::Predicate::from(equality.get_text()), {
            let predicate_value = ctx.predicate_value().unwrap();
            if let Some(value) = predicate_value.value() {
                visit_value(value)
            } else if let Some(var) = predicate_value.VAR_() {
                Value::from(get_var(var))
            } else {
                panic!("Unexpected predicate value: `{}`", predicate_value.get_text())
            }
        })
    } else if let Some(substring) = ctx.predicate_substring() {
        ValueConstraint::new(token::Predicate::from(substring.get_text()), {
            if substring.LIKE().is_some() {
                Value::from(get_regex(ctx.STRING_().unwrap()))
            } else {
                Value::from(get_string(ctx.STRING_().unwrap()))
            }
        })
    } else {
        unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]))
    }
}

fn visit_schema_rule(ctx: Rc<Schema_ruleContext>) -> RuleDefinition {
    RuleDeclaration::new(Label::from(ctx.label().unwrap().get_text()))
        .when(Conjunction::new(visit_patterns(ctx.patterns().unwrap())))
        .then(visit_variable_thing_any(ctx.variable_thing_any().unwrap()))
}

fn visit_schema_rule_declaration(ctx: Rc<Schema_ruleContext>) -> RuleDeclaration {
    RuleDeclaration::new(Label::from(ctx.label().unwrap().get_text()))
}

fn visit_type_any(ctx: Rc<Type_anyContext>) -> Type {
    if let Some(var) = ctx.VAR_() {
        Type::Variable(get_var(var))
    } else if let Some(type_) = ctx.type_() {
        visit_type(type_)
    } else if let Some(scoped) = ctx.type_scoped() {
        visit_type_scoped(scoped)
    } else {
        panic!("null type label")
    }
}

fn visit_type_scoped(ctx: Rc<Type_scopedContext>) -> Type {
    if let Some(scoped) = ctx.label_scoped() {
        Type::Label(visit_label_scoped(scoped))
    } else if let Some(var) = ctx.VAR_() {
        Type::Variable(get_var(var))
    } else {
        panic!("null scoped type label")
    }
}

fn visit_type(ctx: Rc<Type_Context>) -> Type {
    if let Some(label) = ctx.label() {
        Type::Label(label.get_text().into())
    } else if let Some(var) = ctx.VAR_() {
        Type::Variable(get_var(var))
    } else {
        panic!("visit_type: not implemented")
    }
}

fn visit_label_any(ctx: Rc<Label_anyContext>) -> Label {
    if let Some(label) = ctx.label() {
        Label::from(label.get_text())
    } else if let Some(label_scoped) = ctx.label_scoped() {
        visit_label_scoped(label_scoped)
    } else {
        unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]))
    }
}

fn visit_label_scoped(ctx: Rc<Label_scopedContext>) -> Label {
    let parts: Vec<String> = ctx.get_text().split(':').map(String::from).collect();
    assert_eq!(parts.len(), 2);
    Label::from((parts[0].clone(), parts[1].clone()))
}

fn visit_value(ctx: Rc<ValueContext>) -> Value {
    if let Some(string) = ctx.STRING_() {
        Value::from(get_string(string))
    } else if let Some(long) = ctx.LONG_() {
        Value::from(get_long(long))
    } else if let Some(double) = ctx.DOUBLE_() {
        Value::from(get_double(double))
    } else if let Some(boolean) = ctx.BOOLEAN_() {
        Value::from(get_boolean(boolean))
    } else if let Some(date) = ctx.DATE_() {
        Value::from(get_date(date).and_hms(0, 0, 0))
    } else if let Some(date_time) = ctx.DATETIME_() {
        Value::from(get_date_time(date_time))
    } else {
        unreachable!("{}", ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]))
    }
}
