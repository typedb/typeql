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

use antlr_rust::token::Token;
use antlr_rust::tree::TerminalNode;
use antlr_rust::tree::{ParseTree, ParseTreeVisitorCompat};

use typeql_grammar::typeqlrustparser::*;
use typeql_grammar::typeqlrustvisitor::TypeQLRustVisitorCompat;

use crate::enum_getter;
use crate::pattern::*;
use crate::query::*;
use crate::typeql_match;

#[derive(Debug)]
pub struct Definable;

#[derive(Debug)]
pub enum ParserReturn {
    Label(String),
    Queries(Vec<Query>),
    Query(Query),
    Pattern(Pattern),
    Patterns(Vec<Pattern>),
    Value(Value),
    Constraint(Constraint),
    Constraints(Vec<Constraint>),
    Definable(Definable),
    Definables(Vec<Definable>),
    None,
}

impl ParserReturn {
    enum_getter!(into_query, Query, Query);
    enum_getter!(into_queries, Queries, Vec<Query>);
    enum_getter!(into_label, Label, String);
    enum_getter!(into_pattern, Pattern, Pattern);
    enum_getter!(into_patterns, Patterns, Vec<Pattern>);
    enum_getter!(into_value, Value, Value);
    enum_getter!(into_constraint, Constraint, Constraint);
    enum_getter!(into_constraints, Constraints, Vec<Constraint>);
    enum_getter!(into_definable, Definable, Definable);
    enum_getter!(into_definables, Definables, Vec<Definable>);
}

impl Default for ParserReturn {
    fn default() -> Self {
        ParserReturn::None
    }
}

pub struct Parser;

impl Default for Parser {
    fn default() -> Self {
        Parser {}
    }
}

impl Parser {
    fn get_var(&mut self, var: &TerminalNode<TypeQLRustParserContextType>) -> UnboundVariable {
        let name = &var.symbol.get_text()[1..];
        if name == "_" {
            UnboundVariable::anonymous()
        } else {
            UnboundVariable::named(String::from(name))
        }
    }

    fn get_string(&self, string: &TerminalNode<TypeQLRustParserContextType>) -> String {
        let quoted = string.get_text();
        String::from(&quoted[1..quoted.len() - 1])
    }

    fn get_isa_constraint(
        &mut self,
        _isa: &TerminalNode<TypeQLRustParserContextType>,
        ctx: &Type_ContextAll,
    ) -> IsaConstraint {
        IsaConstraint {
            type_name: self.visit_type_(ctx).into_label(),
            is_explicit: false,
        }
    }
}

impl<'input> ParseTreeVisitorCompat<'input> for Parser {
    type Node = TypeQLRustParserContextType;
    type Return = ParserReturn;

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
        ParserReturn::Queries(
            (0..)
                .map_while(|i| ctx.query(i))
                .map(|query_ctx| self.visit_query(query_ctx.as_ref()).into_query())
                .collect(),
        )
    }

    fn visit_eof_pattern(&mut self, ctx: &Eof_patternContext<'input>) -> Self::Return {
        self.visit_pattern(ctx.pattern().unwrap().as_ref())
    }

    fn visit_eof_patterns(&mut self, ctx: &Eof_patternsContext<'input>) -> Self::Return {
        self.visit_patterns(ctx.patterns().unwrap().as_ref())
    }

    fn visit_eof_definables(&mut self, ctx: &Eof_definablesContext<'input>) -> Self::Return {
        let definables = ctx.definables().unwrap();
        ParserReturn::Definables(
            (0..)
                .map_while(|i| definables.definable(i))
                .map(|definable_ctx| {
                    self.visit_definable(definable_ctx.as_ref())
                        .into_definable()
                })
                .collect(),
        )
    }

    fn visit_eof_variable(&mut self, ctx: &Eof_variableContext<'input>) -> Self::Return {
        self.visit_pattern_variable(ctx.pattern_variable().unwrap().as_ref())
    }

    fn visit_eof_label(&mut self, ctx: &Eof_labelContext<'input>) -> Self::Return {
        ParserReturn::Label(ctx.label().unwrap().get_text())
    }

    fn visit_eof_schema_rule(&mut self, ctx: &Eof_schema_ruleContext<'input>) -> Self::Return {
        self.visit_schema_rule(ctx.schema_rule().unwrap().as_ref())
    }

    fn visit_query(&mut self, ctx: &QueryContext<'input>) -> Self::Return {
        if let Some(query_match) = ctx.query_match() {
            self.visit_query_match(query_match.as_ref())
        } else {
            ParserReturn::Query(Query::Dud(String::from("Unsupported")))
        }
    }

    fn visit_query_define(&mut self, ctx: &Query_defineContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_query_undefine(&mut self, ctx: &Query_undefineContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_query_insert(&mut self, ctx: &Query_insertContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_query_delete_or_update(
        &mut self,
        ctx: &Query_delete_or_updateContext<'input>,
    ) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_query_match(&mut self, ctx: &Query_matchContext<'input>) -> Self::Return {
        let mut match_query = typeql_match(
            self.visit_patterns(ctx.patterns().unwrap().as_ref())
                .into_patterns(),
        )
        .into_match();
        if let Some(modifiers) = ctx.modifiers() {
            if let Some(filter) = modifiers.filter() {
                match_query =
                    match_query.filter(self.visit_filter(filter.as_ref()).into_patterns());
            }
        }
        ParserReturn::Query(match_query.into_query())
    }

    fn visit_query_match_aggregate(
        &mut self,
        ctx: &Query_match_aggregateContext<'input>,
    ) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_query_match_group(&mut self, ctx: &Query_match_groupContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_query_match_group_agg(
        &mut self,
        ctx: &Query_match_group_aggContext<'input>,
    ) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_modifiers(&mut self, ctx: &ModifiersContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_filter(&mut self, ctx: &FilterContext<'input>) -> Self::Return {
        ParserReturn::Patterns(
            (0..)
                .map_while(|i| ctx.VAR_(i))
                .map(|x| self.get_var(x.as_ref()).into_pattern())
                .collect(),
        )
    }

    fn visit_sort(&mut self, ctx: &SortContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_offset(&mut self, ctx: &OffsetContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_limit(&mut self, ctx: &LimitContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_match_aggregate(&mut self, ctx: &Match_aggregateContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_aggregate_method(&mut self, ctx: &Aggregate_methodContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_match_group(&mut self, ctx: &Match_groupContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_definables(&mut self, ctx: &DefinablesContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_definable(&mut self, ctx: &DefinableContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_patterns(&mut self, ctx: &PatternsContext<'input>) -> Self::Return {
        ParserReturn::Patterns(
            (0..)
                .map_while(|i| ctx.pattern(i))
                .map(|pattern| self.visit_pattern(pattern.as_ref()).into_pattern())
                .collect(),
        )
    }

    fn visit_pattern(&mut self, ctx: &PatternContext<'input>) -> Self::Return {
        if let Some(var) = ctx.pattern_variable() {
            self.visit_pattern_variable(var.as_ref())
        } else {
            panic!("visit_pattern: not implemented")
        }
    }

    fn visit_pattern_conjunction(
        &mut self,
        ctx: &Pattern_conjunctionContext<'input>,
    ) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_pattern_disjunction(
        &mut self,
        ctx: &Pattern_disjunctionContext<'input>,
    ) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_pattern_negation(&mut self, ctx: &Pattern_negationContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_pattern_variable(&mut self, ctx: &Pattern_variableContext<'input>) -> Self::Return {
        if let Some(var_thing_any) = ctx.variable_thing_any() {
            self.visit_variable_thing_any(var_thing_any.as_ref())
        } else if let Some(var_type) = ctx.variable_type() {
            self.visit_variable_type(var_type.as_ref())
        } else {
            panic!("visit_pattern_variable: not implemented")
        }
    }

    fn visit_variable_concept(&mut self, ctx: &Variable_conceptContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_variable_type(&mut self, ctx: &Variable_typeContext<'input>) -> Self::Return {
        let mut var_type = match self.visit_type_any(ctx.type_any().unwrap().as_ref()) {
            ParserReturn::Pattern(p) => p.into_type_variable(),
            ParserReturn::Label(p) => UnboundVariable::hidden().type_(p),
            other @ _ => panic!("{:?}", other),
        };
        for constraint in (0..).map_while(|i| ctx.type_constraint(i)) {
            if constraint.RELATES().is_some() {
                let _overridden: Option<u8> = match constraint.AS() {
                    None => None,
                    Some(_) => todo!(),
                };
                var_type = var_type.constrain_type(
                    match self.visit_type_(constraint.type_(0).unwrap().as_ref()) {
                        ParserReturn::Label(label) => RelatesConstraint::from(label),
                        ParserReturn::Pattern(var) => {
                            RelatesConstraint::from(var.into_unbound_variable())
                        }
                        _ => panic!(""),
                    }
                    .into_type_constraint(),
                );
            } else if constraint.TYPE().is_some() {
                let scoped_label = self.visit_label_any(constraint.label_any().unwrap().as_ref());
                var_type = var_type.type_(scoped_label.into_label());
            } else {
                panic!("visit_variable_type: not implemented")
            }
        }
        ParserReturn::Pattern(var_type.into_pattern())
    }

    fn visit_type_constraint(&mut self, ctx: &Type_constraintContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_variable_things(&mut self, ctx: &Variable_thingsContext<'input>) -> Self::Return {
        self.visit_children(ctx)
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
            panic!("visit_variable_thing_any: illegal grammar")
        }
    }

    fn visit_variable_thing(&mut self, ctx: &Variable_thingContext<'input>) -> Self::Return {
        let mut var_thing = self.get_var(ctx.VAR_().unwrap().as_ref()).into_thing();
        if let Some(isa) = ctx.ISA_() {
            var_thing = var_thing.constrain_thing(
                self.get_isa_constraint(isa.as_ref(), ctx.type_().unwrap().as_ref())
                    .into_thing_constraint(),
            )
        }
        if let Some(attributes) = ctx.attributes() {
            var_thing = self
                .visit_attributes(attributes.as_ref())
                .into_constraints()
                .into_iter()
                .fold(var_thing, |var_thing, constraint| {
                    var_thing.constrain_thing(constraint.into_thing())
                });
        }
        ParserReturn::Pattern(var_thing.into_pattern())
    }

    fn visit_variable_relation(&mut self, ctx: &Variable_relationContext<'input>) -> Self::Return {
        let mut relation = match ctx.VAR_() {
            Some(var) => self.get_var(var.as_ref()),
            None => UnboundVariable::hidden(),
        }
        .constrain_thing(
            self.visit_relation(ctx.relation().unwrap().as_ref())
                .into_constraint()
                .into_thing(),
        );

        if let Some(isa) = ctx.ISA_() {
            relation = relation.constrain_thing(
                self.get_isa_constraint(isa.as_ref(), ctx.type_().unwrap().as_ref())
                    .into_thing_constraint(),
            );
        }

        if let Some(_attributes) = ctx.attributes() {
            todo!();
        }

        ParserReturn::Pattern(relation.into_pattern())
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
            self.visit_predicate(ctx.predicate().unwrap().as_ref())
                .into_constraint()
                .into_thing(),
        );

        if let Some(isa) = ctx.ISA_() {
            attribute = attribute.constrain_thing(
                self.get_isa_constraint(isa.as_ref(), ctx.type_().unwrap().as_ref())
                    .into_thing_constraint(),
            );
        }

        if let Some(_attributes) = ctx.attributes() {
            todo!();
        }

        ParserReturn::Pattern(attribute.into_pattern())
    }

    fn visit_relation(&mut self, ctx: &RelationContext<'input>) -> Self::Return {
        ParserReturn::Constraint(
            RelationConstraint::new(
                (0..)
                    .map_while(|i| ctx.role_player(i))
                    .map(|ctx| {
                        let player = self.get_var(ctx.player().unwrap().VAR_().unwrap().as_ref());
                        if let Some(type_) = ctx.type_() {
                            RolePlayerConstraint::from((
                                self.visit_type_(type_.as_ref()).into_label(),
                                player,
                            ))
                        } else {
                            RolePlayerConstraint::from(player)
                        }
                    })
                    .collect(),
            )
            .into_constraint(),
        )
    }

    fn visit_role_player(&mut self, ctx: &Role_playerContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_player(&mut self, ctx: &PlayerContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_attributes(&mut self, ctx: &AttributesContext<'input>) -> Self::Return {
        ParserReturn::Constraints(
            (0..)
                .map_while(|i| ctx.attribute(i))
                .map(|attribute| self.visit_attribute(attribute.as_ref()).into_constraint())
                .collect(),
        )
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
                    self.visit_predicate(predicate.as_ref())
                        .into_constraint()
                        .into_thing()
                        .into_value(),
                )
            } else {
                panic!("Illegal grammar")
            }
        } else if let Some(_) = ctx.VAR_() {
            todo!()
        } else {
            panic!("illegal grammar")
        };

        ParserReturn::Constraint(has.into_constraint())
    }

    fn visit_predicate(&mut self, ctx: &PredicateContext<'input>) -> Self::Return {
        let (predicate, value) = if let Some(value) = ctx.value() {
            (Predicate::Eq, self.visit_value(value.as_ref()).into_value())
        } else {
            todo!()
        };

        ParserReturn::Constraint(ValueConstraint::new(predicate, value).into_constraint())
    }

    fn visit_predicate_equality(
        &mut self,
        ctx: &Predicate_equalityContext<'input>,
    ) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_predicate_substring(
        &mut self,
        ctx: &Predicate_substringContext<'input>,
    ) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_predicate_value(&mut self, ctx: &Predicate_valueContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_schema_rule(&mut self, ctx: &Schema_ruleContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_type_any(&mut self, ctx: &Type_anyContext<'input>) -> Self::Return {
        if let Some(var) = ctx.VAR_() {
            ParserReturn::Pattern(Pattern::from(self.get_var(var.as_ref())))
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
            ParserReturn::Pattern(Pattern::from(self.get_var(var.as_ref())))
        } else {
            panic!("null scoped type label")
        }
    }

    fn visit_type_(&mut self, ctx: &Type_Context<'input>) -> Self::Return {
        if let Some(label) = ctx.label() {
            ParserReturn::Label(label.get_text())
        } else if let Some(var) = ctx.VAR_() {
            ParserReturn::Pattern(self.get_var(var.as_ref()).into_pattern())
        } else {
            panic!("")
        }
    }

    fn visit_label_any(&mut self, ctx: &Label_anyContext<'input>) -> Self::Return {
        if let Some(label) = ctx.label() {
            ParserReturn::Label(label.get_text())
        } else {
            panic!("visit_label_any: not implemented")
        }
    }

    fn visit_label_scoped(&mut self, ctx: &Label_scopedContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_label(&mut self, ctx: &LabelContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_labels(&mut self, ctx: &LabelsContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_label_array(&mut self, ctx: &Label_arrayContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_schema_native(&mut self, ctx: &Schema_nativeContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_type_native(&mut self, ctx: &Type_nativeContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_value_type(&mut self, ctx: &Value_typeContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_value(&mut self, ctx: &ValueContext<'input>) -> Self::Return {
        if let Some(string) = ctx.STRING_() {
            ParserReturn::Value(Value::from(self.get_string(string.as_ref())))
        } else {
            todo!()
        }
    }

    fn visit_regex(&mut self, ctx: &RegexContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_unreserved(&mut self, ctx: &UnreservedContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }
}
