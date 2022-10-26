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

use antlr_rust::{
    token::Token,
    tree::{ParseTree, TerminalNode as ANTLRTerminalNode},
};
use chrono::{NaiveDate, NaiveDateTime};
use std::rc::Rc;

use crate::common::{
    date_time,
    error::{ErrorMessage, ILLEGAL_GRAMMAR},
    string::*,
    token::Predicate,
};
use typeql_grammar::typeqlrustparser::*;

use crate::{pattern::*, query::*};

type ParserResult<T> = Result<T, ErrorMessage>;
type TerminalNode<'a> = ANTLRTerminalNode<'a, TypeQLRustParserContextType>;

fn get_string(string: Rc<TerminalNode>) -> String {
    unquote(&string.get_text())
}

fn get_regex(string: Rc<TerminalNode>) -> String {
    unescape_regex(&unquote(&string.get_text()))
}

fn get_long(long: Rc<TerminalNode>) -> ParserResult<i64> {
    long.get_text().parse().map_err(|_| ILLEGAL_GRAMMAR.format(&[long.get_text().as_str()]))
}

fn get_double(double: Rc<TerminalNode>) -> ParserResult<f64> {
    double.get_text().parse().map_err(|_| ILLEGAL_GRAMMAR.format(&[double.get_text().as_str()]))
}

fn get_boolean(boolean: Rc<TerminalNode>) -> ParserResult<bool> {
    boolean.get_text().parse().map_err(|_| ILLEGAL_GRAMMAR.format(&[boolean.get_text().as_str()]))
}

fn get_date(date: Rc<TerminalNode>) -> ParserResult<NaiveDate> {
    NaiveDate::parse_from_str(&date.get_text(), "%Y-%m-%d")
        .map_err(|_| ILLEGAL_GRAMMAR.format(&[date.get_text().as_str()]))
}

fn get_date_time(date_time: Rc<TerminalNode>) -> ParserResult<NaiveDateTime> {
    date_time::parse(&date_time.get_text())
        .ok_or_else(|| ILLEGAL_GRAMMAR.format(&[date_time.get_text().as_str()]))
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

fn get_isa_constraint(
    _isa: Rc<TerminalNode>,
    ctx: Rc<Type_ContextAll>,
) -> ParserResult<IsaConstraint> {
    match visit_type(ctx)? {
        Type::Label(label) => Ok(IsaConstraint::from(label)),
        Type::Variable(var) => Ok(IsaConstraint::from(var)),
    }
}

fn get_role_player_constraint(ctx: Rc<Role_playerContext>) -> ParserResult<RolePlayerConstraint> {
    let player = get_var(ctx.player().unwrap().VAR_().unwrap());
    if let Some(type_) = ctx.type_() {
        match visit_type(type_)? {
            Type::Label(label) => Ok(RolePlayerConstraint::from((label, player))),
            Type::Variable(var) => Ok(RolePlayerConstraint::from((var, player))),
        }
    } else {
        Ok(RolePlayerConstraint::from(player))
    }
}

fn get_role_players(ctx: Rc<RelationContext>) -> ParserResult<Vec<RolePlayerConstraint>> {
    (0..).map_while(|i| ctx.role_player(i)).map(get_role_player_constraint).collect()
}

pub(crate) fn visit_eof_query(ctx: Rc<Eof_queryContext>) -> ParserResult<Query> {
    visit_query(ctx.query().unwrap())
}

pub(crate) fn visit_eof_queries(ctx: Rc<Eof_queriesContext>) -> ParserResult<Vec<Query>> {
    (0..).map_while(|i| ctx.query(i)).map(visit_query).collect()
}

pub(crate) fn visit_eof_pattern(ctx: Rc<Eof_patternContext>) -> ParserResult<Pattern> {
    visit_pattern(ctx.pattern().unwrap())
}

pub(crate) fn visit_eof_patterns(ctx: Rc<Eof_patternsContext>) -> ParserResult<Vec<Pattern>> {
    visit_patterns(ctx.patterns().unwrap())
}

pub(crate) fn visit_eof_definables(ctx: Rc<Eof_definablesContext>) -> ParserResult<Vec<Pattern>> {
    let definables_ctx = ctx.definables().unwrap();
    (0..).map_while(|i| definables_ctx.definable(i)).map(visit_definable).collect()
}

pub(crate) fn visit_eof_variable(ctx: Rc<Eof_variableContext>) -> ParserResult<Variable> {
    visit_pattern_variable(ctx.pattern_variable().unwrap())
}

pub(crate) fn visit_eof_label(ctx: Rc<Eof_labelContext>) -> ParserResult<String> {
    Ok(ctx.label().unwrap().get_text())
}

pub(crate) fn visit_eof_schema_rule(
    ctx: Rc<Eof_schema_ruleContext>,
) -> ParserResult<RuleDefinition> {
    visit_schema_rule(ctx.schema_rule().unwrap())
}

fn visit_query(ctx: Rc<QueryContext>) -> ParserResult<Query> {
    if let Some(query_match) = ctx.query_match() {
        Ok(visit_query_match(query_match)?.into_query())
    } else if let Some(query_insert) = ctx.query_insert() {
        Ok(visit_query_insert(query_insert)?.into_query())
    } else if let Some(query_delete) = ctx.query_delete() {
        Ok(visit_query_delete(query_delete)?.into_query())
    } else if let Some(query_update) = ctx.query_update() {
        Ok(visit_query_update(query_update)?.into_query())
    } else if let Some(query_define) = ctx.query_define() {
        Ok(visit_query_define(query_define)?.into_query())
    } else if let Some(query_undefine) = ctx.query_undefine() {
        Ok(visit_query_undefine(query_undefine)?.into_query())
    } else {
        Err(ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]))
    }
}

fn visit_query_define(ctx: Rc<Query_defineContext>) -> ParserResult<TypeQLDefine> {
    Ok(TypeQLDefine::new(visit_definables(ctx.definables().unwrap())?))
}

fn visit_query_undefine(ctx: Rc<Query_undefineContext>) -> ParserResult<TypeQLUndefine> {
    Ok(TypeQLUndefine::new(visit_definables(ctx.definables().unwrap())?))
}

fn visit_query_insert(ctx: Rc<Query_insertContext>) -> ParserResult<TypeQLInsert> {
    let variable_things = visit_variable_things(ctx.variable_things().unwrap())?;
    if let Some(patterns) = ctx.patterns() {
        Ok(TypeQLMatch::new(visit_patterns(patterns)?).insert(variable_things))
    } else {
        Ok(TypeQLInsert::new(variable_things))
    }
}

fn visit_query_delete(ctx: Rc<Query_deleteContext>) -> ParserResult<TypeQLDelete> {
    let variable_things = visit_variable_things(ctx.variable_things().unwrap())?;
    Ok(TypeQLMatch::new(visit_patterns(ctx.patterns().unwrap())?).delete(variable_things))
}

fn visit_query_update(ctx: Rc<Query_updateContext>) -> ParserResult<TypeQLUpdate> {
    Ok(visit_query_delete(ctx.query_delete().unwrap())?
        .insert(visit_variable_things(ctx.variable_things().unwrap())?))
}

fn visit_query_match(ctx: Rc<Query_matchContext>) -> ParserResult<TypeQLMatch> {
    let mut match_query = TypeQLMatch::new(visit_patterns(ctx.patterns().unwrap())?);
    if let Some(modifiers) = ctx.modifiers() {
        if let Some(filter) = modifiers.filter() {
            match_query = match_query.filter(visit_filter(filter)?);
        }
        if let Some(sort) = modifiers.sort() {
            match_query = match_query.sort(visit_sort(sort));
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

fn visit_sort(ctx: Rc<SortContext>) -> Sorting {
    Sorting::new((0..).map_while(|i| ctx.var_order(i)).map(visit_var_order).collect())
}

fn visit_var_order(ctx: Rc<Var_orderContext>) -> OrderedVariable {
    OrderedVariable {
        var: get_var(ctx.VAR_().unwrap()),
        order: ctx.ORDER_().map(|order| order.get_text()),
    }
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

fn visit_definables(ctx: Rc<DefinablesContext>) -> ParserResult<Vec<Pattern>> {
    (0..).map_while(|i| ctx.definable(i)).map(visit_definable).collect()
}

fn visit_definable(ctx: Rc<DefinableContext>) -> ParserResult<Pattern> {
    if let Some(variable_type) = ctx.variable_type() {
        Ok(visit_variable_type(variable_type)?.into_variable().into_pattern())
    } else {
        let rule_ctx = ctx.schema_rule().unwrap();
        if rule_ctx.patterns().is_some() {
            visit_schema_rule(rule_ctx).map(RuleDefinition::into_pattern)
        } else {
            visit_schema_rule_declaration(rule_ctx).map(RuleDeclaration::into_pattern)
        }
    }
}

fn visit_patterns(ctx: Rc<PatternsContext>) -> ParserResult<Vec<Pattern>> {
    (0..).map_while(|i| ctx.pattern(i)).map(visit_pattern).collect()
}

fn visit_pattern(ctx: Rc<PatternContext>) -> ParserResult<Pattern> {
    if let Some(var) = ctx.pattern_variable() {
        Ok(visit_pattern_variable(var)?.into_pattern())
    } else if let Some(disjunction) = ctx.pattern_disjunction() {
        Ok(visit_pattern_disjunction(disjunction)?.into_pattern())
    } else if let Some(conjunction) = ctx.pattern_conjunction() {
        Ok(visit_pattern_conjunction(conjunction)?.into_pattern())
    } else if let Some(negation) = ctx.pattern_negation() {
        Ok(visit_pattern_negation(negation)?.into_pattern())
    } else {
        Err(ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]))
    }
}

fn visit_pattern_conjunction(ctx: Rc<Pattern_conjunctionContext>) -> ParserResult<Conjunction> {
    Ok(Conjunction::from(visit_patterns(ctx.patterns().unwrap())?))
}

fn visit_pattern_disjunction(ctx: Rc<Pattern_disjunctionContext>) -> ParserResult<Disjunction> {
    Ok(Disjunction::from(
        (0..)
            .map_while(|i| ctx.patterns(i))
            .map(visit_patterns)
            .map(|result| {
                result.map(|mut nested| match nested.len() {
                    1 => nested.pop().unwrap(),
                    _ => Conjunction::from(nested).into_pattern(),
                })
            })
            .collect::<ParserResult<Vec<Pattern>>>()?,
    ))
}

fn visit_pattern_negation(ctx: Rc<Pattern_negationContext>) -> ParserResult<Negation> {
    let mut patterns = visit_patterns(ctx.patterns().unwrap())?;
    Ok(match patterns.len() {
        1 => Negation::from(patterns.pop().unwrap()),
        _ => Negation::from(Conjunction::from(patterns).into_pattern()),
    })
}

fn visit_pattern_variable(ctx: Rc<Pattern_variableContext>) -> ParserResult<Variable> {
    if let Some(var_thing_any) = ctx.variable_thing_any() {
        Ok(visit_variable_thing_any(var_thing_any)?.into_variable())
    } else if let Some(var_type) = ctx.variable_type() {
        Ok(visit_variable_type(var_type)?.into_variable())
    } else if let Some(var_concept) = ctx.variable_concept() {
        Ok(visit_variable_concept(var_concept).into_variable())
    } else {
        Err(ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]))
    }
}

fn visit_variable_concept(ctx: Rc<Variable_conceptContext>) -> ConceptVariable {
    get_var(ctx.VAR_(0).unwrap()).is(get_var(ctx.VAR_(1).unwrap()))
}

fn visit_variable_type(ctx: Rc<Variable_typeContext>) -> ParserResult<TypeVariable> {
    let mut var_type = visit_type_any(ctx.type_any().unwrap())?.into_type_variable();
    for constraint in (0..).map_while(|i| ctx.type_constraint(i)) {
        if constraint.ABSTRACT().is_some() {
            var_type = var_type.abstract_();
        } else if constraint.OWNS().is_some() {
            let overridden =
                constraint.AS().map(|_| visit_type(constraint.type_(1).unwrap())).transpose()?;
            let is_key = IsKeyAttribute::from(constraint.IS_KEY().is_some());
            var_type = var_type.constrain_owns(OwnsConstraint::from((
                visit_type(constraint.type_(0).unwrap())?,
                overridden,
                is_key,
            )));
        } else if constraint.PLAYS().is_some() {
            let overridden =
                constraint.AS().map(|_| visit_type(constraint.type_(0).unwrap())).transpose()?;
            var_type = var_type.constrain_plays(PlaysConstraint::from((
                visit_type_scoped(constraint.type_scoped().unwrap())?,
                overridden,
            )));
        } else if constraint.REGEX().is_some() {
            var_type = var_type.regex(get_regex(constraint.STRING_().unwrap()));
        } else if constraint.RELATES().is_some() {
            let overridden =
                constraint.AS().map(|_| visit_type(constraint.type_(1).unwrap())).transpose()?;
            var_type = var_type.constrain_relates(RelatesConstraint::from((
                visit_type(constraint.type_(0).unwrap())?,
                overridden,
            )));
        } else if constraint.SUB_().is_some() {
            var_type = var_type.constrain_sub(SubConstraint::from(visit_type_any(
                constraint.type_any().unwrap(),
            )?));
        } else if constraint.TYPE().is_some() {
            let scoped_label = visit_label_any(constraint.label_any().unwrap())?;
            var_type = var_type.type_(scoped_label);
        } else {
            panic!("visit_variable_type: not implemented")
        }
    }
    Ok(var_type)
}

fn visit_type_constraint(_ctx: Rc<Type_constraintContext>) -> ParserResult<()> {
    todo!()
}

fn visit_variable_things(ctx: Rc<Variable_thingsContext>) -> ParserResult<Vec<ThingVariable>> {
    (0..).map_while(|i| ctx.variable_thing_any(i)).map(visit_variable_thing_any).collect()
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
    if let Some(iid) = ctx.IID_() {
        var_thing = var_thing.iid(iid.get_text())?;
    }
    if let Some(isa) = ctx.ISA_() {
        var_thing = var_thing.constrain_isa(get_isa_constraint(isa, ctx.type_().unwrap())?)
    }
    if let Some(attributes) = ctx.attributes() {
        var_thing = visit_attributes(attributes)?
            .into_iter()
            .fold(var_thing, |var_thing, has| var_thing.constrain_has(has));
    }
    Ok(var_thing)
}

fn visit_variable_relation(ctx: Rc<Variable_relationContext>) -> ParserResult<ThingVariable> {
    let mut relation = match ctx.VAR_() {
        Some(var) => get_var(var),
        None => UnboundVariable::hidden(),
    }
    .constrain_relation(visit_relation(ctx.relation().unwrap())?);

    if let Some(isa) = ctx.ISA_() {
        relation = relation.constrain_isa(get_isa_constraint(isa, ctx.type_().unwrap())?);
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
    .constrain_value(visit_predicate(ctx.predicate().unwrap())?);

    if let Some(isa) = ctx.ISA_() {
        attribute = attribute.constrain_isa(get_isa_constraint(isa, ctx.type_().unwrap())?);
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
    (0..).map_while(|i| ctx.attribute(i)).map(visit_attribute).collect()
}

fn visit_attribute(ctx: Rc<AttributeContext>) -> ParserResult<HasConstraint> {
    if let Some(label) = ctx.label() {
        if let Some(var) = ctx.VAR_() {
            HasConstraint::try_from((label.get_text(), get_var(var)))
        } else if let Some(predicate) = ctx.predicate() {
            Ok(HasConstraint::new((label.get_text(), visit_predicate(predicate)?)))
        } else {
            Err(ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]))?
        }
    } else if let Some(var) = ctx.VAR_() {
        Ok(HasConstraint::from(get_var(var)))
    } else {
        Err(ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]))
    }
}

fn visit_predicate(ctx: Rc<PredicateContext>) -> ParserResult<ValueConstraint> {
    if let Some(value) = ctx.value() {
        ValueConstraint::new(Predicate::Eq, visit_value(value)?)
    } else if let Some(equality) = ctx.predicate_equality() {
        ValueConstraint::new(Predicate::from(equality.get_text()), {
            let predicate_value = ctx.predicate_value().unwrap();
            if let Some(value) = predicate_value.value() {
                visit_value(value)?
            } else if let Some(var) = predicate_value.VAR_() {
                Value::from(get_var(var))
            } else {
                panic!("Unexpected predicate value: `{}`", predicate_value.get_text())
            }
        })
    } else if let Some(substring) = ctx.predicate_substring() {
        ValueConstraint::new(Predicate::from(substring.get_text()), {
            if substring.LIKE().is_some() {
                Value::from(get_regex(ctx.STRING_().unwrap()))
            } else {
                Value::from(get_string(ctx.STRING_().unwrap()))
            }
        })
    } else {
        todo!()
    }
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

fn visit_schema_rule(ctx: Rc<Schema_ruleContext>) -> ParserResult<RuleDefinition> {
    Ok(RuleDefinition::new(
        Label::from(ctx.label().unwrap().get_text()),
        Conjunction::from(visit_patterns(ctx.patterns().unwrap())?),
        visit_variable_thing_any(ctx.variable_thing_any().unwrap())?,
    ))
}

fn visit_schema_rule_declaration(ctx: Rc<Schema_ruleContext>) -> ParserResult<RuleDeclaration> {
    Ok(RuleDeclaration::new(Label::from(ctx.label().unwrap().get_text())))
}

fn visit_type_any(ctx: Rc<Type_anyContext>) -> ParserResult<Type> {
    if let Some(var) = ctx.VAR_() {
        Ok(Type::Variable(get_var(var)))
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
        Ok(Type::Label(visit_label_scoped(scoped)?))
    } else if let Some(var) = ctx.VAR_() {
        Ok(Type::Variable(get_var(var)))
    } else {
        panic!("null scoped type label")
    }
}

fn visit_type(ctx: Rc<Type_Context>) -> ParserResult<Type> {
    if let Some(label) = ctx.label() {
        Ok(Type::Label(label.get_text().into()))
    } else if let Some(var) = ctx.VAR_() {
        Ok(Type::Variable(get_var(var)))
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

fn visit_label_scoped(ctx: Rc<Label_scopedContext>) -> ParserResult<Label> {
    let parts: Vec<String> = ctx.get_text().split(':').map(String::from).collect();
    Ok(Label::from((parts[0].clone(), parts[1].clone())))
}

fn visit_label(_ctx: Rc<LabelContext>) -> ParserResult<()> {
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
    } else if let Some(long) = ctx.LONG_() {
        Ok(Value::from(get_long(long)?))
    } else if let Some(double) = ctx.DOUBLE_() {
        Ok(Value::from(get_double(double)?))
    } else if let Some(boolean) = ctx.BOOLEAN_() {
        Ok(Value::from(get_boolean(boolean)?))
    } else if let Some(date) = ctx.DATE_() {
        Value::try_from(get_date(date)?.and_hms(0, 0, 0))
    } else if let Some(date_time) = ctx.DATETIME_() {
        Value::try_from(get_date_time(date_time)?)
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
