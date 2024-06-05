/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use chrono::{NaiveDate, NaiveDateTime};
use pest::{
    pratt_parser::{Assoc, Op, PrattParser},
    Parser,
};
use pest_derive::Parser;

use crate::{
    common::{
        date_time,
        error::{syntax_error, TypeQLError, TypeQLError::IllegalGrammar},
        string::{unescape_regex, unquote},
        token,
        token::Aggregate,
        validatable::Validatable,
        Result,
    },
    // parser::Rule::clause_undefine,
    pattern::{
        Annotation, ConceptStatement, Conjunction, Constant, Definable, Disjunction,
        Expression, Function, HasConstraint, IsaConstraint, Label, Negation, Operation, OwnsConstraint, Pattern,
        PlaysConstraint, Comparison, RelatesConstraint, RelationConstraint, RolePlayerConstraint, RuleLabel, Statement,
        SubConstraint, ThingStatement, ThingStatementBuilder, TypeStatement, TypeStatementBuilder, Value,
        ValueStatement, ValueStatementBuilder,
    },
    query::{
        modifier::{sorting, Modifiers, Sorting},
        AggregateQueryBuilder, Filter, Limit, MatchClause, Offset, Projection, ProjectionAttribute, ProjectionKeyLabel,
        ProjectionKeyVar, ProjectionSubquery, Query, TypeQLDefine, TypeQLDelete, TypeQLFetch, TypeQLGet,
        TypeQLGetAggregate, TypeQLGetGroup, TypeQLGetGroupAggregate, TypeQLInsert, TypeQLUndefine, TypeQLUpdate,
    },
};
use crate::variable::{Variable, TypeReference};

#[cfg(test)]
mod test;

#[derive(Parser)]
#[grammar = "parser/typeql.pest"]
pub(crate) struct TypeQLParser;

type Node<'a> = pest::iterators::Pair<'a, Rule>;
type ChildNodes<'a> = pest::iterators::Pairs<'a, Rule>;

trait IntoChildNodes<'a> {
    fn into_child(self) -> Result<Node<'a>>;
    fn into_children(self) -> ChildNodes<'a>;
}

impl<'a> IntoChildNodes<'a> for Node<'a> {
    fn into_child(self) -> Result<Node<'a>> {
        let mut children = self.into_children();
        let child = children.consume_any();
        match children.try_consume_any() {
            None => Ok(child),
            Some(next) => Err(IllegalGrammar { input: format!("{child} is followed by more tokens: {next}") }.into()),
        }
    }

    fn into_children(self) -> ChildNodes<'a> {
        self.into_inner()
    }
}

trait RuleMatcher<'a> {
    fn skip_expected(&mut self, rule: Rule) -> &mut Self;
    fn consume_expected(&mut self, rule: Rule) -> Node<'a>;
    fn peek_rule(&mut self) -> Option<Rule>;
    fn try_consume_expected(&mut self, rule: Rule) -> Option<Node<'a>>;
    fn consume_any(&mut self) -> Node<'a>;
    fn try_consume_any(&mut self) -> Option<Node<'a>>;
}

impl<'a, T: Iterator<Item = Node<'a>> + Clone> RuleMatcher<'a> for T {
    fn skip_expected(&mut self, rule: Rule) -> &mut Self {
        self.consume_expected(rule);
        self
    }

    fn consume_expected(&mut self, _rule: Rule) -> Node<'a> {
        let next = self.consume_any();
        assert_eq!(next.as_rule(), _rule);
        next
    }

    fn consume_any(&mut self) -> Node<'a> {
        self.next().expect("attempting to consume from an empty iterator")
    }

    fn peek_rule(&mut self) -> Option<Rule> {
        self.clone().peekable().peek().map(|node| node.as_rule())
    }

    fn try_consume_expected(&mut self, rule: Rule) -> Option<Node<'a>> {
        (Some(rule) == self.peek_rule()).then(|| self.consume_any())
    }

    fn try_consume_any(&mut self) -> Option<Node<'a>> {
        self.next()
    }
}

fn parse(rule: Rule, string: &str) -> Result<ChildNodes<'_>> {
    let result = TypeQLParser::parse(rule, string);
    match result {
        Ok(nodes) => Ok(nodes),
        Err(error) => Err(syntax_error(string, error).into()),
    }
}

fn parse_single(rule: Rule, string: &str) -> Result<Node<'_>> {
    Ok(parse(rule, string)?.consume_any())
}

// pub(crate) fn visit_eof_query(query: &str) -> Result<Query> {
//     visit_query(parse_single(Rule::eof_query, query)?.into_children().consume_expected(Rule::query)).validated()
// }
//
// pub(crate) fn visit_eof_queries(queries: &str) -> Result<impl Iterator<Item = Result<Query>> + '_> {
//     Ok(parse(Rule::eof_queries, queries)?
//         .consume_expected(Rule::eof_queries)
//         .into_children()
//         .filter(|child| matches!(child.as_rule(), Rule::query))
//         .map(|query| visit_query(query).validated()))
// }

// pub(crate) fn visit_eof_pattern(pattern: &str) -> Result<Pattern> {
//     visit_pattern(parse_single(Rule::eof_pattern, pattern)?.into_children().consume_expected(Rule::pattern)).validated()
// }
//
// pub(crate) fn visit_eof_patterns(patterns: &str) -> Result<Vec<Pattern>> {
//     visit_patterns(parse_single(Rule::eof_patterns, patterns)?.into_children().consume_expected(Rule::patterns))
//         .into_iter()
//         .map(Validatable::validated)
//         .collect()
// }
//
// pub(crate) fn visit_eof_definables(definables: &str) -> Result<Vec<Definable>> {
//     visit_definables(parse_single(Rule::eof_definables, definables)?.into_children().consume_expected(Rule::definables))
//         .into_iter()
//         .map(Validatable::validated)
//         .collect()
// }
//
// pub(crate) fn visit_eof_statement(statement: &str) -> Result<Statement> {
//     visit_statement(parse_single(Rule::eof_statement, statement)?.into_children().consume_expected(Rule::statement))
//         .validated()
// }
//
// pub(crate) fn visit_eof_label(label: &str) -> Result<Label> {
//     let parsed = parse_single(Rule::eof_label, label)?.into_children().consume_expected(Rule::label);
//     let string = parsed.as_str();
//     if string != label {
//         Err(TypeQLError::InvalidTypeLabel { label: label.to_string() })?;
//     }
//     Ok(string.into())
// }
//
// pub(crate) fn visit_eof_schema_rule(rule: &str) -> Result<crate::pattern::Rule> {
//     visit_schema_rule(parse_single(Rule::eof_schema_rule, rule)?).validated()
// }
//
// fn get_string_from_quoted(string: Node<'_>) -> String {
//     debug_assert_eq!(string.as_rule(), Rule::QUOTED_STRING);
//     unquote(string.as_str())
// }
//
// fn get_regex(string: Node<'_>) -> String {
//     debug_assert_eq!(string.as_rule(), Rule::QUOTED_STRING);
//     unescape_regex(&unquote(string.as_str()))
// }
//
// fn get_long(long: Node<'_>) -> i64 {
//     debug_assert_eq!(long.as_rule(), Rule::LONG_);
//     long_from_string(long.as_str())
// }
//
// fn long_from_string(string: &str) -> i64 {
//     string.parse().unwrap_or_else(|_| panic!("{}", TypeQLError::IllegalGrammar { input: string.to_owned() }))
// }
//
// fn double_from_string(string: &str) -> f64 {
//     string.parse().unwrap_or_else(|_| panic!("{}", TypeQLError::IllegalGrammar { input: string.to_owned() }))
// }
//
// fn get_boolean(boolean: Node<'_>) -> bool {
//     debug_assert_eq!(boolean.as_rule(), Rule::BOOLEAN_);
//     boolean
//         .as_str()
//         .parse()
//         .unwrap_or_else(|_| panic!("{}", TypeQLError::IllegalGrammar { input: boolean.to_string() }))
// }
//
// fn get_date(date: Node<'_>) -> NaiveDate {
//     debug_assert_eq!(date.as_rule(), Rule::DATE_);
//     NaiveDate::parse_from_str(date.as_str(), "%Y-%m-%d")
//         .unwrap_or_else(|_| panic!("{}", TypeQLError::IllegalGrammar { input: date.to_string() }))
// }
//
// fn get_date_time(date_time: Node<'_>) -> NaiveDateTime {
//     debug_assert_eq!(date_time.as_rule(), Rule::DATETIME_);
//     date_time::parse(date_time.as_str())
//         .unwrap_or_else(|| panic!("{}", TypeQLError::IllegalGrammar { input: date_time.to_string() }))
// }
//
// fn get_var(node: Node<'_>) -> Variable {
//     debug_assert_eq!(node.as_rule(), Rule::VAR_);
//     let name = node.as_str();
//     let (prefix, _name) = name.split_at(1);
//     match prefix {
//         "$" => get_var_concept(node.into_children().consume_expected(Rule::VAR_CONCEPT_)),
//         // "?" => ConceptVariable::Value(get_var_value(node.into_children().consume_expected(Rule::VAR_VALUE_))),
//         _ => unreachable!("Unexpected variable prefix in {node}"),
//     }
// }
//
// fn get_var_concept(node: Node<'_>) -> Variable {
//     debug_assert_eq!(node.as_rule(), Rule::VAR_CONCEPT_);
//     let name = node.as_str();
//
//     assert!(name.len() > 1);
//     assert!(name.starts_with('$'));
//     let name = &name[1..];
//
//     if name == "_" {
//         Variable::Anonymous
//     } else {
//         Variable::Named(String::from(name))
//     }
// }
//
// // fn get_var_value(node: Node<'_>) -> ValueVariable {
// //     debug_assert_eq!(node.as_rule(), Rule::VAR_VALUE_);
// //     let name = node.as_str();
// //     assert!(name.len() > 1);
// //     assert!(name.starts_with('?'));
// //     ValueVariable::Named(String::from(&name[1..]))
// // }
//
// fn get_isa_constraint(isa: Node<'_>, node: Node<'_>) -> IsaConstraint {
//     debug_assert_eq!(isa.as_rule(), Rule::ISA_);
//     let is_explicit = matches!(isa.into_child().unwrap().as_rule(), Rule::ISAX).into();
//     match visit_type_ref(node) {
//         TypeReference::Label(label) => IsaConstraint::from((label, is_explicit)),
//         TypeReference::Variable(var) => IsaConstraint::from((var, is_explicit)),
//     }
// }
//
// fn get_role_player_constraint(node: Node<'_>) -> RolePlayerConstraint {
//     debug_assert_eq!(node.as_rule(), Rule::role_player);
//     let mut children_rev = node.into_children().rev();
//     let player = get_var_concept(
//         children_rev.consume_expected(Rule::player).into_children().consume_expected(Rule::VAR_CONCEPT_),
//     );
//     if let Some(type_) = children_rev.try_consume_expected(Rule::type_ref) {
//         match visit_type_ref(type_) {
//             TypeReference::Label(label) => RolePlayerConstraint::from((label, player)),
//             TypeReference::Variable(var) => RolePlayerConstraint::from((var, player)),
//         }
//     } else {
//         RolePlayerConstraint::from(player)
//     }
// }
//
// fn get_role_players(node: Node<'_>) -> Vec<RolePlayerConstraint> {
//     node.into_children().map(get_role_player_constraint).collect()
// }
//
// fn visit_query(node: Node<'_>) -> Query {
//     debug_assert_eq!(node.as_rule(), Rule::query);
//     let mut children = node.into_children();
//     let child = children.consume_any();
//     let query = match child.as_rule() {
//         Rule::query_define => visit_query_define(child).into(),
//         Rule::query_undefine => visit_query_undefine(child).into(),
//         Rule::query_insert => visit_query_insert(child).into(),
//         Rule::query_delete => visit_query_delete(child).into(),
//         Rule::query_update => visit_query_update(child).into(),
//         Rule::query_get => visit_query_get(child).into(),
//         Rule::query_fetch => visit_query_fetch(child).into(),
//         Rule::query_get_aggregate => visit_query_get_aggregate(child).into(),
//         Rule::query_get_group => visit_query_get_group(child).into(),
//         Rule::query_get_group_agg => visit_query_get_group_agg(child).into(),
//         _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
//     };
//     debug_assert!(children.try_consume_any().is_none());
//     query
// }
//
// fn visit_query_fetch(node: Node<'_>) -> TypeQLFetch {
//     debug_assert_eq!(node.as_rule(), Rule::query_fetch);
//     let mut children = node.into_children();
//     let clause_match = visit_clause_match(children.consume_expected(Rule::clause_match));
//     let projections = visit_clause_fetch(children.consume_expected(Rule::clause_fetch));
//     let modifiers = visit_modifiers(children.consume_expected(Rule::modifiers));
//     debug_assert!(children.try_consume_any().is_none());
//     TypeQLFetch { match_clause: clause_match, projections, modifiers }
// }
//
// fn visit_clause_fetch(node: Node<'_>) -> Vec<Projection> {
//     debug_assert_eq!(node.as_rule(), Rule::clause_fetch);
//     let mut children = node.into_children();
//     children.skip_expected(Rule::FETCH);
//     let projections = children.consume_expected(Rule::projections).into_children();
//     debug_assert!(children.try_consume_any().is_none());
//     projections.map(visit_projection).collect()
// }
//
// fn visit_projection(node: Node<'_>) -> Projection {
//     debug_assert_eq!(node.as_rule(), Rule::projection);
//     let mut children = node.into_children();
//     let child = children.consume_any();
//     match child.as_rule() {
//         Rule::projection_key_var => {
//             let key_var = visit_projection_key_var(child);
//             let node = children.try_consume_any();
//             if let Some(n) = node {
//                 let attrs = visit_projection_attributes(n);
//                 Projection::Attribute(key_var, attrs)
//             } else {
//                 Projection::Variable(key_var)
//             }
//         }
//         Rule::projection_key_label => {
//             let key_label = visit_projection_key_label(child);
//             let subquery = visit_projection_subquery(children.consume_expected(Rule::projection_subquery));
//             Projection::Subquery(key_label, subquery)
//         }
//         _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
//     }
// }
//
// fn visit_projection_key_var(node: Node<'_>) -> ProjectionKeyVar {
//     debug_assert_eq!(node.as_rule(), Rule::projection_key_var);
//     let mut children = node.into_children();
//     let variable = get_var(children.consume_expected(Rule::VAR_));
//     let label = children.try_consume_expected(Rule::projection_key_as_label).map(visit_projection_as_label);
//     debug_assert!(children.try_consume_any().is_none());
//     ProjectionKeyVar { variable, label }
// }
//
// fn visit_projection_as_label(node: Node<'_>) -> ProjectionKeyLabel {
//     debug_assert_eq!(node.as_rule(), Rule::projection_key_as_label);
//     let mut children = node.into_children();
//     children.skip_expected(Rule::AS);
//     let label = visit_projection_key_label(children.consume_expected(Rule::projection_key_label));
//     debug_assert!(children.try_consume_any().is_none());
//     label
// }
//
// fn visit_projection_key_label(node: Node<'_>) -> ProjectionKeyLabel {
//     debug_assert_eq!(node.as_rule(), Rule::projection_key_label);
//     let mut children = node.into_children();
//     let child = children.consume_any();
//     match child.as_rule() {
//         Rule::QUOTED_STRING => ProjectionKeyLabel { label: get_string_from_quoted(child) },
//         Rule::label => ProjectionKeyLabel { label: child.as_str().to_owned() },
//         _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
//     }
// }
//
// fn visit_projection_attributes(node: Node<'_>) -> Vec<ProjectionAttribute> {
//     debug_assert_eq!(node.as_rule(), Rule::projection_attributes);
//     node.into_children().map(visit_projection_attribute).collect()
// }
//
// fn visit_projection_attribute(node: Node<'_>) -> ProjectionAttribute {
//     debug_assert_eq!(node.as_rule(), Rule::projection_attribute);
//     let mut children = node.into_children();
//     let attribute_label = Label::from(children.consume_expected(Rule::label).as_str());
//     let label = children.try_consume_expected(Rule::projection_key_as_label).map(visit_projection_as_label);
//     debug_assert!(children.try_consume_any().is_none());
//     ProjectionAttribute { attribute: attribute_label, label }
// }
//
// fn visit_projection_subquery(node: Node<'_>) -> ProjectionSubquery {
//     debug_assert_eq!(node.as_rule(), Rule::projection_subquery);
//     let mut children = node.into_children();
//     let child = children.consume_any();
//     match child.as_rule() {
//         Rule::query_fetch => ProjectionSubquery::Fetch(Box::new(visit_query_fetch(child))),
//         Rule::query_get_aggregate => ProjectionSubquery::GetAggregate(visit_query_get_aggregate(child)),
//         _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
//     }
// }
//
// fn visit_query_define(node: Node<'_>) -> TypeQLDefine {
//     debug_assert_eq!(node.as_rule(), Rule::query_define);
//     let mut children = node.into_children();
//     let query = visit_clause_define(children.consume_expected(Rule::clause_define));
//     debug_assert!(children.try_consume_any().is_none());
//     query
// }
//
// fn visit_clause_define(node: Node<'_>) -> TypeQLDefine {
//     debug_assert_eq!(node.as_rule(), Rule::clause_define);
//     let mut children = node.into_children();
//     children.skip_expected(Rule::DEFINE);
//     let query = TypeQLDefine::new(visit_definables(children.consume_expected(Rule::definables)));
//     debug_assert!(children.try_consume_any().is_none());
//     query
// }
//
// fn visit_query_undefine(node: Node<'_>) -> TypeQLUndefine {
//     debug_assert_eq!(node.as_rule(), Rule::query_undefine);
//     let mut children = node.into_children();
//     let query = visit_clause_undefine(children.consume_expected(clause_undefine));
//     debug_assert!(children.try_consume_any().is_none());
//     query
// }
//
// fn visit_clause_undefine(node: Node<'_>) -> TypeQLUndefine {
//     debug_assert_eq!(node.as_rule(), Rule::clause_undefine);
//     let mut children = node.into_children();
//     children.skip_expected(Rule::UNDEFINE);
//     let query = TypeQLUndefine::new(visit_definables(children.consume_expected(Rule::definables)));
//     debug_assert!(children.try_consume_any().is_none());
//     query
// }
//
// fn visit_query_insert(node: Node<'_>) -> TypeQLInsert {
//     debug_assert_eq!(node.as_rule(), Rule::query_insert);
//     let mut children = node.into_children();
//     let child = children.consume_any();
//     let query = match child.as_rule() {
//         Rule::clause_match => {
//             let clause_match = visit_clause_match(child);
//             let clause_insert = visit_clause_insert(children.consume_expected(Rule::clause_insert));
//             let modifiers = visit_modifiers(children.consume_expected(Rule::modifiers));
//             TypeQLInsert { match_clause: Some(clause_match), statements: clause_insert, modifiers }
//         }
//         Rule::clause_insert => TypeQLInsert::new(visit_clause_insert(child)),
//         _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: children.to_string() }),
//     };
//     debug_assert!(children.try_consume_any().is_none());
//     query
// }
//
// fn visit_query_delete(node: Node<'_>) -> TypeQLDelete {
//     debug_assert_eq!(node.as_rule(), Rule::query_delete);
//     let mut children = node.into_children();
//     let clause_match = visit_clause_match(children.consume_expected(Rule::clause_match));
//     let clause_delete = visit_clause_delete(children.consume_expected(Rule::clause_delete));
//     let modifiers = visit_modifiers(children.consume_expected(Rule::modifiers));
//     debug_assert!(children.try_consume_any().is_none());
//     TypeQLDelete { match_clause: clause_match, statements: clause_delete, modifiers }
// }
//
// fn visit_query_update(node: Node<'_>) -> TypeQLUpdate {
//     debug_assert_eq!(node.as_rule(), Rule::query_update);
//     let mut children = node.into_children();
//     let query_delete = visit_query_delete(children.consume_expected(Rule::query_delete));
//     let clause_insert = visit_clause_insert(children.consume_expected(Rule::clause_insert));
//     let modifiers = visit_modifiers(children.consume_expected(Rule::modifiers));
//     debug_assert!(children.try_consume_any().is_none());
//     TypeQLUpdate { query_delete, insert_statements: clause_insert, modifiers }
// }
//
// fn visit_query_get(node: Node<'_>) -> TypeQLGet {
//     debug_assert_eq!(node.as_rule(), Rule::query_get);
//     let mut children = node.into_children();
//     let clause_match = visit_clause_match(children.consume_expected(Rule::clause_match));
//     let clause_get = visit_clause_get(children.consume_expected(Rule::clause_get));
//     let modifiers = visit_modifiers(children.consume_expected(Rule::modifiers));
//     debug_assert!(children.try_consume_any().is_none());
//     TypeQLGet { match_clause: clause_match, filter: Filter { vars: clause_get }, modifiers }
// }
//
// fn visit_clause_insert(node: Node<'_>) -> Vec<ThingStatement> {
//     debug_assert_eq!(node.as_rule(), Rule::clause_insert);
//     let mut children = node.into_children();
//     children.skip_expected(Rule::INSERT);
//     let clause = visit_statement_things(children.consume_expected(Rule::statement_things));
//     debug_assert!(children.try_consume_any().is_none());
//     clause
// }
//
// fn visit_clause_delete(node: Node<'_>) -> Vec<ThingStatement> {
//     debug_assert_eq!(node.as_rule(), Rule::clause_delete);
//     let mut children = node.into_children();
//     children.skip_expected(Rule::DELETE);
//     let statements = visit_statement_things(children.consume_expected(Rule::statement_things));
//     debug_assert!(children.try_consume_any().is_none());
//     statements
// }
//
// fn visit_clause_match(node: Node<'_>) -> MatchClause {
//     debug_assert_eq!(node.as_rule(), Rule::clause_match);
//     let mut children = node.into_children();
//     children.skip_expected(Rule::MATCH);
//     let clause = MatchClause::new(Conjunction::new(visit_patterns(children.consume_expected(Rule::patterns))));
//     debug_assert!(children.try_consume_any().is_none());
//     clause
// }
//
// fn visit_clause_get(node: Node<'_>) -> Vec<Variable> {
//     debug_assert_eq!(node.as_rule(), Rule::clause_get);
//     node.into_children().skip_expected(Rule::GET).map(get_var).collect()
// }
//
// fn visit_modifiers(node: Node<'_>) -> Modifiers {
//     debug_assert_eq!(node.as_rule(), Rule::modifiers);
//     let mut modifiers = Modifiers::default();
//     for modifier in node.into_children() {
//         match modifier.as_rule() {
//             Rule::sort => modifiers.sorting = Some(visit_sort(modifier)),
//             Rule::offset => {
//                 modifiers.offset = Some(Offset {
//                     offset: get_long(modifier.into_children().skip_expected(Rule::OFFSET).consume_expected(Rule::LONG_))
//                         as usize,
//                 })
//             }
//             Rule::limit => {
//                 modifiers.limit = Some(Limit {
//                     limit: get_long(modifier.into_children().skip_expected(Rule::LIMIT).consume_expected(Rule::LONG_))
//                         as usize,
//                 })
//             }
//             _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: modifier.to_string() }),
//         };
//     }
//     modifiers
// }
//
// fn visit_query_get_aggregate(node: Node<'_>) -> TypeQLGetAggregate {
//     debug_assert_eq!(node.as_rule(), Rule::query_get_aggregate);
//     let mut children = node.into_children();
//     let query_get = visit_query_get(children.consume_expected(Rule::query_get));
//     let (method, var) = visit_clause_aggregate(children.consume_expected(Rule::clause_aggregate));
//     debug_assert!(children.try_consume_any().is_none());
//     match method {
//         Aggregate::Count => query_get.count(),
//         method => query_get.aggregate(method, var.unwrap()),
//     }
// }
//
// fn visit_clause_aggregate(node: Node<'_>) -> (Aggregate, Option<Variable>) {
//     debug_assert_eq!(node.as_rule(), Rule::clause_aggregate);
//     let mut children = node.into_children();
//     let method = visit_aggregate_method(children.consume_expected(Rule::aggregate_method));
//     let var = children.try_consume_expected(Rule::VAR_).map(get_var);
//     debug_assert!(children.try_consume_any().is_none());
//     (method, var)
// }
//
// fn visit_query_get_group(node: Node<'_>) -> TypeQLGetGroup {
//     debug_assert_eq!(node.as_rule(), Rule::query_get_group);
//     let mut children = node.into_children();
//     let query = visit_query_get(children.consume_expected(Rule::query_get))
//         .group(visit_clause_group(children.consume_expected(Rule::clause_group)));
//     debug_assert!(children.try_consume_any().is_none());
//     query
// }
//
// fn visit_clause_group(node: Node<'_>) -> Variable {
//     debug_assert_eq!(node.as_rule(), Rule::clause_group);
//     let mut children = node.into_children();
//     children.skip_expected(Rule::GROUP);
//     let var = get_var(children.consume_expected(Rule::VAR_));
//     debug_assert!(children.try_consume_any().is_none());
//     var
// }
//
// fn visit_query_get_group_agg(node: Node<'_>) -> TypeQLGetGroupAggregate {
//     debug_assert_eq!(node.as_rule(), Rule::query_get_group_agg);
//     let mut children = node.into_children();
//     let query = visit_query_get(children.consume_expected(Rule::query_get))
//         .group(visit_clause_group(children.consume_expected(Rule::clause_group)));
//     let (method, var) = visit_clause_aggregate(children.consume_expected(Rule::clause_aggregate));
//     debug_assert!(children.try_consume_any().is_none());
//     match method {
//         Aggregate::Count => query.count(),
//         method => query.aggregate(method, var.unwrap()),
//     }
// }
//
// fn visit_sort(node: Node<'_>) -> Sorting {
//     debug_assert_eq!(node.as_rule(), Rule::sort);
//     let mut children = node.into_children();
//     children.skip_expected(Rule::SORT);
//     Sorting::new(children.map(visit_sort_var).collect())
// }
//
// fn visit_sort_var(node: Node<'_>) -> sorting::SortVariable {
//     debug_assert_eq!(node.as_rule(), Rule::var_order);
//     let mut children = node.into_children();
//     let var = get_var(children.consume_expected(Rule::VAR_));
//     let order = children.try_consume_expected(Rule::ORDER_).map(|child| token::Order::from(child.as_str()));
//     let sorted_variable = (var, order).into();
//     debug_assert!(children.try_consume_any().is_none());
//     sorted_variable
// }
//
// fn visit_aggregate_method(node: Node<'_>) -> token::Aggregate {
//     debug_assert_eq!(node.as_rule(), Rule::aggregate_method);
//     token::Aggregate::from(node.as_str())
// }
//
// fn visit_definables(node: Node<'_>) -> Vec<Definable> {
//     debug_assert_eq!(node.as_rule(), Rule::definables);
//     node.into_children().map(visit_definable).collect()
// }
//
// fn visit_definable(node: Node<'_>) -> Definable {
//     debug_assert_eq!(node.as_rule(), Rule::definable);
//     let mut children = node.into_children();
//     let child = children.consume_any();
//     let definable = match child.as_rule() {
//         Rule::statement_type => visit_statement_type(child).into(),
//         Rule::schema_rule => visit_schema_rule(child).into(),
//         Rule::schema_rule_label => visit_schema_rule_label(child).into(),
//         _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
//     };
//     debug_assert!(children.try_consume_any().is_none());
//     definable
// }
//
// fn visit_patterns(node: Node<'_>) -> Vec<Pattern> {
//     debug_assert_eq!(node.as_rule(), Rule::patterns);
//     node.into_children().map(visit_pattern).collect()
// }
//
// fn visit_pattern(node: Node<'_>) -> Pattern {
//     debug_assert_eq!(node.as_rule(), Rule::pattern);
//     let mut children = node.into_children();
//     let pattern = match children.peek_rule().unwrap() {
//         Rule::statement => visit_statement(children.consume_expected(Rule::statement)).into(),
//         Rule::pattern_disjunction => {
//             visit_pattern_disjunction(children.consume_expected(Rule::pattern_disjunction)).into()
//         }
//         Rule::pattern_conjunction => {
//             visit_pattern_conjunction(children.consume_expected(Rule::pattern_conjunction)).into()
//         }
//         Rule::pattern_negation => visit_pattern_negation(children.consume_expected(Rule::pattern_negation)).into(),
//         _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: children.consume_any().to_string() }),
//     };
//     debug_assert!(children.try_consume_any().is_none());
//     pattern
// }
//
// fn visit_pattern_conjunction(node: Node<'_>) -> Conjunction {
//     debug_assert_eq!(node.as_rule(), Rule::pattern_conjunction);
//     let mut children = node.into_children();
//     let conjunction = Conjunction::new(visit_patterns(children.consume_expected(Rule::patterns)));
//     debug_assert!(children.try_consume_any().is_none());
//     conjunction
// }
//
// fn visit_pattern_disjunction(node: Node<'_>) -> Disjunction {
//     debug_assert_eq!(node.as_rule(), Rule::pattern_disjunction);
//     Disjunction::new(
//         node.into_children()
//             .filter(|child| matches!(child.as_rule(), Rule::patterns))
//             .map(visit_patterns)
//             .map(|mut nested| match nested.len() {
//                 1 => nested.pop().unwrap(),
//                 _ => Conjunction::new(nested).into(),
//             })
//             .collect::<Vec<Pattern>>(),
//     )
// }
//
// fn visit_pattern_negation(node: Node<'_>) -> Negation {
//     debug_assert_eq!(node.as_rule(), Rule::pattern_negation);
//     let mut children = node.into_children();
//     children.skip_expected(Rule::NOT);
//     let mut patterns = visit_patterns(children.consume_expected(Rule::patterns));
//     let negation = match patterns.len() {
//         1 => Negation::new(patterns.pop().unwrap()),
//         _ => Negation::new(Conjunction::new(patterns).into()),
//     };
//     debug_assert!(children.try_consume_any().is_none());
//     negation
// }
//
// fn visit_statement(node: Node<'_>) -> Statement {
//     debug_assert_eq!(node.as_rule(), Rule::statement);
//     let mut children = node.into_children();
//     let child = children.consume_any();
//     let statement = match child.as_rule() {
//         Rule::statement_thing_any => visit_statement_thing_any(child).into(),
//         Rule::statement_type => visit_statement_type(child).into(),
//         Rule::statement_concept => visit_statement_concept(child).into(),
//         Rule::statement_value => visit_statement_value(child).into(),
//         _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
//     };
//     debug_assert!(children.try_consume_any().is_none());
//     statement
// }
//
// fn visit_statement_concept(node: Node<'_>) -> ConceptStatement {
//     debug_assert_eq!(node.as_rule(), Rule::statement_concept);
//     let mut children = node.into_children();
//     let var = get_var_concept(children.consume_expected(Rule::VAR_CONCEPT_))
//         .is(get_var_concept(children.skip_expected(Rule::IS).consume_expected(Rule::VAR_CONCEPT_)));
//     debug_assert!(children.try_consume_any().is_none());
//     var
// }
//
// fn visit_statement_value(node: Node<'_>) -> ValueStatement {
//     debug_assert_eq!(node.as_rule(), Rule::statement_value);
//     let mut children = node.into_children();
//     let var_value = get_var_value(children.consume_expected(Rule::VAR_VALUE_));
//     let var = match children.peek_rule() {
//         Some(Rule::ASSIGN) => var_value.assign(visit_expression(children.skip_expected(Rule::ASSIGN).consume_any())),
//         Some(Rule::predicate) => var_value.predicate(visit_predicate(children.consume_any())),
//         _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: children.to_string() }),
//     };
//     debug_assert!(children.try_consume_any().is_none());
//     var
// }
//
// fn visit_statement_type(node: Node<'_>) -> TypeStatement {
//     debug_assert_eq!(node.as_rule(), Rule::statement_type);
//     let mut children = node.into_children();
//     let mut type_statement = visit_type_ref_any(children.consume_expected(Rule::type_ref_any)).into_type_statement();
//     type_statement = children.map(Node::into_children).fold(type_statement, |var_type, mut constraint_nodes| {
//         let keyword = constraint_nodes.consume_any();
//         let statement = match keyword.as_rule() {
//             Rule::ABSTRACT => var_type.abstract_(),
//             Rule::OWNS => {
//                 let type_ = visit_type_ref(constraint_nodes.consume_expected(Rule::type_ref));
//                 let overridden = constraint_nodes
//                     .try_consume_expected(Rule::AS)
//                     .map(|_| visit_type_ref(constraint_nodes.consume_expected(Rule::type_ref)));
//                 let annotations = visit_annotations_owns(constraint_nodes.consume_expected(Rule::annotations_owns));
//                 var_type.constrain_owns(OwnsConstraint::new(type_, overridden, annotations))
//             }
//             Rule::PLAYS => {
//                 let type_ = visit_type_ref_scoped(constraint_nodes.consume_expected(Rule::type_ref_scoped));
//                 let overridden = constraint_nodes
//                     .try_consume_expected(Rule::AS)
//                     .map(|_| visit_type_ref(constraint_nodes.consume_expected(Rule::type_ref)));
//                 var_type.constrain_plays(PlaysConstraint::new(type_, overridden))
//             }
//             Rule::REGEX => var_type.regex(get_regex(constraint_nodes.consume_expected(Rule::QUOTED_STRING))),
//             Rule::RELATES => {
//                 let type_ = visit_type_ref(constraint_nodes.consume_expected(Rule::type_ref));
//                 let overridden = constraint_nodes
//                     .try_consume_expected(Rule::AS)
//                     .map(|_| visit_type_ref(constraint_nodes.consume_expected(Rule::type_ref)));
//                 var_type.constrain_relates(RelatesConstraint::from((type_, overridden)))
//             }
//             Rule::SUB_ => var_type.constrain_sub(SubConstraint::from((
//                 visit_type_ref_any(constraint_nodes.consume_expected(Rule::type_ref_any)),
//                 matches!(keyword.into_child().unwrap().as_rule(), Rule::SUBX).into(),
//             ))),
//             Rule::TYPE => var_type.type_(visit_label_any(constraint_nodes.consume_expected(Rule::label_any))),
//             Rule::VALUE => {
//                 var_type.value(token::ValueType::from(constraint_nodes.consume_expected(Rule::value_type).as_str()))
//             }
//             _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: constraint_nodes.to_string() }),
//         };
//         debug_assert!(constraint_nodes.try_consume_any().is_none());
//         statement
//     });
//     type_statement
// }
//
// fn visit_annotations_owns(node: Node<'_>) -> Vec<Annotation> {
//     debug_assert_eq!(node.as_rule(), Rule::annotations_owns);
//     node.into_children()
//         .map(|annotation| match annotation.as_rule() {
//             Rule::ANNOTATION_KEY => Annotation::Key,
//             Rule::ANNOTATION_UNIQUE => Annotation::Unique,
//             _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: annotation.to_string() }),
//         })
//         .collect()
// }
//
// fn visit_statement_things(node: Node<'_>) -> Vec<ThingStatement> {
//     debug_assert_eq!(node.as_rule(), Rule::statement_things);
//     node.into_children().map(visit_statement_thing_any).collect()
// }
//
// fn visit_statement_thing_any(node: Node<'_>) -> ThingStatement {
//     debug_assert_eq!(node.as_rule(), Rule::statement_thing_any);
//     let mut children = node.into_children();
//     let child = children.consume_any();
//     let statement = match child.as_rule() {
//         Rule::statement_thing => visit_statement_thing(child),
//         Rule::statement_relation => visit_statement_relation(child),
//         Rule::statement_attribute => visit_statement_attribute(child),
//         _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
//     };
//     debug_assert!(children.try_consume_any().is_none());
//     statement
// }
//
// fn visit_statement_thing(node: Node<'_>) -> ThingStatement {
//     debug_assert_eq!(node.as_rule(), Rule::statement_thing);
//     let mut children = node.into_children();
//     let self1 = get_var_concept(children.consume_expected(Rule::VAR_CONCEPT_));
//     let mut stmt_thing: ThingStatement = self1.into();
//     if children.peek_rule() != Some(Rule::attributes) {
//         let keyword = children.consume_any();
//         stmt_thing = match keyword.as_rule() {
//             Rule::IID => stmt_thing.iid(children.consume_expected(Rule::IID_).as_str()),
//             Rule::ISA_ => {
//                 stmt_thing.constrain_isa(get_isa_constraint(keyword, children.consume_expected(Rule::type_ref)))
//             }
//             _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: children.to_string() }),
//         }
//     }
//     if let Some(attributes) = children.try_consume_expected(Rule::attributes) {
//         stmt_thing =
//             visit_attributes(attributes).into_iter().fold(stmt_thing, |var_thing, has| var_thing.constrain_has(has));
//     }
//     debug_assert!(children.try_consume_any().is_none());
//     stmt_thing
// }
//
// fn visit_statement_relation(node: Node<'_>) -> ThingStatement {
//     debug_assert_eq!(node.as_rule(), Rule::statement_relation);
//     let mut children = node.into_children();
//     let mut relation = children
//         .try_consume_expected(Rule::VAR_CONCEPT_)
//         .map(get_var_concept)
//         .unwrap_or(Variable::Hidden)
//         .relation(visit_relation(children.consume_expected(Rule::relation)));
//
//     if let Some(isa) = children.try_consume_expected(Rule::ISA_) {
//         let type_ = children.consume_expected(Rule::type_ref);
//         relation = relation.constrain_isa(get_isa_constraint(isa, type_));
//     }
//     if let Some(attributes) = children.try_consume_expected(Rule::attributes) {
//         relation = visit_attributes(attributes).into_iter().fold(relation, |relation, has| relation.constrain_has(has));
//     }
//     debug_assert!(children.try_consume_any().is_none());
//     relation
// }
//
// fn visit_statement_attribute(node: Node<'_>) -> ThingStatement {
//     debug_assert_eq!(node.as_rule(), Rule::statement_attribute);
//     let mut children = node.into_children();
//     let mut attribute = children
//         .try_consume_expected(Rule::VAR_CONCEPT_)
//         .map(get_var_concept)
//         .unwrap_or(Variable::Hidden)
//         .compare(visit_predicate(children.consume_expected(Rule::predicate)));
//
//     if let Some(isa) = children.try_consume_expected(Rule::ISA_) {
//         let type_ = children.consume_expected(Rule::type_ref);
//         attribute = attribute.constrain_isa(get_isa_constraint(isa, type_));
//     }
//     if let Some(attributes) = children.try_consume_expected(Rule::attributes) {
//         attribute =
//             visit_attributes(attributes).into_iter().fold(attribute, |attribute, has| attribute.constrain_has(has));
//     }
//     debug_assert!(children.try_consume_any().is_none());
//     attribute
// }
//
// fn visit_relation(node: Node<'_>) -> RelationConstraint {
//     debug_assert_eq!(node.as_rule(), Rule::relation);
//     RelationConstraint::new(get_role_players(node))
// }
//
// fn visit_attributes(node: Node<'_>) -> Vec<HasConstraint> {
//     debug_assert_eq!(node.as_rule(), Rule::attributes);
//     node.into_children().map(visit_attribute).collect()
// }
//
// fn visit_attribute(node: Node<'_>) -> HasConstraint {
//     debug_assert_eq!(node.as_rule(), Rule::attribute);
//     let mut children = node.into_children();
//     let constraint = match children.skip_expected(Rule::HAS).peek_rule() {
//         Some(Rule::label) => {
//             let label: Label = children.consume_expected(Rule::label).as_str().to_owned().into();
//             match children.peek_rule() {
//                 Some(Rule::VAR_) => {
//                     let var = get_var(children.consume_expected(Rule::VAR_));
//                     match var {
//                         Variable::Concept(cvar) => HasConstraint::HasConcept(Some(label), cvar),
//                         Variable::Value(vvar) => HasConstraint::HasValue(label, vvar),
//                     }
//                 }
//                 Some(Rule::predicate) => {
//                     HasConstraint::from((label, visit_predicate(children.consume_expected(Rule::predicate))))
//                 }
//                 _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: children.to_string() }),
//             }
//         }
//         Some(Rule::VAR_CONCEPT_) => HasConstraint::from(get_var_concept(children.consume_expected(Rule::VAR_CONCEPT_))),
//         _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: children.to_string() }),
//     };
//     debug_assert!(children.try_consume_any().is_none());
//     constraint
// }
//
// fn visit_predicate(node: Node<'_>) -> Comparison {
//     debug_assert_eq!(node.as_rule(), Rule::predicate);
//     let mut children = node.into_children();
//     let constraint = match children.peek_rule() {
//         Some(Rule::constant) => {
//             Comparison::new(token::Comparator::Eq, visit_constant(children.consume_expected(Rule::constant)).into())
//         }
//         Some(Rule::predicate_equality) => {
//             Comparison::new(token::Comparator::from(children.consume_expected(Rule::predicate_equality).as_str()), {
//                 let res_predicate_value = children.consume_expected(Rule::value).into_child();
//                 debug_assert!(res_predicate_value.is_ok());
//                 let predicate_value = res_predicate_value.unwrap();
//                 match predicate_value.as_rule() {
//                     Rule::constant => visit_constant(predicate_value).into(),
//                     Rule::VAR_ => Value::from(get_var(predicate_value)),
//                     _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: predicate_value.to_string() }),
//                 }
//             })
//         }
//         Some(Rule::predicate_substring) => {
//             let predicate = token::Comparator::from(children.consume_expected(Rule::predicate_substring).as_str());
//             Comparison::new(
//                 predicate,
//                 {
//                     match predicate {
//                         token::Comparator::Like => get_regex(children.consume_expected(Rule::QUOTED_STRING)),
//                         token::Comparator::Contains => {
//                             get_string_from_quoted(children.consume_expected(Rule::QUOTED_STRING))
//                         }
//                         _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: children.to_string() }),
//                     }
//                 }
//                 .into(),
//             )
//         }
//         _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: children.to_string() }),
//     };
//     debug_assert!(children.try_consume_any().is_none());
//     constraint
// }
//
// fn visit_expression(node: Node<'_>) -> Expression {
//     debug_assert_eq!(node.as_rule(), Rule::expression);
//     let pratt_parser: PrattParser<Rule> = PrattParser::new()
//         .op(Op::infix(Rule::ADD, Assoc::Left) | Op::infix(Rule::SUBTRACT, Assoc::Left))
//         .op(Op::infix(Rule::MULTIPLY, Assoc::Left)
//             | Op::infix(Rule::DIVIDE, Assoc::Left)
//             | Op::infix(Rule::MODULO, Assoc::Left))
//         .op(Op::infix(Rule::POWER, Assoc::Right));
//
//     pratt_parser
//         .map_primary(|primary| match primary.as_rule() {
//             Rule::VAR_ => get_var(primary).into(),
//             Rule::constant => Expression::Constant(visit_constant(primary)),
//             Rule::expression_function => Expression::Function(visit_function(primary)),
//             Rule::expression_parenthesis => visit_expression(primary.into_children().consume_any()),
//             _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: primary.to_string() }),
//         })
//         .map_infix(|left, op, right| {
//             let op = match op.as_rule() {
//                 Rule::ADD => token::ArithmeticOperator::Add,
//                 Rule::SUBTRACT => token::ArithmeticOperator::Subtract,
//                 Rule::MULTIPLY => token::ArithmeticOperator::Multiply,
//                 Rule::DIVIDE => token::ArithmeticOperator::Divide,
//                 Rule::MODULO => token::ArithmeticOperator::Modulo,
//                 Rule::POWER => token::ArithmeticOperator::Power,
//                 _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: op.to_string() }),
//             };
//             Expression::Operation(Operation::new(op, left, right))
//         })
//         .parse(node.into_children())
// }
//
// fn visit_function(node: Node<'_>) -> Function {
//     debug_assert_eq!(node.as_rule(), Rule::expression_function);
//     let mut children = node.into_children();
//     Function {
//         function_name: visit_function_name(children.consume_expected(Rule::expression_function_name)),
//         args: children.map(visit_expression).collect(),
//     }
// }
//
// fn visit_function_name(node: Node<'_>) -> token::Function {
//     debug_assert_eq!(node.as_rule(), Rule::expression_function_name);
//     let mut children = node.into_children();
//     let child = children.consume_any();
//     let function_token = match child.as_rule() {
//         Rule::ABS => token::Function::Abs,
//         Rule::CEIL => token::Function::Ceil,
//         Rule::FLOOR => token::Function::Floor,
//         Rule::MAX => token::Function::Max,
//         Rule::MIN => token::Function::Min,
//         Rule::ROUND => token::Function::Round,
//         _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
//     };
//     debug_assert!(children.try_consume_any().is_none());
//     function_token
// }
//
// fn visit_schema_rule_label(node: Node<'_>) -> RuleLabel {
//     debug_assert_eq!(node.as_rule(), Rule::schema_rule_label);
//     let mut children = node.into_children();
//     children.skip_expected(Rule::RULE);
//     let rule = RuleLabel::new(Label::from(children.consume_expected(Rule::label).as_str()));
//     debug_assert!(children.try_consume_any().is_none());
//     rule
// }
//
// fn visit_schema_rule(node: Node<'_>) -> crate::pattern::Rule {
//     debug_assert_eq!(node.as_rule(), Rule::schema_rule);
//     let mut children = node.into_children();
//     let rule = RuleLabel::new(Label::from(children.skip_expected(Rule::RULE).consume_expected(Rule::label).as_str()))
//         .when(Conjunction::new(visit_patterns(children.skip_expected(Rule::WHEN).consume_expected(Rule::patterns))))
//         .then(visit_statement_thing_any(
//             children.skip_expected(Rule::THEN).consume_expected(Rule::statement_thing_any),
//         ));
//     debug_assert!(children.try_consume_any().is_none());
//     rule
// }
//
// fn visit_type_ref_any(node: Node<'_>) -> TypeReference {
//     debug_assert_eq!(node.as_rule(), Rule::type_ref_any);
//     let mut children = node.into_children();
//     let child = children.consume_any();
//     let type_ = match child.as_rule() {
//         Rule::VAR_CONCEPT_ => TypeReference::Variable(get_var_concept(child)),
//         Rule::type_ref => visit_type_ref(child),
//         Rule::type_ref_scoped => visit_type_ref_scoped(child),
//         _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
//     };
//     debug_assert!(children.try_consume_any().is_none());
//     type_
// }
//
// fn visit_type_ref_scoped(node: Node<'_>) -> TypeReference {
//     debug_assert_eq!(node.as_rule(), Rule::type_ref_scoped);
//     let mut children = node.into_children();
//     let child = children.consume_any();
//     let type_ = match child.as_rule() {
//         Rule::label_scoped => TypeReference::Label(visit_label_scoped(child)),
//         Rule::VAR_CONCEPT_ => TypeReference::Variable(get_var_concept(child)),
//         _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
//     };
//     debug_assert!(children.try_consume_any().is_none());
//     type_
// }
//
// fn visit_type_ref(node: Node<'_>) -> TypeReference {
//     debug_assert_eq!(node.as_rule(), Rule::type_ref);
//     let mut children = node.into_children();
//     let child = children.consume_any();
//     let type_ = match child.as_rule() {
//         Rule::label => TypeReference::Label(child.as_str().into()),
//         Rule::VAR_CONCEPT_ => TypeReference::Variable(get_var_concept(child)),
//         _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
//     };
//     debug_assert!(children.try_consume_any().is_none());
//     type_
// }
//
// fn visit_label_any(node: Node<'_>) -> Label {
//     debug_assert_eq!(node.as_rule(), Rule::label_any);
//     let mut children = node.into_children();
//     let child = children.consume_any();
//     let label = match child.as_rule() {
//         Rule::label => Label::from(child.as_str()),
//         Rule::label_scoped => visit_label_scoped(child),
//         _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
//     };
//     debug_assert!(children.try_consume_any().is_none());
//     label
// }
//
// fn visit_label_scoped(node: Node<'_>) -> Label {
//     debug_assert_eq!(node.as_rule(), Rule::label_scoped);
//     let parts: Vec<String> = node.as_str().split(':').map(String::from).collect();
//     assert_eq!(parts.len(), 2);
//     Label::from((parts[0].clone(), parts[1].clone()))
// }
//
// fn visit_constant(node: Node<'_>) -> Constant {
//     debug_assert_eq!(node.as_rule(), Rule::constant);
//     let mut children = node.into_children();
//     let child = children.consume_any();
//     let constant = match child.as_rule() {
//         Rule::QUOTED_STRING => Constant::from(get_string_from_quoted(child)),
//         Rule::signed_long => Constant::from(long_from_string(child.as_str())),
//         Rule::signed_double => Constant::from(double_from_string(child.as_str())),
//         Rule::BOOLEAN_ => Constant::from(get_boolean(child)),
//         Rule::DATE_ => Constant::from(get_date(child).and_hms_opt(0, 0, 0).unwrap()),
//         Rule::DATETIME_ => Constant::from(get_date_time(child)),
//         _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
//     };
//     debug_assert!(children.try_consume_any().is_none());
//     constant
// }
