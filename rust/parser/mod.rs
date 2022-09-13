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

use antlr_rust::token::Token;
use antlr_rust::tree::ParseTree;
use antlr_rust::tree::TerminalNode as ANTLRTerminalNode;
use chrono::{NaiveDateTime, Timelike};
use std::rc::Rc;

use crate::common::error::{ErrorMessage, ILLEGAL_GRAMMAR, ILLEGAL_STATE};
use typeql_grammar::typeqlrustparser::*;

use crate::pattern::*;
use crate::query::*;
use crate::typeql_match;

#[derive(Debug)]
pub struct Definable;

type ParserResult<T> = Result<T, ErrorMessage>;
type TerminalNode<'a> = ANTLRTerminalNode<'a, TypeQLRustParserContextType>;

#[derive(Debug)]
enum Type {
    Unscoped(String),
    Scoped(ScopedType),
    Variable(TypeVariable),
}

fn get_string(string: Rc<TerminalNode>) -> String {
    let quoted = string.get_text();
    String::from(&quoted[1..quoted.len() - 1])
}

fn get_long(long: Rc<TerminalNode>) -> ParserResult<i64> {
    long.get_text()
        .parse()
        .map_err(|_| ILLEGAL_GRAMMAR.format(&[long.get_text().as_str()]))
}

fn get_double(double: Rc<TerminalNode>) -> ParserResult<f64> {
    double
        .get_text()
        .parse()
        .map_err(|_| ILLEGAL_GRAMMAR.format(&[double.get_text().as_str()]))
}

fn get_date(date: Rc<TerminalNode>) -> ParserResult<NaiveDateTime> {
    NaiveDateTime::parse_from_str(&date.get_text(), "%Y-%m-%d")
        .map_err(|_| ILLEGAL_GRAMMAR.format(&[date.get_text().as_str()]))
}

fn parse_date_time(date_time_text: &str) -> Option<NaiveDateTime> {
    let has_seconds = date_time_text.matches(":").count() == 2;
    if has_seconds {
        let has_nanos = date_time_text.matches(".").count() == 1;
        if has_nanos {
            let parts: Vec<&str> = date_time_text.splitn(2, ".").collect();
            let (date_time, nanos) = (parts[0], parts[1]);
            NaiveDateTime::parse_from_str(date_time, "%Y-%m-%dT%H:%M:%S")
                .ok()?
                .with_nanosecond(
                    format!("{}{}", nanos, "0".repeat(9 - nanos.len()))
                        .parse()
                        .ok()?,
                )
        } else {
            NaiveDateTime::parse_from_str(date_time_text, "%Y-%m-%dT%H:%M:%S").ok()
        }
    } else {
        NaiveDateTime::parse_from_str(date_time_text, "%Y-%m-%dT%H:%M").ok()
    }
}

fn get_date_time(date_time: Rc<TerminalNode>) -> ParserResult<NaiveDateTime> {
    parse_date_time(&date_time.get_text())
        .ok_or(ILLEGAL_GRAMMAR.format(&[date_time.get_text().as_str()]))
}

fn get_var(var: Rc<TerminalNode>) -> UnboundVariable {
    let name = &var.symbol.get_text()[1..];
    if name == "_" {
        UnboundVariable::anonymous()
    } else {
        UnboundVariable::named(String::from(name))
    }
}

fn get_isa_constraint(
    _isa: Rc<TerminalNode>,
    ctx: Rc<Type_ContextAll>,
) -> ParserResult<IsaConstraint> {
    match visit_type(ctx)? {
        Type::Unscoped(label) => Ok(IsaConstraint::from(label)),
        Type::Variable(var) => Ok(IsaConstraint::from(var)),
        _ => Err(ILLEGAL_STATE.format(&[line!().to_string().as_str()])),
    }
}

fn get_role_player_constraint(ctx: Rc<Role_playerContext>) -> ParserResult<RolePlayerConstraint> {
    let player = get_var(ctx.player().unwrap().VAR_().unwrap());
    if let Some(type_) = ctx.type_() {
        match visit_type(type_)? {
            Type::Unscoped(label) => Ok(RolePlayerConstraint::from((label, player))),
            Type::Variable(var) => Ok(RolePlayerConstraint::from((var, player))),
            _ => Err(ILLEGAL_STATE.format(&[line!().to_string().as_str()])),
        }
    } else {
        Ok(RolePlayerConstraint::from(player))
    }
}

fn get_role_players(ctx: Rc<RelationContext>) -> ParserResult<Vec<RolePlayerConstraint>> {
    (0..)
        .map_while(|i| ctx.role_player(i))
        .map(get_role_player_constraint)
        .collect()
}

pub fn visit_eof_query(ctx: Rc<Eof_queryContext>) -> ParserResult<Query> {
    visit_query(ctx.query().unwrap())
}

fn visit_eof_queries(ctx: Rc<Eof_queriesContext>) -> ParserResult<Vec<Query>> {
    (0..).map_while(|i| ctx.query(i)).map(visit_query).collect()
}

fn visit_eof_pattern(ctx: Rc<Eof_patternContext>) -> ParserResult<Pattern> {
    visit_pattern(ctx.pattern().unwrap())
}

fn visit_eof_patterns(ctx: Rc<Eof_patternsContext>) -> ParserResult<Vec<Pattern>> {
    visit_patterns(ctx.patterns().unwrap())
}

fn visit_eof_definables(ctx: Rc<Eof_definablesContext>) -> ParserResult<Vec<Definable>> {
    let definables_ctx = ctx.definables().unwrap();
    (0..)
        .map_while(|i| definables_ctx.definable(i))
        .map(visit_definable)
        .collect()
}

fn visit_eof_variable(ctx: Rc<Eof_variableContext>) -> ParserResult<Variable> {
    visit_pattern_variable(ctx.pattern_variable().unwrap())
}

fn visit_eof_label(ctx: Rc<Eof_labelContext>) -> ParserResult<String> {
    Ok(ctx.label().unwrap().get_text())
}

fn visit_eof_schema_rule(ctx: Rc<Eof_schema_ruleContext>) -> ParserResult<()> {
    visit_schema_rule(ctx.schema_rule().unwrap())
}

fn visit_query(ctx: Rc<QueryContext>) -> ParserResult<Query> {
    if let Some(query_match) = ctx.query_match() {
        Ok(visit_query_match(query_match)?.into_query())
    } else {
        Err(ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]))
    }
}

fn visit_query_define(_ctx: Rc<Query_defineContext>) -> ParserResult<()> {
    todo!()
}

fn visit_query_undefine(_ctx: Rc<Query_undefineContext>) -> ParserResult<()> {
    todo!()
}

fn visit_query_insert(_ctx: Rc<Query_insertContext>) -> ParserResult<()> {
    todo!()
}

fn visit_query_delete_or_update(_ctx: Rc<Query_delete_or_updateContext>) -> ParserResult<()> {
    todo!()
}

fn visit_query_match(ctx: Rc<Query_matchContext>) -> ParserResult<TypeQLMatch> {
    let mut match_query = typeql_match(visit_patterns(ctx.patterns().unwrap())?).into_match();
    if let Some(modifiers) = ctx.modifiers() {
        if let Some(filter) = modifiers.filter() {
            match_query = match_query.filter(visit_filter(filter)?);
        }
        if let Some(sort) = modifiers.sort() {
            match_query = match_query.sort(visit_sort(sort)?);
        }
        if let Some(limit) = modifiers.limit() {
            match_query = match_query.limit(get_long(limit.LONG_().unwrap())? as usize);
        }
        if let Some(offset) = modifiers.offset() {
            match_query = match_query.offset(get_long(offset.LONG_().unwrap())? as usize);
        }
    }
    Ok(match_query)
}

fn visit_query_match_aggregate(_ctx: Rc<Query_match_aggregateContext>) -> ParserResult<()> {
    todo!()
}

fn visit_query_match_group(_ctx: Rc<Query_match_groupContext>) -> ParserResult<()> {
    todo!()
}

fn visit_query_match_group_agg(_ctx: Rc<Query_match_group_aggContext>) -> ParserResult<()> {
    todo!()
}

fn visit_modifiers(_ctx: Rc<ModifiersContext>) -> ParserResult<()> {
    todo!()
}

fn visit_filter(ctx: Rc<FilterContext>) -> ParserResult<Vec<UnboundVariable>> {
    Ok((0..).map_while(|i| ctx.VAR_(i)).map(get_var).collect())
}

fn visit_sort(ctx: Rc<SortContext>) -> ParserResult<Sorting> {
    Ok(Sorting::new(
        (0..).map_while(|i| ctx.VAR_(i)).map(get_var).collect(),
        &if let Some(order) = ctx.ORDER_() {
            order.get_text()
        } else {
            String::from("") // FIXME
        },
    ))
}

fn visit_offset(_ctx: Rc<OffsetContext>) -> ParserResult<()> {
    todo!()
}

fn visit_limit(_ctx: Rc<LimitContext>) -> ParserResult<()> {
    todo!()
}

fn visit_match_aggregate(_ctx: Rc<Match_aggregateContext>) -> ParserResult<()> {
    todo!()
}

fn visit_aggregate_method(_ctx: Rc<Aggregate_methodContext>) -> ParserResult<()> {
    todo!()
}

fn visit_match_group(_ctx: Rc<Match_groupContext>) -> ParserResult<()> {
    todo!()
}

fn visit_definables(_ctx: Rc<DefinablesContext>) -> ParserResult<()> {
    todo!()
}

fn visit_definable(_ctx: Rc<DefinableContext>) -> ParserResult<Definable> {
    todo!()
}

fn visit_patterns(ctx: Rc<PatternsContext>) -> ParserResult<Vec<Pattern>> {
    (0..)
        .map_while(|i| ctx.pattern(i))
        .map(visit_pattern)
        .collect()
}

fn visit_pattern(ctx: Rc<PatternContext>) -> ParserResult<Pattern> {
    if let Some(var) = ctx.pattern_variable() {
        Ok(visit_pattern_variable(var)?.into_pattern())
    } else {
        todo!()
    }
}

fn visit_pattern_conjunction(_ctx: Rc<Pattern_conjunctionContext>) -> ParserResult<()> {
    todo!()
}

fn visit_pattern_disjunction(_ctx: Rc<Pattern_disjunctionContext>) -> ParserResult<()> {
    todo!()
}

fn visit_pattern_negation(_ctx: Rc<Pattern_negationContext>) -> ParserResult<()> {
    todo!()
}

fn visit_pattern_variable(ctx: Rc<Pattern_variableContext>) -> ParserResult<Variable> {
    if let Some(var_thing_any) = ctx.variable_thing_any() {
        Ok(visit_variable_thing_any(var_thing_any)?.into_variable())
    } else if let Some(var_type) = ctx.variable_type() {
        Ok(visit_variable_type(var_type)?.into_variable())
    } else if let Some(var_concept) = ctx.variable_concept() {
        Ok(visit_variable_concept(var_concept)?.into_variable())
    } else {
        Err(ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]))
    }
}

fn visit_variable_concept(_ctx: Rc<Variable_conceptContext>) -> ParserResult<BoundVariable> {
    todo!()
}

fn visit_variable_type(ctx: Rc<Variable_typeContext>) -> ParserResult<TypeVariable> {
    let mut var_type = match visit_type_any(ctx.type_any().unwrap())? {
        Type::Variable(p) => p,
        Type::Unscoped(p) => UnboundVariable::hidden().type_(p).into_type(),
        _ => Err(ILLEGAL_STATE.format(&[line!().to_string().as_str()]))?,
    };
    for constraint in (0..).map_while(|i| ctx.type_constraint(i)) {
        if constraint.PLAYS().is_some() {
            let _overridden: Option<()> = match constraint.AS() {
                None => None,
                Some(_) => todo!(),
            };
            var_type = var_type.constrain_type(
                match visit_type_scoped(constraint.type_scoped().unwrap())? {
                    Type::Scoped(scoped) => PlaysConstraint::from(scoped),
                    Type::Variable(var) => PlaysConstraint::from(var),
                    _ => Err(ILLEGAL_STATE.format(&[line!().to_string().as_str()]))?,
                }
                .into_type_constraint(),
            );
        } else if constraint.RELATES().is_some() {
            let _overridden: Option<()> = match constraint.AS() {
                None => None,
                Some(_) => todo!(),
            };
            var_type = var_type.constrain_type(
                match visit_type(constraint.type_(0).unwrap())? {
                    Type::Unscoped(label) => RelatesConstraint::from(label),
                    Type::Variable(var) => RelatesConstraint::from(var),
                    _ => Err(ILLEGAL_STATE.format(&[line!().to_string().as_str()]))?,
                }
                .into_type_constraint(),
            );
        } else if constraint.SUB_().is_some() {
            var_type = var_type.constrain_type(
                match visit_type_any(constraint.type_any().unwrap())? {
                    Type::Unscoped(label) => SubConstraint::from(label),
                    Type::Variable(var) => SubConstraint::from(var),
                    _ => Err(ILLEGAL_STATE.format(&[line!().to_string().as_str()]))?,
                }
                .into_type_constraint(),
            );
        } else if constraint.TYPE().is_some() {
            let scoped_label = visit_label_any(constraint.label_any().unwrap())?;
            var_type = var_type.type_(scoped_label).into_type();
        } else {
            panic!("visit_variable_type: not implemented")
        }
    }
    Ok(var_type)
}

fn visit_type_constraint(_ctx: Rc<Type_constraintContext>) -> ParserResult<()> {
    todo!()
}

fn visit_variable_things(_ctx: Rc<Variable_thingsContext>) -> ParserResult<()> {
    todo!()
}

fn visit_variable_thing_any(ctx: Rc<Variable_thing_anyContext>) -> ParserResult<ThingVariable> {
    if let Some(var_thing) = ctx.variable_thing() {
        visit_variable_thing(var_thing)
    } else if let Some(var_relation) = ctx.variable_relation() {
        visit_variable_relation(var_relation)
    } else if let Some(var_attribute) = ctx.variable_attribute() {
        visit_variable_attribute(var_attribute)
    } else {
        Err(ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]))
    }
}

fn visit_variable_thing(ctx: Rc<Variable_thingContext>) -> ParserResult<ThingVariable> {
    let mut var_thing = get_var(ctx.VAR_().unwrap()).into_thing();
    if let Some(isa) = ctx.ISA_() {
        var_thing = var_thing
            .constrain_thing(get_isa_constraint(isa, ctx.type_().unwrap())?.into_thing_constraint())
    }
    if let Some(attributes) = ctx.attributes() {
        var_thing = visit_attributes(attributes)?
            .into_iter()
            .fold(var_thing, |var_thing, constraint| {
                var_thing.constrain_thing(constraint.into_thing_constraint())
            });
    }
    Ok(var_thing)
}

fn visit_variable_relation(ctx: Rc<Variable_relationContext>) -> ParserResult<ThingVariable> {
    let mut relation = match ctx.VAR_() {
        Some(var) => get_var(var),
        None => UnboundVariable::hidden(),
    }
    .constrain_thing(visit_relation(ctx.relation().unwrap())?.into_thing_constraint());

    if let Some(isa) = ctx.ISA_() {
        relation = relation.constrain_thing(
            get_isa_constraint(isa, ctx.type_().unwrap())?.into_thing_constraint(),
        );
    }

    if let Some(_attributes) = ctx.attributes() {
        todo!();
    }

    Ok(relation)
}

fn visit_variable_attribute(ctx: Rc<Variable_attributeContext>) -> ParserResult<ThingVariable> {
    let mut attribute = match ctx.VAR_() {
        Some(var) => get_var(var),
        None => UnboundVariable::hidden(),
    }
    .constrain_thing(visit_predicate(ctx.predicate().unwrap())?.into_thing_constraint());

    if let Some(isa) = ctx.ISA_() {
        attribute = attribute.constrain_thing(
            get_isa_constraint(isa, ctx.type_().unwrap())?.into_thing_constraint(),
        );
    }

    if let Some(_attributes) = ctx.attributes() {
        todo!();
    }

    Ok(attribute)
}

fn visit_relation(ctx: Rc<RelationContext>) -> ParserResult<RelationConstraint> {
    Ok(RelationConstraint::new(get_role_players(ctx)?))
}

fn visit_role_player(_ctx: Rc<Role_playerContext>) -> ParserResult<()> {
    todo!()
}

fn visit_player(_ctx: Rc<PlayerContext>) -> ParserResult<()> {
    todo!()
}

fn visit_attributes(ctx: Rc<AttributesContext>) -> ParserResult<Vec<HasConstraint>> {
    (0..)
        .map_while(|i| ctx.attribute(i))
        .map(visit_attribute)
        .collect()
}

fn visit_attribute(ctx: Rc<AttributeContext>) -> ParserResult<HasConstraint> {
    let has = if let Some(label) = ctx.label() {
        if let Some(var) = ctx.VAR_() {
            HasConstraint::from_typed_variable(label.get_text(), get_var(var).into_thing())
        } else if let Some(predicate) = ctx.predicate() {
            HasConstraint::from_value(
                label.get_text(),
                visit_predicate(predicate)?
            )
        } else {
            Err(ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]))?
        }
    } else if let Some(_) = ctx.VAR_() {
        todo!()
    } else {
        Err(ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]))?
    };

    Ok(has)
}

fn visit_predicate(ctx: Rc<PredicateContext>) -> ParserResult<ValueConstraint> {
    let (predicate, value) = if let Some(value) = ctx.value() {
        (Predicate::Eq, visit_value(value)?)
    } else if let Some(equality) = ctx.predicate_equality() {
        (
            Predicate::from(equality.get_text()),
            if let Some(_value) = ctx.predicate_value().unwrap().value() {
                todo!()
            } else if let Some(var) = ctx.predicate_value().unwrap().VAR_() {
                Value::from(get_var(var))
            } else {
                Err(ILLEGAL_STATE.format(&[line!().to_string().as_str()]))?
            },
        )
    } else {
        todo!()
    };

    Ok(ValueConstraint::new(predicate, value))
}

fn visit_predicate_equality(_ctx: Rc<Predicate_equalityContext>) -> ParserResult<()> {
    todo!()
}

fn visit_predicate_substring(_ctx: Rc<Predicate_substringContext>) -> ParserResult<()> {
    todo!()
}

fn visit_predicate_value(_ctx: Rc<Predicate_valueContext>) -> ParserResult<()> {
    todo!()
}

fn visit_schema_rule(_ctx: Rc<Schema_ruleContext>) -> ParserResult<()> {
    todo!()
}

fn visit_type_any(ctx: Rc<Type_anyContext>) -> ParserResult<Type> {
    if let Some(var) = ctx.VAR_() {
        Ok(Type::Variable(get_var(var).into_type()))
    } else if let Some(type_) = ctx.type_() {
        visit_type(type_)
    } else if let Some(scoped) = ctx.type_scoped() {
        visit_type_scoped(scoped)
    } else {
        panic!("null type label")
    }
}

fn visit_type_scoped(ctx: Rc<Type_scopedContext>) -> ParserResult<Type> {
    if let Some(scoped) = ctx.label_scoped() {
        Ok(Type::Scoped(visit_label_scoped(scoped)?))
    } else if let Some(var) = ctx.VAR_() {
        Ok(Type::Variable(get_var(var).into_type()))
    } else {
        panic!("null scoped type label")
    }
}

fn visit_type(ctx: Rc<Type_Context>) -> ParserResult<Type> {
    if let Some(label) = ctx.label() {
        Ok(Type::Unscoped(label.get_text()))
    } else if let Some(var) = ctx.VAR_() {
        Ok(Type::Variable(get_var(var).into_type()))
    } else {
        panic!("visit_type: not implemented")
    }
}

fn visit_label_any(ctx: Rc<Label_anyContext>) -> ParserResult<String> {
    if let Some(label) = ctx.label() {
        Ok(label.get_text())
    } else {
        panic!("visit_label_any: not implemented")
    }
}

fn visit_label_scoped(ctx: Rc<Label_scopedContext>) -> ParserResult<ScopedType> {
    let parts: Vec<String> = ctx.get_text().split(":").map(String::from).collect();
    Ok(ScopedType::from((parts[0].clone(), parts[1].clone())))
}

fn visit_label(_ctx: Rc<LabelContext>) -> ParserResult<()> {
    todo!()
}

fn visit_labels(_ctx: Rc<LabelsContext>) -> ParserResult<()> {
    todo!()
}

fn visit_label_array(_ctx: Rc<Label_arrayContext>) -> ParserResult<()> {
    todo!()
}

fn visit_schema_native(_ctx: Rc<Schema_nativeContext>) -> ParserResult<()> {
    todo!()
}

fn visit_type_native(_ctx: Rc<Type_nativeContext>) -> ParserResult<()> {
    todo!()
}

fn visit_value_type(_ctx: Rc<Value_typeContext>) -> ParserResult<()> {
    todo!()
}

fn visit_value(ctx: Rc<ValueContext>) -> ParserResult<Value> {
    if let Some(string) = ctx.STRING_() {
        Ok(Value::from(get_string(string)))
    } else if let Some(date_time) = ctx.DATETIME_() {
        Value::try_from(get_date_time(date_time)?)
    } else if let Some(date) = ctx.DATE_() {
        Value::try_from(get_date(date)?)
    } else {
        todo!()
    }
}

fn visit_regex(_ctx: Rc<RegexContext>) -> ParserResult<()> {
    todo!()
}

fn visit_unreserved(_ctx: Rc<UnreservedContext>) -> ParserResult<()> {
    todo!()
}
