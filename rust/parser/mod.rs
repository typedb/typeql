/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use pest::Parser;
use pest_derive::Parser;

use crate::{
    common::{
        error::{syntax_error, TypeQLError},
        LineColumn, Span, Spanned,
    },
    pattern::{statement::Variable, Label},
    query::Query,
    Result,
};

mod data;
mod expression;
mod schema;
mod statement;

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
            begin: LineColumn { line: begin_line, column: begin_col },
            end: LineColumn { line: end_line, column: end_col },
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

// pub(crate) fn visit_eof_pattern(pattern: &str) -> Result<Pattern> {
//     visit_pattern(parse_single(Rule::eof_pattern, pattern)?.into_children().consume_expected(Rule::pattern)).validated()
// }

// pub(crate) fn visit_eof_patterns(patterns: &str) -> Result<Vec<Pattern>> {
//     visit_patterns(parse_single(Rule::eof_patterns, patterns)?.into_children().consume_expected(Rule::patterns))
//         .into_iter()
//         .map(Validatable::validated)
//         .collect()
// }

// pub(crate) fn visit_eof_definables(definables: &str) -> Result<Vec<Definable>> {
//     visit_definables(parse_single(Rule::eof_definables, definables)?.into_children().consume_expected(Rule::definables))
//         .into_iter()
//         .map(Validatable::validated)
//         .collect()
// }

// pub(crate) fn visit_eof_statement(statement: &str) -> Result<Statement> {
//     visit_statement(parse_single(Rule::eof_statement, statement)?.into_children().consume_expected(Rule::statement))
//         .validated()
// }

// pub(crate) fn visit_eof_label(label: &str) -> Result<Label> {
//     let parsed = parse_single(Rule::eof_label, label)?.into_children().consume_expected(Rule::label);
//     let string = parsed.as_str();
//     if string != label {
//         Err(TypeQLError::InvalidTypeLabel { label: label.to_string() })?;
//     }
//     Ok(string.into())
// }

// pub(crate) fn visit_eof_schema_rule(rule: &str) -> Result<crate::pattern::Rule> {
//     visit_schema_rule(parse_single(Rule::eof_schema_rule, rule)?).validated()
// }

fn visit_query(node: Node<'_>) -> Query {
    debug_assert_eq!(node.as_rule(), Rule::query);
    let mut children = node.into_children();
    let child = children.consume_any();
    let query = match child.as_rule() {
        Rule::query_schema => Query::Schema(schema::visit_query_schema(child)),
        Rule::query_data => Query::Data(data::visit_query_data(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };
    debug_assert!(children.try_consume_any().is_none());
    query
}

fn visit_label_any(node: Node<'_>) -> Label {
    debug_assert_eq!(node.as_rule(), Rule::label_any);
    let child = node.into_child();
    match child.as_rule() {
        Rule::label => visit_label(child),
        Rule::label_scoped => visit_label_scoped(child),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_label(label: Node<'_>) -> Label {
    debug_assert_eq!(label.as_rule(), Rule::label);
    Label::new_unscoped(label.as_str(), label.span())
}

fn visit_label_scoped(label: Node<'_>) -> Label {
    debug_assert_eq!(label.as_rule(), Rule::label_scoped);
    let span = label.span();
    let mut children = label.into_children();
    let scope = children.consume_expected(Rule::label);
    let name = children.consume_expected(Rule::label);
    Label::new_scoped(scope.as_str(), name.as_str(), span)
}

fn visit_list_label(node: Node<'_>) -> Label {
    debug_assert_eq!(node.as_rule(), Rule::list_label);
    visit_label(node.into_child())
}

fn visit_var(node: Node<'_>) -> Variable {
    debug_assert_eq!(node.as_rule(), Rule::var);
    let span = node.span();

    let name = node.as_str();
    assert!(name.len() > 1);
    assert!(name.starts_with('$'));
    let name = &name[1..];
    match name {
        "_" => Variable::Anonymous(span),
        name => Variable::Named(span, String::from(name)),
    }
}

fn visit_list_var(node: Node<'_>) -> Variable {
    debug_assert_eq!(node.as_rule(), Rule::list_var);
    visit_var(node.into_child())
}

fn visit_value_type_primitive(node: Node<'_>) -> String {
    node.as_str().to_owned() // FIXME
}
