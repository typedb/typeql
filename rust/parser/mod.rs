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
use antlr_rust::tree::TerminalNode;
use antlr_rust::tree::{ParseTree, ParseTreeVisitorCompat};
use chrono::{NaiveDateTime, Timelike};
use std::string::ParseError;

use crate::common::error::{ErrorMessage, ILLEGAL_GRAMMAR, ILLEGAL_STATE};
use typeql_grammar::typeqlrustparser::*;
use typeql_grammar::typeqlrustvisitor::TypeQLRustVisitorCompat;

use crate::enum_getter;
use crate::pattern::*;
use crate::query::*;
use crate::typeql_match;

#[derive(Debug)]
pub struct Definable;

#[must_use]
#[derive(Debug)]
pub enum ParserResult {
    Constraint(Constraint),
    Constraints(Vec<Constraint>),
    Definable(Definable),
    Definables(Vec<Definable>),
    Pattern(Pattern),
    Patterns(Vec<Pattern>),
    Query(Query),
    Queries(Vec<Query>),
    Sorting(Sorting),

    Label(String),
    ScopedLabel((String, String)),

    Value(Value),
    Err(ErrorMessage),
}

impl ParserResult {
    enum_getter!(into_constraint, Constraint, Constraint);
    enum_getter!(into_constraints, Constraints, Vec<Constraint>);
    enum_getter!(into_definable, Definable, Definable);
    enum_getter!(into_definables, Definables, Vec<Definable>);
    enum_getter!(into_pattern, Pattern, Pattern);
    enum_getter!(into_patterns, Patterns, Vec<Pattern>);
    enum_getter!(into_query, Query, Query);
    enum_getter!(into_queries, Queries, Vec<Query>);
    enum_getter!(into_sorting, Sorting, Sorting);

    enum_getter!(into_label, Label, String);

    enum_getter!(into_value, Value, Value);

    pub fn is_err(&self) -> bool {
        matches!(self, Self::Err(_))
    }
    pub fn is_ok(&self) -> bool {
        !self.is_err()
    }
}

impl Default for ParserResult {
    fn default() -> Self {
        ParserResult::Err(ILLEGAL_STATE.format(&[]))
    }
}

#[derive(Default)]
pub struct Parser;

fn parse_date_time(date_time_text: &str) -> Option<NaiveDateTime> {
    let has_seconds = date_time_text.matches(":").count() == 2;
    if has_seconds {
        let has_nanos = date_time_text.matches(".").count() == 1;
        if has_nanos {
            let parts: Vec<&str> = date_time_text.splitn(2, ".").collect();
            let (date_time, nanos) = (parts[0], parts[1]);
            NaiveDateTime::parse_from_str(date_time, "%Y-%m-%dT%H:%M:%S").ok()?
                .with_nanosecond(format!("{}{}", nanos, "0".repeat(9 - nanos.len())).parse().ok()?)
        } else {
            NaiveDateTime::parse_from_str(date_time_text, "%Y-%m-%dT%H:%M:%S").ok()
        }
    } else {
        NaiveDateTime::parse_from_str(date_time_text, "%Y-%m-%dT%H:%M").ok()
    }
}

impl Parser {
    fn get_string(&self, string: &TerminalNode<TypeQLRustParserContextType>) -> String {
        let quoted = string.get_text();
        String::from(&quoted[1..quoted.len() - 1])
    }

    fn get_long(
        &self,
        long: &TerminalNode<TypeQLRustParserContextType>,
    ) -> Result<i64, ErrorMessage> {
        long.get_text()
            .parse()
            .map_err(|_| ILLEGAL_GRAMMAR.format(&[long.get_text().as_str()]))
    }

    fn get_double(
        &self,
        double: &TerminalNode<TypeQLRustParserContextType>,
    ) -> Result<f64, ErrorMessage> {
        double.get_text()
            .parse()
            .map_err(|_| ILLEGAL_GRAMMAR.format(&[double.get_text().as_str()]))
    }

    fn get_date(
        &self,
        date: &TerminalNode<TypeQLRustParserContextType>,
    ) -> Result<NaiveDateTime, ErrorMessage> {
        NaiveDateTime::parse_from_str(&date.get_text(), "%Y-%m-%d")
            .map_err(|_| ILLEGAL_GRAMMAR.format(&[date.get_text().as_str()]))
    }

    fn get_date_time(
        &self,
        date_time: &TerminalNode<TypeQLRustParserContextType>,
    ) -> Result<NaiveDateTime, ErrorMessage> {
        let date_time_text = &date_time.get_text();
        parse_date_time(date_time_text).ok_or(ILLEGAL_GRAMMAR.format(&[date_time_text]))
    }

    fn get_var(&mut self, var: &TerminalNode<TypeQLRustParserContextType>) -> UnboundVariable {
        let name = &var.symbol.get_text()[1..];
        if name == "_" {
            UnboundVariable::anonymous()
        } else {
            UnboundVariable::named(String::from(name))
        }
    }

    fn get_isa_constraint(
        &mut self,
        _isa: &TerminalNode<TypeQLRustParserContextType>,
        ctx: &Type_ContextAll,
    ) -> Result<IsaConstraint, ErrorMessage> {
        Ok(match self.visit_type_(ctx) {
            ParserResult::Label(label) => IsaConstraint::from(label),
            ParserResult::Pattern(var) => IsaConstraint::from(var.into_unbound_variable()),
            ParserResult::Err(err) => return Err(err),
            _ => return Err(ILLEGAL_STATE.format(&[])),
        })
    }
}

macro_rules! maybe {
    ($e:expr) => {
        match $e {
            err @ ParserResult::Err(_) => return err,
            result => result,
        }
    };
}

macro_rules! maybe_unwrap {
    ($e:expr) => {
        match $e {
            Err(err) => return ParserResult::Err(err),
            Ok(result) => result,
        }
    };
}

impl<'input> ParseTreeVisitorCompat<'input> for Parser {
    type Node = TypeQLRustParserContextType;
    type Return = ParserResult;

    fn temp_result(&mut self) -> &mut Self::Return {
        panic!("temp_result")
    }

    fn aggregate_results(&self, _aggregate: Self::Return, _next: Self::Return) -> Self::Return {
        panic!("aggregate_results")
    }
}

impl<'input> TypeQLRustVisitorCompat<'input> for Parser {
    fn visit_eof_query(&mut self, ctx: &Eof_queryContext<'input>) -> Self::Return {
        self.visit_query(ctx.query().unwrap().as_ref())
    }

    fn visit_eof_queries(&mut self, ctx: &Eof_queriesContext<'input>) -> Self::Return {
        let mut queries = Vec::new();
        for i in 0.. {
            if let Some(query_ctx) = ctx.query(i) {
                queries.push(maybe!(self.visit_query(query_ctx.as_ref())).into_query());
            } else {
                break;
            }
        }
        ParserResult::Queries(queries)
    }

    fn visit_eof_pattern(&mut self, ctx: &Eof_patternContext<'input>) -> Self::Return {
        self.visit_pattern(ctx.pattern().unwrap().as_ref())
    }

    fn visit_eof_patterns(&mut self, ctx: &Eof_patternsContext<'input>) -> Self::Return {
        self.visit_patterns(ctx.patterns().unwrap().as_ref())
    }

    fn visit_eof_definables(&mut self, ctx: &Eof_definablesContext<'input>) -> Self::Return {
        let definables_ctx = ctx.definables().unwrap();
        let mut definables = Vec::new();
        for i in 0.. {
            if let Some(definable_ctx) = definables_ctx.definable(i) {
                definables
                    .push(maybe!(self.visit_definable(definable_ctx.as_ref())).into_definable());
            } else {
                break;
            }
        }
        ParserResult::Definables(definables)
    }

    fn visit_eof_variable(&mut self, ctx: &Eof_variableContext<'input>) -> Self::Return {
        self.visit_pattern_variable(ctx.pattern_variable().unwrap().as_ref())
    }

    fn visit_eof_label(&mut self, ctx: &Eof_labelContext<'input>) -> Self::Return {
        ParserResult::Label(ctx.label().unwrap().get_text())
    }

    fn visit_eof_schema_rule(&mut self, ctx: &Eof_schema_ruleContext<'input>) -> Self::Return {
        self.visit_schema_rule(ctx.schema_rule().unwrap().as_ref())
    }

    fn visit_query(&mut self, ctx: &QueryContext<'input>) -> Self::Return {
        if let Some(query_match) = ctx.query_match() {
            self.visit_query_match(query_match.as_ref())
        } else {
            ParserResult::Err(ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]))
        }
    }

    fn visit_query_define(&mut self, _ctx: &Query_defineContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_query_undefine(&mut self, _ctx: &Query_undefineContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_query_insert(&mut self, _ctx: &Query_insertContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_query_delete_or_update(
        &mut self,
        _ctx: &Query_delete_or_updateContext<'input>,
    ) -> Self::Return {
        todo!()
    }

    fn visit_query_match(&mut self, ctx: &Query_matchContext<'input>) -> Self::Return {
        let mut match_query = typeql_match(
            maybe!(self.visit_patterns(ctx.patterns().unwrap().as_ref())).into_patterns(),
        )
        .into_match();
        if let Some(modifiers) = ctx.modifiers() {
            if let Some(filter) = modifiers.filter() {
                match_query =
                    match_query.filter(maybe!(self.visit_filter(filter.as_ref())).into_patterns());
            }
            if let Some(sort) = modifiers.sort() {
                match_query =
                    match_query.sort(maybe!(self.visit_sort(sort.as_ref())).into_sorting());
            }
            if let Some(limit) = modifiers.limit() {
                match_query = match_query
                    .limit(maybe_unwrap!(self.get_long(limit.LONG_().unwrap().as_ref())) as usize);
            }
            if let Some(offset) = modifiers.offset() {
                match_query = match_query.offset(maybe_unwrap!(
                    self.get_long(offset.LONG_().unwrap().as_ref())
                ) as usize);
            }
        }
        ParserResult::Query(match_query.into_query())
    }

    fn visit_query_match_aggregate(
        &mut self,
        _ctx: &Query_match_aggregateContext<'input>,
    ) -> Self::Return {
        todo!()
    }

    fn visit_query_match_group(&mut self, _ctx: &Query_match_groupContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_query_match_group_agg(
        &mut self,
        _ctx: &Query_match_group_aggContext<'input>,
    ) -> Self::Return {
        todo!()
    }

    fn visit_modifiers(&mut self, _ctx: &ModifiersContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_filter(&mut self, ctx: &FilterContext<'input>) -> Self::Return {
        ParserResult::Patterns(
            (0..)
                .map_while(|i| ctx.VAR_(i))
                .map(|x| self.get_var(x.as_ref()).into_pattern())
                .collect(),
        )
    }

    fn visit_sort(&mut self, ctx: &SortContext<'input>) -> Self::Return {
        ParserResult::Sorting(Sorting::new(
            (0..)
                .map_while(|i| ctx.VAR_(i))
                .map(|x| self.get_var(x.as_ref()))
                .collect(),
            &if let Some(order) = ctx.ORDER_() {
                order.get_text()
            } else {
                String::from("")
            },
        ))
    }

    fn visit_offset(&mut self, _ctx: &OffsetContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_limit(&mut self, _ctx: &LimitContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_match_aggregate(&mut self, _ctx: &Match_aggregateContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_aggregate_method(&mut self, _ctx: &Aggregate_methodContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_match_group(&mut self, _ctx: &Match_groupContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_definables(&mut self, _ctx: &DefinablesContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_definable(&mut self, _ctx: &DefinableContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_patterns(&mut self, ctx: &PatternsContext<'input>) -> Self::Return {
        let mut patterns = Vec::new();
        for i in 0.. {
            if let Some(pattern_ctx) = ctx.pattern(i) {
                patterns.push(maybe!(self.visit_pattern(pattern_ctx.as_ref())).into_pattern());
            } else {
                break;
            }
        }
        ParserResult::Patterns(patterns)
    }

    fn visit_pattern(&mut self, ctx: &PatternContext<'input>) -> Self::Return {
        if let Some(var) = ctx.pattern_variable() {
            self.visit_pattern_variable(var.as_ref())
        } else {
            todo!()
        }
    }

    fn visit_pattern_conjunction(
        &mut self,
        _ctx: &Pattern_conjunctionContext<'input>,
    ) -> Self::Return {
        todo!()
    }

    fn visit_pattern_disjunction(
        &mut self,
        _ctx: &Pattern_disjunctionContext<'input>,
    ) -> Self::Return {
        todo!()
    }

    fn visit_pattern_negation(&mut self, _ctx: &Pattern_negationContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_pattern_variable(&mut self, ctx: &Pattern_variableContext<'input>) -> Self::Return {
        if let Some(var_thing_any) = ctx.variable_thing_any() {
            self.visit_variable_thing_any(var_thing_any.as_ref())
        } else if let Some(var_type) = ctx.variable_type() {
            self.visit_variable_type(var_type.as_ref())
        } else if let Some(var_concept) = ctx.variable_concept() {
            self.visit_variable_concept(var_concept.as_ref())
        } else {
            ParserResult::Err(ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]))
        }
    }

    fn visit_variable_concept(&mut self, _ctx: &Variable_conceptContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_variable_type(&mut self, ctx: &Variable_typeContext<'input>) -> Self::Return {
        let mut var_type = match maybe!(self.visit_type_any(ctx.type_any().unwrap().as_ref())) {
            ParserResult::Pattern(p) => p.into_type_variable(),
            ParserResult::Label(p) => UnboundVariable::hidden().type_(p).into_type(),
            other => panic!("{:?}", other),
        };
        for constraint in (0..).map_while(|i| ctx.type_constraint(i)) {
            if constraint.PLAYS().is_some() {
                let _overridden: Option<u8> = match constraint.AS() {
                    None => None,
                    Some(_) => todo!(),
                };
                var_type = var_type
                    .constrain_type(
                        match maybe!(
                            self.visit_type_scoped(constraint.type_scoped().unwrap().as_ref())
                        ) {
                            ParserResult::ScopedLabel(scoped) => PlaysConstraint::from(scoped),
                            ParserResult::Pattern(var) => {
                                PlaysConstraint::from(var.into_unbound_variable())
                            }
                            other => panic!("{:?}", other),
                        }
                        .into_type_constraint(),
                    )
                    .into_type();
            } else if constraint.RELATES().is_some() {
                let _overridden: Option<u8> = match constraint.AS() {
                    None => None,
                    Some(_) => todo!(),
                };
                var_type = var_type
                    .constrain_type(
                        match maybe!(self.visit_type_(constraint.type_(0).unwrap().as_ref())) {
                            ParserResult::Label(label) => RelatesConstraint::from(label),
                            ParserResult::Pattern(var) => {
                                RelatesConstraint::from(var.into_unbound_variable())
                            }
                            other => panic!("{:?}", other),
                        }
                        .into_type_constraint(),
                    )
                    .into_type();
            } else if constraint.SUB_().is_some() {
                var_type =
                    var_type
                        .constrain_type(
                            match maybe!(
                                self.visit_type_any(constraint.type_any().unwrap().as_ref())
                            ) {
                                ParserResult::Label(label) => SubConstraint::from(label),
                                ParserResult::Pattern(var) => {
                                    SubConstraint::from(var.into_unbound_variable())
                                }
                                other => panic!("{:?}", other),
                            }
                            .into_type_constraint(),
                        )
                        .into_type();
            } else if constraint.TYPE().is_some() {
                let scoped_label =
                    maybe!(self.visit_label_any(constraint.label_any().unwrap().as_ref()));
                var_type = var_type.type_(scoped_label.into_label()).into_type();
            } else {
                panic!("visit_variable_type: not implemented")
            }
        }
        ParserResult::Pattern(var_type.into_pattern())
    }

    fn visit_type_constraint(&mut self, _ctx: &Type_constraintContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_variable_things(&mut self, _ctx: &Variable_thingsContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_variable_thing_any(
        &mut self,
        ctx: &Variable_thing_anyContext<'input>,
    ) -> Self::Return {
        if let Some(var_thing) = ctx.variable_thing() {
            self.visit_variable_thing(var_thing.as_ref())
        } else if let Some(var_relation) = ctx.variable_relation() {
            self.visit_variable_relation(var_relation.as_ref())
        } else if let Some(var_attribute) = ctx.variable_attribute() {
            self.visit_variable_attribute(var_attribute.as_ref())
        } else {
            ParserResult::Err(ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]))
        }
    }

    fn visit_variable_thing(&mut self, ctx: &Variable_thingContext<'input>) -> Self::Return {
        let mut var_thing = self.get_var(ctx.VAR_().unwrap().as_ref()).into_thing();
        if let Some(isa) = ctx.ISA_() {
            var_thing = var_thing
                .constrain_thing(
                    maybe_unwrap!(
                        self.get_isa_constraint(isa.as_ref(), ctx.type_().unwrap().as_ref())
                    )
                    .into_thing_constraint(),
                )
                .into_thing()
        }
        if let Some(attributes) = ctx.attributes() {
            var_thing = self
                .visit_attributes(attributes.as_ref())
                .into_constraints()
                .into_iter()
                .fold(var_thing, |var_thing, constraint| {
                    var_thing
                        .constrain_thing(constraint.into_thing())
                        .into_thing()
                });
        }
        ParserResult::Pattern(var_thing.into_pattern())
    }

    fn visit_variable_relation(&mut self, ctx: &Variable_relationContext<'input>) -> Self::Return {
        let mut relation = match ctx.VAR_() {
            Some(var) => self.get_var(var.as_ref()),
            None => UnboundVariable::hidden(),
        }
        .constrain_thing(
            maybe!(self.visit_relation(ctx.relation().unwrap().as_ref()))
                .into_constraint()
                .into_thing(),
        );

        if let Some(isa) = ctx.ISA_() {
            relation = relation.constrain_thing(
                maybe_unwrap!(self.get_isa_constraint(isa.as_ref(), ctx.type_().unwrap().as_ref()))
                    .into_thing_constraint(),
            );
        }

        if let Some(_attributes) = ctx.attributes() {
            todo!();
        }

        ParserResult::Pattern(relation.into_pattern())
    }

    fn visit_variable_attribute(
        &mut self,
        ctx: &Variable_attributeContext<'input>,
    ) -> Self::Return {
        let mut attribute = match ctx.VAR_() {
            Some(var) => self.get_var(var.as_ref()),
            None => UnboundVariable::hidden(),
        }
        .constrain_thing(
            maybe!(self.visit_predicate(ctx.predicate().unwrap().as_ref()))
                .into_constraint()
                .into_thing(),
        );

        if let Some(isa) = ctx.ISA_() {
            attribute = attribute.constrain_thing(
                maybe_unwrap!(self.get_isa_constraint(isa.as_ref(), ctx.type_().unwrap().as_ref()))
                    .into_thing_constraint(),
            );
        }

        if let Some(_attributes) = ctx.attributes() {
            todo!();
        }

        ParserResult::Pattern(attribute.into_pattern())
    }

    fn visit_relation(&mut self, ctx: &RelationContext<'input>) -> Self::Return {
        let mut role_players = Vec::new();
        for i in 0.. {
            if let Some(role_player_ctx) = ctx.role_player(i) {
                let player =
                    self.get_var(role_player_ctx.player().unwrap().VAR_().unwrap().as_ref());
                role_players.push(if let Some(type_) = role_player_ctx.type_() {
                    match maybe!(self.visit_type_(type_.as_ref())) {
                        ParserResult::Label(label) => RolePlayerConstraint::from((label, player)),
                        ParserResult::Pattern(var) => {
                            RolePlayerConstraint::from((var.into_type_variable(), player))
                        }
                        other => panic!("{:?}", other),
                    }
                } else {
                    RolePlayerConstraint::from(player)
                });
            } else {
                break;
            }
        }
        ParserResult::Constraint(RelationConstraint::new(role_players).into_constraint())
    }

    fn visit_role_player(&mut self, _ctx: &Role_playerContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_player(&mut self, _ctx: &PlayerContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_attributes(&mut self, ctx: &AttributesContext<'input>) -> Self::Return {
        let mut constraints = Vec::new();
        for i in 0.. {
            if let Some(attribute_ctx) = ctx.attribute(i) {
                constraints
                    .push(maybe!(self.visit_attribute(attribute_ctx.as_ref())).into_constraint());
            } else {
                break;
            }
        }
        ParserResult::Constraints(constraints)
    }

    fn visit_attribute(&mut self, ctx: &AttributeContext<'input>) -> Self::Return {
        let has = if let Some(label) = ctx.label() {
            if let Some(var) = ctx.VAR_() {
                HasConstraint::from_typed_variable(
                    label.get_text(),
                    self.get_var(var.as_ref()).into_variable(),
                )
            } else if let Some(predicate) = ctx.predicate() {
                HasConstraint::from_value(
                    label.get_text(),
                    maybe!(self.visit_predicate(predicate.as_ref()))
                        .into_constraint()
                        .into_thing()
                        .into_value(),
                )
            } else {
                return ParserResult::Err(ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]));
            }
        } else if let Some(_) = ctx.VAR_() {
            todo!()
        } else {
            return ParserResult::Err(ILLEGAL_GRAMMAR.format(&[&ctx.get_text()]));
        };

        ParserResult::Constraint(has.into_constraint())
    }

    fn visit_predicate(&mut self, ctx: &PredicateContext<'input>) -> Self::Return {
        let (predicate, value) = if let Some(value) = ctx.value() {
            (
                Predicate::Eq,
                maybe!(self.visit_value(value.as_ref())).into_value(),
            )
        } else if let Some(equality) = ctx.predicate_equality() {
            (
                Predicate::from(equality.get_text()),
                if let Some(_value) = ctx.predicate_value().unwrap().value() {
                    todo!()
                } else if let Some(var) = ctx.predicate_value().unwrap().VAR_() {
                    Value::from(self.get_var(var.as_ref()))
                } else {
                    return ParserResult::Err(ILLEGAL_STATE.format(&[]));
                },
            )
        } else {
            todo!()
        };

        ParserResult::Constraint(ValueConstraint::new(predicate, value).into_constraint())
    }

    fn visit_predicate_equality(
        &mut self,
        _ctx: &Predicate_equalityContext<'input>,
    ) -> Self::Return {
        todo!()
    }

    fn visit_predicate_substring(
        &mut self,
        _ctx: &Predicate_substringContext<'input>,
    ) -> Self::Return {
        todo!()
    }

    fn visit_predicate_value(&mut self, _ctx: &Predicate_valueContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_schema_rule(&mut self, _ctx: &Schema_ruleContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_type_any(&mut self, ctx: &Type_anyContext<'input>) -> Self::Return {
        if let Some(var) = ctx.VAR_() {
            ParserResult::Pattern(Pattern::from(self.get_var(var.as_ref())))
        } else if let Some(type_) = ctx.type_() {
            self.visit_type_(type_.as_ref())
        } else if let Some(scoped) = ctx.type_scoped() {
            self.visit_type_scoped(scoped.as_ref())
        } else {
            panic!("null type label")
        }
    }

    fn visit_type_scoped(&mut self, ctx: &Type_scopedContext<'input>) -> Self::Return {
        if let Some(scoped) = ctx.label_scoped() {
            self.visit_label_scoped(scoped.as_ref())
        } else if let Some(var) = ctx.VAR_() {
            ParserResult::Pattern(Pattern::from(self.get_var(var.as_ref())))
        } else {
            panic!("null scoped type label")
        }
    }

    fn visit_type_(&mut self, ctx: &Type_Context<'input>) -> Self::Return {
        if let Some(label) = ctx.label() {
            ParserResult::Label(label.get_text())
        } else if let Some(var) = ctx.VAR_() {
            ParserResult::Pattern(self.get_var(var.as_ref()).into_pattern())
        } else {
            panic!("")
        }
    }

    fn visit_label_any(&mut self, ctx: &Label_anyContext<'input>) -> Self::Return {
        if let Some(label) = ctx.label() {
            ParserResult::Label(label.get_text())
        } else {
            panic!("visit_label_any: not implemented")
        }
    }

    fn visit_label_scoped(&mut self, ctx: &Label_scopedContext<'input>) -> Self::Return {
        let parts: Vec<String> = ctx.get_text().split(":").map(String::from).collect();
        ParserResult::ScopedLabel((parts[0].clone(), parts[1].clone()))
    }

    fn visit_label(&mut self, _ctx: &LabelContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_labels(&mut self, _ctx: &LabelsContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_label_array(&mut self, _ctx: &Label_arrayContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_schema_native(&mut self, _ctx: &Schema_nativeContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_type_native(&mut self, _ctx: &Type_nativeContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_value_type(&mut self, _ctx: &Value_typeContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_value(&mut self, ctx: &ValueContext<'input>) -> Self::Return {
        if let Some(string) = ctx.STRING_() {
            ParserResult::Value(Value::from(self.get_string(string.as_ref())))
        } else if let Some(date_time) = ctx.DATETIME_() {
            ParserResult::Value(maybe_unwrap!(Value::try_from(maybe_unwrap!(
                self.get_date_time(date_time.as_ref())
            ))))
        } else if let Some(date) = ctx.DATE_() {
            ParserResult::Value(maybe_unwrap!(Value::try_from(maybe_unwrap!(
                self.get_date(date.as_ref())
            ))))
        } else {
            todo!()
        }
    }

    fn visit_regex(&mut self, _ctx: &RegexContext<'input>) -> Self::Return {
        todo!()
    }

    fn visit_unreserved(&mut self, _ctx: &UnreservedContext<'input>) -> Self::Return {
        todo!()
    }
}
