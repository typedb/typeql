/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use itertools::Itertools;

use crate::{
    common::{error::TypeQLError, Spanned},
    identifier::Label,
    parser::{
        expression::{visit_expression, visit_expression_function, visit_expression_value},
        statement::visit_comparison,
        visit_label, visit_var, visit_vars, IntoChildNodes, Node, Rule, RuleMatcher,
    },
    statement::{
        comparison::ComparisonStatement, Assignment, AssignmentPattern, DeconstructField, InStream, Is, Statement,
        StructDeconstruct,
    },
};

pub(super) fn visit_statement_single(node: Node<'_>) -> Statement {
    debug_assert_eq!(node.as_rule(), Rule::statement_single);
    let child = node.into_child();
    match child.as_rule() {
        Rule::statement_is => Statement::Is(visit_statement_is(child)),
        Rule::statement_in => Statement::InStream(visit_statement_in(child)),
        Rule::statement_comparison => Statement::Comparison(visit_statement_comparison(child)),
        Rule::statement_assignment => Statement::Assignment(visit_statement_assignment(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

pub fn visit_statement_assignment(node: Node<'_>) -> Assignment {
    debug_assert_eq!(node.as_rule(), Rule::statement_assignment);
    let span = node.span();
    let mut children = node.into_children();
    let lhs = visit_assignment_left(children.consume_expected(Rule::assignment_left));
    children.skip_expected(Rule::ASSIGN);
    let rhs = visit_expression(children.consume_expected(Rule::expression));
    debug_assert_eq!(children.try_consume_any(), None);
    Assignment::new(span, lhs, rhs)
}

pub fn visit_assignment_left(node: Node<'_>) -> AssignmentPattern {
    debug_assert_eq!(node.as_rule(), Rule::assignment_left);
    let child = node.into_child();
    match child.as_rule() {
        Rule::vars => AssignmentPattern::Variables(visit_vars(child)),
        Rule::struct_destructor => AssignmentPattern::Deconstruct(visit_struct_destructor(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_struct_destructor(node: Node<'_>) -> StructDeconstruct {
    debug_assert_eq!(node.as_rule(), Rule::struct_destructor);
    let span = node.span();
    let mut children = node.into_children();

    let field_map = children
        .by_ref()
        .tuple_windows()
        .map(|(key, value)| (visit_struct_key(key), visit_struct_destructor_value(value)))
        .collect();

    debug_assert_eq!(children.try_consume_any(), None);
    StructDeconstruct::new(span, field_map)
}

fn visit_struct_destructor_value(node: Node<'_>) -> DeconstructField {
    debug_assert_eq!(node.as_rule(), Rule::struct_destructor_value);
    let child = node.into_child();
    match child.as_rule() {
        Rule::var => DeconstructField::Variable(visit_var(child)),
        Rule::struct_destructor => DeconstructField::Deconstruct(visit_struct_destructor(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_struct_key(node: Node<'_>) -> Label {
    debug_assert_eq!(node.as_rule(), Rule::struct_key);
    visit_label(node.into_child())
}

pub fn visit_statement_comparison(node: Node<'_>) -> ComparisonStatement {
    debug_assert_eq!(node.as_rule(), Rule::statement_comparison);
    let span = node.span();
    let mut children = node.into_children();
    let lhs = visit_expression_value(children.consume_expected(Rule::expression_value));
    let comparison = visit_comparison(children.consume_expected(Rule::comparison));
    debug_assert_eq!(children.try_consume_any(), None);
    ComparisonStatement::new(span, lhs, comparison)
}

pub fn visit_statement_is(node: Node<'_>) -> Is {
    debug_assert_eq!(node.as_rule(), Rule::statement_is);
    let span = node.span();
    let mut children = node.into_children();
    let lhs = visit_var(children.consume_expected(Rule::var));
    children.skip_expected(Rule::IS);
    let rhs = visit_var(children.consume_expected(Rule::var));
    Is::new(span, lhs, rhs)
}

pub fn visit_statement_in(node: Node<'_>) -> InStream {
    debug_assert_eq!(node.as_rule(), Rule::statement_in);
    let span = node.span();
    let mut children = node.into_children();
    let lhs = visit_vars(children.consume_expected(Rule::vars));
    children.skip_expected(Rule::IN);
    let rhs = visit_expression_function(children.consume_expected(Rule::expression_function));
    InStream::new(span, lhs, rhs)
}
