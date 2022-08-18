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

use crate::query::*;
use crate::typeql_match;

pub enum ParserReturn {
    Label(String),
    Queries(Vec<Query>),
    Query(Query),
    Pattern(Pattern),
    Patterns(Vec<Pattern>),
    ToDo(),
}

impl ParserReturn {
    pub fn into_query(self) -> Query {
        if let ParserReturn::Query(q) = self {
            q
        } else {
            panic!("")
        }
    }

    fn into_label(self) -> String {
        if let ParserReturn::Label(s) = self {
            s
        } else {
            panic!("")
        }
    }

    fn into_pattern(self) -> Pattern {
        if let ParserReturn::Pattern(p) = self {
            p
        } else {
            panic!("")
        }
    }

    fn into_patterns(self) -> Vec<Pattern> {
        if let ParserReturn::Patterns(p) = self {
            p
        } else {
            panic!("")
        }
    }
}

impl Default for ParserReturn {
    fn default() -> Self {
        ParserReturn::ToDo()
    }
}

pub struct Parser {
    state: ParserReturn,
}

impl Default for Parser {
    fn default() -> Self {
        Parser {
            state: ParserReturn::default(),
        }
    }
}

impl Parser {
    fn get_var(&mut self, var: &TerminalNode<TypeQLRustParserContextType>) -> UnboundVariable {
        let name = &var.symbol.get_text()[1..];
        if name == "_" {
            UnboundVariable {
                reference: Reference::Anonymous(()),
            }
        } else {
            UnboundVariable {
                reference: Reference::Named(String::from(name)),
            }
        }
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
        &mut self.state
    }

    fn aggregate_results(&self, _aggregate: Self::Return, _next: Self::Return) -> Self::Return {
        ParserReturn::ToDo()
    }
}

impl<'input> TypeQLRustVisitorCompat<'input> for Parser {
    fn visit_eof_query(&mut self, ctx: &Eof_queryContext<'input>) -> Self::Return {
        self.visit_query(ctx.query().unwrap().as_ref())
    }

    fn visit_eof_queries(&mut self, ctx: &Eof_queriesContext<'input>) -> Self::Return {
        ParserReturn::Queries(
            ctx.query_all()
                .iter()
                .map(|query_ctx| self.visit_query(query_ctx.as_ref()).into_query())
                .collect(),
        )
    }

    fn visit_eof_pattern(&mut self, ctx: &Eof_patternContext<'input>) -> Self::Return {
        self.visit_pattern(ctx.pattern().unwrap().as_ref())
    }

    fn visit_eof_patterns(&mut self, ctx: &Eof_patternsContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_eof_definables(&mut self, ctx: &Eof_definablesContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_eof_variable(&mut self, ctx: &Eof_variableContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_eof_label(&mut self, ctx: &Eof_labelContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_eof_schema_rule(&mut self, ctx: &Eof_schema_ruleContext<'input>) -> Self::Return {
        self.visit_children(ctx)
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
        ParserReturn::Query(Query::Match(typeql_match(
            self.visit_patterns(ctx.patterns().unwrap().as_ref())
                .into_patterns(),
        )))
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
        self.visit_children(ctx)
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
            ctx.pattern_all()
                .iter()
                .map(|pattern| self.visit_pattern(pattern).into_pattern())
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
        } else {
            panic!("visit_pattern_variable: not implemented")
        }
    }

    fn visit_variable_concept(&mut self, ctx: &Variable_conceptContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_variable_type(&mut self, ctx: &Variable_typeContext<'input>) -> Self::Return {
        self.visit_children(ctx)
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
        } else {
            panic!("visit_variable_thing_any: not implemented")
        }
    }

    fn visit_variable_thing(&mut self, ctx: &Variable_thingContext<'input>) -> Self::Return {
        let unscoped = self.get_var(ctx.VAR_().unwrap().as_ref());
        if let Some(isa) = ctx.ISA_() {
            ParserReturn::Pattern(Pattern::Conjunctable(Conjunctable::from(
                unscoped.constrain(
                    self.get_isa_constraint(isa.as_ref(), ctx.type_().unwrap().as_ref()),
                ),
            )))
        } else {
            panic!("visit_variable_thing: not implemented")
        }
    }

    fn visit_variable_relation(&mut self, ctx: &Variable_relationContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_variable_attribute(
        &mut self,
        ctx: &Variable_attributeContext<'input>,
    ) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_relation(&mut self, ctx: &RelationContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_role_player(&mut self, ctx: &Role_playerContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_player(&mut self, ctx: &PlayerContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_attributes(&mut self, ctx: &AttributesContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_attribute(&mut self, ctx: &AttributeContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_predicate(&mut self, ctx: &PredicateContext<'input>) -> Self::Return {
        self.visit_children(ctx)
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
        self.visit_children(ctx)
    }

    fn visit_type_scoped(&mut self, ctx: &Type_scopedContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_type_(&mut self, ctx: &Type_Context<'input>) -> Self::Return {
        ParserReturn::Label(ctx.label().unwrap().get_text())
    }

    fn visit_label_any(&mut self, ctx: &Label_anyContext<'input>) -> Self::Return {
        self.visit_children(ctx)
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
        self.visit_children(ctx)
    }

    fn visit_regex(&mut self, ctx: &RegexContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }

    fn visit_unreserved(&mut self, ctx: &UnreservedContext<'input>) -> Self::Return {
        self.visit_children(ctx)
    }
}
