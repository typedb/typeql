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
    pattern::{
        definition::type_::declaration::{
            AnnotationOwns, AnnotationRelates, AnnotationSub, AnnotationValueType, Owned, Owns, Played, Plays, Related,
            Relates, Sub, ValueType,
        },
        Definable, Label, Type,
    },
    query::{Query, SchemaQuery, TypeQLDefine, TypeQLUndefine},
    Result,
};

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
    fn into_child(self) -> Result<Node<'a>>;
    fn into_children(self) -> ChildNodes<'a>;
}

impl<'a> IntoChildNodes<'a> for Node<'a> {
    fn into_child(self) -> Result<Node<'a>> {
        let mut children = self.into_children();
        let child = children.consume_any();
        match children.try_consume_any() {
            None => Ok(child),
            Some(next) => {
                Err(TypeQLError::IllegalGrammar { input: format!("{child} is followed by more tokens: {next}") }.into())
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
        Rule::query_schema => Query::Schema(visit_query_schema(child)),
        Rule::query_data => todo!(),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };
    debug_assert!(children.try_consume_any().is_none());
    query
}

fn visit_query_schema(node: Node<'_>) -> SchemaQuery {
    debug_assert_eq!(node.as_rule(), Rule::query_schema);
    let mut children = node.into_children();
    let child = children.consume_any();
    let query = match child.as_rule() {
        Rule::query_define => SchemaQuery::Define(visit_query_define(child)),
        Rule::query_undefine => SchemaQuery::Undefine(visit_query_undefine(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };
    debug_assert!(children.try_consume_any().is_none());
    query
}

fn visit_query_define(node: Node<'_>) -> TypeQLDefine {
    debug_assert_eq!(node.as_rule(), Rule::query_define);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::DEFINE);
    let query = TypeQLDefine::new(visit_definables(children.consume_expected(Rule::definables)), span);
    debug_assert!(children.try_consume_any().is_none());
    query
}

fn visit_query_undefine(node: Node<'_>) -> TypeQLUndefine {
    debug_assert_eq!(node.as_rule(), Rule::query_undefine);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::UNDEFINE);
    let query = TypeQLUndefine::new(visit_definables(children.consume_expected(Rule::definables)), span);
    debug_assert!(children.try_consume_any().is_none());
    query
}

fn visit_definables(node: Node<'_>) -> Vec<Definable> {
    debug_assert_eq!(node.as_rule(), Rule::definables);
    node.into_children().map(visit_definable).collect()
}

fn visit_definable(node: Node<'_>) -> Definable {
    debug_assert_eq!(node.as_rule(), Rule::definable);
    let mut children = node.into_children();
    let child = children.consume_any();
    let definable = match child.as_rule() {
        Rule::definition_type => Definable::TypeDeclaration(visit_definition_type(child)),
        Rule::definition_function => todo!(),
        Rule::definition_struct => todo!(),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };
    debug_assert!(children.try_consume_any().is_none());
    definable
}

fn visit_definition_type(node: Node<'_>) -> Type {
    debug_assert_eq!(node.as_rule(), Rule::definition_type);
    let span = node.span();
    let mut children = node.into_children();
    let label = visit_label(children.consume_expected(Rule::label));
    children.fold(Type::new(label, span), |type_declaration, constraint| {
        let constraint = constraint.into_child().unwrap();
        match constraint.as_rule() {
            Rule::sub_declaration => type_declaration.set_sub(visit_sub_declaration(constraint)),
            Rule::value_type_declaration => type_declaration.set_value_type(visit_value_type_declaration(constraint)),
            Rule::owns_declaration => type_declaration.add_owns(visit_owns_declaration(constraint)),
            Rule::relates_declaration => type_declaration.add_relates(visit_relates_declaration(constraint)),
            Rule::plays_declaration => type_declaration.add_plays(visit_plays_declaration(constraint)),
            _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: constraint.to_string() }),
        }
    })
}

fn visit_plays_declaration(node: Node<'_>) -> Plays {
    debug_assert_eq!(node.as_rule(), Rule::plays_declaration);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::PLAYS);

    let played_label = children.consume_any();
    let label = visit_label(played_label);
    let played = match children.try_consume_expected(Rule::AS) {
        None => Played::new(label, None),
        Some(_) => Played::new(label, Some(visit_label(children.consume_expected(Rule::label)))),
    };

    debug_assert!(children.try_consume_any().is_none());
    Plays::new(played, span)
}

fn visit_relates_declaration(node: Node<'_>) -> Relates {
    debug_assert_eq!(node.as_rule(), Rule::relates_declaration);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::RELATES);

    let related_label = children.consume_any();
    let related = match related_label.as_rule() {
        Rule::list_label => Related::List(visit_list_label(related_label)),
        Rule::label => {
            let label = visit_label(related_label);
            match children.try_consume_expected(Rule::AS) {
                None => Related::Attribute(label, None),
                Some(_) => Related::Attribute(label, Some(visit_label(children.consume_expected(Rule::label)))),
            }
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: related_label.to_string() }),
    };

    let annotations_relates = visit_annotations_relates(children.consume_expected(Rule::annotations_relates));
    debug_assert!(children.try_consume_any().is_none());
    Relates::new(related, annotations_relates, span)
}

fn visit_annotations_relates(node: Node<'_>) -> Vec<AnnotationRelates> {
    vec![] // FIXME
}

fn visit_owns_declaration(node: Node<'_>) -> Owns {
    debug_assert_eq!(node.as_rule(), Rule::owns_declaration);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::OWNS);

    let owned_label = children.consume_any();
    let owned = match owned_label.as_rule() {
        Rule::list_label => Owned::List(visit_list_label(owned_label)),
        Rule::label => {
            let label = visit_label(owned_label);
            match children.try_consume_expected(Rule::AS) {
                None => Owned::Attribute(label, None),
                Some(_) => Owned::Attribute(label, Some(visit_label(children.consume_expected(Rule::label)))),
            }
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: owned_label.to_string() }),
    };

    let annotations_owns = visit_annotations_owns(children.consume_expected(Rule::annotations_owns));
    debug_assert!(children.try_consume_any().is_none());
    Owns::new(owned, annotations_owns, span)
}

fn visit_list_label(node: Node<'_>) -> Label {
    debug_assert_eq!(node.as_rule(), Rule::list_label);
    let mut children = node.into_children();
    let label = children.consume_expected(Rule::LABEL);
    debug_assert!(children.try_consume_any().is_none());
    Label::new_unscoped(label.as_str(), label.span())
}

fn visit_annotations_owns(node: Node<'_>) -> Vec<AnnotationOwns> {
    debug_assert_eq!(node.as_rule(), Rule::annotations_owns);
    let children = node.into_children();
    children
        .map(|anno| {
            let span = anno.span();
            match anno.as_rule() {
                Rule::annotation_card => {
                    let mut children = anno.into_children();
                    children.skip_expected(Rule::ANNOTATION_CARD);
                    let lower = children.consume_expected(Rule::ANNOTATION_CARD_LOWER).as_str().parse().unwrap();
                    let upper = children.consume_expected(Rule::ANNOTATION_CARD_UPPER).as_str().parse().ok();
                    AnnotationOwns::Cardinality(lower, upper, span)
                }
                Rule::ANNOTATION_DISTINCT => AnnotationOwns::Distinct(span),
                Rule::ANNOTATION_KEY => AnnotationOwns::Key(span),
                Rule::ANNOTATION_UNIQUE => AnnotationOwns::Unique(span),
                _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: anno.to_string() }),
            }
        })
        .collect()
}

fn visit_value_type_declaration(node: Node<'_>) -> ValueType {
    debug_assert_eq!(node.as_rule(), Rule::value_type_declaration);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::VALUE);
    let value_type_node = children.consume_any();
    let (value_type, annotations_value_type) = match value_type_node.as_rule() {
        Rule::label => (visit_label(value_type_node).to_string(), Vec::new()),
        Rule::value_type_primitive => (
            value_type_node.as_str().to_owned(),
            visit_annotations_value(children.consume_expected(Rule::annotations_value)),
        ),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: value_type_node.to_string() }),
    };
    debug_assert!(children.try_consume_any().is_none());
    ValueType::new(value_type, annotations_value_type, span)
}

fn visit_annotations_value(node: Node<'_>) -> Vec<AnnotationValueType> {
    node.into_children()
        .map(|anno| match anno.as_rule() {
            Rule::annotation_regex => visit_annotation_regex(anno),
            Rule::annotation_values => visit_annotation_values(anno),
            _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: anno.to_string() }),
        })
        .collect()
}

fn visit_annotation_values(node: Node<'_>) -> AnnotationValueType {
    let span = node.span();
    let values = node.into_children().skip_expected(Rule::ANNOTATION_VALUES).map(|v| v.as_str().to_owned()).collect();
    AnnotationValueType::Values(values, span)
}

fn visit_annotation_regex(node: Node<'_>) -> AnnotationValueType {
    let span = node.span();
    let mut children = node.into_children();
    children.consume_expected(Rule::ANNOTATION_REGEX);
    let regex = children.consume_expected(Rule::QUOTED_STRING).as_str().to_owned();
    AnnotationValueType::Regex(regex, span)
}

fn visit_sub_declaration(node: Node<'_>) -> Sub {
    debug_assert_eq!(node.as_rule(), Rule::sub_declaration);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::SUB);
    let supertype_label = visit_label(children.consume_expected(Rule::label));
    let annotations_sub = visit_annotations_sub(children.consume_expected(Rule::annotations_sub));
    debug_assert!(children.try_consume_any().is_none());
    Sub::new(supertype_label, annotations_sub, span)
}

fn visit_annotations_sub(node: Node<'_>) -> Vec<AnnotationSub> {
    node.into_children()
        .map(|anno| match anno.as_rule() {
            Rule::ANNOTATION_ABSTRACT => AnnotationSub::Abstract(anno.span()),
            Rule::ANNOTATION_CASCADE => AnnotationSub::Cascade(anno.span()),
            Rule::ANNOTATION_INDEPENDENT => AnnotationSub::Independent(anno.span()),
            _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: anno.to_string() }),
        })
        .collect()
}

fn visit_label(label: Node<'_>) -> Label {
    Label::new_unscoped(label.as_str(), label.span())
}
