/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use itertools::Itertools;
use pest::{iterators::Pair, Parser};
use pest_derive::Parser;

use self::{
    define::{function::visit_definition_function, struct_::visit_definition_struct, visit_query_define},
    undefine::visit_query_undefine,
};
use crate::{
    common::{
        error::{syntax_error, TypeQLError},
        identifier::Identifier,
        token, LineColumn, Span, Spanned,
    },
    parser::{pipeline::visit_query_pipeline, redefine::visit_query_redefine},
    query::{Query, SchemaQuery},
    schema::definable,
    type_::{BuiltinValueType, Label, List, Optional, ReservedLabel, ScopedLabel, Type},
    value::{
        BooleanLiteral, DateFragment, DateLiteral, DateTimeLiteral, DateTimeTZLiteral, IntegerLiteral,
        Literal, Sign, SignedDecimalLiteral, SignedIntegerLiteral, StringLiteral, TimeFragment, TimeZone, ValueLiteral,
    },
    variable::Variable,
    Result,
};

mod annotation;
mod define;
mod expression;
mod pipeline;
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

pub(crate) fn visit_eof_definition_function(query: &str) -> Result<definable::Function> {
    Ok(visit_definition_function(
        parse_single(Rule::eof_definition_function, query)?.into_children().consume_expected(Rule::definition_function),
    ))
}

pub(crate) fn visit_eof_definition_struct(query: &str) -> Result<definable::Struct> {
    Ok(visit_definition_struct(
        parse_single(Rule::eof_definition_struct, query)?.into_children().consume_expected(Rule::definition_struct),
    ))
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
        Rule::query_pipeline => Query::Pipeline(visit_query_pipeline(child)),
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

fn visit_value_literal(node: Node<'_>) -> Literal {
    debug_assert_eq!(node.as_rule(), Rule::value_literal);
    let span = node.span();
    let child = node.into_child();
    let value_literal = match child.as_rule() {
        Rule::quoted_string_literal => ValueLiteral::String(visit_quoted_string_literal(child)),
        Rule::boolean_literal => ValueLiteral::Boolean(BooleanLiteral { value: child.as_str().to_owned() }),
        Rule::signed_integer => ValueLiteral::Integer(visit_signed_integer(child)),
        Rule::signed_decimal => ValueLiteral::Decimal(visit_signed_decimal(child)),

        Rule::datetime_tz_literal => ValueLiteral::DateTimeTz(visit_datetime_tz_literal(child)),
        Rule::datetime_literal => ValueLiteral::DateTime(visit_datetime_literal(child)),
        Rule::date_literal => ValueLiteral::Date(visit_date_literal(child)),

        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };
    Literal::new(span, value_literal)
}

fn visit_sign(node: Node<'_>) -> Sign {
    debug_assert_eq!(node.as_rule(), Rule::sign);
    match node.as_str() {
        "+" => Sign::Plus,
        "-" => Sign::Minus,
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: node.to_string() }),
    }
}

fn visit_integer_literal(node: Node<'_>) -> IntegerLiteral {
    debug_assert_eq!(node.as_rule(), Rule::integer_literal);
    IntegerLiteral { value: node.as_str().to_owned() }
}

fn visit_quoted_string_literal(node: Node<'_>) -> StringLiteral {
    debug_assert_eq!(node.as_rule(), Rule::quoted_string_literal);
    StringLiteral { value: node.as_str().to_owned() }
}

fn visit_signed_integer(node: Node<'_>) -> SignedIntegerLiteral {
    debug_assert_eq!(node.as_rule(), Rule::signed_integer);
    let mut children = node.into_children().collect::<Vec<_>>();
    let integral = children.pop().unwrap();
    let sign = children.pop().map(|node| visit_sign(node)).unwrap_or(Sign::Plus);
    debug_assert_eq!(integral.as_rule(), Rule::integer_literal);
    SignedIntegerLiteral { sign, integral: integral.as_str().to_owned() }
}

fn visit_signed_decimal(node: Node<'_>) -> SignedDecimalLiteral {
    debug_assert_eq!(node.as_rule(), Rule::signed_decimal);
    let mut children = node.into_children().collect::<Vec<_>>();
    let decimal = children.pop().unwrap().as_str().to_owned();
    let sign = children.pop().map(|node| visit_sign(node)).unwrap_or(Sign::Plus);
    SignedDecimalLiteral { sign, decimal }
}

fn visit_datetime_tz_literal(node: Node<'_>) -> DateTimeTZLiteral {
    debug_assert_eq!(node.as_rule(), Rule::datetime_tz_literal);
    let (date_node, time_node, tz_node) = node.into_children().collect_tuple().unwrap();
    let date = visit_date_fragment(date_node);
    let time = visit_time(time_node);
    let timezone = match tz_node.as_rule() {
        Rule::iana_timezone => TimeZone::IANA(tz_node.as_str().to_owned()),
        Rule::iso8601_timezone_offset => TimeZone::ISO(tz_node.as_str().to_owned()),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: tz_node.to_string() }),
    };
    DateTimeTZLiteral { date, time, timezone }
}

fn visit_datetime_literal(node: Node<'_>) -> DateTimeLiteral {
    debug_assert_eq!(node.as_rule(), Rule::datetime_literal);
    let (date_node, time_node) = node.into_children().collect_tuple().unwrap();
    let date = visit_date_fragment(date_node);
    let time = visit_time(time_node);
    DateTimeLiteral { date, time }
}

fn visit_date_literal(node: Node<'_>) -> DateLiteral {
    debug_assert_eq!(node.as_rule(), Rule::date_literal);
    let date = visit_date_fragment(node.into_child());
    DateLiteral { date }
}

fn visit_date_fragment(node: Node<'_>) -> DateFragment {
    debug_assert_eq!(node.as_rule(), Rule::date_fragment);
    let (year, month, day) = node.into_children().map(|child| child.as_str().to_owned()).collect_tuple().unwrap();
    DateFragment { year, month, day }
}

fn visit_time(node: Node<'_>) -> TimeFragment {
    debug_assert_eq!(node.as_rule(), Rule::time);
    let children = node.into_children().collect::<Vec<_>>();
    let (hour, minute, second) = (
        children[0].as_str().to_owned(),
        children[1].as_str().to_owned(),
        children.get(2).map(|node| node.as_str().to_owned()),
    );
    let second_fraction = children.get(3).map(|node| node.as_str().to_owned());
    TimeFragment { hour, minute, second, second_fraction }
}
