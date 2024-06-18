/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::{IntoChildNodes, Node, Rule};
use crate::{
    common::{error::TypeQLError, token::Comparator, Spanned},
    expression::Expression,
    parser::{
        expression::{visit_expression, visit_expression_function, visit_expression_value},
        visit_var, RuleMatcher,
    },
    pattern::{
        statement::{Assignment, Comparison, InStream, Is, Single, Variable},
        Statement,
    },
};

pub(super) fn visit_statement(node: Node<'_>) -> Statement {
    debug_assert_eq!(node.as_rule(), Rule::statement);
    let child = node.into_child();
    match child.as_rule() {
        Rule::statement_single => Statement::Single(visit_statement_single(child)),
        Rule::statement_multi => todo!(),
        Rule::statement_anon_relation => todo!(),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_statement_single(node: Node<'_>) -> Single {
    debug_assert_eq!(node.as_rule(), Rule::statement_single);
    let child = node.into_child();
    match child.as_rule() {
        Rule::statement_is => Single::Is(visit_statement_is(child)),
        Rule::statement_in => Single::InStream(visit_statement_in(child)),
        Rule::statement_comparison => Single::Comparison(visit_statement_comparison(child)),
        Rule::statement_assignment => Single::Assignment(visit_statement_assignment(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_statement_assignment(node: Node<'_>) -> Assignment {
    debug_assert_eq!(node.as_rule(), Rule::statement_assignment);
    let span = node.span();
    let mut children = node.into_children();
    let lhs = visit_assignment_left(children.consume_expected(Rule::assignment_left));
    children.skip_expected(Rule::ASSIGN);
    let rhs = visit_expression(children.consume_expected(Rule::expression));
    debug_assert!(children.try_consume_any().is_none());
    Assignment::new(span, lhs, rhs)
}

fn visit_assignment_left(node: Node<'_>) -> Vec<Variable> {
    debug_assert_eq!(node.as_rule(), Rule::assignment_left);
    node.into_children().map(visit_var).collect()
}

fn visit_statement_comparison(node: Node<'_>) -> Comparison {
    debug_assert_eq!(node.as_rule(), Rule::statement_comparison);
    let span = node.span();
    let mut children = node.into_children();
    let lhs = visit_expression_value(children.consume_expected(Rule::expression_value));
    let comparison = visit_comparison(children.consume_expected(Rule::comparison));
    debug_assert!(children.try_consume_any().is_none());
    Comparison::new(span, lhs, comparison)
}

fn visit_comparison(node: Node<'_>) -> (Comparator, Expression) {
    debug_assert_eq!(node.as_rule(), Rule::comparison);
    let mut children = node.into_children();
    let comparator_node = children.consume_expected(Rule::comparator).into_child();
    let comparator = match comparator_node.as_rule() {
        Rule::EQ => Comparator::Eq,
        Rule::NEQ => Comparator::Neq,
        Rule::GTE => Comparator::Gte,
        Rule::GT => Comparator::Gt,
        Rule::LTE => Comparator::Lte,
        Rule::LT => Comparator::Lt,
        Rule::CONTAINS => Comparator::Contains,
        Rule::LIKE => Comparator::Like,
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: comparator_node.to_string() }),
    };
    let rhs = visit_expression_value(children.consume_expected(Rule::expression_value));
    debug_assert!(children.try_consume_any().is_none());
    (comparator, rhs)
}

fn visit_statement_is(node: Node<'_>) -> Is {
    debug_assert_eq!(node.as_rule(), Rule::statement_is);
    let span = node.span();
    let mut children = node.into_children();
    let lhs = visit_var(children.consume_expected(Rule::VAR));
    children.skip_expected(Rule::IS);
    let rhs = visit_var(children.consume_expected(Rule::VAR));
    Is::new(span, lhs, rhs)
}

fn visit_statement_in(node: Node<'_>) -> InStream {
    debug_assert_eq!(node.as_rule(), Rule::statement_in);
    let span = node.span();
    let mut children = node.into_children();
    let mut lhs = vec![visit_var(children.consume_expected(Rule::VAR))];
    while let Some(var) = children.try_consume_expected(Rule::VAR) {
        lhs.push(visit_var(var));
    }
    children.skip_expected(Rule::IN);
    let rhs = visit_expression_function(children.consume_expected(Rule::expression_function));
    InStream::new(span, lhs, rhs)
}

pub(super) fn visit_statement_things(_node: Node<'_>) {
    todo!()
}
