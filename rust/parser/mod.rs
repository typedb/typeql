/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use pest::Parser;
use pest_derive::Parser;

use self::{
    data::{visit_pattern, visit_patterns},
    define::{visit_definables, visit_query_define},
    statement::visit_statement,
    undefine::visit_query_undefine,
};
use crate::{
    common::{
        error::{syntax_error, TypeQLError},
        token, LineColumn, Span, Spanned,
    },
    identifier::{Identifier, Label, ReservedLabel, ScopedLabel, Variable},
    parser::redefine::visit_query_redefine,
    pattern::{Pattern, Statement},
    query::{Query, SchemaQuery},
    schema::definable::Definable,
    type_::{BuiltinValueType, List, Optional, Type},
    Result,
};

mod annotation;
mod data;
mod define;
mod expression;
mod redefine;
mod statement;
mod undefine;

#[cfg(test)]
mod test;

#[derive(Parser)]
#[grammar = "parser/typeql.pest"]
pub(crate) struct TypeQLParser;

type Node<'a> = pest::iterators::Pair<'a, Rule>;
type ChildNodes<'a> = pest::iterators::Pairs<'a, Rule>;

impl Spanned for Node<'_> {
    fn span(&self) -> Option<Span> {
        let (begin_line, begin_col) = self.as_span().start_pos().line_col();
        let (end_line, end_col) = self.as_span().end_pos().line_col();
        Some(Span {
            begin: LineColumn { line: begin_line as u32, column: begin_col as u32 },
            end: LineColumn { line: end_line as u32, column: end_col as u32 },
        })
    }
}

trait IntoChildNodes<'a> {
    fn into_child(self) -> Node<'a>;
    fn into_children(self) -> ChildNodes<'a>;
}

impl<'a> IntoChildNodes<'a> for Node<'a> {
    fn into_child(self) -> Node<'a> {
        let mut children = self.into_children();
        let child = children.consume_any();
        match children.try_consume_any() {
            None => child,
            Some(next) => {
                unreachable!("{child} is followed by more tokens: {next}")
            }
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

pub(crate) fn visit_eof_query(query: &str) -> Result<Query> {
    Ok(visit_query(parse_single(Rule::eof_query, query)?.into_children().consume_expected(Rule::query)))
}

pub(crate) fn visit_eof_queries(queries: &str) -> Result<impl Iterator<Item = Query> + '_> {
    Ok(parse(Rule::eof_queries, queries)?
        .consume_expected(Rule::eof_queries)
        .into_children()
        .filter(|child| matches!(child.as_rule(), Rule::query))
        .map(visit_query))
}

pub(crate) fn visit_eof_pattern(pattern: &str) -> Result<Pattern> {
    Ok(visit_pattern(parse_single(Rule::eof_pattern, pattern)?.into_children().consume_expected(Rule::pattern)))
}

pub(crate) fn visit_eof_patterns(patterns: &str) -> Result<Vec<Pattern>> {
    Ok(visit_patterns(parse_single(Rule::eof_patterns, patterns)?.into_children().consume_expected(Rule::patterns)))
}

pub(crate) fn visit_eof_definables(definables: &str) -> Result<Vec<Definable>> {
    Ok(visit_definables(
        parse_single(Rule::eof_definables, definables)?.into_children().consume_expected(Rule::definables),
    ))
}

pub(crate) fn visit_eof_statement(statement: &str) -> Result<Statement> {
    Ok(visit_statement(parse_single(Rule::eof_statement, statement)?.into_children().consume_expected(Rule::statement)))
}

pub(crate) fn visit_eof_label(label: &str) -> Result<Label> {
    let parsed = parse_single(Rule::eof_label, label)?.into_children().consume_expected(Rule::label);
    let string = parsed.as_str();
    if string != label {
        Err(TypeQLError::InvalidTypeLabel { label: label.to_string() })?;
    }
    Ok(visit_label(parsed))
}

fn visit_query(node: Node<'_>) -> Query {
    debug_assert_eq!(node.as_rule(), Rule::query);
    let mut children = node.into_children();
    let child = children.consume_any();
    let query = match child.as_rule() {
        Rule::query_schema => Query::Schema(visit_query_schema(child)),
        Rule::query_data => Query::Data(data::visit_query_data(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };
    debug_assert_eq!(children.try_consume_any(), None);
    query
}

fn visit_query_schema(node: Node<'_>) -> SchemaQuery {
    debug_assert_eq!(node.as_rule(), Rule::query_schema);
    let mut children = node.into_children();
    let child = children.consume_any();
    let query = match child.as_rule() {
        Rule::query_define => SchemaQuery::Define(visit_query_define(child)),
        Rule::query_redefine => SchemaQuery::Redefine(visit_query_redefine(child)),
        Rule::query_undefine => SchemaQuery::Undefine(visit_query_undefine(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };
    debug_assert_eq!(children.try_consume_any(), None);
    query
}

fn visit_label(node: Node<'_>) -> Label {
    debug_assert_eq!(node.as_rule(), Rule::label);
    let child = node.into_child();
    match child.as_rule() {
        Rule::identifier => Label::Identifier(visit_identifier(child)),
        Rule::kind => Label::Reserved(visit_kind(child)),
        Rule::ROLE => Label::Reserved(ReservedLabel::new(child.span(), token::Type::Role)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_kind(node: Node<'_>) -> ReservedLabel {
    debug_assert_eq!(node.as_rule(), Rule::kind);
    let span = node.span();
    let child = node.into_child();
    let token = match child.as_rule() {
        Rule::ENTITY => token::Type::Entity,
        Rule::RELATION => token::Type::Relation,
        Rule::ATTRIBUTE => token::Type::Attribute,
        Rule::ROLE => token::Type::Role,
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };
    ReservedLabel::new(span, token)
}

fn visit_label_scoped(node: Node<'_>) -> ScopedLabel {
    debug_assert_eq!(node.as_rule(), Rule::label_scoped);
    let span = node.span();
    let mut children = node.into_children();
    let scope = visit_label(children.consume_expected(Rule::label));
    let name = visit_label(children.consume_expected(Rule::label));
    debug_assert_eq!(children.try_consume_any(), None);
    ScopedLabel::new(span, scope, name)
}

fn visit_identifier(node: Node<'_>) -> Identifier {
    debug_assert_eq!(node.as_rule(), Rule::identifier);
    Identifier::new(node.span(), node.as_str().to_owned())
}

fn visit_label_list(node: Node<'_>) -> List {
    debug_assert_eq!(node.as_rule(), Rule::label_list);
    let span = node.span();
    let inner = Type::Label(visit_label(node.into_child()));
    List::new(span, inner)
}

fn visit_var(node: Node<'_>) -> Variable {
    debug_assert_eq!(node.as_rule(), Rule::var);
    let span = node.span();
    let child = node.into_child();
    match child.as_rule() {
        Rule::VAR_ANONYMOUS => Variable::Anonymous(span),
        Rule::var_named => visit_var_named(child),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_var_named(node: Node<'_>) -> Variable {
    debug_assert_eq!(node.as_rule(), Rule::var_named);
    let span = node.span();
    Variable::Named(span, visit_identifier_var(node.into_child()))
}

fn visit_identifier_var(node: Node<'_>) -> Identifier {
    debug_assert_eq!(node.as_rule(), Rule::identifier_var);
    Identifier::new(node.span(), node.as_str().to_owned())
}

fn visit_vars(node: Node<'_>) -> Vec<Variable> {
    debug_assert_eq!(node.as_rule(), Rule::vars);
    node.into_children().map(visit_var).collect()
}

fn visit_var_list(node: Node<'_>) -> List {
    debug_assert_eq!(node.as_rule(), Rule::var_list);
    let span = node.span();
    let inner = visit_var(node.into_child());
    List::new(span, Type::Variable(inner))
}

fn visit_value_type(node: Node<'_>) -> Type {
    debug_assert_eq!(node.as_rule(), Rule::value_type);
    let child = node.into_child();
    match child.as_rule() {
        Rule::value_type_primitive => Type::BuiltinValue(visit_value_type_primitive(child)),
        Rule::identifier => Type::Label(Label::Identifier(visit_identifier(child))),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_value_type_optional(node: Node<'_>) -> Optional {
    debug_assert_eq!(node.as_rule(), Rule::value_type_optional);
    let span = node.span();
    let inner = visit_value_type(node.into_child());
    Optional::new(span, inner)
}

fn visit_value_type_list(node: Node<'_>) -> List {
    debug_assert_eq!(node.as_rule(), Rule::value_type_list);
    let span = node.span();
    let inner = visit_value_type(node.into_child());
    List::new(span, inner)
}

fn visit_value_type_primitive(node: Node<'_>) -> BuiltinValueType {
    debug_assert_eq!(node.as_rule(), Rule::value_type_primitive);
    let span = node.span();
    let child = node.into_child();
    let token = match child.as_rule() {
        Rule::BOOLEAN => token::ValueType::Boolean,
        Rule::DATE => token::ValueType::Date,
        Rule::DATETIME => token::ValueType::DateTime,
        Rule::DATETIME_TZ => token::ValueType::DateTimeTZ,
        Rule::DECIMAL => token::ValueType::Decimal,
        Rule::DOUBLE => token::ValueType::Double,
        Rule::DURATION => token::ValueType::Duration,
        Rule::LONG => token::ValueType::Long,
        Rule::STRING => token::ValueType::String,
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };
    BuiltinValueType::new(span, token)
}

fn visit_integer_literal(node: Node<'_>) -> u64 {
    debug_assert_eq!(node.as_rule(), Rule::integer_literal);
    node.as_str().parse().unwrap() // TODO what should happen if the number is too large?
}
